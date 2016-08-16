package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Feature;
import util.WeatherData;

public class MissingValues 
{
	public HashMap<Integer, WeatherData> resolveMissingValue(HashMap<Integer, WeatherData> XDataMap,HashMap<Integer, WeatherData> YDataMap)
	{
			for(Map.Entry<Integer, WeatherData> keyWeatherData : XDataMap.entrySet())
			{
				WeatherData wd = keyWeatherData.getValue();
				int keyInData = keyWeatherData.getKey();
				ArrayList<Feature> featureList = wd.getFeatures();
				for(Feature f : featureList)
				{
				     String NATest = (String) f.getValues().get(0);
				     if(NATest.equals("NA"))
				     {
				    	 WeatherData wdata = YDataMap.get(keyInData);
				    	 ArrayList<Feature> yFeatureList = wdata.getFeatures();
				    	 for(Feature yf : yFeatureList)
							{
				    		 String label = (String)yf.getValues().get(0);
				    		 List<String> newFVal = getFeatureVal(label,f.getName(),XDataMap,YDataMap);
				    		 f.setValues(newFVal);
							}
				     }
				}
			}
			
			return XDataMap;
	}
	
	private List<String> getFeatureVal(String label,String fName,HashMap<Integer, WeatherData> XDataMap,HashMap<Integer, WeatherData> YDataMap)
	{
		List<String> notNAFValues = null;
		for(Map.Entry<Integer, WeatherData> keyWeatherData : XDataMap.entrySet())
		{
			WeatherData wd = keyWeatherData.getValue();
			int keyInData = keyWeatherData.getKey();
			ArrayList<Feature> featureList = wd.getFeatures();
			for(Feature f : featureList)
			{
			     String notAnNA = (String) f.getValues().get(0);
			     if(f.getName().equals(fName)&& (!notAnNA.equals("NA")))
			     {
			    	 WeatherData wdata = YDataMap.get(keyInData);
			    	 ArrayList<Feature> yFeatureList = wdata.getFeatures();
			    	 for(Feature yf : yFeatureList)
						{
			    		   if(yf.getValues().contains(label))
			    		    {
			    			 notNAFValues.add(notAnNA);
					    	 break;
			    		    }
						}
			    	 
			    	 
			     }
			}
		}
		return notNAFValues;
	}

}
