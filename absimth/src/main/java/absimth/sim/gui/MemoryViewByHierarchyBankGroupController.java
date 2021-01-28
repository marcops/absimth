
package absimth.sim.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.hardware.memory.BankConfModel;
import absimth.sim.configuration.model.hardware.memory.BankGroupConfModel;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MemoryViewByHierarchyBankGroupController implements Initializable {
	private Stage stage;
	public GridPane gridPaneModule;
	public static final Integer WIDTH_ROW_EMPTY = 10;
	public static final Integer HEIGHT_COL_EMPTY = 10;
	public static final Integer WIDTH_COL_BANK = 50;
	public static final Integer HEIGHT_ROW_BANK = 30;
	
	private static final String BACKGROUND_BLACK= "-fx-background-color: black; ";
	private static final String FONT_COLOR_WHITE =  "-fx-text-fill: white; ";
	private static final String BACKGROUND_DEFAULT = "-fx-background-color: #FFFFFF; ";
	private static final String BACKGROUND_MODULE = "-fx-background-color: #32CD32; ";
	private static final String BACKGROUND_BANKGROUP = "-fx-background-color: black; ";
	private static final String BACKGROUND_BANK = "-fx-background-color: grey; ";
	
	private Integer chipPos;
	private Integer rank;
	private Integer module;
	
	
	public static int getBankGroupColumnSize() {
		BankGroupConfModel bank = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getModule().getRank().getChip().getBankGroup();
		int columnSize = bank.getAmount()/2;
		if (columnSize == 0) columnSize = 1;
		return columnSize;
	}
	
	public static int getBankGroupRowSize() {
		BankGroupConfModel bank = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getModule().getRank().getChip().getBankGroup();
		int rowSize = bank.getAmount()/2;
		if(bank.getAmount()%2!=0) rowSize++;
		return rowSize;
	}
	
	private static int getBankColumnSize() {
		BankConfModel bank = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory()
				.getModule().getRank().getChip().getBankGroup().getBank();
		int columnSize = bank.getAmount()/2;
		if (columnSize == 0) columnSize = 1;
		return columnSize;
	}
	
	private static int getBankRowSize() {
		BankConfModel bank = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory()
				.getModule().getRank().getChip().getBankGroup().getBank();
		int rowSize = bank.getAmount()/2;
		if(bank.getAmount()%2!=0) rowSize++;
		return rowSize;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		createBankGroup(gridPaneModule, getBankGroupRowSize(), getBankGroupColumnSize());
	}

	private static void createBankGroup(GridPane p, int rowSize, int columnSize) {
		int minWidth = (WIDTH_COL_BANK+WIDTH_ROW_EMPTY)*getBankColumnSize()+WIDTH_ROW_EMPTY ;
		createColumnsRow(p,getBankGroupColumnSize(), minWidth);
		int rowPos = 0;
		createEmptyRowGroupBank(p, rowPos, columnSize, BACKGROUND_MODULE);
		for (int i = 0; i < rowSize; i++) {
			rowPos++;
			
			int colPos = 0;
			p.add(createEmptyColumnBankGroup(BACKGROUND_MODULE), colPos, rowPos);
			for(int j=0;j<columnSize;j++) {
				colPos++;
				p.add(createBankGroupItem(i*rowSize+j), colPos, rowPos);
				colPos++;
				p.add(createEmptyColumnBankGroup(BACKGROUND_MODULE), colPos, rowPos);
			}
			RowConstraints r = new RowConstraints();
			r.setMinHeight((HEIGHT_ROW_BANK+30)*getBankGroupRowSize());
			r.setFillHeight(true);
			r.setVgrow(Priority.ALWAYS);
			p.getRowConstraints().add(r);
			rowPos++;
			createEmptyRowGroupBank(p, rowPos, columnSize, BACKGROUND_MODULE);
		}
	}
	
	
	private static Pane createEmptyColumnBankGroup(String color) {
		Pane backPanel = new Pane();
		backPanel.setStyle(color);
		return backPanel;
		
	}

	private static Pane createBankGroupItem(int bankPos) {
		Label l = new Label();
		l.setText("BankGroup " + bankPos);
		l.setStyle(FONT_COLOR_WHITE);
		l.setMinHeight(20);
		l.setMaxHeight(20);
		l.setAlignment(Pos.TOP_LEFT);
		
		GridPane grid = createBank();
		
		HBox bh = new HBox();
		bh.setStyle(BACKGROUND_BANKGROUP);
		bh.setFillHeight(true);

		VBox bv = new VBox();
		bv.setFillWidth(true);
		VBox.setVgrow(grid, Priority.ALWAYS);
		bv.getChildren().add(l);
		bv.getChildren().add(grid);
		
		HBox.setHgrow(bv, Priority.ALWAYS);
		bh.getChildren().add(bv);
		return bh;
	}

	private static GridPane createBank() {
		int columnSize = getBankColumnSize();
		int rowSize = getBankRowSize();
		int rowPos = 0;
		GridPane pane = new GridPane();
		createColumnsRow(pane,getBankColumnSize(), WIDTH_COL_BANK);
		
		createEmptyRowGroupBank(pane, rowPos, columnSize, BACKGROUND_BANKGROUP);
		for (int i = 0; i < rowSize; i++) {
			rowPos++;
			
			int colPos = 0;
			pane.add(createEmptyColumnBankGroup(BACKGROUND_BANKGROUP), colPos, rowPos);
			for(int j=0;j<columnSize;j++) {
				colPos++;
				pane.add(createBankItem(i*rowSize+j), colPos, rowPos);
				colPos++;
				pane.add(createEmptyColumnBankGroup(BACKGROUND_BANKGROUP), colPos, rowPos);
			}
			RowConstraints r = new RowConstraints();
			r.setMinHeight(HEIGHT_ROW_BANK);
			r.setFillHeight(true);
			r.setVgrow(Priority.ALWAYS);
			pane.getRowConstraints().add(r);
			rowPos++;
			createEmptyRowGroupBank(pane, rowPos, columnSize, BACKGROUND_BANKGROUP);
		}
		return pane;
	}

	private static Pane createBankItem(int bankPos) {
		Pane p = new Pane();
		
		Label l = new Label();
		l.setText("Bank " + bankPos);
		l.setStyle(FONT_COLOR_WHITE);
		l.setMinHeight(20);
		l.setMaxHeight(20);
		l.setAlignment(Pos.TOP_LEFT);

		VBox vbox = new VBox();
		vbox.setFillWidth(true);		
		vbox.setStyle(BACKGROUND_BANK);
		vbox.getChildren().add(l);
		vbox.getChildren().add(p);
		return vbox;
		
	}
	private static Pane createBackgroundPanel(String color) {
		Pane backPanel = new Pane();
		backPanel.setStyle(color);
		return backPanel;
	}

	private static void createEmptyRowGroupBank(GridPane p, int row, int columnSize, String color) {
		int nOfColumn = (columnSize*2)+1;
		for(int i=0;i<nOfColumn;i++)
			p.add(createBackgroundPanel(color), i, row);
		
		RowConstraints r = new RowConstraints();
		r.setMinHeight(HEIGHT_COL_EMPTY);
		r.setMaxHeight(HEIGHT_COL_EMPTY);
		p.getRowConstraints().add(r);
	}
	
	private static void createColumnsRow(GridPane p, int columnSize, int minWidth) {
		int nOfColumn = (columnSize*2)+1;
		
		List<ColumnConstraints> lst = new ArrayList<>();
		for(int i=0;i<nOfColumn;i++) {
			if(i%2==0) lst.add(createColumnEmpty());
			else lst.add(createColumnBankGroup(minWidth));
		}
		
		p.getColumnConstraints().addAll(lst);
		p.setAlignment(Pos.TOP_CENTER);
	}
	
	private static ColumnConstraints createColumnEmpty() {
		ColumnConstraints col = new ColumnConstraints();
		col.setMinWidth(WIDTH_ROW_EMPTY);
		col.setMaxWidth(WIDTH_ROW_EMPTY);
		col.setHgrow(Priority.ALWAYS);
		return col;
	}

	private static ColumnConstraints createColumnBankGroup(int minWidth) {
		ColumnConstraints col = new ColumnConstraints();
		col.setMinWidth(minWidth);
		col.setHgrow(Priority.ALWAYS);
		col.setFillWidth(true);
		return col;
	}

	// Used to pass stage from main
	public void setStage(Stage stage,int module, int rank,  int chipPos) {
		this.stage = stage;
		this.module = module;
		this.rank = rank;
		this.chipPos = chipPos;
		stage.setTitle("MV by Bank Group/Bank - Module: " + module +", Rank: " +rank+ ", Chip: " + chipPos);
	}
}
