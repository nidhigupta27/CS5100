package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import util.Feature;
import util.WeatherData;

public class InformationRatio 
{
	private HashMap<Integer,WeatherData> trainDataXMap = new HashMap<Integer,WeatherData>();
	private HashMap<Integer,WeatherData> trainDataYMap = new HashMap<Integer,WeatherData>();
    private HashMap<Integer,Double> gain_ratios_partitions = new HashMap<Integer,Double>();
    
	public  double calcInformationRatio(HashSet<WeatherData> trainDataX,HashSet<WeatherData> trainDataY,Feature attribute)
    {
    	int i=0;
    	double x=0.0,y=0.0;
    	int total_count_in_train = 0;
    	int count_rain_yes = 0;
    	int count_rain_no = 0;
    	boolean isRain = true;
    	double bestGainRatio = 0.0;
    	//Added an additional key to map trainDataX with trainDataY
    	for(WeatherData wd : trainDataX)
    	{
    		i++;
    		trainDataXMap.put(i, wd); 
    	}
    	//Added an additional key to map trainDataY with trainDataX
    	for(WeatherData wd : trainDataY)
    	{
    		i++;
    		trainDataYMap.put(i, wd);    		
    	}
    	total_count_in_train = trainDataX.size();
    	//Calculates the number of records in train data with EVENT=RAIN
    	count_rain_yes = getCountsOfRain(trainDataYMap,true,total_count_in_train);
    	
    	//Calculates the number of records in train data with EVENT!=RAIN		
        count_rain_no =  getCountsOfRain(trainDataYMap,false,total_count_in_train);
        
        //Probability that Rain occurred in train dataset
        x = count_rain_yes/total_count_in_train;
       //Probability that Rain did not occur in train dataset
        y = count_rain_no/total_count_in_train;
        
        //The information gain of the entire training set
    	double information_gain_System = getInformationGain(x,y);
    	
    	//loop thru all elements in trainDataXMap
    	for(int entry=0;i<trainDataXMap.size();entry++)
    	{
    		WeatherData wd = trainDataXMap.get(entry);
    		
    		for(Feature fd : wd.getFeatures())
    		{
    			if (fd.getName() == attribute.getName())
    			{
    				int partitonElem = (Integer) fd.getValues().get(0);
    				double gainRatio = calc_gain_ratio(attribute.getName(),partitonElem,trainDataXMap,trainDataYMap,information_gain_System,total_count_in_train);
    				gain_ratios_partitions.put(getKey(wd),gainRatio);
    			}
    		}   		
    	} 
    	//Returns the best gain ratio among  gain ratios computed in different partitions 
    	for(int k=0;k<gain_ratios_partitions.size();k++)
    	{
    		if(gain_ratios_partitions.get(k)>bestGainRatio)
    		{
    			bestGainRatio = gain_ratios_partitions.get(k);
    		}
    	}
    	return bestGainRatio;
    	//HashMap<Integer,WeatherData> trainDataXMap_sorted = sort_trainingData(trainDataXMap,attribute);
        	
    }
	//Helper method that returns the key given the vlaue in HashMap
	private Integer getKey(WeatherData value)
	{
	    for(Integer key : trainDataXMap.keySet())
	    {
	        if(trainDataXMap.get(key).equals(value))
	        {
	            return key; //return the first found
	        }
	    }
	    return null;
	}
	private double calc_gain_ratio(String attrb,int partitonElem,HashMap<Integer, WeatherData> trainDataXMap,HashMap<Integer, WeatherData> trainDataYMap,double information_gain_System,int total_count_in_train)
	{
		//Create XTrain and YTrain HashMaps for each partitions
		
		HashMap<Integer,Integer> X_Train_attrb_part1 = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> X_Train_attrb_part2 = new HashMap<Integer,Integer>();
		HashMap<Integer,WeatherData> Y_Train_attrb_part1 = new HashMap<Integer,WeatherData>();
		HashMap<Integer,WeatherData> Y_Train_attrb_part2 = new HashMap<Integer,WeatherData>();
		
		for(int i=0;i<trainDataXMap.size();i++)
		{
			WeatherData wd = trainDataXMap.get(i);
			
			for(Feature f: wd.getFeatures())
			{
				if(f.getName() == attrb)
				{
					int attrb_val = (Integer)f.getValues().get(0);
					int xTRainKey = getKey(wd);
					if(attrb_val <= partitonElem)
					{
						X_Train_attrb_part1.put(xTRainKey,attrb_val);
						Y_Train_attrb_part1.put(xTRainKey, trainDataYMap.get(xTRainKey));
					}
					else
					{
						X_Train_attrb_part2.put(xTRainKey,attrb_val);
						Y_Train_attrb_part2.put(xTRainKey, trainDataYMap.get(xTRainKey));
					}
				}
			}
		}
		//Size of first partition
		int Total_Count_attr_part1 = X_Train_attrb_part1.size();
		
		//Size of second partition
		int Total_Count_attr_part2 = X_Train_attrb_part2.size();
		
		//No of records in first partition where EVENT = RAIN 
		int count_rain_yes_part1 = getCountsOfRain(Y_Train_attrb_part1,true,Total_Count_attr_part1);
		
		//No of records in first partition where EVENT != RAIN 
		int count_rain_no_part1 = getCountsOfRain(Y_Train_attrb_part1,false,Total_Count_attr_part1);		
		
		//Compute the Information Gain in first partition
		double getInfoGain_part1 = getInformationGain((count_rain_yes_part1/Total_Count_attr_part1),
				                                      (count_rain_no_part1/Total_Count_attr_part1));
		//No of records in second partition where EVENT = RAIN 
        int count_rain_yes_part2 = getCountsOfRain(Y_Train_attrb_part2,true,Total_Count_attr_part2);
		
        //No of records in first partition where EVENT != RAIN
		int count_rain_no_part2 = getCountsOfRain(Y_Train_attrb_part2,false,Total_Count_attr_part2);	
		
		//Compute the Information Gain in second partition
		double getInfoGain_part2 = getInformationGain((count_rain_yes_part2/Total_Count_attr_part2),
				                                       (count_rain_no_part2/Total_Count_attr_part2));
		//Total Information from both partitions
		double infoGainFrom_Partition = getInfoGain_part1 + getInfoGain_part2;
		
		//Gain from the Partition
		double Gain_from_partition = information_gain_System - infoGainFrom_Partition;
		
		//Split information of partition
		double splitInfoFromPartition = getInformationGain((Total_Count_attr_part1/total_count_in_train),
                (Total_Count_attr_part2/total_count_in_train));
		
		//Gain ratio of Partition
		double gain_ratio_partition = Gain_from_partition/splitInfoFromPartition;
		return gain_ratio_partition;
	}
	
