package absimth.sim.gui.helper;

import absimth.sim.gui.model.TimelineDataSet;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;

public class JavaFxTimeline extends Pane {

    public void draw(int width, int height, int offset, TimelineDataSet dataSet, String title) {
    	this.getChildren().clear();
        // Set the background color
        this.setStyle("-fx-background-color: #ffffff");
        
        // Draw the main line from left to right
        int axisLength = width - (2 * offset);

        createHorizontalLine(offset, axisLength);

        // Draw the year lines
        int positionToDraw = dataSet.getEnd() - dataSet.getStart() + 1;
        double distanceBetweenYears = axisLength / positionToDraw;

        for (int i = 0; i < positionToDraw; i++) {
            int currentPosition = dataSet.getStart() + i;
            double yearLineX = (offset * 3) + (i * distanceBetweenYears);

            createEachVerticalLine(offset, currentPosition, yearLineX);
            // Add a special notation
            String notation = dataSet.get(currentPosition);
            if (notation != null && !notation.isEmpty()) {
                createLabelDataInformation(height, offset, yearLineX, notation.substring(0, Math.min(notation.length(), 24)));
            }
            
            // Add a label for every 10 year
            if (currentPosition % 10 == 0) {
                createPositionLabelEveryTen(offset, currentPosition, yearLineX);
            }

        }
        
        createTitle(title);
    }

	private void createHorizontalLine(int offset, int axisLength) {
		Line horizontalAxis = new Line(offset, offset * 3, axisLength, offset * 3);
        horizontalAxis.setStroke(Color.DARKGREY);
        horizontalAxis.setStrokeWidth(5);
        this.getChildren().add(horizontalAxis);
	}

	private void createEachVerticalLine(int offset, int currentPosition, double yearLineX) {
		Line yearLine = new Line(yearLineX, offset * 2, yearLineX, offset * 4);
		yearLine.setStroke(Color.DARKGREY);
		yearLine.setStrokeWidth(currentPosition % 10 == 0 ? 4 : 2);
		this.getChildren().add(yearLine);
	}

	private void createLabelDataInformation(int height, int offset, double yearLineX, String notation) {
		Line notationLine = new Line(yearLineX, offset * 4, yearLineX, offset * 8);
		notationLine.setStroke(Color.RED);
		notationLine.setStrokeWidth(3);
		this.getChildren().add(notationLine);

		int notationLabelWidth = height - (offset * 9);

		Label notationLabel = new Label(notation);
		notationLabel.setLayoutX(yearLineX);
		notationLabel.setLayoutY((offset * 9));
		notationLabel.setPrefWidth(notationLabelWidth);
		notationLabel.setPrefHeight(40);
		notationLabel.setStyle("-fx-font-size: 14px; -fx-text-alignment: left;");
		notationLabel.getTransforms().add(new Rotate(70, 15, 10, 0, Rotate.Z_AXIS));
		this.getChildren().add(notationLabel);
	}
	
	private void createTitle(String title) {
		Label yearLabel = new Label(title);
		yearLabel.setLayoutX(0);
		yearLabel.setLayoutY(0);
		yearLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
		this.getChildren().add(yearLabel);
	}

	private void createPositionLabelEveryTen(int offset, final int currentPosition, double yearLineX) {
		Label yearLabel = new Label(String.format("% 3d",currentPosition));
		yearLabel.setLayoutX(yearLineX - 20);
		yearLabel.setLayoutY(offset * 5);
		yearLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
		this.getChildren().add(yearLabel);
	}
}