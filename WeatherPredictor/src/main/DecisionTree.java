package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import util.Node;
import util.WeatherData;

// main file. Execution starts from here.
public class DecisionTree {

	// stores all target classes
	static ArrayList<String> targets = new ArrayList<String>();
	// stores list of all features
	static private ArrayList<String> featureSet = null;
	// used to combine result in Classname-Classname format
	static HashMap<Integer,String> comp = new HashMap<Integer,String>();
	static String outputFileName = null;

	public static void main(String args[]) throws Exception {
		// add all class label to targets ArrayList (Rain,Snow,Fog,Thunderstorm)
		updateTarget();

		// stores the tree root of all the classed
		HashMap<String,Node> rootForClasses = new HashMap<String,Node>();
		// stores feature set for train data
		HashMap<Integer,WeatherData> XtrainDataMap = new LinkedHashMap<Integer,WeatherData>();
		// stores class labels for train data
		HashMap<Integer,WeatherData> YtrainDataMap= new LinkedHashMap<Integer,WeatherData>();
		// stores feature set for test data
		HashMap<Integer,WeatherData> XtestDataMap = new LinkedHashMap<Integer,WeatherData>();
		// stores actual class labels for test data
		HashMap<Integer,WeatherData> YtestDataMap = new LinkedHashMap<Integer,WeatherData>();
		// stores feature set for random data yet to be classified
		HashMap<Integer,WeatherData> XrandomDataMap = new LinkedHashMap<Integer,WeatherData>();

		// read train, test and random data
		String trainfileName =args[0];
		String testfileName = args[1];
		String classifyfileName= args[2];
		outputFileName = args[3];
		XtrainDataMap = readData(trainfileName,true);
		YtrainDataMap = readData(trainfileName,false);
		XtestDataMap = readData(testfileName,true);
		YtestDataMap = readData(testfileName,false);
		XrandomDataMap = readData(classifyfileName,true);

		System.out.println("x train map size "+XtrainDataMap.size());
		System.out.println("y test map size "+YtestDataMap.size());
		System.out.println("random data size "+XrandomDataMap.size());
		// stores current time
		double startTime = System.currentTimeMillis();
		// create a decision tree model of each class and classify test data
		for (String target : targets) {
			try {
				int i = 0;
				// construct tree for current target class
				GrowTree tree = new GrowTree(featureSet, target, XtrainDataMap,
						YtrainDataMap);
				System.out.println("calling tree construct for "+target);
				// constructs the tree and return root node of created tree
				Node root = tree.construct();
				System.out.println("tree created successfully for "+target);
				ValidateWithPruning vwp = new ValidateWithPruning(root,
						featureSet, target, XtrainDataMap,YtrainDataMap,XtestDataMap, YtestDataMap);
				HashMap<Integer, String> res = vwp.validateAfterPrune();
				// returns root node of pruned tree
				Node prunedRoot = vwp.getRoot();
				// stores all the root node all classes
				rootForClasses.put(target, prunedRoot);
				// format the result to classname-classname format
				formatResult(res,i);
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		// stores current end
		double endTime = System.currentTimeMillis();

		// Evaluation of the result
		Evaluation eval = new Evaluation(YtestDataMap,targets,comp);
		double accuracy = eval.computeAccuracy();

		// J48 CLASSIFIER
		J48Decision  J48= new J48Decision(targets,YtestDataMap);
		// classifies test data using Weka J48 Classifier
		double J48_accuracy = J48.evaluate();
		System.out.println("Accuracy of Weka J48 Implementation = "+J48_accuracy);
		System.out.println("Accuracy of C4.5 Implementation = "+accuracy);
		System.out.println("Time for training and testing C4.5 Implementation= "+(endTime-startTime)/(1000*60)+" minutes");

		//classify random data
		comp = new HashMap<Integer,String>();
		for(String target: targets){
			int i=0;
			Node curRoot =null;
			for(Map.Entry<String, Node> rootData : rootForClasses.entrySet())
			{
				String curTarget = rootData.getKey();
				if(curTarget.contains(target)){
					curRoot = rootData.getValue();
					break;
				}
			}
			ValidateWithPruning vwp = new ValidateWithPruning(curRoot,
					featureSet, target, null,null,XrandomDataMap, null);
			HashMap<Integer, String> res = vwp.getResult();	
			formatResult(res,i);
		}		
		// writes the result of classifying random data to a text file
		writeResultToFile(XrandomDataMap);
		System.out.println("Random data is classified successfully and written to file");
	}

	// writeResultToFile() takes feature set of random datas to be classified and write them to output.txt file
	public static void writeResultToFile(HashMap<Integer,WeatherData> XrandomDataMap){
		try{
			PrintWriter out = new PrintWriter(outputFileName);
			out.println("Sl.no \t| Events ");
			TreeMap<Integer,String> sorted_map = new TreeMap<Integer,String>(comp);
			int i=0;
			for(Map.Entry<Integer, String> result : sorted_map.entrySet()){
				if(result.getKey()!=i) {
					for(int j=i;j<result.getKey();j++){
						out.println(j+"\t\t|None");
					}
				}
				out.println(result.getKey()+"\t\t|"+result.getValue());
				i=result.getKey()+1;
			}
			if(i!=(XrandomDataMap.size())) {
				for(int j=i;j<(XrandomDataMap.size());j++) {
					out.println(j+"\t\t|None");
				}
			}
			out.flush();
			out.close();			
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	// combines the classification result for each class, from their respective model into
	// a format consistent with the input file. For example, the format is Fog-Rain for a data which
	// was classified to classes Fog and Rain respectively.
	private static HashMap<Integer,String> formatResult(HashMap<Integer,String> res,int i) {
		for(Map.Entry<Integer, String> result : res.entrySet()) {
			String wd = result.getValue();
			if(comp.containsKey(i)){
				if(!wd.equals("")) {
					String s = comp.get(i);
					s +="-"+wd;
					comp.put(i,s);
				}
			}
			else {
				if(!wd.equals(""))
					comp.put(i, wd);
			}
			i++;
		}
		return comp;
	}

	// reads the input file
	// if boolean isX is true, then updates X values (feature values)
	//                is false, then updates Y value (class)
	private static HashMap<Integer,WeatherData> readData(String filename,boolean isX) {
		HashMap<Integer,WeatherData> data = new LinkedHashMap<Integer,WeatherData>();
		Scanner scanner = null;
		int key=0;
		try {
			scanner = new Scanner(new File(filename));
		} catch (FileNotFoundException exception) {
			System.out.println("Error: File not found"+filename);
			System.exit(1);
		}
		String line;
		String firstLine = scanner.nextLine(); 
		if(featureSet == null) {
			addFeatureNames(firstLine);
		}
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			WeatherData newData = new WeatherData();
			newData.addFeatureNames(firstLine);
			if(isX)
			{
				newData.addFeaturesToXData(line);
			}
			else
			{
				newData.addFeaturesToYData(line);
			}
			data.put(key, newData);
			key++;
		}
		return data;
	}	

	// adds all the feature name to featureSet.
	public static void addFeatureNames(String names) {
		featureSet = new ArrayList<String>();
		String[] tempNames= names.split(",");
		for(String name: tempNames) {
			if(!name.contains("EST")&&!name.contains("Events"))
				featureSet.add(name);
		}
	}

	// adds all the target classes to targets.
	public static void updateTarget() {
		targets.add("Fog");
		targets.add("Rain");
		targets.add("Snow");
		targets.add("Thunderstorm");
	}
}