# a minimal example to get RISC-V 32I bare metal assembler running on spike
CC = compiler/bin/riscv32-unknown-elf-gcc
SRC_DIR = source/
CFLAGS = -mno-riscv-attribute -nostartfiles -nostdlib -fno-ident -mabi=ilp32 -T $(BOOT_DIR)linker.ld -Wno-builtin-declaration-mismatch -Wno-implicit-function-declaration
BUILD_DIR = build/
BOOT_DIR = bootstrap/

OC = compiler/bin/riscv32-unknown-elf-objcopy
OCFLAGS = --dump-section .text=
PNAME = $*
POUTPUT = $(BUILD_DIR)$(PNAME)

CRV32M=RV32M

ifeq ($(COMPILER),$(CRV32M))
	CFLAGS += -march=rv32imf 
else
	CFLAGS += -march=rv32i 
endif

all: 
	@echo "Specify one program name to build"
	@echo ""
	@echo "Example:"
	@echo "\tmake sum"
	@echo ""
	@echo "or"
	@echo "\tmake mult COMPILER=RV32M"

%:
	cat $(BOOT_DIR)bootstrap.asm $(SRC_DIR)$(PNAME).c > $(POUTPUT).c
	$(CC) -S $(CFLAGS) $(POUTPUT).c -o $(POUTPUT).s
	$(CC) $(CFLAGS) $(POUTPUT).c -o $(POUTPUT).o
	$(OC) $(POUTPUT).o $(OCFLAGS)$(POUTPUT).bo
	head -c -24 $(POUTPUT).bo > $(POUTPUT).bin
	rm  $(POUTPUT).o $(POUTPUT).bo $(POUTPUT).c

clean:
	rm  -rf $(BUILD_DIR)*
