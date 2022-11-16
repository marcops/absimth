package absimth.module.memoryController;

import absimth.exception.FixableErrorException;
import absimth.exception.UnfixableErrorException;
import absimth.module.memoryController.util.ecc.EccType;
import absimth.sim.SimulatorManager;
import absimth.sim.memoryController.IMemoryController;
import absimth.sim.memoryController.MemoryController;
import absimth.sim.utils.Bits;

public class ReedSolomonMemoryController  extends MemoryController implements IMemoryController {

	private long getMaxAddress() {
		return SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getTotalOfAddress()/2;
	}
	
	@Override
	public void write(long address, long data) throws Exception {
		Bits baseData = EccType.REED_SOLOMON.getEncode().encode(Bits.from(data));
		
		Bits data1 = baseData.subbit(0, 64);
		SimulatorManager.getSim().getReport().memoryControllerInc("WRITTEN "+EccType.REED_SOLOMON);
		MemoryController.writeBits(address, data1);
		
		Bits data2 = baseData.subbit(64, 64);
		SimulatorManager.getSim().getReport().memoryControllerInc("WRITTEN "+EccType.REED_SOLOMON);
		MemoryController.writeBits(getMaxAddress()+address, data2);
	}

	private Bits readInternal(long address) throws Exception {
		try {
			SimulatorManager.getSim().getReport().memoryControllerInc("READ "+EccType.REED_SOLOMON);
			return  MemoryController.readBits(address);
		} catch (FixableErrorException e) {
			try {
				getMemoryStatus().getFromAddress(address).setFixedData(e.getRecovered());
				getMemoryStatus().getFromAddress(address).setOriginalData(e.getRecovered());
			}catch (Exception e1) {}
			return e.getRecovered();	
		}catch (UnfixableErrorException e) {
			SimulatorManager.getSim().getOs().getCurrentCPU().getCurrentProgram().setSucesuful(false);
	           try {
					getMemoryStatus().getFromAddress(address).setFixedData(e.getInput());
				}catch (Exception e1) {}
	           return e.getInput();
		}catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	public long read(long address) throws Exception {
		Bits data1 = readInternal(address);
		Bits data2 = readInternal(getMaxAddress()+address);
		return data1.append(data2).toLong();
	}

	@Override
	public Bits justDecode(long address) throws Exception {
		try {
			Bits b = SimulatorManager.getSim().getMemory().read(address);
			return EccType.REED_SOLOMON.getEncode().decode(b);
		} catch (FixableErrorException e) {
			return e.getRecovered();	
		}catch (UnfixableErrorException e) {
           getMemoryStatus().getFromAddress(address).setFixedData(e.getInput());
           return e.getInput();
		}catch (Exception e) {
			throw e;
		}
	}

	@Override
	public EccType getCurrentEccType(long address) {
		return EccType.REED_SOLOMON;
	}
}
