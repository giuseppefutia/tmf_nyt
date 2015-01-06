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
import it.polito.tellmefirst.apimanager.NYTimesSearcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NYTimesEnhancer {

    static Log LOG = LogFactory.getLog(NYTimesEnhancer.class);

    private NYTimesSearcher nyTimesSearcher;
    private static final String DBPEDIA_ENGLISH_ENDPOINT = "http://dbpedia.org/sparql";

    public NYTimesEnhancer() {
        nyTimesSearcher = new NYTimesSearcher();
    }

    private String getResultFromAPI(String urlStr){
        LOG.debug("[getResultFromAPI] - BEGIN");
        String result = "";
        try{
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() != 200) {
                throw new IOException(conn.getResponseMessage());
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
        //ResultSetFormatter.out(System.out, results);
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

    public String getNewsFromNYTimes(String uri, String label) {

        LOG.debug("[getNewsFromNYTimes] - BEGIN");

        String result;
        String nytUri = getNYTimesURIfromDBpedia(uri);
        if(nytUri.equals("")){
            result = "{\"offset\" : \"0\" , \"results\" : [] , \"total\" : 0}";
        } else {
            String search = nyTimesSearcher.getSearchApiQuery(nytUri, label);
            result = getResultFromAPI(search);
        }
        LOG.debug("[getNewsFromNYTimes] - END");

        return result;
    }

    // just for testing
    public static void main(String[] args){
        NYTimesEnhancer nyTimesEnhancer = new NYTimesEnhancer();
        String results = nyTimesEnhancer.getNewsFromNYTimes("http://dbpedia.org/resource/Facebook", "Facebook");
        System.out.println(results);
    }

}
