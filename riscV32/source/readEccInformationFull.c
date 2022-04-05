#include "../library/stdio.hpp"


int main(void) {
	int locationWithFail = 262134;
	int *zeroAddress = (int *)0x0;

	int read_failed_address = 0;
	int len = 50000;
	int arr[len];
	
	for(int i = 0; i < len; i++) arr[i] = i;
	
	read_failed_address = *(zeroAddress + locationWithFail);
	
	for(int i = 0; i < len; i++) arr[i] = read_failed_address;

	return 0;
}
