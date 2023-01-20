#include <mpi.h>
#include <stdio.h>
#include <iostream>
#include <fstream>
#include <chrono>

using namespace std;

struct number {
    int numberOfDigits;
    int* digits;
    number(int numberDigits) {
        this->numberOfDigits = numberDigits;
        this->digits = new int[numberDigits] { 0 };
    }
    
};

void print(int vec[], int size) {
    for (int i = 0; i < size; i++) {
        cout << vec[i] << " ";
    }
    cout << endl;
}

void printNumber(number number) {
    cout << endl << "Number of digits: " << number.numberOfDigits << endl;
    for (int i = number.numberOfDigits - 1; i >= 0; i--) {
        cout << number.digits[i] << " ";
    }
    cout << endl;
}

number readNumber(string fileName) {
    ifstream myfile;
    myfile.open(fileName);

    int numberDigits;
    myfile >> numberDigits;

    number number(numberDigits);

    for (int i = 0; i < number.numberOfDigits; i++) {
        myfile >> number.digits[i];
    }

    myfile.close();
    return number;
}

void writeNumber(string filename, number number) {
    fstream myfile;
    myfile.open(filename, fstream::out);

    for (int i = number.numberOfDigits - 1; i >= 0; i--){
      myfile << number.digits[i] << " ";
    }
    
    myfile << "\n";
    myfile.close();
}


//Varianta Secventiala.
int mainSecvential() {

    auto startTime = chrono::high_resolution_clock::now();

    number number1 = readNumber("C:\\ppd\\lab3\\number1.txt");
    number number2 = readNumber("C:\\ppd\\lab3\\number2.txt");

    if (number1.numberOfDigits < number2.numberOfDigits) {
        swap(number1, number2);
    }

    number result = number(number1.numberOfDigits + 1);

    int carry = 0;
    for (int i = 0; i < number1.numberOfDigits; i++) {
        if (i < number2.numberOfDigits) {
            int sum = carry + number1.digits[i] + number2.digits[i];
            result.digits[i] = sum % 10;
            carry = sum / 10;
        }
        else {
            result.digits[i] = (number1.digits[i] + carry) % 10 ;
            carry = (number1.digits[i] + carry) / 10;
        }
    }

    if (carry < 0) carry = 0;
    result.digits[result.numberOfDigits - 1] = carry;

    writeNumber("C:\\ppd\\lab3\\result.txt", result);

    //printNumber(number1);
    //printNumber(number2);
    //printNumber(result);

    delete[] number1.digits;
    delete[] number2.digits;
    delete[] result.digits;

    auto endTime = chrono::high_resolution_clock::now();
    cout << chrono::duration<double, milli>(endTime - startTime).count() / 1000 << "\n";


    return 0;
}


