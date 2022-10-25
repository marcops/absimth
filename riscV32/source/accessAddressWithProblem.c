#include "../library/stdio.hpp"

int main(void) {
	int randomLocationWithFail = 262134;
	int *zeroAddressPointer = (int *)0x0;

	int len = 100000;
	int array[len];
	int load = 0;
	
	for(int i = 0; i < len; i++) array[i] = i;
	
	for(int i = 0; i < len; i++) load = array[i];
	for(int i = 0; i < len; i++) load = array[i];

	return 0;
}
