#include <iostream>
#include <vector>
#include <fstream>
#include <chrono>
#include <thread>
#include <sstream>


using namespace std;

const int M = 10;
const int N = 10000;
const int m = 5;
const int n = 5;
const int nr = max(m / 2, n / 2);
const int bound = 10;



struct structMatrix
{
 
    int numberOfLines = M;
    int numberOfColumns = N;
    double** matrix = new double* [M];
    
};

struct structBorderMatrix {
    int numberOfLines = M + (2 * nr);
    int numberOfColumns = N + (2 * nr);
    double** matrix = new double* [M + (2 * nr)];
};

struct structLinearMatrix {
    int numberOfElements = M * N;
    double* linearMatrix = new double[M * N];
};

struct structKernelSizeMatrix {
    int numberOfLines = m;
    int numberOfColumns = n;
    double** matrix = new double*[m];
};



struct structMatrix sMatrix;
struct structBorderMatrix sBorderMatrix;
struct structLinearMatrix sLinearMatrix;
struct structKernelSizeMatrix sNeighMatrix;
struct structKernelSizeMatrix sKernel;



void printMatrix(const structMatrix sM) {
    for (int i = 0; i < sM.numberOfLines; i++) {
        for (int j = 0; j < sM.numberOfColumns; j++) {
            cout << sM.matrix[i][j] << "  ";
        }
        cout << "\n";
    }
}

void printBorderMatrix(const structBorderMatrix sB) {
    for (int i = 0; i < sB.numberOfLines; i++) {
        for (int j = 0; j < sB.numberOfColumns; j++) {
            cout << sB.matrix[i][j] << "  ";
        }
        cout << "\n";
    }
}

void printKernelMatrix(const structKernelSizeMatrix sK) {
    for (int i = 0; i < sK.numberOfLines; i++) {
        for (int j = 0; j < sK.numberOfColumns; j++) {
            cout << sK.matrix[i][j] << "  ";
        }
        cout << "\n";
    }
}

void printVector(const structLinearMatrix sLM) {
    for (int i = 0; i < sLM.numberOfElements; i++) {
        cout << sLM.linearMatrix[i] << " ";
    }
}

void borderMatrix(const structMatrix& sM, structBorderMatrix& sB, int delSize) {
    for (int i = 0; i < sB.numberOfLines - delSize; i++) {
        for (int j = 0; j < sB.numberOfColumns - delSize; j++) {
            if (i != 0 && j != 0 && i != sB.numberOfLines - delSize - 1 && j != sB.numberOfColumns - delSize - 1) {
                sB.matrix[i][j] = sM.matrix[i - 1][j - 1];
            }
            else if (i == 0 && j > 0 && j < sB.numberOfColumns - delSize - 1) sB.matrix[i][j] = sM.matrix[i][j - 1];
            else if (i == sB.numberOfLines - delSize - 1 && j > 0 && j < sB.numberOfColumns - delSize - 1) sB.matrix[i][j] = sM.matrix[i - 2][j - 1];
            else if (j == 0 && i > 0 && i < sB.numberOfLines - delSize - 1) sB.matrix[i][j] = sM.matrix[i - 1][j];
            else if (j == sB.numberOfColumns - delSize - 1 && i > 0 && i < sB.numberOfLines - delSize - 1) sB.matrix[i][j] = sM.matrix[i - 1][j - 2];
        }
    }
    sB.matrix[0][0] = sM.matrix[0][0];
    sB.matrix[sB.numberOfLines - delSize - 1][sB.numberOfColumns - delSize - 1] = sM.matrix[sM.numberOfLines - 1][sM.numberOfColumns - 1];
    sB.matrix[0][sB.numberOfColumns - delSize - 1] = sM.matrix[0][sM.numberOfColumns - 1];
    sB.matrix[sB.numberOfLines - delSize - 1][0] = sM.matrix[sM.numberOfLines - 1][0];
}

