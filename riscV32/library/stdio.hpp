#ifndef _ABSIMTH_STDIO_HPP_
#define _ABSIMTH_STDIO_HPP_

void putchar(int pc) {
    asm("addi x10, x0, 3");
    asm("lw x11, %0" : : "g"(pc));
    asm("ecall");
}

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
