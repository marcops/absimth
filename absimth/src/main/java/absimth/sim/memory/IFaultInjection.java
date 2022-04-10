package absimth.sim.memory;

import absimth.sim.utils.Bits;

public interface IFaultInjection {
	boolean onWrite(long address, Bits data) throws Exception;

	boolean onRead(long address) throws Exception;

	void preInstruction();

	void posInstruction();
	
}
