//Raheeq Mousa 1220515
//Zaid Mousa 1221833
package Application;

import java.util.ArrayList;

public class DecisionTreeNode {
    private int featureIndex;         // Index of the feature used to split (only for decision nodes)
    private ArrayList<DecisionTreeNode> node_childrens;          // (only for decision nodes)
    private Object status;        // is edible or not
    private boolean isLeaf;           // this is used to know if this decision tree node is a leaf or not
    private String featureValue;
    private String branchValue; // The value of the feature for this branch

    //this is used for decision nodes
    public DecisionTreeNode(int featureIndex, ArrayList<DecisionTreeNode> c, String value) {
        this.featureIndex = featureIndex;
        this.node_childrens=c;
        this.isLeaf = false;
        this.featureValue=value;
    }

    //this is used for leaf nodes
    public DecisionTreeNode(Object status) {
        this.status = status;
        this.isLeaf = true;
    }

    public DecisionTreeNode() {
        this.isLeaf = true;
        this.status = "";
        this.featureIndex = -1;
    }


    public int getFeatureIndex() {
        return featureIndex;
    }
    public void setFeatureIndex(int featureIndex) {
        this.featureIndex = featureIndex;
    }

    

    public ArrayList<DecisionTreeNode> getNode_childrens() {
		return node_childrens;
	}

	public void setNode_childrens(ArrayList<DecisionTreeNode> node_childrens) {
		this.node_childrens = node_childrens;
	}

	public Object isStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isLeaf() {
        return isLeaf;
    }
    public void setLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    
	public String getFeatureValue() {
		return featureValue;
	}
	public void setFeatureValue(String featureValue) {
		this.featureValue = featureValue;
	}
	
	

	public String getBranchValue() {
		return branchValue;
	}

	public void setBranchValue(String branchValue) {
		this.branchValue = branchValue;
	}

	@Override
	public String toString() {
		return "DecisionTreeNode [featureIndex=" + featureIndex + ", node_childrens=" + node_childrens + ", status="
				+ status + ", isLeaf=" + isLeaf + "]";
	}


}
