//Raheeq Mousa 1220515
//Zaid Mousa 1221833
package Application;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Driver extends Application {

	private static MyList training_set;
	static MyList test_set;
	public static String[] features;
	public static ArrayList<ArrayList<String>> featureValues=new ArrayList<>();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		training_set = new MyList();
		primaryStage.setTitle("CSV File Reader");

		Button readButton = new Button("Read CSV");

		readButton.setOnAction(e -> {
			readCSVFile(primaryStage); // Read file and calculate results immediately

			if (training_set.getNumber_of_objects() != 0) {
				DecisionTree d_tree = new DecisionTree();

				// Initialize featureValues for all features
				featureValues = new ArrayList<>();
				for (int i = 0; i < training_set.getObject(0).getFeatures().length; i++) {
					featureValues.add(new ArrayList<>());
				}

				// Collect unique feature values
				for (int i = 0; i < training_set.getNumber_of_objects(); i++) {
					for (int j = 0; j < training_set.getObject(0).getFeatures().length; j++) {
						String featureValue = training_set.getObject(i).getFeature(j);
						if (featureValue != null && !featureValues.get(j).contains(featureValue)) {
							featureValues.get(j).add(featureValue);
						}
					}
				}

				//this to get the features indices
				ArrayList<Integer> feature_indices = new ArrayList<>();
				for (int i = 0; i < training_set.getObject(0).getFeatures().length; i++) {
					feature_indices.add(i);
				}
				
				// Build the decision tree
				DecisionTreeNode root = d_tree.buildDecisionTree(training_set, feature_indices);

				// Print the decision tree
				DecisionTree.printTree(root, 0);

				// Compute and display metrics
				d_tree.displayMetrics(test_set, root, "YES");
			}
		});


		StackPane startStackPane = new StackPane(readButton);
		primaryStage.setScene(new Scene(startStackPane, 400, 200));
		primaryStage.show();
	}
	
//..............................READ THE CHOSEN FILE.......................................................

	public static void readCSVFile(Stage stage) {

		MyList all_data = new MyList();
		DataInputStream input = null;

		int count = 0;
		int entities_counter = 0;

		try {
			// Use FileChooser to select the file
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select CSV File");
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));

			File file = fileChooser.showOpenDialog(stage);
			if (file == null) {
				return;
			}

			input = new DataInputStream(new FileInputStream(file));

			String header = input.readLine(); // to skip the header line
			System.out.println(header);
			String line = "";
			count = 0;
			features=header.split(",");
			String[] in=header.split(",");
			

			while ((line = input.readLine()) != null) {
				String[] array = line.split(",");
				String[] features = new String[array.length - 1]; // exclude the Edible column
				for (int i = 0; i < features.length; i++) {
					features[i] = array[i + 1];
				}

				if (array.length != in.length) { // that means the line (row) read has missing or extra information
					count++;
					continue;
				}

				try {
					String status = array[0];

					Entity r = new Entity(features, status);
					all_data.addToList(r);
					entities_counter++;

				} catch (IllegalArgumentException ex) {
					count++;
				}
			}

			all_data.shuffle(); // ensures the data is distributed randomly to prevent bias
			
			training_set = all_data.subList(0, ((70 * all_data.getNumber_of_objects()) / 100));
			test_set = all_data.subList(((70 * all_data.getNumber_of_objects()) / 100), all_data.getNumber_of_objects());

			
			
			
			//calculate entropy and information gain immediately after reading
			double entropy = calculateEntropy(training_set);
			double infoGain = calculateInformationGain(training_set, 0); // Using feature index 0 as an example "IG(EDIBLE,CAP-SHAPE)"
			
			double gainRatio = calculateGainRatio(training_set,0);
			
			// Display results in an alert
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Processing Complete");
			alert.setHeaderText("Data Processing Results");
			alert.setContentText("Training Set Size: " + training_set.getNumber_of_objects() + "\n" +
					"Test Set Size: " + test_set.getNumber_of_objects() + "\n" +
					"Rows with Issues: " + count + "\n" +
					"Number of Entities: " + entities_counter + "\n\n" +
					"Entropy: " + entropy + "\n"+
					"Information Gain (Feature Index 0): " + infoGain+ "\n" +
					"Gain Ratio: " + gainRatio + "\n"
			);

			alert.showAndWait();
			
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


