
package absimth.sim.gui;

import java.net.URL;
import java.util.ResourceBundle;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.hardware.memory.PhysicalAddress;
import absimth.sim.gui.helper.AbsimthEvent;
import absimth.sim.gui.helper.MemoryTableHelper;
import absimth.sim.gui.helper.UIUtil;
import absimth.sim.memoryController.model.ECCMemoryFaultModel;
import absimth.sim.utils.Bits;
import absimth.sim.utils.HexaFormat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MemoryViewByAddressController implements Initializable {
	private static final int BYTES_PR_PAGE = 256; // 64 words
	// Keeping track of memory table
	private int tableRootAddress = 0;
	private int totalAddress = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getTotalOfAddress().intValue() -1;
	
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
	public Label labelPhysicalAddress;
	
	//Properties
	public Label labelAddressTotal;
	public TextField textFieldAddress;
	public TextField textFieldModule;
	public TextField textFieldRank;
	
	public TextField textFieldBankGroup;
	public TextField textFieldBank;
	public TextField textFieldRow;
	public TextField textFieldColumn;
	public TextField textFieldCell;
	
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

	/**
	 * Runs in start of guiController. Initializes registerTable, memoryTable and
	 * programTable.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		labelAddressTotal.setText("Address Total: " + HexaFormat.f(SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getTotalOfAddress().intValue()));
		// Memory table
		memoryTable.getSelectionModel().setCellSelectionEnabled(true);
		memoryColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
		memoryColumn.setStyle("-fx-background-color: #E0E0E0");
		memoryDataColumn0.setCellValueFactory(new PropertyValueFactory<>("x0"));
		memoryDataColumn1.setCellValueFactory(new PropertyValueFactory<>("x1"));
		memoryDataColumn2.setCellValueFactory(new PropertyValueFactory<>("x2"));
		memoryDataColumn3.setCellValueFactory(new PropertyValueFactory<>("x3"));
		memoryDataColumn4.setCellValueFactory(new PropertyValueFactory<>("x4"));
		memoryDataColumn5.setCellValueFactory(new PropertyValueFactory<>("x5"));
		memoryDataColumn6.setCellValueFactory(new PropertyValueFactory<>("x6"));
		memoryDataColumn7.setCellValueFactory(new PropertyValueFactory<>("x7"));
		
		    
//		memoryDataColumn0.setCellFactory(TextFieldTableCell.forTableColumn());
		memoryDataColumn0.setCellFactory(tc->createColorFormat(0));
		memoryDataColumn1.setCellFactory(tc->createColorFormat(1));
		memoryDataColumn2.setCellFactory(tc->createColorFormat(2));
		memoryDataColumn3.setCellFactory(tc->createColorFormat(3));
		memoryDataColumn4.setCellFactory(tc->createColorFormat(4));
		memoryDataColumn5.setCellFactory(tc->createColorFormat(5));
		memoryDataColumn6.setCellFactory(tc->createColorFormat(6));
		memoryDataColumn7.setCellFactory(tc->createColorFormat(7));
		
		
		memoryDataColumn0.setOnEditCommit((TableColumn.CellEditEvent<MemoryTableHelper, String> t) -> {
			labelPhysicalAddress.setText("ABC");
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
		
		memoryDataColumn6.setOnEditCommit((TableColumn.CellEditEvent<MemoryTableHelper, String> t) -> {
			setNewValue(t, 6);
		});
		
		memoryDataColumn7.setOnEditCommit((TableColumn.CellEditEvent<MemoryTableHelper, String> t) -> {
			setNewValue(t, 7);
		});
		
	}


	private static TextFieldTableCell<MemoryTableHelper, String> createColorFormat(int column) {
		return new TextFieldTableCell<>(TextFormatter.IDENTITY_STRING_CONVERTER) {
			@Override
			public void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
					MemoryTableHelper mem = getTableView().getItems().get(getIndex());
					ECCMemoryFaultModel rep = SimulatorManager.getSim().getMemoryController().getMemoryStatus().getFromAddress( mem.getBaseAddress()+column);
					UIUtil.printCellMemoryStatus(this, rep);
				} else {
//					UIUtil.printCellMemoryStatus(this, null);
				}
			}

		
		};
	}
	
	
	public void cellTableOnMouseClick(MouseEvent event) {
//		 System.out.println("Clicked on " + (cellTable.getSelectionModel().getSelectedCells().get(0)).getRow() + ","+(cellTable.getSelectionModel().getSelectedCells().get(0)).getColumn()); 
		 int x = memoryTable.getSelectionModel().getSelectedCells().get(0).getRow();
		 int y = memoryTable.getSelectionModel().getSelectedCells().get(0).getColumn()-1;
//		 System.out.println();
		 if(event.getTarget().getClass().getSimpleName().compareTo("TableColumnHeader") != 0 && x>=0 && y>=0 && x<32 && y < 8) {
			 System.out.println("ok - " + x + ","+ y);
			 int addrOffset = (x*8)+y;
			 int address = tableRootAddress + addrOffset;
			 textFieldAddress.setText(HexaFormat.f(address));
			 
			 PhysicalAddress pa = SimulatorManager.getSim().getPhysicalAddressService().getPhysicalAddress((long)address);
			 textFieldModule.setText(""+pa.getModule());
			 textFieldRank.setText(""+pa.getRank());
			 textFieldBankGroup.setText(""+pa.getBankGroup());
			 
			 textFieldBank.setText(""+pa.getBank());
			 textFieldRow.setText(""+pa.getRow());
			 textFieldColumn.setText(""+pa.getColumn());
			 textFieldCell.setText(""+(pa.getPagePosition()*8 ==0?0:pa.getPagePosition()*8-1) + "-" +((pa.getPagePosition()+1)*8-1));
		 }
	}

	
	private void setNewValue(TableColumn.CellEditEvent<MemoryTableHelper, String> t, int p) {
		int row = t.getTablePosition().getRow();
		int destAddr = UIUtil.getAddressFromField(t.getNewValue());
		if (destAddr < 0) return;
		try {
			SimulatorManager.getSim().getMemory().write((long)tableRootAddress + (row*8) + p, Bits.from(destAddr));
		}catch (Exception e) {
			System.err.println(e);
		}
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
		int destAddr = UIUtil.getAddressFromField(textFieldAddr.getText());
		if (destAddr < 0) return;
		textFieldAddr.setText("");
		tableRootAddress = BYTES_PR_PAGE * (destAddr / BYTES_PR_PAGE);
		memoryTable.setItems(initializeMemoryTable(tableRootAddress));
		setMemoryButtonStates();
	}

	private static int readMemory(int add) {
		try {
			if(1048810 == add) 
				return SimulatorManager.getSim().getMemory().read(add).toInt();
			return SimulatorManager.getSim().getMemory().read(add).toInt();
		}catch (Exception e) {
			System.err.println(e);
			return 0;
		}
	}

	/**
	 * Disables/Enables previous/next buttons depending on tableRootAddress
	 */
	private void setMemoryButtonStates() {
		if (tableRootAddress == 0) {
			buttonPreviousTable.setDisable(true);
			buttonNextTable.setDisable(false);
		} else if (tableRootAddress == totalAddress - BYTES_PR_PAGE) {
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
	private ObservableList<MemoryTableHelper> initializeMemoryTable(int startAddr) {
		ObservableList<MemoryTableHelper> memTable = FXCollections.observableArrayList();
		for (int addrOffset = 0; addrOffset < BYTES_PR_PAGE; addrOffset +=8) {
			if (startAddr + addrOffset >= totalAddress)
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
		stage.setTitle("Memory View by Address");
		stage.addEventHandler(AbsimthEvent.ABSIMTH_UPDATE_EVENT, event -> onAbsimthUpdateEvent());
		memoryTable.setItems(initializeMemoryTable(0));
	}


	private void onAbsimthUpdateEvent() {
		buttonRefreshTableOnAction();
	}
}
