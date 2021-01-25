// This is our minimal startup code (usually in _start)
asm("li sp, 0x100000"); // SP set to 1 MB
asm("jal main");        // call main
asm("mv a1, x10");      // save return value in a1
asm("li a0, 10");       // prepare ecall exit
asm("ecall");           // now your simlator should stop

