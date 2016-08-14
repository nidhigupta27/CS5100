package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import util.Feature;
import util.WeatherData;

public class DecisionTree {
	public static void main(String args[]) throws IOException {
		HashMap<Integer,WeatherData> XtrainDataMap = new LinkedHashMap<Integer,WeatherData>();
		HashMap<Integer,WeatherData> YtrainDataMap= new LinkedHashMap<Integer,WeatherData>();
		HashMap<Integer,WeatherData> XtestDataMap = new LinkedHashMap<Integer,WeatherData>();
		HashMap<Integer,WeatherData> YtestDataMap = new LinkedHashMap<Integer,WeatherData>();
		ArrayList<Feature> features = new ArrayList<Feature>();
		
		XtrainDataMap = readData("weatherDataTrain.txt",true);
		YtrainDataMap = readData("weatherDataTrain.txt",false);
		XtestDataMap = readData("weatherDataTest.txt",true);
		YtrainDataMap = readData("weatherDataTest.txt",false);
		String target_val = "Rain";
		for(Map.Entry<Integer, WeatherData> key_weatherData : XtrainDataMap.entrySet())
		{
			WeatherData wd = key_weatherData.getValue();
			ArrayList<Feature> allFeatures = wd.getFeatures();
			for(Feature f : allFeatures)
			{
				if((f.getName().contains("EST")) || (f.getName().contains("PrecipitationIn")))
				{
					continue;
				}
				else
				{
					features.add(f);
				}
					
			}
			break;
		} 
		SelectFeature sFeature = new SelectFeature(target_val,features,XtrainDataMap,YtrainDataMap);
		Feature selected_feat = sFeature.getFeature();
		double selected_GainRatio = sFeature.getGainRatio();
		HashMap<Integer,WeatherData>xTrainLeftParts = sFeature.getXTrainLeftPart();
		HashMap<Integer,WeatherData>xTrainRightParts = sFeature.getXTrainRightPart();
		HashMap<Integer,WeatherData>yTrainLeftParts = sFeature.getYTrainLeftPart();
		HashMap<Integer,WeatherData>yTrainRightParts = sFeature.getXTrainRightPart();
		double maxGainRatioFeatureValue = sFeature.getMaxGainRatioFeatureValue();
		System.out.println("the feature  selected is"+selected_feat.getName());
		/*InformationRatio ir = new InformationRatio();
		for(WeatherData wd : XtrainData)
		{
			//System.out.println("the inf ratio is");
			ArrayList<Feature> frs = wd.getFeatures();
		    for(Feature f : frs)
		    {
		    	if(f.getName().equals("Mean TemperatureF"))
		    	{
		    		System.out.println("the feature is"+f.getName());
		    		double gainr = ir.calcInformationRatio(XtrainData,YtrainData,f);
		    		System.out.println("the inf ratio is"+gainr);
		    	
		    	}
		    }
		    break;
			//ir.calcInformationRatio(trainDataX,trainDataY,)
		}*/
		
		/*for(WeatherData wd : YtrainData)
		{
			for(Feature fd: wd.getFeatures())
			{
				System.out.println(fd.getName());
				System.out.println(fd.getValues());
			}
			
		}*/
	}

	private static HashMap<Integer,WeatherData> readData(String filename,boolean isX) {
		HashMap<Integer,WeatherData> data = new LinkedHashMap<Integer,WeatherData>();
		Scanner scanner = null;
		int key = 0;
		try {
			scanner = new Scanner(new File(filename));
		} catch (FileNotFoundException exception) {
			System.out.println("Error: File not found");
			System.exit(1);
		}
		String line;
		String firstLine = scanner.nextLine(); 
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			WeatherData newData = new WeatherData();
			newData.addFeatureNames(firstLine);
			if(isX)
			{
				newData.addFeaturesToXData(line);
			}
			else
			{
				newData.addFeaturesToYData(line);
			}			
			data.put(key, newData);
			key++;
			//data.add(newData);
		}
		return data;
	}
	
}
