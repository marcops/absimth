package absimth.module.memoryController;

import absimth.exception.FixableErrorException;
import absimth.module.memoryController.util.ecc.EccType;
import absimth.sim.SimulatorManager;
import absimth.sim.memory.IMemoryController;
import absimth.sim.memory.MemoryController;
import absimth.sim.utils.Bits;

public class HammingMemoryController  extends MemoryController implements IMemoryController {

	@Override
	public void write(long address, long data) throws Exception {
		SimulatorManager.getSim().getReport().memoryControllerInc("WRITTEN "+EccType.HAMMING_SECDEC);
		MemoryController.writeBits(address, EccType.HAMMING_SECDEC.getEncode().encode(Bits.from(data)));
	}

	@Override
	public long read(long address) throws Exception {
		SimulatorManager.getSim().getReport().memoryControllerInc("READ "+EccType.HAMMING_SECDEC);
		try {
			return EccType.HAMMING_SECDEC.getEncode().decode(MemoryController.readBits(address)).toLong();
		} catch (FixableErrorException e) {
			return e.getRecovered().toLong();			
		}catch (Exception e) {
			throw e;
		}
	}

	@Override
	public Bits justDecode(long address) throws Exception {
		try {
			Bits b = SimulatorManager.getSim().getMemory().read(address);
			return EccType.HAMMING_SECDEC.getEncode().decode(b);
		} catch (FixableErrorException e) {
			return e.getRecovered();			
		}catch (Exception e) {
			throw e;
		}
	}

	@Override
	public EccType getCurrentEccType(long address) {
		return EccType.HAMMING_SECDEC;
	}
}
