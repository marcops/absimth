void printString(char *str) {
	asm ("addi x10, x0, 4"); 
	asm ("lw x11, %0": : "g"(str) ); 
	asm ("ecall"); 
}

void main() {
  	printString("MARCO");
}
