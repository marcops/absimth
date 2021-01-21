#ifndef _STDIO_HPP_
#define _STDIO_HPP_

void print(char *str) {
    asm("addi x10, x0, 4");
    asm("lw x11, %0" : : "g"(str));
    asm("ecall");
}

int mult(int a, int b) {
    int r = 0;
    for(int i=0;i<b;i++) r +=a;
    return r;
}
//implentation of '/' division
int div(int a, int b) {
    int r = a;
	int d = 0;
    while(r>=b) {
		r -= b;
		d++;
	}
    return d;
}

//implentation of '%' division
int mod(int a, int b) {
    return a - mult(b ,div(a,b));
}

static char *itoa_helper(char *dest, int i) {
    if (i <= -10) dest = itoa_helper(dest, div(i , 10));
    *dest++ = '0' - mod(i , 10);
    return dest;
}

char *itoa(char *dest, int i) {
    char *s = dest;
    if (i < 0) *s++ = '-';
    else i = -i;
    *itoa_helper(s, i) = '\0';
    return dest;
}

#endif
