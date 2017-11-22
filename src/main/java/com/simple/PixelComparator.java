package com.simple;

import static java.lang.Math.abs;

public class PixelComparator {
    private final int pixelDifference;

    public PixelComparator() {
        this(0);
    }

    public PixelComparator(int pixelDifference) {
        if (pixelDifference < 0) this.pixelDifference = 0;
        else if (pixelDifference > 0xFE) this.pixelDifference = 0xFE;
        else this.pixelDifference = pixelDifference;
    }

    public boolean isDifferent(int firstPixel, int secondPixel) {
        boolean absolute = firstPixel == secondPixel;
        if (absolute || pixelDifference == 0) {
            return !absolute;
        }

        int firstRed = (firstPixel >> 16) & 0xff;
        int firstGreen = (firstPixel >> 8) & 0xff;
        int firstBlue = firstPixel & 0xff;

        int secondRed = (secondPixel >> 16) & 0xff;
        int secondGreen = (secondPixel >> 8) & 0xff;
        int secondBlue = secondPixel & 0xff;

        return (abs(firstRed - secondRed) > pixelDifference) ||
                (abs(firstGreen - secondGreen) > pixelDifference) ||
                (abs(firstBlue - secondBlue) > pixelDifference);
    }
}
