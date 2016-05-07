package test.mdauml.persistence.classes;

import java.util.ArrayList;
import java.util.List;

import static test.mdauml.persistence.classes.InnerClass.AbstractInner.*;


public class InnerClass {

    private List<TheInner> children = new ArrayList<>();

    public void addChild(String child){
        children.add(new TheInner(child));
    }

    public List<TheInner> getChildren() {
        return children;
    }

    public void setChildren(List<TheInner> children) {
        this.children = children;
    }


    public boolean equals(Object o){
        if(o instanceof  InnerClass){
            InnerClass i = (InnerClass) o ;
            if(i.children == null || children==null)
                return false;
            if(i.children.size()!=children.size())
                return false;
            int j=0;
            for(TheInner inner : children){
                if(!inner.something.equals(i.children.get(j++).something))
                    return false;
            }
            return true;
        }
        return false;
    }
    public class TheInner{

        private String something;

        public TheInner(String something){
            this.something=something;
        }

        public String getSomething() {
            return something;
        }

        public void setSomething(String something) {
            this.something = something;
        }

    }

    public class InnerWithoutConstructor{
        public void mymethod(){
            int s = children.size();
        }
    }
    InnerWithoutConstructor innerWithoutConstructor;

    public void setInnerWithoutConstructor(InnerWithoutConstructor innerWithoutConstructor) {
        this.innerWithoutConstructor = innerWithoutConstructor;
    }

    private class PrivateInner{

    }
    PrivateInner privateInner= new PrivateInner();

    static public class StaticInner{

    }
    StaticInner staticInner;

    static private class StaticPrivateInner{

    }
    StaticPrivateInner staticPrivateInner = new StaticPrivateInner();
    public abstract class AbstractInner{

        public abstract class InnerAbstractInner{

            public abstract void doSomethingElse();

        }

        protected InnerAbstractInner innerAbstractInner;

        public abstract void doSomething();
    }

    public void setPrivateInner(PrivateInner privateInner) {
        this.privateInner = privateInner;
    }

    public void setStaticInner(StaticInner staticInner) {
        this.staticInner = staticInner;
    }

    public void setStaticPrivateInner(StaticPrivateInner staticPrivateInner) {
        this.staticPrivateInner = staticPrivateInner;
    }

    AbstractInner abstractInner = new AbstractInner() {
        @Override
        public void doSomething() {
            innerAbstractInner = new InnerAbstractInner() {
                @Override
                public void doSomethingElse() {
                    int a = 2*2;
                }
            };
        }
    };

    public void initialize(){
        abstractInner.doSomething();
    }
}
