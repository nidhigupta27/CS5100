package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

import util.WeatherData;

public class DecisionTree {
	public static void main(String args[]) {
		HashSet<WeatherData> trainData = read("weatherData.txt");
		//HashSet<WeatherData> testData = read(args[1]);
	}

	private static HashSet<WeatherData> read(String filename) {
		HashSet<WeatherData> data = new HashSet<WeatherData>();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(filename));
		} catch (FileNotFoundException exception) {
			System.out.println("Error: File not found");
			System.exit(1);
		}
		String line;
		String firstLine = scanner.nextLine(); 
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			WeatherData newData = new WeatherData();
			newData.addFeatureNames(firstLine);
			newData.addFeatures(line);
			data.add(newData);
		}
		return data;
	}
}
