package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import util.Feature;
import util.WeatherData;

public class DecisionTree {
	public static void main(String args[]) {
		HashSet<WeatherData> XtrainData = readData("weatherDataTrain.txt",true);
		HashSet<WeatherData> YtrainData = readData("weatherDataTrain.txt",false);
		HashSet<WeatherData> XtestData  = readData("weatherDataTest.txt",true);
		HashSet<WeatherData> YtestData  = readData("weatherDataTest.txt",false);
		
		InformationRatio ir = new InformationRatio();
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
		}
		
		/*for(WeatherData wd : YtrainData)
		{
			for(Feature fd: wd.getFeatures())
			{
				System.out.println(fd.getName());
				System.out.println(fd.getValues());
			}
			
		}*/
	}

	private static HashSet<WeatherData> readData(String filename,boolean isX) {
		HashSet<WeatherData> data = new HashSet<WeatherData>();
		Scanner scanner = null;
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
			data.add(newData);
		}
		return data;
	}
	
}
