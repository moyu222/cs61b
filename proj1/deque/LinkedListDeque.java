package deque;
import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T> {
    private class Node{
        public T item;
        public Node pre;
        public Node next;

        public Node(T i, Node p, Node n) {
            item = i;
            pre = p;
            next = n;
        }

    }

    private Node sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.pre = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        Node first = new Node(item, sentinel, sentinel.next);
        sentinel.next = first;
        first.next.pre = first;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        Node last = new Node(item, sentinel.pre, sentinel);
        sentinel.pre = last;
        last.pre.next = last;
        size += 1;
    }

//    public boolean isEmpty() {
//        if (sentinel.pre == sentinel) {
//            return true;
//        }else{
//            return false;
//        }
//    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node p = sentinel.next;
        while(p != sentinel) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if(isEmpty()) {
            return null;
        }else{
            Node first = sentinel.next;
            sentinel.next = first.next;
            sentinel.next.pre = sentinel;
            size = size - 1;
            return first.item;
        }
    }

    @Override
    public T removeLast() {
        if(isEmpty()) {
            return null;
        }else {
            Node last = sentinel.pre;
            sentinel.pre = last.pre;
            sentinel.pre.next = sentinel;
            size = size - 1;
            return last.item;
        }
    }

    @Override
    public T get(int index) {
        Node p = sentinel.next;
        int temp = 0;
        while(p != sentinel) {
            if (temp == index) {
                return p.item;
            }
            temp += 1;
            p = p.next;
        }
        return null;
    }

    private T getRecursiveHelper(Node curr, int index) {
        if (index == 0) {
            return curr.item;
        }
        if (curr == sentinel) {
            return null;
        }
        return getRecursiveHelper(curr.next, index - 1);
    }

    public T getRecursive(int index) {
        Node p = sentinel.next;
        return getRecursiveHelper(p,index);
    }

    /** implement for loop iteration */
    @Override
    public Iterator<T> iterator() {
        return new LinkListDequeIterator();
    }

    private class LinkListDequeIterator implements Iterator<T> {
        private Node curr;
        private int count;
        public LinkListDequeIterator() {curr = sentinel.next; count = 0;}

        @Override
        public boolean hasNext() {
            return count < size;
        }

        @Override
        public T next() {
            T returnValue = curr.item;
            curr = curr.next;
            count += 1;
            return returnValue;
        }
    }

    /** implement .equal method */
    @Override
    public boolean equals(Object other) {
        if (this == other) {return true;}
        if (other instanceof LinkedListDeque otherDeque) {
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
