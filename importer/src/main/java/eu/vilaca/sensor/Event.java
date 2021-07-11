package eu.vilaca.sensor;

import io.micrometer.core.instrument.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.micrometer.core.instrument.Tag.*;

class Event {
	private static final Map<String, List<Tag>> DIC = Map.of(
			"QA00CO",
			List.of(of("tag", "Air quality"), of("subtag", "Carbon monoxide")),
			"QA0NO2",
			List.of(of("tag", "Air quality"), of("subtag", "Nitrogen dioxide")),
			"QAPM10",
			List.of(of("tag", "Air quality"), of("subtag", "Particles with a diameter < 10 \uF06Dm")),
			"QAPM25",
			List.of(of("tag", "Air quality"), of("subtag", "Particles with a diameter < 2.5 \uF06Dm")),
			"METEMP",
			List.of(of("tag", "Weather"), of("subtag", "Temperature")),
			"ME00HR",
			List.of(of("tag", "Weather"), of("subtag", "Relative humidity")),
			"ME00PA",
			List.of(of("tag", "Weather"), of("subtag", "Atmospheric pressure")),
			"RULAEQ",
			List.of(of("tag", "Noise"), of("subtag", "Continuous noise level."))
	);

	private String id;
	private String avg;
	private String date;
	private String dateStandard;
	private Double value;
	private String unit;
	private String address;
	private Coordinates coordinates;

	public void setId(String id) {
		this.id = id;
	}

	public void setAvg(String avg) {
		this.avg = avg;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setDateStandard(String dateStandard) {
		this.dateStandard = dateStandard;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	public List<Tag> getTags() {
		final var tags = new ArrayList<Tag>();
		tags.add(of("id", this.id));
		tags.add(of("type", this.metricId()));
		final var idx = this.address.indexOf(" - ");
		if (idx == -1) {
			tags.add(of("location", this.address));
		} else {
			tags.add(of("location", this.address.substring(idx + 3)));
			tags.add(of("zone", this.address.substring(0, idx)));
		}
		tags.add(of("locid", this.id.substring(this.id.length() - 4)));
		tags.add(of("unit", this.unit));
		tags.add(of("lat", coordinates.lat.toString()));
		tags.add(of("lon", coordinates.lng.toString()));
		tags.add(of("up", Boolean.valueOf(!isDown()).toString()));
		for (var entry : DIC.entrySet()) {
			if (this.id.startsWith(entry.getKey())) {
				tags.addAll(entry.getValue());
			}
		}
		return tags;
	}

	private String metricId() {
		return this.id.substring(0, this.id.length() - 4);
	}

	public boolean isDown() {
		return this.value == -99.0;
	}

	private static class Coordinates {
		private Integer x;
		private Integer y;
		private Integer z;
		private Double lat;
		private Double lng;

		public void setX(Integer x) {
			this.x = x;
		}

		public void setY(Integer y) {
			this.y = y;
		}

		public void setLat(Double lat) {
			this.lat = lat;
		}

		public void setLng(Double lng) {
			this.lng = lng;
		}

		public void setZ(Integer z) {
			this.z = z;
		}
	}
}
