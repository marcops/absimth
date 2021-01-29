package absimth.sim.memory;

import absimth.sim.configuration.model.hardware.memory.BankConfModel;
import absimth.sim.configuration.model.hardware.memory.BankGroupConfModel;
import absimth.sim.configuration.model.hardware.memory.CellConfModel;
import absimth.sim.configuration.model.hardware.memory.ChannelMode;
import absimth.sim.configuration.model.hardware.memory.ModuleConfModel;
import absimth.sim.configuration.model.hardware.memory.PhysicalAddress;
import absimth.sim.configuration.model.hardware.memory.RankConfModel;
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
	private long moduleSize;
	private ChannelMode mode;

	public long getMaxAddress() {
		return maxAddress;
	}
	
	public long getColumnSize() {
		return columnSize;
	}
	
	public PhysicalAddress getPhysicalAddressReverse(int module, int rank, int bankGroup, int bank, int row, int column) {
		long address = column + (row * columnSize) + (bank * cellSize) + (bankGroup * bankSize) + (rank * bankGroupSize);
		address *= moduleSize;
		address += module;
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
		long row = c.getRow();
		long column = c.getColumns();

		long cellSize = row * column;
		long bankSize = cellSize * bank;
		long bankGroupSize = bankSize * bankGroup;
		long rankSize = bankGroupSize * rank;

		long maxAddress = rankSize * mod.getAmount();
		return PhysicalAddressService.builder()
//				.rowSize(row)
				.columnSize(column)
				.cellSize(cellSize)
				.bankSize(bankSize)
				.bankGroupSize(bankGroupSize)
				.rankSize(rankSize)
				.maxAddress(maxAddress)
				.moduleSize(mod.getAmount())
				.mode(channelMode == null ? ChannelMode.SINGLE : channelMode)
				.build();
	}

	public long getModule(long pAddress) {
		if (pAddress >= maxAddress)
			throw new IllegalArgumentException("exceed memory address");
		
		return mode == ChannelMode.SINGLE ? 
				(pAddress / rankSize) : 
				(pAddress % mode.getValue());
		
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
		return pAddress - (getMultRow(pAddress) * columnSize) 
				- (getMultBank(pAddress) * cellSize)
				- (getMultBankGroup(pAddress) * bankSize) 
				- (getMultRank(pAddress) * bankGroupSize);
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
		return pAddress - (getSingleRow(pAddress) * columnSize) 
				- (getSingleBank(pAddress) * cellSize)
				- (getSingleBankGroup(pAddress) * bankSize) 
				- (getSingleRank(pAddress) * bankGroupSize)
				- (getModule(pAddress) * rankSize);
	}
	
	public long getRank(long pAddress) {
		return ChannelMode.SINGLE == mode ? getSingleRank(pAddress) : getMultRank(pAddress/mode.getValue()) ;
	}

	public long getBankGroup(long pAddress) {
		return ChannelMode.SINGLE == mode ? getSingleBankGroup(pAddress) : getMultBankGroup(pAddress/mode.getValue()) ;
	}
	
	public long getBank(long pAddress) {
		return ChannelMode.SINGLE == mode ? getSingleBank(pAddress) : getMultBank(pAddress/mode.getValue()) ;
	}
	
	public long getRow(long pAddress) {
		return ChannelMode.SINGLE == mode ? getSingleRow(pAddress) : getMultRow(pAddress/mode.getValue()) ;
	}

	public long getColumn(long pAddress) {
		return ChannelMode.SINGLE == mode ? getSingleColumn(pAddress) : getMultColumn(pAddress/mode.getValue()) ;
	}

}
