package eu.vilaca.sensor;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

class ParamAmbClient {
	private ParamAmbClient() {
	}

	static List<Event> parseMetrics(String responseBody) {
		final var mapper = new ObjectMapper();
		try {
			return mapper.readValue(
					responseBody,
					mapper.getTypeFactory().constructCollectionType(List.class, Event.class));
		} catch (IOException e) {
			return Collections.emptyList();
		}
	}

	static String getMetrics() {
		final var client = new OkHttpClient();
		final var request = new Request.Builder()
				.url("http://opendata-cml.qart.pt:8080/lastmeasurements")
				.build();
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		} catch (IOException e) {
			return "";
		}
	}
}
