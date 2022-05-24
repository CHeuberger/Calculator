package cfh.calculator.data;

import static java.util.Objects.*;

import java.util.HashMap;
import java.util.Map;


public class Environment<D extends Data<D>> {

    public static Environment<DoubleData> of(String name1, double value1) {
        return new Environment<>(name1, new DoubleData(value1));
    }
    
    public static Environment<DoubleData> of(String name1, double value1, String name2, double value2) {
        return new Environment<>(name1, new DoubleData(value1), name2, new DoubleData(value2));
    }
    
    public static Environment<DoubleData> of(String name1, double value1, String name2, double value2, String name3, double value3) {
        return new Environment<>(name1, new DoubleData(value1), name2, new DoubleData(value2), name3, new DoubleData(value3));
    }
    
    //==============================================================================================
    
    private final Map<String, D> variables = new HashMap<>();
    
    public Environment() {
    }
    
    public Environment(String name1, D value1) {
        variables.put(requireNonNull(name1), requireNonNull(value1));
    }
    
    public Environment(String name1, D value1, String name2, D value2) {
        variables.put(requireNonNull(name1), requireNonNull(value1));
        variables.put(requireNonNull(name2), requireNonNull(value2));
    }
    
    public Environment(String name1, D value1, String name2, D value2, String name3, D value3) {
        variables.put(requireNonNull(name1), requireNonNull(value1));
        variables.put(requireNonNull(name2), requireNonNull(value2));
        variables.put(requireNonNull(name3), requireNonNull(value3));
    }
    
    public Environment<D> put(String name, D value) {
        variables.put(requireNonNull(name), requireNonNull(value));
        return this;
    }
    
    public int size() {
        return variables.size();
    }
    
    public void clear() {
        variables.clear();
    }
    
    public boolean contains(String key) {
        return variables.containsKey(key);
    }
    
    public D get(String key) {
        return variables.get(key);
    }
    
    public D getOrDefault(String key, D defaultValue) {
        return variables.getOrDefault(key, defaultValue);
    }
    
    public D replace(String key, D value) {
        return variables.replace(key, value);
    }
    
    public D remove(String key) {
        return variables.remove(key);
    }
}
