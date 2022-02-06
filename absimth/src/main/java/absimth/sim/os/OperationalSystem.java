package absimth.sim.os;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import absimth.sim.os.model.OSProgramModel;
import lombok.Getter;

public class OperationalSystem {
	@Getter
	private HashMap<Integer, OSCpuExecutor> cpuExecutor = new HashMap<>();
	
	
	public OSCpuExecutor getCpuExecutor(int cpu) {
		return cpuExecutor.getOrDefault(cpu, new OSCpuExecutor(cpu));
	}

	public OSProgramModel add(Integer cpu, String name, int programId, int nextAddressFree, int data[]) {
		cpuExecutor.putIfAbsent(cpu, new OSCpuExecutor(cpu));
//		int[] data = SimulatorManager.getSim().getBinaryPrograms().get(name);
		int stackSize =  cpuExecutor.get(cpu).getICPU() .getInstruction(data[0]).getImm(); //new RV32IInstruction(data[0]).getImm();
		
		OSProgramModel program = OSProgramModel.builder()
			.name(name)
			.programId(programId)
			.initialAddress(nextAddressFree)
			.stackSize(stackSize)
			.instructionLength(0)
			//+3 - is a small code remove on builder
			.totalOfMemoryUsed((data.length/4) + stackSize +  3)
			.data(data)
			.task(-1)
			.cpu(cpu)
			.sucesuful(true)
			.build();
		
		cpuExecutor.get(cpu).add(program);

		return program;
	}

	public boolean isRunning() {
		List<Integer> cpus = genRandomCpus();
		return !cpus.isEmpty();
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
		for (Integer i : cpuExecutor.keySet()) {
			if(cpuExecutor.get(i).isRunningApp()) numbers.add(i);
			
		}
		Collections.shuffle(numbers);
		return numbers;
	}
}
