/* File: guiController.java
 * Authors: Marc Sun Bøg & Simon Amtoft Pedersen
 *
 * The following file handles the flow of the entire program. 
 * The methods in this file are mostly GUI methods, which uses methods from other files.
 */

package absimth.sim.gui;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.CPUModel;
import absimth.sim.cpu.ICPUInstruction;
import absimth.sim.gui.helper.AbsimthEvent;
import absimth.sim.gui.helper.TableHelper;
import absimth.sim.gui.helper.UIUtil;
import absimth.sim.memoryController.model.ECCMemoryFaultModel;
import absimth.sim.os.OSCpuExecutor;
import absimth.sim.utils.Bits;
import absimth.sim.utils.HexaFormat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CPUController implements Initializable {
	// CONSTANTS
	private static final int ADDRESS_PR_PAGE = 256; // 64 words
	//TODO MOVER to OS
//	public static final int MEMORY_SIZE = 10485760;//10485760; // 10MiB memory
	private int totalAddress = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getTotalOfAddress().intValue() -1;
	// Keeping track of memory table
	//REmove?
	private int tableRootAddress = 0;

	// FXML ELEMENTS
	private Stage stage;

	// UI elements
	public VBox mainVBox;
	public MenuItem menuItemOpen;
	public MenuItem menuItemExit;
	// Input
	public Button buttonNextProgram;
	public Button buttonNext;
	public Button buttonRun;
	public Button buttonNextTable;
	public Button buttonPreviousTable;
	public TextField textFieldAddr;
	public ComboBox<String> comboBoxCpu;
	public ComboBox<String> comboBoxCpuProgram;
	
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
		
		memSelection = memoryTable.getSelectionModel();
		memoryColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		memoryDataColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
		memoryDataColumn.setCellFactory(tc->createColorFormat());
		memoryColumn.setCellFactory(tc->createColorFormat());
		
	}

	private TextFieldTableCell<TableHelper, String> createColorFormat() {
		return new TextFieldTableCell<>(TextFormatter.IDENTITY_STRING_CONVERTER) {
			@Override
			public void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
		        if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
					ECCMemoryFaultModel rep = SimulatorManager.getSim().getMemoryController().getMemoryStatus().getFromAddress(getIndex() + tableRootAddress);
			        UIUtil.printCellMemoryStatus(this, rep);
		        } else {
//		        	UIUtil.printCellMemoryStatus(this, null);
		        }
		    }
		};
	}
	

	public void executeNextProgramInstruction() throws Exception {
		String p = comboBoxCpuProgram.getSelectionModel().getSelectedItem();
		cpuExecutor.changeProgramRunning(p);
		executeNextInstruction();
	}
	private void setButtonRun(boolean instruction) {
		buttonRun.setText(instruction? "Load Instruction" : "Run");
	}
	/**
	 * Handles action when 'next' button is pressed. If a file has been picked, and
	 * program is not done, then it executes the next instruction
	 * @throws Exception 
	 */
	public void executeNextInstruction() throws Exception {
		try {
			boolean isInstructionLoad = cpuExecutor.inInstructionMode();
			cpuExecutor.executeNextInstruction();
			
			comboBoxCpuProgram.setValue(cpuExecutor.getProgramName());
			initializeComboCpuProgram();
			
			updateView();
			
			if(!isInstructionLoad) updateNext();
			checkHasProgramToRun();
		} catch (IllegalAccessError e) {
			System.out.println(e.getMessage());
			buttonRun.setDisable(true);
			buttonNext.setDisable(true);
			buttonNextProgram.setDisable(true);
			textFieldConsole.appendText("\r\n" + e.getMessage());
		}
		
	}

	private void checkHasProgramToRun() {
		if (!cpuExecutor.isRunningApp()) {
			buttonRun.setDisable(true);
			buttonNext.setDisable(true);
			buttonNextProgram.setDisable(true);
		} else {
			buttonRun.setDisable(false);
			buttonNext.setDisable(false);
			buttonNextProgram.setDisable(false);
			
		}
	}

	private void updateView() throws Exception {
		if (cpuExecutor != null) {
			registerTable.setItems(initializeRegisterTable());
			programTable.setItems(initializePcTable());
			memoryTable.setItems(initializeMemoryTable(cpuExecutor.getInitialAddress()));
			setButtonRun(cpuExecutor.inInstructionMode());
		} else {
			memoryTable.setItems(initializeMemoryTable(0));
		}
	}


	public void executeRestOfProgram() {
		try {
			RunAllInstructionDialog.start(cpuExecutor, e->{
				if(!cpuExecutor.isRunningApp()) {
					buttonNext.setDisable(true);
					buttonRun.setDisable(true);
					buttonNextProgram.setDisable(true);
					
					// Clear selections
					pcSelection.clearSelection();
					memSelection.clearSelection();
					regSelection.clearSelection();
					initializeComboCpuProgram();
				} else {
					try {
						updateView();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});

			// Disable buttons except reset
		//TODO REFRESH
		} catch (IllegalAccessError e) {
			System.out.println(e.getMessage());
			buttonRun.setDisable(true);
			buttonNext.setDisable(true);
			buttonNextProgram.setDisable(true);
			textFieldConsole.appendText("\r\n" + e.getMessage());
		}
	}

	public void closeProgram() {
		stage.close();
	}


	/**
	 * Updates TableView with results from executed instruction
	 * @throws Exception 
	 */
	private void updateNext() throws Exception {
		int previousPC = cpuExecutor.getPreviousPC();
		ICPUInstruction inst = getInstruction(previousPC + cpuExecutor.getInitialAddress());
		replaceTableVal(registerTable, inst.getRd(), String.format("%d", cpuExecutor.getRegister(inst.getRd())));
		pcSelection.clearAndSelect(previousPC);
		pcSelection.getTableView().scrollTo(previousPC);
		
		if (inst.isNoRd()) {
			if (inst.isSType())
				updateMemoryTable();
			if (inst.isEcall()) {
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
		regSelection.clearAndSelect(inst.getRd());
		regSelection.getTableView().scrollTo(inst.getRd());
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
		try {
			destAddr = Integer.parseInt(textFieldAddr.getText(), 16);
			textFieldAddr.setText("");
		} catch (NumberFormatException e) {
			textFieldConsole.setText("Failed to parse 32bit hexadecimal address (without 0x-prefix)");
			System.err.println(e.toString());
			return;
		}
		if (destAddr > totalAddress) {
			textFieldConsole.setText("Address exceeds memory (" + HexaFormat.f(totalAddress) + ")");
			return;
		}
		tableRootAddress = ADDRESS_PR_PAGE * (destAddr / ADDRESS_PR_PAGE);
		int addrOffset = destAddr - tableRootAddress;
		memoryTable.setItems(initializeMemoryTable(tableRootAddress));
		memSelection.clearAndSelect(addrOffset);
		memSelection.getTableView().scrollTo(addrOffset);
		setMemoryButtonStates();
	}

	/**
	 * Gets address from previously executed instruction and updates table view
	 * accordingly
	 * @throws Exception 
	 */
	private void updateMemoryTable() throws Exception {
		ICPUInstruction inst = getInstruction(cpuExecutor.getPreviousPC());
		int addr = (cpuExecutor.getRegister(inst.getRs1()) +inst.getImm()) & 0xFFFFFFFC; // Remove byte offset
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
		try {
			//return SimulatorManager.getSim().getMemory().read(add).toInt();
			return SimulatorManager.getSim().getMemoryController().justDecode(add).toInt();
		}catch (Exception e) {
			System.err.println(e);
			return 0;
		}
	}
	
	private ICPUInstruction getInstruction(int pc) throws Exception {
		try {
			Bits l = SimulatorManager.getSim().getMemoryController().justDecode(pc);
			return cpuExecutor.getICPU().getInstruction(l.toInt());
		}catch (Exception e) {
			System.out.println(e.toString());
			return cpuExecutor.getICPU().getInstruction(0);
		}
	}

	/**
	 * Disables/Enables previous/next buttons depending on tableRootAddress
	 */
	private void setMemoryButtonStates() {
		if (tableRootAddress == 0) {
			buttonPreviousTable.setDisable(true);
			buttonNextTable.setDisable(false);
		} else if (tableRootAddress == totalAddress - ADDRESS_PR_PAGE) {
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
	 * @throws Exception 
	 */
	private  ObservableList<TableHelper> initializePcTable() throws Exception {
		ObservableList<TableHelper> pcTable = FXCollections.observableArrayList();
		for (int i = 0; i < cpuExecutor.getProgramLength(); i++) {
			pcTable.add(new TableHelper(String.format("%d", i << 2), String.format("%s", getInstruction(cpuExecutor.getInitialAddress()+i).getAssemblyString())));
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
	private ObservableList<TableHelper> initializeMemoryTable(int startAddr) {
		ObservableList<TableHelper> memTable = FXCollections.observableArrayList();
		for (int addrOffset = 0; addrOffset < ADDRESS_PR_PAGE; addrOffset ++) {
			if (startAddr + addrOffset >= totalAddress)
				break;
			memTable.add(new TableHelper(String.format("0x%06X", startAddr + addrOffset),
					String.format("0x%06X", readMemory(startAddr + addrOffset))));
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
		this.stage.setResizable(false);
		this.stage.addEventHandler(AbsimthEvent.ABSIMTH_UPDATE_EVENT, event -> onAbsimthUpdateEvent());
		initializeComboCpu();
	}

	private void initializeComboCpu() {
		List<String> lst = new ArrayList<>();
		List<CPUModel> lstCpu = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getCpu();
		for(int i=0;i<lstCpu.size();i++) {
			lst.add("CPU " + i + " - " + lstCpu.get(i).getName());
		}
		comboBoxCpu.setItems(FXCollections.observableArrayList(lst));
	}
	
	private void initializeComboCpuProgram() {
		comboBoxCpuProgram.setDisable(false);
		comboBoxCpuProgram.setItems(FXCollections.observableArrayList(cpuExecutor.getListOfProgramsName()));
	}

	public void comboBoxCpuProgramOnHidden() throws Exception {
		cpuExecutor.changeProgramRunning(comboBoxCpuProgram.getSelectionModel().getSelectedItem());
		updateView();
	}
	
	
	public void comboboxCpuOnAction() throws Exception {
		this.cpu = comboBoxCpu.getSelectionModel().getSelectedIndex();
		cpuExecutor = SimulatorManager.getSim().getOs().getCpuExecutor(this.cpu);

		this.stage.setTitle("CPU " + this.cpu);
		programTable.setItems(initializePcTable());
		memoryTable.setItems(initializeMemoryTable(cpuExecutor.getInitialAddress()));
		registerTable.setItems(initializeRegisterTable());
		setButtonRun(cpuExecutor.inInstructionMode());		
		initializeComboCpuProgram();
		
		checkHasProgramToRun();
		
		
	}

	private void onAbsimthUpdateEvent() {
		try {
			this.cpu = comboBoxCpu.getSelectionModel().getSelectedIndex();
			if(cpu >=0) cpuExecutor = SimulatorManager.getSim().getOs().getCpuExecutor(this.cpu);
			if(cpuExecutor!=null) {
				String p = comboBoxCpuProgram.getSelectionModel().getSelectedItem();
				if(p != null) cpuExecutor.changeProgramRunning(p);
				comboBoxCpuProgram.getSelectionModel().select(cpuExecutor.getProgramName());
			}
			updateView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
