package test.mdauml.persistence.classes;

public class ArrayClass {

    public int[] arrayOfInt;
    public String[] arrayOfString;


    public int[] getArrayOfInt() {
        return arrayOfInt;
    }

    public void setArrayOfInt(int[] arrayOfInt) {
        this.arrayOfInt = arrayOfInt;
    }

    public String[] getArrayOfString() {
        return arrayOfString;
    }

    public void setArrayOfString(String[] arrayOfString) {
        this.arrayOfString = arrayOfString;
    }

    public boolean equals(Object o){
        if(o instanceof  ArrayClass) {
            ArrayClass a = (ArrayClass) o;
            if(a.arrayOfInt != null && arrayOfInt != null ) {
                if (a.arrayOfInt.length != arrayOfInt.length)
                    return false;
                else for (int i = 0; i < arrayOfInt.length; i++) {
                    if (a.arrayOfInt[i] != arrayOfInt[i])
                        return false;
                }
            }else if(!(a.arrayOfInt==null && arrayOfInt == null))
                    return false;


            if(a.arrayOfString != null && arrayOfString != null ) {
                if (a.arrayOfString.length != arrayOfString.length)
                    return false;
                else for (int i = 0; i < arrayOfString.length; i++) {
                    if (! a.arrayOfString[i].equals(arrayOfString[i]))
                        return false;
                }
            }else if(!(a.arrayOfString==null && arrayOfString == null))
                return false;



            return true;

        }
        return false;

    }
}
