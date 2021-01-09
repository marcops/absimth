package absimth.sim;

import absimth.exception.HardErrorException;
import absimth.exception.SoftErrorException;
import absimth.sim.ecc.CRC8;
import absimth.sim.ecc.EccType;
import absimth.sim.ecc.Hamming;

public class MemoryController {

	private Memory memory = new Memory(10485760/4+1);
	private EccType currentEccType = EccType.HAMMING_SECDEC;
	
	public void write(long address, long data) {
		memory.write(address, Bits.from(data));
	}

	public long read(long address) {
		return memory.read(address).toLong();
	}
	
	public Bits encode(Bits data) {
		if (currentEccType == EccType.CRC8)
			return CRC8.encode(data);
		if (currentEccType == EccType.HAMMING_SECDEC)
			return Hamming.encode(data);
		return data;
	}

	public Bits decode(Bits data) throws HardErrorException, SoftErrorException {
		try {
			if (currentEccType == EccType.CRC8)
				return CRC8.decode(data);
			if (currentEccType == EccType.HAMMING_SECDEC)
				return Hamming.decode(data);
			return data;
		} catch (HardErrorException e) {
//			checkNext();
			throw e;
		} catch (SoftErrorException e) {
//			checkNext();
			throw e;
		}

	}
	
//	private void checkNext() {
//		if (eccConfModel.getMode() != null && eccConfModel.getMode() == EccMode.NUMBER_OF_FAILS) {
//			if (Report.getInstance().getTotalReadInstruction().get()
//					+ Report.getInstance().getTotalWriteInstruction().get() > eccConfModel.getAfter()) {
//				System.out.println("position ecc " +position);
//				if (position < eccConfModel.getType().size() - 1)
//					position++;
//				if (position != eccConfModel.getType().size() - 1) {
//					currentEccType = getEccType(eccConfModel.getType(), position);
//					System.out.println("moved " + currentEccType.name());
//				}
//			}
//		}
//
//	}
}
