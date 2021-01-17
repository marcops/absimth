package absimth.sim.configuration;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import absimth.sim.configuration.model.AbsimthConfigurationModel;
import absimth.sim.configuration.model.BankConfModel;
import absimth.sim.configuration.model.BankGroupConfModel;
import absimth.sim.configuration.model.CPUModel;
import absimth.sim.configuration.model.CellConfModel;
import absimth.sim.configuration.model.ChannelMode;
import absimth.sim.configuration.model.ChipConfModel;
import absimth.sim.configuration.model.HardwareModel;
import absimth.sim.configuration.model.LogModel;
import absimth.sim.configuration.model.MemoryConfModel;
import absimth.sim.configuration.model.ModuleConfModel;
import absimth.sim.configuration.model.RankConfModel;
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
		return model;
	}

	private static LogModel validateLog(AbsimthConfigurationModel model) {
		if(model.getLog() == null) {
			model.setLog(LogModel.builder()
					.cpu(true)
					.cpuInstruction(true)
					.memory(true)
					.other(true)
					.build());
		}
		return model.getLog();
	}

	private static HardwareModel validateHardware(AbsimthConfigurationModel model) {
		if(model.getHardware() == null) {
			AbsimLog.log("Using default Hardware config");
			model.setHardware(HardwareModel.builder()
					.build());
		}
		validateCpu(model.getHardware());
		validateMemory(model.getHardware());
		return model.getHardware();
	}

	private static MemoryConfModel validateMemory(HardwareModel hardware) {
		if(hardware.getMemory() == null) {
			AbsimLog.log("Using default Memory config");
			hardware.setMemory(MemoryConfModel.builder()
							.channelMode(ChannelMode.SINGLE)
							.name("DEFAULT_MEMORY")
							.build());
		}
		validadeModule(hardware.getMemory());
		return hardware.getMemory();
		
	}

	private static ModuleConfModel validadeModule(MemoryConfModel memory) {
		if(memory.getModule() == null) 
			memory.setModule(ModuleConfModel.builder().amount(1).build());

		validadeRank(memory.getModule());
		return memory.getModule();
	}

	private static RankConfModel validadeRank(ModuleConfModel module) {
		if(module.getRank() == null) 
			module.setRank(RankConfModel.builder().amount(1).build());
		validateChip(module.getRank());
		return module.getRank();
	}

	private static ChipConfModel validateChip(RankConfModel rank) {
		if(rank.getChip() == null) 
			rank.setChip(ChipConfModel.builder().amount(1).build());
		validateBankGroup(rank.getChip());
		return rank.getChip(); 
	}

	private static BankGroupConfModel validateBankGroup(ChipConfModel chip) {
		if(chip.getBankGroup() == null)
			chip.setBankGroup(BankGroupConfModel.builder().amount(1).build());
		validateBank(chip.getBankGroup());
		return chip.getBankGroup() ;
	}

	private static BankConfModel validateBank(BankGroupConfModel bankGroup) {
		if(bankGroup.getBank()==null) 
			bankGroup.setBank(BankConfModel.builder().amount(1).build());
		validateCell(bankGroup.getBank());
		return bankGroup.getBank();
	}

	private static CellConfModel validateCell(BankConfModel bank) {
		if(bank.getCell() == null) 
			bank.setCell(CellConfModel.builder().columns(1000).row(1000).build());
		return bank.getCell() ;
	}

	private static CPUModel validateCpu(HardwareModel hardware) {
		if(hardware.getCpu() == null) {
			AbsimLog.log("Using Cpu config default");
			hardware.setCpu(CPUModel.builder()
						.amount(1)
						.build());
		}
		return hardware.getCpu();
	}
}
