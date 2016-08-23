package util;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// This class is a blue print for weather data.
// contains all the features for a data.

public class WeatherData {
	private ArrayList<Feature> features;
	private ArrayList<String> featureNames;
	public WeatherData() {
		features = new ArrayList<Feature>();
		featureNames =new ArrayList<String>();
	}

	// updates the list of feature name for the weather instance
	public void addFeatureNames(String names) {
		String[] tempNames= names.split(",");
		for(String name: tempNames) {
			featureNames.add(name);
		}
	}

	// featureValue() takes a feature name as input and returns corresponding value.
	public ArrayList<String> featureValue(String featureName) {
		for(int i=0;i<this.features.size();i++) {
			if(this.features.get(i).getName().contains(featureName)) {
				ArrayList<String> temp = new ArrayList<String>();
				temp.addAll(this.features.get(i).getValues());
				return temp;
			}
		}
		return null;
	}

	// returns Y value of current instance (ie; class name)
	public List getYValue() {
		int i=0;
		for(String category: this.getFeatureNames() )
		{
			if(category.contains("Events"))
				return this.getFeatures().get(i).getValues();
		}
		return null;
	}

	// getFeatures() returns all the features of current weather data
	public ArrayList<Feature> getFeatures() {
		return this.features;
	}

	// getFeatureNames() returns all the feature names of current weather data
	public ArrayList<String> getFeatureNames() {
		return this.featureNames;
	}

	// updates X data as a weather instance
	public void addFeaturesToXData(String data) {
		String[] featureValues= data.split(",");
		int i=0;
		for(String feature: featureValues) 
		{
			// ignore Events column as it contains class labels
			if (featureNames.get(i).contains("Events"))
			{
				i++;
				continue;
			}
			// if the occurance of Precipitation is trace (represented as 'T') , it is replaced with 0.0001
			// for uniformity
			else 
			{
				if(featureNames.get(i).contains("PrecipitationIn")) {
					if(feature.contains("T")) {
						feature="0.0001";
					}
					Object featureObj = feature;
					List<Object> precp = new ArrayList<Object>();
					precp.add(feature);
					features.add(new Feature<Object> (precp,featureNames.get(i) ));
				}
				// any missing value is replaced with 0 here
				else {
					Object featureObj = feature;
					if (feature.isEmpty())
					{
						feature = "0";
					}
					List<Object> val = new ArrayList<Object>();
					val.add(feature);
					features.add(new Feature<Object> (val,featureNames.get(i) ));
				}			
			}
			i++;
		}
	}

	// addFeaturesToYData() creates a weather instance with class label
	public void addFeaturesToYData(String data) {
		String[] featureValues= data.split(",");
		int i=0;
		for(String feature: featureValues) 
		{
			// ignores all labels except Events
			if(!featureNames.get(i).contains("Events")) {
				i++;
				continue;
			}
			else
			{
				List<String> events = new ArrayList<String>();
				if(feature.isEmpty()) {
					String event = "";
					events.add(event);
					features.add(new Feature<String> (events,featureNames.get(i) ));
				} else {
					events = new ArrayList<String>();
					String[] eventSplit = feature.split("-");
					for(int j=0;j<eventSplit.length;j++) {
						events.add(eventSplit[j]);
					}
					features.add(new Feature<String> (events,featureNames.get(i) ));				
				}
			}
			i++;
		}
	}
}