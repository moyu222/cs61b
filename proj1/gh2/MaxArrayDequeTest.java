package gh2;
import java.util.Comparator;
import org.junit.Test;
import static org.junit.Assert.*;

public class MaxArrayDequeTest {
    @Test
    public void testIntegerMax() {
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(new IntegerComparator());
        deque.addLast(1);
        deque.addLast(3);
        deque.addLast(2);
        assertEquals(Integer.valueOf(3), deque.max()); // Should return 3
    }

    @Test
    public void testStringMax() {
        MaxArrayDeque<String> deque = new MaxArrayDeque<>(new StringLengthComparator());
        deque.addLast("cat");
        deque.addLast("elephant");
        deque.addLast("dog");
        assertEquals("elephant", deque.max()); // Should return "elephant"
    }

    @Test
    public void testEmptyDeque() {
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(new IntegerComparator());
        assertNull(deque.max()); // Should return null
    }

    @Test
    public void testMultipleMaxValues() {
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(new IntegerComparator());
        deque.addLast(1);
        deque.addLast(5);
        deque.addLast(5);
        deque.addLast(3);
        assertEquals(Integer.valueOf(5), deque.max()); // Should return 5
    }

    // Comparator for Integer
    static class IntegerComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer a, Integer b) {
            return Integer.compare(a, b);
        }
    }

    // Comparator for String Length
    static class StringLengthComparator implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            return Integer.compare(a.length(), b.length());
        }
    }
}

