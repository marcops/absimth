package absimth.sim.gui.helper;

import java.io.IOException;
import java.util.List;

import absimth.sim.SimulatorManager;
import absimth.sim.configuration.model.hardware.memory.PhysicalAddress;
import absimth.sim.gui.AlertDialog;
import absimth.sim.gui.CPUController;
import absimth.sim.gui.CpuTimelineController;
import absimth.sim.gui.HelpController;
import absimth.sim.gui.Memory3DCell;
import absimth.sim.gui.MemoryViewByAddressController;
import absimth.sim.gui.MemoryViewByHierarchyBankGroupController;
import absimth.sim.gui.MemoryViewByHierarchyCellController;
import absimth.sim.gui.MemoryViewByHierarchyModuleController;
import absimth.sim.gui.model.Cell3DInfoModel;
import absimth.sim.memoryController.model.ECCMemoryFaultModel;
import absimth.sim.memoryController.model.ECCMemoryFaultType;
import absimth.sim.utils.HexaFormat;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Cell;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;

public class UIUtil {
	
	public static void updateAllWindows() {
		Window.getWindows().forEach(x -> {
			x.fireEvent(new AbsimthEvent(AbsimthEvent.ABSIMTH_UPDATE_EVENT));
		});
	}
	
	public static void erasePanel(GridPane pane) {
		pane.getColumnConstraints().clear();
		pane.getRowConstraints().clear();
		pane.getChildren().clear();
	}
	
	public static void printCellMemoryStatus(Cell<?> cell, ECCMemoryFaultModel rep) {
		printCellMemoryStatus(cell, rep,0, -1);
	}
	
	public static void printCellMemoryStatus(Cell<?> cell, ECCMemoryFaultModel rep, int chipPos, int position) {
		if (rep == null) {
			cell.setStyle(null);
			return;
		}
		if(!rep.getPosition().contains(position) && position != -1) {
			cell.setStyle(null);
			return;
		}
		
		if(!rep.hasFaulInThisChip(chipPos)) {
			cell.setStyle(null);
			return;
		}
		
		ECCMemoryFaultType status = rep.getFaultType();
		switch (status) {
			case INVERTED: cell.setStyle(UIColors.COLUMN_DATA_FAIL_NOT_READ); break;
			case FIXABLE_ERROR: cell.setStyle(UIColors.COLUMN_DATA_FAIL_READ_AND_FIXED); break;
			case UNFIXABLE_ERROR: cell.setStyle(UIColors.COLUMN_DATA_FAIL_READ_AND_NOT_FIXABLE); break;
			default: cell.setStyle(null); break;
		}
	}
	
	public static int getAddressFromField(String orig) {
		final int totalAddress = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getTotalOfAddress().intValue() -1;
		try {
			final String dest = orig.toLowerCase().replaceAll("x", "");
			int destAddr = Integer.parseInt(dest, 16);
			if (destAddr < totalAddress) return destAddr;
			AlertDialog.error("Address exceeds memory (" + HexaFormat.f(totalAddress) + " )");
		} catch (NumberFormatException e) {
			AlertDialog.error("Failed to parse 32bit hexadecimal address (use without 0x-prefix)");
			System.out.println(e);
		}
		return -1;
	}
	
