import java.io.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {


        int M =  10, N = 10000, m = 5, n = 5, bound = 100;
        int numberOfLinearization = Math.max(m/2, n/2);

        double[][] matrix = readMatrixFromFile("C:\\ppd\\lab1\\matrix.txt", M, N);
        double[][] borderMatrix = repeatBorderMatrix(matrix, numberOfLinearization);
        double[][] kernel = readMatrixFromFile("C:\\ppd\\lab1\\kernel.txt", m, n);
        double[] linearMatrix = matrixLinearization(matrix);
        double[] finalMatrix = new double[linearMatrix.length];

        Long startTime = System.nanoTime();
        if(Objects.equals(args[0], "0")){
            for(int i = 0; i < linearMatrix.length; i++){
                int cord_i = i / N;
                int cord_j = i % N;
                double value = calculate(cord_i, cord_j, getNeighMatrix(borderMatrix, cord_i, cord_j, m, n), kernel);
                finalMatrix[i] = value;
            }
        }
        else{
            int numberOfThreads = Integer.parseInt(args[0]);

            MyThread[] threads = new MyThread[numberOfThreads];

            int defaultLength = (M*N) / numberOfThreads;
            int start = 0;
            int end = defaultLength;
            int rest = (M * N) % numberOfThreads;
            for (int i = 0; i<numberOfThreads; i++){
                if (rest > 0){
                    end++;
                    rest--;
                }
                threads[i] = new MyThread(N, linearMatrix, m, n, borderMatrix, kernel, finalMatrix, start, end);
                threads[i].start();
                start = end;
                end = start + defaultLength;

            }

            for (int i = 0; i < numberOfThreads; i++){
                threads[i].join();
            }

        }
        //writeMatrix("C:\\ppd\\lab1\\resultMatrix", unlinearizationVector(finalMatrix, M, N));
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

    private static double[][] createRandomMatrix(int m, int n, int bound){
        Random random = new Random();
        double[][] matrix = new double[m][n];
        for (int i = 0; i < m; i++){
            for (int j = 0; j < n; j++){
                matrix[i][j] = random.nextInt(bound);
            }
        }
        return matrix;
    }

    private static double[][] borderMatrix(double[][] matrix){
        double[][] newMatrix = new double[matrix.length + 2][matrix[0].length + 2];
        for(int i = 0; i < newMatrix.length; i++){
            for(int j = 0; j < newMatrix[0].length; j++){
                if (i!=0 && j!=0 && i!=newMatrix.length-1 && j!=newMatrix[0].length-1){
                    newMatrix[i][j] = matrix[i-1][j-1];
                }
                else if (i == 0 && j > 0 && j < newMatrix[0].length-1) newMatrix[i][j] = matrix[i][j - 1];
                else if (i == newMatrix.length - 1 && j > 0 && j < newMatrix[0].length-1) newMatrix[i][j] = matrix[i - 2][j - 1];
                else if (j == 0 && i > 0 && i < newMatrix.length - 1) newMatrix[i][j] = matrix[i - 1][j];
                else if (j == newMatrix[0].length - 1 && i > 0 && i < newMatrix.length - 1) newMatrix[i][j] = matrix[i - 1][j - 2];
            }
        }
        newMatrix[0][0] = matrix[0][0];
        newMatrix[newMatrix.length - 1][newMatrix[0].length - 1] = matrix[matrix.length - 1][matrix[0].length - 1];
        newMatrix[0][newMatrix[0].length - 1] = matrix[0][matrix[0].length - 1];
        newMatrix[newMatrix.length - 1][0] =  matrix[matrix.length - 1][0];
        return newMatrix;
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

    private static double[][] repeatBorderMatrix(double[][] matrix, int numberOfBorder){
        double[][] borderMatrix = matrix;
        for (int i = 0; i < numberOfBorder; i++){
            borderMatrix = borderMatrix(borderMatrix);
        }
        return borderMatrix;
    }

    public static double[][] getNeighMatrix(double[][] borderMatrix, int i, int j, int m , int n){
        i += m/2; j += n/2;
        int contNewMI = 0; int contNewMJ = 0;
        double[][] newMatrix = new double[m][n];
        for(int cont_i = i - m/2; cont_i <= i + m/2; cont_i++){
            contNewMJ = 0;
            for (int cont_j = j - n / 2; cont_j <= j + n/2; cont_j++){
                newMatrix[contNewMI][contNewMJ] = borderMatrix[cont_i][cont_j];
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


