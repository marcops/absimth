package absimth.module.cpu.riscv32;

import absimth.module.cpu.riscv32.module.RV32Cpu2Mem;
import absimth.sim.cpu.ICPUInstruction;
import absimth.sim.utils.Bits;


public class RISCV32f extends RISCV32im {
	@Override
	public void executeInstruction() throws Exception {
		prevPc = pc;
		RV32FInstruction inst = new RV32FInstruction();
		inst.loadInstruction(memory.getWord(pc*4));

		if(RV32FInstruction.isFInstruction(inst)) doInstruction(inst);
		else super.executeInstruction();
	}

	private void doInstruction(RV32FInstruction inst) throws Exception {
		switch (inst.opcode) {
		case 0b1010011: // FADD / FSLTI / SLTIU / XORI / ORI / ANDI / SLLI / SRLI / SRAI
			iTypeFloat(inst);
			break;
		case 0b100111://FSW
		{
			int addr = reg[inst.rs1] + inst.imm;
			memory.storeWord(addr, reg[inst.rs2]);
			pc++;
		}
			break;
		case 0b000111: // FLW
		{
			int addr = reg[inst.rs1] + inst.imm; // Byte address
			reg[inst.rd] = memory.getWord(addr);
			pc++;
		}
			break;
		default:
			System.err.println("executeInstruction " + inst.opcode);
			throw new Exception("Wrong instruction " + inst.opcode + " at executeInstruction");
		}
	}
	
	
	/**
	 * Handles execution of I-type integer instructions: FADD / FSLT / FSLTU / FXOR
	 * / FOR / FAND / FSLL / FSRL / FSRA
	 * @throws Exception 
	 */
	private void iTypeFloat(RV32FInstruction inst) throws Exception {
		switch (inst.funct5) {
		case 0b00000: //FADD
			reg[inst.rd] = float2intRepresentation(intRepresentation2float(reg[inst.rs1]) + intRepresentation2float(reg[inst.rs2]));
			break;
		case 0b00001://FSUB
			reg[inst.rd] = float2intRepresentation(intRepresentation2float(reg[inst.rs1]) - intRepresentation2float(reg[inst.rs2]));
			break;	
		case 0b00010://FMUL
			reg[inst.rd] = float2intRepresentation(intRepresentation2float(reg[inst.rs1]) * intRepresentation2float(reg[inst.rs2]));
			break;
		case 0b00011://FDIV
			reg[inst.rd] = float2intRepresentation(intRepresentation2float(reg[inst.rs1]) / intRepresentation2float(reg[inst.rs2]));
			break;
		case 0b11010://FCVT.s.W/FCVT.S.WU
		case 0b11000://FCVT.W/FCVT.WU
			float value = float2intRepresentation(reg[inst.rs1]); 
			reg[inst.rd] = RV32Cpu2Mem.java2int((int)value);
			break;
//		case 0b01011://FSQRT
//			System.err.println("Not Implemented FLOAT INSTRUCTION");
//			break;
//		case 0b00100://FSGNJ/FSGNJN/FSGNJX
//			System.err.println("Not Implemented FLOAT INSTRUCTION");
//			break;
//		case 0b00101://FMIN/FMAX
//			System.err.println("Not Implemented FLOAT INSTRUCTION");
//			break;
//		case 0b11100://FMV.X/FCLASS
//			System.err.println("Not Implemented FLOAT INSTRUCTION");
//			break;
//		case 0b10100://FEQ/FLT/FLE
//			System.err.println("Not Implemented FLOAT INSTRUCTION");
//			break;
//		case 0b11110://FMV.W.X
//			System.err.println("Not Implemented FLOAT INSTRUCTION");
//			break;
		default:
			System.err.println("iTypeFloat:"+Integer.toBinaryString(inst.funct5));
			throw new Exception("Wrong instruction ");
		}
		pc++;
	}

	@Override
	public String getName() {
		return "RISCV32f";
		
	}

	@Override
	public ICPUInstruction getInstruction(int data) {
		RV32FInstruction inst = new RV32FInstruction();
		inst.loadInstruction(data);
		return inst;
	}
	
	public int float2intRepresentation(float value) {
		return Float.floatToIntBits(value); 
	}
	public float intRepresentation2float(int value) {
		StringBuilder input1 = new StringBuilder(Bits.from(value).toBitString().substring(0, 32));			
		int intBits = Integer.parseInt(input1.reverse().toString(), 2);
		return Float.intBitsToFloat(intBits);

	}
}
