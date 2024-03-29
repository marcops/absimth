package absimth.module.cpu.riscv32;

import absimth.module.cpu.riscv32.module.RV32Cpu2Mem;
import absimth.sim.cpu.ICPUInstruction;
import lombok.Getter;

public class RV32IInstruction implements ICPUInstruction {
	protected int instruction;
	protected int opcode;
	@Getter(onMethod_={@Override})
	protected int rd;
	@Getter(onMethod_={@Override})
	protected int rs1;
	@Getter(onMethod_={@Override})
	protected int rs2;
	protected int funct3;
	protected int funct7;
	@Getter(onMethod_={@Override})
	protected int imm;
	@Getter(onMethod_={@Override})
	protected boolean noRd = false;
	@Getter(onMethod_={@Override})
	protected boolean sType = false;
	@Getter(onMethod_={@Override})
	protected boolean ecall = false;
	@Getter(onMethod_={@Override})
	protected String assemblyString;

	public void loadInstruction(int instruction) {
		// Used in nearly all
		preDecodeInstruction(instruction);
		// Immediate is different for all types
		decodeInstruction(instruction);
	}

	protected void decodeInstruction(int instruction) {
		switch (opcode) {
		case 0b1101111: // J-type
			this.imm = getImmJ(instruction);
			break;
		case 0b1100111: // I-type
		case 0b0000011:
		case 0b0010011:
			this.imm = (instruction >> 20); // bits 31 to 20
			this.funct7 = (instruction >> 25) & 0x7F; // bits 31 to 25
			// No break since I-type also uses funct7 in shift instructions
			break;
		case 0b0110011: // R-type
			this.funct7 = (instruction >> 25) & 0x7F; // bits 31 to 25
			break;
		case 0b0100011: // S-type
			imm = (((instruction >> 20) & 0xFFFFFFE0) | ((instruction >>> 7) & 0x0000001F)); // Returns bits 31 to 25
																								// and 11 to 7
			break;
		case 0b1100011: // B-type
			this.imm = getImmB(instruction);
			break;
		case 0b0110111:// U-type
		case 0b0010111:
			this.imm = instruction & 0xFFFFF000;
			break;
		case 0b1110011:
		// R-type and ECALL doesn't have an immediate
			break;
		default:
			System.err.println("opcode :"+ Integer.toBinaryString(opcode));
			break;
		}

		this.assemblyString = toAssemblyString(); // The instruction show in assembly code
	}

	protected void preDecodeInstruction(int instruction) {
		this.instruction = instruction;
		this.opcode = instruction & 0x7F; // First 7 bits
		this.rd = (instruction >> 7) & 0x1F; // bits 11 to 7
		this.funct3 = (instruction >> 12) & 0x7; // bits 14 to 12
		this.rs1 = (instruction >> 15) & 0x1F; // bits 19 to 15
		this.rs2 = (instruction >> 20) & 0x1F; // bits 24 to 20
	}

	/**
	 * Returns the B-type immediate Decoded like this: imm[12|10:5|4:1|11]
	 */
	private static int getImmB(int instruction) {
		return ((((((instruction >>> 7) & 0x0000001F) | (instruction >> 20) & 0xFFFFFFE0)) & 0xFFFFF7FE)
				| (((((instruction >> 20) & 0xFFFFFFE0) | ((instruction >>> 7) & 0x0000001F)) & 0x00000001) << 11));
	}

	/**
	 * Returns the J-type immediate Decoded like this: imm[20|10:1|11|19:12]
	 */
	private static int getImmJ(int instruction) {
		int b12to19 = (instruction >> 12) & 0xFF; // Bits 12 to 19 of immediate (12 to 19 of instruction)
		int b11 = (instruction >> 20) & 0x1; // Bit 11 of immediate (20th bit of instruction)
		int b1to10 = (instruction >> 21) & 0x3FF; // Bit 1 to 10 of immediate (21 to 30 of instruction)
		int b20 = (instruction >> 31); // Bit 20 of immediate (MSB of instruction)
		return (b20 << 20 | b12to19 << 12 | b11 << 11 | b1to10 << 1);
	}

