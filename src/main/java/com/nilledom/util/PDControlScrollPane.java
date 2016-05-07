package com.nilledom.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * A JScrollPane that will bubble a mouse wheel scroll event to the parent
 * JScrollPane if one exists when this scrollpane either tops out or bottoms out.
 */
public class PDControlScrollPane extends JScrollPane {

    public PDControlScrollPane() {
        super();

        addMouseWheelListener(new PDMouseWheelListener());
    }

    class PDMouseWheelListener implements MouseWheelListener {

        private JScrollBar bar;
        private JScrollPane parentScrollPane;

        private JScrollPane getParentScrollPane() {
            if (parentScrollPane == null) {
                Component parent = getParent();
                while (!(parent instanceof JScrollPane) && parent != null) {
                    parent = parent.getParent();
                }
                parentScrollPane = (JScrollPane)parent;
            }
            return parentScrollPane;
        }

        public PDMouseWheelListener() {
            bar = PDControlScrollPane.this.getVerticalScrollBar();
        }
        public void mouseWheelMoved(MouseWheelEvent e) {
            JScrollPane parent = getParentScrollPane();
            if (parent != null) {
            /*
             * Only dispatch if the bar is not visible
             */
                if(!bar.isVisible())
                    parent.dispatchEvent(cloneEvent(e));

            }
        /*
         * If parent scrollpane doesn't exist, remove this as a listener.
         * We have to defer this till now (vs doing it in constructor)
         * because in the constructor this item has no parent yet.
         */
            else {
                PDControlScrollPane.this.removeMouseWheelListener(this);
            }
        }

        private MouseWheelEvent cloneEvent(MouseWheelEvent e) {
            return new MouseWheelEvent(getParentScrollPane(), e.getID(), e
                    .getWhen(), e.getModifiers(), 1, 1, e
                    .getClickCount(), false, e.getScrollType(), e
                    .getScrollAmount(), e.getWheelRotation());
        }
    }
}
