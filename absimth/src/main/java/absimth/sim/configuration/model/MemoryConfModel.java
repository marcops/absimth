package absimth.sim.configuration.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import absimth.sim.configuration.model.hardware.memory.ChannelMode;
import absimth.sim.configuration.model.hardware.memory.ModuleConfModel;
import absimth.sim.utils.Bits;
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
	private Integer linesPerClock;
	private Integer frequencyMhz;
    private Integer casLatency;
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
	
	public Long getRankSize() {
		return (long) module.getRank().getChip().getBankGroup().getBank().getCell().getColumns() 
				* module.getRank().getChip().getBankGroup().getBank().getCell().getRow()
				* module.getRank().getChip().getBankGroup().getBank().getAmount() 
				* module.getRank().getChip().getBankGroup().getAmount(); 
				
	}

	public Integer getWorldSize() {
		return 8 * module.getRank().getChip().getAmount();
	}
	
	@Override
	public String toString() {
		return "    Name=" + name + "\r\n"+
				"    Frequency=" + frequencyMhz+ "Mhz\r\n"+
				"    Theoretical Maximum Memory Bandwidth=" + calculateBandwidthInBytesPerSec()+ " Bytes/s - ("+calculateBandwidthInBytesPerSec()/1000000000D+"GB/s)\r\n"+
				"    RAM latency - Absolute Latency for Memory Accesses=" + calcMemoryLatency()+ " nanoseconds\r\n"+
				"    Total of Address=" + String.format("0x%08x",getTotalOfAddress() )+ "\r\n"+
				"    Channel Mode=" + channelMode + "\r\n" +
				"    Word Size=" + getWorldSize() + "\r\n" +
				"    Lines per Clock=" + linesPerClock + "\r\n" +
				"    Column Address Strobe (CAS) latency=" + casLatency+ "\r\n"+
				"    Module amount=" + module.getAmount() + "\r\n"+
				"    Rank amount=" + module.getRank().getAmount() + "\r\n" +
				"    Chip amount=" + module.getRank().getChip().getAmount() + "\r\n" +
				"    Bank Group amount=" + module.getRank().getChip().getBankGroup().getAmount() + "\r\n" +
				"    Bank amount=" + module.getRank().getChip().getBankGroup().getBank().getAmount() + "\r\n" +
				"    Cell=" + module.getRank().getChip().getBankGroup().getBank().getCell().getRow() + ", "+
				 module.getRank().getChip().getBankGroup().getBank().getCell().getColumns();
	}


	private Long calculateBandwidthInBytesPerSec() {
		/*
		 * Base DRAM clock frequency Number of data transfers per clock: Two, in the
		 * case of "double data rate" (DDR, DDR2, DDR3, DDR4) memory. Memory bus
		 * (interface) width: Each DDR, DDR2, or DDR3 memory interface is 64 bits wide.
		 * Those 64 bits are sometimes referred to as a "line." Number of interfaces:
		 * Modern personal computers typically use two memory interfaces (dual-channel
		 * mode) for an effective 128-bit bus width.
		 * 
		 * For example, a computer with dual-channel memory and one DDR2-800 module per
		 * channel running at 400 MHz would have a theoretical maximum memory bandwidth
		 * of:
		 * 
		 * 400,000,000 clocks per second × 2 lines per clock × 64 bits per line × 2
		 * interfaces = 102,400,000,000 (102.4 billion) bits per second (in bytes,
		 * 12,800 MB/s or 12.8 GB/s)
		 */
		Long calc = frequencyMhz * 1000000L;
		calc *= linesPerClock;
		calc *= getWorldSize();
		calc *= channelMode.getValue();
		return calc / Bits.BYTE_SIZE;
	}
	
	private Double calcMemoryLatency() {
		/*
		 * This is a RAM latency calculator. It takes the data rate (MHz) and cas
		 * latency (CL), then calculates the absolute latency for memory accesses in
		 * nanoseconds.
		 */
		
		//if( dataRate < 1.0 ) dataRate = 1.0;
		//if( casLatency < 1.0 ) casLatency = 1.0;
		return (1.0D / (frequencyMhz / 2.0D) * casLatency) * 1000;
	}
	
}
