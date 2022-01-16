#include "../library/stdio.hpp"
#include "../library/math.hpp"

// function to get matrix elements entered by the user
void getMatrixElements(int matrix[][10], int row, int column)
{
    for (int i = 0; i < row; ++i)
    {
        for (int j = 0; j < column; ++j)
        {
            matrix[i][j] = srand();
        }
    }
}

// function to multiply two matrices
void multiplyMatrices(int first[][10],
                      int second[][10],
                      int result[][10],
                      int r1, int c1, int r2, int c2)
{

    // Initializing elements of matrix mult to 0.
    for (int i = 0; i < r1; ++i)
    {
        for (int j = 0; j < c2; ++j)
        {
            result[i][j] = 0;
        }
    }

    // Multiplying first and second matrices and storing it in result
    for (int i = 0; i < r1; ++i)
    {
        for (int j = 0; j < c2; ++j)
        {
            for (int k = 0; k < c1; ++k)
            {
                result[i][j] += first[i][k] * second[k][j];
            }
        }
    }
}

// function to display the matrix
void display(int result[][10], int row, int column)
{
    for (int i = 0; i < row; ++i)
    {
        for (int j = 0; j < column; ++j)
        {
            print_int(result[i][j]);

            if (j == column - 1)
                print_str("\r\n");
            else
                print_str(", ");
        }
    }
}

int main()
{
    int first[10][10], second[10][10], result[10][10], r1 = 2, c1 = 3, r2 = 3, c2 = 2;

    // Taking input until
    // 1st matrix columns is not equal to 2nd matrix row
    if (c1 != r2)
    {
        print_str("Error! Enter rows and columns again.\n");
        return 0;
    }

    // get elements of the first matrix
    getMatrixElements(first, r1, c1);

    // get elements of the second matrix
    getMatrixElements(second, r2, c2);

    // multiply two matrices.
    multiplyMatrices(first, second, result, r1, c1, r2, c2);

    print_str("\nFirst Matrix:\n");
    display(first, r1, c1);

    print_str("\nSecond Matrix:\n");
    display(second, r2, c2);

    // display the result
    print_str("\nOutput Matrix:\n");
    display(result, r1, c2);

    return 0;
}