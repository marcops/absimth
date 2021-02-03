package absimth.sim.gui.helper;

import javafx.event.Event;
import javafx.event.EventType;

public class AbsimthEvent extends Event {

	public static final EventType<AbsimthEvent> ABSIMTH_UPDATE_EVENT = new EventType<>(ANY, "ABSIMTH_UPDATE_EVENT");

	public AbsimthEvent(EventType<? extends AbsimthEvent> eventType) {
		super(eventType);
	}
}