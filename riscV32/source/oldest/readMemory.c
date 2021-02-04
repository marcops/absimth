#include "../library/stdio.hpp"

int readRegister(){
	int ret;
    asm("mv %0, x3" : "=r"(ret) : );
	return ret;
}

int main(void) {
	int a = readRegister();
	char str[6];
	itoa(str, a);
	print(str);
}
