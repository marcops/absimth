#include "../library/stdio.hpp"
#include "../library/math.hpp"

void hanoi(int len, char origem, char dest, char aux)
{
    if (len == 1)
    {
        print_str("\nMoving disc 1 from ");
        putchar(origem);
        print_str(" to ");
        putchar(dest);
        return;
    }
    hanoi(len - 1, origem, aux, dest);
    print_str("\nMoving disc ");
    print_int(len);
    print_str(" from ");
    putchar(origem);
    print_str(" to ");
    putchar(dest);
    hanoi(len - 1, aux, dest, origem);
}

int main()
{
    int len = 5;
    print_str("5 discs - 3 tower");
    hanoi(len, 'A', 'C', 'B');
    return 0;
}