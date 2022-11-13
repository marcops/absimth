package absimth.module.memoryController;

import java.util.List;

import absimth.exception.FixableErrorException;
import absimth.exception.UnfixableErrorException;
import absimth.module.memoryController.util.ecc.EccType;
import absimth.sim.SimulatorManager;
import absimth.sim.memoryController.IMemoryController;
import absimth.sim.memoryController.MemoryController;
import absimth.sim.memoryController.model.ECCMemoryFaultModel;
import absimth.sim.utils.Bits;

public class HammingMemoryController  extends MemoryController implements IMemoryController {
	private final boolean SCRUBBE = false; 
	private long numberOfExecution =0;
	private final int cycleSize = 10;
	
	private void evaluate() {
		if(!SCRUBBE) return;
		numberOfExecution++;	
		if(numberOfExecution<cycleSize) return;
		numberOfExecution = 0;
		scrubbe();
	}
	private void scrubbe() {
		List<Long> lst = this.getMemoryStatus().getAddressWithError();
		lst.forEach(address->{
			try {
				write(address, read(address));
			} catch (Exception e) {
				//none
			}
		});
	}
	
	
	@Override
	public void write(long address, long data) throws Exception {
		evaluate();
		SimulatorManager.getSim().getReport().memoryControllerInc("WRITTEN "+EccType.HAMMING_SECDEC);
		MemoryController.writeBits(address, EccType.HAMMING_SECDEC.getEncode().encode(Bits.from(data)));
	}
	
	

	@Override
	public long read(long address) throws Exception {
		evaluate();
		SimulatorManager.getSim().getReport().memoryControllerInc("READ "+EccType.HAMMING_SECDEC);
		try {
			ECCMemoryFaultModel model = getMemoryStatus().getFromAddress(address);
			if(model!= null) model.setDirtAccess(true);
			
			return EccType.HAMMING_SECDEC.getEncode().decode(MemoryController.readBits(address)).toLong();
		} catch (FixableErrorException e) {
			getMemoryStatus().getFromAddress(address).setFixedData(e.getRecovered());
			return e.getRecovered().toLong();			
		}catch (UnfixableErrorException e) {
			getMemoryStatus().getFromAddress(address).setFixedData(e.getInput());
			return e.getInput().toLong();
//			throw e;
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
