/* File: guiController.java
 * Authors: Marc Sun Bøg & Simon Amtoft Pedersen
 *
 * The following file handles the flow of the entire program. 
 * The methods in this file are mostly GUI methods, which uses methods from other files.
 */

package absimth.sim.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import absimth.sim.SimulatorManager;
import absimth.sim.cpu.riscv32i.RV32IInstruction;
import absimth.sim.os.OSCpuExecutor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CPUController implements Initializable {
	// CONSTANTS
	private static final int ADDRESS_PR_PAGE = 256; // 64 words
	//TODO MOVER to OS
	public static final int MEMORY_SIZE = 10485760;//10485760; // 10MiB memory

	// Keeping track of memory table
	//REmove?
	private int tableRootAddress = 0;

	// FXML ELEMENTS
	private Stage stage;

	// UI elements
	public VBox mainVBox;
	public MenuItem menuItemOpen;
	public MenuItem menuItemExit;
	public Label programLabel;
	// Input
	public Button buttonNext;
	public Button buttonRun;
	public Button buttonReset;
	public Button buttonNextTable;
	public Button buttonPreviousTable;
	public TextField textFieldAddr;
	public ComboBox<String> comboBoxCpu;
	
	// Output
	public TextArea textFieldConsole;

	// Table elements
	public TableView<TableHelper> registerTable;
	public TableColumn<TableHelper, String> registerColumn;
	public TableColumn<TableHelper, String> registerValueColumn;
	public TableView<TableHelper> memoryTable;
	public TableColumn<TableHelper, String> memoryColumn;
	public TableColumn<TableHelper, String> memoryDataColumn;
	public TableView<TableHelper> programTable;
	public TableColumn<TableHelper, String> programColumn;
	public TableColumn<TableHelper, String> programInstructionColumn;

	// Table selection
	private TableView.TableViewSelectionModel<TableHelper> pcSelection;
	private TableView.TableViewSelectionModel<TableHelper> regSelection;
	private TableView.TableViewSelectionModel<TableHelper> memSelection;

	// Controller variables
	private int cpu;
	private OSCpuExecutor cpuExecutor;
	/**
	 * Runs in start of guiController. Initializes registerTable, memoryTable and
	 * programTable.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Instruction table
		programColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		programInstructionColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
		pcSelection = programTable.getSelectionModel();

		// Register table
		registerColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		registerValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
		regSelection = registerTable.getSelectionModel();

		// Memory table
		memoryColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		memoryDataColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
		memSelection = memoryTable.getSelectionModel();
	}

	/**
	 * Handles action when 'next' button is pressed. If a file has been picked, and
	 * program is not done, then it executes the next instruction
	 */
	public void executeNextInstruction() {
		boolean isInstructionLoad = cpuExecutor.inInstructionMode();
		cpuExecutor.executeNextInstruction();
		
		updateView();
		
		if(!isInstructionLoad) updateNext();
		
		if (!cpuExecutor.isRunningApp()) {
			buttonRun.setDisable(true);
			buttonNext.setDisable(true);
			SimulatorManager.getSim().getReport().printReport();
		}
		programLabel.setText("Running " + cpuExecutor.getProgramName());
	}

	private void updateView() {
		registerTable.setItems(initializeRegisterTable());
		programTable.setItems(initializePcTable());
		if (cpuExecutor != null)
			memoryTable.setItems(initializeMemoryTable(cpuExecutor.getInitialAddress()));
		else
			memoryTable.setItems(initializeMemoryTable(0));
	}


	/**
	 * Handles action when 'Run' button is pressed. If a file has been picked, and
	 * program is not done, then it executes the remaining instructions
	 */
	public void executeRestOfProgram() {
		if(!cpuExecutor.isRunningApp()) return;
		while(cpuExecutor.isRunningApp()) {
			cpuExecutor.executeNextInstruction();
			updateNext();
		}
		SimulatorManager.getSim().getReport().printReport();
		// Disable buttons except reset
		buttonNext.setDisable(true);
		buttonRun.setDisable(true);

		
		// Clear selections
		pcSelection.clearSelection();
		memSelection.clearSelection();
		regSelection.clearSelection();
	}

	/**
	 * Handles action when 'reset' button is pressed. Refreshes data, clears
	 * histories and resets selection.
	 * @throws IOException 
	 */
	public void resetProgram() throws IOException  {
	}

	public void closeProgram() {
		stage.close();
	}


	/**
	 * Updates TableView with results from executed instruction
	 */
	private void updateNext() {
		int previousPC = cpuExecutor.getPreviousPC();
		RV32IInstruction inst = getInstruction(previousPC + cpuExecutor.getInitialAddress());
		replaceTableVal(registerTable, inst.rd, String.format("%d", cpuExecutor.getRegister(inst.rd)));
		pcSelection.clearAndSelect(previousPC);
		pcSelection.getTableView().scrollTo(previousPC);
		
		if (inst.noRd) {
			if (inst.sType)
				updateMemoryTable();
			if (inst.ecall) {
				switch (cpuExecutor.getRegister(10)) {
				case 1:
					consolePrint(String.format("%d", cpuExecutor.getRegister(11)));
					break;
				case 4:
					consolePrint(cpuExecutor.getMemoryStr(cpuExecutor.getRegister(11)));
					break;
				case 11:
					consolePrint(String.format("%c", cpuExecutor.getRegister(11)));
					break;
				default:
//					System.err.println("cpu.reg[10] " + cpu.reg[10]);					
					break;
				}
			}
			return;
		}
		regSelection.clearAndSelect(inst.rd);
		regSelection.getTableView().scrollTo(inst.rd);
	}

	/**
	 * Changes memory table view from tableRootAddress to tableRootAddress -
	 * BYTES_PR_PAGE Disables corresponding button if needed.
	 */
	public void previousMemoryTable() {
		tableRootAddress -= ADDRESS_PR_PAGE;
		memSelection.clearSelection();
		memoryTable.setItems(initializeMemoryTable(tableRootAddress));
		setMemoryButtonStates();
	}

	/**
	 * Changes memory table view from tableRootAddress to tableRootAddress +
	 * BYTES_PR_PAGE Disables corresponding button if needed.
	 */
	public void nextMemoryTable() {
		tableRootAddress += ADDRESS_PR_PAGE;
		memSelection.clearSelection();
		memoryTable.setItems(initializeMemoryTable(tableRootAddress));
		setMemoryButtonStates();
	}

	/**
	 * Attempts to parse textFieldAddr input as hexadecimal number. If no exception
	 * caught, change table view to said address.
	 */
	public void gotoAddress() {
		int destAddr;
		int addrOffset;
		try {
			destAddr = Integer.parseInt(textFieldAddr.getText(), 16);
			textFieldAddr.setText("");
		} catch (NumberFormatException e) {
			textFieldConsole.setText("Failed to parse 32bit hexadecimal address (without 0x-prefix)");
			System.err.println(e.toString());
			return;
		}
		if (destAddr > MEMORY_SIZE - 1) {
			textFieldConsole.setText("Address exceeds memory (" + (MEMORY_SIZE - 1) + " Bytes)");
			return;
		}
		tableRootAddress = ADDRESS_PR_PAGE * (destAddr / ADDRESS_PR_PAGE);
		addrOffset = destAddr - tableRootAddress;
		memoryTable.setItems(initializeMemoryTable(tableRootAddress));
		memSelection.clearAndSelect(addrOffset >> 2);
		memSelection.getTableView().scrollTo(addrOffset >> 2);
		setMemoryButtonStates();
	}

	/**
	 * Gets address from previously executed instruction and updates table view
	 * accordingly
	 */
	private void updateMemoryTable() {
		RV32IInstruction inst = getInstruction(cpuExecutor.getPreviousPC());
		int addr = (cpuExecutor.getRegister(inst.rs1) +inst.imm) & 0xFFFFFFFC; // Remove byte offset
		addr = addr/4;
		int addrOffset;
		// Check if requested address is in same block as tableRootAddress
		if (addr / ADDRESS_PR_PAGE == tableRootAddress / ADDRESS_PR_PAGE) {
			// Relative address compared to tableRootAddress
			addrOffset = addr - tableRootAddress;
			replaceTableVal(memoryTable, addrOffset , String.format("0x%08X", readMemory(addr)));
			memSelection.clearAndSelect(addrOffset );
			memSelection.getTableView().scrollTo(addrOffset);
		} else {
			
			tableRootAddress = ADDRESS_PR_PAGE * (addr / ADDRESS_PR_PAGE);
			addrOffset = addr - tableRootAddress;
			memoryTable.setItems(initializeMemoryTable(tableRootAddress));
			memSelection.clearAndSelect(addrOffset);
			memSelection.getTableView().scrollTo(addrOffset);
		}
		setMemoryButtonStates();
	}

	private static int readMemory(int add) {
//		System.out.println("reading " + add);
		return SimulatorManager.getSim().getMemory().read(add).toInt();
	}
	private static RV32IInstruction getInstruction(int pc) {
		return new RV32IInstruction(readMemory(pc));
	}

	/**
	 * Disables/Enables previous/next buttons depending on tableRootAddress
	 */
	private void setMemoryButtonStates() {
		if (tableRootAddress == 0) {
			buttonPreviousTable.setDisable(true);
			buttonNextTable.setDisable(false);
		} else if (tableRootAddress == MEMORY_SIZE - ADDRESS_PR_PAGE) {
			buttonPreviousTable.setDisable(false);
			buttonNextTable.setDisable(true);
		} else {
			buttonPreviousTable.setDisable(false);
			buttonNextTable.setDisable(false);
		}
	}

	/**
	 * Replaces value at given index in given table.
	 * 
	 * @param table: Target TableView object
	 * @param index: Target index of table
	 * @param val:   Value to insert at index
	 */
	private static void replaceTableVal(TableView<TableHelper> table, int index, String val) {
		table.getItems().get(index).setValue(val);
	}

	/**
	 * Simulates console output by appending outPrint to what is already there.
	 * 
	 * @param outPrint: String to append
	 */
	private void consolePrint(String outPrint) {
		textFieldConsole.setText(textFieldConsole.getText() + outPrint);
	}

	/**
	 * Sets up program table.
	 * 
	 * @param program: An array of Instruction objects.
	 * @return Returns a new ObservableList with Program Counter and Parsed
	 *         Instruction
	 */
	private  ObservableList<TableHelper> initializePcTable() {
		ObservableList<TableHelper> pcTable = FXCollections.observableArrayList();
		for (int i = 0; i < cpuExecutor.getProgramLength(); i++) {
			pcTable.add(new TableHelper(String.format("%d", i << 2), String.format("%s", getInstruction(cpuExecutor.getInitialAddress()+i).assemblyString)));
		}
		return pcTable;
	}

	/**
	 * Sets up memory table
	 * 
	 * @param startAddr: Start address that will be displayed as index 0 in
	 *                   resulting list
	 * @return Returns a new ObservableList consisting of (up to) BYTES_PR_PAGE / 4
	 *         rows.
	 */
	private static ObservableList<TableHelper> initializeMemoryTable(int startAddr) {
		ObservableList<TableHelper> memTable = FXCollections.observableArrayList();
		for (int addrOffset = 0; addrOffset < ADDRESS_PR_PAGE; addrOffset ++) {
			if (startAddr + addrOffset >= MEMORY_SIZE)
				break;
			//TODO FIX OS.MEMORY, access directly
			//"0x%06X"
			memTable.add(new TableHelper(String.format("%08d", startAddr + addrOffset),
					String.format("0x%08X", readMemory(startAddr + addrOffset))));
		}
		return memTable;
	}

	/**
	 * Sets up register table
	 * 
	 * @return Returns a new ObservableList consisting of 32 registers with value 0.
	 */
	//TODO FIX IT make iqual memoryttable and remove copy
	private ObservableList<TableHelper> initializeRegisterTable() {
		ObservableList<TableHelper> regTable = FXCollections.observableArrayList();
		for (int i = 0; i < 32; i++) {
			regTable.add(new TableHelper("x" + i, String.format("%d", cpuExecutor.getRegister(i))));
		}
		return regTable;
	}



	// Used to pass stage from main
	public void setStage(Stage stage) {
		this.stage = stage;
		initializeComboCpu();
	}

	private void initializeComboCpu() {
		List<String> lst = new ArrayList<>();
		for(int i=0;i<SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getCpu().getAmount();i++) {
			lst.add("CPU " + i);
		}
		comboBoxCpu.setItems(FXCollections.observableArrayList(lst));
	}

	public void comboboxCpuOnAction() {
		this.cpu = comboBoxCpu.getSelectionModel().getSelectedIndex();
		cpuExecutor = SimulatorManager.getSim().getOs().getCpuExecutor(this.cpu);

		this.stage.setTitle("CPU " + this.cpu);
		programTable.setItems(initializePcTable());
		memoryTable.setItems(initializeMemoryTable(0));
		registerTable.setItems(initializeRegisterTable());
		buttonNext.setDisable(false);
		buttonRun.setDisable(false);
		
		
	}
	
}
