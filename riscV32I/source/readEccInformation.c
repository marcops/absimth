#include "../library/absimth.hpp"

int main(void) {
	 int *locationEcc = (int*)0x4;
	 int *zeroAddress = (int*)0x0;

	int globalAppInitialAddress = read_initial_address();
	int ecc_address_content_last_adress_failed = *(locationEcc - globalAppInitialAddress);
	print_int(ecc_address_content_last_adress_failed);

	 int read_failed_address = *(zeroAddress - globalAppInitialAddress + ecc_address_content_last_adress_failed);
	 print_int(read_failed_address);
	
	return 0;
}
