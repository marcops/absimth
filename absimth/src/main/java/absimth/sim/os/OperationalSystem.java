package absimth.sim.os;

import java.util.HashMap;

import absimth.sim.SimulatorManager;

public class OperationalSystem {
	private HashMap<Integer, OSCpuExecutor> cpuExecutor = new HashMap<>();
	private int nextAddressFree = 0;
	
	public OSCpuExecutor getCpuExecutor(int cpu) {
		return cpuExecutor.getOrDefault(cpu, new OSCpuExecutor());
	}

	public void add(Integer cpu, String name, int programId) {
		cpuExecutor.putIfAbsent(cpu, new OSCpuExecutor());
		
		cpuExecutor.get(cpu).add(name, programId, nextAddressFree);
		int[] data = SimulatorManager.getSim().getBinaryPrograms().get(name);
		//+3 - is a small code remove on builder
		nextAddressFree += (data.length/4) + SimulatorManager.STACK_POINTER_PROGRAM_SIZE +  3;
		System.out.println(nextAddressFree);
	}

}
