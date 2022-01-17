/*
https://www.geeksforgeeks.org/ml-frequent-pattern-growth-algorithm/
*/

#include "../library/stdio.hpp"
#include "../library/stddef.hpp"
#include "../library/string.hpp"
#include "../library/stdbool.hpp"

#define ORIGINAL_TRANSACTION_LEN 5
#define MAX_FREQUENCY_LEN 12
#define MAX_ITEMS_LEN 10
#define MAX_NODES_LEN 10

/*typedef struct frequency_pattern_t {
    struct frequency_len conditional_frequency;
    char item;
} * conditional_pattern;
struct conditional_pattern_t conditionalPattern[MAX_NODES_LEN] = {{0}};*/

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

typedef struct conditional_pattern_t
{
  struct frequency_len_t conditional_frequency;
  char conditional_pattern[ORIGINAL_TRANSACTION_LEN][MAX_ITEMS_LEN];
  int frequency[ORIGINAL_TRANSACTION_LEN];
  int len;
  char item;
} conditional_pattern;

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

int getOrderedItem(char **original_transaction, frequency_len *freqPtr, char ordered_item[ORIGINAL_TRANSACTION_LEN][MAX_ITEMS_LEN])
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
  return ORIGINAL_TRANSACTION_LEN;
}

void getConditionalString(char item, char *ordered_item, char *tmp)
{
  for (int i = 0; ordered_item[i] != NULL; i++)
  {
    if (ordered_item[i] == item)
      break;
    tmp[i] = ordered_item[i];
  }
}

int hasConditionalPattern(conditional_pattern *conditionalPattern, char *item)
{
  for (int i = 0; i < conditionalPattern->len; i++)
  {
    if (memcmp(conditionalPattern->conditional_pattern[i], item, strlen(conditionalPattern->conditional_pattern[i])) == 0)
      return i;
  }
  return -1;
}

void getConditionalPatternFromItem(char item, conditional_pattern *conditionalPattern, char ordered_item[ORIGINAL_TRANSACTION_LEN][MAX_ITEMS_LEN], int len)
{
  conditionalPattern->len = 0;
  for (int i = 0; i < len; i++)
  {
    if (*strchr(ordered_item[i], item) != NULL)
    {
      char tmp[MAX_ITEMS_LEN];
      memset(tmp, 0, sizeof(tmp));
      getConditionalString(item, ordered_item[i], tmp);
      if (strlen(tmp) == 0)
        continue;
      int pos = hasConditionalPattern(conditionalPattern, tmp);
      if (pos == -1)
      {
        conditionalPattern->frequency[conditionalPattern->len] = 1;
        memcpy(conditionalPattern->conditional_pattern[conditionalPattern->len], tmp, strlen(tmp));
        conditionalPattern->len++;
      }
      else
      {
        conditionalPattern->frequency[pos]++;
      }
    }
  }
}

int discoverConditionalPattern(frequency_len *freqLenMinimun, char ordered_item[ORIGINAL_TRANSACTION_LEN][MAX_ITEMS_LEN], int len, conditional_pattern *conditionalPattern)
{
  for (int i = freqLenMinimun->len - 1, j = 0; i > 0; i--, j++)
  {
    conditionalPattern[j].item = freqLenMinimun->freq[i].item;
    getConditionalPatternFromItem(conditionalPattern[j].item, &conditionalPattern[j], ordered_item, len);
  }
  return freqLenMinimun->len - 1;
}

int sumConditionalFrequency(conditional_pattern *conditionalPattern)
{
  int val = 0;
  for (int j = 0; j < conditionalPattern->len; j++)
  {
    val += conditionalPattern->frequency[j];
  }
  return val;
}

void getConditionFrequency(conditional_pattern *conditionalPattern, char *refOut)
{
  char *ref = conditionalPattern->conditional_pattern[0];
  int zLen = strlen(ref);
  int val = 0;
  int tAdd = 0;

  for (int z = 0; z < zLen; z++)
  {
    char add = ref[z];

    for (int j = 0; j < conditionalPattern->len; j++)
    {
      if (*strchr(conditionalPattern->conditional_pattern[j], ref[z]) == NULL)
      {
        add = 0;
        break;
      }
    }
    if (add != 0)
    {
      refOut[tAdd] = add;
      tAdd++;
    }
  }
}

void discoverConditionalFrequency(conditional_pattern *conditionalPattern, int len)
{
  if (len == 0)
    return;
  for (int i = 0; i < len; i++)
  {
    int val = sumConditionalFrequency(&conditionalPattern[i]);
    char res[MAX_ITEMS_LEN];
    memset(&res, 0, sizeof(res));
    getConditionFrequency(&conditionalPattern[i], res);
    int l = strlen(res);
    for (int j = 0; j < l; j++)
    {
      int flen = conditionalPattern[i].conditional_frequency.len;
      conditionalPattern[i].conditional_frequency.freq[flen].item = res[j];
      conditionalPattern[i].conditional_frequency.freq[flen].frequency = val;
      conditionalPattern[i].conditional_frequency.len++;
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
  int len = getOrderedItem(original_transaction, &freqLenMinimun, ordered_item);

  print_str("Ordered-Item Set\r\n");
  for (int i = 0; i < len; i++)
  {
    print_str(ordered_item[i]);
    print_str("\n");
  }
  print_str("Conditional Pattern\n");
  struct conditional_pattern_t conditionalPattern[MAX_NODES_LEN] = {{0}};
  len = discoverConditionalPattern(&freqLenMinimun, ordered_item, len, conditionalPattern);
  discoverConditionalFrequency(conditionalPattern, len);

  for (int i = 0; i < len; i++)
  {
    putchar(conditionalPattern[i].item);
    print_str(" - ");
    for (int j = 0; j < conditionalPattern[i].len; j++)
    {
      print_str(conditionalPattern[i].conditional_pattern[j]);
      print_int(conditionalPattern[i].frequency[j]);
      print_str(",");
    }
    print_str(" - ");
    for (int j = 0; j < conditionalPattern[i].conditional_frequency.len; j++)
    {
      putchar(conditionalPattern[i].conditional_frequency.freq[j].item);
      print_int(conditionalPattern[i].conditional_frequency.freq[j].frequency);
      print_str(",");
    }
    print_str("\n");
  }
  print_str("Frequent Pattern\n");

  for (int i = 0; i < len; i++)
  {
    putchar(conditionalPattern[i].item);
    print_str(" - ");
    for (int z = 1;; z++)
    {
      if(z>conditionalPattern[i].conditional_frequency.len) break;
      for (int j = 0; j < conditionalPattern[i].conditional_frequency.len; j += z)
      {

        for (int y = 0; y < z; y++)
          putchar(conditionalPattern[i].conditional_frequency.freq[j+y].item);
        putchar(conditionalPattern[i].item);
        print_int(conditionalPattern[i].conditional_frequency.freq[j].frequency);
        print_str(",");
      }
    }
    print_str("\n");
  }
}