	private int getCountsOfRain(HashMap<Integer,WeatherData> trainYData,boolean isRain,int total_cnt)
	{
		int rainYesCount = 0;
		for(int i=0;i<trainYData.size();i++)
		{
			WeatherData wd = trainYData.get(i);
			ArrayList<Feature> frs = wd.getFeatures();
			List fr_vals = frs.get(0).getValues();
			for(int j=0;j<fr_vals.size();j++)
			{
				String fr_val = (String)fr_vals.get(i);
				if(fr_val == "Rain")
				{
					  rainYesCount++;				  
				}
				
			}			
		}
		if(isRain)
		{
			return rainYesCount;
		}
		else
		{
			return (total_cnt - rainYesCount);
		}
	}
	private double getInformationGain(double x,double y)
	{
		double log_x = log(x,2);
		double log_y = log(y,2);
		double info_gain = -((x*log_x)+(y*log_y));
		return info_gain;
	}
	private double log(double x, int base)
	{
	    return (Math.log(x) / Math.log(base));
	}
	/*private HashMap<Integer, WeatherData> sort_trainingData(
			HashMap<Integer, WeatherData> trainDataXMap, Feature attrb) {
		Set<Entry<Integer, WeatherData>> set = trainDataXMap.entrySet();
		List<Entry<Integer, WeatherData>> list = new ArrayList<Entry<Integer, WeatherData>>(
				set);
		Collections.sort(list,
				new Comparator<Map.Entry<Integer, WeatherData>>() {
					public Integer compare(Map.Entry<Integer, WeatherData> o1,
							Map.Entry<Integer, WeatherData> o2) {
						for (Feature wd : o1.getValue().getFeatures()) {
							if (wd.getName() == "Mean TemperatureF") {
								ArrayList<Feature> o2Features = o2.getValue()
										.getFeatures();
								for (Feature f : o2Features) {
									if (f.getName() == "Mean TemperatureF") {
										Integer o1Val = (Integer) f.getValues()
												.get(0);
										Integer o2Val = (Integer) wd
												.getValues().get(0);

										return (o1Val.compareTo(o2Val));
									}
								}

							}
						}

					}
				});
		return list;
	}*/
}
