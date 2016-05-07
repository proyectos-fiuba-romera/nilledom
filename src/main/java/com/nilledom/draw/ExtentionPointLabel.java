package com.nilledom.draw;

import java.awt.*;

public class ExtentionPointLabel extends MultiLineLabel {

    @Override
    public void recalculateSize(DrawingContext drawingContext){

        DrawingContext.FontType fontType = getFontType();
        FontMetrics fm = drawingContext.getFontMetrics(fontType);
        String [] lines = getSource().getLabelText().split("\n");
        if(lines.length>1)
            setSize(Math.max( fm.stringWidth(lines[0]),fm.stringWidth(lines[1])), 2 * fm.getHeight());
        else
            setSize(fm.stringWidth(lines[0]), 2 * fm.getHeight());
        super.recalculateSize(drawingContext);
    }


}
