/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package analysePortalU;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.ingrid.utils.xml.ConfigurableNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

public class AnalysePortalUData {

    public static int PAGE_SIZE = 50;

    public void analyse() throws HttpException, IOException, ParserConfigurationException, SAXException, TransformerException {

        Map<String, Map<String, String>> resultMap = new HashMap<String, Map<String, String>>();

        Scanner in = new Scanner(getClass().getClassLoader().getResourceAsStream("keywords.txt"));
        while (in.hasNextLine()) {
            String keyword = URLEncoder.encode(in.nextLine().trim(), "UTF-8");

            int currentPage = 1;
            boolean moreResults = true;

            while (moreResults) {

                String url = "http://www.portalu.de/opensearch/query?q=" + keyword.replace(' ', '+') + "+datatype:metadata+ranking:score&h=" + PAGE_SIZE + "&detail=1&ingrid=1&p=" + currentPage;

                HttpClientParams httpClientParams = new HttpClientParams();
                HttpConnectionManager httpConnectionManager = new SimpleHttpConnectionManager();
                httpClientParams.setSoTimeout(60 * 1000);
                httpConnectionManager.getParams().setConnectionTimeout(60 * 1000);
                httpConnectionManager.getParams().setSoTimeout(60 * 1000);

                HttpClient client = new HttpClient(httpClientParams, httpConnectionManager);
                HttpMethod method = new GetMethod(url);

                // set a request header
                // this can change in the result of the response since it might
                // be
                // interpreted differently
                // method.addRequestHeader("Accept-Language", language);
                // //"de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4");

                System.out.println("Query: " + url);
                int status = client.executeMethod(method);
                if (status == 200) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(method.getResponseBodyAsStream());
                    XPathUtils xpath = new XPathUtils(new ConfigurableNamespaceContext());
                    NodeList results = xpath.getNodeList(doc, "/rss/channel/item");
                    int numberOfResults = results.getLength();

                    for (int i = 0; i < results.getLength(); i++) {
                        Node node = results.item(i);
                        String fileIdentifier = xpath.getString(node, ".//*/fileIdentifier/CharacterString");
                        if (!resultMap.containsKey(fileIdentifier)) {
                            resultMap.put(fileIdentifier, new HashMap<String, String>());
                        }
                        Map<String, String> currentMap = resultMap.get(fileIdentifier);
                        currentMap.put("uuid", fileIdentifier);
                        currentMap.put("partner", xpath.getString(node, "partner"));
                        currentMap.put("provider", xpath.getString(node, "provider"));
                        currentMap.put("udk-class", xpath.getString(node, "udk-class"));
                        currentMap.put("source", xpath.getString(node, "source"));
                        currentMap.put("url", new URL(xpath.getString(node, "link")).toString());
                        currentMap.put("title", xpath.getString(node, ".//*/title/CharacterString"));
                        currentMap.put("description", xpath.getString(node, ".//*/abstract/CharacterString"));
                        Node addressNode = xpath.getNode(node, ".//*/contact/idfResponsibleParty");
                        String addressString = "";
                        String tmp = xpath.getString(addressNode, "indiviualName/CharacterString");
                        if (tmp != null && tmp.length() > 0)
                            addressString += tmp + "\n";

                        tmp = xpath.getString(addressNode, "organisationName/CharacterString");
                        if (tmp != null && tmp.length() > 0)
                            addressString += tmp + "\n";

                        tmp = xpath.getString(addressNode, "contactInfo/CI_Contact/address/CI_Address/deliveryPoint/CharacterString");
                        if (tmp != null && tmp.length() > 0)
                            addressString += tmp + "\n";

                        tmp = xpath.getString(addressNode, "contactInfo/CI_Contact/address/CI_Address/postalCode/CharacterString");
                        if (tmp != null && tmp.length() > 0)
                            addressString += tmp + " ";
                        tmp = xpath.getString(addressNode, "ontactInfo/CI_Contact/address/CI_Address/city/CharacterString");
                        if (tmp != null && tmp.length() > 0)
                            addressString += tmp + "\n";

                        tmp = xpath.getString(addressNode, "contactInfo/CI_Contact/address/CI_Address/country/CharacterString");
                        if (tmp != null && tmp.length() > 0)
                            addressString += tmp + "\n";

                        tmp = xpath.getString(addressNode, "contactInfo/CI_Contact/address/CI_Address/electronicMailAddress/CharacterString");
                        if (tmp != null && tmp.length() > 0)
                            addressString += "Email: " + tmp + "\n";

                        tmp = xpath.getString(addressNode, "contactInfo/CI_Contact/phone/CI_Telephone/voice/CharacterString");
                        if (tmp != null && tmp.length() > 0)
                            addressString += "Tel: " + tmp + "\n";

                        tmp = xpath.getString(addressNode, "contactInfo/CI_Contact/phone/CI_Telephone/facsimile/CharacterString");
                        if (tmp != null && tmp.length() > 0)
                            addressString += "Fax: " + tmp + "\n";

                        currentMap.put("pointOfContact", addressString);
                    }
                    if (numberOfResults > 0 && numberOfResults >= PAGE_SIZE) {
                        currentPage++;
                    } else {
                        moreResults = false;
                    }
                } else {
                    moreResults = false;
                }
            }

        }

        StringWriter sw = new StringWriter();
        ExcelCSVPrinter ecsvp = new ExcelCSVPrinter(sw);
        boolean fieldsWritten = false;
        for (String key : resultMap.keySet()) {
            Map<String, String> result = resultMap.get(key);
            if (!fieldsWritten) {
                for (String field : result.keySet()) {
                    ecsvp.print(field);
                }
                ecsvp.println("");
                fieldsWritten = true;
            }
            for (String value : result.values()) {
                ecsvp.print(value);
            }
            ecsvp.println("");
        }

        PrintWriter out = new PrintWriter("result.csv");
        out.write(sw.toString());
        out.close();
        in.close();
        
        System.out.println("Done.");
    }

    public static void main(String[] args) throws Exception {

        AnalysePortalUData app = new AnalysePortalUData();
        app.analyse();
    }

}