	private static double getHeightMemoryModuleView() {
		int each = 125;
		int max = 800;
		int module = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getModule().getAmount();
		int rank = SimulatorManager.getSim().getAbsimthConfiguration().getHardware().getMemory().getModule().getRank().getAmount();
		int size = module * rank * each;
		size+=26;
		if(size> max) return max;
		if(size < each) return each;
		return size;
	}
	
	
	public static void openHelp() {
		try {
			FXMLLoader loader = new FXMLLoader(Thread.currentThread().getContextClassLoader().getResource("gui/help.fxml"));
			Parent root = loader.load();
			HelpController controller = loader.getController();
			Stage stage = new Stage();
			controller.setStage(stage);
			
			stage.setScene(new Scene(root, 500, 500));
			stage.show();
			// Hide this current window (if this is what you want)
//				((Node) (event.getSource())).getScene().getWindow().hide();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void openMemoryCell3D(List<List<List<Cell3DInfoModel>>> dData) {
		Platform.runLater(() -> {
			Memory3DCell controller = new Memory3DCell();
			controller.start(dData);
		});
	}
	
	public static void openMemoryViewModule() {
		try {
			FXMLLoader loader = new FXMLLoader(Thread.currentThread().getContextClassLoader().getResource("gui/memoryHierarchyModule.fxml"));
			Parent root = loader.load();
			MemoryViewByHierarchyModuleController controller = loader.getController();
			Stage stage = new Stage();
			controller.setStage(stage);
			
			stage.setScene(new Scene(root, 950, getHeightMemoryModuleView()));
			stage.show();
			// Hide this current window (if this is what you want)
//				((Node) (event.getSource())).getScene().getWindow().hide();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void openMemoryViewBank(int module, int rank, int chipPos) {
 		try {
 			FXMLLoader loader = new FXMLLoader(Thread.currentThread().getContextClassLoader().getResource("gui/memoryHierarchyBankGroup.fxml"));
 			Parent root = loader.load();
 			MemoryViewByHierarchyBankGroupController controller = loader.getController();
 			Stage istage = new Stage();
 			controller.setStage(istage, module, rank , chipPos);
 			int heigth = 600;
 			if(MemoryViewByHierarchyBankGroupController.getBankGroupRowSize() == 1) heigth = 200;
 			int width = 1100;
 			istage.setScene(new Scene(root, width, heigth));
 			istage.show();
 			// Hide this current window (if this is what you want)
// 			((Node) (event.getSource())).getScene().getWindow().hide();
 		} catch (IOException e) {
 			e.printStackTrace();
 		}
 	}
	
	public static void openCpuInstructionView() {
		try {
			FXMLLoader loader = new FXMLLoader(Thread.currentThread().getContextClassLoader().getResource("gui/cpu.fxml"));
			Parent root = loader.load();
			CPUController controller = loader.getController();
			Stage stage = new Stage();
			controller.setStage(stage);
			stage.setScene(new Scene(root, 800, 800));
			stage.show();
			// Hide this current window (if this is what you want)
//			((Node) (event.getSource())).getScene().getWindow().hide();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public static void openMemoryViewAddress() {
		try {
			FXMLLoader loader = new FXMLLoader(Thread.currentThread().getContextClassLoader().getResource("gui/memoryViewByAddressController.fxml"));
			Parent root = loader.load();
			MemoryViewByAddressController controller = loader.getController();
			Stage stage = new Stage();
			controller.setStage(stage);
			stage.setScene(new Scene(root, 1100, 880));
			stage.show();
			// Hide this current window (if this is what you want)
//			((Node) (event.getSource())).getScene().getWindow().hide();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	public static void openMemoryViewCell(int module, int rank, int chipPos, int bankGroup, int bank) {
		openMemoryViewCell(module, rank, chipPos,bankGroup, bank, null);
	}
	public static void openMemoryViewCell(int module, int rank, int chipPos, int bankGroup, int bank, PhysicalAddress pa) {
 		try {
// 			System.out.println("m="+module+", r="+rank+",c="+ chipPos+",gp"+ bankGroup+",b="+bank);
 			FXMLLoader loader = new FXMLLoader(Thread.currentThread().getContextClassLoader().getResource("gui/memoryHierarchyCell.fxml"));
 			Parent root = loader.load();
 			MemoryViewByHierarchyCellController controller = loader.getController();
 			Stage istage = new Stage();
 			controller.setStage(istage, module, rank , chipPos, bankGroup, bank, pa);
 			int width = 800;
 			int heigth = 460;
 			istage.setResizable(false);
 			istage.setScene(new Scene(root, width, heigth));
 			istage.show();
 			// Hide this current window (if this is what you want)
// 			((Node) (event.getSource())).getScene().getWindow().hide();
 		} catch (IOException e) {
 			e.printStackTrace();
 		}
	}

	public static void openCpuTimeline() {
		try {
			FXMLLoader loader = new FXMLLoader(Thread.currentThread().getContextClassLoader().getResource("gui/cpuTimeline.fxml"));
			Parent root = loader.load();
			CpuTimelineController controller = loader.getController();
			Stage stage = new Stage();
			stage.setScene(new Scene(root, 1820, 620));
			controller.setStage(stage);
			stage.show();
			// Hide this current window (if this is what you want)
//			((Node) (event.getSource())).getScene().getWindow().hide();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
