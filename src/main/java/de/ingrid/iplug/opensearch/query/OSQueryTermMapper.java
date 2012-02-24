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
