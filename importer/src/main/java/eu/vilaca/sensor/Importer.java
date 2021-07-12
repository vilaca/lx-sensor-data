package eu.vilaca.sensor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableScheduling
public class Importer {

	private static final Logger logger = LoggerFactory.getLogger(Importer.class);

	private final MeterRegistry meterRegistry;
	private final Counter update;
	private final Counter updateSuccess;
	private final Counter dlFailed;
	private final Counter psFailed;

	private List<Event> events;

	public Importer(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
		this.update = meterRegistry.counter("import started");
		this.updateSuccess = meterRegistry.counter("import success");
		this.dlFailed = meterRegistry.counter("import failed error downloading.");
		this.psFailed = meterRegistry.counter("import failed error parsing.");
	}

	public static void main(String[] args) {
		SpringApplication.run(Importer.class, args);
	}

	@Scheduled(fixedDelay = 10 * 60 * 1000, initialDelay = 500)
	private void importMetrics() {

		logger.debug("Import metrics started.");

		update.increment();

		final var responseBody = ParamAmbClient.getMetrics();
		if (responseBody == null) {
			dlFailed.increment();
			return;
		}

		final var events = ParamAmbClient.parseMetrics(responseBody);
		if (events == null) {
			psFailed.increment();
			return;
		}

		this.events = events;

		for (Event event : events) {
			final var tags = event.getTags();
			if (logger.isDebugEnabled()) {
				final var tagContents = tags.stream()
						.map(tag -> tag.getKey() + ": " + tag.getValue() + ", ")
						.collect(Collectors.joining());
				logger.debug(event.getValue() + " = " + tagContents);
			}
			this.meterRegistry.gauge("sensor-param-amb-lx", tags, event.getValue());
		}

		updateSuccess.increment();

		logger.debug("Import metrics finished.");
	}
}