///* File: guiController.java
// * Authors: Marc Sun Bøg & Simon Amtoft Pedersen
// *
// * The following file handles the flow of the entire program. 
// * The methods in this file are mostly GUI methods, which uses methods from other files.
// */
//
//package absimth.sim.gui;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.URL;
//import java.util.ResourceBundle;
//
//import absimth.sim.SimulatorManager;
//import absimth.sim.cpu.riscv32i.RV32IInstruction;
//import absimth.sim.os.OperationalSystem;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.Initializable;
//import javafx.scene.control.Button;
//import javafx.scene.control.MenuItem;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.scene.control.TextArea;
//import javafx.scene.control.TextField;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.layout.VBox;
//import javafx.stage.FileChooser;
//import javafx.stage.Stage;
//
//public class MemoryController implements Initializable {
//	// CONSTANTS
//	private static final int BYTES_PR_PAGE = 256; // 64 words
//	//TODO MOVER to OS
//	public static final int MEMORY_SIZE = 10485760; // 10MiB memory
//
//	// Keeping track of memory table
//	private int tableRootAddress = 0;
//
//	// FXML ELEMENTS
//	private Stage primaryStage;
//
//	// UI elements
//	public VBox mainVBox;
//	public MenuItem menuItemOpen;
//	public MenuItem menuItemExit;
//
//	// Input
//	public Button buttonNext;
////	public Button buttonPrevious;
//	public Button buttonRun;
//	public Button buttonReset;
//	public Button buttonNextTable;
//	public Button buttonPreviousTable;
//	public TextField textFieldAddr;
//
//	// Output
//	public TextArea textFieldConsole;
//
//	// Table elements
//	public TableView<TableHelper> registerTable;
//	public TableColumn<TableHelper, String> registerColumn;
//	public TableColumn<TableHelper, String> registerValueColumn;
//	public TableView<TableHelper> memoryTable;
//	public TableColumn<TableHelper, String> memoryColumn;
//	public TableColumn<TableHelper, String> memoryDataColumn;
//	public TableView<TableHelper> programTable;
//	public TableColumn<TableHelper, String> programColumn;
//	public TableColumn<TableHelper, String> programInstructionColumn;
//
//	// Table selection
//	private TableView.TableViewSelectionModel<TableHelper> pcSelection;
//	private TableView.TableViewSelectionModel<TableHelper> regSelection;
//	private TableView.TableViewSelectionModel<TableHelper> memSelection;
//
//	// Controller variables
//	private OperationalSystem os = OperationalSystem.getOS();
//	private File fileRunning; 
//	// History keeping for stepping back and forth
////	private ArrayList<int[]> regHistory = new ArrayList<>();
////	private ArrayList<Integer> pcHistory = new ArrayList<>();
////	private ArrayList<byte[]> memHistory = new ArrayList<>();
//
//	/**
//	 * Runs in start of guiController. Initializes registerTable, memoryTable and
//	 * programTable.
//	 */
//	@Override
//	public void initialize(URL location, ResourceBundle resources) {
//		// Instruction table
//		programColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//		programInstructionColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
//		pcSelection = programTable.getSelectionModel();
//
//		// Register table
//		registerColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//		registerValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
//		regSelection = registerTable.getSelectionModel();
//
//		// Memory table
//		memoryColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//		memoryDataColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
//		memSelection = memoryTable.getSelectionModel();
//	}
//
//	/**
//	 * Displays file chooser when Ctrl+O is asserted or when Open... button is
//	 * pressed. If file is not picked, buttons are disabled.
//	 * 
//	 * @throws IOException Throws exception if file is busy
//	 */
//	public void chooseFile() throws IOException {
//		FileChooser fileChooser = new FileChooser();
//		fileChooser.setTitle("Open binary RISC-V code");
//		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
//		File file = fileChooser.showOpenDialog(primaryStage);
//		if (file != null) {
//			// Initialize processor
////			program = getInstructions(file);
////			os.startApp(file);
//			loadFile(file);
//			fileRunning = file;
//		} else {
//			os.stopApp();
////			program = null;
////			programLength = 0;
////			cpu = null;
//			textFieldConsole.setText("No file chosen.");
//
//			// Disable all buttons
//			buttonNext.setDisable(true);
////			buttonPrevious.setDisable(true);
//			buttonRun.setDisable(true);
//			buttonReset.setDisable(true);
//			buttonNextTable.setDisable(true);
//			buttonPreviousTable.setDisable(true);
//			textFieldAddr.setDisable(true);
//		}
//		// Clear selections
//		pcSelection.clearSelection();
//		memSelection.clearSelection();
//		regSelection.clearSelection();
//
//		// Clear history
////		regHistory = new ArrayList<>();
////		memHistory = new ArrayList<>();
////		pcHistory = new ArrayList<>();
//	}
//
//	private void loadFile(File file) throws IOException {
////		cpu = new RV32ICpu(programMemAllocatedSize);
////		HardwareManager.getInstance().getCpu().setReg2(MEMORY_SIZE);
//		
////		programLength = getInstructions(file);
//		os.startApp(file);
//		tableRootAddress = 0;
//		// Initialize pc, mem and register tables
//		programTable.setItems(initializePcTable());
//		memoryTable.setItems(initializeMemoryTable(0));
//		registerTable.setItems(initializeRegisterTable());
//
//		// Display default stack pointer value
//		replaceTableVal(registerTable, 2, String.format("%d", os.getRegister(2)));
//
//		// Default button states
//		buttonNext.setDisable(false);
//		buttonRun.setDisable(false);
//		buttonReset.setDisable(false);
//		textFieldAddr.setDisable(false);
//		if (BYTES_PR_PAGE < MEMORY_SIZE)
//			buttonNextTable.setDisable(false);
//		textFieldConsole.setText("");
//		primaryStage.setTitle("RV32I Simulator - " + file.getName());
//	}
//
//	/**
//	 * Handles action when 'next' button is pressed. If a file has been picked, and
//	 * program is not done, then it executes the next instruction
//	 */
//	public void executeNextInstruction() {
////		if (buttonPrevious.isDisabled())
////			buttonPrevious.setDisable(false);
////		int[] tempReg = new int[32];
////		System.arraycopy(cpu.reg, 0, tempReg, 0, 32);
////		pcHistory.add(cpu.pc);
////		regHistory.add(tempReg);
//
//		// Only store copy of memory if the next instruction is sType to avoid too much
//		// wasted memory.
////		if (program[cpu.pc].sType) {
////			byte[] tempMem = new byte[mem.getMemory().length];
////			System.arraycopy(mem.getMemory(), 0, tempMem, 0, tempMem.length);
////			memHistory.add(tempMem);
////		}
//
//		os.executeNextInstruction();
//		updateNext();
////		if (cpu.pc >= program.length) { // Disable press of button if program is done
//		if (!os.isRunningApp()) {
//			buttonRun.setDisable(true);
//			buttonNext.setDisable(true);
//			SimulatorManager.getSim().getReport().printReport();
//		}
//	}
//
//	/**
//	 * Handles action when 'previous' button is pressed. Reverts differences caused
//	 * by previously executed instruction
//	 */
////	public void rewindOnce() {
////		if (buttonNext.isDisabled())
////			buttonNext.setDisable(false);
////		if (buttonRun.isDisabled())
////			buttonRun.setDisable(false);
////		// If most recently executed instruction was sType, restore memory
////		if (program[cpu.prevPc].sType) {
////			System.arraycopy(memHistory.get(memHistory.size() - 1), 0, mem.getMemory(), 0, MEMORY_SIZE);
////			memHistory.remove(memHistory.size() - 1);
////			updateMemoryTable();
////		}
////		// Revert program counter
////		if (pcHistory.size() > 1)
////			cpu.prevPc = pcHistory.get(pcHistory.size() - 2);
////		else
////			cpu.prevPc = 0;
////		cpu.pc = pcHistory.get(pcHistory.size() - 1);
////		pcSelection.clearAndSelect(cpu.prevPc); // Select previous program counter
////		regSelection.clearAndSelect(program[cpu.prevPc].rd);
////
////		// Revert register values
////		System.arraycopy(regHistory.get(regHistory.size() - 1), 0, cpu.reg, 0, 32);
////		replaceTableVal(registerTable, program[cpu.pc].rd, String.format("%d", cpu.reg[program[cpu.pc].rd]));
////
////		// Delete from history
////		pcHistory.remove(pcHistory.size() - 1);
////		regHistory.remove(regHistory.size() - 1);
////		if (pcHistory.isEmpty()) {
////			buttonPrevious.setDisable(true);
////			regSelection.clearSelection();
////			memSelection.clearSelection();
////			pcSelection.clearSelection();
////		}
////	}
//
//	/**
//	 * Handles action when 'Run' button is pressed. If a file has been picked, and
//	 * program is not done, then it executes the remaining instructions
//	 */
//	public void executeRestOfProgram() {
//		if(!os.isRunningApp()) return;
////		if (programLength == 0 || cpu == null)
////		if (programLength == 0)
////			return;
////		if (cpu.pc >= program.length)
////		if (cpu.pc < 0)
////			return;
////		while (cpu.pc < programLength && cpu.pc>=0) {
//		while(os.isRunningApp()) {
////			cpu.executeInstruction();
//			os.executeNextInstruction();
//			updateNext();
//		}
//		SimulatorManager.getSim().getReport().printReport();
//		// Disable buttons except reset
//		buttonNext.setDisable(true);
////		buttonPrevious.setDisable(true);
//		buttonRun.setDisable(true);
//
//		// Clear selections
//		pcSelection.clearSelection();
//		memSelection.clearSelection();
//		regSelection.clearSelection();
//
//		// Clear history
////		regHistory = new ArrayList<>();
////		memHistory = new ArrayList<>();
////		pcHistory = new ArrayList<>();
//	}
//
//	/**
//	 * Handles action when 'reset' button is pressed. Refreshes data, clears
//	 * histories and resets selection.
//	 * @throws IOException 
//	 */
//	public void resetProgram() throws IOException  {
//		if (fileRunning != null)
//			loadFile(fileRunning);
////		// New CPU instance and refreshing data.
////		cpu = new RV32ICpu(programMemAllocatedSize);
////		memoryTable.setItems(initializeMemoryTable(tableRootAddress = 0));
////		registerTable.setItems(initializeRegisterTable());
////		replaceTableVal(registerTable, 2, String.format("%d", cpu.reg[2]));
////		programTable.setItems(initializePcTable());
////
////		// Clear selections
////		pcSelection.clearSelection();
////		memSelection.clearSelection();
////		regSelection.clearSelection();
////
////		// Re-enable buttons
////		buttonNext.setDisable(false);
//////		buttonPrevious.setDisable(true);
////		buttonRun.setDisable(false);
////		setMemoryButtonStates();
////		
////
////		// Clear history
//////		regHistory = new ArrayList<>();
//////		memHistory = new ArrayList<>();
//////		pcHistory = new ArrayList<>();
////		textFieldConsole.setText("");
//	}
//
//	// Exits application when Ctrl+Q is asserted or Exit button is pressed.
//	public void closeProgram() {
//		System.exit(0);
//	}
//
//	/* HELPER METHODS FOR GUI CONTROL */
//
//	/**
//	 * Updates TableView with results from executed instruction
//	 */
//	private void updateNext() {
//		int previousPC = os.getPreviousPC();
//		RV32IInstruction inst = getInstruction(previousPC);
//		replaceTableVal(registerTable, inst.rd, String.format("%d", os.getRegister(inst.rd)));
//		pcSelection.clearAndSelect(previousPC);
//		pcSelection.getTableView().scrollTo(previousPC);
//		if (inst.noRd) {
//			if (inst.sType)
//				updateMemoryTable();
//			if (inst.ecall) {
//				switch (os.getRegister(10)) {
//				case 1:
//					consolePrint(String.format("%d", os.getRegister(11)));
//					break;
//				case 4:
//					consolePrint(os.getMemoryStr(os.getRegister(11)));
//					break;
//				case 11:
//					consolePrint(String.format("%c", os.getRegister(11)));
//					break;
//				default:
////					System.err.println("cpu.reg[10] " + cpu.reg[10]);					
//					break;
//				}
//			}
//			return;
//		}
//		regSelection.clearAndSelect(inst.rd);
//		regSelection.getTableView().scrollTo(inst.rd);
//	}
//
//	/**
//	 * Changes memory table view from tableRootAddress to tableRootAddress -
//	 * BYTES_PR_PAGE Disables corresponding button if needed.
//	 */
//	public void previousMemoryTable() {
//		tableRootAddress -= BYTES_PR_PAGE;
//		memSelection.clearSelection();
//		memoryTable.setItems(initializeMemoryTable(tableRootAddress));
//		setMemoryButtonStates();
//	}
//
//	/**
//	 * Changes memory table view from tableRootAddress to tableRootAddress +
//	 * BYTES_PR_PAGE Disables corresponding button if needed.
//	 */
//	public void nextMemoryTable() {
//		tableRootAddress += BYTES_PR_PAGE;
//		memSelection.clearSelection();
//		memoryTable.setItems(initializeMemoryTable(tableRootAddress));
//		setMemoryButtonStates();
//	}
//
//	/**
//	 * Attempts to parse textFieldAddr input as hexadecimal number. If no exception
//	 * caught, change table view to said address.
//	 */
//	public void gotoAddress() {
//		int destAddr;
//		int addrOffset;
//		try {
//			destAddr = Integer.parseInt(textFieldAddr.getText(), 16);
//			textFieldAddr.setText("");
//		} catch (NumberFormatException e) {
//			textFieldConsole.setText("Failed to parse 32bit hexadecimal address (without 0x-prefix)");
//			System.err.println(e.toString());
//			return;
//		}
//		if (destAddr > MEMORY_SIZE - 1) {
//			textFieldConsole.setText("Address exceeds memory (" + (MEMORY_SIZE - 1) + " Bytes)");
//			return;
//		}
//		tableRootAddress = BYTES_PR_PAGE * (destAddr / BYTES_PR_PAGE);
//		addrOffset = destAddr - tableRootAddress;
//		memoryTable.setItems(initializeMemoryTable(tableRootAddress));
//		memSelection.clearAndSelect(addrOffset >> 2);
//		memSelection.getTableView().scrollTo(addrOffset >> 2);
//		setMemoryButtonStates();
//	}
//
//	/**
//	 * Gets address from previously executed instruction and updates table view
//	 * accordingly
//	 */
//	private void updateMemoryTable() {
//		RV32IInstruction inst = getInstruction(os.getPreviousPC());
//		int addr = (os.getRegister(inst.rs1) +inst.imm) & 0xFFFFFFFC; // Remove byte offset
//		int addrOffset;
//		// Check if requested address is in same block as tableRootAddress
//		if (addr / BYTES_PR_PAGE == tableRootAddress / BYTES_PR_PAGE) {
//			// Relative address compared to tableRootAddress
//			addrOffset = addr - tableRootAddress;
//			replaceTableVal(memoryTable, addrOffset >> 2, String.format("0x%08X", readMemory(addr)));
//			memSelection.clearAndSelect(addrOffset >> 2);
//			memSelection.getTableView().scrollTo(addrOffset >> 2);
//		} else {
//			tableRootAddress = BYTES_PR_PAGE * (addr / BYTES_PR_PAGE);
//			addrOffset = addr - tableRootAddress;
//			memoryTable.setItems(initializeMemoryTable(tableRootAddress));
//			memSelection.clearAndSelect(addrOffset >> 2);
//			memSelection.getTableView().scrollTo(addrOffset >> 2);
//		}
//		setMemoryButtonStates();
//	}
//
//	private static int readMemory(int add) {
//		//todo MELHORAR
//		return SimulatorManager.getSim().getMemory().read(add/4).toInt();
//	}
//	private static RV32IInstruction getInstruction(int pc) {
//		return new RV32IInstruction(readMemory(pc*4));
//	}
//
//	/**
//	 * Disables/Enables previous/next buttons depending on tableRootAddress
//	 */
//	private void setMemoryButtonStates() {
//		if (tableRootAddress == 0) {
//			buttonPreviousTable.setDisable(true);
//			buttonNextTable.setDisable(false);
//		} else if (tableRootAddress == MEMORY_SIZE - BYTES_PR_PAGE) {
//			buttonPreviousTable.setDisable(false);
//			buttonNextTable.setDisable(true);
//		} else {
//			buttonPreviousTable.setDisable(false);
//			buttonNextTable.setDisable(false);
//		}
//	}
//
//	/**
//	 * Replaces value at given index in given table.
//	 * 
//	 * @param table: Target TableView object
//	 * @param index: Target index of table
//	 * @param val:   Value to insert at index
//	 */
//	private static void replaceTableVal(TableView<TableHelper> table, int index, String val) {
//		table.getItems().get(index).setValue(val);
//	}
//
//	/**
//	 * Simulates console output by appending outPrint to what is already there.
//	 * 
//	 * @param outPrint: String to append
//	 */
//	private void consolePrint(String outPrint) {
//		textFieldConsole.setText(textFieldConsole.getText() + outPrint);
//	}
//
//	/**
//	 * Sets up program table.
//	 * 
//	 * @param program: An array of Instruction objects.
//	 * @return Returns a new ObservableList with Program Counter and Parsed
//	 *         Instruction
//	 */
//	private  ObservableList<TableHelper> initializePcTable() {
//		ObservableList<TableHelper> pcTable = FXCollections.observableArrayList();
//		for (int i = 0; i < os.getProgramLength(); i++) {
//			pcTable.add(new TableHelper(String.format("%d", i << 2), String.format("%s", getInstruction(i).assemblyString)));
//		}
//		return pcTable;
//	}
//
//	/**
//	 * Sets up memory table
//	 * 
//	 * @param startAddr: Start address that will be displayed as index 0 in
//	 *                   resulting list
//	 * @return Returns a new ObservableList consisting of (up to) BYTES_PR_PAGE / 4
//	 *         rows.
//	 */
//	private static ObservableList<TableHelper> initializeMemoryTable(int startAddr) {
//		ObservableList<TableHelper> memTable = FXCollections.observableArrayList();
//		for (int addrOffset = 0; addrOffset < BYTES_PR_PAGE; addrOffset += 4) {
//			if (startAddr + addrOffset >= MEMORY_SIZE)
//				break;
//			//TODO FIX OS.MEMORY, access directly
//			memTable.add(new TableHelper(String.format("0x%06X", startAddr + addrOffset),
//					String.format("0x%08X", readMemory(startAddr + addrOffset))));
//		}
//		return memTable;
//	}
//
//	/**
//	 * Sets up register table
//	 * 
//	 * @return Returns a new ObservableList consisting of 32 registers with value 0.
//	 */
//	private static ObservableList<TableHelper> initializeRegisterTable() {
//		ObservableList<TableHelper> regTable = FXCollections.observableArrayList();
//		for (int i = 0; i < 32; i++) {
//			regTable.add(new TableHelper("x" + i, "0"));
//		}
//		return regTable;
//	}
//
//
//
//	// Used to pass stage from main
//	public void setStage(Stage stage) {
//		this.primaryStage = stage;
//	}
//}
