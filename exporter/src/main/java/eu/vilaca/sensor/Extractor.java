package eu.vilaca.sensor;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@NoArgsConstructor
@Slf4j
@Service
class Extractor {

	private static final String AMBIENT_PARAMS_MEASUREMENTS_URL = "http://opendata-cml.qart.pt:8080/lastmeasurements";

	Optional<String> getMeasurements() {
		final var request = new Request.Builder()
				.url(AMBIENT_PARAMS_MEASUREMENTS_URL)
				.build();
		final var restCall = new OkHttpClient().newCall(request);
		try (final var response = restCall.execute()) {
			final var body = response.body();
			if (Objects.nonNull(body)) {
				final var bodyAsString = body.string();
				if (!bodyAsString.isEmpty()) {
					return Optional.of(bodyAsString);
				}
			}
			log.warn("Empty/Null response from CML open data endpoint.");
		} catch (IOException ex) {
			log.warn("Failure calling CML open data endpoint.", ex);
		}
		return Optional.empty();
	}
}
