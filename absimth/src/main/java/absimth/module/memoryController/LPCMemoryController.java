package absimth.module.memoryController;

import java.util.List;

import absimth.exception.FixableErrorException;
import absimth.module.memoryController.util.ecc.EccType;
import absimth.sim.SimulatorManager;
import absimth.sim.memoryController.IMemoryController;
import absimth.sim.memoryController.MemoryController;
import absimth.sim.memoryController.model.ECCMemoryFaultModel;
import absimth.sim.utils.Bits;

public class LPCMemoryController  extends MemoryController implements IMemoryController {
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
		SimulatorManager.getSim().getReport().memoryControllerInc("WRITTEN "+EccType.LPC);
		
		Bits baseData = EccType.LPC.getEncode().encode(Bits.from(data));
		
		Bits data1 = baseData.subbit(0, 64);
		SimulatorManager.getSim().getReport().memoryControllerInc("WRITTEN "+EccType.LPC);
		MemoryController.writeBits(address, data1);
		
		Bits data2 = baseData.subbit(64, 64);
		SimulatorManager.getSim().getReport().memoryControllerInc("WRITTEN "+EccType.LPC);
		MemoryController.writeBits(getMaxAddress()+address, data2);
	}

	private long getMaxAddress() {
		return SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getTotalOfAddress()/2;
	}
	
	@Override
	public long read(long address) throws Exception {
		evaluate();

		SimulatorManager.getSim().getReport().memoryControllerInc("READ "+EccType.LPC);
		Bits data1 = MemoryController.readBits(address);
		SimulatorManager.getSim().getReport().memoryControllerInc("READ "+EccType.LPC);
		Bits data2 = MemoryController.readBits(getMaxAddress()+address);
		
		ECCMemoryFaultModel model = getMemoryStatus().getFromAddress(address);
		if(model!= null) model.setDirtAccess(true);
		try {
			return EccType.LPC.getEncode().decode(data1.append(data2)).toLong();	
		} catch (FixableErrorException e) {
			Bits fixed = e.getRecovered();	
			getMemoryStatus().getFromAddress(address).setFixedData(fixed);
			return fixed.toLong();
		}catch (Exception e) {
			throw e;
		}
		
	}

	@Override
	public Bits justDecode(long address) throws Exception {
		try {
			Bits b1 = SimulatorManager.getSim().getMemory().read(address);
			Bits b2 = SimulatorManager.getSim().getMemory().read(getMaxAddress()+address);
			Bits nData = b1.append(b2);
			return EccType.LPC.getEncode().decode(nData);
		} catch (FixableErrorException e) {
			return e.getRecovered();			
		}catch (Exception e) {
			throw e;
		}
	}

	@Override
	public EccType getCurrentEccType(long address) {
		return EccType.LPC;
	}
}
