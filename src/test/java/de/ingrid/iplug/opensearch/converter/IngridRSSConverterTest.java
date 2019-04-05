/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
package de.ingrid.iplug.opensearch.converter;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import de.ingrid.utils.IngridHits;

public class IngridRSSConverterTest extends TestCase {

    private final String XML_INPUT_FILE = "response_example2.xml";
    private final String XML_INPUT_FILE_EUROPEANA = "response_europeana.xml";

    IngridRSSConverter ingridConverter = new IngridRSSConverter();

    @Override
    protected void setUp() throws Exception {
        List<RankingModifier> l = new ArrayList<RankingModifier>();
        l.add(new RankingModifierFromPD());
        ingridConverter.setRankingModifiers(l);
        super.setUp();
    }

    public final void testProcessResult() {
        try {
            Resource resource = new ClassPathResource(XML_INPUT_FILE);
            IngridHits hits = ingridConverter.processResult("bla", resource.getInputStream(), null);
            assertTrue(hits.getHits().length > 0);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed.");
        }
    }

    public final void testProcessResultFromEuropeana() {
        try {
            Resource resource = new ClassPathResource(XML_INPUT_FILE_EUROPEANA);
            IngridHits hits = ingridConverter.processResult("bla", resource.getInputStream(), null);
            assertTrue(hits.getHits().length > 0);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed.");
        }
    }
}
