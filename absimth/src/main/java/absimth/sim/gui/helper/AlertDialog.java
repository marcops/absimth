package absimth.sim.gui.helper;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertDialog {

	public static void info() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog Box");
		alert.setHeaderText("This is header section to write heading");
		alert.setContentText("This is body section to write some info!");
		alert.showAndWait();
	}

	public static void error(String msg) {
		Alert alert = new Alert(AlertType.ERROR, msg);
//		Alert alert = new Alert(AlertType.ERROR);
//		alert.setTitle("Error");
//		alert.setHeaderText("This is header section to write heading");
		//setContentText
//		alert.setContentText(msg);
		alert.showAndWait();
	}
}
