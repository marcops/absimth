//package absimth.sim.faultInjection;
//
//import java.util.Random;
//
//import absimth.sim.SimulatorManager;
//
//public class AroundFaultMode {
//
//	private long lastPosition = -1;
//	
//	public long exec() {
//		Integer maxAddress = SimulatorManager.MAX;
//		if(lastPosition<0) return new Random().nextInt(maxAddress);
//		Integer dist = 100;
//		Integer size = maxAddress/(dist*100);
//		Integer nPos = new Random().nextInt(size*2);
//		lastPosition = (lastPosition-size) + nPos;
//		return lastPosition;
//	}
//
//}

//TODO LEVAR EM CONSIDERACAO o CHIP?
