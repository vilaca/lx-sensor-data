package eu.vilaca.sensor;

import io.micrometer.core.instrument.Metrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class Exporter {

	private final Extractor extractor;
	private final Transformer transformer;
	private final Loader metrics;

	public static void main(String[] args) {
		SpringApplication.run(Exporter.class, args);
	}

	@Scheduled(fixedDelay = 10 * 60 * 1000, initialDelay = 500)
	void importMetrics() {

		try {
			final var responseBody = extractor.getMeasurements();
			if (responseBody.isEmpty()) {
				Metrics.counter("import-param-amb-lx", "status", "download-failed").increment();
				return;
			}
			final var events = transformer.parseMetrics(responseBody.get());
			if (events.isEmpty()) {
				Metrics.counter("import-param-amb-lx", "status", "parse-failed").increment();
				return;
			}
			metrics.registerAndUpdateMetrics(events);
			Metrics.counter("import-param-amb-lx", "status", "success").increment();
		} finally {
			Metrics.counter("import-param-amb-lx", "status", "counter").increment();
		}
	}
}