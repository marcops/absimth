#include "../library/stdio.hpp"
#include "../library/stdbool.hpp"

bool isPrime(int n)
{
    int flag = 0;
    int m = n / 2;
    for (int i = 2; i <= m; i++)
    {
        if (n % i == 0)
            return false;
    }
    return true;
}
int main()
{
    int i = 1, total = 10, count = 0;
    print_str("Printing first 10 primes numbers\r\n");

    while (count < total)
    {
        if (isPrime(i))
        {
            print_int(i);
            print_str("\r\n");
            count++;
        }
        i++;
    }
    return 0;
}