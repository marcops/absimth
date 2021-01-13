package absimth.sim.os;

import java.util.HashMap;

public class OperationalSystem {
	private HashMap<Integer, OSCpuExecutor> cpuExecutor = new HashMap<>();

	// TODO MELHORAR
	public static final int ADDRESS_TO_EACH_PROGRAM = 5000;
	private int addressRef = 0;
	// END TODO

	public OSCpuExecutor getCpuExecutor(int cpu) {
		return cpuExecutor.getOrDefault(cpu, new OSCpuExecutor());
	}

	public void add(Integer cpu, String name) {
		//TEST
		addressRef += 2000;
		//END TEST
		cpuExecutor.putIfAbsent(cpu, new OSCpuExecutor());
		cpuExecutor.get(cpu).add(name, addressRef, ADDRESS_TO_EACH_PROGRAM);
		addressRef += ADDRESS_TO_EACH_PROGRAM;
	}
}
