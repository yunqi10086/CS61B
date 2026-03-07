package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        AList<Integer>Ns = new AList<Integer>();
        AList<Double> times=new AList<Double>();
        AList<Integer> opCounts=new AList<Integer>();
        for(int i=1;i<=128;i*=2){
            Ns.addLast(1000*i);
        }
        for(int i=0;i<Ns.size();i++){
            AList<Integer> a=new AList<Integer>();
            Stopwatch sw = new Stopwatch();
            for(int j=1;j<Ns.get(i);j++){
                a.addLast(0);
            }
            double timeInSeconds = sw.elapsedTime();
            times.addLast(timeInSeconds);
            opCounts.addLast(Ns.get(i));
        }
        printTimingTable(Ns, times, opCounts);
    }
}
