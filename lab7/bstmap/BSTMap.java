package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K,V> {

    private int size;
    private Node root;

    /** private class Node - represent one node in BST that store key-value */
    private class Node {
        private K key;
        private V val;
        private Node left, right;

        public Node(K key, V val) {
            this.key = key;
            this.val =val;
        }
    }

    /** initialize an empty BSTMap  */
    public BSTMap() {
        size = 0;
        root = null;
    }

    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    /** find the key. return true even if the val is null */
    @Override
    public boolean containsKey(K key) {
        return findKey(root, key);
    }

    private boolean findKey(Node x, K key) {
        if (x == null) {
            return false;
        }
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            return findKey(x.left, key);
        } else if (cmp > 0) {
            return findKey(x.right, key);
        } else {
            return true;
        }
    }

    @Override
    public V get(K key) {
        return find(root, key);
    }
    private V find(Node x, K key) {
        if (x == null) {
            return null;
        }
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            return find(x.left, key);
        } else if (cmp > 0) {
            return find(x.right, key);
        } else {
            return x.val;
        }
    }

    @Override
    public int size() {
        return size;
    }

    /** first step to complete. insert node to make a BST
     * when the key is same, update the value*/
    @Override
    public void put(K key, V value) {
        root = put(root, key, value);
        size += 1;
    }

    private Node put(Node x, K key, V value) {
        if (x == null) {
            return new Node(key, value);
        }
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            x.left = put(x.left, key, value);
        } else if (cmp > 0) {
            x.right = put(x.right, key, value);
        } else {
            x.val = value;
        }
        return x;
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /** not completed */
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    private class BSTMapIter implements Iterator<K> {

        private Node curr;

        public BSTMapIter(){
            curr = root;
        }
        @Override
        public boolean hasNext() {
            return curr.left != null || curr.right != null;
        }

        @Override
        public K next() {
            return null;
        }
    }
}
