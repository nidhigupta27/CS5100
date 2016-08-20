package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Feature;
import util.WeatherData;

public class MissingValues {
	public HashMap<Integer, WeatherData> resolveMissingValue(
			HashMap<Integer, WeatherData> XDataMap,
			HashMap<Integer, WeatherData> YDataMap) {
		for (Map.Entry<Integer, WeatherData> keyWeatherData : XDataMap
				.entrySet()) {
			List<String> newFVal = new ArrayList<String>();
			WeatherData wd = keyWeatherData.getValue();
			int keyInData = keyWeatherData.getKey();
			ArrayList<Feature> featureList = wd.getFeatures();
			for (Feature f : featureList) {
				if (!f.getName().contains("EST")) {
					String NATest = (String) f.getValues().get(0);
					if (NATest.contains("NA")) {
						WeatherData wdata = YDataMap.get(keyInData);
						ArrayList<Feature> yFeatureList = wdata.getFeatures();
						for (Feature yf : yFeatureList) {
							String label = (String) yf.getValues().get(0);
							System.out.println("The label and fname " + label
									+ " " + f.getName());
							newFVal.add(getFeatureVal(label, f.getName(),
									XDataMap, YDataMap));
							System.out.println("th output from getFeatureVal"
									+ newFVal);
							// if(newFVal!=null)
							// {
							f.setValues(newFVal);
							// }

						}
					}
				}
			}
		}

		return XDataMap;
	}

	private String getFeatureVal(String label, String fName,
			HashMap<Integer, WeatherData> XDataMap,
			HashMap<Integer, WeatherData> YDataMap) {
		String notNAFValues = "";
		boolean gotFValue = false;
		for (Map.Entry<Integer, WeatherData> keyWeatherData : XDataMap
				.entrySet()) {
			WeatherData wd = keyWeatherData.getValue();
			int keyInData = keyWeatherData.getKey();
			ArrayList<Feature> featureList = wd.getFeatures();
			for (Feature f : featureList) {
				if (!f.getName().contains("EST")) {
					String notAnNA = (String) f.getValues().get(0);
					if (f.getName().equals(fName) && (!notAnNA.equals("NA"))) {
						WeatherData wdata = YDataMap.get(keyInData);
						ArrayList<Feature> yFeatureList = wdata.getFeatures();
						for (Feature yf : yFeatureList) {
							if (yf.getValues().contains(label)) {
								System.out.println("missing values" + notAnNA);
								notNAFValues = notAnNA;
								gotFValue = true;
							}
						}
					}
				}
				if (gotFValue)
					break;
			}
			if (gotFValue)
				break;
		}
		if (notNAFValues.isEmpty()) {
			notNAFValues = "0";
		}
		return notNAFValues;

	}

}
