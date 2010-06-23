package de.ingrid.iplug.opensearch.tools;

import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;

public class QueryUtils {
    
    /**
     * This function returns the value to a specific field of an IngridQuery.
     * 
     * @param field
     * @param ingridQuery
     * @return
     */
    public static String getFieldValue(String field, IngridQuery ingridQuery) {
        for (FieldQuery fieldQuery : ingridQuery.getFields()) {
            if (field.equals(fieldQuery.getFieldName())) {
                return fieldQuery.getFieldValue();
            }            
        }
        return null;
    }
}
