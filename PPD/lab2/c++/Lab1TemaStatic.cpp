#include <iostream>
#include <vector>
#include <fstream>
#include <chrono>
#include <thread>
#include <sstream>
#include <barrier>
#include <latch>

using namespace std;


const int M = 10;
const int N = 10000;
const int m = 5;
const int n = 5;
const int nr = max(m / 2, n / 2);
const int bound = 10;
const int sizeC = ((M * N) / 16) + 1;



struct structMatrix
{
    int numberOfLines = M;
    int numberOfColumns = N;
    double matrix[M][N];
};

struct structLinearMatrix {
    int numberOfElements = M * N;
    double linearMatrix[M * N];
};

struct structKernelSizeMatrix {
    int numberOfLines = m;
    int numberOfColumns = n;
    double matrix[m][n];
};

struct structMatrix sMatrix;
struct structLinearMatrix sLinearMatrix;
struct structKernelSizeMatrix sNeighMatrix;
struct structKernelSizeMatrix sKernel;


void printMatrix(structMatrix& sM) {
    for (int i = 0; i < sM.numberOfLines; i++) {
        for (int j = 0; j < sM.numberOfColumns; j++) {
            cout << sM.matrix[i][j] << "  ";
        }
        cout << "\n";
    }
}

void printKernelMatrix(structKernelSizeMatrix& sK) {
    for (int i = 0; i < sK.numberOfLines; i++) {
        for (int j = 0; j < sK.numberOfColumns; j++) {
            cout << sK.matrix[i][j] << "  ";
        }
        cout << "\n";
    }
}

void printVector(structLinearMatrix& sLM) {
    for (int i = 0; i < sLM.numberOfElements; i++) {
        cout << sLM.linearMatrix[i] << " ";
    }
}

void matrixLinearization(structLinearMatrix& sLM, structMatrix& sM) {
    for (int i = 0; i < sM.numberOfLines; i++) {
        for (int j = 0; j < sM.numberOfColumns; j++) {
            sLM.linearMatrix[i * sM.numberOfColumns + j] = sM.matrix[i][j];
        }
    }
}

void getNeighMatrix(structLinearMatrix& sL, int i, int j, int m, int n, structKernelSizeMatrix& sK) {
    int contNewMI = 0; int contNewMJ = 0;
    int saveContI = 0; int saveContJ = 0;
    for (int cont_i = i - sK.numberOfLines / 2; cont_i <= i + sK.numberOfLines / 2; cont_i++) {
        contNewMJ = 0;
        for (int cont_j = j - sK.numberOfColumns / 2; cont_j <= j + sK.numberOfColumns / 2; cont_j++) {
            if (cont_i < 0)
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
            if (cont_j < 0)
            {
                saveContJ = 0;
            }
            else if (cont_j >= N) {
                saveContJ = N - 1;
            }
            else
            {
                saveContJ = cont_j;
            }

            sK.matrix[contNewMI][contNewMJ] = sL.linearMatrix[saveContI * N + saveContJ];
            contNewMJ++;
        }
        contNewMI++;
    }
}

double calculate(int i, int j, structKernelSizeMatrix& sKMatrix, structKernelSizeMatrix& sKKernel) {
    int value = 0;
    for (int cont_i = 0; cont_i < sKMatrix.numberOfLines; cont_i++) {
        for (int cont_j = 0; cont_j < sKMatrix.numberOfLines; cont_j++) {
            value += sKMatrix.matrix[cont_i][cont_j] * sKKernel.matrix[cont_i][cont_j];
            //value += Math.sqrt(Math.pow(matrix[cont_i][cont_j], 3) * Math.pow(kernel[cont_i][cont_j], 3));
        }
    }
    return value;
}

void unlinearizationVector(structLinearMatrix& sL, int M, int N, structMatrix& sM) {
    for (int i = 0; i < sL.numberOfElements; i++) {
        int cord_i = i / N;
        int cord_j = i % N;
        sM.matrix[cord_i][cord_j] = sL.linearMatrix[i];
    }
}

