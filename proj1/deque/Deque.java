package deque;

import java.util.Iterator;

public interface Deque<T> extends Iterable<T> {

    default boolean isEmpty() {
        return this.size() == 0;
    }

    int size();

    void addFirst(T item);

    void addLast(T item);

    void printDeque();

    T removeFirst();

    T removeLast();

    T get(int index);

    Iterator<T> iterator();

}
