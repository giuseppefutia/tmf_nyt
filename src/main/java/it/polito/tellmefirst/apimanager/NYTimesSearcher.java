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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class NYTimesSearcher {

    static Log LOG = LogFactory.getLog(NYTimesSearcher.class);
    final static String NYT_API = "http://api.nytimes.com/svc/semantic/v2/concept/name/";
    final static String API_KEY = "8eb6657c2a9c0793224fb4e27652cc51:1:63640734";

    public String getSearchApiQuery(String prefLabel, String type){

        LOG.debug("[getSearchApiQuery] - BEGIN");

        String result = "";
        String query = null;
        try {
            query = type + "/" + URLEncoder.encode(prefLabel, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        result = NYT_API + query + ".json?fields=article_list&api-key="+API_KEY;

        LOG.debug("[getSearchApiQuery] - END");
        return result;
    }

    // just for testing
    public static void main(String[] args){
        NYTimesSearcher nyTimesSearcher = new NYTimesSearcher();
        String s = nyTimesSearcher.getSearchApiQuery("Obama, Barack", "nytd_per");
        System.out.println("Search Query: " + s);
    }
}
