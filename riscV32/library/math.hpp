#ifndef _ABSIMTH_MATH_HPP_
#define _ABSIMTH_MATH_HPP_

int __muldf3(float a, float b) {
	int negative = 0;
	if((a>=0&&b<0) || (a<0&&b>=0)) negative = 1;
	if(a<0) a=-a;
	if(b<0) b=-b;

    int r = 0;
    for(int i=0;i<b;i++) r +=a;
    if(negative) r=-r;
    return r;
}

int __subdf3(float a, float b) {
	return a-b;
}

int __adddf3(float a, float b) {
	return a+b;
}

// int __divdf3(float a, float b) {
// 	return a/b;
// }


float native_sqrtf(float x) {
	asm ("fsqrt.s %0, %1" : "=f" (x) : "f" (x));
	return x;
}

#define M_EULER 2.718281828459f
#define M_PI 3.141592653589f


int msb(unsigned int v)
{
    unsigned int r = 0;
    while (v >>= 1)
        r++;
    return r;
}

float log(float y)
{
    int log2;
    float divisor, x, result;
    
    // print_float(y);
    log2 = msb((int)y); // See: https://stackoverflow.com/a/4970859/6630230
    // print_int(log2);
    //printf("log2: %d\n", log2);
    divisor = (float)(1 << log2);
    // print_float(divisor);
    
    //printf("divisor: %f\n", divisor);
    x = y / divisor; // normalized value between [1.0, 2.0]
    // print_float(x);
    result = -1.7417939F + (2.8212026F + (-1.4699568F + (0.44717955F - 0.056570851F * x) * x) * x) * x;
    result += ((float)log2) * 0.69314718F; // ln(2) = 0.69314718
    return result;
}

float exp(float x) {
	// assert(x <= 0.0f);
	x = -x;
	float r = 1.0f, c = 1.0f; 
	for (int i = 1; i < 8; ++i) {
		c *= x / i;
		r += c;
	}
	return 1.0f / r;
}

float pow(float base, int exp) {
    int i;
	float result = 1.0f;
    for (i = 0; i < exp; i++)
        result *= base;
    return result;
 }

// float exp(float exp) {
//     float euler = 2.718281828459f;
//     return pow(euler, exp);
//  }

int mult(int a, int b) {
	int negative = 0;
	if((a>=0&&b<0) || (a<0&&b>=0)) negative = 1;
	if(a<0) a=-a;
	if(b<0) b=-b;

    int r = 0;
    for(int i=0;i<b;i++) r +=a;
    if(negative) r=-r;
    return r;
}

//implentation of '/' division
int div(int a, int b) {
	int d = 0;
	int negative = 0;
	if((a>=0&&b<0) || (a<0&&b>=0)) negative = 1;
	if(a<0) a=-a;
	if(b<0) b=-b;

	int r = a;
    while(r>=b) {
		r -= b;
		d++;
	}
    if(negative) d=-d;
    return d;
}

//implentation of '%' division
int mod(int a, int b) {
    return a - mult(b ,div(a,b));
}

static unsigned int srand() {
static unsigned int x = 123456789, y = 234567891, z = 345678912, w = 456789123, c = 0;
/* Implementation of a 32-bit KISS generator which uses no multiply instructions */
    y ^= (y << 5);
    y ^= (y >> 7);
    y ^= (y << 22);
    int t = z + w + c;
    z = w;
    c = t < 0;
    w = t & 2147483647;
    x += 1411392427;
    return x + y + w;
}

#endif
