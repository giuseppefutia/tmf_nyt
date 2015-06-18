# TMF NYTimes Enhancer

This repository contains the New York Times enhancement module of [TellMeFirst](https://github.com/TellMeFirst/TellMeFirst).

This module directly interacts with the [DBpedia Sparql endpoint](http://dbpedia.org/sparql) and ["The New York Times" semantic APIs](http://developer.nytimes.com/docs/semantic_api in order to retrieve news related to the results of the classification system of TellMeFirst.

## API usage

Once you bulid this module with Maven, you can invoke getNewsFromNYTimes().

``` java

	NYTimesEnhancer nyTimesEnhancer = new NYTimesEnhancer();
    String results = nyTimesEnhancer.getNewsFromNYTimes("http://dbpedia.org/resource/Barack_Obama");

```

The parameter of getNewsFromNYTimes() is a DBpedia URI.
