package util;

import java.util.ArrayList;
import java.util.List;
// blue print for a feature of weather data
// Each feature has a name and value
// For example, name: "Mean Temperature" value: {87}
//              name: "Events" value: {"Fog","Rain"}
public class Feature<V> {
	private String name;
	private List<V> values = new ArrayList<V>();

	// constructor
	public Feature(List<V> values, String name) {
		this.name = name;
		this.values=  values;
	}

	// returns name of a feature
	public String getName() {
		return this.name;
	}

	// return value of a feature
	public List<V> getValues(){
		return this.values;
	}

	// sets the name of a feature
	public void setName(String name) {
		this.name = name;
	}

	// sets the value of a feature
	public void setValues(List<V> values) {
		this.values=values;
	}
}