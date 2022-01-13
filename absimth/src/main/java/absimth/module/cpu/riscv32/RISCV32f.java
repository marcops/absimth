package absimth.module.cpu.riscv32;

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
			float value = intRepresentation2float(reg[inst.rs1]); 
			reg[inst.rd] = (int)value;
			break;
		case 0b01011://FSQRT
			reg[inst.rd] = float2intRepresentation((float)Math.sqrt(intRepresentation2float(reg[inst.rs1])));
			break;
			case 0b00101://FMIN/FMAX
				switch(inst.rm) {
				case 0b000://FMIN
					reg[inst.rd] = float2intRepresentation((float)Math.min(intRepresentation2float(reg[inst.rs1]),intRepresentation2float(reg[inst.rs2]))); 
					break;
				case 0b001://FMAX
					reg[inst.rd] = float2intRepresentation((float)Math.max(intRepresentation2float(reg[inst.rs1]),intRepresentation2float(reg[inst.rs2])));
					break;
				}
			break;
		case 0b10100://FEQ/FLT/FLE
			switch(inst.rm) {
			case 0b010://FEQ
				reg[inst.rd] = intRepresentation2float(reg[inst.rs1]) == intRepresentation2float(inst.rs2) ? 1 : 0; 
				break;
			case 0b001://FLT
				reg[inst.rd] = intRepresentation2float(reg[inst.rs1]) < intRepresentation2float(inst.rs2) ? 1 : 0;
				break;
			case 0b00://FLE
				reg[inst.rd] = intRepresentation2float(reg[inst.rs1]) <= intRepresentation2float(inst.rs2) ? 1 : 0;
				break;
			}
			break;
			case 0b00100://FSGNJ/FSGNJN/FSGNJX
			System.err.println("Not Implemented FLOAT INSTRUCTION - FSGNJ/FSGNJN/FSGNJX");
			break;
		case 0b11100://FMV.X/FCLASS
			System.err.println("Not Implemented FLOAT INSTRUCTION - FMV.X/FCLASS");
			break;
		case 0b11110://FMV.W.X
			System.err.println("Not Implemented FLOAT INSTRUCTION - FMV.W.X");
			break;
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
	
	public static int float2intRepresentation(float value) {
		return Float.floatToIntBits(value); 
	}
	public static float intRepresentation2float(int value) {
		StringBuilder input1 = new StringBuilder(Bits.from(value).toBitString().substring(0, 32));			
		int intBits = Integer.parseInt(input1.reverse().toString(), 2);
		return Float.intBitsToFloat(intBits);

	}
}