//......................................THIS METHOD TO CALCULATE THE ENTROPY........................................................

	// Method to calculate entropy
	/*since we are implementing the decision tree, so we need be aware
	that the user might read a data set with multi class target class
	*/
	public static double calculateEntropy(MyList data) {
		HashMap<Object, Integer> classes_counter = new HashMap<>(); //i want to count the number of occurences for each class in the target column

		for (int i=0;i<data.getNumber_of_objects();i++) { //traverse all entities and count the occurences
			Object label = (data.getObject(i).getStatus());
			if (classes_counter.containsKey(label)) { //if the class exists in the hash map i will add one to its counter
				classes_counter.put(label, classes_counter.get(label) + 1);
			} else if (!classes_counter.containsKey(label))  { //if the class does not exist in the hash map i will insert it to the hash and put 1 to its occurences
				classes_counter.put(label, 1);
			}
		}

		double total = data.getNumber_of_objects();
		double entropy = 0;

		for (Object key: classes_counter.keySet()) {
			
			double probability = classes_counter.get(key) / total;

			if(probability>0){ //because log(0) leads to an error
				entropy -= (probability) * log2(probability);
			}
		}

		//System.out.println(entropy);
		return entropy;
	}


//.....................................THIS METHOD TO CALCULATE THE INFORMATION GAIN...................................................

	//method to calculate information gain
	public static double calculateInformationGain(MyList data, int featureIndex) {
		double Entropy=calculateEntropy(data);

		Set<Object> values=new HashSet<>();
		//for the mushroom data set -> values="EDIBLE","POISONOUS"
		for(int i=0;i<data.getNumber_of_objects();i++) {
			Entity e=data.getObject(i);
			Object featureValue=e.getFeature(featureIndex);

			values.add(featureValue);
		}

		//System.out.println(values);
		double weighted_entropy=0;

		for(Object value:values) {

			MyList subset = new MyList();
			for (int j=0; j<data.getNumber_of_objects();j++) {
				Entity e = (Entity) data.getObject(j);
				if (e.getFeature(featureIndex).equals(value)) {
					subset.addToList(e);
				}
			}

			//find the entropy of the subset
			double subsetEntropy = calculateEntropy(subset);

			//calculate the proportion of instances in this subset
			double probability = (double) subset.getNumber_of_objects() / data.getNumber_of_objects();

			//update the weighted entropy
			weighted_entropy+=probability*subsetEntropy;
		}

		//System.out.println(values.size());

		double information_gain = Entropy - weighted_entropy;


		return information_gain;

	}
//.....................................THIS METHOD TO CALCULATE THE GAIN RATIO...................................................

	public static double calculateGainRatio(MyList data, int featureIndex) {
		double infoGain = calculateInformationGain(data, featureIndex);

		Set<String> featureValues = new HashSet<>();
		for (Entity entity : data.getList()) {
			featureValues.add(entity.getFeature(featureIndex));
		}

		double splitInfo = 0.0;
		for (String value : featureValues) {
			MyList subset = new MyList();
			for (Entity entity : data.getList()) {
				if (value.equals(entity.getFeature(featureIndex))) {
					subset.addToList(entity);
				}
			}

			double proportion = (double) subset.getNumber_of_objects() / data.getNumber_of_objects();
			if (proportion > 0) {
				splitInfo -= proportion * log2(proportion);
			}
		}

		if (splitInfo == 0) {
			return 0.0;
		}

		return infoGain / splitInfo;
	}
//........................................................................

	// Log base 2 calculation
	public static double log2(double x) {
		return Math.log(x) / Math.log(2);
	}



}