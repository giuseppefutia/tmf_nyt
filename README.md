# TMF NYTimes Enhancer

This repository contains the New York Times enhancement module of [TellMeFirst](https://github.com/TellMeFirst/TellMeFirst).

This module directly interacts with the [DBpedia Sparql endpoint](http://dbpedia.org/sparql) and ["The New York Times" semantic APIs](http://developer.nytimes.com/docs/semantic_api) in order to retrieve news related to the results of the classification system of TellMeFirst.

## API usage

After building this module with Maven, you can invoke getNewsFromNYTimes().

	NYTimesEnhancer nyTimesEnhancer = new NYTimesEnhancer();
	String dbpediaURI = "http://dbpedia.org/resource/Facebook";
	String label = "Facebook";
	String results = nyTimesEnhancer.getNewsFromNYTimes(dbpediaURI, label);

The first parameter of getNewsFromNYTimes() is the DBpedia URI, while the second one is a label produced by TellMeFirst from the Wikipedia title of the page (both these parameters are included in the results of TellMeFirst classification system).
