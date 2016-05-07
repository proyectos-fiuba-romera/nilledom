package test.mdauml.persistence.classes;

/**
 * Created by fromera on 28/08/15.
 */
public class SingletonClass {

    public static SingletonClass instance = new SingletonClass();

    private SingletonClass(){}


    private String value="pepe";

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean equals(Object o){

        if(o instanceof  SingletonClass){
            return  ((SingletonClass)o).value.equals(this.value);
        }

        return false;
    }

}
