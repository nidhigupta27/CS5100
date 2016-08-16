package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import util.Feature;
import util.Node;
import util.WeatherData;

public class DecisionTree {
	static ArrayList<String> targets = new ArrayList<String>();

	public static void main(String args[]) {
		// read data
		// add all predictions to targets ArrayList (Rain,Snow,Fog,Thunderstorm)
		updateTarget();

		ArrayList<Feature> features = new ArrayList<Feature>();
		HashMap<Integer,WeatherData> XtrainDataMap = new LinkedHashMap<Integer,WeatherData>();
		HashMap<Integer,WeatherData> YtrainDataMap= new LinkedHashMap<Integer,WeatherData>();
		HashMap<Integer,WeatherData> XtestDataMap = new LinkedHashMap<Integer,WeatherData>();
		HashMap<Integer,WeatherData> YtestDataMap = new LinkedHashMap<Integer,WeatherData>();

		XtrainDataMap = readData("weatherDataTrain.txt",true);
		YtrainDataMap = readData("weatherDataTrain.txt",false);
		XtestDataMap = readData("weatherDataTest.txt",true);
		YtestDataMap = readData("weatherDataTest.txt",false);

		for(Map.Entry<Integer, WeatherData> key_weatherData : XtrainDataMap.entrySet())
		{
			WeatherData wd = key_weatherData.getValue();
			ArrayList<Feature> allFeatures = wd.getFeatures();
			for(Feature f : allFeatures)
			{
				if((f.getName().contains("EST")) ){
					continue;
				}else{
					features.add(f);
				}					
			}
			break;
		}
		ArrayList<String> resultCompare = new ArrayList<String>();
		//for(String target: targets){
		    String target="Fog";
			//Validate v = new Validate(features, target, XtrainDataMap, YtrainDataMap, XtestDataMap,
			//		YtestDataMap);
			
			//HashMap<Integer,String> res = v.validate();
			ValidateWithPruning vwp = new ValidateWithPruning(features, target, XtrainDataMap, YtrainDataMap, XtestDataMap,
					YtestDataMap);
			HashMap<Integer,String> res = vwp.validateAfterPrune();
			ArrayList<String> forCompare = new ArrayList<String>();
			//System.out.println("result size "+res.size());
			for(Map.Entry<Integer, String> result : res.entrySet()) {
				String wd = result.getValue();
			 
				resultCompare.add(wd);
			// }
				
			}
			
			
		//}
		ArrayList<String> actualResult = new ArrayList<String>();
		for(Map.Entry<Integer, WeatherData> result : YtestDataMap.entrySet()) {
			WeatherData wd = result.getValue();
			ArrayList x =(wd.featureValue("Events")); 
			String p="";
			for(int i=0;i<x.size();i++) {
				if(!p.equals("")) {
				p +="-"+x.get(i);
				}
				else {
					p+=x.get(i);
				}
			}
			actualResult.add(p);
		}
		int correct_prediction_fog=0;
		int fogCount= 0;
		for(int i=0;i<actualResult.size();i++) {
			System.out.println("actual: "+actualResult.get(i));
			System.out.println("predicted: "+resultCompare.get(i));
			if(actualResult.get(i).contains("Fog")&&(resultCompare.contains("Fog"))) {
				correct_prediction_fog ++;
			}
			if(actualResult.get(i).contains("Fog")) {
				fogCount++;
			}
		}
		System.out.println("Accuracy for Fog "+(double)(correct_prediction_fog/fogCount));
	}

	private static HashMap<Integer,WeatherData> readData(String filename,boolean isX) {
		HashMap<Integer,WeatherData> data = new LinkedHashMap<Integer,WeatherData>();
		Scanner scanner = null;
		int key=0;
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
		}
		return data;
	}	

	public static void updateTarget() {
		targets.add("Fog");
		targets.add("Rain");
		targets.add("Snow");
		targets.add("Thunderstorm");
		targets.add("Normal");
	}
}