package test.mdauml.persistence.classes;


/**
 * Created by Fernando on 17/08/2015.
 */
public class Primitives {
    short sh;
    int i;
    long l;
    byte b;
    char c;
    float f;
    double d;
    boolean tr;
    boolean fa;


    public short getSh() {
        return sh;
    }

    public void setSh(short sh) {
        this.sh = sh;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public long getL() {
        return l;
    }

    public void setL(long l) {
        this.l = l;
    }

    public byte getB() {
        return b;
    }

    public void setB(byte b) {
        this.b = b;
    }

    public char getC() {
        return c;
    }

    public void setC(char c) {
        this.c = c;
    }

    public float getF() {
        return f;
    }

    public void setF(float f) {
        this.f = f;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public boolean isTr() {
        return tr;
    }

    public void setTr(boolean tr) {
        this.tr = tr;
    }

    public boolean isFa() {
        return fa;
    }

    public void setFa(boolean fa) {
        this.fa = fa;
    }

    @Override public boolean equals(Object o) {
        if (o instanceof Primitives) {
            Primitives p = (Primitives) o;
            return sh == p.sh &&
                i == p.i &&
                l == p.l &&
                f == p.f &&
                d == p.d &&
                b == p.b &&
                c == p.c &&
                tr == p.tr &&
                fa == p.fa;
        }
        return false;
    }
}
