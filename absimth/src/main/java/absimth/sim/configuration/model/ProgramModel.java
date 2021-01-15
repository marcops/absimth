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
public class ProgramModel {
	private Integer cpu;
	private String name;
	@Override
	public String toString() {
		return String.format(" at cpu%02d, program=%s", cpu, name);
	}
	
}
