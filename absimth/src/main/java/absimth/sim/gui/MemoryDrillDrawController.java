
package absimth.sim.gui;

import java.net.URL;
import java.util.ResourceBundle;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.MemoryConfModel;
import absimth.sim.configuration.model.hardware.memory.ModuleConfModel;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MemoryDrillDrawController implements Initializable {
	private Stage primaryStage;
	public VBox mainVBox;
	public GridPane gridPaneModule;

	private Pane backgroundPanel;
	private static final String BORDER_BLACK = "-fx-border-color: black; ";
	private static final String FONT_LARGE = "-fx-font-size: 60; ";

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		MemoryConfModel memory = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory();
		
		createModule(memory.getModule());
		
		gridPaneModule.setAlignment(Pos.CENTER);
	}

	private void createModule(ModuleConfModel module) {
		gridPaneModule.add(l, 0, 0);
		for(int i=0;i<module.getAmount();i++) {
			//TOP
			Label l = new Label("AAA");
			l.setStyle(BORDER_BLACK + FONT_LARGE);
			gridPaneModule.add(l, 0, 0);
			
			//BUTTOM
			gridPaneModule.add(l, 0, 0);
		}
	}

	public void closeProgram() {
		primaryStage.close();
	}

	// Used to pass stage from main
	public void setStage(Stage stage) {
		this.primaryStage = stage;
	}
}
