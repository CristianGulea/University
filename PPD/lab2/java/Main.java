import java.io.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {

        int M =  10, N = 10000, m = 5, n = 5, bound = 100;
        int numberOfLinearization = Math.max(m/2, n/2);

        double[][] matrix = readMatrixFromFile("C:\\lab2\\ppd\\matrix.txt", M, N);
        double[][] kernel = readMatrixFromFile("C:\\lab2\\ppd\\kernel.txt", m, n);
        double[] linearMatrix = matrixLinearization(matrix);
        double[] finalMatrix = new double[linearMatrix.length];
        String secv = "";

        Long startTime = System.nanoTime();
        if(Objects.equals(args[0], "0")){
            for(int i = 0; i < linearMatrix.length; i++){
                int cord_i = i / N;
                int cord_j = i % N;
                double value = calculate(cord_i, cord_j, getNeighMatrix(linearMatrix, cord_i, cord_j, m, n, N, M), kernel);
                finalMatrix[i] = value;
            }
            secv = "secv";
        }
        else{
            int numberOfThreads = Integer.parseInt(args[0]);

            MyThread[] threads = new MyThread[numberOfThreads];
            CyclicBarrier barrier = new CyclicBarrier(numberOfThreads);

            int defaultLength = (M*N) / numberOfThreads;
            int start = 0;
            int end = defaultLength;
            int rest = (M * N) % numberOfThreads;
            for (int i = 0; i<numberOfThreads; i++){
                if (rest > 0){
                    end++;
                    rest--;
                }
                threads[i] = new MyThread(N, linearMatrix, m, n, kernel, start, end, barrier, M);
                threads[i].start();
                start = end;
                end = start + defaultLength;

            }

            for (int i = 0; i < numberOfThreads; i++){
                threads[i].join();
            }

            secv = "thread";
        }

        /**
        if (secv.equals("thread")){
            writeMatrix("C:\\ppd\\lab2\\resultMatrix", unlinearizationVector(linearMatrix, M, N));
        }
        else{
            writeMatrix("C:\\ppd\\lab2\\resultMatrix", unlinearizationVector(finalMatrix, M, N));
        }
         **/


        Long endTime = System.nanoTime();
        System.out.println(endTime-startTime);
    }

    private static void printMatrix(double[][] matrix){
        for(int i = 0; i<matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static void printVector(double[] vector){
        for (double elem : vector) {
            System.out.print(elem + " ");
        }
    }

    private static double[] matrixLinearization(double[][] matrix){
        double[] linearMatrix = new double[matrix.length*matrix[0].length];
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j <matrix[0].length; j++){
                linearMatrix[i * matrix[0].length + j] = matrix[i][j];
            }
        }
        return linearMatrix;
    }

    public static double[][] getNeighMatrix(double[] linearMatrix, int i, int j, int m , int n, int N, int M){
        int contNewMI = 0; int contNewMJ = 0;
        int saveContI = 0; int saveContJ = 0;
        double[][] newMatrix = new double[m][n];
        for(int cont_i = i - m/2; cont_i <= i + m/2; cont_i++){
            contNewMJ = 0;
            for (int cont_j = j - n / 2; cont_j <= j + n/2; cont_j++){
                if (cont_i  < 0)
                {
                    saveContI = 0;
                }
                else if (cont_i >= M)
                {
                    saveContI = M - 1;
                }
                else
                {
                    saveContI = cont_i;
                }
                if (cont_j  < 0)
                {
                    saveContJ = 0;
                }
                else if (cont_j >= N){
                    saveContJ = N - 1;
                }
                else
                {
                    saveContJ = cont_j;
                }

                newMatrix[contNewMI][contNewMJ] = linearMatrix[saveContI * N + saveContJ];
                contNewMJ++;
            }
            contNewMI++;
        }
        return newMatrix;
    }

    public static double calculate(int i, int j, double[][] matrix, double[][] kernel) {
        int value = 0;
        for(int cont_i = 0; cont_i < matrix.length; cont_i++){
            for(int cont_j = 0; cont_j < matrix.length; cont_j++){
                value += matrix[cont_i][cont_j] * kernel[cont_i][cont_j];
                //value += Math.sqrt(Math.pow(matrix[cont_i][cont_j], 3) * Math.pow(kernel[cont_i][cont_j], 3));
            }
        }
        return value;
    }

    private static double[][] unlinearizationVector(double[] linearMatrix, int M, int N){
        double[][] matrix =  new double[M][N];
        for(int i = 0; i < linearMatrix.length; i++){
            int cord_i = i / N;
            int cord_j = i % N;
            matrix[cord_i][cord_j] = linearMatrix[i];
        }
        return matrix;
    }

    private static double[][] readMatrixFromFile(String filename, int m, int n) throws FileNotFoundException {
        double[][] matrix = new double[m][n];
        Scanner input = new Scanner(new File(filename));
        while(input.hasNextLine()) {
            for (int i=0; i < m; i++) {
                String[] line = input.nextLine().trim().split(" ");
                for (int j=0; j < n; j++) {
                    matrix[i][j] = Double.parseDouble(line[j]);
                }
            }
        }
        return  matrix;
    }

    static void writeMatrix(String filename, double[][] matrix) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    bw.write(matrix[i][j] + " ");
                }
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


