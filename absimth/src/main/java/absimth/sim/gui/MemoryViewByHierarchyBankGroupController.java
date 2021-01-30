
package absimth.sim.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.hardware.memory.BankConfModel;
import absimth.sim.configuration.model.hardware.memory.BankGroupConfModel;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
	public static final Integer WIDTH_COL_BANK = 50;
	public static final Integer HEIGHT_ROW_BANK = 30;
	
	private static final String BORDER_GREEN =  "-fx-border-color: #32CD32;";
	private static final String BACKGROUND_BLACK= "-fx-background-color: black; ";
	private static final String FONT_COLOR_WHITE =  "-fx-text-fill: white; ";
	private static final String FONT_COLOR_BLACK =  "-fx-text-fill: black; ";
//	private static final String BACKGROUND_DEFAULT = "-fx-background-color: #FFFFFF; ";
//	private static final String BACKGROUND_MODULE = "-fx-background-color: #32CD32; ";
	private static final String BACKGROUND_BANKGROUP = "-fx-background-color: #808080; ";
	private static final String BACKGROUND_BANK = "-fx-background-color: #E8E8E8; ";
	private static final int LIMIT_COLUMN = 4; 
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
		int columnSize = bank.getAmount();
		if(columnSize>LIMIT_COLUMN) return LIMIT_COLUMN;
		return columnSize;
	}
	
	public static int getBankGroupRowSize() {
		BankGroupConfModel bank = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getModule().getRank().getChip().getBankGroup();
		int qtd = bank.getAmount()-getBankGroupColumnSize();
		if(qtd<1) return 1;
		int rowSize = qtd/LIMIT_COLUMN;
		rowSize+=2;
		return rowSize;
	}
	
	private static int getBankColumnSize() {
		BankConfModel bank = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory()
				.getModule().getRank().getChip().getBankGroup().getBank();
		int columnSize = bank.getAmount();
		if(columnSize>LIMIT_COLUMN) return LIMIT_COLUMN;
		return columnSize;
	}
	
	private static int getBankRowSize() {
		BankConfModel bank = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory()
				.getModule().getRank().getChip().getBankGroup().getBank();
		int qtd = bank.getAmount()-getBankColumnSize();
		if(qtd<1) return 1;
		int rowSize = qtd/LIMIT_COLUMN;
		rowSize+=2;
		return rowSize;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	private void createBankGroup(GridPane p, int rowSize, int columnSize) {
		p.setStyle(BORDER_GREEN + " -fx-border-width: 5;");
		int minWidth = (WIDTH_COL_BANK+WIDTH_ROW_EMPTY)*getBankColumnSize()+WIDTH_ROW_EMPTY ;
		createColumnsRow(p,getBankGroupColumnSize(), minWidth);
		int rowPos = 0;
		createEmptyRowGroupBank(p, rowPos, columnSize, BACKGROUND_BLACK);
		int totBank = 0;
		for (int i = 0; i < rowSize; i++) {
			rowPos++;
			
			int colPos = 0;
			p.add(createEmptyColumnBankGroup(BACKGROUND_BLACK), colPos, rowPos);
			for(int j=0;j<columnSize;j++) {
				
				if(getTotalBankGroup()<=totBank) {
					colPos++;
					p.add(createEmptyColumnBankGroup(BACKGROUND_BLACK), colPos, rowPos);
					colPos++;
					p.add(createEmptyColumnBankGroup(BACKGROUND_BLACK), colPos, rowPos);
					continue;
				}
				colPos++;
				p.add(createBankGroupItem(totBank), colPos, rowPos);
				colPos++;
				
				p.add(createEmptyColumnBankGroup(BACKGROUND_BLACK), colPos, rowPos);
				totBank++;
			}
			RowConstraints r = new RowConstraints();
			r.setMinHeight((HEIGHT_ROW_BANK+30)*getBankRowSize());
			r.setFillHeight(true);
			r.setVgrow(Priority.ALWAYS);
			p.getRowConstraints().add(r);
			rowPos++;
			createEmptyRowGroupBank(p, rowPos, columnSize, BACKGROUND_BLACK);
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
		l.setStyle(FONT_COLOR_WHITE);
		l.setMinHeight(20);
		l.setMaxHeight(20);
		l.setAlignment(Pos.TOP_LEFT);
		
		GridPane grid = createBank(bankGroupPos);
		
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

	private GridPane createBank(int bankGroupPos) {
		int columnSize = getBankColumnSize();
		int rowSize = getBankRowSize();
		int rowPos = 0;
		GridPane pane = new GridPane();
		createColumnsRow(pane,getBankColumnSize(), WIDTH_COL_BANK);
		
		createEmptyRowGroupBank(pane, rowPos, columnSize, BACKGROUND_BANKGROUP);
		int totBank =0;
		for (int i = 0; i < rowSize; i++) {
			rowPos++;
			
			int colPos = 0;
			pane.add(createEmptyColumnBankGroup(BACKGROUND_BANKGROUP), colPos, rowPos);
			for(int j=0;j<columnSize;j++) {
				
				if(getTotalBank()<=totBank) {
					colPos++;
					pane.add(createEmptyColumnBankGroup(BACKGROUND_BANKGROUP), colPos, rowPos);
					colPos++;
					pane.add(createEmptyColumnBankGroup(BACKGROUND_BANKGROUP), colPos, rowPos);
					continue;
				}
				colPos++;
				pane.add(createBankItem(totBank, module, rank, chipPos, bankGroupPos), colPos, rowPos);
				colPos++;
				pane.add(createEmptyColumnBankGroup(BACKGROUND_BANKGROUP), colPos, rowPos);
				totBank++;
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

	private static Pane createBankItem(int bankPos,int module,int  rank,int  chipPos, int bankGroupPos) {
		Pane p = new Pane();
		
		Label l = new Label();
		l.setText("Bank " + bankPos);
		l.setStyle(FONT_COLOR_BLACK);
		l.setMinHeight(20);
		l.setMaxHeight(20);
		l.setAlignment(Pos.TOP_LEFT);

		VBox vbox = new VBox();
		vbox.setFillWidth(true);		
		vbox.setStyle(BACKGROUND_BANK);
		vbox.getChildren().add(l);
		vbox.getChildren().add(p);
		
		vbox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
		     @Override
		     public void handle(MouseEvent event) {
		         openMemoryViewByBank(module, rank, chipPos, bankGroupPos, bankPos);
		         event.consume();
		     }
		     
		     private void openMemoryViewByBank(int module, int rank, int chipPos, int bankGroup, int bank) {
		 		try {
		 			System.out.println("m="+module+", r="+rank+",c="+ chipPos+",gp"+ bankGroup+",b="+bank);
		 			FXMLLoader loader = new FXMLLoader(this.getClass().getClassLoader().getResource("gui/memoryHierarchyCell.fxml"));
		 			Parent root = loader.load();
		 			MemoryViewByHierarchyCellController controller = loader.getController();
		 			Stage istage = new Stage();
		 			controller.setStage(istage, module, rank , chipPos, bankGroup, bank);
		 			int width = 800;
		 			int heigth = 460;
		 			istage.setResizable(false);
		 			istage.setScene(new Scene(root, width, heigth));
		 			istage.show();
		 			// Hide this current window (if this is what you want)
//		 			((Node) (event.getSource())).getScene().getWindow().hide();
		 		} catch (IOException e) {
		 			e.printStackTrace();
		 		}
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
		createBankGroup(gridPaneModule, getBankGroupRowSize(), getBankGroupColumnSize());
	}
}
