package absimth.sim.gui;

import absimth.sim.SimulatorManager;
import absimth.sim.os.OSCpuExecutor;
import absimth.sim.utils.AbsimLog;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class RunAllInstructionDialog {

	public static Task<Void> start( EventHandler<WindowEvent> onSucceeded) {
		return start(null, onSucceeded);
	}
	
	public static Task<Void> start(OSCpuExecutor cpuExecutor, EventHandler<WindowEvent> onSucceeded) {
		ProgressForm pForm = new ProgressForm();

		Task<Void> task = new Task<>() {
			@Override
			public Void call() {
				try {
					if (cpuExecutor != null) {
						do {
							cpuExecutor.executeNextInstruction();
							if(pForm.stop) return null;
						} while(cpuExecutor.isRunningApp());
					} else {
						while(SimulatorManager.getSim().getOs().executeNextInstruction()) {
							if(pForm.stop) return null;
						}
					}
					return null;
				} catch (Exception e) {
					e.printStackTrace();
					AbsimLog.fatal(e.getMessage());
					System.err.println(e);
					return null;
				}
			}
		};

		pForm.activateProgressBar(task);

		task.setOnSucceeded(event -> {
			pForm.getDialogStage().close();
		});
		pForm.getDialogStage().setOnHiding(onSucceeded);
		pForm.getDialogStage().show();

		Thread thread = new Thread(task);
		thread.start();
		return task;
	}

	public static class ProgressForm {
		private final Stage dialogStage;
		private final ProgressBar pb = new ProgressBar();
		private boolean stop = false;
		
		public ProgressForm() {
			dialogStage = new Stage();
			dialogStage.initStyle(StageStyle.UTILITY);
			dialogStage.setResizable(false);
			dialogStage.initModality(Modality.APPLICATION_MODAL);

			// PROGRESS BAR
			final Label label = new Label("Please Wait...");
			final Button btn = new Button("Stop");
			btn.setOnAction(e -> {
				this.stop = true;
			});
			pb.setProgress(-1F);
			pb.setPrefWidth(180D);
					
			final VBox vb = new VBox();
			vb.setSpacing(10);
			vb.setAlignment(Pos.CENTER);
			vb.getChildren().addAll(label, pb, btn);
			vb.setPrefSize(200D, 90D);
			Scene scene = new Scene(vb);
			dialogStage.setScene(scene);
		}

		public void activateProgressBar(final Task<?> task) {
			pb.progressProperty().bind(task.progressProperty());
			dialogStage.show();
		}
		
		public Stage getDialogStage() {
			return dialogStage;
		}
	}

}