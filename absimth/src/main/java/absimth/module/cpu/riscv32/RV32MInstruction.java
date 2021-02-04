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
	
	public RV32MInstruction(int instruction) {
		super(instruction);
		assemblyString = itoAssemblyString();
	}

	private String itoAssemblyString() {
		String instr = "", arg1 = "", arg2 = "", arg3 = "";
		if(!super.isFoundInstruction()) {
			arg1 = String.format("x%d", rd);
			arg2 = String.format("x%d", rs1);
			arg3 = String.format("x%d", rs2);
			
			if(isRTypeMult(this)) instr = "mult";
			if(isRTypeDiv(this)) instr = "div";
			if(!instr.isBlank()) {
				super.foundInstruction = true;
				return String.format("%s %s %s %s", instr, arg1, arg2, arg3);
			}
		}
		return assemblyString;
	}

}
