package absimth.sim.os.model;

import absimth.module.cpu.riscv32.module.RV32Cpu2Mem;

public class OSRiscvMemoryAccess implements IOSMemoryAccess {
	private RV32Cpu2Mem memory = new RV32Cpu2Mem();
	private Integer initialAddress;
	private Integer stackSize;
	private Boolean manageDynamicMemory;
	private OSProgramModel program;
	
	public OSRiscvMemoryAccess(Integer stackSize2, Integer initialAddress2, OSProgramModel program2) {
		this.initialAddress = initialAddress2;
		this.stackSize = stackSize2;
		this.manageDynamicMemory = false;
		this.program = program2;
	}

	private int getVirtualAddress(int address) {
		int vAdd = initialAddress + (address/4);
		if (vAdd < initialAddress)
			System.out.println("Memory Leak - address require minor then the initial address, vAdd " + vAdd + ", min "+ initialAddress);
		if (vAdd > initialAddress + stackSize)
			System.out.println("Memory Leak - address require major then the max address for this program, vAdd " + vAdd + ", max " + (initialAddress + stackSize));
		if(manageDynamicMemory)
			program.setDynamicAddress(vAdd);
		return vAdd;
	}

	public int getWord(int address) throws Exception {
		return memory.getWord(getVirtualAddress(address));
	}

	@Override
	public void storeWord(int addr, int data) throws Exception {
		memory.storeWord(getVirtualAddress(addr), data);
	}

	@Override
	public int getByte(int addr) throws Exception {
		return memory.getByte(getVirtualAddress(addr));
	}

	@Override
	public int getHalfWord(int addr) throws Exception {
		return memory.getHalfWord(getVirtualAddress(addr));
	}

	@Override
	public String getString(int addr) throws Exception {
		return memory.getString(getVirtualAddress(addr));
	}

	@Override
	public void storeHalfWord(int addr, short s) throws Exception {
		memory.storeHalfWord(getVirtualAddress(addr), addr%4, s);
	}

	@Override
	public void storeByte(int addr, byte b) throws Exception {
		memory.storeByte(getVirtualAddress(addr), addr%4, b);		
	}

	@Override
	public void startManageDynamicMemory() {
		this.manageDynamicMemory = true;
	}
}
