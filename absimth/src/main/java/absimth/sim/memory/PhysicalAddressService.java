package absimth.sim.memory;

import absimth.sim.configuration.model.hardware.memory.BankConfModel;
import absimth.sim.configuration.model.hardware.memory.BankGroupConfModel;
import absimth.sim.configuration.model.hardware.memory.CellConfModel;
import absimth.sim.configuration.model.hardware.memory.ChannelMode;
import absimth.sim.configuration.model.hardware.memory.ModuleConfModel;
import absimth.sim.configuration.model.hardware.memory.PhysicalAddress;
import absimth.sim.configuration.model.hardware.memory.RankConfModel;
import absimth.sim.utils.Bits;
import lombok.Builder;

@Builder
public class PhysicalAddressService {
//	private long rowSize;
	private long columnSize;
	private long bankSize;
	private long bankGroupSize;
	private long rankSize;
	private long cellSize;
	private long maxAddress;
//	private long moduleSize;
	private long pageSize;
	private ChannelMode channelMode;

	public long getMaxAddress() {
		return maxAddress;
	}
	
	public long getColumnSize() {
		return columnSize;
	}
	
	public PhysicalAddress getPhysicalAddressReverse(long module, long rank, long bankGroup, long bank, int row, int column, int pagePos) {
		long address = (column * pageSize) + (row * columnSize) + (bank * cellSize) + (bankGroup * bankSize) + (rank * bankGroupSize);
		if(channelMode == ChannelMode.SINGLE_CHANNEL) {
			address += pagePos;
			address +=(module * rankSize);
		} else {
			if(address>=pageSize)
				address *= channelMode.getValue();
			address += (module*channelMode.getValue());
			address += pagePos;
//			address += module;
		}
		return PhysicalAddress.create(this, address);
	}
	
	public PhysicalAddress getPhysicalAddress(Long pAddress) {
		return PhysicalAddress.create(this, pAddress);
	}
	
	public static PhysicalAddressService create(ModuleConfModel mod, ChannelMode channelMode) {
		int rank = mod.getRank().getAmount();
		RankConfModel r = mod.getRank();

		BankGroupConfModel bg = r.getChip().getBankGroup();
		int bankGroup = r.getChip().getBankGroup().getAmount();

		BankConfModel b = bg.getBank();
		int bank = bg.getBank().getAmount();

		CellConfModel c = b.getCell();
		long pageSize = c.getHeight()/Bits.BYTE_SIZE;
		long row = c.getRow();
		long columnSize = c.getColumns() * pageSize;

		long cellSize = row * columnSize;
		long bankSize = cellSize * bank;
		long bankGroupSize = bankSize * bankGroup;
		long rankSize = bankGroupSize * rank;

		long maxAddress = rankSize * mod.getAmount();
		return PhysicalAddressService.builder()
//				.rowSize(row)
				.columnSize(columnSize)
				.cellSize(cellSize)
				.bankSize(bankSize)
				.bankGroupSize(bankGroupSize)
				.rankSize(rankSize)
				.maxAddress(maxAddress)
//				.moduleSize(mod.getAmount())
				.pageSize(pageSize)
				.channelMode(channelMode == null ? ChannelMode.SINGLE_CHANNEL : channelMode)
				.build();
	}

	public long getModule(long pAddress) {
		if (pAddress >= maxAddress)
			throw new IllegalArgumentException("exceed memory address");
		
		return channelMode == ChannelMode.SINGLE_CHANNEL ? 
				(pAddress / rankSize) : 
				getMultiAddress(pAddress);
		
	}

	private long getMultiAddress(long pAddress) {
		return (pAddress/pageSize) % channelMode.getValue();
	}

	
	private long getMultRank(long pAddress) {
		long rank = pAddress;
		return rank / bankGroupSize;
	}
	
	private long getMultBankGroup(long pAddress) {
		long bankGroup = pAddress 
				- (getMultRank(pAddress) * bankGroupSize);
		return bankGroup / bankSize;
	}

	private long getMultBank(long pAddress) {
		long bank = pAddress 
				- (getMultBankGroup(pAddress) * bankSize) 
				- (getMultRank(pAddress) * bankGroupSize);
		return bank / cellSize;
	}

	private long getMultRow(long pAddress) {
		long row = pAddress - (getMultBank(pAddress) * cellSize) 
				- (getMultBankGroup(pAddress) * bankSize)
				- (getMultRank(pAddress) * bankGroupSize) ;
		return row / columnSize;
	}

	public long getMultColumn(long pAddress) {
		long column =  pAddress - (getMultRow(pAddress) * columnSize) 
				- (getMultBank(pAddress) * cellSize)
				- (getMultBankGroup(pAddress) * bankSize) 
				- (getMultRank(pAddress) * bankGroupSize);
		return column / pageSize;
	}

	
	private long getSingleRank(long pAddress) {
		long rank = pAddress - (getModule(pAddress) * rankSize);
		return rank / bankGroupSize;
	}
	
	private long getSingleBankGroup(long pAddress) {
		long bankGroup = pAddress 
				- (getSingleRank(pAddress) * bankGroupSize)
				- (getModule(pAddress) * rankSize);
		return bankGroup / bankSize;
	}
	
	private long getSingleBank(long pAddress) {
		long bank = pAddress 
				- (getSingleBankGroup(pAddress) * bankSize) 
				- (getSingleRank(pAddress) * bankGroupSize)
				- (getModule(pAddress) * rankSize);
		return bank / cellSize;
	}

	private long getSingleRow(long pAddress) {
		long row = pAddress - (getSingleBank(pAddress) * cellSize) 
				- (getSingleBankGroup(pAddress) * bankSize)
				- (getSingleRank(pAddress) * bankGroupSize) 
				- (getModule(pAddress) * rankSize);
		return row / columnSize;
	}

	public long getSingleColumn(long pAddress) {
		long column =  pAddress - (getSingleRow(pAddress) * columnSize) 
				- (getSingleBank(pAddress) * cellSize)
				- (getSingleBankGroup(pAddress) * bankSize) 
				- (getSingleRank(pAddress) * bankGroupSize)
				- (getModule(pAddress) * rankSize);
		return column / pageSize;
	}
	
	public long getRank(long pAddress) {
		return ChannelMode.SINGLE_CHANNEL == channelMode ? getSingleRank(pAddress) : getMultRank(pAddress/channelMode.getValue()) ;
	}

	public long getBankGroup(long pAddress) {
		return ChannelMode.SINGLE_CHANNEL == channelMode ? getSingleBankGroup(pAddress) : getMultBankGroup(pAddress/channelMode.getValue()) ;
	}
	
	public long getBank(long pAddress) {
		return ChannelMode.SINGLE_CHANNEL == channelMode ? getSingleBank(pAddress) : getMultBank(pAddress/channelMode.getValue()) ;
	}
	
	public long getRow(long pAddress) {
		return ChannelMode.SINGLE_CHANNEL == channelMode ? getSingleRow(pAddress) : getMultRow(pAddress/channelMode.getValue()) ;
	}

	public long getColumn(long pAddress) {
		return ChannelMode.SINGLE_CHANNEL == channelMode ? getSingleColumn(pAddress) : getMultColumn(pAddress/channelMode.getValue()) ;
	}

	public long getPagePosition(long address) {
		return address % pageSize;
	}

}
