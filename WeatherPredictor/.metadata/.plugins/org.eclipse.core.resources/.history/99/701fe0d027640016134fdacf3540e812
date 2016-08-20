package util;

import java.util.HashMap;
import util.Feature;

public class Node {
	private String type;
	private Feature feature;
	private HashMap<String, Node> children;
	private String leafLabel;

	public Node(Feature feature) {
		this.type = "root";
		this.feature = feature;
		children = new HashMap<String, Node>();
	}

	public Node(String nodeLabel) {
		this.type = "leaf";
		this.leafLabel = nodeLabel;
	}
	public Feature getAttribute() {
		return feature;
	}

	public void addChild(String Name, Node child) {
		children.put(Name, child);
	}
	public HashMap<String, Node> getChildren() {
		return children;
	}

	public String getleafLabel() {
		return leafLabel;
	}

	public void setleafLabel(String leafLabel) {
		this.leafLabel = leafLabel;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}