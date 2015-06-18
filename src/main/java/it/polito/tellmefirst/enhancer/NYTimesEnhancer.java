/*
* TellMeFirst - A Knowledge Discovery Application
*
* Copyright (C) 2015 Giuseppe Futia
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package it.polito.tellmefirst.enhancer;


import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import it.polito.tellmefirst.apimanager.NYTimesSearcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class NYTimesEnhancer {

    static Log LOG = LogFactory.getLog(NYTimesEnhancer.class);

    private NYTimesSearcher nyTimesSearcher;
    private static final String DBPEDIA_ENGLISH_ENDPOINT = "http://dbpedia.org/sparql";

    public NYTimesEnhancer() {
        nyTimesSearcher = new NYTimesSearcher();
    }

    private String getResultFromAPI(String urlStr, String type) {

        LOG.debug("[getResultFromAPI] - BEGIN");
        String result = "";
        try{
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept", type);
            if (conn.getResponseCode() != 200) {
                System.out.println(conn.getResponseMessage());
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
            result = sb.toString();
        }catch (Exception e){
            LOG.error("[getResultFromAPI] - EXCEPTION: ", e);
        }
        LOG.debug("[getResultFromAPI] - END");
        return result;
    }

    private ResultSet executeSparqlQuery(String queryString, String endpoint) {

        LOG.debug("[executeSparqlQuery] - BEGIN");

        Query query = QueryFactory.create(queryString);
        QueryExecution querex = QueryExecutionFactory.sparqlService(endpoint, query);
        ResultSet results = querex.execSelect();
        querex.close() ;

        LOG.debug("[executeSparqlQuery] - END");

        return results;

    }

    private String getNYTimesURIfromDBpedia(String dbpediaUri) {

        LOG.debug("[getNYTimesURIfromDBpedia] - BEGIN");

        String result = "";
        try {
            String cleanUri = dbpediaUri.replace("%28","(").replace("%29", ")").replace("%27", "'");
            ResultSet resultSet = executeSparqlQuery("select ?sameas where {" +
                    "?sameas <http://www.w3.org/2002/07/owl#sameAs> <" + cleanUri + "> ." +
                    "}", DBPEDIA_ENGLISH_ENDPOINT);
            while (resultSet.hasNext()) {
                QuerySolution row= resultSet.next();
                String uri = row.get("sameas").toString();
                if(uri.contains("data.nytimes.com")){
                    LOG.debug("[getNytUri] - END");
                    return uri;
                }
            }
        }catch (Exception e){
            LOG.error("[getNYTimesURIfromDBpedia] - EXCEPTION: ", e);
        }

        LOG.debug("[getNYTimesURIfromDBpedia] - END");

        return result;

    }

    public String getNewsFromNYTimes(String uri) {

        LOG.debug("[getNewsFromNYTimes] - BEGIN");

        String result = "";
        String prefLabel = "";
        String type = "";

        String nytUri = getNYTimesURIfromDBpedia(uri);
        String rdfEntity = getResultFromAPI(nytUri, "application/rdf+xml");
        Model model = ModelFactory.createDefaultModel();
        InputStream is = new ByteArrayInputStream(rdfEntity.getBytes());
        model.read(is, null);
        StmtIterator statements = model.listStatements();
        while (statements.hasNext()) {
            Statement st = statements.next();
            if(st.getPredicate().asResource().toString().equals("http://www.w3.org/2004/02/skos/core#prefLabel")) {
                prefLabel = st.getObject().asLiteral().getString();
            }
            if(st.getPredicate().asResource().toString().equals("http://www.w3.org/2004/02/skos/core#inScheme")) {
                type = st.getObject().asResource().toString().split("http://data.nytimes.com/elements/")[1];
            }
        }

        if(nytUri.equals("")){
            result = "{\"offset\" : \"0\" , \"results\" : [] , \"total\" : 0}";
        } else {
            String search = nyTimesSearcher.getSearchApiQuery(prefLabel, type);
            result = getResultFromAPI(search, "");
        }
        LOG.debug("[getNewsFromNYTimes] - END");

        return result;
    }

    // just for testing
    public static void main(String[] args){
        NYTimesEnhancer nyTimesEnhancer = new NYTimesEnhancer();
        String results = nyTimesEnhancer.getNewsFromNYTimes("http://dbpedia.org/resource/Barack_Obama");
    }

}
