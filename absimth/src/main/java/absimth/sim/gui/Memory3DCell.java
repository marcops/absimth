package absimth.sim.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import absimth.sim.gui.helper.UIColors;
import absimth.sim.gui.model.Cell3DInfoModel;
import absimth.sim.memoryController.model.ECCMemoryFaultType;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class Memory3DCell  {

	private PerspectiveCamera camera;
	private Rotate cameraRotateX, cameraRotateY, cameraRotateZ;
	private Translate cameraTranslate;

	private double mouseOldX, mouseNewX;
	private double mouseOldY, mouseNewY;

	private List<Map<ECCMemoryFaultType, Image>> lstOfColors = new ArrayList<>();
	
	private void createListOfColors() {
		
//		lstOfColors.
		Map<ECCMemoryFaultType, Image> hashZero = new HashMap<>();
		hashZero.put(ECCMemoryFaultType.NONE, generateBit("0", UIColors.BACKGROUND_GREY_VERYLIGHT, UIColors.FONT_COLOR_BLACK));
		hashZero.put(ECCMemoryFaultType.INVERTED, generateBit("0", UIColors.BACKGROUND_GREY_VERYLIGHT, UIColors.FONT_COLOR_RED));
		hashZero.put(ECCMemoryFaultType.FIXABLE_ERROR, generateBit("0", UIColors.BACKGROUND_LIGHTYELLOW, UIColors.FONT_COLOR_BLACK));
		hashZero.put(ECCMemoryFaultType.UNFIXABLE_ERROR, generateBit("0", UIColors.BACKGROUND_LIGHTRED, UIColors.FONT_COLOR_BLACK));
//		
		Map<ECCMemoryFaultType, Image> hashOne = new HashMap<>();
		hashOne.put(ECCMemoryFaultType.NONE, generateBit("1", UIColors.BACKGROUND_GREY_VERYLIGHT, UIColors.FONT_COLOR_BLACK));
		hashOne.put(ECCMemoryFaultType.INVERTED, generateBit("1", UIColors.BACKGROUND_GREY_VERYLIGHT, UIColors.FONT_COLOR_RED));
		hashOne.put(ECCMemoryFaultType.FIXABLE_ERROR, generateBit("1", UIColors.BACKGROUND_LIGHTYELLOW, UIColors.FONT_COLOR_BLACK));
		hashOne.put(ECCMemoryFaultType.UNFIXABLE_ERROR, generateBit("1", UIColors.BACKGROUND_LIGHTRED, UIColors.FONT_COLOR_BLACK));
		
		lstOfColors.add(hashZero);
		lstOfColors.add(hashOne);
	}
	public void start(List<List<List<Cell3DInfoModel>>> dData) {
		createListOfColors();
		Stage primaryStage = new Stage();
		int width = 850;
		int height = 650;
		List<Box> lst = createList(width, height, 16, 16, 8, dData);
		Group root = new Group(); // layout
		root.getChildren().addAll(lst);

		camera = new PerspectiveCamera();

		cameraRotateX = new Rotate(0, Rotate.X_AXIS);
		cameraRotateY = new Rotate(0, Rotate.Y_AXIS);
		cameraRotateZ = new Rotate(0, Rotate.Z_AXIS);
		cameraTranslate = new Translate(0, 0, -height);
		
		cameraRotateX.setPivotX(width/2);
		cameraRotateY.setPivotX(height/2);
		cameraRotateZ.setPivotX(-height/2);
		
		camera.getTransforms().addAll(cameraRotateX, cameraRotateY, cameraRotateZ, cameraTranslate);
		root.getChildren().add(camera);

		Scene scene = new Scene(root, width, height); // show scene

		scene.setOnMousePressed(new EventHandler<>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				onMousePressed(mouseEvent);
			}
		});
		scene.setOnMouseDragged(new EventHandler<>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				onMouseDragged(mouseEvent);
			}
		});

		scene.setCamera(camera);
		primaryStage.setTitle("Cell 3D View");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	List<Box> createList(int width, int heigth, int xLength, int yLength, int zLength, List<List<List<Cell3DInfoModel>>> dData) {
		int boxWidth = width/(xLength*2);
		int boxHeigth = width/(yLength*2);
		int boxDepth = heigth/(zLength*2);
		
		List<Box> lst = new ArrayList<>();
		for (int k = zLength-1; k >=0 ; k--) {
			List<List<Cell3DInfoModel>> lstxy = dData.get(k);
			for (int i = 0; i < xLength; i++) {
				List<Cell3DInfoModel> lsty = lstxy.get(i);
				for (int j = 0; j < yLength; j++) {
					Cell3DInfoModel celInfo = lsty.get(j);
					lst.add(createCube(i, j, k, boxWidth ,boxHeigth , boxDepth, celInfo));
				}
			}
		}
		return lst;
	}

	Box createCube(int x, int y, int z,  int boxWidth, int boxHeigth, int boxDepth, Cell3DInfoModel cellInfo) {
		Box box = new Box();
		box.setWidth(boxWidth); // x size
		box.setHeight(boxHeigth); // y size
		box.setDepth(boxDepth);// z size
		// tirar da direit
		box.setTranslateY((boxWidth * x) +(boxWidth/2*x));
		box.setTranslateX((boxHeigth * y) +(boxHeigth/2*y));
		box.setTranslateZ((boxDepth * z ) +(boxDepth/2*z));
		
		Tooltip tooltip = new Tooltip("x="+x+ ",y="+y+",z="+z + "\r\n" + cellInfo.getPhysicalAddress().toString());
		tooltip.setPrefWidth(200);
		tooltip.setWrapText(true);
		Tooltip.install(box, tooltip);
		
		PhongMaterial mat = new PhongMaterial();
		mat.setSpecularColor(Color.BLACK);

		Map<ECCMemoryFaultType, Image> map = lstOfColors.get(Integer.valueOf(cellInfo.getText()));
		mat.setDiffuseMap(map.get(cellInfo.getStatus()));
		box.setMaterial(mat);
		return box;
	}

	private static Image generateBit(String msg, String styleGrid, String styleFont) {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setStyle(styleGrid);
		
		Label label1 = new Label(msg);
		label1.setStyle(styleFont + " -fx-font-size: 56em;");
		GridPane.setHalignment(label1, HPos.CENTER);

		grid.add(label1, 0, 0);
		grid.setGridLinesVisible(true);

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(100);
		grid.getColumnConstraints().addAll(col1);

		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(100);
		
		grid.getRowConstraints().addAll(row1);
		grid.setPrefSize(800, 650);

		return grid.snapshot(createParans(grid), null);
	}
	static SnapshotParameters createParans(GridPane grid){
		SnapshotParameters params = new SnapshotParameters();
	    Scene s = new Scene(grid);
        params.setCamera(s.getCamera());
        params.setDepthBuffer(s.isDepthBuffer());
        params.setFill(s.getFill());
		return params;
	}
	public void onMousePressed(MouseEvent mouseEvent) {
		mouseOldX = mouseNewX = mouseEvent.getSceneX();
		mouseOldY = mouseNewY = mouseEvent.getSceneY();
	}

	public void onMouseDragged(MouseEvent mouseEvent) {
		mouseOldX = mouseNewX;
		mouseOldY = mouseNewY;
		mouseNewX = mouseEvent.getSceneX();
		mouseNewY = mouseEvent.getSceneY();

		double mouseDeltaX = (mouseNewX - mouseOldX);
		double mouseDeltaY = (mouseNewY - mouseOldY);

		cameraRotateX.setAngle(cameraRotateX.getAngle() - mouseDeltaY);
		cameraRotateY.setAngle(cameraRotateY.getAngle() + mouseDeltaX);
	}

}