void borderSameMatrix(const structBorderMatrix& sM, structBorderMatrix& sB, int delSize) {
    for (int i = 0; i < sB.numberOfLines - delSize; i++) {
        for (int j = 0; j < sB.numberOfColumns - delSize; j++) {
            if (i != 0 && j != 0 && i != sB.numberOfLines - delSize - 1 && j != sB.numberOfColumns - delSize - 1) {
                sB.matrix[i][j] = sM.matrix[i - 1][j - 1];
            }
            else if (i == 0 && j > 0 && j < sB.numberOfColumns - delSize - 1) sB.matrix[i][j] = sM.matrix[i][j - 1];
            else if (i == sB.numberOfLines - delSize - 1 && j > 0 && j < sB.numberOfColumns - delSize - 1) sB.matrix[i][j] = sM.matrix[i - 2][j - 1];
            else if (j == 0 && i > 0 && i < sB.numberOfLines - delSize - 1) sB.matrix[i][j] = sM.matrix[i - 1][j];
            else if (j == sB.numberOfColumns - delSize - 1 && i > 0 && i < sB.numberOfLines - delSize - 1) sB.matrix[i][j] = sM.matrix[i - 1][j - 2];
        }
    }
    sB.matrix[delSize][delSize] = sB.matrix[delSize][delSize + 1];
    sB.matrix[sB.numberOfLines - delSize - 1][sB.numberOfColumns - delSize - 1] = sB.matrix[sB.numberOfLines - delSize - 1][sB.numberOfColumns - delSize - 2];
    sB.matrix[0][sB.numberOfColumns - delSize - 1] = sB.matrix[0][sB.numberOfColumns - delSize - 2];
    sB.matrix[sB.numberOfLines - delSize - 1][0] = sB.matrix[sB.numberOfLines - delSize - 2][0];
}

void matrixLinearization(structLinearMatrix& sLM, const structMatrix& sM) {
    for (int i = 0; i < sM.numberOfLines; i++) {
        for (int j = 0; j < sM.numberOfColumns; j++) {
            sLM.linearMatrix[i * sM.numberOfColumns + j] = sM.matrix[i][j];
        }
    }
}

void getNeighMatrix(const structBorderMatrix& sB, int i, int j, int m, int n, structKernelSizeMatrix& sK) {
    i += sK.numberOfLines / 2; j += sK.numberOfColumns / 2;
    int contNewMI = 0; int contNewMJ = 0;
    for (int cont_i = i - sK.numberOfLines / 2; cont_i <= i + sK.numberOfLines / 2; cont_i++) {
        contNewMJ = 0;
        for (int cont_j = j - sK.numberOfColumns / 2; cont_j <= j + sK.numberOfColumns / 2; cont_j++) {
            sK.matrix[contNewMI][contNewMJ] = sB.matrix[cont_i][cont_j];
            contNewMJ++;
        }
        contNewMI++;
    }
}

double calculate(int i, int j, const structKernelSizeMatrix sKMatrix, const structKernelSizeMatrix sKKernel) {
    int value = 0;
    for (int cont_i = 0; cont_i < sKMatrix.numberOfLines; cont_i++) {
        for (int cont_j = 0; cont_j < sKMatrix.numberOfLines; cont_j++) {
            value += sKMatrix.matrix[cont_i][cont_j] * sKKernel.matrix[cont_i][cont_j];
            //value += Math.sqrt(Math.pow(matrix[cont_i][cont_j], 3) * Math.pow(kernel[cont_i][cont_j], 3));
        }
    }
    return value;
}

void unlinearizationVector(const structLinearMatrix& sL, int M, int N, structMatrix& sM) {
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

    myfile.close();
}

