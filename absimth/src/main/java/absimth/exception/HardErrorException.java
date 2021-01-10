package absimth.exception;

import absimth.sim.Bits;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HardErrorException extends Exception {
	private final Bits input;
}