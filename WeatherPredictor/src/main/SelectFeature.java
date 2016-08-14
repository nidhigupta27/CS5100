package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import util.Feature;
import util.WeatherData;

public class SelectFeature 
{
   private Feature feature;
   private HashMap<Integer,WeatherData>xTrainLeftPart;
   private HashMap<Integer,WeatherData>xTrainRightPart;
   private HashMap<Integer,WeatherData>yTrainLeftPart;
   private HashMap<Integer,WeatherData>yTrainRightPart;
   private double gainRatio;
   private double maxGainRatioFeatureValue;
   
   public SelectFeature(String target_val,ArrayList<Feature> features,HashMap<Integer,WeatherData> XDataInTrain,
		   HashMap<Integer,WeatherData> YDataInTrain) throws IOException
		   {
	             feature = null;
	             gainRatio = Double.MIN_VALUE;
	             System.out.println("the min double"+gainRatio);
	             xTrainLeftPart = null;
	             xTrainRightPart = null;
	             yTrainLeftPart = null;
	             yTrainRightPart = null;
	             
	             //Loop through the feature list to find the feature with maximum Gain Ratio
	             for(Feature ftr : features)
	             {
	            	 double currentGainRatio = Double.MIN_VALUE;
	            	 HashMap<Integer,WeatherData> xTrainLeftPartCurrent = null;
	            	 HashMap<Integer,WeatherData> xTrainRightPartCurrent = null;
	            	 HashMap<Integer,WeatherData> yTrainLeftPartCurrent = null;
	            	 HashMap<Integer,WeatherData> yTrainRightPartCurrent = null;
	            	 GainRatio gr = new GainRatio(XDataInTrain,YDataInTrain,ftr,target_val);
	            	 currentGainRatio = gr.getGainRatio();
	            	 xTrainLeftPartCurrent = gr.getXTrainLeftPart();
	            	 xTrainRightPartCurrent = gr.getXTrainRightPart();
	            	 yTrainLeftPartCurrent = gr.getYTrainLeftPart();
	            	 yTrainRightPartCurrent = gr.getYTrainRightPart();
	            	 maxGainRatioFeatureValue = gr.getMaxGainRatioFeatureValue();
	             
	                if(currentGainRatio > gainRatio)
	                 {
		            	 gainRatio = currentGainRatio;
		            	 xTrainLeftPart = xTrainLeftPartCurrent;
		            	 xTrainRightPart = xTrainRightPartCurrent;
		            	 yTrainLeftPart = yTrainLeftPartCurrent;
		            	 yTrainRightPart = yTrainRightPartCurrent;
		            	 feature = ftr;
	                 }
	             }
	             
		   }
   public Feature getFeature()
   {
   	return feature;
   }
   public double getMaxGainRatioFeatureValue()
   {
 	  return maxGainRatioFeatureValue; 
   }
   public double getGainRatio()
   {
 	  return gainRatio;
   }
   public HashMap<Integer,WeatherData> getXTrainLeftPart()
   {
 	  return xTrainLeftPart;
   }
   public HashMap<Integer,WeatherData> getXTrainRightPart()
   {
 	  return xTrainRightPart;
   }
   public HashMap<Integer,WeatherData> getYTrainLeftPart()
   {
 	  return yTrainLeftPart;
   }
   public HashMap<Integer,WeatherData> getYTrainRightPart()
   {
 	  return yTrainRightPart;
   }
   
}