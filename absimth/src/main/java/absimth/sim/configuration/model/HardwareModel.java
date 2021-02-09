package absimth.sim.configuration.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import absimth.sim.utils.HexaFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class HardwareModel {
	private List<CPUModel> cpu;
	private MemoryConfModel memory;
	private Integer peripheralAddressSize;

	@Override
	public String toString() {
		String str = "Reserved Peripheral Address size for Modules:" + HexaFormat.f(peripheralAddressSize) + " \r\n";
		for (int i = 0; i < cpu.size(); i++)
			str += " CPU\r\n" + cpu.get(i) + "\r\n\r\n";
		return str + "MEMORY\r\n" + memory;
	}

}
