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
public class ModulesModel {
	private String memoryController;
	private String memoryFaultInjection;
	
	@Override
	public String toString() {
		return "MEMORY\r\n"+
				"  Controller=" + memoryController + "\r\n"+
				"  Fault Injection=" + memoryFaultInjection+"\r\n";
	}
}



