//#include "../library/stdio.hpp"
int main(void) {
	 register int *foo asm ("a3");
	int *add = (int*) (5*4);
//	char str[6];
//	itoa(str, 1 + (*add));
//	print(str);
	for (int i = 0; i < 50; i++)
		*(add- (*foo)) = i;
}
