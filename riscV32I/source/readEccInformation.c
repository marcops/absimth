#include "../library/absimth.hpp"

int main(void) {
	int *initialAddress = (int*)0x4;
	int globalAppInitialAddress = read_initial_address();
	int ecc_address_content_last_adress_failed = *(initialAddress - globalAppInitialAddress);
	print_int(ecc_address_content_last_adress_failed);


	// for (int i = 0; i < 260000; i++)
	// {
	// 	//just wast of time
	// 	*(initialAddress + i) = i;

	// 	if (*ecc_address_content_last_adress_failed != 0)
	// 	{
	// 		print("fail_founded\r\n");
	// 		char str[20];
	// 		int readRottenAddress = *ecc_address_content_last_adress_failed;
	// 		itoa(str, readRottenAddress);
	// 		print(str);
	// 		print("\r\n");
	// 		return readRottenAddress;
	// 	}
	// }
	return 0;
}
