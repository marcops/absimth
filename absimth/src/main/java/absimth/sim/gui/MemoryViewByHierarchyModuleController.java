
package absimth.sim.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.MemoryConfModel;
import absimth.sim.configuration.model.hardware.memory.ChipConfModel;
import absimth.sim.configuration.model.hardware.memory.ModuleConfModel;
import absimth.sim.configuration.model.hardware.memory.PhysicalAddress;
import absimth.sim.configuration.model.hardware.memory.RankConfModel;
import absimth.sim.gui.helper.UIUtil;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MemoryViewByHierarchyModuleController implements Initializable {
	private Stage stage;
	public VBox mainVBox;
	public GridPane gridPaneModule;
	public TextField txtFieldAddress;
	public ComboBox<Integer> comboBoxChip;
	
	private static final String BORDER_BLACK = "-fx-border-color: black; ";
	private static final String FONT_12= "-fx-font-size: 12; ";
	private static final String FONT_LARGE = "-fx-font-size: 60; ";
	private static final String FONT_COLOR_WHITE =  "-fx-text-fill: white; ";
	private static final String BACKGROUND_DEFAULT = "-fx-background-color: #FFFFFF; ";
	private static final String BACKGROUND_MODULE = "-fx-background-color: #32CD32; ";
	private static final String BACKGROUND_BLACK= "-fx-background-color: black; ";
	private static final Integer HEIGHT_ROW_MODULE = 100;
	private static final Integer WIDTH_ROW_MODULE = 300;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		MemoryConfModel memory = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory();

		ColumnConstraints col0 = new ColumnConstraints();
		col0.setMinWidth(5);
		col0.setMaxWidth(5);
		
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setMinWidth(100);
		col1.setMaxWidth(100);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setFillWidth(true);
		col2.setMinWidth(WIDTH_ROW_MODULE);
		col2.setHgrow(Priority.ALWAYS);
		ColumnConstraints col3 = new ColumnConstraints();
		col3.setMinWidth(5);
		col3.setMaxWidth(5);
		gridPaneModule.getColumnConstraints().addAll(col0,col1,col2,col3);
		gridPaneModule.setAlignment(Pos.TOP_CENTER);
		createModule(memory.getModule());
		
		for (int i = 0; i < memory.getModule().getRank().getChip().getAmount(); i++)
			comboBoxChip.getItems().add(i);
		comboBoxChip.getSelectionModel().select(0);
	}

	public void btnOpenAddress() {
		int destAddr = UIUtil.getAddressFromField(txtFieldAddress.getText());
		if (destAddr < 0) return;
		PhysicalAddress pa = SimulatorManager.getSim().getPhysicalAddressService().getPhysicalAddress((long)destAddr);
		UIUtil.openMemoryViewBank((int)pa.getModule(), (int)pa.getRank(), comboBoxChip.getSelectionModel().getSelectedIndex());
		UIUtil.openMemoryViewCell((int)pa.getModule(), (int)pa.getRank(), comboBoxChip.getSelectionModel().getSelectedIndex(), (int)pa.getBankGroup(), (int)pa.getBank(), pa);
	}
	
	private static Pane createChipPanel(int chipPos, int module, int rank) {
		Pane backPanel = new Pane();
		backPanel.setStyle(BACKGROUND_BLACK + FONT_12);
		Label l = new Label();
		l.setText("CHIP " + chipPos);
		l.setStyle(FONT_COLOR_WHITE);
		l.setAlignment(Pos.CENTER);
		backPanel.getChildren().add(l);
		backPanel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
		     @Override
		     public void handle(MouseEvent event) {
		    	 UIUtil.openMemoryViewBank(module, rank, chipPos);
		         event.consume();
		     }
		});
		return backPanel;
	}
	

	private static Pane createBackgroundMemoryPanel() {
		Pane backPanel = new Pane();
		backPanel.setStyle(BACKGROUND_MODULE);
		return backPanel;
	}
	
	private static Pane createBackgroundPanel() {
		Pane backPanel = new Pane();
//		backPanel.setStyle(BACKGROUND_DEFAULT);
		return backPanel;
	}

	private void createModule(ModuleConfModel module) {
		createEmptyRow(0);
		for (int i = 0, rowPos =1; i < module.getAmount(); i++) {
			RankConfModel rankConfig = module.getRank();
			for(int j=0;j<rankConfig.getAmount();j++) {
				createModuleRow(rowPos++, i, j, module.getRank());
				createEmptyRow(rowPos++);
			}
		}
	}

	private static Pane createModuleDescriptionPanel(int module, int rank) {
		Pane backPanel = new Pane();
		Label l = new Label();
		l.setText("Module "+module+"\r\n" + "Rank "+rank+"\r\n\r\n" + "Address\r\n" + "0x00000000 to\r\n0xFFFFFFFF");
		l.setStyle(BORDER_BLACK + FONT_12 );
		l.setMinHeight(HEIGHT_ROW_MODULE);
		l.setMaxHeight(HEIGHT_ROW_MODULE);
		l.setAlignment(Pos.CENTER);
		backPanel.setStyle(BACKGROUND_DEFAULT);
		backPanel.getChildren().add(l);
		return backPanel;
	}
	private void createModuleRow(int row, int module, int rank, RankConfModel rankConfModel) {
		gridPaneModule.add(createBackgroundPanel(), 0, row);
		gridPaneModule.add(createModuleDescriptionPanel(module, rank), 1, row);
		gridPaneModule.add(createBackgroundPanel(), 3, row);
		
		
		GridPane c = createChips(rankConfModel.getChip(), module, rank);
		gridPaneModule.add(c, 2, row);

		RowConstraints r = new RowConstraints();
		r.setMinHeight(HEIGHT_ROW_MODULE);
		r.setMaxHeight(HEIGHT_ROW_MODULE);
		gridPaneModule.getRowConstraints().add(r);
	}

	private static GridPane createChips(ChipConfModel chipConf, int module, int rank) {
		int spaceWidth = 10;
		GridPane panel = new GridPane();
		panel.setStyle(BORDER_BLACK + FONT_LARGE + BACKGROUND_MODULE);
		panel.setAlignment(Pos.TOP_CENTER);
		
		//empty row
		//row
		List<ColumnConstraints> lstColumns = new ArrayList<>();
		ColumnConstraints ci = new ColumnConstraints();
		ci.setMinWidth(spaceWidth);
		ci.setMaxWidth(spaceWidth);
		lstColumns.add(ci);
		panel.add(createBackgroundMemoryPanel(),0,1);
		
		int chipWidth = (WIDTH_ROW_MODULE-(spaceWidth*2))/chipConf.getAmount();
		int chipPos = 1;
		for(int i=0; i< chipConf.getAmount();i++) {
			ColumnConstraints cc = new ColumnConstraints();
			cc.setFillWidth(true);
			cc.setMinWidth(chipWidth);
			cc.setHgrow(Priority.ALWAYS);
			lstColumns.add(cc);
			
			panel.add(createChipPanel(i, module, rank),chipPos++,1);
			
			ColumnConstraints ce = new ColumnConstraints();
			ce.setMinWidth(spaceWidth);
			ce.setMaxWidth(spaceWidth);
			lstColumns.add(ce);
			panel.add(createBackgroundMemoryPanel(),chipPos++,1);
		}
		panel.getColumnConstraints().addAll(lstColumns);
		
		RowConstraints ri = new RowConstraints();
		ri.setMinHeight(10);
		panel.getRowConstraints().add(ri);
		
		RowConstraints rc = new RowConstraints();
		rc.setPercentHeight(HEIGHT_ROW_MODULE-20);
		panel.getRowConstraints().add(rc);
		
		RowConstraints re = new RowConstraints();
		re.setMinHeight(10);
		panel.getRowConstraints().add(re);
		
		//empty row
		return panel;
		
	}

	private void createEmptyRow(int row) {
		gridPaneModule.add(createBackgroundPanel(), 0, row);
		gridPaneModule.add(createBackgroundPanel(), 1, row);
		gridPaneModule.add(createBackgroundPanel(), 2, row);
		gridPaneModule.add(createBackgroundPanel(), 3, row);
		
		RowConstraints r = new RowConstraints();
		r.setMinHeight(10);
		gridPaneModule.getRowConstraints().add(r);
	}

	public void closeProgram() {
		stage.close();
	}

	// Used to pass stage from main
	public void setStage(Stage stage) {
		this.stage = stage;
		stage.setTitle("Memory by Hierarchical View - Module/Rank");
	}
}
