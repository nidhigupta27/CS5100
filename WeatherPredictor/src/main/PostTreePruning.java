package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import util.Feature;
import util.Node;
import util.WeatherData;

public class PostTreePruning {
	private Node root;
	private HashMap<Integer, WeatherData> XDataValidTest = new LinkedHashMap<Integer, WeatherData>();
	private HashMap<Integer, WeatherData> YDataValidTest = new LinkedHashMap<Integer, WeatherData>();
	private String target_label;
	private HashMap<Integer, String> result_model = new HashMap<Integer, String>();

	//Constructor of class PostTreePruning. It loads the private fields of class with
	//initial data
	public PostTreePruning(HashMap<Integer, WeatherData> XDataValidTest,
			HashMap<Integer, WeatherData> YDataValidTestOrig,
			HashMap<Integer, WeatherData> XDataValidTestOrig,
			HashMap<Integer, String> result, String t) 
	   {
		    this.XDataValidTest = XDataValidTest;
		    this.YDataValidTest = YDataValidTestOrig;
		    this.target_label = t;
		    this.result_model = result;

	   }
   //Recursive method which traverses through the decision tree.
   //The initial parameters are -  the root as the initial node of tree and entire validation 
   //test data set
   //Recursion stops when 
   //1.a leaf node is encountered or 
   //2.when the size of test data set is zero
   //3.when the node in recursive method is null
	
	
	public Node execute(Node node, HashMap<Integer, WeatherData> XDataValidTest) {
	// Return node value when node type is leaf
		if (node.getType().equals("leaf")) 
		{
			return node;
		}
	//Return null when either node is null or test data size EQUALS zero 
		if ((node == null) || (XDataValidTest.size() == 0)) 
		{
			return null;
		}
     //Traverse through the tree in depth first manner until one of stop conditions is 
	 //encountered 
	 
		for (String c : node.getChildren().keySet()) 
		{
			Node childNode = node.getChildren().get(c);
			
			HashMap<Integer, WeatherData> dataAtNode = new HashMap<Integer, WeatherData>();
	 //Get the threshold value and feature name of node		
			double threshold = Double.parseDouble(c.substring(4)); 
			
			String featureName = node.getFeature();;
    //Loop through all rows in test data set and create a new dataset dataAtNode that consist of all test 
	//instances that satisfy a threshold value test of the node
			for (Map.Entry<Integer, WeatherData> key_weatherData : XDataValidTest.entrySet()) 
			{
				HashMap<Integer, WeatherData> curKeyWeatherData = new HashMap<Integer, WeatherData>();

				curKeyWeatherData.put(key_weatherData.getKey(),
						key_weatherData.getValue());

				WeatherData wd = key_weatherData.getValue();

				ArrayList<Feature> features = wd.getFeatures();

				for (Feature f : features) {

					if (f.getName().equals(featureName)) 
					{
						Double f_value = Double.parseDouble((String) f
								.getValues().get(0));
						String part_string = c.substring(0, 4);
						if ((part_string.equals("less") && f_value <= threshold)
								|| (part_string.equals("more") && f_value > threshold)) 
						{
							dataAtNode.put(key_weatherData.getKey(),
									key_weatherData.getValue());
						}
						break;
					}
				}

			}
			// Recursion happens here
			// For each child node recursively call execute on the child node and dataAtnode created above
			Node newChildNode = execute(childNode, dataAtNode);
			
			//If the child node returned from recursion is not null add child node as the 
			//as one of the children of node
			if (newChildNode != null) 
			{
				node.getChildren().put(c, newChildNode);
			}
		}
		//Return node ,if the node has children(not a leaf node) and the no child of the node is leaf.
		
		if (node.getChildren().size() != 0) {
			HashMap<String, Node> children = node.getChildren();
			for (String c : node.getChildren().keySet()) {
				if (!children.get(c).getType().equals("leaf")) {
					return node;
				}
			}
		}
		//Create a new data set result which stores the number of instances in the test data set
		//labeled as target and the number of instances labeled as empty string(not target)
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		result.put(target_label, 0);
		for (Map.Entry<Integer, WeatherData> key_weatherData : XDataValidTest
				.entrySet()) {
			Integer key = key_weatherData.getKey();
			WeatherData wd = YDataValidTest.get(key);

			ArrayList<Feature> features_in_Y = wd.getFeatures();
			for (Feature f : features_in_Y) {
				if (f.getValues().get(0).equals(target_label)) {
					if (result.containsKey(target_label)) {
						result.put(target_label, (result.get(target_label) + 1));
					}
				}
			}

		}
		// Compute the PrunedTreeLabel as the majority label of test data instances 
		// Score_with_prune is the score(number of test instances) with majority label
		result.put("", (XDataValidTest.size() - result.get(target_label)));
		int score_with_prune = 0;
		String prunedTreeLabel = "";
		for (String r : result.keySet()) {
			if (score_with_prune < result.get(r)) {
				prunedTreeLabel = r;
			}
			score_with_prune = Math.max(score_with_prune, result.get(r));

		}
		
		//Compute the score(number of correct predictions in Unpruned Tree) for model by taking the 
		//above test data set,which is a subset of the original test data set
		
		int scoreFromModel = 0;
		for (Map.Entry<Integer, WeatherData> key_weatherData : XDataValidTest
				.entrySet()) {
			int key_in_data = key_weatherData.getKey();
			WeatherData wd = YDataValidTest.get(key_in_data);
			ArrayList<Feature> features = wd.getFeatures();
			for (Feature f : features) {
				if (f.getValues().contains(target_label)
						&& result_model.get(key_in_data).equals(target_label)) {
					scoreFromModel++;
				} else if (!f.getValues().contains(target_label)
						&& !(result_model.get(key_in_data).equals(target_label))) {
					scoreFromModel++;
				}

			}
		}
		// Replace the subtree at node by leaf label when the score with pruning
		// is no worse than score from original tree
		if (scoreFromModel > score_with_prune) {
			return node;
		} else {
			node.setType("leaf");
			node.getChildren().clear();
			node.setleafLabel(prunedTreeLabel);
			return node;
		}
	}
}
