package test.mdauml.persistence.classes;


import java.util.HashSet;
import java.util.Set;

public class SetClass {

    Set<String> set = new HashSet<String>();


    public Set<String> getSet() {
        return set;
    }

    public void setSet(Set<String> set) {
        this.set = set;
    }

    public boolean equals(Object o){
        if(o instanceof SetClass){
            SetClass s = (SetClass)o;
            return s.set.equals(set);
        }
        return false;
    }
}
