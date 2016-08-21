package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
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

	public static void main(String args[]) throws Exception {
		// read data
		// add all predictions to targets ArrayList (Rain,Snow,Fog,Thunderstorm)
		updateTarget();

		ArrayList<Feature> features = new ArrayList<Feature>();
		HashMap<Integer,WeatherData> XtrainDataMap = new LinkedHashMap<Integer,WeatherData>();
		HashMap<Integer,WeatherData> YtrainDataMap= new LinkedHashMap<Integer,WeatherData>();
		HashMap<Integer,WeatherData> XtestDataMap = new LinkedHashMap<Integer,WeatherData>();
		HashMap<Integer,WeatherData> YtestDataMap = new LinkedHashMap<Integer,WeatherData>();
        //Train/test
		XtrainDataMap = readData("weatherDataTrain2",true);
		YtrainDataMap = readData("weatherDataTrain2",false);
		XtestDataMap = readData("weatherDataTest",true);
		YtestDataMap = readData("weatherDataTest",false);
	
		for(Map.Entry<Integer, WeatherData> weatherData : XtrainDataMap.entrySet())
		{
			WeatherData wd = weatherData.getValue();
			ArrayList<Feature> allFeatures = wd.getFeatures();
			for(Feature f : allFeatures)
			{						
				if(!f.getName().equals("EST"))
				{
					//System.out.println(f.getName());
					features.add(f);
				}
			}
			break;
		}
		ArrayList<String> resultCompare = new ArrayList<String>();
		HashMap<Integer,String> comp = new HashMap<Integer,String>();
		for (String target : targets) {
			try {
				int i = 0;
				GrowTree tree = new GrowTree(features, target, XtrainDataMap,
						YtrainDataMap, XtestDataMap, YtestDataMap);
				System.out.println("calling tree construct");
				Node root = tree.construct();
				System.out.println("tree created successfully");

				ValidateWithPruning vwp = new ValidateWithPruning(root,
						features, target, XtrainDataMap,YtrainDataMap,XtestDataMap, YtestDataMap);
				HashMap<Integer, String> res = vwp.validateAfterPrune();
				ArrayList<String> forCompare = new ArrayList<String>();
				// System.out.println("result size "+res.size());
				for (Map.Entry<Integer, String> result : res.entrySet()) {
					String wd = result.getValue();
					if (comp.containsKey(i)) {
						if (!wd.equals("")) {
							String s = comp.get(i);
							s += "-" + wd;
							comp.put(i, s);
						}
					} else {
						if (!wd.equals(""))
							comp.put(i, wd);
					}
					i++;
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
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
		int correct_prediction=0;
		int fogCount= 0;
		
		ArrayList<String> all_targets = new ArrayList<String>();
		all_targets.add("Rain");
		all_targets.add("Snow");
		all_targets.add("Thunderstorm");
		all_targets.add("Fog");
		int count_in_not_null = 0;
		int count_in_null = 0;
		for(int i=0;i<actualResult.size();i++)
		{
			System.out.println("actualResults"+ actualResult.get(i)+"\t"+"modelResults"+ comp.get(i));
			//System.out.println("modelResults"+ comp.get(i));
			for(String target: all_targets)
			{
				if((comp.get(i)!=null) && (!actualResult.get(i).contains("Normal")))
				{
					count_in_not_null++;
					//System.out.println("the value of actualResults"+ actualResult.get(i));
					//System.out.println("the value of modelResults"+ comp.get(i));
					String[] actualResults = actualResult.get(i).split("-");
					String[] modelResults =  comp.get(i).split("-");
					if(Arrays.asList(actualResults).contains(target) && Arrays.asList(modelResults).contains(target))
					{
						//System.out.println("Both the model and actual have target");
						correct_prediction ++;
					}
					else if(!Arrays.asList(actualResults).contains(target) && !Arrays.asList(modelResults).contains(target))
					{
						//System.out.println("Both the model and actual not have target");
						correct_prediction ++;
					}
				
				}
				if((comp.get(i)==null) || (actualResult.get(i).contains("Normal")))
				{
					count_in_null++;
					if(actualResult.get(i).contains("Normal")&&comp.get(i)==null) 
					{
							
						//System.out.println("Bz of normal and null");
						correct_prediction ++;
					}
					if(comp.get(i)==null && !(actualResult.get(i).contains("Normal")))
					{
						String[] actualResults = actualResult.get(i).split("-");
						if(!Arrays.asList(actualResults).contains(target))
								{
							     correct_prediction ++;
								}
						
					}
					if(comp.get(i)!=null && (actualResult.get(i).contains("Normal")))
					{
						String[] modelResults = comp.get(i).split("-");
						if(!Arrays.asList(modelResults).contains(target))
						  {
							     correct_prediction ++;
					      }
						
					}
					
				}
				
			}
					
		}
								
		System.out.println("Accuarcy "+(double)correct_prediction/(count_in_not_null+count_in_null));
		//System.out.println("Accuracy for Thunderstorm "+(double)(correct_prediction_fog/fogCount));
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
		//targets.add("Normal");
	}
}