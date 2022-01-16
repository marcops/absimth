#include "../library/stdio.hpp"
#include "../library/math.hpp"

int main()
{
    for (int i = 0; i < 10; i++)
    {
        print_int(srand());
        print_str("\r\n");
    }
}
