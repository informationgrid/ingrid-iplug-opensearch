/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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

import java.util.Map;

import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;

/**
 * This class is responsible for analysing a search term and adding keywords for
 * AND, OR and NOT, which can be configured via Spring.
 * 
 * @author Andre
 * 
 */
public class OSQueryTermMapper {

    private static final String NOT = "NOT";
    private static final String AND = "AND";
    private static final String OR = "OR";

    // injected by Spring
    private Map<String, String> mapping;

    public String map(TermQuery term, int pos, IngridQuery q) {
        String mappedTerm = "";
        boolean isInvertedQuery = false;
        try {
            if (!q.isRequred() && pos == 1) {
                mappedTerm += mapping.get(OR);
            }
        } catch (Exception e) {
        }
        try {
            if (q.isProhibited()) {
                if (pos == 1) {
                    mappedTerm += mapping.get(NOT);
                }
                isInvertedQuery = true;
            }
        } catch (Exception e) {
        }
        if (term.get("constructorValuesAsString").toString().startsWith("false")) {
            mappedTerm += isInvertedQuery ? mapping.get(OR) + mapping.get(NOT) : mapping.get(OR);
        } else if (term.isProhibited()) {
            mappedTerm += isInvertedQuery ? mapping.get(AND) : mapping.get(NOT);
        } else if ((pos > 1 || mappedTerm.length() > 0)) {
            mappedTerm += isInvertedQuery ? (pos==1) ? "": mapping.get(NOT) : mapping.get(AND);
        }
        String t = term.getContent().toString();
        if (t.contains(" ")) {
            return mappedTerm + "\"" + term.getContent().toString() + "\"";
        } else {
            return mappedTerm + term.getContent().toString();
        }

    }

    public void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }
}
