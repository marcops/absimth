#include "../library/absimth.hpp"

int main(int argc, char *argv[])
{
    int i, numberOfTerms = 30;

    // initialize first and second terms
    int t1 = 0, t2 = 1;

    // initialize the next term (3rd term)
    int nextTerm = t1 + t2;

    // print the first two terms t1 and t2
    printf_string("Fibonacci Series: ");
    print_int(t1);
    printf_string(",");
    print_int(t2);

    // print 3rd to nth terms
    for (i = 3; i <= numberOfTerms; ++i)
    {
        printf_string(",");
        print_int(nextTerm);

        t1 = t2;
        t2 = nextTerm;
        nextTerm = t1 + t2;
    }

    return 0;
}
