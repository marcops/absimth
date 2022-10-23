#include "../library/stdio.hpp"

int main(void) {
	int randomLocationWithFail = 262134;
	int *zeroAddressPointer = (int *)0x0;

	int readFailAddress = 0;
	int len = 50000;
	int array[len];
	
	for(int i = 0; i < len; i++) array[i] = i;
	
	*(zeroAddressPointer + randomLocationWithFail) = 200;
	
	for(int i = 0; i < len; i++) array[i] =  i;

	*(zeroAddressPointer + randomLocationWithFail) = 201;

	for(int i = 0; i < len; i++) array[i] =  i;
	
	return 0;
}
