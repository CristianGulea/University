public class MyThread extends Thread{
    private int numberOfColumns;
    private double[] linearMatrix;
    private int numberOfKernelLines;
    private int numberOfKernelColumns;
    double[][] borderMatrix;
    double[][] kernel;
    double[] finalMatrix;
    int start, end;

    public MyThread(int numberOfColumns, double[] linearMatrix, int numberOfKernelLines, int numberOfKernelColumns, double[][] borderMatrix, double[][] kernel, double[] finalMatrix, int start, int end) {
        this.numberOfColumns = numberOfColumns;
        this.linearMatrix = linearMatrix;
        this.numberOfKernelLines = numberOfKernelLines;
        this.numberOfKernelColumns = numberOfKernelColumns;
        this.borderMatrix = borderMatrix;
        this.kernel = kernel;
        this.finalMatrix = finalMatrix;
        this.end = end;
        this.start = start;
    }

    @Override
    public void run(){
        for(int i = start; i < end; i++){
            int cord_i = i / numberOfColumns;
            int cord_j = i % numberOfColumns;
            double value = Main.calculate(cord_i, cord_j, Main.getNeighMatrix(borderMatrix, cord_i, cord_j, numberOfKernelLines, numberOfKernelColumns), kernel);
            finalMatrix[i] = value;
        }
    }
}
