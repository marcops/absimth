package absimth.module.cpu.riscv32.module;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RV32CPUState {
	private int pc; 
	private int prevPc;
	private int[] reg; 
}
