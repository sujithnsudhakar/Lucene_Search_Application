package com.test.lucene;

import com.lucene.indexer.LuceneIndexer;
import com.lucene.searcher.LuceneSearcher;

public class LuceneDemo {

	public static void main(String[] args) throws Exception {
		
		//Currently accepts only boolean query and phrase query. No wild card queries allowed
		String query = "Sunny and Priya";
		
		LuceneIndexer luceneIndexer = new LuceneIndexer();
		System.out.println("------------------------------------------------------------------");
		System.out.println("----------------List of Parsed and Indexed Files------------------");
		System.out.println("------------------------------------------------------------------");
		luceneIndexer.processIndexing();
		LuceneSearcher lucenerSearcher = new LuceneSearcher();
		
		try {
			System.out.println("------------------------------------------------------------------");
			System.out.println("The given query is " + query);
			System.out.println("------------------------------------------------------------------");
			System.out.println("-----------Ranked List of relevant documents----------------------");
			System.out.println("------------------------------------------------------------------");
			lucenerSearcher.processSearching(query);
		} catch (Exception e) {
			System.out.println("Exception thrown inside main() method: " + e);
			e.printStackTrace();
		}
	}
}
