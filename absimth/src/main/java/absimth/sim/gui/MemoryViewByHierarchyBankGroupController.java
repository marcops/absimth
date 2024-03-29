
package absimth.sim.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.hardware.memory.BankConfModel;
import absimth.sim.configuration.model.hardware.memory.BankGroupConfModel;
import absimth.sim.gui.helper.AbsimthEvent;
import absimth.sim.gui.helper.UIColors;
import absimth.sim.gui.helper.UIUtil;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MemoryViewByHierarchyBankGroupController implements Initializable {
//	private Stage stage;
	public GridPane gridPaneModule;
	public static final Integer WIDTH_ROW_EMPTY = 10;
	public static final Integer HEIGHT_COL_EMPTY = 10;
	public static final Integer WIDTH_COL_BANK = 70;
	public static final Integer HEIGHT_ROW_BANK = 20;
	
	private Integer chipPos;
	private Integer rank;
	private Integer module;
	
	
	private static Integer getTotalBankGroup() {
		return SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getModule().getRank().getChip().getBankGroup().getAmount();
	}
	
	private static Integer getTotalBank() {
		return SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getModule().getRank().getChip().getBankGroup().getBank().getAmount();
	}
	
	public static int getBankGroupColumnSize() {
		BankGroupConfModel bank = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getModule().getRank().getChip().getBankGroup();
		Double colSize = Math.sqrt(bank.getAmount());
		if(colSize.compareTo(Double.valueOf(colSize.intValue()))!=0)colSize++;
		return colSize.intValue();
	}
	
	public static int getBankGroupRowSize() {
		BankGroupConfModel bank = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getModule().getRank().getChip().getBankGroup();
		Double rowSize = Math.sqrt(bank.getAmount());
		if(rowSize - Double.valueOf(rowSize.intValue()) > 0.5D ) rowSize++;
		return rowSize.intValue();
	}
	
	private static int getBankColumnSize() {
		BankConfModel bank = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory()
				.getModule().getRank().getChip().getBankGroup().getBank();
		Double colSize = Math.sqrt(bank.getAmount());
		if(colSize.compareTo(Double.valueOf(colSize.intValue()))!=0)colSize++;
		return colSize.intValue();
	}
	
	private static int getBankRowSize() {
		BankConfModel bank = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory()
				.getModule().getRank().getChip().getBankGroup().getBank();
		Double rowSize = Math.sqrt(bank.getAmount());
		if(rowSize - Double.valueOf(rowSize.intValue()) > 0.5D ) rowSize++;
		return rowSize.intValue();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	private void createBankGroup(GridPane p, int rowSize, int columnSize) {
		p.setStyle(UIColors.BORDER_GREEN + " -fx-border-width: 5;");
		int minWidth = (WIDTH_COL_BANK+WIDTH_ROW_EMPTY)*getBankColumnSize()+WIDTH_ROW_EMPTY ;
		createColumnsRow(p,getBankGroupColumnSize(), minWidth);
		int rowPos = 0;
		createEmptyRowGroupBank(p, rowPos, columnSize, UIColors.BACKGROUND_BLACK);
		int totBank = 0;
		for (int i = 0; i < rowSize; i++) {
			rowPos++;
			
			int colPos = 0;
			p.add(createEmptyColumnBankGroup(UIColors.BACKGROUND_BLACK), colPos, rowPos);
			for(int j=0;j<columnSize;j++) {
				
				if(getTotalBankGroup()<=totBank) {
					colPos++;
					p.add(createEmptyColumnBankGroup(UIColors.BACKGROUND_BLACK), colPos, rowPos);
					colPos++;
					p.add(createEmptyColumnBankGroup(UIColors.BACKGROUND_BLACK), colPos, rowPos);
					continue;
				}
				colPos++;
				p.add(createBankGroupItem(totBank), colPos, rowPos);
				colPos++;
				
				p.add(createEmptyColumnBankGroup(UIColors.BACKGROUND_BLACK), colPos, rowPos);
				totBank++;
			}
			RowConstraints r = new RowConstraints();
			r.setMinHeight((HEIGHT_ROW_BANK+30)*getBankRowSize());
			r.setFillHeight(true);
			r.setVgrow(Priority.ALWAYS);
			p.getRowConstraints().add(r);
			rowPos++;
			createEmptyRowGroupBank(p, rowPos, columnSize, UIColors.BACKGROUND_BLACK);
		}
	}
	
	
	private static Pane createEmptyColumnBankGroup(String color) {
		Pane backPanel = new Pane();
		backPanel.setStyle(color);
		return backPanel;
		
	}

	private Pane createBankGroupItem(int bankGroupPos) {
		Label l = new Label();
		l.setText("BankGroup " + bankGroupPos);
		l.setStyle(UIColors.FONT_COLOR_WHITE);
		l.setMinHeight(20);
		l.setMaxHeight(20);
		l.setAlignment(Pos.TOP_LEFT);
		
		GridPane grid = createBank(bankGroupPos);
		
		HBox bh = new HBox();
		boolean contain = SimulatorManager.getSim().getMemoryController().getMemoryStatus().containErrorInsideBankGroup(module, rank, chipPos, bankGroupPos);
		String style = contain ? UIColors.BACKGROUND_RED: UIColors.BACKGROUND_GREY_DARK;
		
		bh.setStyle(style);
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

	private GridPane createBank(int bankGroupPos) {
		int columnSize = getBankColumnSize();
		int rowSize = getBankRowSize();
		int rowPos = 0;
		GridPane pane = new GridPane();
		createColumnsRow(pane,getBankColumnSize(), WIDTH_COL_BANK);
		
		createEmptyRowGroupBank(pane, rowPos, columnSize, UIColors.BACKGROUND_GREY_DARK);
		int totBank =0;
		for (int i = 0; i < rowSize; i++) {
			rowPos++;
			
			int colPos = 0;
			pane.add(createEmptyColumnBankGroup(UIColors.BACKGROUND_GREY_DARK), colPos, rowPos);
			for(int j=0;j<columnSize;j++) {
				
				if(getTotalBank()<=totBank) {
					colPos++;
					pane.add(createEmptyColumnBankGroup(UIColors.BACKGROUND_GREY_DARK), colPos, rowPos);
					colPos++;
					pane.add(createEmptyColumnBankGroup(UIColors.BACKGROUND_GREY_DARK), colPos, rowPos);
					continue;
				}
				colPos++;
				pane.add(createBankItem(totBank, module, rank, chipPos, bankGroupPos), colPos, rowPos);
				colPos++;
				pane.add(createEmptyColumnBankGroup(UIColors.BACKGROUND_GREY_DARK), colPos, rowPos);
				totBank++;
			}
			RowConstraints r = new RowConstraints();
			r.setMinHeight(HEIGHT_ROW_BANK);
			r.setFillHeight(true);
			r.setVgrow(Priority.ALWAYS);
			pane.getRowConstraints().add(r);
			rowPos++;
			createEmptyRowGroupBank(pane, rowPos, columnSize, UIColors.BACKGROUND_GREY_DARK);
		}
		
		
		return pane;
	}

	private static Pane createBankItem(int bankPos,int module,int  rank,int  chipPos, int bankGroupPos) {
		Pane p = new Pane();
		
		Label l = new Label();
		l.setText("Bank " + bankPos);
		l.setStyle(UIColors.FONT_COLOR_BLACK);
		l.setMinHeight(20);
		l.setMaxHeight(20);
		l.setAlignment(Pos.TOP_LEFT);

		VBox vbox = new VBox();
		vbox.setFillWidth(true);
		boolean contain = SimulatorManager.getSim().getMemoryController().getMemoryStatus().containErrorInsideBank(module, rank, chipPos, bankGroupPos, bankPos);
		String style = contain ? UIColors.BACKGROUND_RED: UIColors.BACKGROUND_GREY_LIGHT;
		vbox.setStyle(style);
		vbox.getChildren().add(l);
		vbox.getChildren().add(p);
		
		vbox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				UIUtil.openMemoryViewCell(module, rank, chipPos, bankGroupPos, bankPos);
				event.consume();
			}

		});
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
//		this.stage = stage;
		this.module = module;
		this.rank = rank;
		this.chipPos = chipPos;
		stage.setTitle("MV by Bank Group/Bank - Module: " + module +", Rank: " +rank+ ", Chip: " + chipPos);
		stage.addEventHandler(AbsimthEvent.ABSIMTH_UPDATE_EVENT, event -> onAbsimthUpdateEvent());
		onAbsimthUpdateEvent();
	}

	private void onAbsimthUpdateEvent() {
		UIUtil.erasePanel(gridPaneModule);
		createBankGroup(gridPaneModule, getBankGroupRowSize(), getBankGroupColumnSize());
	}
}
