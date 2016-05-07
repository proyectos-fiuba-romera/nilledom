/**
 * Copyright 2007 Wei-ju Wu
 * <p/>
 * This file is part of TinyUML.
 * <p/>
 * TinyUML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p/>
 * TinyUML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with TinyUML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.nilledom.ui.commands;

import javax.imageio.ImageIO;

import com.nilledom.ui.diagram.DiagramEditor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * This class exports a diagram to a Portable Network Graphics file.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class PngExporter extends FileWriter {

    /**
     * Export the editor graphics to a file in PNG format.
     *
     * @param editor the editor
     * @param file   the file to write
     * @throws java.io.IOException if error occurred
     */
    public void writePNG(DiagramEditor editor, File file) throws IOException {
        Dimension size = editor.getTotalCanvasSize();
        File theFile = getFileWithExtension(file);
        if (canWrite(editor, theFile)) {
            BufferedImage image = new BufferedImage((int) size.getWidth(), (int) size.getHeight(),
                BufferedImage.TYPE_INT_RGB);
            editor.paintComponentNonScreen(image.getGraphics());
            ImageIO.write(image, "png", theFile);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected String getSuffix() {
        return ".png";
    }
}
