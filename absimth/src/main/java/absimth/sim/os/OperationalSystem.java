package absimth.sim.os;

import java.util.HashMap;

public class OperationalSystem {
	private HashMap<Integer, OSCpuExecutor> cpuExecutor = new HashMap<>();

	public OSCpuExecutor getCpuExecutor(int cpu) {
		return cpuExecutor.getOrDefault(cpu, new OSCpuExecutor());
	}

	public void add(Integer cpu, String name, int programId) {
		cpuExecutor.putIfAbsent(cpu, new OSCpuExecutor());
		cpuExecutor.get(cpu).add(name, programId);
	}

}
