#include "../library/stdio.hpp"

int main(int argc, char *argv[])
{
    int i, numberOfTerms = 30;

    // initialize first and second terms
    int t1 = 0, t2 = 1;

    // initialize the next term (3rd term)
    int nextTerm = t1 + t2;

    // print the first two terms t1 and t2
    print_str("Fibonacci Series: ");
    print_int(t1);
    print_str(",");
    print_int(t2);

    // print 3rd to nth terms
    for (i = 3; i <= numberOfTerms; ++i)
    {
        print_str(",");
        print_int(nextTerm);

        t1 = t2;
        t2 = nextTerm;
        nextTerm = t1 + t2;
    }

    return 0;
}
