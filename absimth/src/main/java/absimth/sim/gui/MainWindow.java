package absimth.sim.gui;

import java.io.IOException;

import absimth.sim.SimulationRunnerWithoutView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow extends Application {
	private final int HEIGHT = 640;
	private final int WIDTH = 1000;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parameters params = this.getParameters();
		if(params.getNamed().isEmpty())
			openWindow(primaryStage);
		else
			processWithoutWindow(params);
	}

	private void processWithoutWindow(Parameters params) throws Exception {
		SimulationRunnerWithoutView sim = new SimulationRunnerWithoutView();
		sim.init(params.getNamed());
		sim.run();
		System.exit(0);
	}

	private void openWindow(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(this.getClass().getClassLoader().getResource("gui/absimth.fxml"));
		Parent root = loader.load();
		AbsimthController controller = loader.getController();
		controller.setStage(primaryStage);
		primaryStage.setScene(new Scene(root, WIDTH, HEIGHT));
		primaryStage.show();
	}
}
