package deque;
import java.util.Iterator;

public interface Deque<T> extends Iterable<T> {
    default boolean isEmpty() {
        if (this.size() == 0) {return true;}
        return false;
    }

    int size();
    void addFirst(T item);
    void addLast(T item);
    void printDeque();
    T removeFirst();
    T removeLast();
    T get(int idex);
    Iterator<T> iterator();

}
