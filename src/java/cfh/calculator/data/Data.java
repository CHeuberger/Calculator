package cfh.calculator.data;


public abstract sealed class Data<D extends Data<D>> 
permits DoubleData {
    
    @SuppressWarnings("unchecked")
    public D identity() { return (D) this; }
    public abstract D negative();
    
    public abstract D add(D data);
    public abstract D subtract(D data);
    
    public abstract D multiply(D data);
    public abstract D divide(D data);
    public abstract D remainder(D data);

    public abstract Number number();

    public abstract double doubleValue();
    
    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(Object obj);
    
    @Override
    public abstract int hashCode();
    
}
