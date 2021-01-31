package absimth.sim.os;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import absimth.module.cpu.riscv32i.RV32IInstruction;
import absimth.sim.SimulatorManager;

public class OperationalSystem {
	private HashMap<Integer, OSCpuExecutor> cpuExecutor = new HashMap<>();
	
	public OSCpuExecutor getCpuExecutor(int cpu) {
		return cpuExecutor.getOrDefault(cpu, new OSCpuExecutor(cpu));
	}

	public void add(Integer cpu, String name, int programId) {
		int nextAddressFree = SimulatorManager.getSim().getAbsimthConfiguration().getRun().getPeripheralAddressSize();
		cpuExecutor.putIfAbsent(cpu, new OSCpuExecutor(cpu));
		
		int[] data = SimulatorManager.getSim().getBinaryPrograms().get(name);
		int stackSize = new RV32IInstruction(data[0]).getImm();
		cpuExecutor.get(cpu).add(name, programId, nextAddressFree, stackSize);
		//+3 - is a small code remove on builder
		nextAddressFree += (data.length/4) + stackSize +  3;
//		System.out.println(nextAddressFree);
	}

	public boolean isRunning() {
		List<Integer> cpus = genRandomCpus();
		if(cpus.isEmpty()) return false;
		return true;
	}
	
	public boolean executeNextInstruction() throws Exception {
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
			if(cpuExecutor.get(i).isRunningApp()) numbers.add(i);
		}
		Collections.shuffle(numbers);
		return numbers;
	}
	

}
