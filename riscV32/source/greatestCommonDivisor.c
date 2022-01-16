#include "../library/stdio.hpp"
#include "../library/math.hpp"

int gcd(int a, int b) {
    while (a != b)
        if (a > b)
            a = a - b;
        else
            b = b - a;
    return a;
}
int main()
{
    print_str("GCD of 1950 - 1500 is ");
    int a = 1950;
    int b = 1500;

    print_int(gcd(a,b));
}
