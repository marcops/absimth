
package absimth.sim.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.hardware.memory.CellConfModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MemoryViewByHierarchyCellController implements Initializable {
//	private Stage stage;
	public VBox mainVBox;
	public TableView<String> cellTable;
	public TableColumn<String, String> tableColumnX;
	public List<TableColumn<String, String>> tableColumn = new ArrayList<>();
	private static final Integer COLUMN_SIZE = 16;
	private CellConfModel cell;
	public Label labelCellInformation;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cell = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getModule().getRank().getChip().getBankGroup().getBank().getCell();
		
		labelCellInformation.setText("Cell " + cell.getRow() + " x " + cell.getColumns());
		
		mountBitTable();
		
	}

	private void mountBitTable() {
		tableColumnX.setStyle("-fx-background-color: #E0E0E0");
		tableColumnX.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
		
		cellTable.getSelectionModel().setCellSelectionEnabled(true);
		for (int col = 0; col < COLUMN_SIZE; col++) {
			TableColumn<String, String> tcol = new TableColumn<>(""+col);
			tableColumn.add(tcol);
			cellTable.getColumns().add(tcol);
			tcol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));

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
		}
	}
	
	/**
	 * @param event  
	 */
	public void cellTableOnMouseClick(MouseEvent event) {
//		 System.out.println("Clicked on " + (cellTable.getSelectionModel().getSelectedCells().get(0)).getRow() + ","+(cellTable.getSelectionModel().getSelectedCells().get(0)).getColumn()); 
		 int x = cellTable.getSelectionModel().getSelectedCells().get(0).getRow();
		 int y = cellTable.getSelectionModel().getSelectedCells().get(0).getColumn()-1;
//		 System.out.println();
		 if(event.getTarget().getClass().getSimpleName().compareTo("TableColumnHeader") != 0 && x>=0 && y>=0 && x<COLUMN_SIZE && y < COLUMN_SIZE) {
			 System.out.println("ok - " + x + ","+ y);
		 }
	}

	public void previousMemoryTable() {
//		tableRootAddress -= ADDRESS_PR_PAGE;
//		memSelection.clearSelection();
//		memoryTable.setItems(initializeMemoryTable(tableRootAddress));
//		setMemoryButtonStates();
	}

	/**
	 * Changes memory table view from tableRootAddress to tableRootAddress +
	 * BYTES_PR_PAGE Disables corresponding button if needed.
	 */
	public void nextMemoryTable() {
//		tableRootAddress += ADDRESS_PR_PAGE;
//		memSelection.clearSelection();
//		memoryTable.setItems(initializeMemoryTable(tableRootAddress));
//		setMemoryButtonStates();
	}
	public void gotoAddress() {}
//	public void closeProgram() {
//		stage.close();
//	}

	// Used to pass stage from main
	/**
	 * @param module  
	 * @param rank 
	 * @param chipPos 
	 * @param bankGroup 
	 * @param bank 
	 */
	public void setStage(Stage stage, int module, int rank, int chipPos, int bankGroup, int bank) {
//		this.stage = stage;
		stage.setTitle("MV by Cell");
		extracted();
	}

	private void extracted() {
		ObservableList<String> rowData = FXCollections.observableArrayList();
		for (int i = 0; i < COLUMN_SIZE; i++) {
			rowData.add("0");
		}
		cellTable.setItems(rowData);
	}
}
