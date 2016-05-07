package test.mdauml.persistence.classes;

import java.util.ArrayList;
import java.util.List;


public class ListClass {


    List<String> list = new ArrayList<>();


    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public boolean equals(Object o){
        if(o instanceof ListClass){
            ListClass l = (ListClass)o;
            return l.list.equals(list);
        }
        return false;

    }
}