void readMatrixFromAFile(string fileName, structMatrix& sM) {
    ifstream myfile;
    myfile.open(fileName);

    for (int i = 0; i < sM.numberOfLines; i++) {
        for (int j = 0; j < sM.numberOfColumns; j++) {
            myfile >> sM.matrix[i][j];
        }
    }

    myfile.close();
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

void repeatBorderMatrix(const structMatrix& sM, int numberOfBorder, structBorderMatrix& sB) {
    int value = 2 * max(m / 2, n / 2) - 2;
    borderMatrix(sM, sB, value);

    struct structBorderMatrix sb;
    for (int i = 0; i < M + (2 * nr); i++) {
        sb.matrix[i] = new double[N + (2 * nr)];
    }

    for (int i = 1; i < numberOfBorder; i++) {
        for (int i = 0; i < sb.numberOfLines; i++) {
            for (int j = 0; j < sb.numberOfColumns; j++) {
                sb.matrix[i][j] = sB.matrix[i][j];
            }
        }

        value -= 2;
        borderSameMatrix(sb, sB, value);
    }

    for (int i = 0; i < M + (2 * nr); i++) {
        delete[]sb.matrix[i];
    }
    delete[]sb.matrix;
    
}

void f(const structLinearMatrix& sLinearMatrix, const structMatrix& sMatrix, const structBorderMatrix& sBorderMatrix,const  structKernelSizeMatrix& sKernel, structLinearMatrix& sFinal, int start, int end) {
    structKernelSizeMatrix sNeighMatrix;
    for (int i = 0; i < n; i++) {
        sNeighMatrix.matrix[i] = new double[N];
    }

    for (int i = start; i < end; i++) {
        int cord_i = i / N;
        int cord_j = i % N;
        getNeighMatrix(sBorderMatrix, cord_i, cord_j, m, n, sNeighMatrix);
        double value = calculate(cord_i, cord_j, sNeighMatrix, sKernel);
        sFinal.linearMatrix[i] = value;
    }

    for (int i = 0; i < n; i++) {
        delete[]sNeighMatrix.matrix[i];
    }
    delete[]sNeighMatrix.matrix;
}

void alocateDynamicArrays() {
  
    for (int i = 0; i < M; i++) {
        sMatrix.matrix[i] = new double[N];
    }

    for (int i = 0; i < M + (2 * nr); i++) {
        sBorderMatrix.matrix[i] = new double[N + (2 * nr)];
    }

    for (int i = 0; i < n; i++) {
        sKernel.matrix[i] = new double[n];
    }

    for (int i = 0; i < n; i++) {
        sNeighMatrix.matrix[i] = new double[n];
    }

}

void dealocateDynamicArrays() {
    for (int i = 0; i < M; i++) {
        delete[]sMatrix.matrix[i];
    }
    delete[]sMatrix.matrix;

    for (int i = 0; i < M + (2 * nr); i++) {
        delete[]sBorderMatrix.matrix[i];
    }
    delete[]sBorderMatrix.matrix;

    for (int i = 0; i < n; i++) {
        delete[]sKernel.matrix[i];
    }
    delete[]sKernel.matrix;

    for (int i = 0; i < n; i++) {
        delete[]sNeighMatrix.matrix[i];
    }
    delete[]sNeighMatrix.matrix;
}

int main(int argc, char* argv[])
{
    alocateDynamicArrays();

    readMatrixFromAFile("C:\\ppd\\lab1\\matrix.txt", sMatrix);
    readKernelFromAFile("C:\\ppd\\lab1\\kernel.txt", sKernel);
    matrixLinearization(sLinearMatrix, sMatrix);
    repeatBorderMatrix(sMatrix, nr, sBorderMatrix);
    structLinearMatrix sFinal;
    
    string p_string = argv[1];
    int p;
    stringstream obj;
    obj << p_string;
    obj >> p;


    auto startTime = chrono::high_resolution_clock::now();

    if (p > 0) {

        int start = 0, rest = sLinearMatrix.numberOfElements % p, cat = sLinearMatrix.numberOfElements / p;
        int end = cat;
        vector<thread> thr(p);

        for (int i = 0; i < p; i++) {
            if (rest) {
                end++;
                rest--;
            }
            thr[i] = thread(f, ref(sLinearMatrix), ref(sMatrix), ref(sBorderMatrix), ref(sKernel), ref(sFinal), start, end);
            start = end;
            end = start + cat;
        }

        for (int i = 0; i < p; i++) {
            thr[i].join();
        }
    }
    else {
        for (int i = 0; i < sLinearMatrix.numberOfElements; i++) {
            int cord_i = i / N;
            int cord_j = i % N;

            getNeighMatrix(sBorderMatrix, cord_i, cord_j, m, n, sNeighMatrix);
            double value = calculate(cord_i, cord_j, sNeighMatrix, sKernel);
            sFinal.linearMatrix[i] = value;
        }
    }

    unlinearizationVector(sFinal, M, N, sMatrix);
    //writeMatrixToFile("C:\\ppd\\lab1\\finalMatrixD.txt");
    dealocateDynamicArrays();

    auto endTime = chrono::high_resolution_clock::now();
    cout << chrono::duration<double, milli>(endTime - startTime).count() << "\n";
    return 0;
}