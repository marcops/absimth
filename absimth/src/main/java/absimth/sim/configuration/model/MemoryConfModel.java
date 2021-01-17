package absimth.sim.configuration.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class MemoryConfModel {
	private ModuleConfModel module;
	private ChannelMode channelMode;
	private String name;
	
	public Long getTotalOfAddress() {
		return (long) module.getRank().getChip().getBankGroup().getBank().getCell().getColumns() 
				* module.getRank().getChip().getBankGroup().getBank().getCell().getRow()
				* module.getRank().getChip().getBankGroup().getBank().getAmount() 
				* module.getRank().getChip().getBankGroup().getAmount() 
				* module.getRank().getAmount() 
				* module.getAmount();
	}

	public Integer getWorldLength() {
		return 8 * module.getRank().getChip().getAmount();
	}
	
	@Override
	public String toString() {
		return  "    Channel Mode=" + channelMode + "\r\n" +
				"    Word Length=" + getWorldLength() + "\r\n" +
				"    Total of Address=" + getTotalOfAddress() + "\r\n"+
				"    Name=" + name + "\r\n"+
				"    Module amount=" + module.getAmount() + "\r\n"+
				"    Rank amount=" + module.getRank().getAmount() + "\r\n" +
				"    Chip amount=" + module.getRank().getChip().getAmount() + "\r\n" +
				"    Bank Group amount=" + module.getRank().getChip().getBankGroup().getAmount() + "\r\n" +
				"    Bank amount=" + module.getRank().getChip().getBankGroup().getBank().getAmount() + "\r\n" +
				"    Cell=" + module.getRank().getChip().getBankGroup().getBank().getCell().getRow() + ", "+
				 module.getRank().getChip().getBankGroup().getBank().getCell().getColumns();
	}
	
	
}
