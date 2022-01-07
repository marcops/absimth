package absimth.sim.configuration.model.hardware.memory;

import absimth.sim.memory.PhysicalAddressService;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PhysicalAddress {
	private long pAddress;
	private long module;
	private long rank;
	private long bankGroup;
	private long bank;
	private long row;
	private long column;
	private long pagePosition;
	private int columnLength;

	public static PhysicalAddress create(PhysicalAddressService vas, long pAddress) {
		return PhysicalAddress.builder()
				.pAddress(pAddress)
				.module(vas.getModule(pAddress))
				.rank(vas.getRank(pAddress))
				.bankGroup(vas.getBankGroup(pAddress))
				.bank(vas.getBank(pAddress))
				.row(vas.getRow(pAddress))
				.column(vas.getColumn(pAddress))
				.pagePosition(vas.getPagePosition(pAddress))
				.columnLength((int)vas.getColumnSize())
				.build();
	}

//	public long getCellPosition() {
//		return (row*columnLength) + column;
//	}
	
	@Override
	public String toString() {
		return "PhysicalAddress [" + pAddress +" / 0x"+Long.toHexString(pAddress) + "] [module=" + module 
			+ ", rank=" + rank 
			+ ", bankGroup=" + bankGroup 
			+ ", bank=" + bank
//			+ ", cell=" + getCellPosition()
			+ ", row=" + row 
			+ ", column=" + column
//			+ ", pagePosition=" + pagePosition
			+ "]";
	}
}
