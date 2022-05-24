package cfh.calculator.data;


public final class DoubleData extends Data<DoubleData> {

    private double value;
    
    DoubleData(double value) {
        this.value = value;
    }
    
    @Override
    public DoubleData negative() {
        return new DoubleData(-value);
    }
    
    @Override
    public DoubleData add(DoubleData data) {
        return new DoubleData(this.value + data.value);
    }
    
    @Override
    public DoubleData subtract(DoubleData data) {
        return new DoubleData(this.value - data.value);
    }
    
    @Override
    public DoubleData multiply(DoubleData data) {
        return new DoubleData(this.value * data.value);
    }
    
    @Override
    public DoubleData divide(DoubleData data) {
        return new DoubleData(this.value / data.value);
    }
    
    @Override
    public DoubleData remainder(DoubleData data) {
        return new DoubleData(this.value % data.value);
    }

    @Override
    public Double number() {
        return value;
    }
    
    @Override
    public double doubleValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return Double.toString(value);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        return (obj instanceof DoubleData other) && ((DoubleData) obj).value == other.value;
    }
    
    @Override
    public int hashCode() {
        return 47 * Double.hashCode(value);
    }
}
