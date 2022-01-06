package absimth.sim.memory;

public interface IFaultInjection {
	void onWrite() throws Exception;

	void onRead() throws Exception;

	void preInstruction();

	void posInstruction();
	
}
