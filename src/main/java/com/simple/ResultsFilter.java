package com.simple;

import java.awt.*;
import java.util.List;

public class ResultsFilter {
    private final int minWidth;
    private final int minHeight;

    public ResultsFilter(int minWidth, int minHeight) {
        if (minWidth < 0) this.minWidth = 0;
        else this.minWidth = minWidth;
        if (minHeight < 0) this.minHeight = 0;
        else this.minHeight = minHeight;
    }

    public ResultsFilter(int minWidthAndHeight) {
        this(minWidthAndHeight, minWidthAndHeight);
    }

    public void filterResults(List<Rectangle> results) {
        results.removeIf(rectangle -> (rectangle.width < minWidth) || (rectangle.height < minHeight));
    }
}
