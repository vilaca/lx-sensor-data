package eu.vilaca.sensor;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
class Loader {

	private final Map<String, Double> strongRefGauge = new HashMap<>(512);
	private final Map<String, List<Tag>> tagsCache = new HashMap<>(512);

	private final MeterRegistry registry;

	void registerAndUpdateMetrics(List<Event> events) {
		events.stream()
				.filter(e -> !e.isDown())
				.forEach(this::registerAndUpdateMetric);
	}

	private void registerAndUpdateMetric(Event event) {

		final var tags = getEvents(event);
		if (log.isDebugEnabled()) {
			final var tagContents = tags.stream()
					.map(tag -> tag.getKey() + ": " + tag.getValue() + ", ")
					.collect(Collectors.joining());
			log.debug(event.getValue() + " = " + tagContents);
		}
		Objects.requireNonNull(registry.gauge("sensor-param-amb-lx", tags, strongRefGauge, g -> g.get(event.getId())))
				.put(event.getId(), event.getValue());
	}

	private List<Tag> getEvents(Event event) {
		var tags = tagsCache.get(event.getId());
		if (Objects.isNull(tags)) {
			tags = event.getTags();
			tagsCache.put(event.getId(), tags);
		}
		return tags;
	}
}
