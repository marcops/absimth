package absimth.sim.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import absimth.module.cpu.riscv32.RISCV32f;

class FloatRiscvTest {
	@Test
	void testDual() throws Exception {
		RISCV32f risc = new RISCV32f();
		int i = risc.float2intRepresentation(5.2f);
		float f = risc.intRepresentation2float(i);
		int fi = risc.float2intRepresentation(f);
		float ff = risc.intRepresentation2float(fi);
		assertEquals(ff, 5.2f);
	}

}
