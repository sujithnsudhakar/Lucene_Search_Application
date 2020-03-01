# Lucene_Search_Application

Mini Retrieval System - Apache Lucene
Overview:
The system can parse <b>text and HTML files<b> and return a <b>ranked list</b> of relevant documents <b>given a search query</b>. 
Currently, the query is hardcoded and the document corpus is provided within the project directory.

<b>The functionality has been modularized and implemented into the following logical blocks:</b>
1. Pre-processing which includes lexical analysis, Stopword elimination, Stemming
2. Indexing
3. Search based on user query(Currently allowing Phrase and Boolean query)

Getting Started:
Clone as into local directory and import as simple java project.
Run the LuceneDemo.java file as Simple java Application

Pre-Requisites:
Java 1.8 and above

Libraries Used:
1. Apache Lucene lucene-core-8.2.0 - For indexing and searching text document
2. jsoup-1.12.1 - For parsing HTML document and pick only Body contents

Running the Test:
1. Test by giving different queries in LuceneDemo.java file according to the contents present in document corpus(data folder)
2. Based on similarity with the query, the documents are given relevance score(Computed using Cosine Similarity). 
   These documents are ranked according to the similarity scores and it is displayed.
