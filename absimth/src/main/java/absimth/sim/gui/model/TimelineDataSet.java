package absimth.sim.gui.model;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimelineDataSet {
	private Integer start;
	private Integer end;
	private Map<Integer, String> entries;
}
