void main(void) {
	int *finalAddress = (int *)0x4fff0;
	int *initialAddress = (int *)0x10000;

	//262143
	for(int i = 0; i < 260000; i++)
	  *(initialAddress+i) = i;

}