void readKernelFromAFile(string fileName, structKernelSizeMatrix& sK) {
    ifstream myfile;
    myfile.open(fileName);

    for (int i = 0; i < sK.numberOfLines; i++) {
        for (int j = 0; j < sK.numberOfColumns; j++) {
            myfile >> sK.matrix[i][j];
        }
    }
}

void readMatrixFromAFile(string fileName, structMatrix& sM) {
    ifstream myfile;
    myfile.open(fileName);

    for (int i = 0; i < sM.numberOfLines; i++) {
        for (int j = 0; j < sM.numberOfColumns; j++) {
            myfile >> sM.matrix[i][j];
        }
    }
}

void writeMatrixToFile(string filename) {
    fstream myfile;

    myfile.open(filename, fstream::out);

    for (int i = 0; i < sMatrix.numberOfLines; i++)
    {
        for (int j = 0; j < sMatrix.numberOfColumns; j++)
        {
            myfile << sMatrix.matrix[i][j] << " ";
        }
        myfile << "\n";
    }

    myfile.close();
}

void f(structLinearMatrix& sLinearMatrix, structMatrix& sMatrix, structKernelSizeMatrix& sKernel, int start, int end, latch& barrier) {
    structKernelSizeMatrix sNeighMatrix;
    double cacheLinearMatrix[sizeC];
    for (int i = start; i < end; i++) {
        int cord_i = i / N;
        int cord_j = i % N;

        getNeighMatrix(sLinearMatrix, cord_i, cord_j, m, n, sNeighMatrix);
        double value = calculate(cord_i, cord_j, sNeighMatrix, sKernel);
        cacheLinearMatrix[i-start] = value;
    }
    barrier.arrive_and_wait();

    for (int i = start; i < end; i++) {
        sLinearMatrix.linearMatrix[i] = cacheLinearMatrix[i - start];
    }
    
}


int main(int argc, char* argv[])
{
    readMatrixFromAFile("C:\\ppd\\lab2\\matrix.txt", sMatrix);
    readKernelFromAFile("C:\\ppd\\lab2\\kernel.txt", sKernel);
    matrixLinearization(sLinearMatrix, sMatrix);
    string secv;
    structLinearMatrix sFinal;
    string p_string = argv[1];
    int p;
    stringstream obj;
    obj << p_string; 
    obj >> p;  

    auto startTime = chrono::high_resolution_clock::now();
    
    if (p != 0) {

        latch barrier{ p };
        int start = 0, rest = sLinearMatrix.numberOfElements % p, cat = sLinearMatrix.numberOfElements / p;
        int end = cat;
        vector<thread> thr(p);
        

        for (int i = 0; i < p; i++) {
            if (rest) {
                end++;
                rest--;
            }
            thr[i] = thread(f, ref(sLinearMatrix), ref(sMatrix), ref(sKernel), start, end, ref(barrier));
            start = end;
            end = start + cat;
        }

        for (int i = 0; i < p; i++) {
            thr[i].join();
        }

        secv = "thread";

    }
    else {
        for (int i = 0; i < sLinearMatrix.numberOfElements; i++) {
            int cord_i = i / N;
            int cord_j = i % N;

            getNeighMatrix(sLinearMatrix, cord_i, cord_j, m, n, sNeighMatrix);
            double value = calculate(cord_i, cord_j, sNeighMatrix, sKernel);
            sFinal.linearMatrix[i] = value;
        }
        secv = "secv";
    }
    
    
    if (secv == "thread") {
        unlinearizationVector(sLinearMatrix, M, N, sMatrix);
    }
    else {
        unlinearizationVector(sFinal, M, N, sMatrix);
    }
    
    
    auto endTime = chrono::high_resolution_clock::now();
    cout << chrono::duration<double, milli>(endTime - startTime).count() << "\n";

    writeMatrixToFile("C:\\ppd\\lab2\\finalMatrixC.txt");
    

}