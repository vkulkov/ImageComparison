package com.simple;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ImageProcessor {
    public static void main(String[] args) {
        String imagePath = "result_image.png";
        if (args.length != 0) {
            imagePath = args[0];
        }

        try {
            BufferedImage firstImage = ImageIO.read(ClassLoader.getSystemResource("com/simple/image1.png"));
            BufferedImage secondImage = ImageIO.read(ClassLoader.getSystemResource("com/simple/image2.png"));

            Graphics2D graphics = secondImage.createGraphics();
            graphics.setColor(new Color(255, 0, 0));

            ImageComparator comparator = new ImageComparator(firstImage, secondImage);
            comparator.imageCompare();
            comparator.mergeDots();
            comparator.mergeLines();
            comparator.mergeRegions();

            for (List<Integer> line :
                    comparator.getDiffData()) {
                graphics.drawRect(line.get(0), line.get(1), line.get(2), line.get(3));
            }

            ImageIO.write(secondImage, "png", new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
