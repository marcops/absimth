
package absimth.sim.gui;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import absimth.sim.gui.helper.UIColors;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

public class HelpController implements Initializable {
	public TableView<String> exampleTable;
	public TableColumn<String, String> exampleColumn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		exampleColumn.setCellValueFactory(x->new SimpleStringProperty(x.getValue()));
		exampleColumn.setCellFactory(tc -> createColorFormat());
	}

	private static TextFieldTableCell<String, String> createColorFormat() {
		return new TextFieldTableCell<>(TextFormatter.IDENTITY_STRING_CONVERTER) {
			@Override
			public void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (getIndex() == 1) setStyle(UIColors.columnDataFailRedReadAndFixed);
				if (getIndex() == 2) setStyle(UIColors.columnDataFailNotReadAndFixable); 
				if (getIndex() == 3) setStyle(UIColors.columnDataFailReadAndNotFixable); 
			}
		};
	}
	

	public void setStage(Stage stage) {
		stage.setTitle("Help");
		exampleTable.getItems().addAll(Arrays.asList(
				"Example with Data OK",
				"Example with Correctable Error (CE) - Data was read and fixed",
				"Example with Correctable Error (CE) - Data wasn't read",
				"Example with Uncorrected Error (UE) - Data wasn't read"
				));
	}
}
