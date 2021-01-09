# a minimal example to get RISC-V bare metal assembler running on spike
sum:
	../compiler/bin/riscv32-unknown-elf-gcc  -mno-riscv-attribute -nostartfiles -nostdlib -march=rv32i -mabi=ilp32 -fno-ident  -T linker.ld  sum.c -o sum.obj
	#../compiler/bin/riscv32-unknown-elf-gcc -S -mno-riscv-attribute -nostartfiles -nostdlib -march=rv32i -mabi=ilp32 -fno-ident  -T linker.ld  sum.c 
	../compiler/bin/riscv32-unknown-elf-objcopy sum.obj --dump-section .text=sum.binfull
	head -c -26 sum.binfull > sum.bin
	rm  sum.obj sum.binfull


#loop:
	#~/riscv/bin/riscv32-unknown-elf-gcc -nostartfiles -nostdlib -march=rv32i -mabi=ilp32 -T linker.ld  loop.c -o loop.o
#	~/riscv/bin/riscv32-unknown-elf-objcopy loop.o -S --dump-section .text=loop.bin
