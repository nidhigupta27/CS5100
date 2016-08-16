package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import util.Feature;
import util.Node;
import util.WeatherData;

public class PostTreePruning 
{
   private Node root;
   private HashMap<Integer,WeatherData> XDataValidTest = new LinkedHashMap<Integer,WeatherData>();
   private HashMap<Integer,WeatherData> YDataValidTest = new LinkedHashMap<Integer,WeatherData>();
   private HashMap<Integer,WeatherData> XDataValidTestOrig = new LinkedHashMap<Integer,WeatherData>();
   private HashMap<Integer,WeatherData> YDataValidTestOrig = new LinkedHashMap<Integer,WeatherData>();
   private String target_label;
   
   public PostTreePruning(Node node,HashMap<Integer,WeatherData> XDataValidTest,HashMap<Integer,WeatherData> YDataValidTestOrig,HashMap<Integer,WeatherData> XDataValidTestOrig,HashMap<Integer,String> result,String t)
   {
	   this.XDataValidTest = XDataValidTest;
	   this.YDataValidTest = YDataValidTest;
	   this.XDataValidTestOrig = XDataValidTestOrig;
	   this.YDataValidTestOrig = YDataValidTestOrig;
	   this.target_label = t;
   }
   public Node execute(Node node,HashMap<Integer,WeatherData> XDataValidTest)
   {
	   if((node==null) || (XDataValidTest.size() == 0))
	   {
		   return null;
	   }
	   if(node.getType().equals("leaf"))
	   {
		   return node;
	   }
	   for ( String c : node.getChildren().keySet())
	   {
		  Node childNode = node.getChildren().get(c);
		  
		  HashMap<Integer, WeatherData> dataAtNode =new HashMap<Integer,WeatherData>();
		  
		  for(Map.Entry<Integer, WeatherData> key_weatherData : XDataValidTest.entrySet())
		  {
			  HashMap<Integer, WeatherData> curKeyWeatherData =new HashMap<Integer,WeatherData>();
			  
			  curKeyWeatherData.put(key_weatherData.getKey(), key_weatherData.getValue());
			  
			  String featureName = node.getAttribute().getName();
			  
			  double threshold = Double.parseDouble(c.substring(4));

			  WeatherData wd = key_weatherData.getValue();
			  
			  ArrayList<Feature> features  = wd.getFeatures();
			  
			  //Double f_value = 0.0;
			  
			  for(Feature f : features)
			  {
				  if(f.getName()==featureName)
				  {
					  Double f_value = Double.parseDouble((String)f.getValues().get(0));
				  
			          String part_string = c.substring(0, 4);
			          if((part_string == "less" && f_value<threshold) || (part_string=="more" && f_value>threshold))
			           {
				                dataAtNode.put(key_weatherData.getKey(), key_weatherData.getValue());			  
			           }
				  }
		      }			  
			  
		  }
		  //Recursion happens here
		  Node newChildNode = execute(childNode,dataAtNode);
		  if(newChildNode !=null)
		  {
			  node.getChildren().put(c, newChildNode);
		  }
	   }
	   if(node.getChildren().size()!=0)
	   {
		   HashMap<String,Node> children = node.getChildren();
		   for(String c : node.getChildren().keySet())
		   {
			   if(!children.get(c).getType().equals("leaf"))
			   {
				   return node;
			   }
		   }
	   }
	   HashMap<String,Integer> result = new HashMap<String,Integer>();
	   for(Map.Entry<Integer, WeatherData> key_weatherData : XDataValidTest.entrySet())
	   {
		   Integer key = key_weatherData.getKey();
		   WeatherData wd = YDataValidTest.get(key);
		   ArrayList<Feature> features_in_Y = wd.getFeatures();
		   for(Feature f : features_in_Y)
		   {
			   if(f.getValues().get(0) == target_label)
			   {
				   if(result.containsKey(target_label))
				   {
					   result.put(target_label,(result.get(target_label)+1));
				   }
				   else
				   {
					   result.put(target_label,1);
				   }
			   }
		   }
		   
	   }
	   result.put("not_target", (XDataValidTest.size()-result.get(target_label)));
	   
	   int score_with_prune = 0;
	   String prunedTreeLabel = "";
	   for(String r: result.keySet())
	   {
		   score_with_prune = Math.max(score_with_prune, result.get(r));
		   prunedTreeLabel = r;
	   }
	   
	   int scoreFromModel = 0;
	   for(Map.Entry<Integer, WeatherData> key_weatherData : XDataValidTest.entrySet())
	   {
		   int key_in_data = key_weatherData.getKey();
		   
		   for(Map.Entry<Integer, WeatherData> key_weatherData_orig : XDataValidTestOrig.entrySet())
		   {
			   if(key_weatherData_orig.getKey()==key_in_data)
			   {
				   WeatherData wd = YDataValidTestOrig.get(key_in_data);
				   ArrayList<Feature> features = wd.getFeatures();
				   for(Feature f : features)
				   {
					   if(f.getValues().contains(target_label) && result.get(key_in_data).equals(target_label))
					   {
						   scoreFromModel++;
					   }
				   }
			   }			  
			   
		   }
	   }
	   if(scoreFromModel > score_with_prune)
	   {
		   return node;
	   }
	   else
	   {
		   node.setType("leaf");
		   node.getChildren().clear();
		   node.setleafLabel(target_label);
		   return node;
	   }
   }
}
