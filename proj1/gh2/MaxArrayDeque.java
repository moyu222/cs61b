package gh2;

import java.util.Comparator;
import deque.ArrayDeque;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        this.comparator = c;
    }

    public T max() {
        return max(comparator);
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {return null;}

        T maxItem = items[trans(nextFirst + 1)];
        for (int i = 1; i < size; i++) {
            T currentItem = items[trans(nextFirst + 1 + i)];
            if (c.compare(currentItem, maxItem) > 0) {
                maxItem = currentItem;
            }
        }
        return maxItem;
    }
}
