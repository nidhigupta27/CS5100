package main;

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
	HashMap<Integer,WeatherData> XtrainDataMap;
	HashMap<Integer,WeatherData> YtrainDataMap;
	HashMap<Integer,WeatherData> XtestDataMap;
	HashMap<Integer,WeatherData> YtestDataMap;
	
	private Node root;
	private double score;
	private HashMap<Integer,String> result = new HashMap<Integer,String>();
	public ValidateWithPruning(ArrayList<Feature> features,String target,HashMap<Integer, WeatherData> xtrainDataMap2,HashMap<Integer, WeatherData> ytrainDataMap2,HashMap<Integer, WeatherData> xtestDataMap2,
			HashMap<Integer, WeatherData> ytestDataMap2) {
		this.target = target;
		this.features = features;
		this.XtrainDataMap = xtrainDataMap2;
		this.YtrainDataMap = ytrainDataMap2;
		this.XtestDataMap = xtestDataMap2;
		this.YtestDataMap = ytestDataMap2;

	}
	public HashMap<Integer,String> validateAfterPrune()
	{
		//Validate v = new Validate(features, target, XtrainDataMap, YtrainDataMap, XtestDataMap,
			//	YtestDataMap);
		result = validate();
		PostTreePruning ptp = new PostTreePruning(root,XtestDataMap,YtestDataMap,XtestDataMap,result,target);
		Node newRoot = ptp.execute(root,XtestDataMap);
		root = newRoot;
		result = validate(); 
		return result;
	}
	public HashMap<Integer,String> validate()
	{
		try {
             System.out.println("before grow tree");
     
			GrowTree tree = new GrowTree(this.features,this.target, this.XtrainDataMap,
					this.YtrainDataMap, this.XtestDataMap,
					this.YtestDataMap);
			System.out.println("calling tree construct");
			root = tree.construct();
			System.out.println("tree created successfully");
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		//result = this.YtestDataMap;
		int correct = 0;
		HashMap<Integer,String> res = getResult();
		
		return res;
		//return scores;
	}

	public HashMap<Integer,String> getResult() {
		mine();
		return result;
	}

	private void mine() {
		for (int i = 0; i < XtestDataMap.size(); i++) {
			Node node = root;
			WeatherData currInstance = XtestDataMap.get(i);
			//WeatherData resInstance = result.get(i);
			String value = null;
			//System.out.println("node leaf label "+node.getleafLabel());
			while (!node.getType().equals("leaf")) {
				//System.out.println("node type "+node.getType());
				String attributeName = node.getAttribute().getName();
				ArrayList<Feature> attributeValuePairs = currInstance.getFeatures();
				for(Feature f: attributeValuePairs) {
					if(f.getName().contains(attributeName)) {
						value = (String)f.getValues().get(0);
					}
				}
				//String value = attributeValuePairs.get(attributeName);

				HashMap<String, Node> children = node.getChildren();
				String tmp = "";
				for (String s : children.keySet()) {
					String threshold = s.substring(4);
					if (Double.parseDouble(value) < Double.parseDouble(threshold)) {
						tmp = "less";
					} else {
						tmp = "more";
					}
					String curLabel = s.substring(0, 4);
					
					if (tmp.equals(curLabel)) {node = children.get(s);
					//System.out.println("node "+node.getleafLabel());
					
					}
				}

			}
			//System.out.println("node.getLeafLabel "+node.getType());
			ArrayList<String> temp = new ArrayList<String>();
			String[] s =node.getleafLabel().split("-");
			for(int j=0;j<s.length;j++) {
				temp.add(s[j]);
			}
			result.put(i,node.getleafLabel());
		}
	}

}
