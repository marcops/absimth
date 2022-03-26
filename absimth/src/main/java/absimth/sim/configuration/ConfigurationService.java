package absimth.sim.configuration;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import absimth.sim.configuration.model.AbsimthConfigurationModel;
import absimth.sim.configuration.model.CPUModel;
import absimth.sim.configuration.model.HardwareModel;
import absimth.sim.configuration.model.LogModel;
import absimth.sim.configuration.model.MemoryConfModel;
import absimth.sim.configuration.model.MemoryFaultModel;
import absimth.sim.configuration.model.ModulesModel;
import absimth.sim.configuration.model.ProgramModel;
import absimth.sim.configuration.model.RunModel;
import absimth.sim.configuration.model.hardware.memory.BankConfModel;
import absimth.sim.configuration.model.hardware.memory.BankGroupConfModel;
import absimth.sim.configuration.model.hardware.memory.CellConfModel;
import absimth.sim.configuration.model.hardware.memory.ChannelMode;
import absimth.sim.configuration.model.hardware.memory.ChipConfModel;
import absimth.sim.configuration.model.hardware.memory.ModuleConfModel;
import absimth.sim.configuration.model.hardware.memory.RankConfModel;
import absimth.sim.utils.AbsimLog;
import lombok.Data;

@Data
public class ConfigurationService {
	public static AbsimthConfigurationModel load(String configuration) throws Exception {
		byte[] jsonData = Files.readAllBytes(Paths.get(configuration));

		ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
		objectMapper.enable(Feature.ALLOW_YAML_COMMENTS);
		AbsimthConfigurationModel model = objectMapper.readValue(jsonData, AbsimthConfigurationModel.class);
		return setDefaultIfNotFound(model);
	}

	private static AbsimthConfigurationModel setDefaultIfNotFound(AbsimthConfigurationModel model) {
		validateHardware(model);
		validateLog(model);
		validateModules(model);
		validateRun(model);
		return model;
	}
	
	private static RunModel validateRun(AbsimthConfigurationModel model) {
		if(model.getRun() == null) return null;
		if(model.getRun().getCyclesByProgram() == null) model.getRun().setCyclesByProgram(5);
		List<ProgramModel> programs = model.getRun().getPrograms();
		if(programs == null) return model.getRun();
		validatePrograms(programs);
		return model.getRun();
	}
	
	private static void validatePrograms(List<ProgramModel> programs) {
		for (ProgramModel programModel : programs) {
			if(programModel.getCpu() == null) programModel.setCpu(0);
		}
	}

	private static ModulesModel validateModules(AbsimthConfigurationModel model) {
		if(model.getModules() == null) model.setModules(ModulesModel.builder().build());
		if(model.getModules().getMemoryController() == null) model.getModules().setMemoryController("NoEccMemoryController");
		if(model.getModules().getMemoryFaultInjection() == null) model.getModules().setMemoryFaultInjection(MemoryFaultModel.builder().name("NoFaultErrorMFI").build());
		
		return model.getModules();
	}

	private static LogModel validateLog(AbsimthConfigurationModel model) {
		if (model.getLog() == null) {
			AbsimLog.logView("Using default Log config");
			model.setLog(LogModel.builder().build());
		}
		return model.getLog();
	}

	private static HardwareModel validateHardware(AbsimthConfigurationModel model) {
		if(model.getHardware() == null) {
			AbsimLog.logView("Using default Hardware config");
			model.setHardware(HardwareModel.builder().build());
		}
		if (model.getHardware().getPeripheralAddressSize() == null) model.getHardware().setPeripheralAddressSize(0);
		if (model.getHardware().getPeripheralAddressSize() % 4 != 0 && model.getHardware().getPeripheralAddressSize() != 0) {
			int val = (model.getHardware().getPeripheralAddressSize()/4+1)*4;
			AbsimLog.logView("PeripheralAddressSize Should be a multiple of 4 changing "+model.getHardware().getPeripheralAddressSize()+" to " + val+ "\r\n");
			model.getHardware().setPeripheralAddressSize(val);
		}
		validateCpu(model.getHardware());
		validateMemory(model.getHardware());
		return model.getHardware();
	}

