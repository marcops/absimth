package absimth.exception;

import absimth.sim.memory.faultInjection.model.Bits;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SoftErrorException extends Exception {
	private final Bits input;
	private final Bits recovered;
}
