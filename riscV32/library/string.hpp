#ifndef _ABSIMTH_STRING_HPP_
#define _ABSIMTH_STRING_HPP_

#include "stddef.hpp"



void *memset(void *ptr, int value, unsigned int len)
{
    unsigned char *dst = ptr;
    while (len > 0)
    {
        *dst = (unsigned char)value;
        dst++;
        len--;
    }
    return ptr;
}


void * memcpy(void* dst, const void* src, unsigned int len)
{
    char *pszDest = (char *)dst;
    const char *pszSource =( const char*)src;
    if((pszDest!= NULL) && (pszSource!= NULL))
    {
        while(len) //till cnt
        {
            //Copy byte by byte
            *(pszDest++)= *(pszSource++);
            --len;
        }
    }
    return dst;
}

int strlen(const char *src) {
    int count = 0;
    while(src[count]!=NULL) count++;
    return count;
}

char* strcpy(char* dest, const char* src) {
    return memcpy(dest, src, strlen(src));
}

#endif
