package eu.vilaca.sensor;

import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.stream.Collectors;

@SpringBootApplication
@EnableScheduling
public class Importer {

	private static final Logger logger = LoggerFactory.getLogger(Importer.class);

	public static void main(String[] args) {
		SpringApplication.run(Importer.class, args);
	}

	@Scheduled(fixedDelay = 10 * 60 * 1000, initialDelay = 500)
	private void importMetrics() {

		Metrics.counter("import-param-amb-lx", "status", "started").increment();

		final var responseBody = ParamAmbClient.getMetrics();
		if (responseBody.isEmpty()) {
			Metrics.counter("import-param-amb-lx", "status", "download-failed").increment();
			return;
		}

		final var events = ParamAmbClient.parseMetrics(responseBody);
		if (events.isEmpty()) {
			Metrics.counter("import-param-amb-lx", "status", "parse-failed").increment();
			return;
		}

		for (Event event : events) {
			if (event.isDown()) {
				continue;
			}
			final var tags = event.getTags();
			if (logger.isDebugEnabled()) {
				final var tagContents = tags.stream()
						.map(tag -> tag.getKey() + ": " + tag.getValue() + ", ")
						.collect(Collectors.joining());
				logger.debug(event.getValue() + " = " + tagContents);
			}
			Metrics.gauge("sensor-param-amb-lx", tags, event.getValue());
		}

		Metrics.counter("import-param-amb-lx", "status", "success").increment();

		logger.debug("Import metrics finished.");
	}
}