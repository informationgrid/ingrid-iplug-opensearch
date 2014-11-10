/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
package de.ingrid.iplug.opensearch.query;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.ingrid.iplug.opensearch.model.OSMapping;
import de.ingrid.iplug.opensearch.model.OSMapping.IngridFieldType;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/spring.xml"})
public class OSQueryBuilderTest {
    
    @Autowired
    private OSQueryBuilder qb;
    
    @Test
    public void createQueryAND() {
        assertEquals("wasser", makeOSQueryFromQuery("wasser").get(OSQuery.OS_SEARCH_TERMS));
        assertEquals("wasser+baum", makeOSQueryFromQuery("wasser baum").get(OSQuery.OS_SEARCH_TERMS));
    }
    
    @Test
    public void createQueryOR() {
        assertEquals("wasser+OR+Baum", makeOSQueryFromQuery("wasser OR Baum").get(OSQuery.OS_SEARCH_TERMS));
        assertEquals("wasser+OR+Baum+OR+Wald", makeOSQueryFromQuery("wasser OR Baum OR Wald").get(OSQuery.OS_SEARCH_TERMS));
    }
    
    @Test
    public void createQueryANDOR() {
        assertEquals("wasser+wald+OR+Baum", makeOSQueryFromQuery("wasser wald OR Baum").get(OSQuery.OS_SEARCH_TERMS));
        assertEquals("wasser+OR+Baum+Wald", makeOSQueryFromQuery("wasser OR Baum Wald").get(OSQuery.OS_SEARCH_TERMS));
    }
    
    @Test
    public void createQueryNOT() {
        assertEquals("-wasser", makeOSQueryFromQuery("-wasser").get(OSQuery.OS_SEARCH_TERMS));
        assertEquals("-wasser", makeOSQueryFromQuery("NOT wasser").get(OSQuery.OS_SEARCH_TERMS));
        assertEquals("wasser+-Baum", makeOSQueryFromQuery("wasser -Baum").get(OSQuery.OS_SEARCH_TERMS));
        assertEquals("wasser+-Baum", makeOSQueryFromQuery("wasser NOT Baum").get(OSQuery.OS_SEARCH_TERMS));
    }
    
    @Test
    public void createQueryPhrase() {
        assertEquals("%22wasser+baum%22", makeOSQueryFromQuery("\"wasser baum\"").get(OSQuery.OS_SEARCH_TERMS));
        assertEquals("-%22wasser+baum%22", makeOSQueryFromQuery("-\"wasser baum\"").get(OSQuery.OS_SEARCH_TERMS));
        assertEquals("wald+%22wasser+baum%22", makeOSQueryFromQuery("wald \"wasser baum\"").get(OSQuery.OS_SEARCH_TERMS));
        assertEquals("wald+OR+%22wasser+baum%22", makeOSQueryFromQuery("wald OR \"wasser baum\"").get(OSQuery.OS_SEARCH_TERMS));
    }
    
    @Test
    public void createQueryALL() {
        assertEquals("baum+wald+-wasser", makeOSQueryFromQuery("baum wald -wasser").get(OSQuery.OS_SEARCH_TERMS));
        assertEquals("baum+OR+wald+-wasser", makeOSQueryFromQuery("baum OR wald -wasser").get(OSQuery.OS_SEARCH_TERMS));
    }
    
    @Test
    public void createQueryNestedBoolean() {
        assertEquals("baum+wald+-wasser", makeOSQueryFromQuery("baum (wald -wasser)").get(OSQuery.OS_SEARCH_TERMS));
        assertEquals("baum+OR+wald+-wasser", makeOSQueryFromQuery("baum OR (wald -wasser)").get(OSQuery.OS_SEARCH_TERMS));
        assertEquals("baum+-wald+wasser", makeOSQueryFromQuery("baum NOT (wald -wasser)").get(OSQuery.OS_SEARCH_TERMS));
    }

    @Test
    public void createQueryMapped() {
        List<OSMapping> mapping = new ArrayList<OSMapping>();
        OSMapping siteMap     = new OSMapping();siteMap.setActive(true);siteMap.setType(IngridFieldType.DOMAIN);siteMap.setMapping("mySite");
        OSMapping partnerMap  = new OSMapping();partnerMap.setActive(true);partnerMap.setType(IngridFieldType.PARTNER);partnerMap.setMapping("myPartner");
        OSMapping providerMap = new OSMapping();providerMap.setActive(true);providerMap.setType(IngridFieldType.PROVIDER);providerMap.setMapping("myProvider");
        mapping.add(siteMap);
        mapping.add(partnerMap);
        mapping.add(providerMap);
        
        assertEquals("wasser", makeOSQueryFromQuery("wasser site:www.wemove.com").get(OSQuery.OS_SEARCH_TERMS));
        assertEquals("wasser+mySite:www.wemove.com", makeOSQueryFromQuery("wasser site:www.wemove.com", mapping).get(OSQuery.OS_SEARCH_TERMS));
        
        assertEquals("wasser", makeOSQueryFromQuery("wasser partner:bb").get(OSQuery.OS_SEARCH_TERMS));
        assertEquals("wasser+myPartner:bb", makeOSQueryFromQuery("wasser partner:bb", mapping).get(OSQuery.OS_SEARCH_TERMS));
        
        assertEquals("wasser", makeOSQueryFromQuery("wasser provider:you").get(OSQuery.OS_SEARCH_TERMS));
        assertEquals("wasser+myProvider:you", makeOSQueryFromQuery("wasser provider:you", mapping).get(OSQuery.OS_SEARCH_TERMS));
        
        siteMap.setMapping("[]");
        partnerMap.setMapping("[]");
        providerMap.setMapping("[]");        
        assertEquals("wasser+www.wemove.com", makeOSQueryFromQuery("wasser site:www.wemove.com", mapping).get(OSQuery.OS_SEARCH_TERMS));
        assertEquals("wasser+bb", makeOSQueryFromQuery("wasser partner:bb", mapping).get(OSQuery.OS_SEARCH_TERMS));
        assertEquals("wasser+you", makeOSQueryFromQuery("wasser provider:you", mapping).get(OSQuery.OS_SEARCH_TERMS));
        // inactive and other parameters should be ignored
        partnerMap.setActive(false);
        assertEquals("wasser", makeOSQueryFromQuery("wasser partner:bb", mapping).get(OSQuery.OS_SEARCH_TERMS));
        assertEquals("wasser+you", makeOSQueryFromQuery("wasser dummy:ignore provider:you", mapping).get(OSQuery.OS_SEARCH_TERMS));
    }

    private final OSQuery makeOSQueryFromQuery(String searchTerms) {
        return makeOSQueryFromQuery(searchTerms, null);
    }
    
    private final OSQuery makeOSQueryFromQuery(String searchTerms, List<OSMapping> mapping) {
        IngridQuery query = null;
        try {
            query = QueryStringParser.parse(searchTerms);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        if (mapping == null)
            mapping = new ArrayList<OSMapping>();
        
        //OSQueryBuilder qb = new OSQueryBuilder();
        //qb.setTermMapper(termMapper);
        return qb.createQuery(query, 0, 10, mapping);
    }
}
