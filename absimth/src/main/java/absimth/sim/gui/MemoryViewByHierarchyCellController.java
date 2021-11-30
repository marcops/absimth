
package absimth.sim.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.hardware.memory.CellConfModel;
import absimth.sim.configuration.model.hardware.memory.PhysicalAddress;
import absimth.sim.gui.helper.AbsimthEvent;
import absimth.sim.gui.helper.UIUtil;
import absimth.sim.gui.model.Cell3DInfoModel;
import absimth.sim.memoryController.model.ECCMemoryFaultModel;
import absimth.sim.memoryController.model.ECCMemoryFaultType;
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
	private Integer posHeight;
	
	public TextField txtAddress;
	public TextField txtModule;
	public TextField txtRank;
	public TextField txtChip;
	public TextField txtBankGroup;
	public TextField txtBank;
	public TextField txtRow;
	public TextField txtColumn;
	public TextField txtCellData;
	public TextField txtCellDataBit;
	public ComboBox<Integer> comboBoxCellHeight;
	public Label labelCellPosition;
	
	public Button buttonPreviousTable;
	public Button buttonUpTable;
	public Button buttonDownTable;
	public Button buttonNextTable;
	
	private Integer posCol;
	private Integer posRow;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.posHeight = 0;
		this.posCol = 0;
		this.posRow = 0;
		
		txtRow.setText(""+posCol);
		txtColumn.setText(""+posRow);
		
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
		             
	             if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
	            	 	PhysicalAddress pa = SimulatorManager.getSim()
	 						.getPhysicalAddressService()
	 						.getPhysicalAddressReverse(module, rank, bankGroup, bank, getIndex()+posRow, col+posCol, posHeight);
	            	 	
	            	 	ECCMemoryFaultModel rep = SimulatorManager.getSim().getMemoryController().getMemoryStatus().getFromAddress(pa.getPAddress());
						UIUtil.printCellMemoryStatus(this, rep, posHeight);
					} else {
//						UIUtil.printCellMemoryStatus(this, null);
					}
