#include "../library/stdio.h"

void main(void) {
	int *ecc_address_content_last_adress_failed = (int *)0x1;
	int *initialAddress = (int *)0x10000;
	for(int i = 0; i < 260000; i++) {
		//just wast of time
	  *(initialAddress+i) = i;

		if((*ecc_address_content_last_adress_failed) !=0){
			print("fail_founded");
			int readRottenAddress = *ecc_address_content_last_adress_failed;
			return;
		}
	}

}
