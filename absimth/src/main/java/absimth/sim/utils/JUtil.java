package absimth.sim.utils;

import java.util.HashSet;
import java.util.Set;

public class JUtil {
	public static Set<Integer> createSet(int length){
		Set<Integer> set = new HashSet<>();
		for(int i=0;i<length;i++) set.add(i);
		return set;
	}
}
