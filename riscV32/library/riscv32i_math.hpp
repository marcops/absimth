#ifndef _RISCV32I_MATH_HPP_
#define _RISCV32I_MATH_HPP_

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

#endif
