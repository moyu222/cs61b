package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>{
    public T[] items;
    public int size;
    public int nextFirst;
    public int nextLast;

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

    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextFirst] = item;
        nextFirst = trans(nextFirst - 1);
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextLast] = item;
        nextLast = trans(nextLast + 1);
        size += 1;
    }
/**
    public boolean isEmpty() {
        return size == 0;
    }*/

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(items[trans(nextFirst + 1 + i)] + " ");
        }
        System.out.println();
    }

    @Override
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

    @Override
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

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return items[trans(nextFirst + 1 + index)];
    }

    /** implement for loop iteration*/
    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int pos;
        private int count;
        public ArrayDequeIterator() {
            pos = trans(nextFirst + 1);
        }

        @Override
        public boolean hasNext() {
            return count < size;
        }

        public T next() {
            T returnItem = items[pos];
            pos = trans(pos + 1);
            count += 1;
            return returnItem;
        }

    }

    /** implement equals method*/
    @Override
    public boolean equals(Object other) {
        if (this == other) {return true;}
        if (other instanceof ArrayDeque otherDeque) {
            if (this.size != otherDeque.size) {return false;}
            for (int i = 0; i < size; i++) {
                if (this.get(i) == null) {
                    if (otherDeque.get(i) != null) {
                        return false;
                    }
                } else if (!this.get(i).equals(otherDeque.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
