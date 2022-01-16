/*
https://www.geeksforgeeks.org/ml-frequent-pattern-growth-algorithm/
*/

#include "../library/stdio.hpp"
#include "../library/stddef.hpp"
#include "../library/string.hpp"

#define ORIGINAL_TRANSACTION_LEN 5
#define MAX_FREQUENCY_LEN 12
#define MAX_ITEMS_LEN 10

typedef struct frequency_t
{
  char item;
  int frequency;
} frequency;

typedef struct frequency_len_t
{
  struct frequency_t freq[MAX_FREQUENCY_LEN];
  int len;
} frequency_len;

void addToFrequency(char c, frequency_len *freqPtr)
{
  int position = -1;
  for (int i = 0; i < freqPtr->len; i++)
  {
    if (freqPtr->freq[i].item == c)
    {
      position = i;
      break;
    }
  }
  if (position == -1)
  {
    position = freqPtr->len;
    freqPtr->len++;
  }
  freqPtr->freq[position].frequency++;
  freqPtr->freq[position].item = c;
}

frequency_len *discoverFrequency(char **transaction, int len, frequency_len *freqPtr)
{
  for (int i = 0; i < len; i++)
  {
    int tlen = strlen(transaction[i]);
    for (int j = 0; j < tlen; j++)
    {
      addToFrequency(transaction[i][j], freqPtr);
    }
  }
}

frequency_len *mininumFrequency(frequency_len *freqInPtr, int minimun, frequency_len *freqOutPtr)
{
  for (int i = 0; i < freqInPtr->len; i++)
  {
    if (freqInPtr->freq[i].frequency >= minimun)
    {
      freqOutPtr->freq[freqOutPtr->len].frequency = freqInPtr->freq[i].frequency;
      freqOutPtr->freq[freqOutPtr->len].item = freqInPtr->freq[i].item;
      freqOutPtr->len++;
    }
  }
}

void getOrderedItem(char **original_transaction, frequency_len *freqPtr, char ordered_item[ORIGINAL_TRANSACTION_LEN][MAX_ITEMS_LEN])
{
  for (int i = 0; i < ORIGINAL_TRANSACTION_LEN; i++)
  {
    int z = 0;
    for (int j = 0; j < freqPtr->len; j++)
    {
      char *ptr = strchr(original_transaction[i], freqPtr->freq[j].item);
      if (*ptr != NULL)
      {
        ordered_item[i][z] = *ptr;
        z++;
        if (z >= MAX_ITEMS_LEN)
          break;
      }
    }
  }
}

void main(void)
{
  char *original_transaction[ORIGINAL_TRANSACTION_LEN];
  original_transaction[0] = "EKMNOY\0";
  original_transaction[1] = "DEKNOY\0";
  original_transaction[2] = "AEKM\0";
  original_transaction[3] = "CKMUY\0";
  original_transaction[4] = "CEIKOO\0";

  print_str("Original item\r\n");
  for (int i = 0; i < ORIGINAL_TRANSACTION_LEN; i++)
  {
    print_str(original_transaction[i]);
    print_str("\n");
  }

  struct frequency_len_t freqLen;
  memset(&freqLen, 0, sizeof(freqLen));

  discoverFrequency(original_transaction, ORIGINAL_TRANSACTION_LEN, &freqLen);

  print_str("Frequency of each item\r\n");
  for (int i = 0; i < freqLen.len; i++)
  {
    putchar(freqLen.freq[i].item);
    print_str(" - ");
    print_int(freqLen.freq[i].frequency);
    print_str("\n");
  }

  struct frequency_len_t freqLenMinimun;
  memset(&freqLenMinimun, 0, sizeof(freqLenMinimun));

  mininumFrequency(&freqLen, 3, &freqLenMinimun);

  print_str("with frequency minimun of 3\r\n");
  for (int i = 0; i < freqLenMinimun.len; i++)
  {
    putchar(freqLenMinimun.freq[i].item);
    print_str(" - ");
    print_int(freqLenMinimun.freq[i].frequency);
    print_str("\n");
  }

  char ordered_item[ORIGINAL_TRANSACTION_LEN][MAX_ITEMS_LEN];
  memset(&ordered_item, 0, sizeof(ordered_item));
  getOrderedItem(original_transaction, &freqLenMinimun, ordered_item);

  print_str("Ordered-Item Set\r\n");
  for (int i = 0; i < ORIGINAL_TRANSACTION_LEN; i++)
  {
    print_str(ordered_item[i]);
    print_str("\n");
  }
}
