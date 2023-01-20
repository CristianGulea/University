
#include "cuda_runtime.h"
#include "device_launch_parameters.h"

#include <iostream>
#include <string.h>
#include <math.h>

#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/opencv.hpp>

using namespace std;
using namespace cv;

void loadData(string& imagePath, uchar3* h_image, uchar3* d_image, uchar3* d_filterImage);

int rows, cols;
Mat rgbImage;

// Kernel function for red filter
__global__
void rgbToRedCudaKernel(uchar3* const rgbImage, uchar3* const redImage, int rows, int cols)
{
    const long pointIndex = threadIdx.x + blockIdx.x * blockDim.x;

    if (pointIndex < rows * cols) { // this is necessary only if too many threads are started
        uchar3 const imagePoint = rgbImage[pointIndex];
        redImage[pointIndex].x = (imagePoint.x- imagePoint.y) + (imagePoint.x- imagePoint.z);
        redImage[pointIndex].y = 0;
        redImage[pointIndex].z = 0;
    }
}

int main(int argc, char** argv)
{
    string imagePath = "4.jpg";
    string imageOutputPath = "4output.jpg";
    uchar3* h_image;
    uchar3* d_image;
    uchar3* d_filterImage;
    uchar3* h_filterImage;

    Mat inputImage;
    inputImage = imread(imagePath, IMREAD_COLOR);
    if (inputImage.empty()) 
    {
        cout << "Could not open or find the image" << endl;
        system("pause"); 
        exit(-1);
    }
    //convert BGR TO RGB
    cvtColor(inputImage, rgbImage, COLOR_BGR2RGB);
    cols = inputImage.cols;
    rows = inputImage.rows;
    int pixels = rows * cols;
    cout << cols << " " << rows << "\n";
    cout << pixels<<"\n";
    //copy rgb host image to device
    //alocate memory first
    h_image =(uchar3*) rgbImage.ptr<uchar3>(0);
    cudaMalloc((void**)&(d_image), sizeof(uchar3) * pixels);
    cudaMalloc((void**)&(d_filterImage), sizeof(uchar3) * pixels);
    cudaMemset(d_filterImage, 0, sizeof(uchar3) * pixels);

    cudaMemcpy(d_image, h_image, sizeof(uchar3) * pixels, cudaMemcpyHostToDevice);

    const int blockThreadSize = 512;
    const int numberOfBlocks = 1 + ((rows * cols - 1) / blockThreadSize); // a/b rounded up
    const dim3 blockSize(blockThreadSize, 1, 1);
    const dim3 gridSize(numberOfBlocks, 1, 1);
    rgbToRedCudaKernel << <gridSize, blockSize  >> > (d_image, d_filterImage, rows,cols);
    cudaDeviceSynchronize();

    size_t numPixels = rows * cols;
    h_filterImage =(uchar3*) malloc(sizeof(uchar3) * pixels);
    cudaMemcpy(h_filterImage, d_filterImage, sizeof(uchar3) * numPixels, cudaMemcpyDeviceToHost);

 
    Mat outputrgb(rows, cols, CV_8UC3,h_filterImage,0);
    Mat outputbgr(rows, cols, CV_8UC3);
    cvtColor(outputrgb, outputbgr, COLOR_RGB2BGR);
    cv::imshow("", outputbgr);
    waitKey(0);

    //output the image
    cv::imwrite(imageOutputPath, outputbgr);

    return 0;
}


