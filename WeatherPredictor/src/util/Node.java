package util;
import java.util.HashMap;
import util.Feature;

// This class is blueprint for a node in the decision tree.
// Each node has a type,feature,child nodes and leaf label(if node is leaf).
public class Node {
	// type of node. Can have values root and leaf.
	private String type;
	// feature at the node.
	private String feature;
	// children
	private HashMap<String, Node> children;
	// leafLabel stores the following values:
	// - null , if node type="root"
	// - "" , ie empty string if the classification is of type, the instance does not belong to a particular class
	private String leafLabel;

	// constructor for creating a root node and leaf node.
	// children are created only for root nodes
	public Node(String feature,String type) {
		this.type = type;		
		if(type.contains("root")) {
			this.feature = feature;
			children = new HashMap<String, Node>();
		}
		else {
			this.leafLabel = feature;
		}
	}

	// addChild() adds a child to current node.
	// name will be a string with format 'lessthreshold_val' or 'morethreshold_val' indicating 
	// lessthreshold_val -indicating we reach this child node if test data's feature x value is less than
	//                    parent root node's feature x value(threshold_val)
	// morethreshold_val -indicating we reach this child node if test data's feature x value is greater than
	//                    parent root node's feature x value(threshold_val)
	public void addChild(String Name, Node child) {
		children.put(Name, child);
	}

	// returns the children of current node
	public HashMap<String, Node> getChildren() {
		return children;
	}

	// returns the leaf label of current node
	public String getleafLabel() {
		return leafLabel;
	}

	// getFeature() returns the feature
	public String getFeature() {
		return feature;
	}

	// sets the leaf label of current node
	public void setleafLabel(String leafLabel) {
		this.leafLabel = leafLabel;
	}

	// returns the type of current node as "root" or "leaf"
	public String getType() {
		return type;
	}

	// sets current node type
	public void setType(String type) {
		this.type = type;
	}
}