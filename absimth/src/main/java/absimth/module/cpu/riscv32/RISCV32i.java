package absimth.module.cpu.riscv32;

import java.util.Arrays;

import absimth.sim.cpu.ICPU;
import absimth.sim.cpu.ICPUInstruction;
import absimth.sim.os.model.IOSMemoryAccess;
import absimth.sim.utils.AbsimLog;
import lombok.Getter;
import lombok.Setter;


public class RISCV32i extends ICPU {
	//TODO USAR RV32ICPUState
	@Getter(onMethod_={@Override})
	@Setter(onMethod_={@Override})
	protected int pc = 0; // Program counter
	//TODO REMOVE PREVPC
	@Getter(onMethod_={@Override})
	@Setter(onMethod_={@Override})
	protected int prevPc; // Previous pc
	@Setter(onMethod_={@Override})
	@Getter(onMethod_={@Override})
	protected int[] reg = new int[32]; // RISC-V registers x0 to x31
	protected IOSMemoryAccess memAccess;
	
	@Override
	public void initializeRegisters(int _stackSize, int initialAddress) {
		reg[2] = _stackSize;
		reg[3] = initialAddress;
		//TODO MELHORAR ISTO
//		memory.setInitialAddress(initialAddress);
	}

	/**
	 * Executes one instruction given by the Instruction array 'program' at index
	 * given by the program counter 'pc'. Uses the opcode field of the instruction
	 * to determine which type of instruction it is and call that method.
	 * return false if has more instruction, true if has more todo
	 * @throws Exception 
	 */
	@Override
	public String executeInstruction(Integer data, IOSMemoryAccess _memAccess) throws Exception {
		reg[0] = 0; // x0 must always be 0
		//todo IMPROVE, create a interface here
		memAccess = _memAccess;
		prevPc = pc;
		RV32IInstruction inst = new RV32IInstruction();
		inst.loadInstruction(data == null ? memAccess.getWord(pc*4) : data.intValue());
		AbsimLog.instruction(inst.assemblyString);
		return doInstruction(inst);
	}

	

	private String doInstruction(RV32IInstruction inst) throws Exception {
		switch (inst.opcode) {
		// R-type instructions
		case 0b0110011: // ADD / SUB / SLL / SLT / SLTU / XOR / SRL / SRA / OR / AND
			rType(inst);
			break;

		// J-type instruction
		case 0b1101111: // JAL
			reg[inst.rd] = (pc + 1) << 2; // Store address of next instruction in bytes
			pc += inst.imm >> 2;
			break;

		// I-type instructions
		case 0b1100111: // JALR
			reg[inst.rd] = (pc + 1) << 2;
			pc = ((reg[inst.rs1] + inst.imm) & 0xFFFFFFFE) >> 2;
			break;
		case 0b0000011: // LB / LH / LW / LBU / LHU
			iTypeLoad(inst);
			break;
		case 0b0010011: // ADDI / SLTI / SLTIU / XORI / ORI / ANDI / SLLI / SRLI / SRAI
			iTypeInteger(inst);
			break;
		case 0b1110011: // ECALL
			return iTypeEcall();

		// S-type instructions
		case 0b0100011: // SB / SH / SW
			sType(inst);
			break;

		// B-type instructions
		case 0b1100011: // BEQ / BNE / BLT / BGE / BLTU / BGEU
			bType(inst);
			break;

		// U-type instructions
		case 0b0110111: // LUI
			reg[inst.rd] = inst.imm;
			pc++;
			break;
		case 0b0010111: // AUIPC
			reg[inst.rd] = (pc << 2) + inst.imm; // Shift pc because we count in 4 byte words
			pc++;
			break;
		default:
			System.err.println("executeInstruction " + inst.opcode);
			throw new Exception("11.Wrong instruction " + inst.opcode + " at executeInstruction");
//			break;
		}
		return null;
	}
	
