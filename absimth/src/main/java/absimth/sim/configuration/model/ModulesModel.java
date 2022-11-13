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
	private MemoryFaultModel memoryFaultInjection;
	
	@Override
	public String toString() {
		return "MEMORY\r\n"+
				"  Controller:" + memoryController + "\r\n"+
				"  Fault Injection:" + memoryFaultInjection+"\r\n";
	}

	public String toSmall() {
		return memoryController.replace("MemoryController", "") + ";" + (memoryFaultInjection.getConfig().contains("-1;true;3;2;16;100") == true ? "R" : "C");
	}
}



