package absimth.sim.configuration;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import absimth.sim.configuration.model.AbsimthConfigurationModel;
import lombok.Data;

@Data
public class ConfigurationService {
	public static AbsimthConfigurationModel load(String configuration) throws Exception {
		byte[] jsonData = Files.readAllBytes(Paths.get(configuration));

		ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
		objectMapper.enable(Feature.ALLOW_YAML_COMMENTS);
		return objectMapper.readValue(jsonData, AbsimthConfigurationModel.class);
	}
}
