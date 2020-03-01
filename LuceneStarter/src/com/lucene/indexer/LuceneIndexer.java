package com.lucene.indexer;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;

import com.lucene.preprocessing.Constant;
import com.lucene.preprocessing.CustomTokenizer;

public class LuceneIndexer {
	/**
	 * 
	 * @param stream
	 * @param file
	 */
	public void indexDocsFromDirectory(TokenStream stream, Path file) {
		// Folder where our index should be generated.
		try {
			// org.apache.lucene.store.Directory instance
			Directory dir = FSDirectory.open(Paths.get(Constant.INDEX_DIR));
			// standard analyzer uses default stop words
			Analyzer analyzer = new StandardAnalyzer();

			// IndexWriter Configuration
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

			// IndexWriter writes new index files to the directory
			IndexWriter writer = new IndexWriter(dir, iwc);
			// System.out.println("Files Last Mod Date
			// "+Files.getLastModifiedTime(file));
			addDocumentToIndexer(writer, stream, file);

			writer.close();
		} catch (IOException e) {
			System.out.println("Exception thrown inside indexDocsFromDirectory() method: " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param writer
	 * @param stream
	 * @param path
	 * @throws IOException
	 */
	public void addDocumentToIndexer(IndexWriter writer, TokenStream stream, Path path) throws IOException {
		// Try with resources--internally it closes all the resources after
		// execution
		String content = "";
		//System.out.println("Files Last Mod Date " + Files.getLastModifiedTime(path));
		FileTime time = Files.getLastModifiedTime(path);
		while (stream.incrementToken()) {
			content = content + " " + (stream.getAttribute(CharTermAttribute.class).toString());
		}
		// System.out.print(content);
		// Create lucene Document
		Document doc = new Document();
		//
		LuceneIndexer luceneIndexer = new LuceneIndexer();
		String text = new String(Files.readAllBytes(path));
		String title = "";
		if (luceneIndexer.isHTMLDocument(path.toFile())) {
			org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(text);
			title = jsoupDoc.title().toString() + "\n";
			doc.add(new StringField("HTMLtitle", title, Field.Store.YES));
		}
		doc.add(new StringField("name", path.getFileName().toString(), Field.Store.YES));
		doc.add(new StringField("path", path.toString(), Field.Store.YES));
		doc.add(new StringField("modifiedDate", time.toString(), Field.Store.YES));
		doc.add(new TextField("contents", content, Store.YES));

		// Updates a document by first deleting the document and then adding the
		// new document.
		writer.updateDocument(new Term("path", path.toString()), doc);
		// List all the parsed files:
		System.out.println(doc.get("name"));
	}
	/**
	 *sends the files for indexing
	 */
	public void processIndexing() {
		CustomTokenizer token = new CustomTokenizer();
		final Path docDir = Paths.get(Constant.DOCUMENTS_DIR);
		System.out.println("Currently parsing...");
		if (Files.isDirectory(docDir)) {
			try {
				Files.walkFileTree(docDir, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						boolean isTextHTMLDoc = checkDocumentType(file.getFileName().toFile());
						if (isTextHTMLDoc) {
							TokenStream streamFromTokenizer;
							streamFromTokenizer = token.processTokens(file);
							LuceneIndexer luceneIndexer = new LuceneIndexer();
							luceneIndexer.indexDocsFromDirectory(streamFromTokenizer, file);
						}
						return FileVisitResult.CONTINUE;
					}
				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 
	 * @param file
	 * @return
	 */
	public boolean checkDocumentType(File file) {
		if (file.isHidden() || file.isDirectory()) {
			return false;
		}
		if (!file.getName().endsWith("txt") && !file.getName().endsWith("html") && !file.getName().endsWith("htm")) {
			return false;
		} else {
			return true;
		}
	}
	public boolean isHTMLDocument(File file) {
		if (file.isHidden() || file.isDirectory()) {
			return false;
		}
		if (file.getName().endsWith("html") || file.getName().endsWith("htm")) {
			return true;
		} else {
			return false;
		}
	}

}
