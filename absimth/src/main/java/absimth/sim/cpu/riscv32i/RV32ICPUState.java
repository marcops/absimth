package absimth.sim.cpu.riscv32i;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RV32ICPUState {
	private int pc; 
	private int prevPc;
	private int[] reg; 
}
