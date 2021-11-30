package absimth.exception;

import java.util.Set;

import absimth.sim.utils.Bits;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnfixableErrorException extends Exception {
	private static final long serialVersionUID = 1L;
	private final Bits input;
	private final Set<Integer> position;
}
