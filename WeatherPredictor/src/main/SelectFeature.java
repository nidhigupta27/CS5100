package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import util.Feature;
import util.WeatherData;

public class SelectFeature 
{
	private String feature;
	private HashMap<Integer, WeatherData> xTrainLeftPart;
	private HashMap<Integer, WeatherData> xTrainRightPart;
	private HashMap<Integer, WeatherData> yTrainLeftPart;
	private HashMap<Integer, WeatherData> yTrainRightPart;
	private double gainRatio;
	private double maxGainRatioFeatureValue;
	HashMap<String, HashMap<Integer, WeatherData>> ysubset;
	HashMap<String, HashMap<Integer, WeatherData>> xsubset;
//Constructor of SelectFeature to initialize the fields of SelectFeature class
	public SelectFeature(String target_val, ArrayList<String> features,
			HashMap<Integer, WeatherData> XDataInTrain,
			HashMap<Integer, WeatherData> YDataInTrain) throws IOException {
		    feature = null;
		    gainRatio = Double.MIN_VALUE;
		    xTrainLeftPart = null;
		    xTrainRightPart = null;
		    yTrainLeftPart = null;
		    yTrainRightPart = null;
		HashMap<String, Double> gainRatioMap = new HashMap<String, Double>();

		// Loop through the feature list to find the feature with maximum Gain
		// Ratio
		for (String ftr : features) {
			double currentGainRatio = Double.MIN_VALUE;
			HashMap<Integer, WeatherData> xTrainLeftPartCurrent = null;
			HashMap<Integer, WeatherData> xTrainRightPartCurrent = null;
			HashMap<Integer, WeatherData> yTrainLeftPartCurrent = null;
			HashMap<Integer, WeatherData> yTrainRightPartCurrent = null;
			HashMap<String, HashMap<Integer, WeatherData>> ysubsetCurrent = null;
			HashMap<String, HashMap<Integer, WeatherData>> xsubsetCurrent = null;
			GainRatio gr = new GainRatio(XDataInTrain, YDataInTrain, ftr,target_val);
			currentGainRatio = gr.getGainRatio();
			xTrainLeftPartCurrent = gr.getXTrainLeftPart();
			xTrainRightPartCurrent = gr.getXTrainRightPart();
			yTrainLeftPartCurrent = gr.getYTrainLeftPart();
			yTrainRightPartCurrent = gr.getYTrainRightPart();
			maxGainRatioFeatureValue = gr.getMaxGainRatioFeatureValue();
			ysubsetCurrent = gr.getYSubset();
			xsubsetCurrent = gr.getXSubset();
			gainRatioMap.put(ftr, currentGainRatio);
			
			
			if (currentGainRatio > gainRatio) {
				gainRatio = currentGainRatio;
				xTrainLeftPart = xTrainLeftPartCurrent;
				xTrainRightPart = xTrainRightPartCurrent;
				yTrainLeftPart = yTrainLeftPartCurrent;
				yTrainRightPart = yTrainRightPartCurrent;
				ysubset = ysubsetCurrent;
				xsubset = xsubsetCurrent;
				feature = ftr;
			}
		}
		//Calculate the number of features with Gain as zero
		int count = 0;
		for (Map.Entry<String, Double> data : gainRatioMap.entrySet()) {
			String key = data.getKey();
			double gain = data.getValue();
			if (gain == 0.0)
				count++;

		}

		if (count == gainRatioMap.size()) {
			String key = null;
			for (Map.Entry<String, Double> data : gainRatioMap.entrySet()) {
				key = data.getKey();
				break;
			}
			GainRatio gr = new GainRatio(XDataInTrain, YDataInTrain, key,
					target_val);
			gainRatio = gr.getGainRatio();
			xTrainLeftPart = gr.getXTrainLeftPart();
			xTrainRightPart = gr.getXTrainRightPart();
			yTrainLeftPart = gr.getYTrainLeftPart();
			yTrainRightPart = gr.getYTrainRightPart();
			ysubset = gr.getYSubset();
			xsubset = gr.getXSubset();
			feature = gr.getFeature();

		}

	}
   //Get method to return feature
	public String getFeature() {
		return feature;
	}
	//Get method to return the  threshold value of feature
	public double getMaxGainRatioFeatureValue() {
		return maxGainRatioFeatureValue;
	}
	//Get method to return the  gain ratio of feature
	public double getGainRatio() {
		return gainRatio;
	}
	//Get method to return the  subset of the training data(all non categorical attributes)
	//having feature value less than or equal to threshold
	public HashMap<Integer, WeatherData> getXTrainLeftPart() {
		return xTrainLeftPart;
	}
	//Get method to return the  subset of the training data(non categorical attributes)
	//having feature value greater than threshold
	public HashMap<Integer, WeatherData> getXTrainRightPart() {
		return xTrainRightPart;
	}
	//Get method to return the  subset of the training data(categorical attributes)
	//having feature value less than or equal to threshold
	public HashMap<Integer, WeatherData> getYTrainLeftPart() {
		return yTrainLeftPart;
	}
	//Get method to return the  subset of the training data(categorical attributes)
	//having feature value greater than threshold
	public HashMap<Integer, WeatherData> getYTrainRightPart() {
		return yTrainRightPart;
	}
    
	//Get method to return Y subset of the training data(categorical attributes)
	
	public HashMap<String, HashMap<Integer, WeatherData>> getYSubset() {
		for (Map.Entry<String, HashMap<Integer, WeatherData>> data : ysubset
				.entrySet()) {
			String key = data.getKey();
			break;
		}

		return ysubset;
	}
	//Get method to return X subset of the training data(non categorical attributes)
	public HashMap<String, HashMap<Integer, WeatherData>> getXSubset() {
		return xsubset;
	}

}