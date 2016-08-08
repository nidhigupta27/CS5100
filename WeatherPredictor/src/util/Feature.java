package util;

import java.util.ArrayList;
import java.util.List;

public class Feature<V> {
	private String name;
	private List<V> values = new ArrayList<V>();

	public Feature(List<V> values, String name) {
		this.name = name;
		this.values=  values;

	}

	public String getName() {
		return this.name;
	}

	public List<V> getValues(){
		return this.values;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValues(List<V> values) {
		this.values=values;
	}
}
