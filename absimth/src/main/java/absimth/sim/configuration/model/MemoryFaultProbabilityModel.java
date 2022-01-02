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
public class MemoryFaultProbabilityModel {
	private Double probabilityRate;
	private Long initialAddress;
	private Boolean errorOnlyInUsedMemory;
	private Integer nearErrorRange;
	
	//
	private Integer radiusIntensity;
	private Integer angle;
	private Double declineIntensity;
	private Double declineRadius;

//	@Override
//	public String toString() {
//		return "MEMORY\r\n"+
//				"  Controller=" + memoryController + "\r\n"+
//				"  Fault Injection=" + memoryFaultInjection+"\r\n";
//	}
}
