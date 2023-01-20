import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class MyThread extends Thread{
    private int numberOfColumns;
    private double[] linearMatrix;
    private int numberOfKernelLines;
    private int numberOfKernelColumns;
    double[][] kernel;
    int start, end;
    CyclicBarrier barrier;
    int M;

    public MyThread(int numberOfColumns, double[] linearMatrix, int numberOfKernelLines, int numberOfKernelColumns, double[][] kernel, int start, int end, CyclicBarrier barrier, int M) {
        this.numberOfColumns = numberOfColumns;
        this.linearMatrix = linearMatrix;
        this.numberOfKernelLines = numberOfKernelLines;
        this.numberOfKernelColumns = numberOfKernelColumns;
        this.kernel = kernel;
        this.end = end;
        this.start = start;
        this.barrier = barrier;
        this.M = M;
    }

    @Override
    public void run(){
        try {
        double[] cacheVec = new double[end-start];
        for(int i = start; i < end; i++){
            int cord_i = i / numberOfColumns;
            int cord_j = i % numberOfColumns;
            double value = Main.calculate(cord_i, cord_j, Main.getNeighMatrix(linearMatrix, cord_i, cord_j, numberOfKernelLines, numberOfKernelColumns, this.numberOfColumns, M), kernel);
            cacheVec[i - start] = value;
        }
        barrier.await();
        for(int i = start; i < end; i++){
            linearMatrix[i] = cacheVec[i-start];
        }
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
