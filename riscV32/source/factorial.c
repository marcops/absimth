#include "../library/stdio.hpp"
#include "../library/math.hpp"

int main()
{
    int i, f = 1, num = 8;

    for (i = 1; i <= num; i++)
        f = f * i;

    print_str("Factorial of 8 is: ");
    print_int(f);
    return 0;
}