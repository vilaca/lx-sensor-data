package eu.vilaca.sensor;

import io.micrometer.core.instrument.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.micrometer.core.instrument.Tag.of;
import static java.util.List.of;
import static java.util.Map.*;

class Event {
	private String id;
	private String avg;
	private String date;
	private String dateStandard;
	private Double value;
	private String unit;
	private String address;
	private Coordinates coordinates;

	private static Map<String, List<Tag>> buildDic() {
		return ofEntries(
				entry("CT0TMD", of(of("desc", "Transit"), of("subdesc", "Vehicles"))),
				entry("ME00HR", of(of("desc", "Weather"), of("subdesc", "Relative humidity"))),
				entry("ME00PA", of(of("desc", "Weather"), of("subdesc", "Atmospheric pressure"))),
				entry("ME00PP", of(of("desc", "Weather"), of("subdesc", "Precipitation"))),
				entry("ME00UV", of(of("desc", "Weather"), of("subdesc", "Ultra violet"))),
				entry("ME00VD", of(of("desc", "Weather"), of("subdesc", "Wind direction"))),
				entry("ME00VI", of(of("desc", "Weather"), of("subdesc", "Wind intensity"))),
				entry("METEMP", of(of("desc", "Weather"), of("subdesc", "Temperature"))),
				entry("QA00CO", of(of("desc", "Air quality"), of("subdesc", "Carbon monoxide"))),
				entry("QA00NO", of(of("desc", "Air quality"), of("subdesc", "Nitrogen oxide"))),
				entry("QA00O3", of(of("desc", "Air quality"), of("subdesc", "Ozone"))),
				entry("QA0NO2", of(of("desc", "Air quality"), of("subdesc", "Nitrogen dioxide"))),
				entry("QA0SO2", of(of("desc", "Air quality"), of("subdesc", "Sulfur dioxide"))),
				entry("QAPM10", of(of("desc", "Air quality"), of("subdesc", "Particles < 10 µm"))),
				entry("QAPM25", of(of("desc", "Air quality"), of("subdesc", "Particles < 2.5 µm"))),
				entry("RULAEQ", of(of("desc", "Noise"), of("subdesc", "Continuous noise level."))));
	}

	public String getId() {
		return this.id;
	}

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

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	List<Tag> getTags() {
		final var tags = new ArrayList<Tag>();
		tags.add(of("id", this.id));
		tags.add(of("type", this.metricId()));
		tags.add(of("locid", this.id.substring(this.id.length() - 4)));
		final var idx = this.address.indexOf(" - ");
		if (idx == -1) {
			tags.add(of("location", this.address));
		} else {
			tags.add(of("location", this.address.substring(idx + 3)));
			tags.add(of("location-area", this.address.substring(0, idx)));
		}
		tags.add(of("unit", this.unit));
		tags.add(of("lat", coordinates.lat.toString()));
		tags.add(of("lon", coordinates.lng.toString()));
		for (var entry : buildDic().entrySet()) {
			if (this.id.startsWith(entry.getKey())) {
				tags.addAll(entry.getValue());
			}
		}
		return tags;
	}

	boolean isDown() {
		return this.value == -99.0;
	}

	private String metricId() {
		return this.id.substring(0, this.id.length() - 4);
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
