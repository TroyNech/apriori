/*
 * Author: Troy Nechanicky, 150405860, nech5860@mylaurier.ca
 * Version: January 31, 2019
 */

package a1;

import java.util.ArrayList;
import java.util.List;

class Itemset {
	private List<String> itemset;
	
	Itemset(String item) {
		itemset = new ArrayList<String>();
		itemset.add(item);
	}
	
	Itemset(List<String> items) {
		this.itemset = items;
	}
	
	void add(Itemset itemset) {
		this.itemset.addAll(itemset.itemset);
	}
	
	//returning Itemset more helpful than returning String
	// but like returning String, want to ensure returning something that doesn't not reference item in list
	Itemset getItem(int index) {
		return new Itemset(itemset.get(index));
	}
	
	//start = inclusive, end = exclusive
	//returning Itemset more helpful than returning String
	// but like returning String, want to ensure returning something that doesn't not reference item in list
	Itemset getItems(int start, int end) {
		return new Itemset(new ArrayList<String>(itemset.subList(start, end)));
	}
	
	boolean isInTransaction(List<String> transaction) {
		return transaction.containsAll(itemset);
	}
	
	//returns removed item as Itemset
	Itemset remove(int i) {
		return new Itemset(itemset.remove(i));
	}
	
	//change list formatting to set formatting
	String setOutputFormat() {
		return itemset.toString().replaceFirst("\\[", "{").replaceFirst("\\]", "}");
	}
	
	int size() {
		return itemset.size();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Itemset) {
			Itemset otherItemset = (Itemset) other;
			
			return itemset.equals(otherItemset.itemset);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return itemset.hashCode();
	}
	
	@Override
	public String toString() {
		return itemset.toString();
	}

}