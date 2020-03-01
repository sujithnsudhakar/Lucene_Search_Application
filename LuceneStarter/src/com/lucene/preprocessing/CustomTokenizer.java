package com.lucene.preprocessing;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.lucene.indexer.LuceneIndexer;

public class CustomTokenizer {
	/**
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public TokenStream processTokens(Path path) throws IOException {
		Analyzer analyzer = new StandardAnalyzer();
		String text = new String(Files.readAllBytes(path));
		String title = "";
		LuceneIndexer luceneIndexer = new LuceneIndexer();
		if (luceneIndexer.isHTMLDocument(path.toFile())) {
			Document doc = Jsoup.parse(text);
			title = doc.title().toString() + "\n";
			text = doc.body().text();
			text = title + text;
		}
		
		CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
		
		TokenStream stream = analyzer.tokenStream("testFieldName", new StringReader(text));
		stream = new StopFilter(stream, stopWords);
		stream = new PorterStemFilter(stream);
		stream.reset();
		return stream;
	}
}