int mainVarianta1A() {
    MPI_Init(NULL, NULL);

    int rank, p;
    MPI_Comm_size(MPI_COMM_WORLD, &p);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    

    string fileNumber1 = "C:\\ppd\\lab3\\number1.txt";
    string fileNumber2 = "C:\\ppd\\lab3\\number2.txt";
    string resultFileName = "C:\\ppd\\lab3\\result2.txt";

    ifstream ifstreamNumber1;
    ifstreamNumber1.open(fileNumber1);

    ifstream ifstreamNumber2;
    ifstreamNumber2.open(fileNumber2);

    int length, numberDigits2;
    ifstreamNumber2 >> numberDigits2;

    int numberDigits1;
    ifstreamNumber1 >> numberDigits1;

    if (numberDigits1 > numberDigits2) {
        length = numberDigits1;
    }
    else {
        length = numberDigits2;
    }


    int* aux_a = new int[length] { 0 };
    int* aux_b = new int[length] { 0 };
    int* aux_c = new int[length] { 0 };

    if (rank == 0) {

        auto startTime = chrono::high_resolution_clock::now();

        MPI_Status status;
        int firstDigit;
        int sum;

        number result(length + 1);

        int size = length / (p - 1);
        int rest = length % (p - 1);

        int start = 0;
        int end = size;

        for (int i = 1; i < p; i++) {
            if (rest > 0) {
                end += 1;
                rest -= 1;
            }

            MPI_Send(&start, 1, MPI_INT, i, 0, MPI_COMM_WORLD);
            MPI_Send(&end, 1, MPI_INT, i, 0, MPI_COMM_WORLD);

            int digit1;
            int digit2;
            for (int aux_i = start; aux_i < end; aux_i++) {
                if (aux_i < numberDigits1) {
                    ifstreamNumber1 >> digit1;
                    aux_a[aux_i] = digit1;
                }
                if (aux_i < numberDigits2) {
                    ifstreamNumber2 >> digit2;
                    aux_b[aux_i] = digit2;
                }
            }

            MPI_Send(aux_a + start, end - start, MPI_INT, i, 0, MPI_COMM_WORLD);
            MPI_Send(aux_b + start, end - start, MPI_INT, i, 0, MPI_COMM_WORLD);

            start = end;
            end = start + size;
        }

        for (int i = 1; i < p; i++) {
            MPI_Recv(&start, 1, MPI_INT, i, 0, MPI_COMM_WORLD, &status);
            MPI_Recv(&end, 1, MPI_INT, i, 0, MPI_COMM_WORLD, &status);
            MPI_Recv(aux_c + start, end - start, MPI_INT, i, 0, MPI_COMM_WORLD, &status);

        }

        MPI_Recv(&firstDigit, 1, MPI_INT, p - 1, 0, MPI_COMM_WORLD, &status);
        result.digits[result.numberOfDigits - 1] = firstDigit;

        for (int aux_j = 0; aux_j < result.numberOfDigits - 1; aux_j++) {
            result.digits[aux_j] = aux_c[aux_j];
        }

        //printNumber(result);
        writeNumber(resultFileName, result);

        delete[] result.digits;

        auto endTime = chrono::high_resolution_clock::now();
        cout << chrono::duration<double, milli>(endTime - startTime).count() / 1000 << "\n";
    }
    else {
        //worker
        int start, end, sumDigits, carry = 0;
        MPI_Status status;
        MPI_Recv(&start, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, &status);
        MPI_Recv(&end, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, &status);

        if (rank > 1) {
            MPI_Recv(&carry, 1, MPI_INT, rank - 1, 0, MPI_COMM_WORLD, &status);
        }

        MPI_Recv(aux_a + start, end - start, MPI_INT, 0, 0, MPI_COMM_WORLD, &status);
        MPI_Recv(aux_b + start, end - start, MPI_INT, 0, 0, MPI_COMM_WORLD, &status);

        for (int aux_i = start; aux_i < end; aux_i++) {
            sumDigits = (aux_a[aux_i] + aux_b[aux_i] + carry) % 10;
            carry = (aux_a[aux_i] + aux_b[aux_i] + carry) / 10;
            aux_c[aux_i] = sumDigits;

        }

        MPI_Send(&start, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
        MPI_Send(&end, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
        MPI_Send(aux_c + start, end - start, MPI_INT, 0, 0, MPI_COMM_WORLD);

        if (rank < p - 1) {
            MPI_Send(&carry, 1, MPI_INT, rank + 1, 0, MPI_COMM_WORLD);
        }
        else {
            MPI_Send(&carry, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
        }
    }

    delete[] aux_a;
    delete[] aux_b;
    delete[] aux_c;

    MPI_Finalize();
    return 0;
}


int mainVarianta1B() {
    MPI_Init(NULL, NULL);

    int rank, p;
    MPI_Comm_size(MPI_COMM_WORLD, &p);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    string fileNumber1 = "C:\\ppd\\lab3\\number1.txt";
    string fileNumber2 = "C:\\ppd\\lab3\\number2.txt";
    string resultFileName = "C:\\ppd\\lab3\\result2.txt";

    ifstream ifstreamNumber1;
    ifstreamNumber1.open(fileNumber1);

    ifstream ifstreamNumber2;
    ifstreamNumber2.open(fileNumber2);

    int length, numberDigits2;
    ifstreamNumber2 >> numberDigits2;

    int numberDigits1;
    ifstreamNumber1 >> numberDigits1;

    if (numberDigits1 > numberDigits2) {
        length = numberDigits1;
    }
    else {
        length = numberDigits2;
    }

    int* aux_a = new int[length] { 0 };
    int* aux_b = new int[length] { 0 };
    int* aux_c = new int[length] { 0 };



    if (rank == 0) {

        auto startTime = chrono::high_resolution_clock::now();

        MPI_Status status;
        int firstDigit;
        int sum;

        number result(length + 1);

        int size = length / (p - 1);
        int rest = length % (p - 1);

        int start = 0;
        int end = size;

        for (int i = 1; i < p; i++) {
            if (rest > 0) {
                end += 1;
                rest -= 1;
            }

            MPI_Send(&start, 1, MPI_INT, i, 0, MPI_COMM_WORLD);
            MPI_Send(&end, 1, MPI_INT, i, 0, MPI_COMM_WORLD);

            int digit1;
            int digit2;
            for (int aux_i = start; aux_i < end; aux_i++) {
                if (aux_i < numberDigits1) {
                    ifstreamNumber1 >> digit1;
                    aux_a[aux_i] = digit1;
                }
                if (aux_i < numberDigits2) {
                    ifstreamNumber2 >> digit2;
                    aux_b[aux_i] = digit2;
                }
            }

            MPI_Send(aux_a + start, end - start, MPI_INT, i, 0, MPI_COMM_WORLD);
            MPI_Send(aux_b + start, end - start, MPI_INT, i, 0, MPI_COMM_WORLD);

            start = end;
            end = start + size;
        }

        for (int i = 1; i < p; i++) {
            MPI_Recv(&start, 1, MPI_INT, i, 0, MPI_COMM_WORLD, &status);
            MPI_Recv(&end, 1, MPI_INT, i, 0, MPI_COMM_WORLD, &status);
            MPI_Recv(aux_c + start, end - start, MPI_INT, i, 0, MPI_COMM_WORLD, &status);

        }

        MPI_Recv(&firstDigit, 1, MPI_INT, p - 1, 0, MPI_COMM_WORLD, &status);
        result.digits[result.numberOfDigits - 1] = firstDigit;

        for (int aux_j = 0; aux_j < result.numberOfDigits - 1; aux_j++) {
            result.digits[aux_j] = aux_c[aux_j];
        }

        writeNumber(resultFileName, result);

        delete[] result.digits;

        auto endTime = chrono::high_resolution_clock::now();
        cout << chrono::duration<double, milli>(endTime - startTime).count() / 1000 << "\n";
    }
    else {
        //worker
        int start, end, sumDigits, carry = 0, savedCarry;
        MPI_Status status;
        MPI_Recv(&start, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, &status);
        MPI_Recv(&end, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, &status);

        MPI_Recv(aux_a + start, end - start, MPI_INT, 0, 0, MPI_COMM_WORLD, &status);
        MPI_Recv(aux_b + start, end - start, MPI_INT, 0, 0, MPI_COMM_WORLD, &status);

        for (int aux_i = start; aux_i < end; aux_i++) {
            sumDigits = (aux_a[aux_i] + aux_b[aux_i] + carry) % 10;
            carry = (aux_a[aux_i] + aux_b[aux_i] + carry) / 10;
            aux_c[aux_i] = sumDigits;
        }

        savedCarry = carry;
        if (rank > 1) {
            MPI_Recv(&carry, 1, MPI_INT, rank - 1, 0, MPI_COMM_WORLD, &status);
            carry += savedCarry;
            if (carry != savedCarry) {
                carry -= savedCarry;
                for (int aux_i = start; aux_i < end; aux_i++) {
                    sumDigits = (aux_a[aux_i] + aux_b[aux_i] + carry) % 10;
                    carry = (aux_a[aux_i] + aux_b[aux_i] + carry) / 10;
                    aux_c[aux_i] = sumDigits;
                }
            }
        }

        MPI_Send(&start, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
        MPI_Send(&end, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
        MPI_Send(aux_c + start, end - start, MPI_INT, 0, 0, MPI_COMM_WORLD);

        if (rank < p - 1) {
            MPI_Send(&carry, 1, MPI_INT, rank + 1, 0, MPI_COMM_WORLD);
        }
        else {
            MPI_Send(&carry, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
        }
    }

    delete[] aux_a;
    delete[] aux_b;
    delete[] aux_c;

    MPI_Finalize();
    return 0;
}


int mainVarianta2() {
    MPI_Init(NULL, NULL);

    int sumDigits = 0; int carry = 0;
    MPI_Status status;

    int rank, p;
    MPI_Comm_size(MPI_COMM_WORLD, &p);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    string fileNumber1 = "C:\\ppd\\lab3\\number1.txt";
    string fileNumber2 = "C:\\ppd\\lab3\\number2.txt";
    string resultFileName = "C:\\ppd\\lab3\\result2.txt";

    ifstream ifstreamNumber1;
    ifstreamNumber1.open(fileNumber1);

    ifstream ifstreamNumber2;
    ifstreamNumber2.open(fileNumber2);

    int numberOfDigitsNumber1;
    int numberOfDigitsNumber2;
    ifstreamNumber1 >> numberOfDigitsNumber1;
    ifstreamNumber2 >> numberOfDigitsNumber2;

    int numberOfDigits = max(numberOfDigitsNumber1, numberOfDigitsNumber2);

    int* a = new int[numberOfDigits] { 0 };
    int* b = new int[numberOfDigits] { 0 };
    int* c = new int[numberOfDigits + 1] { 0 };
    
    int* displacements = new int[p];
    int* buffer_sizes = new int[p];



    int size = numberOfDigits / p;
    int rest = numberOfDigits % p;

    int max_offset = size;
    if (rest > 0) {
        max_offset++;
    }


    int start = 0;
    int end = size;

    for (int i = 0; i < p; i++) {
        if (rest > 0) {
            end += 1;
            rest -= 1;
        }

        buffer_sizes[i] = end - start;
        displacements[i] = start;

        start = end;
        end = start + size;

    }

    int* aux_a = new int[max_offset];
    int* aux_b = new int[max_offset];
    int* aux_c = new int[max_offset];

    if (rank == 0) {
        number number1 = readNumber(fileNumber1);
        number number2 = readNumber(fileNumber2);

        for (int i = 0; i < number1.numberOfDigits; i++) {
            a[i] = number1.digits[i];
        }


        for (int i = 0; i < number2.numberOfDigits; i++) {
            b[i] = number2.digits[i];
        }

        delete[] number1.digits;
        delete[] number2.digits;
    }

    MPI_Scatterv(a, buffer_sizes, displacements, MPI_INT, aux_a, max_offset, MPI_INT, 0, MPI_COMM_WORLD);
    MPI_Scatterv(b, buffer_sizes, displacements, MPI_INT, aux_b, max_offset, MPI_INT, 0, MPI_COMM_WORLD);

    if (rank >= 1) {
        MPI_Recv(&carry, 1, MPI_INT, rank - 1, 0, MPI_COMM_WORLD, &status);
    }


    for (int i = 0; i < buffer_sizes[rank]; i++) {
        sumDigits = aux_a[i] + aux_b[i] + carry;
        aux_c[i] = sumDigits % 10;
        carry = sumDigits / 10;
    }


    if (rank < p - 1) {
        MPI_Send(&carry, 1, MPI_INT, rank + 1, 0, MPI_COMM_WORLD);
    }
    else {
        MPI_Send(&carry, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
    }

    MPI_Gatherv(aux_c, buffer_sizes[rank], MPI_INT, c, buffer_sizes, displacements, MPI_INT, 0, MPI_COMM_WORLD);

    if (rank == 0) {

        auto startTime = chrono::high_resolution_clock::now();

        MPI_Recv(&carry, 1, MPI_INT, p - 1, 0, MPI_COMM_WORLD, &status);
      
        c[numberOfDigits] = carry;


        number result(numberOfDigits + 1);
        for (int i = 0; i < result.numberOfDigits; i++) {
            result.digits[i] = c[i];
        }

        //cout << "a: "; print(a, numberOfDigits);
        //cout << "b: "; print(b, numberOfDigits);
        //cout << "c: "; printNumber(result);

        writeNumber(resultFileName, result);

        delete[] result.digits;
        delete[] aux_a;
        delete[] aux_b;
        delete[] aux_c;
        delete[] displacements;
        delete[] buffer_sizes;
        delete[] a;
        delete[] b;
        delete[] c;

        auto endTime = chrono::high_resolution_clock::now();
        cout << chrono::duration<double, milli>(endTime - startTime).count() / 1000 << "\n";

    }

    
    MPI_Finalize();
    return 0;
}

int main(int argc, char** argv) {
    mainSecvential();
    //mainVarianta1A();
    //mainVarianta1B();
    mainVarianta2();
}