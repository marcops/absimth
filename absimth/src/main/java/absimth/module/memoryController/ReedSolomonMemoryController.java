package absimth.module.memoryController;

import absimth.exception.FixableErrorException;
import absimth.module.memoryController.util.ecc.EccType;
import absimth.sim.SimulatorManager;
import absimth.sim.memoryController.IMemoryController;
import absimth.sim.memoryController.MemoryController;
import absimth.sim.utils.Bits;

public class ReedSolomonMemoryController  extends MemoryController implements IMemoryController {

	@Override
	public void write(long address, long data) throws Exception {
		SimulatorManager.getSim().getReport().memoryControllerInc("WRITTEN "+EccType.REED_SOLOMON);
		MemoryController.writeBits(address, EccType.REED_SOLOMON.getEncode().encode(Bits.from(data)));
	}

	@Override
	public long read(long address) throws Exception {
		SimulatorManager.getSim().getReport().memoryControllerInc("READ "+EccType.REED_SOLOMON);
		try {
			return EccType.REED_SOLOMON.getEncode().decode(MemoryController.readBits(address)).toLong();
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
			return EccType.REED_SOLOMON.getEncode().decode(b);
		} catch (FixableErrorException e) {
			return e.getRecovered();			
		}catch (Exception e) {
			throw e;
		}
	}

	@Override
	public EccType getCurrentEccType(long address) {
		return EccType.REED_SOLOMON;
	}
}
