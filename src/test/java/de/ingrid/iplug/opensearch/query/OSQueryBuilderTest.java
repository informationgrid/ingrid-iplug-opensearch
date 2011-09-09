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
    
    

    private final OSQuery makeOSQueryFromQuery(String searchTerms) {
        IngridQuery query = null;
        try {
            query = QueryStringParser.parse(searchTerms);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        List<OSMapping> mapping = new ArrayList<OSMapping>();
        
        //OSQueryBuilder qb = new OSQueryBuilder();
        //qb.setTermMapper(termMapper);
        return qb.createQuery(query, 0, 10, mapping);
    }
}
