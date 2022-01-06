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
public class MemoryFaultModel {
	private String name;
	private String config;
	
	@Override
	public String toString() {
		return "MemoryFault\r\n"+
				"  Name=" + name + "\r\n"+
				"  Config=" + config+"\r\n";
	}
}



