/*
 * Author: Troy Nechanicky, 150405860, nech5860@mylaurier.ca
 * Version: January 31, 2019
 */

package a1;

import java.util.ArrayList;
import java.util.List;

public class Rule implements Comparable<Rule> {
	private Itemset antecedent;
	private Itemset consequent;
	private double confidence;
	
	private Rule(Itemset antecedent, Itemset consequent, double confidence) {
		this.antecedent = antecedent;
		this.consequent = consequent;
		this.confidence = confidence;
	}
	
	String setOutputFormat() {
		return String.format(antecedent.setOutputFormat() + " -> " + consequent.setOutputFormat() + " (%g)", confidence);
	}
	
	@Override
	//sort first in descending order of confidence, then by ascending order of itemsets
	public int compareTo(Rule otherRule) {
		if (this.confidence != otherRule.confidence) {
			return (this.confidence < otherRule.confidence) ? 1 : -1;
		}
		
		return this.antecedent.toString().compareTo(otherRule.antecedent.toString());
	}	
	
	//find association rules where antecedent length is lengthOfLastGeneratedItemsets - 1 and consequent length is 1
	static List<Rule> computeRules(ItemsetSets itemsetSets) {
		List<Rule> rules = new ArrayList<Rule>();
		//get last generated frequent itemsets
		Itemset[] itemsets = itemsetSets.getFrequentItemsets(itemsetSets.size());
		
		for (Itemset itemset : itemsets) {
			//create all possible rules satisfying antecedent and consequent length requirements
			for (int i = 0; i < itemset.size(); i++) {
				Itemset consequent = itemset.getItem(i);
				Itemset antecedent = itemset.getItems(0, itemset.size());
				antecedent.remove(i);	
				
				double confidence = (double) itemsetSets.getItemsetSupportCount(itemset) / itemsetSets.getItemsetSupportCount(antecedent);

				rules.add(new Rule(antecedent, consequent, confidence));
			}
		}
		
		return rules;		
	}
}