//		             setTooltip(new Tooltip("Live is short, make most of it..!"));
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
			 	
				PhysicalAddress pa = SimulatorManager.getSim().getPhysicalAddressService()
						.getPhysicalAddressReverse(module, rank, bankGroup, bank, row, col, posHeight);

				updateFieldFromPhysicalAddress(pa);
		 }
	}

	
	private void updateFieldEmpty() {
		txtAddress.setText("");
		txtModule.setText("" + module);
		txtRank.setText("" + rank);
		txtChip.setText("" + chipPos);
		txtBankGroup.setText("" + bankGroup);
		txtBank.setText("" + bank);
		txtRow.setText("" + posRow);
		txtColumn.setText("" + posCol);
		txtCellData.setText("");
		labelCellPosition.setText("Cell Position " + posRow+ " x " + posCol + " x o"
				+ "" + posHeight);
		txtCellDataBit.setText("");
	}

	
	private void updateFieldFromPhysicalAddress(PhysicalAddress pa) {
		txtAddress.setText(HexaFormat.f((int) pa.getPAddress()));
		txtModule.setText("" + pa.getModule());
		txtRank.setText("" + pa.getRank());
		txtChip.setText("" + chipPos);
		txtBankGroup.setText("" + pa.getBank());
		txtBank.setText("" + pa.getBank());
		txtRow.setText("" + pa.getRow());
		txtColumn.setText("" + pa.getColumn());
		Bits data = readMemory((int) pa.getPAddress());
		Bits sub = data.subbit(chipPos*BYTE_SIZE, BYTE_SIZE);
		txtCellData.setText(HexaFormat.f(sub.toInt(), 2));
		labelCellPosition.setText("Cell Position " + pa.getRow() + " x " + pa.getColumn() + " x " + posHeight);
		txtCellDataBit.setText(sub.toBitString());
	}

	private static Bits readMemory(int add) {
		try {
			return SimulatorManager.getSim().getMemory().read(add);
		}catch (Exception e) {
			System.err.println(e);
			return new Bits();
		}
	}
	
	private static Integer getIntegerPosition(String orig) {
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
		int y = getIntegerPosition(txtRow.getText());
		if (y == -1) return;
		posRow = y;
		moveRow();
		txtRow.positionCaret(txtRow.getText().length());
	}

	private void moveCol() {
		int colLimit= (cell.getColumns()-COLUMN_SIZE);
		if (posCol > colLimit) posCol = colLimit;
		if (posCol < 0) posCol = 0;
		
		txtColumn.setText(""+posCol);
		buttonPreviousTable.setDisable(posCol == 0);
		buttonNextTable.setDisable(posCol >= colLimit);
		updateTableMemory();
		
	}
	
	public void txtAddressOnAction() {
		int destAddr = UIUtil.getAddressFromField(txtAddress.getText());
		if (destAddr < 0) return;
		PhysicalAddress pa = SimulatorManager.getSim().getPhysicalAddressService().getPhysicalAddress((long)destAddr);
		posRow = (int) pa.getRow();
		posCol = (int) pa.getColumn();
		updateTableMemory();
		updateFieldFromPhysicalAddress(pa);
		txtAddress.positionCaret(txtAddress.getText().length());
	}
	
	public void txtFieldMemColOnAction() {
		int x = getIntegerPosition(txtColumn.getText());
		if (x == -1) return;
		posCol = x;
		moveCol();
		txtColumn.positionCaret(txtColumn.getText().length());
	}

	private void moveRow() {
		int rowLimit= (cell.getRow()-ROW_SIZE);
		if (posRow > rowLimit) posRow = rowLimit;
		if (posRow < 0) posRow = 0;
		
		txtColumn.setText(""+posRow);
		buttonUpTable.setDisable(posRow == 0);
		buttonDownTable.setDisable(posRow >= rowLimit);
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
		posRow -= ROW_SIZE;
		moveRow();

	}
	public void downMemoryTable() {
		posRow += ROW_SIZE;
		moveRow();
	}

	public void comboBoxCellHeightOnAction() {
		posHeight = comboBoxCellHeight.getSelectionModel().getSelectedItem();
		updateTableMemory();
	}
	
	public void setStage(Stage stage, int module, int rank, int chipPos, int bankGroup, int bank, PhysicalAddress pa) {
//		this.stage = stage;
		this.module = module;
		this.rank = rank;
		this.chipPos = chipPos;
		this.bankGroup = bankGroup;
		this.bank = bank;
		stage.setTitle("MV by Cell");
		
		if(pa != null) {
			posCol = (int) pa.getColumn();
			posRow = (int) pa.getRow();
			moveCol();
			moveRow();
			
			updateTableMemory();
			updateFieldFromPhysicalAddress(pa);
		} else {
			updateTableMemory();
		}
		
		stage.addEventHandler(AbsimthEvent.ABSIMTH_UPDATE_EVENT, event -> onAbsimthUpdateEvent());
	}

	private void onAbsimthUpdateEvent() {
		updateTableMemory();
	}

	private void updateTableMemory() {
		ObservableList<List<String>> rowData = getMemoryTable(posHeight);
		cellTable.setItems(rowData);
		cellTable.refresh();
		updateFieldEmpty();
	}

	private ObservableList<List<String>>  getMemoryTable( int cellPos) {
		ObservableList<List<String>> rowData = FXCollections.observableArrayList();
		for (int i = 0; i < ROW_SIZE; i++) {
			List<String> row = new ArrayList<>();
			row.add("" + i);
			for (int j = 0; j < COLUMN_SIZE; j++) {
				PhysicalAddress pa = SimulatorManager.getSim()
						.getPhysicalAddressService()
						.getPhysicalAddressReverse(module, rank, bankGroup, bank, i+posRow, j+posCol, posHeight);
				
				Bits data = readMemory((int) pa.getPAddress());
				boolean bit = data.get((chipPos*BYTE_SIZE)+cellPos);
				row.add(bit?"1":"0");
			}
			rowData.add(row);
		}
		return rowData;
	}
	
	public void btn3dView() {
		List<List<List<Cell3DInfoModel>>> dData = new ArrayList<>();
		for(int i=0;i<8;i++) {
			List<List<Cell3DInfoModel>> rowData = toCell3D(i);
			dData.add(rowData);
		}
		UIUtil.openMemoryCell3D(dData);
	}

	private List<List<Cell3DInfoModel>> toCell3D(int pos) {
		List<List<Cell3DInfoModel>> result = new ArrayList<>();

		ObservableList<List<String>> rowData = getMemoryTable(pos);

		for (int i = 0; i < rowData.size(); i++) {
			List<String> lsty = rowData.get(i);
			
			List<Cell3DInfoModel> axisX = new ArrayList<>();
			for (int j = 1; j < lsty.size(); j++) {
				char text = lsty.get(j).charAt(0);
				if (text == '1' || text == '0') {
					PhysicalAddress pa = SimulatorManager.getSim()
							.getPhysicalAddressService()
							.getPhysicalAddressReverse(module, rank, bankGroup, bank, i+posRow, j+posCol-1, posHeight);
					
					ECCMemoryFaultModel rep = SimulatorManager.getSim().getMemoryController().getMemoryStatus().getFromAddress(pa.getPAddress());
					
					axisX.add(Cell3DInfoModel
							.builder()
							.text(String.valueOf(text))
							.physicalAddress(pa)
							.status(get3dStatus(rep, pos)).build());
				} else {
					System.err.println("i" + i + ",y" + j + ", " + text);
				}
			}
			result.add(axisX);
		}

		return result;
	}

	private static ECCMemoryFaultType get3dStatus(ECCMemoryFaultModel rep, int pos) {
		if(rep ==null) return ECCMemoryFaultType.NONE;
		if(!rep.getPosition().contains(pos))  return ECCMemoryFaultType.NONE;
		return rep.getFaultType();
	}
}
