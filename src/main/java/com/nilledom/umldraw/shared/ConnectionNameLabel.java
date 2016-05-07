package com.nilledom.umldraw.shared;

import com.nilledom.draw.*;

public class ConnectionNameLabel extends AbstractCompositeNode implements Label, LabelSource {

    private Label label;
    private String labelText ="";

    /**
     * Constructor.
     */
    public ConnectionNameLabel() {
        setLabel(new SimpleLabel());
    }

    /**
     * Returns the wrapped label.
     *
     * @return the wrapped label
     */
    public Label getLabel() {
        return label;
    }

    /**
     * Sets a Label. This method is exposed for unit testing.
     *
     * @param aLabel the label
     */
    public void setLabel(Label aLabel) {
        label = aLabel;
        label.setSource(this);
        label.setParent(this);
    }


    /**
     * {@inheritDoc}
     */
    public Label getLabelAt(double mx, double my) {
        if (contains(mx, my))
            return this;
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public LabelSource getSource() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void setSource(LabelSource aSource) {
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        return label.getText();
    }

    /**
     * {@inheritDoc}
     */
    public void setText(String text) {
        label.setText(text);
    }

    /**
     * {@inheritDoc}
     */
    public void setFontType(DrawingContext.FontType aFontType) {
        label.setFontType(aFontType);
    }

    /**
     * {@inheritDoc}
     */
    public void centerHorizontally() {
        label.centerHorizontally();
    }


    /**
     * {@inheritDoc}
     */
    public void centerVertically() {
        label.centerVertically();
    }

    @Override
    public void validate() {
        label.validate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(DrawingContext drawingContext) {
        if (getLabelText() != null) {
            label.draw(drawingContext);

        }
    }


    /**
     * {@inheritDoc}
     */
    public String getLabelText() {
        return labelText;
    }

    /**
     * {@inheritDoc}
     */
    public void setLabelText(String aText) {
        labelText=aText;
    }
}