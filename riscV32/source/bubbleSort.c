#include "../library/stdio.hpp"
#include "../library/math.hpp"

void bubble_sort(int vetor[], int n)
{
    int k, j, aux;

    for (k = 1; k < n; k++)
    {
        for (j = 0; j < n - 1; j++)
        {
            if (vetor[j] > vetor[j + 1])
            {
                aux = vetor[j];
                vetor[j] = vetor[j + 1];
                vetor[j + 1] = aux;
            }
        }
    }
}

int main(int argc, char **argv)
{
    int len = 10;
    int i, vet[len];

    for (i = 0; i < len; i++)
    {
        vet[i] = srand();
    }

    bubble_sort(vet, len);

    for (i = 0; i < len; i++)
    {
        print_int(vet[i]);
        print_str("\r\n");
    }
    return 0;
}
