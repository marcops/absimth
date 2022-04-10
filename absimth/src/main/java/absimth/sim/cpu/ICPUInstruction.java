package absimth.sim.cpu;

public interface ICPUInstruction {
	int getImm();
	boolean isNoRd();
	boolean isSType();
	boolean isEcall();
	int getRd();
	int getRs1();
	int getRs2();
	String getAssemblyString();
	void loadInstruction(int instruction) throws Exception;
}
