
package absimth.sim.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.hardware.memory.CellConfModel;
import absimth.sim.configuration.model.hardware.memory.PhysicalAddress;
import absimth.sim.utils.Bits;
import absimth.sim.utils.HexaFormat;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MemoryViewByHierarchyCellController implements Initializable {
//	private Stage stage;
	public VBox mainVBox;
	public TableView<List<String>> cellTable;
	public TableColumn<List<String>, String> tableColumnX;
	public List<TableColumn<List<String>, String>> tableColumn = new ArrayList<>();
	private static final Integer COLUMN_SIZE = 16;
	private static final Integer ROW_SIZE = 16;
	private static final Integer BYTE_SIZE = 8;
	
	private CellConfModel cell;
	public Label labelCellInformation;
	
	private Integer module;
	private Integer rank;
	private Integer chipPos;
	private Integer bankGroup;
	private Integer bank;
	private Integer cellHeight;
	
	public TextField txtAddress;
	public TextField txtModule;
	public TextField txtRank;
	public TextField txtChip;
	public TextField txtBankGroup;
	public TextField txtBank;
	public TextField txtRow;
	public TextField txtColumn;
	public TextField txtCellHeight;
	public TextField txtCellData;
	public ComboBox<Integer> comboBoxCellHeight;
	public TextField txtFieldMemRow;
	public TextField txtFieldMemCol;
	public Label labelCellPosition;
	
	public Button buttonPreviousTable;
	public Button buttonUpTable;
	public Button buttonDownTable;
	public Button buttonNextTable;
	
	private Integer posCol;
	private Integer posRow;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.cellHeight = 0;
		this.posCol = 0;
		this.posRow = 0;
		
		txtFieldMemRow.setText(""+posCol);
		txtFieldMemCol.setText(""+posRow);
		
		cell = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getModule().getRank().getChip().getBankGroup().getBank().getCell();
		for(int i=0;i<BYTE_SIZE;i++) 
			comboBoxCellHeight.getItems().add(i);

		labelCellInformation.setText("Cell " + cell.getRow() + " x " + cell.getColumns());
		
		mountBitTable();
		
		comboBoxCellHeight.getSelectionModel().select(0);
		
	}

	private void mountBitTable() {
		tableColumnX.setStyle("-fx-background-color: #E0E0E0");
		tableColumnX.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
		
		cellTable.getSelectionModel().setCellSelectionEnabled(true);
		for (int col = 0; col < COLUMN_SIZE; col++) 
			createColumn(col);
	}

	private void createColumn(int col) {
		TableColumn<List<String>, String> tcol = new TableColumn<>(""+col);
		tcol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(col+1)));
		tcol.setCellFactory(t-> {
			return new TableCell<>() {
				 @Override
		         protected void updateItem(String item, boolean empty) {
		             super.updateItem(item, empty);
		             setText( item );
		             setTooltip(new Tooltip("Live is short, make most of it..!"));
		          }
			};				
		});
		
		tableColumn.add(tcol);
		cellTable.getColumns().add(tcol);
	}
	
	/**
	 * @param event  
	 */
	public void cellTableOnMouseClick(MouseEvent event) {
		//TODO SOMar X e Y
		 int row = cellTable.getSelectionModel().getSelectedCells().get(0).getRow();
		 int col = cellTable.getSelectionModel().getSelectedCells().get(0).getColumn()-1;
		 if (event.getTarget().getClass().getSimpleName().compareTo("TableColumnHeader") != 0 && row >= 0 && col >= 0 && row < ROW_SIZE && col < COLUMN_SIZE) {
			 	col+=posCol;
			 	row+=posRow;
			 	labelCellPosition.setText("Cell Position " + row + " x " + col);
				PhysicalAddress pa = SimulatorManager.getSim().getPhysicalAddressService()
						.getPhysicalAddressReverse(module, rank, bankGroup, bank, row, col);

				txtAddress.setText(HexaFormat.f((int) pa.getPAddress()));
				txtModule.setText("" + pa.getModule());
				txtRank.setText("" + pa.getRank());
				txtChip.setText("" + chipPos);
				txtBankGroup.setText("" + pa.getBank());
				txtBank.setText("" + pa.getBank());
				txtRow.setText("" + pa.getRow());
				txtColumn.setText("" + pa.getColumn());
				txtCellHeight.setText("" + cellHeight);
				Bits data = readMemory((int) pa.getPAddress());
				txtCellData.setText(HexaFormat.f(data.subbit(chipPos*BYTE_SIZE, BYTE_SIZE).toInt(), 2));
		 }
	}

	private static Bits readMemory(int add) {
		try {
			return SimulatorManager.getSim().getMemory().read(add);
		}catch (Exception e) {
			System.err.println(e);
			return new Bits();
		}
	}
	
	private static Integer getAddress(String orig) {
		try {
			String digits = orig.replaceAll("[^0-9]", "");
			return Integer.parseInt(digits);
		} catch (NumberFormatException e) {
			//AlertDialog.error("Failed to parse integer");
			System.out.println(e);
		}
		return -1;
	}
	
	public void txtFieldMemRowOnAction() {
		int y = getAddress(txtFieldMemRow.getText());
		if (y == -1) return;
		posRow = y;
		moveRow();
	}

	private void moveCol() {
		int colLimit= (cell.getColumns()-COLUMN_SIZE);
		if (posCol > colLimit) posCol = colLimit;
		if (posCol < 0) posCol = 0;
		
		txtFieldMemCol.setText(""+posCol);
		buttonPreviousTable.setDisable(posCol == 0);
		buttonNextTable.setDisable(posCol >= colLimit);
		updateTableMemory();
		
	}
	public void txtFieldMemColOnAction() {
		int x = getAddress(txtFieldMemCol.getText());
		if (x == -1) return;
		posCol = x;
		moveCol();
	}

	private void moveRow() {
		int rowLimit= (cell.getRow()-ROW_SIZE);
		if (posRow > rowLimit) posRow = rowLimit;
		if (posRow < 0) posRow = 0;
		
		txtFieldMemRow.setText(""+posRow);
		buttonDownTable.setDisable(posRow == 0);
		buttonUpTable.setDisable(posRow >= rowLimit);
		updateTableMemory();
	}
	
	public void previousMemoryTable() {
		posCol -= COLUMN_SIZE;
		moveCol();
	}
	
	public void nextMemoryTable() {
		posCol += COLUMN_SIZE;
		moveCol();
	}

	public void upMemoryTable() {

	}
	public void downMemoryTable() {}

	public void comboBoxCellHeightOnAction() {
		cellHeight = comboBoxCellHeight.getSelectionModel().getSelectedItem();
		updateTableMemory();
	}
	
	public void setStage(Stage stage, int module, int rank, int chipPos, int bankGroup, int bank) {
//		this.stage = stage;
		this.module = module;
		this.rank = rank;
		this.chipPos = chipPos;
		this.bankGroup = bankGroup;
		this.bank = bank;
		stage.setTitle("MV by Cell");
		updateTableMemory();
	}

	private void updateTableMemory() {
		ObservableList<List<String>> rowData = FXCollections.observableArrayList();
		for (int i = 0; i < ROW_SIZE; i++) {
			List<String> row = new ArrayList<>();
			row.add("" + i);
			for (int j = 0; j < COLUMN_SIZE; j++) {
				PhysicalAddress pa = SimulatorManager.getSim()
						.getPhysicalAddressService()
						.getPhysicalAddressReverse(module, rank, bankGroup, bank, i+posCol, j);
				
				Bits data = readMemory((int) pa.getPAddress());
				boolean bit = data.get((chipPos*BYTE_SIZE)+cellHeight);
				row.add(bit?"1":"0");
			}
			rowData.add(row);
		}
		cellTable.setItems(rowData);
	}
}
