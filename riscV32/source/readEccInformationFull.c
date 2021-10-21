#include "../library/absimth.hpp"

int main(void) {
	int *locationEcc = (int *)0x4;
	int *zeroAddress = (int *)0x0;

	int globalAppInitialAddress = read_initial_address();
	int ecc_address_content_last_adress_failed = *(locationEcc - globalAppInitialAddress);

	int read_failed_address = 0;

	int len = 5000;
	int arr[len];
	
	for(int i = 0; i < len; i++)
		arr[i] = i;
	
	read_failed_address = *(zeroAddress - globalAppInitialAddress + ecc_address_content_last_adress_failed);

	for(int i = 0; i < len; i++)
		arr[i] = *(zeroAddress - globalAppInitialAddress + ecc_address_content_last_adress_failed + i);

	return 0;
}
