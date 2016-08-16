package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import util.Feature;
import util.WeatherData;

public class GainRatio 
{
	private HashMap<String,HashMap<Integer,WeatherData>> ysubset;
	private HashMap<String,HashMap<Integer,WeatherData>> xsubset;
  private Feature feature2consider;
  private double partitionPoint;
  private double gainRatio = roundoff(Double.MIN_VALUE);
  private double maxGainRatioFeatureValue;
  private HashMap<Integer,WeatherData> XDataInTrain = new LinkedHashMap<Integer,WeatherData>();
  private HashMap<Integer,WeatherData>xTrainLeftPart;
  private HashMap<Integer,WeatherData>xTrainRightPart;
  private HashMap<Integer,WeatherData>yTrainLeftPart;
  private HashMap<Integer,WeatherData>yTrainRightPart;
  private HashMap<Integer,WeatherData> YDataInTrain = new LinkedHashMap<Integer,WeatherData>();
  private ArrayList<Integer> keys = new ArrayList<Integer>();
  public GainRatio(HashMap<Integer, WeatherData> trainDataX, HashMap<Integer, WeatherData> trainDataY, Feature attribute,
			String target_val)throws IOException {
	
		  this.feature2consider = attribute;
		  this.XDataInTrain = trainDataX;
		  this.YDataInTrain = trainDataY;
		  HashMap<Integer,Double> XDataTrainAttrbMap = new LinkedHashMap<Integer,Double>();
		  XDataTrainAttrbMap = getHashByAttrib(XDataInTrain,attribute);
		  XDataTrainAttrbMap = sortByValue(XDataTrainAttrbMap);
		  this.XDataInTrain = getSortedTrain(XDataTrainAttrbMap,this.XDataInTrain);
		  this.YDataInTrain = getSortedTrain(XDataTrainAttrbMap,this.YDataInTrain);
		  //Testing purpose
		  /*for(Map.Entry<Integer, WeatherData> key_weatherData : XDataInTrain.entrySet())
		  {
			  WeatherData wd = key_weatherData.getValue();
			  ArrayList<Feature> fet = wd.getFeatures();
			  for(Feature f : fet)
			  {
				  if(f.getName().equals(feature2consider.getName()))
				  {
					  Double fval = Double.parseDouble((String)f.getValues().get(0));
					  System.out.println("the sorted order is"+f.getName()+" "+fval);
				  }
			  }
		  }*/
		  
		  int partitionPos = 0;
		  double information_gain_System = roundoff(getInformationGainSystem(trainDataY,target_val));
		  //System.out.println("the inf gain of system"+information_gain_System);
		  // Make an arraylist of keys
		  for(Map.Entry<Integer, Double> key_weatherData : XDataTrainAttrbMap.entrySet())
		  {
			  keys.add(key_weatherData.getKey()); 
		  }  
	  
	  
		  for(int i=0;i<keys.size()-1;i++)
		  {  
			  WeatherData wd1  =   XDataInTrain.get(keys.get(i));
			  WeatherData wd2  =   XDataInTrain.get(keys.get(i+1));
			  ArrayList<Feature> featureData1 = wd1.getFeatures();
			  ArrayList<Feature> featureData2 = wd2.getFeatures();
			  Double feature_Val1=0.0;
			  Double feature_Val2=0.0;
			  for(Feature f : featureData1)
			  {
				  if(f.getName().equals(feature2consider.getName()))
					{
						feature_Val1 = Double.parseDouble((String)f.getValues().get(0));
					}
			  }
			  for(Feature f : featureData2)
			  {
				  if(f.getName().equals(feature2consider.getName()))
					{
						feature_Val2 = Double.parseDouble((String)f.getValues().get(0));
					}
			  }
			  if(feature_Val1 != feature_Val2)
			  {
				  //System.out.println("I am here");
				  double currGainRatio = roundoff(calculateGainRatio(information_gain_System,feature2consider, target_val, XDataInTrain,YDataInTrain,i));
				 // System.out.println("the new gain ratio"+currGainRatio);
				  if (currGainRatio > gainRatio ) 
				  {
					 // System.out.println("the new gain ratio"+currGainRatio);
					    gainRatio = currGainRatio;
					    partitionPos = i;
				  }
			  }
			  
		  }
		 //Calculate Partition Value for the given feature to consider and given target value
		  int keyAtPartitionPos = keys.get(partitionPos);		  
		  WeatherData wd = XDataInTrain.get(keyAtPartitionPos);
          ArrayList<Feature> fts = wd.getFeatures();
          for(Feature f : fts)
          {
        	  if(f.getName().equals(feature2consider.getName()))
        	  {
        		  maxGainRatioFeatureValue = Double.parseDouble((String)(f.getValues().get(0)));
        	  }
          }
	      
       // Initialize the left and right partitions
  		xTrainLeftPart = new LinkedHashMap<Integer, WeatherData>();
  		xTrainRightPart = new LinkedHashMap<Integer, WeatherData>();
  		yTrainLeftPart = new LinkedHashMap<Integer, WeatherData>();
  		yTrainRightPart = new LinkedHashMap<Integer, WeatherData>();
  		
  		for(int i=0;i<=partitionPos;i++)
  		{
  			xTrainLeftPart.put(keys.get(i), XDataInTrain.get(keys.get(i)));
  			yTrainLeftPart.put(keys.get(i), YDataInTrain.get(keys.get(i)));
  		}
  		
  		for(int i=partitionPos+1;i<XDataInTrain.size();i++)
  		{
  			xTrainRightPart.put(keys.get(i), XDataInTrain.get(keys.get(i)));
  			yTrainRightPart.put(keys.get(i), YDataInTrain.get(keys.get(i)));
  		}
  		
  		ysubset = new HashMap<String,HashMap<Integer,WeatherData>>();
		String leftName = "less" + partitionPos;
		String rightName = "more" + partitionPos;
		ysubset.put(leftName, this.getYTrainLeftPart());
		ysubset.put(rightName, this.getYTrainRightPart());
		xsubset = new HashMap<String,HashMap<Integer,WeatherData>>();
		String xleftName = "less" + partitionPos;
		String xrightName = "more" + partitionPos;
		xsubset.put(leftName, this.getXTrainLeftPart());
		xsubset.put(rightName, this.getXTrainRightPart());
          		
		   
  }
  
public double calculateGainRatio(double information_gain_System,Feature feature2consider, String target_val, HashMap<Integer,WeatherData>XDataInTrain,
		  HashMap<Integer,WeatherData> YDataInTrain,int i)
		  {
	        double calGainRatio = 0.0;
	        int lPart = i+1;
	        int rPart = YDataInTrain.size()-i-1;
	        double gainLPart = calcGain(target_val,lPart,YDataInTrain,0);
	        //System.out.println("the gain from left part"+gainLPart);
	        double gainRPart = calcGain(target_val,rPart,YDataInTrain,i+1);
	        //System.out.println("the gain from right part"+gainRPart);
	        double gainFromPart  =  information_gain_System -(gainLPart+gainRPart);
	         //Split information of partition
	        //Test
	        if((lPart==0)||(rPart==0))
	        {
	        	System.out.println("This cannot be the case!");
	        }
			if((lPart!=0) && (rPart!=0))
			{		
				double splitInfoFromPartition = getInformationGain(((double)lPart/XDataInTrain.size()),
	                                                                 ((double)rPart/XDataInTrain.size()));
				
				//System.out.println("The split info from partition"+splitInfoFromPartition);
	            
				//Gain ratio of Partition
				calGainRatio = roundoff(gainFromPart/splitInfoFromPartition);
			}
			//System.out.println("The gain ratio info from partition"+gain_ratio_partition);
	        return calGainRatio;
	        
		  }
          public double calcGain(String target_val,int part_size,HashMap<Integer,WeatherData> YDataInTrain,int startIndex)
		  {
	            double infGain = 0.0;
	            int count_target_yes_part = 0;
	            int count_target_no_part = 0;	
	            double prob_yes_target_part = 0.0;
	    		double prob_no_target_part = 0.0;
	            HashMap<Integer,WeatherData> yTrainInPartition = new LinkedHashMap<Integer,WeatherData>();
	            for(int i=startIndex;i<part_size;i++)
	            {
	            	yTrainInPartition.put(keys.get(i), YDataInTrain.get(keys.get(i)));
	            }
	            count_target_yes_part = getCountsOfTarget(yTrainInPartition,target_val);  
	            count_target_no_part = part_size - count_target_yes_part;
	            if(part_size!=0){
	            	prob_yes_target_part = (count_target_yes_part/(double)part_size);
	            	prob_no_target_part = (count_target_no_part/(double)part_size);			
	    		}
	    		if((prob_yes_target_part!=0.0) && (prob_no_target_part!=0.0))
	    		{
	    			infGain = getInformationGain(prob_yes_target_part,prob_no_target_part);
	    		}	
	            return infGain;
		  }
         public double getInformationGainSystem(HashMap<Integer,WeatherData> trainDataY,String target_val)
          {
			  double systemInfGain = 0.0;
			  int total_count_in_train = trainDataY.size();
			  int count_target_yes = 0;
			  int count_target_no = 0;
			  double x = 0.0;
			  double y = 0.0;
		      //Calculates the number of records in train data with EVENT=RAIN
		       count_target_yes = getCountsOfTarget(trainDataY,target_val);
		    	//System.out.println("Number of entries with Rain yes"+count_target_yes);
		    	//Calculates the number of records in train data with EVENT!=RAIN		
		       count_target_no =  total_count_in_train - count_target_yes;
		        //System.out.println("Number of entries with Rain no"+count_target_no);
		        
		        //Probability that target occurred in train dataset
		       x = (double)count_target_yes/total_count_in_train;
		        //System.out.println("prob of  Rain in train"+x);
		       
		       //Probability that target did not occur in train dataset
		       y = (double)count_target_no/total_count_in_train;
		       //System.out.println("prob of  not Rain in train"+x);
		        
		       //The information gain of the entire training set
		      systemInfGain = roundoff(getInformationGain(x,y));
		     
		      return systemInfGain;
  }
         public HashMap<String,HashMap<Integer,WeatherData>> getYSubset() {
     		return ysubset;
     	}

