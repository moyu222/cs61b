package randomizedtest;

import antlr.DefineGrammarSymbols;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import timingtest.AList;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE

    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> al1 = new AListNoResizing<>();
        BuggyAList<Integer> al2 = new BuggyAList<>();
        al1.addLast(4);
        al2.addLast(4);
        al1.addLast(5);
        al2.addLast(5);
        al1.addLast(6);
        al2.addLast(6);
        assertEquals(al1.size(), al2.size());
        assertEquals(al1.removeLast(), al2.removeLast());
        assertEquals(al1.removeLast(), al2.removeLast());
        assertEquals(al1.removeLast(), al2.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> L1 = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                L1.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                assertEquals(L.size(), L1.size());
            } else if (operationNumber == 2) {
                // getLast
                if (L.size() != 0) {
                    assertEquals(L.getLast(), L1.getLast());
                }
            } else if (operationNumber == 3) {
                // removeLast
                if (L.size() != 0) {
                    L.removeLast();
                    L1.removeLast();
                }
            }
        }
    }

}