	/**
	 * Handles execution of r-Type instructions: ADD / SUB / SLL / SLT / SLTU / XOR
	 * / SRL / SRA / OR / AND
	 * @throws Exception 
	 */
	private void rType(RV32IInstruction inst) throws Exception {
		switch (inst.funct3) {
		case 0b000: // ADD / SUB
			switch (inst.funct7) {
			case 0b0000000: // ADD
				reg[inst.rd] = reg[inst.rs1] + reg[inst.rs2];
				break;
			case 0b0100000: // SUB
				reg[inst.rd] = reg[inst.rs1] - reg[inst.rs2];
				break;
			default:
				System.err.println("rType 0b0000000:" + inst.funct7);
				throw new Exception("10.Wrong instruction " + inst.opcode);
			}
			break;
		case 0b001: // SLL
			reg[inst.rd] = reg[inst.rs1] << reg[inst.rs2];
			break;
		case 0b010: // SLT
			if (reg[inst.rs1] < reg[inst.rs2])
				reg[inst.rd] = 1;
			else
				reg[inst.rd] = 0;
			break;
		case 0b011: // SLTU
			if (Integer.toUnsignedLong(reg[inst.rs1]) < Integer.toUnsignedLong(reg[inst.rs2]))
				reg[inst.rd] = 1;
			else
				reg[inst.rd] = 0;
			break;
		case 0b100: // XOR
			if(inst.funct7 == 0b000)
				reg[inst.rd] = reg[inst.rs1] ^ reg[inst.rs2];
			else { 
				System.err.println("rType 0b0000000 0b100:" + inst.funct7);
				throw new Exception("9.Wrong instruction " + inst.opcode);
			}
			
			break;
		case 0b101: // SRL / SRA
			switch (inst.funct7) {
			case 0b0000000: // SRL
				reg[inst.rd] = reg[inst.rs1] >>> reg[inst.rs2];
				break;
			case 0b0100000: // SRA
				reg[inst.rd] = reg[inst.rs1] >> reg[inst.rs2];
				break;
			default:
				System.err.println("rType 0b101:" + inst.funct7);
				throw new Exception("8.Wrong instruction " + inst.opcode);
			}
			break;
		case 0b110: // OR
			reg[inst.rd] = reg[inst.rs1] | reg[inst.rs2];
			break;
		case 0b111: // AND
			reg[inst.rd] = reg[inst.rs1] & reg[inst.rs2];
			break;
		default:
			System.err.println("rType: " + inst.funct3);
			throw new Exception("7.Wrong instruction " + inst.opcode);
		}
		pc++;
	}

	/**
	 * Handles execution of i-Type load instructions: LB / LH / LW / LBU / LHU
	 * @throws Exception 
	 */
	private void iTypeLoad(RV32IInstruction inst) throws Exception {
		int addr = reg[inst.rs1] + inst.imm; // Byte address

		switch (inst.funct3) {
		case 0b000: // LB
			reg[inst.rd] = memAccess.getByte(addr);
			break;
		case 0b001: // LH
			reg[inst.rd] = memAccess.getHalfWord(addr); 
			break;
		case 0b010: // LW
			reg[inst.rd] = memAccess.getWord(addr);
			break;
		case 0b100: // LBU
			reg[inst.rd] = memAccess.getByte(addr) & 0xFF; // Remove sign bits
			break;
		case 0b101: // LHU
			reg[inst.rd] = memAccess.getHalfWord(addr) & 0xFFFF;
			break;
		default:
			throw new Exception("6.Wrong instruction " + inst.opcode);
		}
		pc++;
	}

	/**
	 * Handles execution of I-type integer instructions: ADDI / SLTI / SLTIU / XORI
	 * / ORI / ANDI / SLLI / SRLI / SRAI
	 * @throws Exception 
	 */
	private void iTypeInteger(RV32IInstruction inst) throws Exception {
		switch (inst.funct3) {
		case 0b000: // ADDI
			reg[inst.rd] = reg[inst.rs1] + inst.imm;
			break;
		case 0b010: // SLTI
			if (reg[inst.rs1] < inst.imm)
				reg[inst.rd] = 1;
			else
				reg[inst.rd] = 0;
			break;
		case 0b011: // SLTIU
			if (Integer.toUnsignedLong(reg[inst.rs1]) < Integer.toUnsignedLong(inst.imm))
				reg[inst.rd] = 1;
			else
				reg[inst.rd] = 0;
			break;
		case 0b100: // XORI
			reg[inst.rd] = reg[inst.rs1] ^ inst.imm;
			break;
		case 0b110: // ORI
			reg[inst.rd] = reg[inst.rs1] | inst.imm;
			break;
		case 0b111: // ANDI
			reg[inst.rd] = reg[inst.rs1] & inst.imm;
			break;
		case 0b001: // SLLI
			reg[inst.rd] = reg[inst.rs1] << inst.imm;
			break;
		case 0b101: // SRLI / SRAI
			int ShiftAmt = inst.imm & 0x1F; // The amount of shifting done by SRLI or SRAI
			switch (inst.funct7) {
			case 0b0000000: // SRLI
				reg[inst.rd] = reg[inst.rs1] >>> ShiftAmt;
				break;
			case 0b0100000: // SRAI
				reg[inst.rd] = reg[inst.rs1] >> ShiftAmt;
				break;
			default:
				System.err.println("iTypeInteger 0b0000000:"+inst.funct7);
				throw new Exception("5.Wrong instruction ");
			}
			break;
		default:
			System.err.println("iTypeInteger:"+inst.funct3);
			throw new Exception("4.Wrong instruction ");
		}
		pc++;
	}

