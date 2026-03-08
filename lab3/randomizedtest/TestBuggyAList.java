package randomizedtest;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing<Integer> a=new AListNoResizing<Integer>();
        BuggyAList<Integer> b=new BuggyAList<Integer>();
        for(int i=4;i<7;i++){
            a.addLast(i);
            b.addLast(i);
            assertEquals(a.size(),b.size());
        }
        for(int i=0;i<3;i++){
            assertEquals(a.removeLast(),b.removeLast());
        }
    }

    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B=new BuggyAList<Integer>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int size2=B.size();
                assertEquals(size,size2);
                System.out.println("size: " + size);
            }else if(operationNumber==2){
                if(L.size()>0&&B.size()>0){
                    int a=L.getLast();
                    int b=B.getLast();
                    assertEquals(a,b);
                    System.out.println("getLast: " + a );
                }
            }else if(operationNumber==3){
                if(L.size()>0&&B.size()>0) {
                    L.removeLast();
                    B.removeLast();
                    System.out.println("removeLast");
                }
            }

        }
    }

}
