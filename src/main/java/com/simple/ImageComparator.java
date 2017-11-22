package com.simple;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageComparator {
    private final int approximation;
    private PixelComparator pixelComparator;

    private List<List<Integer>> diffData = new ArrayList<>();

    private BufferedImage first;
    private BufferedImage second;

    @Deprecated
    public ImageComparator(BufferedImage first, BufferedImage second) {
        this(1);
        this.first = first;
        this.second = second;
    }

    public ImageComparator() {
        this(1);
    }

    public ImageComparator(int approximation) {
        this(approximation, new PixelComparator());
    }

    public ImageComparator(PixelComparator pixelComparator) {
        this(1, pixelComparator);
    }

    public ImageComparator(int approximation, PixelComparator pixelComparator) {
        this.approximation = approximation;
        this.pixelComparator = pixelComparator;
    }

    public List<Rectangle> getDifference(BufferedImage first, BufferedImage second) throws DifferentImageSizesException {
        if ((first.getWidth() != second.getWidth()) || (first.getHeight() != second.getHeight())) {
            throw new DifferentImageSizesException();
        }
        List<Rectangle> buffer = new ArrayList<>();
        for (int y = 0; y < first.getHeight(); y++) {
            for (int x = 0; x < first.getWidth(); x++) {
                if (pixelComparator.isDifferent(first.getRGB(x, y), second.getRGB(x, y))) {
                    int currentX = x;
                    int currentY = y;
                    int currentWidth = 0;
                    int currentHeight = 0;
                    //Trying to merge with already buffered areas
                    int temp = 0;           //in case of multiple merges
                    boolean merged = false;
                    boolean noMore = false;
                    for (int i = 0; !noMore && i < buffer.size(); i++) {
                        Rectangle rect = buffer.get(i);
                        int rectX = rect.x;
                        int rectY = rect.y;
                        int rectWidth = rect.width;
                        int rectHeight = rect.height;
                        //Merged areas data
                        int newX = 0, newY = 0, newWidth = 0, newHeight = 0;
                        boolean intersect = false;
                        //Intersection checks and determination of a new rectangle
                        if ((currentX >= rectX) && (currentY >= rectY) &&
                                (currentX <= rectX + rectWidth + approximation) &&
                                (currentY <= rectY + rectHeight + approximation)) {
                            newX = rectX;
                            newY = rectY;
                            if (currentX + currentWidth < rectX + rectWidth) {
                                newWidth = rectWidth;
                            } else {
                                newWidth = currentWidth + currentX - rectX;
                            }
                            if (currentY + currentHeight < rectY + rectHeight) {
                                newHeight = rectHeight;
                            } else {
                                newHeight = currentHeight + currentY - rectY;
                            }
                            intersect = true;
                        } else if ((currentX >= rectX) && (currentY <= rectY) &&
                                (currentX <= rectX + rectWidth + approximation) &&
                                (currentY + currentHeight + approximation >= rectY)) {
                            newX = rectX;
                            newY = currentY;
                            if (currentX + currentWidth < rectX + rectWidth) {
                                newWidth = rectWidth;
                            } else {
                                newWidth = currentWidth + currentX - rectX;
                            }
                            if (currentY + currentHeight > rectY + rectHeight) {
                                newHeight = currentHeight;
                            } else {
                                newHeight = rectHeight + rectY - currentY;
                            }
                            intersect = true;
                        } else if ((currentX <= rectX) && (currentY >= rectY) &&
                                (currentX + currentWidth + approximation >= rectX) &&
                                (currentY <= rectY + rectHeight + approximation)) {
                            newX = currentX;
                            newY = rectY;
                            if (currentX + currentWidth > rectX + rectWidth) {
                                newWidth = currentWidth;
                            } else {
                                newWidth = rectWidth + rectX - currentX;
                            }
                            if (currentY + currentHeight < rectY + rectHeight) {
                                newHeight = rectHeight;
                            } else {
                                newHeight = currentHeight + currentY - rectY;
                            }
                            intersect = true;
                        } else if ((currentX <= rectX) && (currentY <= rectY) &&
                                (currentX + currentWidth + approximation >= rectX) &&
                                (currentY + currentHeight + approximation >= rectY)) {
                            newX = currentX;
                            newY = currentY;
                            if (currentX + currentWidth > rectX + rectWidth) {
                                newWidth = currentWidth;
                            } else {
                                newWidth = rectWidth + rectX - currentX;
                            }
                            if (currentY + currentHeight > rectY + rectHeight) {
                                newHeight = currentHeight;
                            } else {
                                newHeight = rectHeight + rectY - currentY;
                            }
                            intersect = true;
                        }

                        if (intersect) {
                            if (merged) {
                                buffer.set(temp, null);     //no need of this area anymore
                                noMore = true;              //current point wouldn't be merged with more than two areas anyway
                            }
                            buffer.set(i, new Rectangle(newX, newY, newWidth, newHeight));
                            currentX = newX;
                            currentY = newY;
                            currentWidth = newWidth;
                            currentHeight = newHeight;
                            temp = i;
                            merged = true;
                        }
                    }
                    if (!merged) {
                        buffer.add(new Rectangle(currentX, currentY, currentWidth, currentHeight));
                    }
                    if (noMore) {
                        buffer.remove(null);
                    }
                }
            }
        }
        return buffer;
    }

    @Deprecated
    public void imageCompare() {
        List<Integer> diff = new ArrayList<>();
        for (int i = 0; i < first.getHeight(); i++) {
            for (int j = 0; j < first.getWidth(); j++) {
                if (first.getRGB(j, i) != second.getRGB(j, i)) {
                    diff.add(j);
                    diff.add(i);
                    diffData.add(diff);
                    diff = new ArrayList<>();
                }
            }
        }
    }

    @Deprecated
    public void mergeDots() {
        List<List<Integer>> refined = new ArrayList<>();
        List<Integer> lineData;
        for (int i = 0; i < diffData.size(); i++) {
            Integer x1 = diffData.get(i).get(0);
            Integer y1 = diffData.get(i).get(1);
            Integer x2 = 0;
            while ((i < diffData.size() - 1) &&
                    y1.equals(diffData.get(i + 1).get(1)) &&
                    diffData.get(i + 1).get(0) <= diffData.get(i).get(0) + approximation) {
                x2 = diffData.get(i + 1).get(0);
                i++;
            }
            lineData = new ArrayList<>();
            lineData.add(x1);
            lineData.add(y1);
            if (x2 != 0) {
                lineData.add(x2 - x1);
            } else {
                lineData.add(x2);
            }
            refined.add(lineData);
        }
        setDiffData(refined);
        diffData.sort(((o1, o2) -> {
            int index = o1.get(1).compareTo(o2.get(1));
            if (index == 0) {
                index = o1.get(0).compareTo(o2.get(0));
            }
            return index;
        }) );
    }

    @Deprecated
    public void mergeLines() {
        List<List<Integer>> refined = new ArrayList<>();
        List<Integer> regionData;
        for (int i = 0; i < diffData.size(); i++) {
            Integer x1 = diffData.get(i).get(0);
            Integer y1 = diffData.get(i).get(1);
            Integer width = diffData.get(i).get(2);
            Integer y2 = 0;
            while ((i < diffData.size() - 1) &&
                    x1.equals(diffData.get(i + 1).get(0)) &&
                    diffData.get(i + 1).get(1) <= diffData.get(i).get(1) + approximation) {
                y2 = diffData.get(i + 1).get(1);
                if (width.compareTo(diffData.get(i + 1).get(2)) < 0) {
                    width = diffData.get(i + 1).get(2);
                }
                i++;
            }
            regionData = new ArrayList<>();
            regionData.add(x1);
            regionData.add(y1);
            regionData.add(width);
            if (y2 != 0) {
                regionData.add(y2 - y1);
            } else {
                regionData.add(y2);
            }
            refined.add(regionData);
        }
        setDiffData(refined);
    }

    @Deprecated
    public void mergeRegions() {
        List<List<Integer>> refined = new ArrayList<>();
        List<List<Integer>> buffer = new ArrayList<>();
        for (int i = 0; i < diffData.size(); i++) {
            if (buffer.contains(diffData.get(i))) {
                continue;
            }
            boolean cycle = true;
            List<Integer> temp = new ArrayList<>();
            temp.addAll(diffData.get(i));
            while (cycle) {
                Integer x1 = temp.get(0);
                Integer y1 = temp.get(1);
                Integer width1 = temp.get(2);
                Integer height1 = temp.get(3);
                for (int j = i + 1; j < diffData.size(); j++) {
                    if (buffer.contains(diffData.get(j))) {
                        continue;
                    }
                    Integer x2 = diffData.get(j).get(0);
                    Integer y2 = diffData.get(j).get(1);
                    Integer width2 = diffData.get(j).get(2);
                    Integer height2 = diffData.get(j).get(3);

                    List<Integer> mergedData = new ArrayList<>();
                    if ((x1 >= x2) && (y1 >= y2) &&
                            (x1 < x2 + width2 + approximation) &&
                            (y1 < y2 + height2 + approximation)) {
                        mergedData.add(x2);
                        mergedData.add(y2);
                        if (x1 + width1 < x2 + width2) {
                            mergedData.add(width2);
                        } else {
                            mergedData.add(width1 + x1 - x2);
                        }
                        if (y1 + height1 < y2 + height2) {
                            mergedData.add(height2);
                        } else {
                            mergedData.add(height1 + y1 - y2);
                        }
                    } else if ((x1 >= x2) && (y1 <= y2) &&
                            (x1 < x2 + width2 + approximation) &&
                            (y1 + height1 + approximation > y2)) {
                        mergedData.add(x2);
                        mergedData.add(y1);
                        if (x1 + width1 < x2 + width2) {
                            mergedData.add(width2);
                        } else {
                            mergedData.add(width1 + x1 - x2);
                        }
                        if (y1 + height1 > y2 + height2) {
                            mergedData.add(height1);
                        } else {
                            mergedData.add(height2 + y2 - y1);
                        }
                    } else if ((x1 <= x2) && (y1 >= y2) &&
                            (x1 + width1 + approximation > x2) &&
                            (y1 < y2 + height2 + approximation)) {
                        mergedData.add(x1);
                        mergedData.add(y2);
                        if (x1 + width1 > x2 + width2) {
                            mergedData.add(width1);
                        } else {
                            mergedData.add(width2 + x2 - x1);
                        }
                        if (y1 + height1 < y2 + height2) {
                            mergedData.add(height2);
                        } else {
                            mergedData.add(height1 + y1 - y2);
                        }
                    } else if ((x1 <= x2) && (y1 <= y2) &&
                            (x1 + width1 + approximation > x2) &&
                            (y1 + height1 + approximation > y2)) {
                        mergedData.add(x1);
                        mergedData.add(y1);
                        if (x1 + width1 > x2 + width2) {
                            mergedData.add(width1);
                        } else {
                            mergedData.add(width2 + x2 - x1);
                        }
                        if (y1 + height1 > y2 + height2) {
                            mergedData.add(height1);
                        } else {
                            mergedData.add(height2 + y2 - y1);
                        }
                    }

                    if (j == diffData.size() - 1) {
                        cycle = false;
                    }
                    if (!mergedData.isEmpty()) {
                        temp.clear();
                        temp.addAll(mergedData);
                        buffer.add(diffData.get(j));
                        break;
                    }
                }
            }
            refined.add(temp);
            buffer.add(diffData.get(i));
        }
        setDiffData(refined);
    }

    @Deprecated
    private void setDiffData(List<List<Integer>> diffData) {
        this.diffData = diffData;
    }

    @Deprecated
    public List<List<Integer>> getDiffData() {
        return diffData;
    }
}
