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
	private HashMap<Integer, WeatherData> XDataValidTestOrig = new LinkedHashMap<Integer, WeatherData>();
	private HashMap<Integer, WeatherData> YDataValidTestOrig = new LinkedHashMap<Integer, WeatherData>();
	private String target_label;
	private HashMap<Integer, String> result_model = new HashMap<Integer, String>();

	public PostTreePruning(HashMap<Integer, WeatherData> XDataValidTest,
			HashMap<Integer, WeatherData> YDataValidTestOrig,
			HashMap<Integer, WeatherData> XDataValidTestOrig,
			HashMap<Integer, String> result, String t) {
		    this.XDataValidTest = XDataValidTest;
		    this.YDataValidTest = YDataValidTestOrig;
		    this.XDataValidTestOrig = XDataValidTestOrig;
		    this.YDataValidTestOrig = YDataValidTestOrig;
		    this.target_label = t;
		    this.result_model = result;

	}

	public Node execute(Node node, HashMap<Integer, WeatherData> XDataValidTest) {
		
		if (node.getType().equals("leaf")) {
			return node;
		}
		if ((node == null) || (XDataValidTest.size() == 0)) {
			return null;
		}

		for (String c : node.getChildren().keySet()) {
			Node childNode = node.getChildren().get(c);
			
			HashMap<Integer, WeatherData> dataAtNode = new HashMap<Integer, WeatherData>();
			
			double threshold = Double.parseDouble(c.substring(4));
			String featureName = node.getAttribute().getName();

			for (Map.Entry<Integer, WeatherData> key_weatherData : XDataValidTest
					.entrySet()) {
				HashMap<Integer, WeatherData> curKeyWeatherData = new HashMap<Integer, WeatherData>();

				curKeyWeatherData.put(key_weatherData.getKey(),
						key_weatherData.getValue());

				WeatherData wd = key_weatherData.getValue();

				ArrayList<Feature> features = wd.getFeatures();

				for (Feature f : features) {

					if (f.getName().equals(featureName)) {
						Double f_value = Double.parseDouble((String) f
								.getValues().get(0));
						String part_string = c.substring(0, 4);
						if ((part_string.equals("less") && f_value <= threshold)
								|| (part_string.equals("more") && f_value > threshold)) {
							dataAtNode.put(key_weatherData.getKey(),
									key_weatherData.getValue());
						}
						break;
					}
				}

			}
			// Recursion happens here
			Node newChildNode = execute(childNode, dataAtNode);
			if (newChildNode != null) {
				node.getChildren().put(c, newChildNode);
			}
		}
		if (node.getChildren().size() != 0) {
			HashMap<String, Node> children = node.getChildren();
			for (String c : node.getChildren().keySet()) {
				if (!children.get(c).getType().equals("leaf")) {
					return node;
				}
			}
		}
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
		result.put("", (XDataValidTest.size() - result.get(target_label)));
		int score_with_prune = 0;
		String prunedTreeLabel = "";
		for (String r : result.keySet()) {
			if (score_with_prune < result.get(r)) {
				prunedTreeLabel = r;
			}
			score_with_prune = Math.max(score_with_prune, result.get(r));

		}
		int scoreFromModel = 0;
		for (Map.Entry<Integer, WeatherData> key_weatherData : XDataValidTest
				.entrySet()) {
			int key_in_data = key_weatherData.getKey();
			WeatherData wd = YDataValidTestOrig.get(key_in_data);
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
