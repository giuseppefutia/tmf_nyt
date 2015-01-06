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

package it.polito.tellmefirst.apimanager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NYTimesSearcher {

    static Log LOG = LogFactory.getLog(NYTimesSearcher.class);
    final static String NYT_API = "http://api.nytimes.com/svc/semantic/v2/concept/search";
    final static String API_KEY = "8eb6657c2a9c0793224fb4e27652cc51:1:63640734";

    public String getSearchApiQuery(String uri, String dbpediaLabel){

        LOG.debug("[getSearchApiQuery] - BEGIN");

        String result = "";
        String uriId = uri.split("http://data.nytimes.com/")[1];
        String query = "query="+dbpediaLabel+"&concept_uri="+uriId;
        result = NYT_API+".json?fields=search_api_query&"+query+"&api-key="+API_KEY;

        LOG.debug("[getSearchApiQuery] - END");
        return result;
    }

    // just for testing
    public static void main(String[] args){
        NYTimesSearcher nyTimesSearcher = new NYTimesSearcher();
        String s = nyTimesSearcher.getSearchApiQuery("http://data.nytimes.com/86043020378633512412", "Facebook");
        System.out.println("Search Query: " + s);
    }
}
