/*
 * Author: Troy Nechanicky, 150405860, nech5860@mylaurier.ca
 * Version: January 31, 2019
 */

package a1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

//Class for set of all Itemsets
//Has private class Itemsets that contains frequentItemsets and infrequentItemsets data structures for a certain length 
class ItemsetSets {
	private List<Itemsets> itemsetSets;
	private int minSupportCount;
	
	ItemsetSets(int minSupportCount) {
		itemsetSets = new ArrayList<Itemsets>();
		itemsetSets.add(new Itemsets());
		this.minSupportCount = minSupportCount;
	}
	
	//increment support count of items appearing in transaction
	//Only use when generating frequentItemsets of length 1
	void addTransaction(String[] transaction) {		
		Itemsets frequentItemsets = itemsetSets.get(0);
		
		for (String item : transaction) {
			frequentItemsets.addTransactionItemset(new Itemset(item));
		}
	}
	
	void findNextCandidateSet() {
		Itemsets candidateItemsets;
		int itemsetSize = itemsetSets.size();
		
		if (itemsetSize == 1) {
			candidateItemsets = itemsetSets.get(itemsetSets.size() - 1).findFirstCandidateItemsets();
		} else {
			candidateItemsets = itemsetSets.get(itemsetSets.size() - 1).findCandidateItemsets();
		}

		itemsetSets.add(candidateItemsets);		
	}
	
	//returns Itemset array of frequent Itemsets of length itemsetLength
	Itemset[] getFrequentItemsets(int itemsetLength) {
		return itemsetSets.get(itemsetLength - 1).getItemsets();
	}
	
	int getItemsetSupportCount(Itemset itemset) {
		Itemsets itemsets = itemsetSets.get(itemset.size() - 1);
		
		return itemsets.getItemsetSupportCount(itemset);
		
	}
	
	boolean isLastFrequentItemsetEmpty() {
		return itemsetSets.get(itemsetSets.size() - 1).isFrequentItemsetEmpty();
	}
	
	//increment support count of candidate itemsets appearing in transaction
	void processTransaction(String[] transaction) {
		itemsetSets.get(itemsetSets.size() - 1).processTransaction(transaction);
	}
	
	void purgeInfrequentItemsets() {
		itemsetSets.get(itemsetSets.size() - 1).purgeInfrequentItemsets();
	}
	
	int size() {
		return itemsetSets.size();
	}
	
	private class Itemsets {
		private Map<Itemset, Integer> frequentItemsets;
		private Map<Itemset, Integer> infrequentItemsets;
		
		//uses LinkedHashMap because iterating over them is faster than iterating over normal HashMap
		private Itemsets() {
			frequentItemsets = new LinkedHashMap<Itemset, Integer>();
			infrequentItemsets = new LinkedHashMap<Itemset, Integer>();
		}
		
		private void addCandidateItemset(Itemset itemset) {
			frequentItemsets.put(itemset, 0);		
		}

		private void addTransactionItemset(Itemset itemset) {
			Integer itemSupport = frequentItemsets.putIfAbsent(itemset, 1);
			
			if (itemSupport != null) frequentItemsets.put(itemset, itemSupport + 1);			
		}
		
		//finds candidate itemset based on it's frequentItemsets
		//use to find itemsets of length > 2
		private Itemsets findCandidateItemsets() {
			Itemsets candidateItemsets = new Itemsets();
			Itemset[] frequentItemsetsArr = frequentItemsets.keySet().toArray(new Itemset[frequentItemsets.size()]);
			
			for (int i = 0; i < frequentItemsetsArr.length - 1; i++) {
				for (int j = i + 1; j < frequentItemsetsArr.length; j++) {
					Itemset itemset1Subsequence = frequentItemsetsArr[i].getItems(0, frequentItemsetsArr[i].size() - 1);
					Itemset itemset2Subsequence = frequentItemsetsArr[j].getItems(0, frequentItemsetsArr[i].size() - 1);
					
					//if all itemset values except last match, then combine itemsets to form candidate itemset
					if (itemset1Subsequence.equals(itemset2Subsequence)) {
						Itemset candidateItemset = frequentItemsetsArr[i].getItems(0, frequentItemsetsArr[i].size());
						candidateItemset.add(frequentItemsetsArr[j].getItem(frequentItemsetsArr[i].size() - 1));
						
						candidateItemsets.addCandidateItemset(candidateItemset);
					}
				}
			}
			
			return candidateItemsets;
		}
		
		//finds candidate itemsets of length 2
		private Itemsets findFirstCandidateItemsets() {
			Itemsets candidateItemsets = new Itemsets();
			Itemset[] frequentItemsetsArr = frequentItemsets.keySet().toArray(new Itemset[frequentItemsets.size()]);
			
			for (int i = 0; i < frequentItemsetsArr.length - 1; i++) {
				//combine items to form candidate itemset
				for (int j = i + 1; j < frequentItemsetsArr.length; j++) {
					Itemset itemset1 = frequentItemsetsArr[i].getItem(0);
					Itemset itemset2 = frequentItemsetsArr[j].getItem(0);
					Itemset candidateItemset = itemset1;
					candidateItemset.add(itemset2);
						
					candidateItemsets.addCandidateItemset(candidateItemset);
				}
			}
			
			return candidateItemsets;
		}
		
		//returns Itemset array of frequent Itemsets
		private Itemset[] getItemsets() {
			return frequentItemsets.keySet().toArray(new Itemset[frequentItemsets.size()]);
		}
		
		private int getItemsetSupportCount(Itemset itemset) {
			Integer supportCount = frequentItemsets.get(itemset);
			
			if (supportCount == null) supportCount = infrequentItemsets.get(itemset);
			
			return supportCount;
		}
		
		private boolean isFrequentItemsetEmpty() {
			return frequentItemsets.isEmpty();
		}
		
		
		//increment support count of candidate itemsets appearing in transaction
		private void processTransaction(String[] transaction) {
			for (Map.Entry<Itemset, Integer> frequentItemset : frequentItemsets.entrySet()) {
				if (frequentItemset.getKey().isInTransaction(Arrays.asList(transaction))) {
					frequentItemset.setValue(frequentItemset.getValue() + 1);
				}
			}
		}
		
		private void purgeInfrequentItemsets() {
			infrequentItemsets = frequentItemsets.entrySet().stream()
					.filter(itemset -> itemset.getValue() < minSupportCount)
					.collect(Collectors.toMap(itemset -> itemset.getKey(), itemset -> itemset.getValue()));
			
			frequentItemsets.entrySet().removeAll(infrequentItemsets.entrySet());
		}
	}
}