package absimth.sim.configuration;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import absimth.sim.configuration.model.AbsimthConfigurationModel;
import lombok.Data;

@Data
public class ConfigurationService {
	public static AbsimthConfigurationModel load(String configuration) throws Exception {
		byte[] jsonData = Files.readAllBytes(Paths.get(configuration));

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		return objectMapper.readValue(jsonData, AbsimthConfigurationModel.class);
	}
}
