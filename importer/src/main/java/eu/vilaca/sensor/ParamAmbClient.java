package eu.vilaca.sensor;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.List;

class ParamAmbClient {
	private ParamAmbClient() {
	}

	public static List<Event> parseMetrics(ResponseBody responseBody) {
		final var mapper = new ObjectMapper();
		try {
			return mapper.readValue(
					responseBody.string(),
					mapper.getTypeFactory().constructCollectionType(List.class, Event.class));
		} catch (IOException e) {
			return null;
		}
	}

	public static ResponseBody getMetrics() {
		final var client = new OkHttpClient();
		final var request = new Request.Builder()
				.url("http://opendata-cml.qart.pt:8080/lastmeasurements")
				.build();
		final ResponseBody responseBody;
		try {
			responseBody = client.newCall(request).execute().body();
		} catch (IOException e) {
			return null;
		}
		return responseBody;
	}
}
