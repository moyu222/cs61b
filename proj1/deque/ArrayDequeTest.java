package deque;

public class ArrayDequeTest {
    public static void main(String[] args) {
        ArrayDeque<Integer> List = new ArrayDeque<>();
        for (int i = 0; i < 20; i++) {
            List.addLast(2);
        }

        for (int i = 0; i < 20; i++) {
            List.removeLast();
        }

    }


}