	/**
	 * Handles execution of i-Type ECALL instructions
	 * @throws Exception 
	 */
	private String iTypeEcall() throws Exception {
		pc++;
		switch (reg[10]) {
		case 1: // print_int
			//SimulatorManager.getSim().setTextRiscV(
			return String.valueOf(reg[11]);
		case 2: // print_float
			return String.format("%.6f ", RISCV32f.intRepresentation2float(reg[11]));
		case 3: // putchar
			return String.valueOf((char) reg[11]);
		case 4: // print_string
			return memAccess.getString(reg[11]);
		case 9: // sbrk
			// not sure if we can do this?
			return null;
		case 10: // exit
//			System.out.println("PROGRAM FINISHE CORRECTLY - 10pc = program.length;");
			pc=-1;
//			pc = program.length; // Sets program counter to end of program, to program loop
			return null; // Exits 'iTypeStatus' function and returns to loop.
		case 11: // print_character
			return String.valueOf((char) reg[11]);
		case 17: // exit2
//			pc = program.length;
			pc=-1;
//			System.out.println("17pc = program.length;");
			// System.out.println("Return code: " + reg[11]); // Prints a1 (should be
			// return?)
			return null;
		default:
			System.out.println("ECALL " + reg[10] + " not implemented");
			throw new Exception("3.Wrong instruction ");
		}
	}

	/**
	 * Handles the S-type instructions: SB / SH / SW
	 * @throws Exception 
	 */
	private void sType(RV32IInstruction inst) throws Exception {
		int addr = reg[inst.rs1] + inst.imm;
		switch (inst.funct3) {
		case 0b000: // SB
			memAccess.storeByte(addr, (byte) reg[inst.rs2]);
			break;
		case 0b001: // SH
			memAccess.storeHalfWord(addr, (short) reg[inst.rs2]);
			break;
		case 0b010: // SW
			memAccess.storeWord(addr, reg[inst.rs2]);
			break;
		default:
			System.err.println("sType: "+ inst.funct3);
			throw new Exception("2.Wrong instruction ");
		}
		pc++;
	}
//	@Override
//	public void storeInstruction(int address, int data) throws Exception {
//		memory.storeWord(getVirtualAddress(address), data);
//	}
	/**
	 * Handles the B-type instructions: BEQ / BNE / BLT / BGE / BLTU / BGEU
	 * @throws Exception 
	 */
	private void bType(RV32IInstruction inst) throws Exception {
		int Imm = inst.imm >> 2; // We're counting in words instead of bytes
		switch (inst.funct3) {
		case 0b000: // BEQ
			pc += (reg[inst.rs1] == reg[inst.rs2]) ? Imm : 1;
			break;
		case 0b001: // BNE
			pc += (reg[inst.rs1] != reg[inst.rs2]) ? Imm : 1;
			break;
		case 0b100: // BLT
			pc += (reg[inst.rs1] < reg[inst.rs2]) ? Imm : 1;
			break;
		case 0b101: // BGE
			pc += (reg[inst.rs1] >= reg[inst.rs2]) ? Imm : 1;
			break;
		case 0b110: // BLTU
			pc += (Integer.toUnsignedLong(reg[inst.rs1]) < Integer.toUnsignedLong(reg[inst.rs2])) ? Imm : 1;
			break;
		case 0b111: // BLGEU
			pc += (Integer.toUnsignedLong(reg[inst.rs1]) >= Integer.toUnsignedLong(reg[inst.rs2])) ? Imm : 1;
			break;
		default:
			System.err.println("bType: "+inst.funct3);
			throw new Exception("1.Wrong instruction ");
		}
	}
	
	@Override
	public String toString() {
		return "pc=" + pc + ", " + " reg=" + Arrays.toString(reg) + "";
	}

	@Override
	public String getName() {
		return "RISCV32i";
		
	}

	@Override
	public ICPUInstruction getInstruction(int data) {
		RV32IInstruction inst = new RV32IInstruction();
		inst.loadInstruction(data);
		return inst;
	}
}
