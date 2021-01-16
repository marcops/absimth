package absimth.sim.os;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import absimth.sim.SimulatorManager;

public class OperationalSystem {
	private HashMap<Integer, OSCpuExecutor> cpuExecutor = new HashMap<>();
	private int nextAddressFree = 0;
	
	public OSCpuExecutor getCpuExecutor(int cpu) {
		return cpuExecutor.getOrDefault(cpu, new OSCpuExecutor(cpu));
	}

	public void add(Integer cpu, String name, int programId) {
		cpuExecutor.putIfAbsent(cpu, new OSCpuExecutor(cpu));
		
		cpuExecutor.get(cpu).add(name, programId, nextAddressFree);
		int[] data = SimulatorManager.getSim().getBinaryPrograms().get(name);
		//+3 - is a small code remove on builder
		nextAddressFree += (data.length/4) + SimulatorManager.STACK_POINTER_PROGRAM_SIZE +  3;
//		System.out.println(nextAddressFree);
	}

	public boolean executeNextInstruction() {
		List<Integer> cpus = genRandomCpus();
		if(cpus.isEmpty()) return false;
		for (Integer cpu : cpus) {
			cpuExecutor.get(cpu).executeNextInstruction();
			cpuExecutor.get(cpu).isRunningApp();
		}
		return true;
	}
	private List<Integer> genRandomCpus(){
		List<Integer> numbers = new ArrayList<>();
		for (int i = 0; i < cpuExecutor.size(); i++) {
			if(!cpuExecutor.get(i).isEmpty()) numbers.add(i);
		}
		Collections.shuffle(numbers);
		return numbers;
	}
	

}
