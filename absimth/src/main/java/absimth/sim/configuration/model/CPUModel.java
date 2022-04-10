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
public class CPUModel {
	private Integer amount;
	private String name;

	@Override
	public String toString() {
		return  "  Processors Model:" + name + "\r\n" +
				"  Number Of Processors:" + amount+ "\r\n" +
				"  Running at same frequency of memory ";
	}

}
