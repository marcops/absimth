package absimth.module.memoryFaultInjection;

import absimth.module.cpu.riscv32i.RV32ICpu2Mem;
import absimth.module.memoryController.util.ecc.EccType;
import absimth.sim.SimulatorManager;
import absimth.sim.memory.IFaultInjection;
import absimth.sim.utils.Bits;

public class ErrorAt1000MFI implements IFaultInjection {


	@Override
	public void haveToCreateError() throws Exception {
		int address = 1000;
		SimulatorManager.getSim().getMemory().write(1, EccType.CRC8.getEncode().encode(Bits.from(RV32ICpu2Mem.java2int(address))));
		
		Bits b = EccType.CRC8.getEncode().encode(Bits.from(5));
		b.flip(2);
		SimulatorManager.getSim().getMemory().write(address, b);
	}
}
