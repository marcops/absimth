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
public class AbsimthConfigurationModel {
	private RunModel run;
	private HardwareModel hardware;
	private LogModel log;
	private ModulesModel modules;
	
	@Override
	public String toString() {
		return "------ HARDWARE ------\r\n" + hardware + "\r\n\r\n" +
			   "------ CUSTOM MODULES LOADED ------\r\n" + modules + "\r\n" +
			   "------ PROGRAMS LOADED ------\r\n" + run + "\r\n";
	}
	
}
