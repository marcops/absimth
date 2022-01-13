package absimth.sim.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import absimth.module.cpu.riscv32.RISCV32f;

class FloatRiscvTest {
	@Test
	void testDual() throws Exception {
		int i = RISCV32f.float2intRepresentation(5.2f);
		float f = RISCV32f.intRepresentation2float(i);
		int fi = RISCV32f.float2intRepresentation(f);
		float ff = RISCV32f.intRepresentation2float(fi);
		assertEquals(ff, 5.2f);
	}

}
