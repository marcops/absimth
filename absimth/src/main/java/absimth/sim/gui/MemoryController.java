
package absimth.sim.gui;

import java.net.URL;
import java.util.ResourceBundle;

import absimth.sim.Bits;
import absimth.sim.SimulatorManager;
import absimth.sim.gui.helper.AlertDialog;
import absimth.sim.gui.helper.MemoryTableHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MemoryController implements Initializable {
	private static final int BYTES_PR_PAGE = 256; // 64 words
	// Keeping track of memory table
	private int tableRootAddress = 0;

	// FXML ELEMENTS
	private Stage primaryStage;

	// UI elements
	public VBox mainVBox;
	public MenuItem menuItemExit;

	// Input
	public Button buttonNextTable;
	public Button buttonPreviousTable;
	public Button buttonRefreshTable;
	public TextField textFieldAddr;

	// Table elements
	public TableView<MemoryTableHelper> memoryTable;
	public TableColumn<MemoryTableHelper, String> memoryColumn;
	public TableColumn<MemoryTableHelper, String> memoryDataColumn0;
	public TableColumn<MemoryTableHelper, String> memoryDataColumn1;
	public TableColumn<MemoryTableHelper, String> memoryDataColumn2;
	public TableColumn<MemoryTableHelper, String> memoryDataColumn3;
	public TableColumn<MemoryTableHelper, String> memoryDataColumn4;
	public TableColumn<MemoryTableHelper, String> memoryDataColumn5;
	public TableColumn<MemoryTableHelper, String> memoryDataColumn6;
	public TableColumn<MemoryTableHelper, String> memoryDataColumn7;

	// Table selection
//	private TableView.TableViewSelectionModel<TableHelper> memSelection;

	/**
	 * Runs in start of guiController. Initializes registerTable, memoryTable and
	 * programTable.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Memory table
		memoryColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
		memoryDataColumn0.setCellValueFactory(new PropertyValueFactory<>("x0"));
		memoryDataColumn1.setCellValueFactory(new PropertyValueFactory<>("x1"));
		memoryDataColumn2.setCellValueFactory(new PropertyValueFactory<>("x2"));
		memoryDataColumn3.setCellValueFactory(new PropertyValueFactory<>("x3"));
		memoryDataColumn4.setCellValueFactory(new PropertyValueFactory<>("x4"));
		memoryDataColumn5.setCellValueFactory(new PropertyValueFactory<>("x5"));
		memoryDataColumn6.setCellValueFactory(new PropertyValueFactory<>("x6"));
		memoryDataColumn7.setCellValueFactory(new PropertyValueFactory<>("x7"));
		
		    
		memoryDataColumn0.setCellFactory(TextFieldTableCell.forTableColumn());
		memoryDataColumn1.setCellFactory(TextFieldTableCell.forTableColumn());
		memoryDataColumn2.setCellFactory(TextFieldTableCell.forTableColumn());
		memoryDataColumn3.setCellFactory(TextFieldTableCell.forTableColumn());
		memoryDataColumn4.setCellFactory(TextFieldTableCell.forTableColumn());
		memoryDataColumn5.setCellFactory(TextFieldTableCell.forTableColumn());
		memoryDataColumn6.setCellFactory(TextFieldTableCell.forTableColumn());
		memoryDataColumn7.setCellFactory(TextFieldTableCell.forTableColumn());
		
		
		memoryDataColumn0.setOnEditCommit((TableColumn.CellEditEvent<MemoryTableHelper, String> t) -> {
			setNewValue(t, 0);
		});
		memoryDataColumn1.setOnEditCommit((TableColumn.CellEditEvent<MemoryTableHelper, String> t) -> {
			setNewValue(t, 1);
		});
		
		memoryDataColumn0.setOnEditCommit((TableColumn.CellEditEvent<MemoryTableHelper, String> t) -> {
			setNewValue(t, 0);
		});
		memoryDataColumn2.setOnEditCommit((TableColumn.CellEditEvent<MemoryTableHelper, String> t) -> {
			setNewValue(t, 2);
		});
		memoryDataColumn3.setOnEditCommit((TableColumn.CellEditEvent<MemoryTableHelper, String> t) -> {
			setNewValue(t, 3);
		});
		memoryDataColumn4.setOnEditCommit((TableColumn.CellEditEvent<MemoryTableHelper, String> t) -> {
			setNewValue(t, 4);
		});
		memoryDataColumn5.setOnEditCommit((TableColumn.CellEditEvent<MemoryTableHelper, String> t) -> {
			setNewValue(t, 5);
		});
		
		memoryDataColumn7.setOnEditCommit((TableColumn.CellEditEvent<MemoryTableHelper, String> t) -> {
			setNewValue(t, 6);
		});
		
		memoryDataColumn7.setOnEditCommit((TableColumn.CellEditEvent<MemoryTableHelper, String> t) -> {
			setNewValue(t, 7);
		});
		
	
	}

	private void setNewValue(TableColumn.CellEditEvent<MemoryTableHelper, String> t, int p) {
		int row = t.getTablePosition().getRow();
		int destAddr = getAddress(t.getNewValue());
		if (destAddr < 0) return;
		SimulatorManager.getSim().getMemory().write((long)tableRootAddress + row + p, Bits.from(destAddr));
		memoryTable.setItems(initializeMemoryTable(tableRootAddress));
	}

	/**
	 * Changes memory table view from tableRootAddress to tableRootAddress -
	 * BYTES_PR_PAGE Disables corresponding button if needed.
	 */
	public void previousMemoryTable() {
		tableRootAddress -= BYTES_PR_PAGE;
//		memSelection.clearSelection();
		memoryTable.setItems(initializeMemoryTable(tableRootAddress));
		setMemoryButtonStates();
	}

	/**
	 * Changes memory table view from tableRootAddress to tableRootAddress +
	 * BYTES_PR_PAGE Disables corresponding button if needed.
	 */
	public void nextMemoryTable() {
		tableRootAddress += BYTES_PR_PAGE;
//		memSelection.clearSelection();
		memoryTable.setItems(initializeMemoryTable(tableRootAddress));
		setMemoryButtonStates();
	}

	/**
	 * Attempts to parse textFieldAddr input as hexadecimal number. If no exception
	 * caught, change table view to said address.
	 */
	public void gotoAddress() {
//		int destAddr;
//		int addrOffset;
		int destAddr = getAddress(textFieldAddr.getText());
		if (destAddr < 0) return;
		textFieldAddr.setText("");
		tableRootAddress = BYTES_PR_PAGE * (destAddr / BYTES_PR_PAGE);
//		addrOffset = destAddr - tableRootAddress;
		memoryTable.setItems(initializeMemoryTable(tableRootAddress));
//		memSelection.clearAndSelect(addrOffset >> 2);
//		memSelection.getTableView().scrollTo(addrOffset >> 2);
		setMemoryButtonStates();
	}

	private static int getAddress(String orig) {
		try {
			String dest = orig.toLowerCase().replaceAll("x", "");
			int destAddr = Integer.parseInt(dest, 16);
			if (destAddr < CPUController.MEMORY_SIZE - 1) return destAddr;
			AlertDialog.error("Address exceeds memory (" + (CPUController.MEMORY_SIZE - 1) + " Bytes)");
		} catch (NumberFormatException e) {
			AlertDialog.error("Failed to parse 32bit hexadecimal address (use without 0x-prefix)");
			System.out.println(e);
		}
		
		return -1;
	}


	private static int readMemory(int add) {
		return SimulatorManager.getSim().getMemory().read(add).toInt();
	}

	/**
	 * Disables/Enables previous/next buttons depending on tableRootAddress
	 */
	private void setMemoryButtonStates() {
		if (tableRootAddress == 0) {
			buttonPreviousTable.setDisable(true);
			buttonNextTable.setDisable(false);
		} else if (tableRootAddress == CPUController.MEMORY_SIZE - BYTES_PR_PAGE) {
			buttonPreviousTable.setDisable(false);
			buttonNextTable.setDisable(true);
		} else {
			buttonPreviousTable.setDisable(false);
			buttonNextTable.setDisable(false);
		}
	}


	/**
	 * Sets up memory table
	 * 
	 * @param startAddr: Start address that will be displayed as index 0 in
	 *                   resulting list
	 * @return Returns a new ObservableList consisting of (up to) BYTES_PR_PAGE / 4
	 *         rows.
	 */
	private static ObservableList<MemoryTableHelper> initializeMemoryTable(int startAddr) {
		ObservableList<MemoryTableHelper> memTable = FXCollections.observableArrayList();
		for (int addrOffset = 0; addrOffset < BYTES_PR_PAGE; addrOffset +=8) {
			if (startAddr + addrOffset >= CPUController.MEMORY_SIZE)
				break;
			memTable.add(new MemoryTableHelper(startAddr + addrOffset
					, readMemory(startAddr + addrOffset)
					, readMemory(startAddr + addrOffset +1)
					, readMemory(startAddr + addrOffset +2)
					, readMemory(startAddr + addrOffset +3)
					, readMemory(startAddr + addrOffset +4)
					, readMemory(startAddr + addrOffset +5)
					, readMemory(startAddr + addrOffset +6)
					, readMemory(startAddr + addrOffset +7)
					));
		}
		return memTable;
	}

	public void buttonRefreshTableOnAction() {
		memoryTable.setItems(initializeMemoryTable(tableRootAddress));
	}
	
	public void closeProgram() {
		primaryStage.close();
	}
	
	// Used to pass stage from main
	public void setStage(Stage stage) {
		this.primaryStage = stage;
		memoryTable.setItems(initializeMemoryTable(0));
	}
}
