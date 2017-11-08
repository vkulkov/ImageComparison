package com.simple;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import static com.simple.Constants.*;

public class ImageComparator {

    private List<List<Integer>> diffData = new ArrayList<>();

    private BufferedImage first;
    private BufferedImage second;

    public ImageComparator(BufferedImage first, BufferedImage second) {
        this.first = first;
        this.second = second;
    }

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

    public void mergeDots() {
        List<List<Integer>> refined = new ArrayList<>();
        List<Integer> lineData;
        for (int i = 0; i < diffData.size(); i++) {
            Integer x1 = diffData.get(i).get(0);
            Integer y1 = diffData.get(i).get(1);
            Integer x2 = 0;
            while ((i < diffData.size() - 1) &&
                    y1.equals(diffData.get(i + 1).get(1)) &&
                    diffData.get(i + 1).get(0) <= diffData.get(i).get(0) + DOT_PRECISION_APPROXIMATION) {
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
                    diffData.get(i + 1).get(1) <= diffData.get(i).get(1) + LINE_PRECISION_APPROXIMATION) {
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

    public void mergeRegions() {
        final int deviation = REGION_PRECISION_APPROXIMATION;
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
                    if ((x1 >= x2) && (y1 >= y2) && (x1 < x2 + width2 + deviation) && (y1 < y2 + height2 + deviation)) {
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
                    } else if ((x1 >= x2) && (y1 <= y2) && (x1 < x2 + width2 + deviation) && (y1 + height1 + deviation > y2)) {
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
                    } else if ((x1 <= x2) && (y1 >= y2) && (x1 + width1 + deviation > x2) && (y1 < y2 + height2 + deviation)) {
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
                    } else if ((x1 <= x2) && (y1 <= y2) && (x1 + width1 + deviation > x2) && (y1 + height1 + deviation > y2)) {
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

    private void setDiffData(List<List<Integer>> diffData) {
        this.diffData = diffData;
    }

    public List<List<Integer>> getDiffData() {
        return diffData;
    }
}