	/**
	 * Converts the instruction to an assembly string. Returns the string
	 */
	protected String toAssemblyString() {
		String instr = "", arg1 = "", arg2 = "", arg3 = "";
		switch (opcode) {
		// R-type instructions
		case 0b0110011: // ADD / SUB / SLL / SLT / SLTU / XOR / SRL / SRA / OR / AND
			arg1 = String.format("x%d", rd);
			arg2 = String.format("x%d", rs1);
			arg3 = String.format("x%d", rs2);
			switch (funct3) {
			case 0b000: // ADD / SUB
				switch (funct7) {
				case 0b0000000: // ADD
					instr = "add";
					break;
				case 0b0100000: // SUB
					instr = "sub";
					break;
				default:
					System.err.println("opcode 0b000:"+ funct7);
					break;
				}
				break;
			case 0b001: // SLL
				instr = "sll";
				break;
			case 0b010: // SLT
				instr = "slt";
				break;
			case 0b011: // SLTU
				instr = "sltu";
				break;
			case 0b100: // XOR
				switch (funct7) {
				case  0b0000000:
					instr = "xor";
					break;
					default:
						System.err.println("opcode 0b0110011 0b100:"+ funct7);
					break;
				}
				break;
			case 0b101: // SRL / SRA
				switch (funct7) {
				case 0b0000000: // SRL
					instr = "srl";
					break;
				case 0b0100000: // SRA
					instr = "sra";
					break;
				default:
					System.err.println("opcode 0b101: "+ funct7);
					break;
				}
				break;
			case 0b110: // OR
				instr = "or";
				break;
			case 0b111: // AND
				instr = "and";
				break;
			default:
				System.err.println("opcode 0b0110011 : "+ funct3);
				break;
			}
			break;
		case 0b1101111: // JAL
			arg1 = String.format("x%d", rd);
			arg2 = String.format("x%d", imm);
			instr = "jal";
			break;
		case 0b1100111: // JALR
			arg1 = String.format("x%d", rd);
			arg2 = String.format("x%d", imm);
			instr = "jalr";
			break;
		case 0b0000011: // LB / LH / LW / LBU / LHU
			arg1 = String.format("x%d", rd);
			arg2 = String.format("%d(x%d)", imm, rs1);
			switch (funct3) {
			case 0b000: // LB
				instr = "lb";
				break;
			case 0b001: // LH
				instr = "lh";
				break;
			case 0b010: // LW
				instr = "lw";
				break;
			case 0b100: // LBU
				instr = "lbu";
				break;
			case 0b101: // LHU
				instr = "lhu";
				break;
			default:
				System.err.println("opcode 0b0000011 : "+ funct3);
				break;
			}
			break;
		case 0b0010011: // ADDI / SLTI / SLTIU / XORI / ORI / ANDI / SLLI / SRLI / SRAI
			arg1 = String.format("x%d", rd);
			arg2 = String.format("x%d", rs1);
			arg3 = String.format("%d", imm);
			switch (funct3) {
			case 0b000: // ADDI
				instr = "addi";
				break;
			case 0b010: // SLTI
				instr = "slti";
				break;
			case 0b011: // SLTIU
				instr = "sltiu";
				break;
			case 0b100: // XORI
				instr = "xori";
				break;
			case 0b110: // ORI
				instr = "ori";
				break;
			case 0b111: // ANDI
				instr = "andi";
				break;
			case 0b001: // SLLI
				instr = "slli";
				break;
			case 0b101: // SRLI / SRAI
				switch (funct7) {
				case 0b0000000: // SRLI
					instr = "srli";
					break;
				case 0b0100000: // SRAI
					instr = "srai";
					break;
				default:
					System.err.println("opcode 0b0010011  funct7: "+ funct7);
					break;
				}
				break;
			default:
				System.err.println("opcode 0b0010011  : "+ funct3);
				break;
			}
			break;
		case 0b0001111: // FENCE / FENCE.I
			switch (funct3) {
			case 0b000: // FENCE
				instr = "fence";
				break;
			case 0b001: // FENCE.I
				instr = "fence.i";
				break;
			default:
				System.err.println("opcode 0b0001111  funct7: "+ funct7);
				break;
			}
			break;
		case 0b1110011: // ECALL / EBREAK / CSRRW / CSRRS / CSRRC / CSRRWI / CSRRSI / CSRRCI
			switch (funct3) {
			case 0b000: // ECALL / EBREAK
				switch (imm) {
				case 0b000000000000: // ECALL
					instr = "ecall";
					ecall = true;
					break;
				case 0b000000000001: // EBREAK
					instr = "ebreak";
					break;
				default:
					System.err.println("opcode 0b1110011  0b000: "+ funct7);
					break;
				}
				break;
			case 0b001: // CSRRW
				instr = "csrrw";
				break;
			case 0b010: // CSRRS
				instr = "csrrs";
				break;
			case 0b011: // CSRRC
				instr = "csrrc";
				break;
			case 0b101: // CSRRWI
				instr = "csrrwi";
				break;
			case 0b110: // CSRRSI
				instr = "csrrsi";
				break;
			case 0b111: // CSRRCI
				instr = "csrrci";
				break;
			default:
				System.err.println("opcode 0b1110011: "+ funct3);
				break;
			}
			noRd = true;
			break;

		// S-type instructions
		case 0b0100011: // SB / SH / SW
			arg1 = String.format("x%d", rs2);
			arg2 = String.format("%d(x%d)", imm, rs1);
			switch (funct3) {
			case 0b000: // SB
				instr = "sb";
				break;
			case 0b001: // SH
				instr = "sh";
				break;
			case 0b010: // SW
				instr = "sw";
				break;
			default:
				System.err.println("0b0100011");
				break;
			}
			noRd = true;
			sType = true;
			break;

		// B-type instructions
		case 0b1100011: // BEQ / BNE / BLT / BGE / BLTU / BGEU
			arg1 = String.format("x%d", rs1);
			arg2 = String.format("x%d", rs2);
			arg3 = String.format("%d", imm);
			switch (funct3) {
			case 0b000: // BEQ
				instr = "beq";
				break;
			case 0b001: // BNE
				instr = "bne";
				break;
			case 0b100: // BLT
				instr = "blt";
				break;
			case 0b101: // BGE
				instr = "bge";
				break;
			case 0b110: // BLTU
				instr = "bltu";
				break;
			case 0b111: // BLGEU
				instr = "blgeu";
				break;
			default:
				System.err.println("0b1100011");
				break;
			}
			noRd = true;
			break;

		// U-type instructions
		case 0b0110111: // LUI
			arg1 = String.format("x%d", rd);
			arg2 = String.format("%d", imm >>> 12);
			instr = "lui";
			break;
		case 0b0010111: // AUIPC
			arg1 = String.format("x%d", rd);
			arg2 = String.format("%d", imm >>> 12);
			instr = "auipc";
			break;
		default:
			return unrecognized();
		}
		if(instr.isBlank()) return unrecognized();
		return String.format("%s %s %s %s", instr, arg1, arg2, arg3);
	}

	protected String unrecognized() {
		try {
			byte b[] = RV32Cpu2Mem.splitBytes(instruction);
			return String.format("Unrecognized: 0x%08x [%c%c%c%c]", instruction, b[0], b[1],b[2],b[3]);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return String.format("Unrecognized: 0x%08x ", instruction);
		}
	}
}
