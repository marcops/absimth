int sum(int arr[], int len)
{
	int _sum = 0;
	for(int i = 0; i < len; i++)
		_sum += arr[i];
	return _sum;
}

void main(void) 
{
	int len = 40000;
	int arr[len];
	for(int i = 0; i < len; i++)
		arr[i] = i;
	int _sum = sum(arr, len);
}
