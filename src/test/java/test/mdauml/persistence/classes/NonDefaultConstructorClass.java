package test.mdauml.persistence.classes;

import java.util.Arrays;
import java.util.List;


public class NonDefaultConstructorClass {

    private Integer value;
    private int i;
    private int[] ia;
    private String[] sa;
    private String s;
    private List<String>l;

    public NonDefaultConstructorClass(Integer value, int i , int [] ia, String[]sa,String s,List<String> l){

        this.value = value;
        this.i=i;
        this.ia=ia;
        this.sa=sa;
        this.s=s;
        this.l=l;
    }


    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }


    public void ellipsis(Object ... objs){
        Object [] array = objs;
        int l = array.length;
    }

    public boolean equals (Object o){
        if(o instanceof NonDefaultConstructorClass){
            NonDefaultConstructorClass n = (NonDefaultConstructorClass) o;
            return value.equals(n.value) &&
                    i == n.i &&
                    Arrays.equals(ia,n.ia) &&
                    Arrays.equals(sa,n.sa) &&
                    s.equals(n.s) &&
                    l.equals(n.l);

        }
        return false;
    }
}
