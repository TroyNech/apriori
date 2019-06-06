/*
 * Author: Troy Nechanicky, 150405860, nech5860@mylaurier.ca
 * Version: January 31, 2019
 * 
 * Usage: Apriori filename tgtItemsetLen minSupportCount
 * Description:
 *  Finds association rules of dataset using Apriori algorithm
 *  Expects that data file is space-delimited, with each line representing a transaction
 * Output:
 *  Prints association rules, sorted first in descending order of confidence, then by ascending order of itemsets
 *  where antecedent length is tgtItemsetLen - 1 and consequent length is 1
 * Limitations: 
 *  Little to no error handling for inproper input (i.e. missing/unexpected entries)
 */

package a1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public class Apriori {
	private FileInputStream fileStream;
	private int tgtItemsetLen;
	private ItemsetSets itemsetSets;
	private List<Rule> rules;
	
	Apriori(FileInputStream fileStream, int tgtItemsetLen, int minSupportCount) {	
		this.fileStream = fileStream;
		this.tgtItemsetLen = tgtItemsetLen;
		
		itemsetSets = new ItemsetSets(minSupportCount);
		rules = null;
	}

	public static void main(String[] args) {
		String fileReadername = args[0];
		int tgtItemsetLen = Integer.parseInt(args[1]);
		int minSupportCount = Integer.parseInt(args[2]);
		
		FileInputStream fileStream = null;
		try {
			fileStream = new FileInputStream(fileReadername);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		Apriori apriori = new Apriori(fileStream, tgtItemsetLen, minSupportCount);	
		
		try {
			apriori.findFrequentItems();
			apriori.findFrequentItemsets();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		apriori.computeRules();
		apriori.printRules();
	}
	
	void findFrequentItems() throws IOException {
		String line;
		BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileStream));
		
		while ((line = fileReader.readLine()) != null) {
			String[] items = line.split(" ");
			
			//increment support count of items appearing in transaction
			itemsetSets.addTransaction(items);	
		}
		
		// return stream to top of file
		fileStream.getChannel().position(0);
		
		itemsetSets.purgeInfrequentItemsets();
	}
	
	void findFrequentItemsets() throws IOException {		
		for (int itemsetLength = 1; itemsetLength < tgtItemsetLen && !itemsetSets.isLastFrequentItemsetEmpty(); itemsetLength++) {
			BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileStream));
			String line;
			
			itemsetSets.findNextCandidateSet();
			
			while ((line = fileReader.readLine()) != null) {
				String[] items = line.split(" ");
				
				//increment support count of candidate itemsets appearing in transaction
				itemsetSets.processTransaction(items);
			}
			
			// return stream to top of file
			fileStream.getChannel().position(0);
			
			itemsetSets.purgeInfrequentItemsets();
		}
	}
	
	void computeRules() {
		//get association rules where antecedent length is tgtItemsetLen - 1 and consequent length is 1
		rules = Rule.computeRules(itemsetSets);
		
		//sorted first in descending order of confidence, then by ascending order of itemsets
		Collections.sort(rules);
	}
	
	void printRules() {
		System.out.println("\nRules:");
		
		for (Rule rule : rules) {
			System.out.println(rule.setOutputFormat());
		}
	}
}