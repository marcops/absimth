#include "../library/stdio.hpp"

int main(void)
{
	int *ecc_address_content_last_adress_failed = (int *)0x1;
	int *initialAddress = (int *)0x10000;
	for (int i = 0; i < 260000; i++)
	{
		//just wast of time
		*(initialAddress + i) = i;

		if (*ecc_address_content_last_adress_failed != 0)
		{
			print("fail_founded\r\n");
			char str[20];
			int readRottenAddress = *ecc_address_content_last_adress_failed;
			itoa(str, readRottenAddress);
			print(str);
			print("\r\n");
			return readRottenAddress;
		}
	}
	return 0;
}
