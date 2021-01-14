package absimth.sim.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import absimth.sim.SimulatorManager;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
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
			loadFile(file.getPath(), file.getName());
		} else {
			textFieldConsole.setText("No file chosen.");
			// Disable all buttons
			buttonNext.setDisable(true);
			buttonRun.setDisable(true);
			buttonReset.setDisable(true);
		}
	}

	private void loadFile(String path, String name) throws Exception {
		consolePrint("loading " + path  + name+ "\r\n");
		
		SimulatorManager.getSim().load(path, name);
		consolePrint(SimulatorManager.getSim().getAbsimthConfiguration().toString());
		
		// Default button states
		buttonNext.setDisable(false);
		buttonRun.setDisable(false);
		buttonReset.setDisable(false);

	}

	// Exits application when Ctrl+Q is asserted or Exit button is pressed.
	public void closeProgram() {
		System.exit(0);
	}

	private void consolePrint(String outPrint) {
		textFieldConsole.setText(textFieldConsole.getText() + outPrint);
	}

	/**
	 * @param event not used
	 */
	public void onShowing(WindowEvent event) {
		textFieldConsole.setText("");
		primaryStage.setTitle("Absimth - (just) Another ABstract SIMulator To Hardware");
		mydebug();
	}

	// on future REMOVE it
	private void mydebug() {
		try {
			loadFile("/home/marco/puc/absimth/example/", "example.json");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void openCpuWindow(int cpu) {
		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getClassLoader().getResource("gui/cpu.fxml"));
			Parent root = loader.load();
			CPUController controller = loader.getController();
			Stage stage = new Stage();
			controller.setStage(stage, cpu);
			stage.setScene(new Scene(root, 800, 800));
			stage.show();
			// Hide this current window (if this is what you want)
//			((Node) (event.getSource())).getScene().getWindow().hide();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Used to pass stage from main
	public void setStage(Stage stage) {
		this.primaryStage = stage;
		stage.setOnShowing(this::onShowing);
	}

	public void executeNextInstruction() {
	}

	public void executeRestOfProgram() {
		openCpuWindow(0);
		openCpuWindow(1);
	}

	public void resetProgram() {
	}
}
