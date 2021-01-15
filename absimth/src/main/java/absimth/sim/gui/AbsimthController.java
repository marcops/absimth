package absimth.sim.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.LogModel;
import absimth.sim.utils.AbsimLog;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class AbsimthController implements Initializable {
	private Stage primaryStage;
	// UI elements
	public VBox absimthVBox;
	public MenuItem menuItemOpen;
	public MenuItem menuItemExit;
	// Input
	public Button buttonNext;
	public Button buttonRun;
	public Button buttonReset;
	
	public Button buttonViewCpu;
	public Button buttonViewMemory;

	// Config block
	public TitledPane titledPanelLog;
	public CheckBox checkboxLogCpu;
	public CheckBox checkboxLogMemory;
	public CheckBox checkboxLogInstruction;
	// end config block
	// Output
	public TextArea textFieldConsole;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/**
	 * Displays file chooser when Ctrl+O is asserted or when Open... button is
	 * pressed. If file is not picked, buttons are disabled.
	 * 
	 * @throws Exception
	 */
	public void chooseFile() throws Exception {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Absimth configuration file");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		File file = fileChooser.showOpenDialog(primaryStage);
		if (file != null) {
			loadFile(file.getAbsoluteFile().getParent() + "/", file.getName());
		} else {
			disableView();
		}
	}

	private void loadFile(String path, String name) throws Exception {
		AbsimLog.log("loading " + path + name + "\r\n");

		SimulatorManager.getSim().load(path, name);
		AbsimLog.log(SimulatorManager.getSim().getAbsimthConfiguration().toString());
		enableView();
	}

	// Exits application when Ctrl+Q is asserted or Exit button is pressed.
	public void closeProgram() {
		System.exit(0);
	}

	/**
	 * @param event not used
	 */
	public void onShowing(WindowEvent event) {
		textFieldConsole.setText("");
		primaryStage.setTitle("ABSIMTH - (just) Another ABstract SIMulator To Hardware");
		primaryStage.setOnCloseRequest(e->closeProgram());
		
		SimulatorManager.getSim().setTextAreaToLog(textFieldConsole);
		
		mydebug();
	}

	private void enableView() {
		// Default button states
		titledPanelLog.setDisable(false);
		//buttonNext.setDisable(false);
		//buttonRun.setDisable(false);
		//buttonReset.setDisable(false);
		buttonViewCpu.setDisable(false);
		buttonViewMemory.setDisable(false);

		LogModel log = SimulatorManager.getSim().getAbsimthConfiguration().getLog();
		checkboxLogCpu.setSelected(log.isCpu());
		checkboxLogMemory.setSelected(log.isMemory());
		checkboxLogInstruction.setSelected(log.isCpuInstruction());
	}

	private void disableView() {
		titledPanelLog.setDisable(true);
		buttonNext.setDisable(true);
		buttonRun.setDisable(true);
		buttonReset.setDisable(true);
		buttonViewCpu.setDisable(true);
		buttonViewMemory.setDisable(true);
	}

	// on future REMOVE it
	private void mydebug() {
		try {
			loadFile("/home/marco/puc/absimth/example/", "example.json");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Used to pass stage from main
	public void setStage(Stage stage) {
		this.primaryStage = stage;
		stage.setOnShowing(this::onShowing);
	}

	/// EVENTS
	public void viewMemoryOnAction() {}
	public void viewCpuOnAction() {
		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getClassLoader().getResource("gui/cpu.fxml"));
			Parent root = loader.load();
			CPUController controller = loader.getController();
			Stage stage = new Stage();
			controller.setStage(stage);
			stage.setScene(new Scene(root, 800, 800));
			stage.show();
			// Hide this current window (if this is what you want)
//			((Node) (event.getSource())).getScene().getWindow().hide();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void logCpuOnAction() {
		LogModel log = SimulatorManager.getSim().getAbsimthConfiguration().getLog();
		log.setCpu(!log.isCpu());
		checkboxLogCpu.setSelected(log.isCpu());
	}

	public void logMemoryOnAction() {
		LogModel log = SimulatorManager.getSim().getAbsimthConfiguration().getLog();
		log.setMemory(!log.isMemory());
		checkboxLogMemory.setSelected(log.isMemory());
	}

	public void logInstructionOnAction() {
		LogModel log = SimulatorManager.getSim().getAbsimthConfiguration().getLog();
		log.setCpuInstruction(!log.isCpuInstruction());
		checkboxLogInstruction.setSelected(log.isCpuInstruction());
	}

	public void executeNextInstruction() {
	}

	public void executeRestOfProgram() {
		
	}

	public void resetProgram() {
	}
}
