/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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
