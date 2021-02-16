package absimth.sim.gui;

import java.net.URL;
import java.util.ResourceBundle;

import absimth.sim.SimulatorManager;
import absimth.sim.gui.helper.JavaFxTimeline;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CpuTimelineController implements Initializable {
	public VBox vBox;
	public HBox hBox;
	public ScrollBar vScroll;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
//		TimelineDataSet dt =	TimelineDataSet.builder()
//				.start(0)
//				.end(109)
//				.entries( Map.ofEntries(
//						 entry(0, "[03] sum"),
//				            entry(5, "[04] readEccInformation"),
//				            entry(10, "[03] sum"),
//				            entry(15, "[04] readEccInformation"),
//				            entry(20, "[04] readEccInformation"),
//				            entry(25, "[04] readEccInformation")))
//				.build();
		
		JavaFxTimeline time = new JavaFxTimeline();
		time.setPrefWidth(800);
		time.setPrefHeight(300);
		time.draw(800, 300, 5, SimulatorManager.getSim().getReport().getCpu().getTimeline(0), "CPU 0");
		
		JavaFxTimeline time2 = new JavaFxTimeline();
		time2.setPrefWidth(800);
		time2.setPrefHeight(300);
		time2.draw(800, 300, 5, SimulatorManager.getSim().getReport().getCpu().getTimeline(1), "CPU 1");
		
		
		vBox.getChildren().add(time);
		vBox.getChildren().add(time2);
		
	}

	
	
}
