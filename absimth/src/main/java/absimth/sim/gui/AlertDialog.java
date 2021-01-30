package absimth.sim.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AlertDialog {

	public static void about() {
		Alert alert = new Alert(AlertType.INFORMATION);
		Image image = new Image(AlertDialog.class.getClassLoader().getResource("img/pucrs.png").toExternalForm());
	    ImageView imageView = new ImageView(image);
	    imageView.setFitWidth(64);
	    imageView.setFitHeight(112);
		alert.setGraphic(imageView);
		alert.setTitle("About Absimth");
		alert.setHeaderText("Just Another Abstract Simulator to Hardware\r\n\r\n\r\n" +
				"This simulation has as main objective \r\nlook deeply into the memory\r\n\r\n"+
				"Fell free to send pull request\r\nor use as you wish");
		alert.setContentText("Email: marco.stefani@edu.pucrs.br\r\n" + 
				"Source: https://github.com/marcops/absimth");
		alert.showAndWait();
	}

	public static void error(String msg) {
		Alert alert = new Alert(AlertType.ERROR, msg);
		alert.showAndWait();
	}
}
