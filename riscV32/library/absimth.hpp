#ifndef _ABSIMTH_HPP_
#define _ABSIMTH_HPP_
#include "riscv32i_math.hpp"

void print_str(char *str) {
    asm("addi x10, x0, 4");
    asm("lw x11, %0" : : "g"(str));
    asm("ecall");
}

void print_double(double i, int precision) {
    int   prec=1;
    int retInt = 5;
    int retFrac = 32;

    for(int x=precision;x>0;x--) 
    {
        prec = mult(prec,10);
    }; 

    //retInt = i;              // get integer part
    //retFrac =  mult(i-retInt,prec);  // get decimal part*/
  
    asm("addi x10, x0, 2");
    asm("lw x11, %0" : : "g"(retInt));
    asm("lw x12, %0" : : "g"(retFrac));
    asm("ecall");
}

void print_int(int i) {
    asm("addi x10, x0, 1");
    asm("lw x11, %0" : : "g"(i));
    asm("ecall");
}

int read_initial_address(){
	int ret;
    asm("mv %0, x3" : "=r"(ret) : );
	return ret;
}


#endif
