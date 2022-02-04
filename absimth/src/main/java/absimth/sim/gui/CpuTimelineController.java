package absimth.sim.gui;

import java.net.URL;
import java.util.ResourceBundle;

import absimth.sim.SimulatorManager;
import absimth.sim.gui.helper.JavaFxTimeline;
import absimth.sim.gui.model.TimelineDataSet;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CpuTimelineController implements Initializable {
	public VBox vBox;
	public HBox hBox;
	public ScrollBar vScroll;
	public ScrollBar hScroll;
	private Stage stage;
	private int lastHeightPosition = 0;
	private int lastWidthPosition = 0;
	private final Integer MAX_HEIGHT = 250;
	private final Integer MAX_WIDTH = 100;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	private void draw() {
		vBox.getChildren().clear();
		int maxWidth = 0;
		int totalCpu = SimulatorManager.getSim().getLstCpu().size();
		for (int i = 0; (i + lastHeightPosition) < totalCpu && (i * MAX_HEIGHT) < stage.getScene().getHeight(); i++) {
			TimelineDataSet timeline = filterTimeline(i);
			addCpuTimeline(i, timeline);
			if (timeline.getMaxEntryPosition() > maxWidth) maxWidth = timeline.getMaxEntryPosition();
		}

		calculateHScroll(maxWidth);
		calculateVScroll(totalCpu);
	}

	private void calculateHScroll(int width) {
		double totalVisible = width / MAX_WIDTH;
		int total = (int) Math.ceil(totalVisible);
		hScroll.setMax(total);
		hScroll.setMin(0);
		hScroll.setDisable(total < 1);
		hScroll.setValue(lastWidthPosition);
		hScroll.setUnitIncrement(1);

	}


	private TimelineDataSet filterTimeline(int i) {
		int nextPos = MAX_WIDTH*lastWidthPosition;
		TimelineDataSet timeline = SimulatorManager.getSim().getReport().getCpu().getTimeline(i + lastHeightPosition);
		return TimelineDataSet
				.builder()
				.start(nextPos)
				.end(nextPos+MAX_WIDTH)
				.maxEntryPosition(timeline.getMaxEntryPosition())
				.entries(timeline.getEntriesFiltred(nextPos, nextPos+MAX_WIDTH) )
				.build();
	}


	private void addCpuTimeline(int i, TimelineDataSet timeline) {
		JavaFxTimeline time = new JavaFxTimeline();
		time.setPrefWidth(stage.getScene().getWidth());
		time.setPrefHeight(MAX_HEIGHT);
		time.setMinHeight(MAX_HEIGHT);
		time.setMaxHeight(MAX_HEIGHT);
		time.draw((int) stage.getScene().getWidth(), MAX_HEIGHT, 5, timeline, "CPU " + (i + lastHeightPosition));
		vBox.getChildren().add(time);
	}

	private void calculateVScroll(int totalCpu) {
		double totalVisible = stage.getScene().getHeight() / MAX_HEIGHT;
		double totalToShow = (totalCpu * MAX_HEIGHT) / stage.getScene().getHeight();
		int total = (int) Math.ceil(totalToShow - totalVisible);

		vScroll.setMax(Math.min(total, totalCpu - lastHeightPosition));
		vScroll.setMin(0);
		vScroll.setDisable(total < 1);
		vScroll.setValue(lastHeightPosition);
		vScroll.setUnitIncrement(1);
	}

	public void setStage(Stage stage) {
		this.stage = stage;
		stageSizeChageListener(stage);
		stage.setTitle("Cpu Timeline");
	}

	private void stageSizeChageListener(Stage stage) {
		stage.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				lastHeightPosition = 0;
				lastWidthPosition = 0;
				draw();
			}
		});

		stage.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				lastHeightPosition = 0;
				lastWidthPosition = 0;
				draw();
			}
		});

		vScroll.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				lastHeightPosition = new_val.intValue();
				draw();
			}
		});

		hScroll.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				lastWidthPosition = new_val.intValue();
				draw();
			}
		});

	}
}
