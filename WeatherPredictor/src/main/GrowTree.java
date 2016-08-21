package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import util.Node;
import util.WeatherData;
import util.Feature;

public class GrowTree {
	private String target;
	HashMap<Integer, WeatherData> XtrainDataMap;
	HashMap<Integer, WeatherData> YtrainDataMap;
	HashMap<Integer, WeatherData> XtestDataMap;
	HashMap<Integer, WeatherData> YtestDataMap;
	ArrayList<Feature> features;

	public GrowTree(ArrayList<Feature> features, String target,
			HashMap<Integer, WeatherData> xtrainDataMap,
			HashMap<Integer, WeatherData> ytrainDataMap,
			HashMap<Integer, WeatherData> xtestDataMap,
			HashMap<Integer, WeatherData> ytestDataMap) 
	     {
		    this.features = features;
			this.target = target;
			this.XtrainDataMap = xtrainDataMap;
			this.YtrainDataMap = ytrainDataMap;
			this.XtestDataMap = xtestDataMap;
			this.YtestDataMap = ytestDataMap;
	    }

	/**
	 * Construct tree
	 * 
	 * @return TreeNode
	 * @throws IOException
	 */
	public Node construct() throws IOException {
		return constructTree(this.features, this.XtrainDataMap,
				this.YtrainDataMap);
	}

	/**
	 * Construct tree recursively. First make the root node, then construct its
	 * subtrees recursively, and finally connect root with subtrees.
	 * 
	 * @param target
	 * @param attributes
	 * @param instances
	 * @return TreeNode
	 * @throws IOException
	 */
	private Node constructTree(ArrayList<Feature> features,
			HashMap<Integer, WeatherData> Xinstances,
			HashMap<Integer, WeatherData> Yinstances) throws IOException {

		/*
		 * Stop when (1) entropy is zero (2) no attribute left
		 */

		if (homogenous(target, Yinstances) || Yinstances.size() == 1
				|| features.size() == 0) {
			String leafLabel = "";
			if (homogenous(target, Yinstances)) {
				leafLabel = target;
				// get
				// this.YtrainDataMap.get(0).getYValue();
				/*
				 * ArrayList<String> x=null; for(Map.Entry<Integer, WeatherData>
				 * key_weatherData : Yinstances.entrySet()) { WeatherData wd =
				 * key_weatherData.getValue(); x =(wd.featureValue("Events")); }
				 * for(int i=0;i<x.size();i++) { if(i<x.size()-1) leafLabel +=
				 * x.get(i)+"-"; else leafLabel +=x.get(i); }
				 */
				// leafLabel =
			} else {

				leafLabel = getMajorityLabel(target, Yinstances);
			}
			// System.out.println("leaf label is "+leafLabel);
			Node leaf = new Node(leafLabel);
			//System.out.println("leaf label is "+leafLabel);
			return leaf;
		}
		// Choose the root attribute

		SelectFeature selectedFeature = new SelectFeature(this.target,
				features, Xinstances, Yinstances);

		Feature rootAttr = selectedFeature.getFeature();

		Feature temp = null;
		// Remove the chosen attribute from attribute set
		for (int i = 0; i < features.size(); i++) {
			if (features.get(i).getName().contains(rootAttr.getName())) {
				temp = features.get(i);
				features.remove(i);
				break;
			}
		}
		// Make a new root
		Node root = new Node(rootAttr);
        //System.out.println("Root of  the tree"+ root.getAttribute().getName());
		// Get value subsets of the root attribute to construct branches
		HashMap<String, HashMap<Integer, WeatherData>> xSubsets = selectedFeature
				.getXSubset();
		HashMap<String, HashMap<Integer, WeatherData>> ySubsets = selectedFeature
				.getYSubset();

		for (String valueName : ySubsets.keySet()) {

			HashMap<Integer, WeatherData> ysubset = ySubsets.get(valueName);
			HashMap<Integer, WeatherData> xsubset = xSubsets.get(valueName);

			if (ysubset.size() == 0 || ysubset == null) {
				//String leafLabel = getMajorityLabel(target, ysubset);
				String leafLabel = "";
				// System.out.println("leaf label is "+leafLabel);
				Node leaf = new Node(leafLabel);
				root.addChild(valueName, leaf);
			} else {
				// if(ysubset!=null) {
				for (Map.Entry<Integer, WeatherData> key_weatherData : ysubset
						.entrySet()) {
					WeatherData wd = key_weatherData.getValue();

					// System.out.println("wd size "+wd.)
					/*
					 * ArrayList<Feature> featuresz = new ArrayList<Feature>();
					 * featuresz = wd.getFeatures(); for(Feature f: featuresz) {
					 * System.out.println("feature: "+f.getValues()); }
					 */
				}
				Node child = constructTree(features, xsubset, ysubset);
				
				root.addChild(valueName, child);

				// }
			}
		}

		// Remember to add it again!
		features.add(temp);
		return root;
	}

