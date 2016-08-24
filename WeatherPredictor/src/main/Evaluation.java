package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import util.WeatherData;

// This class is used for evaluating test data
// This is done by comparing actual classification with predicted output
public class Evaluation {
	HashMap<Integer, WeatherData> YtestDataMap = null;
	ArrayList<String> targets = null;
	HashMap<Integer,String> comp = null;

	public Evaluation(HashMap<Integer, WeatherData> YtestDataMap,ArrayList<String> targets,HashMap<Integer,String> comp){
		this.YtestDataMap = YtestDataMap;
		this.targets = targets;
		this.comp = comp;
	}

	// computeAccuracy() compares the actual and expected class for each target class label and 
	// computes accuracy.
	public double computeAccuracy() {
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
		int count_in_not_null = 0;
		int count_in_null = 0;
		for(int i=0;i<actualResult.size();i++)
		{
			for(String target: targets)
			{
				if((comp.get(i)!=null) && (!actualResult.get(i).equals("")))
				{
					count_in_not_null++;
					String[] actualResults = actualResult.get(i).split("-");
					String[] modelResults =  comp.get(i).split("-");
					// true positives
					if(Arrays.asList(actualResults).contains(target) && Arrays.asList(modelResults).contains(target))
					{
						correct_prediction ++;
					}
					else if(!Arrays.asList(actualResults).contains(target) && !Arrays.asList(modelResults).contains(target))
					{
						correct_prediction ++;
					}				
				}
				if((comp.get(i)==null) || (actualResult.get(i).equals("")))
				{
					count_in_null++;
					if(actualResult.get(i).equals("")&&comp.get(i)==null) 
					{
						correct_prediction ++;
					}
					if(comp.get(i)==null && !(actualResult.get(i).equals("")))
					{
						String[] actualResults = actualResult.get(i).split("-");
						if(!Arrays.asList(actualResults).contains(target))
						{
							correct_prediction ++;
						}
					}
					if(comp.get(i)!=null && (actualResult.get(i).equals("")))
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
		return ((double)correct_prediction/(count_in_not_null+count_in_null));
	}
}