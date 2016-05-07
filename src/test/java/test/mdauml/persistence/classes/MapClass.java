package test.mdauml.persistence.classes;

import java.util.HashMap;
import java.util.Map;

public class MapClass {

    private Map<String,Integer> map = new HashMap<>();

    public Map<String, Integer> getMap() {
        return map;
    }

    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }


    public boolean equals(Object o){
        if( o  instanceof  MapClass){
            Map m = ((MapClass)o).map;
            return map.equals(m);

        }
        return false;
    }
}
