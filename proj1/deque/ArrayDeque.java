package deque;

public class ArrayDeque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
    }

    private void resize() {
        int capacity = size * 2;
        T[] tempDeque =  (T[]) new Object[capacity];
        System.arraycopy(items, 0, tempDeque, 0, size);
        System.arraycopy(items, 0, tempDeque, size, size);
        T[] a = (T[]) new Object[capacity];
        int first = capacity / 4;
        System.arraycopy(tempDeque, trans(nextFirst + 1), a, first, size);
    }

    public int trans(int index) {
        if (index >= items.length){
            return index - items.length;
        } else if (index == -1) {
            return items.length - 1;
        }else {
            return index;
        }
    }

    public void addFirst(T item) {
        this.resize();
        items[nextFirst] = item;
        size += 1;
        trans(nextFirst - 1);
    }

    public void addLast(T item) {
        this.resize();
        items[nextLast] = item;
        size += 1;
        trans(nextLast + 1);
    }

    public boolean isEmpty() {
        if (size == 0) {
            return true;
        }else {
            return false;
        }
    }

    public int size() {
        return size;
    }


    public void printDeque() {
        int first = trans(nextFirst + 1);
        int last = trans(nextLast -1);
        if (size != 0 && first > last) {
            for (int i = first; i < (last + items.length); i++) {
                System.out.println(items[trans(i)]);
                System.out.println( );
            }
        } else if (size != 0) {
            for (int i = first; i < last; i++) {
                System.out.println(items[trans(i)]);
                System.out.println( );
            }
        }
    }

    public T removeFrist() {
        int first = trans(nextFirst + 1);
        T x = items[first];
        if (x == null) {
            return null;
        }else {
            items[first] = null;
            size -= 1;
            if (size < items.length / 4) {
                resize();
            }
            return x;
        }
    }

    public T removeLast() {
        int last = trans(nextLast - 1);
        T x = items[last];
        if (x == null) {
            return null;
        }else {
            items[last] = null;
            size -= 1;
            if (size < items.length / 4) {
                resize();
            }
            return x;
        }
    }

    public T get(int key) {
        int index = trans(nextFirst + 1 + key);
        if (items[index] != null) {
            return items[index];
        }else {
            return null;
        }
    }




}
