package eu.vilaca.sensor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class Importer {

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

	@Scheduled(fixedDelay = 15 * 60 * 60 * 1000, initialDelay = 500)
	public void importMetrics() {

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
			this.meterRegistry.gauge("sensor-param-amb-lx", event.getTags(), event.getValue());
		}

		updateSuccess.increment();
	}
}