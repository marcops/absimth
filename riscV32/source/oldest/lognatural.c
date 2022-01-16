#include "../library/absimth.hpp"

int msb( int v)
{
    int r = 0;
    while (v >>= 1) {
        r++;
    }
    return r;
}

//https://stackoverflow.com/questions/58570282/issue-with-implementation-of-natural-logarithm-ln-and-exponentiation
/*
log2: 13
divisor: 8192.000000
x: 1.527100
ln(12510): 9.434252
*/
float ln(float y)
{
    int log2;
    float divisor, x, result;
    
    print_float(y);
    log2 = msb((int)y); // See: https://stackoverflow.com/a/4970859/6630230
    print_int(log2);
    //printf("log2: %d\n", log2);
    divisor = (float)(1 << log2);
    print_float(divisor);
    
    //printf("divisor: %f\n", divisor);
    x = y / divisor; // normalized value between [1.0, 2.0]
    print_float(x);
    result = -1.7417939F + (2.8212026F + (-1.4699568F + (0.44717955F - 0.056570851F * x) * x) * x) * x;
    result += ((float)log2) * 0.69314718F; // ln(2) = 0.69314718
    return result;
}

int main(int argc, char *argv[])
{
    //ln(12510);  9.434252
    print_float(ln(12510));
    return 0;
}
