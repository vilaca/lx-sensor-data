package eu.vilaca.sensor;

import io.micrometer.core.instrument.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.micrometer.core.instrument.Tag.*;

class Event {
	private static final Map<String, List<Tag>> DIC = buildDic();
	private String id;
	private String avg;
	private String date;
	private String dateStandard;
	private Double value;
	private String unit;
	private String address;
	private Coordinates coordinates;

	private static Map<String, List<Tag>> buildDic() {
		var map = new HashMap<String, List<Tag>>();
		map.put("CT0TMD", List.of(of("desc", "Transit"), of("subdesc", "Vehicles")));
		map.put("ME00HR", List.of(of("desc", "Weather"), of("subdesc", "Relative humidity")));
		map.put("ME00PA", List.of(of("desc", "Weather"), of("subdesc", "Atmospheric pressure")));
		map.put("ME00PP", List.of(of("desc", "Weather"), of("subdesc", "Precipitation")));
		map.put("ME00UV", List.of(of("desc", "Weather"), of("subdesc", "Ultra violet")));
		map.put("ME00VD", List.of(of("desc", "Weather"), of("subdesc", "Wind direction")));
		map.put("ME00VI", List.of(of("desc", "Weather"), of("subdesc", "Wind intensity")));
		map.put("METEMP", List.of(of("desc", "Weather"), of("subdesc", "Temperature")));
		map.put("QA00CO", List.of(of("desc", "Air quality"), of("subdesc", "Carbon monoxide")));
		map.put("QA00NO", List.of(of("desc", "Air quality"), of("subdesc", "Nitrogen oxide")));
		map.put("QA00O3", List.of(of("desc", "Air quality"), of("subdesc", "Ozone")));
		map.put("QA0NO2", List.of(of("desc", "Air quality"), of("subdesc", "Nitrogen dioxide")));
		map.put("QA0SO2", List.of(of("desc", "Air quality"), of("subdesc", "Sulfur dioxide")));
		map.put("QAPM10", List.of(of("desc", "Air quality"), of("subdesc", "Particles with a diameter < 10 \uF06Dm")));
		map.put("QAPM25", List.of(of("desc", "Air quality"), of("subdesc", "Particles with a diameter < 2.5 \uF06Dm")));
		map.put("RULAEQ", List.of(of("desc", "Noise"), of("subdesc", "Continuous noise level.")));
		return Collections.unmodifiableMap(map);
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
			tags.add(of("zone", this.address.substring(0, idx)));
		}
		tags.add(of("unit", this.unit));
		tags.add(of("lat", coordinates.lat.toString()));
		tags.add(of("lon", coordinates.lng.toString()));
		tags.add(of("last-update", date + "-" + dateStandard));
		for (var entry : DIC.entrySet()) {
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
