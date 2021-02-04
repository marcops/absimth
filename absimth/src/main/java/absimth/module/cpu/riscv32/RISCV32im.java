package absimth.module.cpu.riscv32;

import absimth.sim.cpu.ICPUInstruction;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RISCV32im extends RISCV32i {

	@Override
	public void executeInstruction() throws Exception {
		RV32MInstruction inst = new RV32MInstruction(memory.getWord(pc*4));
		
		if(RV32MInstruction.isMInstruction(inst)) executeMInstruction(inst); 
		else super.executeInstruction();
	}

	private void executeMInstruction(RV32MInstruction inst) {
		prevPc = pc;
		if(RV32MInstruction.isRTypeMult(inst)) reg[inst.getRd()] = reg[inst.getRs1()] * reg[inst.getRs2()];
		else if(RV32MInstruction.isRTypeDiv(inst)) {
			if (reg[inst.getRs2()] != 0) reg[inst.getRd()] = reg[inst.getRs1()] / reg[inst.getRs2()];
			else reg[inst.getRd()] = -1;
		}
		pc++;
	}

	@Override
	public String getName() {
		return super.getName()+"m";
	}

	@Override
	public ICPUInstruction getInstruction(int data) {
		return new RV32MInstruction(data);
	}
}
