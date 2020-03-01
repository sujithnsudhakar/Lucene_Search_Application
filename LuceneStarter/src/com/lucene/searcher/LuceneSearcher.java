package com.lucene.searcher;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.lucene.indexer.LuceneIndexer;
import com.lucene.preprocessing.Constant;
public class LuceneSearcher {
	
	/**
	 * 
	 * @param query
	 * @throws Exception
	 */
	public void processSearching(String query) throws Exception {
		// Create lucene searcher from our indexed document. It search over a
		// single IndexReader.
		IndexSearcher searcher = createSearcherFromIndex();
		int index = 1;
		int rankIndex = 1;
		String updatedQuery = "";

		Analyzer analyzer = new StandardAnalyzer();
		TokenStream stream = analyzer.tokenStream("testFieldName", new StringReader(query));
		stream = new PorterStemFilter(stream);
		stream.reset();
		while (stream.incrementToken()) {
			updatedQuery = updatedQuery + " " + stream.getAttribute(CharTermAttribute.class).toString();
		}

		// Search indexed contents using search term
		TopDocs foundDocs = searchContent(updatedQuery, searcher);

		// Total found documents
		System.out.println("\n Total Results :: " + foundDocs.totalHits);

		// Print the info about files which has been searched term
		for (ScoreDoc sd : foundDocs.scoreDocs) {
			Document d = searcher.doc(sd.doc);
			System.out.println(index + " " + d.get("name"));
			index++;
		}
		System.out.println("------------------------------------------------------------------");
		System.out.println("----------------------Matching Information------------------------");
		System.out.println("------------------------------------------------------------------");
		LuceneIndexer luceneIndexer = new LuceneIndexer();
		for (ScoreDoc sd : foundDocs.scoreDocs) {
			Document d = searcher.doc(sd.doc);
			File file = new File(d.get("name"));
			System.out.println("Name : " + d.get("name") + ", Rank : " + rankIndex + ", Relevance Score : " + sd.score
					+ ", Path : " + d.get("path") + ", Last Modified Time Date : " + d.get("modifiedDate")
			/* + "Contents "+d.get("contents") */);
			if (luceneIndexer.isHTMLDocument(file)) {
				System.out.print("HTML Title : " + d.get("HTMLtitle"));
			}
			rankIndex++;
		}
	}

	/**
	 * 
	 * @param textToFind
	 * @param searcher
	 * @return
	 * @throws Exception
	 */
	private TopDocs searchContent(String textToFind, IndexSearcher searcher) throws Exception {
		// Create search query
		QueryParser qp = new QueryParser("contents", new StandardAnalyzer());
		Query query = qp.parse(textToFind);

		// search the index
		TopDocs hits = searcher.search(query, 25);
		return hits;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private IndexSearcher createSearcherFromIndex() throws IOException {
		Directory dir = FSDirectory.open(Paths.get(Constant.INDEX_DIR));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}
}
