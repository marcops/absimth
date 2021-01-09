package memcode.sim.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(this.getClass().getClassLoader().getResource("Layout.fxml"));
		Parent root = loader.load();
		guiController controller = loader.getController();
		controller.setStage(primaryStage);
		primaryStage.setTitle("RV32I Simulator");
		primaryStage.setScene(new Scene(root, 800, 800));
		primaryStage.show();
	}
}
