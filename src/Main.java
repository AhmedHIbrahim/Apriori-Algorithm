import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			// Reading from the file
			if(args[0] == null){
				System.out.println("please provide an absolute path to the transactions");
			}
			if(args[1] == null){
				System.out.println("please provide a minimumsupport");
			}
			File file = new File(args[0]);
			double minimumSupport = Double.parseDouble(args[1]);
			FileReader fileReader = new FileReader(file);
			BufferedReader br = new BufferedReader(fileReader);

			String transaction;
			ArrayList<Set> transactions = new ArrayList<Set>();

			// Reading transaction by transaction from text file.
			while ((transaction = br.readLine()) != null) {

				// set = single transaction
				Set<String> set = new HashSet<String>();

				// Splitting each transaction and adding its items to the set
				for (String item : transaction.split(", ")) {
					// trim is used to eliminate leading and trailing spaces
					set.add(item.trim());
				}
				// Adding each (set=transaction) to transactions
				transactions.add(set);
			}
			// Calling aprioriAlgorithm with a transactions parameter
			aprioriAlgorithm(transactions, minimumSupport);
			// Closing the BufferedReader of the file
			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Set aprioriAlgorithm(ArrayList<Set> transactions,
			double minSup) {
		// C1 is the Initial pass over the transactions
		
		Set<String> C1 = initial_pass(transactions);
		

		System.out.println("C1----> " + C1);

		// F1 => Finding single frequent items
		ArrayList<ArrayList> F1 = new ArrayList<ArrayList>();

		Iterator<?> iterOnFirstCandidate = C1.iterator();

		while (iterOnFirstCandidate.hasNext()) {
			String C1_item = iterOnFirstCandidate.next().toString();
			int item_count = 0;
			// Find the count of an item in all of the transactions
			for (Set<?> transaction : transactions) {
				if (transaction.contains(C1_item)) {
					item_count++;
				}
			}
			// Check if the item passes minimum support threshold, if yes : add
			// it to F1
			if (((double) item_count / (double) transactions.size()) >= minSup) {
				ArrayList<String> a = new ArrayList<String>();
				a.add(C1_item);
				F1.add(a);
			}
		}
		System.out.println("F1----> " + F1);

		ArrayList<ArrayList> F_k_1 = new ArrayList<ArrayList>();

		// Initialize Frequent(k-1) with first frequent item set(F1)
		F_k_1 = F1;
		Set<ArrayList> Fk = new HashSet();

		// while F(k-1) not equal to null; starting from K=2; Generate
		// Candidate-Generations
		for (int k = 2; F_k_1 != null; k++) {
			// Get Candidate itemSets from candidate_generationeration method
			ArrayList<ArrayList> Ck = candidate_generation(F_k_1);

			System.out.println("C" + k + "----> " + Ck);

			// If (Candidate itemSets ==1 or it is an empty ArrayList) then
			// break
			if (Ck.size() < 1 || Ck.isEmpty()) {
				break;
			} else {
				// Clear Frequent(k) itemSets to use it in the next iterations
				Fk.clear();
			}

			// Check if each CandidateItemSets is Frequent in the transactions
			// If yes, add it to frequent itemSets
			for (ArrayList candidate : Ck) {
				int candidate_count = 0;
				for (Set transaction : transactions) {
					Set<String> set = new HashSet<String>(candidate);
					if (transaction.containsAll(set)) {
						candidate_count++;
					}
				}
				if (((double) candidate_count / (double) transactions.size()) >= minSup) {
					Fk.add(candidate);
				}
			}

			
			System.out.println("F" + k + "----> " + Fk);

			
			// tempFrequntItemSets : to cast from Set to an ArrayList
			ArrayList tempFrequntItemSets = new ArrayList();
			tempFrequntItemSets.addAll(Fk);
			F_k_1 = tempFrequntItemSets;

		}
		
		return Fk;

	}

	private static Set<String> initial_pass(ArrayList<Set> transactions) {
		Set<String> C1 = new HashSet<String>();
		for (Set<?> transaction : transactions) {
			Iterator<?> iterOnItems = transaction.iterator();
			while (iterOnItems.hasNext()) {
				C1.add(iterOnItems.next().toString());
			}
		}
		return C1;
	}

	private static ArrayList<ArrayList> candidate_generation(ArrayList<ArrayList> f_k_1) {

		ArrayList<ArrayList> candidates = new ArrayList();

		for (int i = 0; i < f_k_1.size(); i++) {
			for (int j = 0; j < f_k_1.size(); j++) {
				if ((f_k_1.get(i) != f_k_1.get(j))) {
					TreeSet<String> set1 = new TreeSet<String>(f_k_1.get(i));
					TreeSet<String> set2 = new TreeSet<String>(f_k_1.get(j));

					Iterator arr1Iterator = set1.iterator();
					Iterator arr2Iterator = set2.iterator();

					ArrayList<String> candidate = new ArrayList<String>();

					// Generate candidate itemSet
					while (arr1Iterator.hasNext()) {
						String item1 = arr1Iterator.next().toString();
						String item2 = arr2Iterator.next().toString();

						if (arr1Iterator.hasNext()) {
							if (item1 != item2) {
								break;
							} else {
								candidate.add(item1);
							}
						} else {
							if (item2.compareTo(item1) >= 1) {
								candidate.add(item1);
								candidate.add(item2);
								candidates.add(candidate);
							}
						}
					}
				}
			}
		}

		for (int i = 0; i < candidates.size(); i++) {
			ArrayList<String> candidate = candidates.get(i);
			for (int j = 0; j < candidate.size(); j++) {

				// Generate tempCandidate from candidate
				ArrayList<String> tempCandidate = new ArrayList();
				for (String item : candidate) {
					tempCandidate.add(item);
				}
				// In each iteration, remove j indexed item from tempCandidate,to get a Subset
				tempCandidate.remove(j);
				// Check if frequent(k-1) doesn't have that subset(tempCandidate)
				if (!(f_k_1.contains(tempCandidate))) {
					candidates.remove(candidate);
				}
			}
		}

		return candidates;
	}
}
