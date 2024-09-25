import java.util.HashMap;
import java.util.NoSuchElementException;
/*_kcole9_*/
import java.util.Set;
/*end_kcole9_*/

/**
 * Implements MapADT using an instance of java.util.HashMap.
 */
public class BasicMap<KeyType, ValueType> implements MapADT<KeyType, ValueType> {

    // use an instance of java.util.HashMap for the functionality of BasicMap
    protected HashMap<KeyType, ValueType> baseMap = new HashMap<>();

    @Override
    public void put(KeyType key, ValueType value) throws IllegalArgumentException {
        if (key == null)
            throw new NullPointerException("null keys not allowed");
        if (baseMap.containsKey(key))
            throw new IllegalArgumentException("key " + key.toString() + " already present in map");
        baseMap.put(key, value);
    }

    @Override
    public boolean containsKey(KeyType key) {
        if (key == null)
            throw new NullPointerException("null keys not allowed");
        return baseMap.containsKey(key);
    }

    @Override
    public ValueType get(KeyType key) throws NoSuchElementException {
        if (key == null)
            throw new NullPointerException("null keys not allowed");
        if (baseMap.containsKey(key)) {
            return baseMap.get(key);
        }
        throw new NoSuchElementException("key " + key.toString() + " not in map");
    }

    @Override
    public ValueType remove(KeyType key) throws NoSuchElementException {
        if (key == null)
            throw new NullPointerException("null keys not allowed");
        if (baseMap.containsKey(key)) {
            return baseMap.remove(key);
        }
        throw new NoSuchElementException("key " + key.toString() + " not in map");
    }

    @Override
    public void clear() {
        baseMap.clear();
    }

    @Override
    public int getSize() {
        return baseMap.size();
    }

    @Override
    public int getCapacity() {
        throw new UnsupportedOperationException("BasicMap does not support the .getCapacity() method");
    }
/*_kcole9_*/
    @Override
    public Set<KeyType> keySet() {
    	return baseMap.keySet();
    }
/*end_kcole9_*/
}
