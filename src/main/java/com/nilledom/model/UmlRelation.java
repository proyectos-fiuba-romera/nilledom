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
package com.nilledom.model;

/**
 * This class implements the common functionality of Relations.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class UmlRelation extends AbstractUmlModelElement implements Relation {

    private UmlModelElement element1, element2;
    private boolean navigableToElement1, navigableToElement2;
    private boolean canSetElement1Navigability, canSetElement2Navigability;
    private Multiplicity element1Multiplicity = Multiplicity.getDefaultInstance();
    private Multiplicity element2Multiplicity = Multiplicity.getDefaultInstance();
    private ReadingDirection readingDirection = ReadingDirection.UNDEFINED;

    /**
     * {@inheritDoc}
     */
    public ReadingDirection getNameReadingDirection() {
        return readingDirection;
    }

    /**
     * {@inheritDoc}
     */
    public void setNameReadingDirection(ReadingDirection dir) {
        readingDirection = dir;
    }

    /**
     * {@inheritDoc}
     */
    public UmlModelElement getElement1() {
        return element1;
    }

    /**
     * {@inheritDoc}
     */
    public void setElement1(UmlModelElement element) {
        element1 = element;
    }

    /**
     * {@inheritDoc}
     */
    public UmlModelElement getElement2() {
        return element2;
    }

    /**
     * {@inheritDoc}
     */
    public void setElement2(UmlModelElement element) {
        element2 = element;
    }

    // ************************************************************************
    // ******** Navigability
    // ***********************************

    /**
     * {@inheritDoc}
     */
    public boolean canSetElement1Navigability() {
        return canSetElement1Navigability;
    }

    /**
     * Sets the element 1 navigability.
     *
     * @param flag the value
     */
    public void setCanSetElement1Navigability(boolean flag) {
        canSetElement1Navigability = flag;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canSetElement2Navigability() {
        return canSetElement2Navigability;
    }

    /**
     * Sets the element 2 navigability.
     *
     * @param flag the value
     */
    public void setCanSetElement2Navigability(boolean flag) {
        canSetElement2Navigability = flag;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNavigableToElement1() {
        return navigableToElement1;
    }

    /**
     * {@inheritDoc}
     */
    public void setNavigableToElement1(boolean flag) {
        navigableToElement1 = flag;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNavigableToElement2() {
        return navigableToElement2;
    }

    /**
     * {@inheritDoc}
     */
    public void setNavigableToElement2(boolean flag) {
        navigableToElement2 = flag;
    }

    // ************************************************************************
    // ******** Multiplicity
    // ***********************************

    /**
     * {@inheritDoc}
     */
    public Multiplicity getElement1Multiplicity() {
        return element1Multiplicity;
    }

    /**
     * {@inheritDoc}
     */
    public void setElement1Multiplicity(Multiplicity multiplicity) {
        element1Multiplicity = multiplicity;
    }

    /**
     * {@inheritDoc}
     */
    public Multiplicity getElement2Multiplicity() {
        return element2Multiplicity;
    }

    /**
     * {@inheritDoc}
     */
    public void setElement2Multiplicity(Multiplicity multiplicity) {
        element2Multiplicity = multiplicity;
    }

    /**
     * {@inheritDoc}
     */
    @Override public String getName() {

        if (element1 != null && element2 != null) {
            return element1.getName() + "->" + element2.getName();
        }
        return "";
    }

    @Override
    public ElementType getElementType() {
        return ElementType.RELATION;
    }

    private boolean isInitialized(){
        return !(getElement2()==null || getElement1()==null );
    }
    @Override
    public int hashCode(){
        if(!isInitialized())
            return 1;
        return (getElement2().getName()+getElement1().getName()).hashCode();
    }

    @Override
    public boolean equals(Object object){
        if(object==null)
            return false;
        if(!(this.getClass().isAssignableFrom(object.getClass())))
            return false;
        UmlRelation other = (UmlRelation) object;
        if(!isInitialized()|| !other.isInitialized())
            return false;

        return getElement2()==other.getElement2() && getElement1()== other.getElement1();
    }
}
