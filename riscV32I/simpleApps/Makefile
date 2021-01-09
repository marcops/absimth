# a minimal example to get RISC-V bare metal assembler running on spike
sum:
	bin/riscv32-unknown-elf-gcc  -mno-riscv-attribute -nostartfiles -nostdlib -march=rv32i -mabi=ilp32 -fno-ident  -T linker.ld  sum.c -o sum.o
	bin/riscv32-unknown-elf-gcc -S -mno-riscv-attribute -nostartfiles -nostdlib -march=rv32i -mabi=ilp32 -fno-ident  -T linker.ld  sum.c 
	~/riscv/bin/riscv32-unknown-elf-objcopy sum.o --dump-section .text=o.bin
	head -c -26 o.bin > sum.bin


#loop:
	#~/riscv/bin/riscv32-unknown-elf-gcc -nostartfiles -nostdlib -march=rv32i -mabi=ilp32 -T linker.ld  loop.c -o loop.o
#	~/riscv/bin/riscv32-unknown-elf-objcopy loop.o -S --dump-section .text=loop.bin
