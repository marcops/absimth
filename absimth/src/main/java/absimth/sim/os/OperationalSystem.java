package absimth.sim.os;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import absimth.sim.SimulatorManager;
import absimth.sim.os.model.OSProgramModel;
import lombok.Getter;

public class OperationalSystem {
	@Getter
	private HashMap<Integer, OSCpuExecutor> cpuExecutor = new HashMap<>();
	@Getter
	private HashMap<Integer, OSCpuExecutor> runAfterExecutor = new HashMap<>();
	
	private int currentCPU = -1;
	
	public OSCpuExecutor getCurrentCPU() {
		return cpuExecutor.get(currentCPU);
	}
	
	public OSCpuExecutor getCpuExecutor(int cpu) {
		return cpuExecutor.getOrDefault(cpu, new OSCpuExecutor(cpu));
	}

	public OSProgramModel add(HashMap<Integer, OSCpuExecutor> ref, Integer cpu, String name, int programId, int nextAddressFree, int data[]) throws Exception {

		ref.putIfAbsent(cpu, new OSCpuExecutor(cpu));
//		int[] data = SimulatorManager.getSim().getBinaryPrograms().get(name);
		int stackSize =  ref.get(cpu).getICPU().getInstruction(data[0]).getImm(); //new RV32IInstruction(data[0]).getImm();
		
		OSProgramModel program = OSProgramModel.builder()
			.name(name)
			.programId(programId)
			.initialAddress(nextAddressFree)
			.stackSize(stackSize)
			.instructionLength(0)
			//+3 - is a small code remove on builder
			.totalOfMemory((data.length*4) + stackSize)
			.data(data)
			.lastDynamicAddress(0)
			.task(-1)
			.cpuId(cpu)
			.initialDynamicAddress(Integer.MAX_VALUE)
			.sucesuful(true)
			.finished(false)
			.build();
		
		ref.get(cpu).add(program);
		return program;
	}
	
	public OSProgramModel add(Integer cpu, String name, int programId, int nextAddressFree, int data[], boolean after) throws Exception {
		if (after) return add(runAfterExecutor, cpu, name, programId, nextAddressFree, data);
		return add(cpuExecutor, cpu, name, programId, nextAddressFree, data);
	}

	public boolean isRunning() {
		List<Integer> cpus = genRandomCpus();
		return !cpus.isEmpty();
	}
	
	public boolean executeNextInstruction() throws Exception {
		List<Integer> cpus = genRandomCpus();
		if(cpus.isEmpty()) {
			if(runAfterExecutor.isEmpty()) return false;
			cpuExecutor = runAfterExecutor;
			runAfterExecutor =  new HashMap<>();
			for(int i=0;i<SimulatorManager.getSim().getLstCpu().size();i++) {
				SimulatorManager.getSim().getLstCpu().get(i).setPc(0);
			}
			cpus = genRandomCpus();
		} 
		for (Integer cpu : cpus) {
			currentCPU = cpu; 
			if(cpu == 1)
				System.out.println("");
			cpuExecutor.get(cpu).executeNextInstruction();
			cpuExecutor.get(cpu).isRunningApp();
		}
		currentCPU = -1;
		return true;
	}
	private List<Integer> genRandomCpus(){
		List<Integer> numbers = new ArrayList<>();
		for (Integer i : cpuExecutor.keySet()) {
			if(cpuExecutor.get(i).isRunningApp()) numbers.add(i);
		}
		Collections.shuffle(numbers);
		return numbers;
	}
}
