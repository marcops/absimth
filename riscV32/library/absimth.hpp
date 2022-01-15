#ifndef _ABSIMTH_HPP_
#define _ABSIMTH_HPP_
#include "riscv32i_math.hpp"

void print_str(char *str) {
    asm("addi x10, x0, 4");
    asm("lw x11, %0" : : "g"(str));
    asm("ecall");
}

void print_float(float i) {
    asm("addi x10, x0, 2");
    asm("lw x11, %0" : : "g"(i));
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
