package absimth.module.cpu.riscv32;

public class RV32FInstruction extends RV32MInstruction {
	protected int rm;
	protected int funct5;
	protected int fmt;
	
	
	public static boolean isFLW(RV32MInstruction inst) {
		return inst.opcode == 0b100111;
	}
	
	public static boolean isFSW(RV32MInstruction inst) {
		return inst.opcode == 0b0000111;
	}
	public static boolean isFArithmetic(RV32MInstruction inst) {
		return inst.opcode == 0b1010011;
	}
	
	public static boolean isFInstruction(RV32MInstruction inst) {
		return isFLW(inst) || isFSW(inst) || isFArithmetic(inst);
	}
	
	@Override
	public void loadInstruction(int instruction) {

		super.preDecodeInstruction(instruction);
		this.rm = this.funct3;
		
		// Immediate is different for all types
		switch (opcode) {
		case 0b100111://FSW
			this.imm = (((instruction >> 20) & 0xFFFFFFE0) | ((instruction >>> 7) & 0x0000001F)); // Returns bits 31 to 25
			this.assemblyString = toAssemblyString();
			break;
		case 0b0000111: // flw
			this.imm = (instruction >> 20);
			this.assemblyString = toAssemblyString();
			break;
		case 0b1010011: // FADD/etc..
			this.fmt = (instruction >> 25) & 0x3; // bits 26 to 25
			this.funct5 = (instruction >> 27) & 0x1F; // bits 31 to 27
			super.funct3 = -1;
			this.assemblyString = toAssemblyString();
			break;
		default:
			super.decodeInstruction(instruction);
			break;
		}

	}

	/**
	 * Converts the instruction to an assembly string. Returns the string
	 */
	@Override
	protected String toAssemblyString() {
		String instr = "", arg1 = "", arg2 = "", arg3 = "";
		switch (opcode) {
		case 0b100111:
			arg1 = String.format("x%d", rs2);
			arg2 = String.format("%d(x%d)", imm, rs1);
			instr = "fsw";
			break;
		case 0b0000111:
			arg1 = String.format("x%d", rd);
			arg2 = String.format("%d(x%d)", imm, rs1);
			instr = "flw";
			break;
		case 0b1010011:
			arg1 = String.format("x%d", rd);
			arg2 = String.format("%d(x%d)", imm, rs1);
			switch(funct5) {
				case 0b00000:
					instr = "fadd";
					break;
				case 0b00001:
					instr = "fsub";
					break;
				case 0b00010:
					instr = "fmult";
					break;
				case 0b00011:
					instr = "fdiv";
					break;
				case 0b11010:
				case 0b11000:
					instr = "fcvt";
					break;
				case 0b01011:
					instr = "fsqrt";
					break;
				case 0b00100:
					instr = "fsgnj";
					break;
				case 0b00101:
					instr = "fmin/fmax";
					break;
				case 0b11100:
					instr = "fmv/fclass";
					break;
				case 0b10100:
					instr = "FEQ/FLT/FLE";
					break;
				case 0b11110:
					instr = "fmv";
					break;
				default:
					instr = "farith";
					break;
			}
			break;
		default:
			return super.toAssemblyString();
				
		}
		return String.format("%s %s %s %s", instr, arg1, arg2, arg3);
	}

}
