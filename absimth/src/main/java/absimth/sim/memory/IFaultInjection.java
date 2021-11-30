package absimth.sim.memory;

public interface IFaultInjection {
//	FaultAddressModel getFault();
	void onWrite() throws Exception;
	void onRead() throws Exception;
	void preInstruction();
	void posInstruction();
}
