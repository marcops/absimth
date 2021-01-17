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
public class HardwareModel {
	private CPUModel cpu;
	private MemoryConfModel memory;

	@Override
	public String toString() {
		return " CPU\r\n" + cpu + "\r\n\r\n"+
				"MEMORY\r\n" + memory;
	}

}
