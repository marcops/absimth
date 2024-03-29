package absimth.sim.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.LogModel;
import absimth.sim.gui.helper.UIUtil;
import absimth.sim.utils.AbsimLog;
import absimth.sim.utils.FileLog;
import javafx.fxml.Initializable;
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
	public Button buttonViewMemoryView;
	public Button buttonViewReport;
	// Config block
	public TitledPane titledPanelLog;
	public CheckBox checkboxLogCpu;
	public CheckBox checkboxLogMemory;
	public CheckBox checkboxLogInstruction;
	public CheckBox checkboxLogOther;
	// end config block
	// Output
	public TextArea textFieldConsole;
	public TextArea textFieldConsoleRiscV;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/**
	 * Displays file chooser when Ctrl+O is asserted or when Open... button is
	 * pressed. If file is not picked, buttons are disabled.
	 * 
	 * @throws Exception
	 */
	public void chooseFile() {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Yaml files (*.yml)", "*.yml");

		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setTitle("Open Absimth configuration file");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		File file = fileChooser.showOpenDialog(primaryStage);
		if (file != null) {
			loadFile(file.getAbsoluteFile().getParent() + "/", file.getName());
		} else {
			if(SimulatorManager.getSim().getPathLoaded() == null || SimulatorManager.getSim().getNameLoaded() == null) 
				disableView();
		}
		
	}

	private void loadFile(String path, String name)  {
		try {
			SimulatorManager.getSim().load(path, name);
			enableView();
		} catch (Exception e) {
			AbsimLog.fatal(e.getMessage());
		}
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
		textFieldConsoleRiscV.setText("");
		primaryStage.setTitle("ABSIMTH - (just) Another ABstract SIMulator To Hardware");
		primaryStage.setOnCloseRequest(e->closeProgram());
		
		SimulatorManager.getSim().setTextAreaToLog(textFieldConsole);
		SimulatorManager.getSim().setTextAreaRiscV(textFieldConsoleRiscV);
		
		//mydebug();
	}

	private void enableView() {
		// Default button states
		titledPanelLog.setDisable(false);
		buttonNext.setDisable(false);
		buttonRun.setDisable(false);
		buttonReset.setDisable(false);
		buttonViewCpu.setDisable(false);
		buttonViewMemory.setDisable(false);
		buttonViewMemoryView.setDisable(false);
		buttonViewReport.setDisable(true);
		
		LogModel log = SimulatorManager.getSim().getAbsimthConfiguration().getLog();
		checkboxLogCpu.setSelected(log.isCpu());
		checkboxLogMemory.setSelected(log.isMemory());
		checkboxLogInstruction.setSelected(log.isCpuInstruction());
		checkboxLogOther.setSelected(log.isMemoryController());
	}

	private void disableView() {
		titledPanelLog.setDisable(true);
		buttonNext.setDisable(true);
		buttonRun.setDisable(true);
		buttonReset.setDisable(true);
		buttonViewCpu.setDisable(true);
		buttonViewMemory.setDisable(true);
		buttonViewMemoryView.setDisable(true);
		buttonViewReport.setDisable(true);
	}

	// on future REMOVE it
//	private void mydebug() {
//		try {
//			loadFile("/home/marco/puc/absimth/example/", "example.yml");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	// Used to pass stage from main
	public void setStage(Stage stage) {
		this.primaryStage = stage;
		stage.setOnShowing(this::onShowing);
	}

	/// EVENTS
	public void viewMemoryOnAction() {
		UIUtil.openMemoryViewAddress();
	}
	
	public void viewCpuOnAction() {
		UIUtil.openCpuInstructionView();	
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
	
	public void logOtherOnAction() {
		LogModel log = SimulatorManager.getSim().getAbsimthConfiguration().getLog();
		log.setMemoryController(!log.isMemoryController());
		checkboxLogOther.setSelected(log.isMemoryController());
	}

	public void executeNextInstruction() throws Exception {
		try {
			if(!SimulatorManager.getSim().getOs().executeNextInstruction()) {
				//finished
				buttonNext.setDisable(true);
				buttonRun.setDisable(true);
				viewReportOnAction();
			}
			UIUtil.updateAllWindows();
		buttonViewReport.setDisable(false);
		} catch (Exception e) {
			System.out.println(e);
			disableView();
		}
	}

	

	public void viewMemoryViewOnAction() {
		UIUtil.openMemoryViewModule();
	}

	public void executeRestOfProgram() {
		
		RunAllInstructionDialog.start(e->{
			try {
				if(!SimulatorManager.getSim().getOs().isRunning()) {
					buttonNext.setDisable(true);
					buttonRun.setDisable(true);
					buttonViewReport.setDisable(false);
					UIUtil.updateAllWindows();
					viewReportOnAction();
				}
			} catch (Exception ex) {
				System.out.println(ex);
				disableView();
			} 
		});
	}

	public void viewReportOnAction() {
		String msg = SimulatorManager.getSim().getAbsimthConfiguration().toString(); 
		msg += SimulatorManager.getSim().getReport().printReport();
		FileLog.report(msg, null);
//		FileLog.reportCSV(msg, null);
		SimulatorManager.getSim().getTextAreaToLog().clear();
		AbsimLog.logView(msg);
	}
	
	public void resetProgram() throws Exception {
		if(SimulatorManager.getSim().reload()) {
			enableView();
			UIUtil.updateAllWindows();
		}
		else disableView();
	}
	
	public void menuMemTable() {
		viewMemoryOnAction();		
	}
	
	public void menuMemHierarchy() {
		viewMemoryViewOnAction();
	}
	
	public void menuCpuExecution() {
		viewCpuOnAction();
	}
	
	public void menuAbout() {
		AlertDialog.about();
	}
	
	public void menuHelp()   {
		UIUtil.openHelp();
	}
	
	public void menuCpuTimeline() {
		UIUtil.openCpuTimeline();
	}
	
	
}
