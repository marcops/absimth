package absimth.module.cpu.riscv32;

import lombok.Getter;

@Getter
public class RV32MInstruction extends RV32IInstruction {

	public static boolean isRTypeMult(RV32MInstruction inst) {
		return inst.opcode == 0b0110011 && 0b000 == inst.funct3 && inst.funct7 == 0b001;
	}
	
	public static boolean isRTypeDiv(RV32MInstruction inst) {
		return inst.opcode == 0b0110011 && 0b100 == inst.funct3 && inst.funct7 == 0b001;
	}
	
	public static boolean isMInstruction(RV32MInstruction inst) {
		return isRTypeMult(inst) || isRTypeDiv(inst);
	}
	
	@Override
	public void loadInstruction(int instruction) {
		// Used in nearly all
		super.preDecodeInstruction(instruction);
		switch (opcode) {
			case 0b0110011: // R-type
				this.funct7 = (instruction >> 25) & 0x7F; // bits 31 to 25
				super.assemblyString = this.toAssemblyString();
				break;
			default:
				super.decodeInstruction(instruction);
				break;
		}
	}
	
	protected String toAssemblyString() {
		String instr = "", arg1 = "", arg2 = "", arg3 = "";
		arg1 = String.format("x%d", rd);
		arg2 = String.format("x%d", rs1);
		arg3 = String.format("x%d", rs2);
		
		if(isRTypeMult(this)) instr = "mult";
		if(isRTypeDiv(this)) instr = "div";
		if(!instr.isBlank()) {
			return String.format("%s %s %s %s", instr, arg1, arg2, arg3);
		}
		return super.toAssemblyString();
	}

}
