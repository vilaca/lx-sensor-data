package eu.vilaca.sensor;

import io.micrometer.core.instrument.Metrics;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class Exporter {

	private final RegisterAndUpdateMetrics metrics;

	Exporter(RegisterAndUpdateMetrics metrics) {
		this.metrics = metrics;
	}

	public static void main(String[] args) {
		SpringApplication.run(Exporter.class, args);
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

		metrics.registerAndUpdateMetrics(events);

		Metrics.counter("import-param-amb-lx", "status", "success").increment();
	}
}