package eu.vilaca.sensor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@Slf4j
@Service
class Transformer {
	List<Event> parseMetrics(String responseBody) {
		final var mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			return mapper.readValue(
					responseBody,
					mapper.getTypeFactory().constructCollectionType(List.class, Event.class));
		} catch (IOException ex) {
			log.info("Malformed (response) data. Exception parsing Json to Event.class", ex);
			return Collections.emptyList();
		}
	}
}
