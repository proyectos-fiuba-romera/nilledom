package test.mdauml.persistence.classes;

/**
 * Created by ferro on 29/8/2015.
 */
public class TransientField {

    private transient int doNotPersist = 5 ;


    public int getDoNotPersist() {
        return doNotPersist;
    }

    public void setDoNotPersist(int doNotPersist) {
        this.doNotPersist = doNotPersist;
    }
}
