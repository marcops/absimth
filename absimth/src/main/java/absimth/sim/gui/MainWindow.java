package absimth.sim.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow extends Application {
	private final int HEIGHT = 640;
	private final int WIDTH = 800;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(this.getClass().getClassLoader().getResource("gui/absimth.fxml"));
		Parent root = loader.load();
		AbsimthController controller = loader.getController();
		controller.setStage(primaryStage);
		primaryStage.setScene(new Scene(root, WIDTH, HEIGHT));
		primaryStage.show();
	}
}
