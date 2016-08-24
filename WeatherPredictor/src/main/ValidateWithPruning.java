package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import util.Feature;
import util.Node;
import util.WeatherData;

// Validate with pruning class classifies the input instances with pruned/un-pruned tree (whichever gives better accuracy)
public class ValidateWithPruning {

	ArrayList<String> features;
	String target;
	HashMap<Integer, WeatherData> XtestDataMap;
	HashMap<Integer, WeatherData> YtestDataMap;
	HashMap<Integer, WeatherData> XtrainDataMap;
	HashMap<Integer, WeatherData> YtrainDataMap;

	private Node root;
	private HashMap<Integer, String> result = new HashMap<Integer, String>();

	public ValidateWithPruning(Node root,ArrayList<String> features, String target,HashMap<Integer, WeatherData> xtrainDataMap,HashMap<Integer, WeatherData> ytrainDataMap,HashMap<Integer, WeatherData> xtestDataMap2,HashMap<Integer, WeatherData> ytestDataMap2) 
	{
		this.target = target;
		this.features = features;
		this.XtestDataMap = xtestDataMap2;
		this.YtestDataMap = ytestDataMap2;
		this.XtrainDataMap = xtrainDataMap;
		this.YtrainDataMap = ytrainDataMap;
		this.root = root;
	}

	// validateAfterPrune() classifies the given test instances and return the result
	public HashMap<Integer, String> validateAfterPrune() throws IOException 
	{	
		// classifies the given test data using un-pruned tree
		mine();
		result = validate();	
		PostTreePruning ptp = new PostTreePruning(XtestDataMap,YtestDataMap, XtestDataMap, result, target);
		// do pruning and see if it is giving better accuracy
		// returns the root node of pruned/un-pruned tree accordingly
		Node newRoot = ptp.execute(root, XtestDataMap);
		root = newRoot;
		mine();
		// result from pruned tree
		result = validate();
		return result;
	}

	// returns the result
	public HashMap<Integer, String> validate() {
		HashMap<Integer, String> res = getResult();
		return res;		
	}

	// classifies and returns the result
	public HashMap<Integer, String> getResult() {
		mine();
		return result;
	}

	// returns current root
	public Node getRoot(){
		return this.root;
	}

	// mine() traverses the given tree (with root node as root) for the given input test instance
	// and finds a label for that instance.
	// Traversal is done by comparing node feature with feature value of test instance
	// Then chose to move left or right depending on if the feature value is less(left) or greater
	// than node threshold value for that feature
	public void mine() {
		for (int i = 0; i < XtestDataMap.size(); i++) {
			Node node = root;
			WeatherData currInstance = XtestDataMap.get(i);
			String value = null;
			while (!node.getType().equals("leaf")) {
				// node feature
				String attributeName = node.getFeature();
				ArrayList<Feature> attributeValuePairs = currInstance.getFeatures();
				// get the corresponding feature value of test instance
				for (Feature f : attributeValuePairs) 
				{
					if (f.getName().contains(attributeName)) 
					{
						value = (String) f.getValues().get(0);
					}
				}

				HashMap<String, Node> children = node.getChildren();
				String tmp = "";
				for (String s : children.keySet()) 
				{
					String threshold = s.substring(4);
					// traverses left or right by comparing node feature value and feature value of current instance
					if (Double.parseDouble(value) <= Double
							.parseDouble(threshold)) 
					{
						tmp = "less";
					} 
					else 
					{
						tmp = "more";
					}
					String curLabel = s.substring(0, 4);

					if (tmp.equals(curLabel)) 
					{
						node = children.get(s);
					}
				}
			}
			ArrayList<String> temp = new ArrayList<String>();
			String[] s = node.getleafLabel().split("-");
			for (int j = 0; j < s.length; j++) {
				temp.add(s[j]);
			}
			// result set is updated here
			result.put(i, node.getleafLabel());
		}
	}
}