package eu.vilaca.sensor;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
class RegisterAndUpdateMetrics {

	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterAndUpdateMetrics.class);

	private final Map<String, Double> strongRefGauge = new HashMap<>(512);
	private final Map<String, List<Tag>> tagsCache = new HashMap<>(512);

	private final MeterRegistry registry;

	public RegisterAndUpdateMetrics(MeterRegistry registry) {
		this.registry = registry;
	}

	void registerAndUpdateMetrics(List<Event> events) {
		for (Event event : events) {
			if (event.isDown()) {
				continue;
			}
			final var tags = tagsCache.putIfAbsent(event.getId(), event.getTags());
			if (LOGGER.isDebugEnabled()) {
				final var tagContents = tags.stream()
						.map(tag -> tag.getKey() + ": " + tag.getValue() + ", ")
						.collect(Collectors.joining());
				LOGGER.debug(event.getValue() + " = " + tagContents);
			}
			registry.gauge("sensor-param-amb-lx", tags, strongRefGauge, g -> g.get(event.getId()))
					.put(event.getId(), event.getValue());
		}
	}
}
