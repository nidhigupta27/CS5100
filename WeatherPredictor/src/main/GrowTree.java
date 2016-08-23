package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import util.Node;
import util.WeatherData;


// GrowTree class construct the decision tree by considering features that have maximum gain ratio
public class GrowTree {
	private String target;
	HashMap<Integer,WeatherData> XtrainDataMap;
	HashMap<Integer,WeatherData> YtrainDataMap;
	HashMap<Integer,WeatherData> XtestDataMap;
	HashMap<Integer,WeatherData> YtestDataMap;
	ArrayList<String> features;

	// constructor
	// updates the list of features, current target class, X value of train data and Y value of train data
	public GrowTree(ArrayList<String >features,String target, HashMap<Integer, WeatherData> xtrainDataMap,
			HashMap<Integer, WeatherData> ytrainDataMap) {
		this.features = features;
		this.target = target;
		this.XtrainDataMap = xtrainDataMap;
		this.YtrainDataMap = ytrainDataMap;
	}

	// constructs a tree and returns the root node
	public Node construct() throws IOException  {
		return constructTree(this.features,this.XtrainDataMap,this.YtrainDataMap);
	}

	/**
	 * Construct tree recursively. 
	 * First creates the root node by selecting the feature with maximum gain ratio
	 * Then construct its subtrees recursively
	 * Finally connect root with subtrees.
	 * @throws IOException 
	 */
	private Node constructTree(ArrayList<String> features,HashMap<Integer,WeatherData> Xinstances,
			HashMap<Integer,WeatherData> Yinstances) throws IOException  {

		// Base condition occurs when 
		// all the train instances belong to same class
		// there is only one train instance
		// there are no features left to assign to a node
		if (homogenous(target, Yinstances) ||Yinstances.size()==1|| features.size() == 0) {
			String leafLabel = "";
			// if all the train instances belong to target class, then leaf label is target			
			if (homogenous(target, Yinstances)) {
				leafLabel = target;		
			} else {
				// if train instances are not homogeneous, then leaf label is made target if majority
				// class in train is target.
				leafLabel = getMajorityLabel(target,Yinstances);
			}

			// creates the leaf node with the label.
			// label is either target name or empty string
			// Label is target name if one of homogeneous or majorityLabel condition is satisfied
			// Label is empty string implies that the train instance does not belong to target class
			Node leaf = new Node(leafLabel,"leaf");
			return leaf;
		}
		// Choose the feature the provides maximum gain at the current time
		SelectFeature selectedFeature = new SelectFeature(target,features,Xinstances,
				                                                     Yinstances);
		String rootFeature = selectedFeature.getFeature();

		String temp =null;
		// Remove the chosen feature from feature set
		for(String feature: features) {
			if(feature.contains(rootFeature))
			{
				temp =feature;
				features.remove(feature);
				break;
			}
		}	

		// Make a new root with selected feature
		Node root = new Node(rootFeature,"root");

		// Get value subsets of the root feature to construct branches
		HashMap<String, HashMap<Integer,WeatherData>> xSubsets = selectedFeature.getXSubset();
		HashMap<String, HashMap<Integer,WeatherData>> ySubsets = selectedFeature.getYSubset();


		// consider the left and right subset
		for (String valueName : ySubsets.keySet()) {
			HashMap<Integer,WeatherData> ysubset = ySubsets.get(valueName);
			HashMap<Integer,WeatherData> xsubset = xSubsets.get(valueName);

			// recursively construct tree for the subsets(children)
			Node child = constructTree(features, xsubset,ysubset);
			// add them as child of current root node
			root.addChild(valueName, child);
		}				
		// add the removed feature again
		features.add(temp);
		return root;
	}

	// homogenous() takes target class, train data classification and checks if all instances
	// in the train data belongs to the same class
	public Boolean homogenous(String target, HashMap<Integer, WeatherData> ytrainDataMap) {
		ArrayList<ArrayList<String>> valuesOfTarget = new ArrayList<ArrayList<String>>();
		int count =0;
		for(Map.Entry<Integer, WeatherData> weatherData : ytrainDataMap.entrySet())
		{
			WeatherData wd = weatherData.getValue();
			ArrayList<String> tempClass = wd.featureValue("Events");			
			if(tempClass.contains(target)) {
				count++;
			}	
		}
		if(count==ytrainDataMap.size()) {
			return true;
		}
		return false;
	}

	// getMajorityLabel() returns the target class if it is majority, else returns empty string
	public String getMajorityLabel(String target, HashMap<Integer, WeatherData> ytrainDataMap)  {
		HashMap<String,Integer> valuesOfTarget = new HashMap<String,Integer>();
		//valuesOfTarget maps class label to their number of occurance
		for(Map.Entry<Integer, WeatherData> key_weatherData : ytrainDataMap.entrySet())
		{
			WeatherData wd = key_weatherData.getValue();
			ArrayList<String> list = wd.featureValue("Events");
			for(int i=0;i<list.size();i++){
				if(valuesOfTarget.containsKey(list.get(i))){
					int x = valuesOfTarget.get(list.get(i));
					valuesOfTarget.put(list.get(i), x+1);
				}
				else {
					valuesOfTarget.put(list.get(i),1); 	
				}
			}
		}
		int maxVal = Integer.MIN_VALUE;
		String maxLabel ="";
		//find the maximum class label
		for(Map.Entry<String, Integer> key_weatherData : valuesOfTarget.entrySet())
		{
			int value = key_weatherData.getValue();
			if(value>maxVal) {
				maxVal = value;
				maxLabel = key_weatherData.getKey();
			}		
			else if(value==maxVal) {
				maxLabel += key_weatherData.getKey();
			}
		}

		if(maxLabel.contains(target)) {
			maxLabel = target;
		}
		// no-class condition (example no rain , no fog,etc)
		else {
			maxLabel ="";
		}
		return maxLabel;
	}
}