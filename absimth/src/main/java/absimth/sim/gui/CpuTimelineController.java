package absimth.sim.gui;

import java.net.URL;
import java.util.ResourceBundle;

import absimth.sim.SimulatorManager;
import absimth.sim.gui.helper.JavaFxTimeline;
import absimth.sim.gui.model.TimelineDataSet;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CpuTimelineController implements Initializable {
	public VBox vBox;
	public HBox hBox;
	public ScrollBar vScroll;
	private Stage stage;
	private int lastPosition = 0;
	private final Integer HEIGHT = 250;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}


	private void draw() {
		vBox.getChildren().clear();
		vBox.getChildren().add(createHorizontalScroll());
		int totalCpu = SimulatorManager.getSim().getLstCpu().size();
		for (int i = 0; (i+lastPosition) < totalCpu && (i*HEIGHT)<stage.getScene().getHeight(); i++) {
			TimelineDataSet timeline = SimulatorManager.getSim().getReport().getCpu().getTimeline(i+lastPosition);
			JavaFxTimeline time = new JavaFxTimeline();
			time.setPrefWidth(stage.getScene().getWidth());
			time.setPrefHeight(HEIGHT);
			time.setMinHeight(HEIGHT);
			time.setMaxHeight(HEIGHT);
			time.draw((int)stage.getScene().getWidth(), HEIGHT, 5, timeline, "CPU "+(i+lastPosition));
			vBox.getChildren().add(time);
		}
		
		double totalVisible = stage.getScene().getHeight()/HEIGHT;
		double totalToShow = (totalCpu*HEIGHT)/stage.getScene().getHeight();
		int total = (int) Math.ceil(totalToShow- totalVisible);
		vScroll.setMax(total);
		vScroll.setMin(0);
		vScroll.setDisable(total<1);
		vScroll.setValue(lastPosition);
		vScroll.setUnitIncrement(1);
		
		
		
	}


	
	private static Node createHorizontalScroll() {
		ScrollBar scroll = new ScrollBar();
		scroll.setOrientation(Orientation.HORIZONTAL);
		return scroll;
	}


	public void setStage(Stage stage) {
		this.stage = stage;
		stageSizeChageListener(stage);
		stage.setTitle("Cpu Timeline");
	}
	
	private void stageSizeChageListener(Stage stage){
        stage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            	lastPosition=0;
            	draw();
            }
        });

        stage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            	lastPosition=0;
            	draw();
            }
        });

        vScroll.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
			public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
            	lastPosition = new_val.intValue();
            	draw();
            }
        });
        

    }
}
