package absimth.sim.faultInjection;

import java.util.Random;

import absimth.sim.gui.guiController;

public class AroundFaultMode {

	private long lastPosition = -1;
	
	public long exec() {
		Integer maxAddress = guiController.MEMORY_SIZE;
		if(lastPosition<0) return new Random().nextInt(maxAddress);
		Integer dist = 100;
		Integer size = maxAddress/(dist*100);
		Integer nPos = new Random().nextInt(size*2);
		lastPosition = (lastPosition-size) + nPos;
		return lastPosition;
	}

}