     	public HashMap<String,HashMap<Integer,WeatherData>> getXSubset() {
     		return xsubset;
     	}
  public Feature getFeature()
  {
  	return feature2consider;
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
  private int getCountsOfTarget(HashMap<Integer,WeatherData> trainYData,String target)
	{
		int rainYesCount = 0;
		for (Map.Entry<Integer, WeatherData> key_weatherData : trainYData.entrySet()) 
		{
			WeatherData wd = (WeatherData) key_weatherData.getValue();
		
			ArrayList<Feature> frs = wd.getFeatures();
			
			List fr_vals = frs.get(0).getValues();
			for(int j=0;j<fr_vals.size();j++)
			{
				String fr_val = fr_vals.get(j).toString();
				if(fr_val.contains(target))
				{
					  rainYesCount++;				  
				}				
			}			
		}
        return rainYesCount;		
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
  private HashMap<Integer,WeatherData> getSortedTrain(HashMap<Integer,Double>XDataTrainAttrbMap,HashMap<Integer,WeatherData> dataInTrain)
  {
	  HashMap<Integer,WeatherData> trainData = new LinkedHashMap<Integer,WeatherData>(); 
	  for(Map.Entry<Integer, Double> key_FeatureVal : XDataTrainAttrbMap.entrySet())
	  {
		  WeatherData wd = dataInTrain.get(key_FeatureVal.getKey());
		  trainData.put(key_FeatureVal.getKey(), wd);
		  
	  }
	  return trainData;
  }
  private HashMap<Integer,Double> getHashByAttrib(HashMap<Integer,WeatherData> XDataInTrain,Feature attrb)
  {
	  HashMap<Integer,Double> XDataTrainAttrbMap = new HashMap<Integer,Double>();
	  for(Map.Entry<Integer, WeatherData> key_weatherData : XDataInTrain.entrySet())
	  {		  
		 WeatherData wd =  key_weatherData.getValue();
		 ArrayList<Feature> features = wd.getFeatures();
		 for(Feature f : features)
		 {
			 if(f.getName().contains(attrb.getName()))
			 {
				 
				 //System.out.println("The feature val"+(String)f.getValues().get(0));
				 //System.out.println("The feature name"+f.getName());
				 Double feature_val = Double.parseDouble((String)f.getValues().get(0));
				 XDataTrainAttrbMap.put(key_weatherData.getKey(),feature_val);
			 }
		 }
	  }
	  return XDataTrainAttrbMap;
	  
  }
  public static <K, V extends Comparable<? super V>> HashMap<K, V> sortByValue( HashMap<K, V> map )
  {
      List<Map.Entry<K, V>> list =
          new LinkedList<Map.Entry<K, V>>( map.entrySet() );
      Collections.sort( list, new Comparator<Map.Entry<K, V>>()
      {
          public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
          {
              return (o1.getValue()).compareTo( o2.getValue() );
          }
      } );

      HashMap<K, V> result = new LinkedHashMap<K, V>();
      for (HashMap.Entry<K, V> entry : list)
      {
          result.put( entry.getKey(), entry.getValue() );
      }
      return result;
  }
  private double roundoff(double num_2_round)
  {
		return (double)Math.round(num_2_round * 100000d) / 100000d;
   }
}