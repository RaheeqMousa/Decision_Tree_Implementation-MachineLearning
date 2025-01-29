//Raheeq Mousa 1220515
//Zaid Mousa 1221833
package Application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyList {
	private List<Entity> list;

	public MyList() {
		this.list = new ArrayList<>();
	}

	public int getNumber_of_objects() {
		return list.size();
	}

	public void addToList(Entity entity) {
		list.add(entity);
	}

	public Entity getObject(int index) {
		return list.get(index);
	}

	public MyList subList(int fromIndex, int toIndex) {
		MyList subList = new MyList();
		subList.list.addAll(this.list.subList(fromIndex, toIndex));
		return subList;
	}
	
	public void remove(Object obj) {
		list.remove(obj);
	}

	public void shuffle() {
		Collections.shuffle(list);
	}

	public List<Entity> getList() {
		return list;
	}

	@Override
	public String toString() {
		return "MyList [list=" + list + "]";
	}
	
	
}
