package absimth.sim.gui;

import static java.util.Map.entry;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import absimth.sim.gui.helper.JavaFxTimeline;
import absimth.sim.gui.model.TimelineDataSet;
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
		
		TimelineDataSet dt =	TimelineDataSet.builder()
				.start(0)
				.end(109)
				.entries( Map.ofEntries(
						 entry(0, "[03] sum"),
				            entry(5, "[04] readEccInformation"),
				            entry(10, "[03] sum"),
				            entry(15, "[04] readEccInformation"),
				            entry(20, "[04] readEccInformation"),
				            entry(25, "[04] readEccInformation")))
				.build();
		
		JavaFxTimeline time = new JavaFxTimeline();
		time.setPrefWidth(800);
		time.setPrefHeight(300);
		time.draw(800, 300, 5, dt, "CPU 0");
		
		TimelineDataSet dt2 =	TimelineDataSet.builder()
				.start(0)
				.end(30)
				.entries( Map.ofEntries(
		            entry(0, "taskId 0"),
		            entry(5, "taskId 1"),
		            entry(10, "taskId 2"),
		            entry(15, "taskId 0"),
		            entry(20, "taskId 1"),
		            entry(25, "taskId 2")))
				.build();
		
		JavaFxTimeline time2 = new JavaFxTimeline();
		time2.setPrefWidth(800);
		time2.setPrefHeight(300);
		time2.draw(800, 300, 5, dt2, "CPU 1");
		
		
		vBox.getChildren().add(time);
		vBox.getChildren().add(time2);
		
	}

	
	
}
