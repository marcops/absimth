package absimth.module.memoryController.util.ecc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import absimth.exception.FixableErrorException;
import absimth.sim.utils.Bits;

public class LPC_64 implements IEccType {
	private int len_matrix = 8;
	private int hamming_parity[][] = {{0 , 1 , 3 , 4 , 6},
			{0 , 2 , 3 , 5 , 6},
			{1 , 2 , 3 , 7},
			{4 , 5 , 6 , 7}};
	
	    		  
	
	int discoverPosition(int a,int b, int c, int d) {
		//if(a==0 && b == 0 && c == 0 && d==0) return -1;
		if(a==1 && b == 1 && c == 0 && d==0) return 0;		
		if(a==1 && b == 0 && c == 1 && d==0) return 1;
		if(a==0 && b == 1 && c == 1 && d==0) return 2;
		if(a==1 && b == 1 && c == 1 && d==0) return 3;
		if(a==1 && b == 0 && c == 0 && d==1) return 4;
		if(a==0 && b == 1 && c == 0 && d==1) return 5;
		if(a==1 && b == 1 && c == 0 && d==1) return 6;
		if(a==0 && b == 0 && c == 1 && d==1) return 7;
		return -1;
	}
	@Override
	public Bits decode(Bits dataFull) throws Exception {
		if (dataFull.length() != 128) throw new Error("invalid bit size");
		Bits data = dataFull.subbit(0, 64);
		int[] ecc = calculateECC(data);
		//Bits b = Bits.sub (data.toIntArray());
		Bits calcEcc = Bits.from(ecc);
		Bits originalEcc = dataFull.subbit(64, 64);
		
		if(calcEcc.equals(originalEcc))
			return data;
		
		int []cECC = calcEcc.toIntArray();
		int []oECC = originalEcc.toIntArray();
		
		int []checkRow = new int[32];
		int []checkColumn = new int[32];
		
		for(int i=0;i<32;i++) {
			checkRow[i] = cECC[i]^oECC[i];
		}
		
		for(int i=0;i<32;i++) {
			checkColumn[i] = cECC[i+32]^oECC[i+32];
		}
		
		int []cRow = new int[len_matrix];
		int []cColumn = new int[len_matrix];
//		
		for (int i = 0,j=0; i < 32; i+=4,j++) {
			cRow[j] = checkRow[i]|checkRow[i+1]|checkRow[i+2]|checkRow[i+3];
		}
		for (int i = 0,j=0; i < 32; i+=4,j++) {
			cColumn[j] = checkColumn[i]|checkColumn[i+1]|checkColumn[i+2]|checkColumn[i+3];
		}
		
		int tRow = 0;
		for(int i=0;i<4;i++) tRow += cRow[i];
		int tColumn = 0;
		for(int i=0;i<4;i++) tColumn += cColumn[i];
		
		Set<Integer> errors = new HashSet<>();
		
		if(tRow>=tColumn) {
			//fix by row
			//int p[] = new int[len_matrix];
			for(int i=0,j=0;j<len_matrix;i+=4,j++) {
				int p = discoverPosition(checkRow[i],checkRow[i+1], checkRow[i+2],checkRow[i+3]);
				if(p!=-1) {
					int cp = p+(j*len_matrix);
					errors.add(cp);
					data.set(cp, !data.get(cp));
				}
			}			
			
		} else{ 
			//fix by column			
			for(int i=0,j=0;j<len_matrix;i+=4,j++) {
				int p = discoverPosition(checkColumn[i],checkColumn[i+1], checkColumn[i+2],checkColumn[i+3]);
				if(p!=-1) {
					int cp = (p*len_matrix)+j;
					errors.add(cp);
					data.set(cp, !data.get(cp));
				}
			}	
		}
		

		throw new FixableErrorException(dataFull , data, errors);
	}

	@Override
	public Bits encode(Bits data) {
		if (data.length() != 64) throw new Error("invalid bit size");
		int[] ecc = calculateECC(data);
		Bits b = Bits.from(data.toIntArray());
		Bits bEcc = Bits.from(ecc);
		return b.append(bEcc);
	}

	private int[] calculateECC(Bits data) {
		int[] idata = data.toIntArray();
		
		int dataRows[][] = getRows(idata, len_matrix);
		int dataColumns[][] = getColumns(idata, len_matrix);
		
		int []ecc = new int[64];
		int p=0;
		for(int i=0;i<dataRows.length;i++) {
			for(int j=0;j<hamming_parity.length;j++) {
				ecc[p] = calcParity(dataRows[i], hamming_parity[j]);
				p++;
			}			
		}
		
		for(int i=0;i<dataColumns.length;i++) {
			for(int j=0;j<hamming_parity.length;j++) {
				ecc[p] = calcParity(dataColumns[i], hamming_parity[j]);
				p++;
			}			
		}
		return ecc;
	}
	
	private int calcParity(int[] bits, int[] parity) {
		int x = bits[parity[0]];
		for(int i=1;i<parity.length;i++)
			x ^= bits[parity[i]];
		return x;
	}

	private int[][] getColumns(int[] idata, int len_hparity) {
		int[][] crows = new int[len_hparity][len_hparity];
		for (int i = 0; i < idata.length; i++) {
			int row_position = i / len_hparity;
			int column_position = i - (row_position * len_hparity);
			crows[column_position][row_position] = idata[i];
		}
		return crows;
	}

	private int[][] getRows(int[] idata, int len_hparity) {
		int [][] crows = new int[len_hparity][len_hparity];
		for(int i=0;i<idata.length;i++) {
			int row_position = i/len_hparity;
			int column_position = i-(row_position*len_hparity);
			crows[row_position][column_position] = idata[i];
		}
		return crows;
	}
	
}