	public Boolean homogenous(String target,
			HashMap<Integer, WeatherData> ytrainDataMap2) 
	{
		ArrayList<ArrayList<String>> valuesOfTarget = new ArrayList<ArrayList<String>>();
		for (Map.Entry<Integer, WeatherData> key_weatherData : ytrainDataMap2
				.entrySet()) 
		{
			WeatherData wd = key_weatherData.getValue();
			valuesOfTarget.add(wd.featureValue("Events"));
		}
		int count = 0;
		String targetName = target;
		for (int i = 0; i < valuesOfTarget.size(); i++) {
			if (valuesOfTarget.get(i).contains(target)) {
				count++;
			}
		}
		if (count == valuesOfTarget.size()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Get the majority target class label from instances
	 * 
	 * @param target
	 * @param ytrainDataMap2
	 * @return String
	 * @throws IOException
	 */
	public String getMajorityLabel(String target,
			HashMap<Integer, WeatherData> ytrainDataMap2) throws IOException {
		HashMap<String, Integer> valuesOfTarget = new HashMap<String, Integer>();
		for (Map.Entry<Integer, WeatherData> key_weatherData : ytrainDataMap2
				.entrySet()) {
			WeatherData wd = key_weatherData.getValue();

			ArrayList<String> list = wd.featureValue("Events");
			for (int i = 0; i < list.size(); i++) {
				if (valuesOfTarget.containsKey(list.get(i))) {
					int x = valuesOfTarget.get(list.get(i));
					valuesOfTarget.put(list.get(i), x + 1);
				} else {
					valuesOfTarget.put(list.get(i), 1);
				}
			}
		}
		int maxVal = Integer.MIN_VALUE;
		String maxLabel = "";
		for (Map.Entry<String, Integer> key_weatherData : valuesOfTarget
				.entrySet()) {
			// System.out.println("fdshfjsdhfjsdhfjsdhj "+key_weatherData.getKey());
			if (key_weatherData.getKey().contains("storm")) {
				// System.out.println("thunderstormmmmmmmmmmmmmmmmm "+key_weatherData.getValue());
			}
			int value = key_weatherData.getValue();
			if (value > maxVal) {
				maxVal = value;
				maxLabel = key_weatherData.getKey();
			} else if (value == maxVal) {
				maxLabel += key_weatherData.getKey();
			}
		}
		if (target.contains("storm")) {
			// System.out.println("maxlabel: "+maxLabel);
		}
		if (maxLabel.contains(target)) {
			maxLabel = target;
		} else {
			maxLabel = "";
		}
		/*
		 * for(Map.Entry<String, Integer> key_weatherData :
		 * valuesOfTarget.entrySet()) { int value = key_weatherData.getValue();
		 * if(value == maxVal && !key_weatherData.getKey().equals(maxLabel)) {
		 * maxVal = value; maxLabel += "-"+key_weatherData.getKey(); } }
		 * //System.out.println("done checking"); System.out.println(maxLabel);
		 */
		return maxLabel;
	}
}