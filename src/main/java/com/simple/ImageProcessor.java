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

            long startTime = System.currentTimeMillis();
            ImageComparator comparator = new ImageComparator(5, new PixelComparator(25));
            List<Rectangle> rectangles = comparator.getAreasOfDifference(firstImage, secondImage);
            long endTime = System.currentTimeMillis();

            for (Rectangle rectangle :
                    rectangles) {
                System.out.println(rectangle.toString());
                graphics.draw(rectangle);
            }
            System.out.println("Spent time: " + (endTime - startTime));

            ImageIO.write(secondImage, "png", new File(imagePath));
        } catch (IOException | DifferentImageSizesException e) {
            e.printStackTrace();
        }
    }
}
