package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author zyzh
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    @Override
    public void clear() {
        n = 0;
        initializeBuckets();
    }

    @Override
    public boolean containsKey(K key) {
        Set<K> kset = this.keySet();
        return kset.contains(key);
    }

    @Override
    public V get(K key) {
        for (Collection<Node> start : this.buckets) {
            for (Node n : start) {
                if (n != null && n.key.equals(key)) {
                    return n.value;
                }
            }
        }
        return null;
    }

    @Override
    public int size() {
        return n;
    }

    /** check the key before add, if existed, change the value
     * null keys will never be inserted  */
    @Override
    public void put(K key, V value) {
        for (Collection<Node> start : this.buckets) {
            if (start != null) {
                for (Node n : start) {
                    if (n != null && n.key.equals(key)) {
                        n.value = value;
                        return;
                    }
                }
            }
        }

        int numOfBuck = Math.floorMod(key.hashCode(),m);
        buckets[numOfBuck].add(new Node(key, value));
        n += 1;
        resize();

    }

    @Override
    public Set<K> keySet() {
        Set<K> hs = new HashSet<>();
        for (Collection<Node> start : buckets) {
            if (start != null) {
                for (Node n : start) {
                    if (n != null ) {
                        hs.add(n.key);
                    }
                }
            }
        }
        return hs;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        HashSet<K> hs = new HashSet<>();
        for (Collection<Node> start : buckets) {
            if (start != null) {
                for (Node n : start) {
                    if (n != null ) {
                        hs.add(n.key);
                    }
                }
            }
        }
        return hs.iterator();
    }


    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!

    /** n is the number of node. and m is the number of buckets */
    private int n;
    private int m;
    private double loadFactor;

    /** Constructors */
    public MyHashMap() {
        n = 0;
        m = 16;
        loadFactor = 0.75;
        buckets = createTable(m);
        initializeBuckets();
    }

    public MyHashMap(int initialSize) {
        n = 0;
        m = initialSize;
        loadFactor = 0.75;
        buckets = createTable(m);
        initializeBuckets();
    }

    private void initializeBuckets() {
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = createBucket();
        }
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        n = 0;
        m = initialSize;
        loadFactor = maxLoad;
        buckets = createTable(m);
        initializeBuckets();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    /** resize the buckets if N/M => loadFactor
     * double M. use .hashCode() and Math.floorMod and .equals() */
    private void resize() {
        if (!((n / m) <= loadFactor)) {
            m = m * 2;
            MyHashMap<K, V> newHM = new MyHashMap(m);
            // reput all nodes, so let's first iterator
            for (Collection<Node> start : buckets) {

                for (Node n : start) {
                    newHM.put(n.key, n.value);
                }
            }
            this.buckets = newHM.buckets;
        }
    }
}
