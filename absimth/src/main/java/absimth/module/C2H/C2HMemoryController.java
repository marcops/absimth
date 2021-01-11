package absimth.module.C2H;

import absimth.sim.Bits;
import absimth.sim.IMemoryController;
import absimth.sim.MemoryController;

public class C2HMemoryController extends MemoryController implements IMemoryController {

	@Override
	public void write(long address, long data) {
		writeBits(address, Bits.from((int)data));
	}

	@Override
	public long read(long address) {
//		count++;
//		if(count%15 ==0) {
//			System.out.println("inverted bit 5 at address " + address);
//			memory.read(address).flip(5);
//		}
		return readBits(address).toLong();
	}
	
//	private Bits encode(Bits data) {
//		if (currentEccType == EccType.CRC8)
//			return CRC8.encode(data);
//		if (currentEccType == EccType.HAMMING_SECDEC)
//			return Hamming.encode(data);
//		return data;
//	}
//
//	private Bits decode(Bits data) throws HardErrorException, SoftErrorException {
//		try {
//			if (currentEccType == EccType.CRC8)
//				return CRC8.decode(data);
//			if (currentEccType == EccType.HAMMING_SECDEC)
//				return Hamming.decode(data);
//			return data;
//		} catch (HardErrorException e) {
////			checkNext();
//			throw e;
//		} catch (SoftErrorException e) {
////			checkNext();
//			throw e;
//		}
//
//	}
	
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
