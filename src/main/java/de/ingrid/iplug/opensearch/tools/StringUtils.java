/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
package de.ingrid.iplug.opensearch.tools;

/**
 * This class offers useful functions working with String-objects.
 * @author André Wallat
 *
 */
public class StringUtils {
	/**
	 * Checks if a text string contains a parameter, which also might be optional. 
	 * @param text, the string to search for a parameter
	 * @param parameter, the parameter to look for
	 * @return true, if parameter was found (with or without '?', which marks a parameter optional) 
	 */
	public static boolean parameterExists(String text, String parameter) {
		if (text.indexOf("{"+parameter+"}") != -1 || text.indexOf("{"+parameter+"?}") != -1 ) {
			return true;
		}
		return false;
	}
	
	/**
	 * This function is used to replace a parameter in the style of an 
	 * Opensearch-template-URL.
	 * @param text
	 * @param parameter
	 * @param insert
	 * @return
	 */
	public static String replaceParameter(String text, String parameter, String insert) {
		if (insert == null) {
			return text;
		}
		
		String pattern = "\\{"+parameter+"\\??\\}";
		String res = text.replaceAll(pattern, insert);
		return res;
		
	}

	/**
	 * Remove all parameter from a template-URL where no replacement 
	 * was offered.
	 * @param query
	 * @return
	 */
	public static String removeUnusedParameter(String query) {
		// this pattern represents:
		// [in front or middle      ] [at the end of the query]
		// xxx={yyy}& | xxx={yyy?}& | &xxx={yyy}& | &xxx={yyy?}
		// where in "yyy" no "&" must occur!
		String pattern = "(\\w*=\\{[^&]*\\??\\}&?|&\\w*=\\{[^&]*\\??\\})";
		String res = query.replaceAll(pattern, "");
		return res;
	}
}
