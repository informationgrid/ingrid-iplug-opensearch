<%--
  **************************************************-
  ingrid-iplug-opensearch:war
  ==================================================
  Copyright (C) 2014 - 2023 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.1 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  http://ec.europa.eu/idabc/eupl5
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the Licence is distributed on an "AS IS" basis,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licence for the specific language governing permissions and
  limitations under the Licence.
  **************************************************#
  --%>
<%@ include file="/WEB-INF/jsp/base/include.jsp" %>
<c:choose>
    <c:when test="${plugdescriptionExists == 'false'}">
        <li
        <c:if test="${active == 'extras'}">
            class="active"
        </c:if>
        >Weitere Einstellungen</li>
    </c:when>
    <c:when test="${active != 'extras'}">
        <li><a href="../base/extras.html">Weitere Einstellungen</a></li>
    </c:when>
    <c:otherwise>
        <li class="active">Weitere Einstellungen</li>
    </c:otherwise>
</c:choose>
<c:choose>
    <c:when test="${plugdescriptionExists == 'false'}">
        <li
        <c:if test="${active == 'osParams'}">
            class="active"
        </c:if>
        >Opensearch Parameter</li>
    </c:when>
    <c:when test="${active != 'osParams'}">
        <li><a href="../iplug-pages/osParams.html">Opensearch Parameter</a></li>
    </c:when>
    <c:otherwise>
        <li class="active">Opensearch Parameter</li>
    </c:otherwise>
</c:choose>
<c:choose>
    <c:when test="${plugdescriptionExists == 'false'}">
        <li
        <c:if test="${active == 'osIngridMapping'}">
            class="active"
        </c:if>
        >Opensearch Mapping</li>
    </c:when>
    <c:when test="${active != 'osIngridMapping'}">
        <li><a href="../iplug-pages/osIngridMapping.html">Opensearch Mapping</a></li>
    </c:when>
    <c:otherwise>
        <li class="active">Opensearch Mapping</li>
    </c:otherwise>
</c:choose>


