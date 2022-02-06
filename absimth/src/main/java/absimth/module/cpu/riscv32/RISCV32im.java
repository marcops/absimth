package absimth.module.cpu.riscv32;

import absimth.sim.cpu.ICPUInstruction;
import absimth.sim.os.model.IOSMemoryAccess;
import absimth.sim.utils.AbsimLog;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RISCV32im extends RISCV32i {

	@Override
	public String executeInstruction(Integer data, IOSMemoryAccess _memAccess) throws Exception {
		prevPc = pc;
		reg[0] = 0; 
		memAccess = _memAccess;
		Integer bData = data == null ? memAccess.getWord(pc*4) : data;
		RV32MInstruction inst = new RV32MInstruction();
		inst.loadInstruction(bData);
		
		if(RV32MInstruction.isMInstruction(inst)) {
			AbsimLog.instruction(inst.assemblyString);
			return doInstruction(inst); 
		}
		return super.executeInstruction(bData, memAccess);
		
	}

	private String doInstruction(RV32MInstruction inst) {
//		prevPc = pc;
		if(RV32MInstruction.isRTypeMult(inst)) 
			reg[inst.getRd()] = reg[inst.getRs1()] * reg[inst.getRs2()];
		if(RV32MInstruction.isRTypeRem(inst)) 
			reg[inst.getRd()] = reg[inst.getRs1()] % reg[inst.getRs2()];
		else if(RV32MInstruction.isRTypeDiv(inst)) {
			if (reg[inst.getRs2()] != 0) 
				reg[inst.getRd()] = reg[inst.getRs1()] / reg[inst.getRs2()];
			else 
				reg[inst.getRd()] = -1;
		}
		pc++;
		return null;
	}

	@Override
	public String getName() {
		return super.getName()+"m";
	}

	@Override
	public ICPUInstruction getInstruction(int data) {
		RV32MInstruction inst = new RV32MInstruction();
		inst.loadInstruction(data);
		return inst;
	}
}
