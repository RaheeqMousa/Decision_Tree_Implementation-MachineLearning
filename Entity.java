//Raheeq Mousa 1220515
//Zaid Mousa 1221833
package Application;

import java.util.Arrays;

public class Entity {
    private String[] features;
    private Object status;   //in mushroom data set ("edible" or "poisonous")

    public Entity(String[] features, String status) {
        this.features = features;
        this.status = status;
    }

    // this method to get a specific feature value by its index
    public String getFeature(int index) {
        return features[index];
    }


	public String[] getFeatures() {
		return features;
	}

	public void setFeatures(String[] features) {
		this.features = features;
	}

	public Object getStatus() {
		return status;
	}

	public void setStatus(Object status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Entitiy [features=" + Arrays.toString(features) + ", status=" + status + "]";
	}



}
