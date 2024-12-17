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

    private void resize(int capacity) {
        T[] tempDeque = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            tempDeque[i] = items[trans(nextFirst + 1 + i)];
        }
        items = tempDeque;
        nextFirst = capacity - 1;
        nextLast = size;
    }

    public int trans(int index) {
        return (index + items.length) % items.length;
    }

    public void addFirst(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextFirst] = item;
        size += 1;
        trans(nextFirst - 1);
    }

    public void addLast(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextLast] = item;
        size += 1;
        trans(nextLast + 1);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }


    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(items[trans(nextFirst + 1 + i)] + " ");
        }
        System.out.println();
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        nextFirst = trans(nextFirst + 1);
        T item = items[nextFirst];
        items[nextFirst] = null;
        size--;
        if (size < items.length / 4 && items.length > 8) {
            resize(items.length / 2);
        }
        return item;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        nextLast = trans(nextLast - 1);
        T item = items[nextLast];
        items[nextLast] = null;
        size--;
        if (size < items.length / 4 && items.length > 8) {
            resize(items.length / 2);
        }
        return item;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return items[trans(nextFirst + 1 + index)];
    }

}
