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
//	private HardwareConfModel hardware;

	@Override
	public String toString() {
		return "Programs to run [\r\n" + run + "]";
	}
	
}