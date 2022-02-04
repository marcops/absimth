package absimth.sim.gui.model;

import java.util.Map;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

//@Data
@Builder
public class TimelineDataSet {
	@Getter
	@Setter
	private Integer start;
	@Getter
	@Setter
	private Integer end;
	private Map<Integer, String> entries;
	@Getter
	private int maxEntryPosition;
	
	
	public String get(int pos) {
		return entries.get(pos);
	}
	public void add(int position, String val) {
		entries.put(position, val);
		if (position > maxEntryPosition) maxEntryPosition = position;
	}
	public Map<Integer, String> getEntriesFiltred(int init, int end) {
		return entries
			.entrySet().stream()
			.filter(x -> x.getKey() >= init && x.getKey() < end)
		    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
