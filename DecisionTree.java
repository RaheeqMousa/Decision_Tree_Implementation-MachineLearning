//Raheeq Mousa 1220515
//Zaid Mousa 1221833
package Application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DecisionTree {

	public DecisionTree() {
		// Default constructor
	}

	
//...................................BUILD DECISION TREE METHOD.....................................................	

	
	public DecisionTreeNode buildDecisionTree(MyList dataset, ArrayList<Integer> features) {
		// if the dataset is empty, return a default leaf node
		if (dataset.getNumber_of_objects() == 0) {
			return new DecisionTreeNode("Unknown");
		}

		// if all entities have the same status, return a leaf node
		if (isPureData(dataset)) {
			Object firstEntityStatus = dataset.getObject(0).getStatus();
			return new DecisionTreeNode(firstEntityStatus);
		}

		// if no features are left to split on, return the majority class
		if (features.isEmpty()) {
			Object majorityClass = findMajorityClass(dataset);
			return new DecisionTreeNode(majorityClass);
		}

		// find the feature with the highest information gain
		int featureIndexWithHighestIG = -1;
		double highestInformationGain = -1;

		for (int feature : features) {
			
			double informationGain = Driver.calculateGainRatio(dataset, feature);
			if (informationGain > highestInformationGain) {
				highestInformationGain = informationGain;
				featureIndexWithHighestIG = feature;
			}
		}

		//create a decision node for the best feature (which has the highest information gain)
		DecisionTreeNode node = new DecisionTreeNode(featureIndexWithHighestIG);

		//remove the selected feature from the list of available features
		features.remove(Integer.valueOf(featureIndexWithHighestIG));

		//split the dataset by the best feature (feature with the highest information gain)
		ArrayList<MyList> subsets = splitDataset(dataset, featureIndexWithHighestIG);

		ArrayList<DecisionTreeNode> children = new ArrayList<>();
		for (int i = 0; i < subsets.size(); i++){
            //recursively build the tree for each subset of data
			MyList subset=subsets.get(i);
        	System.out.println(subsets);
            DecisionTreeNode childNode = buildDecisionTree(subset, new ArrayList<>(features));
            //childNode.setBranchValue(Driver.featureValues.get(node.getFeatureIndex()).get(i));
	        children.add(childNode);
        }

        //create a decision node with the best feature index, children nodes, and features values
        return new DecisionTreeNode(featureIndexWithHighestIG, children, Driver.features[featureIndexWithHighestIG+1]);
	}
	
	
//..................................THIS METHOD TO CALCULATE THE ACCURACY....................................................................

	
	public double calculateAccuracy(MyList testSet, DecisionTreeNode root) {
		int correct = 0;
		
		for (int i = 0; i < testSet.getNumber_of_objects(); i++) {
			Entity entity = testSet.getObject(i);
			Object predicted = predict(entity, root);
			
			if (predicted.equals(entity.getStatus())) {
				correct++;
			}
		}
		
		if (testSet.getNumber_of_objects() == 0) {
			return 0; // No test cases
		}
		
		return (double) correct / testSet.getNumber_of_objects();
	}

	
//.....................................THIS METHOD TO CALCULATE THE PRECISION..........................................................................	
	
	
	public double calculatePrecision(MyList testSet, DecisionTreeNode root, Object positiveClass) {
		int truePositive = 0;
		int falsePositive = 0;

		for (int i = 0; i < testSet.getNumber_of_objects(); i++) {
			Entity entity = testSet.getObject(i);
			Object predicted = predict(entity, root);
			
			if (!predicted.equals("Unknown")) { // Ignore "Unknown" predictions
				if (predicted.equals(positiveClass)) {
					if (((String)entity.getStatus()).equalsIgnoreCase((String)positiveClass)) {
						truePositive++;
					} else {
						falsePositive++;
					}
				}
			}
		}

		System.out.println();
		return (truePositive + falsePositive == 0) ? 0.0 : (double) truePositive / (truePositive + falsePositive);
	}

	
//....................................THIS METHOD TO CALCULATE THE RECALL.....................................................................	
	
	
	public double calculateRecall(MyList testSet, DecisionTreeNode root, Object positiveClass) {
		int truePositive = 0;
		int falseNegative = 0;

		for (int i = 0; i < testSet.getNumber_of_objects(); i++) {
			Entity entity = testSet.getObject(i);
			Object predicted = predict(entity, root);

			if (!predicted.equals("Unknown")) { // Ignore "Unknown" predictions
				if (((String)entity.getStatus()).equalsIgnoreCase((String)positiveClass)) {
					if (predicted.equals(positiveClass)) {
						truePositive++;
					} else {
						falseNegative++;
					}
				}
			}
		}

		return (truePositive + falseNegative == 0) ? 0.0 : (double) truePositive / (truePositive + falseNegative);
	}

//.........................THIS METHOD TO CALCULATE THE F SCORE...........................................................
	
	public double calculateFScore(double precision, double recall) {
		if (precision + recall == 0) {
			return 0; // Avoid division by zero
		}

		double fScore = 2 * ((precision * recall) / (precision + recall));
		return fScore;	
		}

	
	
//..................THIS METHOD TO PREDICT THE ENTITY STATUS USING THE DECISION TREE..........................................................


	public String predict(Entity dataPoint, DecisionTreeNode root) {
		DecisionTreeNode currentNode = root;

		while (currentNode != null && currentNode.isStatus() == null) {
			String featureValue = dataPoint.getFeature(currentNode.getFeatureIndex());

			// Traverse the tree using the correct branch for the feature value
			DecisionTreeNode nextNode = null;
			for (int i = 0; i < currentNode.getNode_childrens().size(); i++) {
				String branchValue = Driver.featureValues.get(currentNode.getFeatureIndex()).get(i);
				if (branchValue.equals(featureValue)) {
					nextNode = currentNode.getNode_childrens().get(i);
					break;
				}
			}

			if (nextNode == null) {
				// feature which its value is not found in the tree, log and return "Unknown" or fallback
				System.out.println("Warning: Feature value " + featureValue + " not found in tree. Returning 'Unknown'.");
				return "Unknown"; // Adjust fallback logic as needed
			}

			currentNode = nextNode;
		}

		return currentNode != null ? (String) currentNode.isStatus() : "Unknown";
	}



//.....................................THIS METHOD TO SPLIT THE DATA SET..........................................................

	
	
	//since the feature may have more than three values --> we needs more than two subsets when we split the dataset
	public static ArrayList<MyList> splitDataset(MyList data, int featureIndex) {
	    // Find all unique values in the given feature column
	    ArrayList<String> uniqueValues = new ArrayList<>();
	    ArrayList<MyList> subsets = new ArrayList<>();

	    for (int i = 0; i < data.getNumber_of_objects(); i++) {
	        Entity entity = (Entity) data.getObject(i);
	        String featureValue = entity.getFeature(featureIndex);

	        if (featureValue == null) continue; // Skip null values

	        // Check if this value is already processed
	        if (!uniqueValues.contains(featureValue)) {
	            uniqueValues.add(featureValue);
	            subsets.add(new MyList()); // Create a new subset for this value
	        }

	        // Add the entity to the appropriate subset
	        int subsetIndex = uniqueValues.indexOf(featureValue);
	        subsets.get(subsetIndex).addToList(entity);
	    }

	    return subsets; // Return the subsets for all unique feature values
	}


//.........................THIS METHOD CHECK IF ALL ENTITIES IN THE DATA SET BELONG TO THE SAME CLASS.......................................................


	private boolean isPureData(MyList data) {
		// Check if all entities in the dataset belong to the same class
		Object firstEntityStatus = data.getObject(0).getStatus();

		for (int i = 1; i < data.getNumber_of_objects(); i++) {
			if (!data.getObject(i).getStatus().equals(firstEntityStatus)) {
				return false;
			}
		}

		return true;
	}
	
//........................this method is used when there are no features left to split on ....................................................	
						
	private Object findMajorityClass(MyList dataset) {
		ArrayList<Object> classCounts = new ArrayList<>();
		ArrayList<Integer> counts = new ArrayList<>();

		for (int i = 0; i < dataset.getNumber_of_objects(); i++) {
			Object label = dataset.getObject(i).getStatus();
			if (!classCounts.contains(label)) {
				classCounts.add(label);
				counts.add(1);
			} else {
				int index = classCounts.indexOf(label);
				counts.set(index, counts.get(index) + 1);
			}
		}

		int maxCount = -1;
		Object majorityClass = null;

		for (int i = 0; i < counts.size(); i++) {
			if (counts.get(i) > maxCount) {
				maxCount = counts.get(i);
				majorityClass = classCounts.get(i);
			}
		}

		return majorityClass;
	}

//...........................................................................................................................	
	
	public void displayMetrics(MyList testSet, DecisionTreeNode root, Object positiveClass) {
		double accuracy = calculateAccuracy(testSet, root);
		double precision = calculatePrecision(testSet, root, positiveClass);
		double recall = calculateRecall(testSet, root, positiveClass);
		double fScore = calculateFScore(precision, recall);

		System.out.println("\nMetrics Summary:");
		System.out.println("Accuracy: " + accuracy);
		System.out.println("Precision: " + precision);
		System.out.println("Recall: " + recall);
		System.out.println("F-Score: " + fScore);
	}

	
//.................................THIS MESTHOD TO PRINT THE DECISION TREE..................................................	
 
    public static void printTree(DecisionTreeNode node, int depth) {
        if (node == null) {
            return;
        }

        // if the node is leaf node then print its status
        if (node.getNode_childrens() == null) {
            System.out.println("  ".repeat(depth) + "Leaf: " + node.isStatus());
        } else {//the node is decision node     
            System.out.println("  ".repeat(depth) + "Feature " + node.getFeatureValue() + ":");

            // here we print each the branches for a specific feature value
            for (int i = 0; i < node.getNode_childrens().size(); i++) {
                System.out.println("  ".repeat(depth + 1) + "If value " + Driver.featureValues.get(node.getFeatureIndex()).get(i) + ":");
                //recursively print the child nodes
                printTree(node.getNode_childrens().get(i), depth + 2);
            }          
        }
        
    }
    
    
}
