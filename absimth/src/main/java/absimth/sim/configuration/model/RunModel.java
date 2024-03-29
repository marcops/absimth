package absimth.sim.configuration.model;

import java.util.List;

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
public class RunModel {
	private Integer cyclesByProgram;
	private List<ProgramModel> programs;

	@Override
	public String toString() {
		String str =  "Operational System \r\n" +
				"Cycles by Program:" + cyclesByProgram + "\r\n"+
				"RUNNING...\r\n";
		for (ProgramModel programModel : programs) {
			str += "  "+programModel.toString() + "\r\n";
		}
		return str;
	}
	
	
}
