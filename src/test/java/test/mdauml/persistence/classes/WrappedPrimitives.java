package test.mdauml.persistence.classes;

/**
 * Created by ferro on 29/8/2015.
 */
public class WrappedPrimitives {

    Short sh;
    Integer i;
    Long l;
    Byte b;
    Character c;
    Float f;
    Double d;
    Boolean tr;
    Boolean fa;


    public Short getSh() {
        return sh;
    }

    public void setSh(Short sh) {
        this.sh = sh;
    }

    public Integer getI() {
        return i;
    }

    public void setI(Integer i) {
        this.i = i;
    }

    public Long getL() {
        return l;
    }

    public void setL(Long l) {
        this.l = l;
    }

    public Byte getB() {
        return b;
    }

    public void setB(Byte b) {
        this.b = b;
    }

    public Character getC() {
        return c;
    }

    public void setC(Character c) {
        this.c = c;
    }

    public Float getF() {
        return f;
    }

    public void setF(Float f) {
        this.f = f;
    }

    public Double getD() {
        return d;
    }

    public void setD(Double d) {
        this.d = d;
    }

    public Boolean getTr() {
        return tr;
    }

    public void setTr(Boolean tr) {
        this.tr = tr;
    }

    public Boolean getFa() {
        return fa;
    }

    public void setFa(Boolean fa) {
        this.fa = fa;
    }

    @Override public boolean equals(Object o) {
        if (o instanceof WrappedPrimitives) {
            WrappedPrimitives p = (WrappedPrimitives) o;
            return sh.equals(p.sh)  &&
                    i.equals(p.i) &&
                    l.equals(p.l) &&
                    b.equals(p.b) &&
                    c.equals(p.c) &&
                    f.equals(p.f) &&
                    d.equals(p.d) &&
                    tr.equals(p.tr) &&
                    fa.equals(p.fa);
        }
        return false;
    }

}
