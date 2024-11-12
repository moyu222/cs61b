package deque;

public class LinkedListDeque<T> {
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

    public void addFirst(T item) {
        Node first = new Node(item, sentinel, sentinel.next);
        sentinel.next = first;
        first.next.pre = first;
        size += 1;
    }

    public void addLast(T item) {
        Node last = new Node(item, sentinel.pre, sentinel);
        sentinel.pre = last;
        last.pre.next = last;
        size += 1;
    }

    public boolean isEmpty() {
        if (sentinel.pre == sentinel) {
            return true;
        }else{
            return false;
        }
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Node p = sentinel.next;
        while(p.next != sentinel) {
            System.out.println(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }

    public T removeFirst() {
        if(sentinel.next == sentinel) {
            return null;
        }else{
            Node first = sentinel.next;
            sentinel.next = sentinel.next.next;
            sentinel.next.pre = sentinel;
            size = size - 1;
            return first.item;
        }
    }

    public T removeLast() {
        if(sentinel.next == sentinel) {
            return null;
        }else {
            Node last = sentinel.pre;
            sentinel.pre = sentinel.pre.pre;
            sentinel.pre.next = sentinel;
            size = size - 1;
            return last.item;
        }
    }

    public T get(int index) {
        Node p = sentinel.next;
        int temp = 0;
        while(p.next != sentinel) {
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
}