	private static MemoryConfModel validateMemory(HardwareModel hardware) {
		if(hardware.getMemory() == null) {
			AbsimLog.logView("Using default Memory config");
			hardware.setMemory(MemoryConfModel.builder().build());
		}
		if(hardware.getMemory().getChannelMode() == null) hardware.getMemory().setChannelMode(ChannelMode.SINGLE_CHANNEL);
		if(hardware.getMemory().getName() == null) hardware.getMemory().setName("DEFAULT_MEMORY");
		if(hardware.getMemory().getFrequencyMhz() == null) hardware.getMemory().setFrequencyMhz(100);
		if(hardware.getMemory().getCasLatency() == null) hardware.getMemory().setCasLatency(10);
		if(hardware.getMemory().getLinesPerClock() == null) hardware.getMemory().setLinesPerClock(2);
		validadeModule(hardware.getMemory());
		
		validateChannel(hardware.getMemory());
		return hardware.getMemory();
		
	}

	private static void validateChannel(MemoryConfModel memory) {
		int amount = memory.getModule().getAmount();
		if(isChannelValid(memory, amount))return;
		AbsimLog.logView("ChannelMode was changed because, Odd module must be " + ChannelMode.SINGLE_CHANNEL + "\r\n");
		memory.setChannelMode(ChannelMode.SINGLE_CHANNEL);
	}

	private static boolean isChannelValid(MemoryConfModel memory, int amount) {
		return (amount == 2 && memory.getChannelMode() == ChannelMode.DUAL_CHANNEL) || 
				(amount == 4 && memory.getChannelMode() == ChannelMode.QUAD_CHANNEL) ||
				(memory.getChannelMode() == ChannelMode.SINGLE_CHANNEL);
	}

	private static ModuleConfModel validadeModule(MemoryConfModel memory) {
		if(memory.getModule() == null)  {
//			AbsimLog.logView("Using default Module config");
			memory.setModule(ModuleConfModel.builder().build());
		}
		if(memory.getModule().getAmount() == null) memory.getModule().setAmount(1);
		
		validadeRank(memory.getModule());
		return memory.getModule();
	}

	private static RankConfModel validadeRank(ModuleConfModel module) {
		if(module.getRank() == null) {
//			AbsimLog.logView("Using default rank config");
			module.setRank(RankConfModel.builder().build());
		}
		if(module.getRank().getAmount() == null) module.getRank().setAmount(1);
		validateChip(module.getRank());
		return module.getRank();
	}

	private static ChipConfModel validateChip(RankConfModel rank) {
		if(rank.getChip() == null) {
//			AbsimLog.logView("Using default chip config");
			rank.setChip(ChipConfModel.builder().build());
		}
		if(rank.getChip().getAmount() == null) rank.getChip().setAmount(9);
		validateBankGroup(rank.getChip());
		return rank.getChip(); 
	}

	private static BankGroupConfModel validateBankGroup(ChipConfModel chip) {
		if(chip.getBankGroup() == null) {
//			AbsimLog.logView("Using default bank group config");
			chip.setBankGroup(BankGroupConfModel.builder().build());
		}
		if(chip.getBankGroup().getAmount() == null) chip.getBankGroup().setAmount(1);
		validateBank(chip.getBankGroup());
		return chip.getBankGroup() ;
	}

	private static BankConfModel validateBank(BankGroupConfModel bankGroup) {
		if(bankGroup.getBank()==null) {
//			AbsimLog.logView("Using default bank config");
			bankGroup.setBank(BankConfModel.builder().amount(1).build());
		}
		if(bankGroup.getBank().getAmount() == null) bankGroup.getBank().setAmount(1);
		validateCell(bankGroup.getBank());
		return bankGroup.getBank();
	}

	private static CellConfModel validateCell(BankConfModel bank) {
		if(bank.getCell() == null) {
//			AbsimLog.logView("Using default cell config");
			bank.setCell(CellConfModel.builder().build());
		}
		if(bank.getCell().getColumns() == null) bank.getCell().setColumns(1000);
		if(bank.getCell().getRow() == null) bank.getCell().setRow(1000);
		return bank.getCell() ;
	}

	private static List<CPUModel> validateCpu(HardwareModel hardware) {
		if(hardware.getCpu() == null) {
			AbsimLog.logView("Using Cpu config default");
			hardware.setCpu(Arrays.asList(CPUModel.builder().build()));
		}
		for(int i=0;i<hardware.getCpu().size();i++) {
			if(hardware.getCpu().get(i).getAmount() == null) hardware.getCpu().get(i).setAmount(1);
			if(hardware.getCpu().get(i).getName() == null) hardware.getCpu().get(i).setName("RISCV32i");
		}
		return hardware.getCpu();
	}
}
