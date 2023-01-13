/*
 * **************************************************-
 * ingrid-iplug-opensearch:war
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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

import java.io.InputStream;
import java.util.List;

import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;

/**
 * The interface of how a converter has to look-a-like.
 * @author André Wallat
 *
 */
public interface IngridConverter {
	public IngridHits processResult(String plugId, InputStream result, String groupedBy);
	
	public void setRankingModifiers(List<RankingModifier> normalizers);
	
	public IngridHitDetail getHitDetailFromCache(String docId);
}
