#include "../library/stdio.hpp"
#include "../library/math.hpp"

#define MAX 10

void insertion_sort(int *a)
{
    int i, j, tmp;

    for (i = 1; i < MAX; i++)
    {
        tmp = a[i];
        for (j = i - 1; j >= 0 && tmp < a[j]; j--)
        {
            a[j + 1] = a[j];
        }
        a[j + 1] = tmp;
    }
}

int main(int argc, char **argv)
{
    int i, vet[MAX];

    for (i = 0; i < MAX; i++)
    {
        vet[i] = srand();
    }

    insertion_sort(vet);

    for (i = 0; i < MAX; i++)
    {
        print_int(vet[i]);
        print_str("\r\n");
    }
    return 0;
}
