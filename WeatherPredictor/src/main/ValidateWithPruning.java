package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import util.Feature;
import util.Node;
import util.WeatherData;

public class ValidateWithPruning {

	ArrayList<Feature> features;
	String target;
	HashMap<Integer, WeatherData> XtestDataMap;
	HashMap<Integer, WeatherData> YtestDataMap;
	HashMap<Integer, WeatherData> XtrainDataMap;
	HashMap<Integer, WeatherData> YtrainDataMap;

	private Node root;
	private double score;
	private HashMap<Integer, String> result = new HashMap<Integer, String>();

	public ValidateWithPruning(Node root,ArrayList<Feature> features, String target,HashMap<Integer, WeatherData> xtrainDataMap,HashMap<Integer, WeatherData> ytrainDataMap,HashMap<Integer, WeatherData> xtestDataMap2,HashMap<Integer, WeatherData> ytestDataMap2) 
	{
		this.target = target;
		this.features = features;
		this.XtestDataMap = xtestDataMap2;
		this.YtestDataMap = ytestDataMap2;
		this.XtrainDataMap = xtrainDataMap;
		this.YtrainDataMap = ytrainDataMap;
		this.root = root;
	}

	public HashMap<Integer, String> validateAfterPrune() throws IOException 
	{	
		mine();
		result = validate();
		//GrowTree tree = new GrowTree(features, target, XtrainDataMap,
			//	YtrainDataMap, XtestDataMap, YtestDataMap);
		//System.out.println("calling tree construct");
		//Node root = tree.construct();
		//System.out.println("tree created successfully");
		PostTreePruning ptp = new PostTreePruning(XtestDataMap,YtestDataMap, XtestDataMap, result, target);
		Node newRoot = ptp.execute(root, XtestDataMap);
	    root = newRoot;
		//mine();
		result = validate();
		return result;
	}

	public HashMap<Integer, String> validate() {

		int correct = 0;
		//mine()
		HashMap<Integer, String> res = getResult();
		return res;
		
	}

	public HashMap<Integer, String> getResult() {
		mine();
		return result;
	}

	private void mine() {
		for (int i = 0; i < XtestDataMap.size(); i++) {
			Node node = root;
			WeatherData currInstance = XtestDataMap.get(i);
			// WeatherData resInstance = result.get(i);
			String value = null;
			 //System.out.println("node leaf label "+node.getleafLabel());
			while (!node.getType().equals("leaf")) {
				 // System.out.println("node type and node name"+node.getType()+" "+node.getAttribute().getName());
				String attributeName = node.getAttribute().getName();
				ArrayList<Feature> attributeValuePairs = currInstance.getFeatures();
				for (Feature f : attributeValuePairs) 
				{
					if (f.getName().contains(attributeName)) 
					{
						value = (String) f.getValues().get(0);
					}
				}
				// String value = attributeValuePairs.get(attributeName);

				HashMap<String, Node> children = node.getChildren();
				String tmp = "";
				for (String s : children.keySet()) 
				{
					String threshold = s.substring(4);
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
			/*if(!node.getleafLabel().isEmpty())
			{
				//System.out.println("node.getLeafLabel type "+node.getType());
				 //System.out.println("node.getLeafLabel "+node.getleafLabel());
			}
			 
			ArrayList<String> temp = new ArrayList<String>();
			String[] s = node.getleafLabel().split("-");
			for (int j = 0; j < s.length; j++) {
				//System.out.println("Inside spilt"+s[j]);
				temp.add(s[j]);
			}*/
			result.put(i, node.getleafLabel());
		}
	}

}