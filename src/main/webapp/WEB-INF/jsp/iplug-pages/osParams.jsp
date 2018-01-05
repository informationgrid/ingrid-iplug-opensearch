<%--
  **************************************************-
  ingrid-iplug-opensearch:war
  ==================================================
  Copyright (C) 2014 - 2018 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.1 or â€“ as soon they will be
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
<%@ include file="/WEB-INF/jsp/base/include.jsp" %><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@page import="de.ingrid.admin.security.IngridPrincipal"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title><fmt:message key="OpensearchConfig.main.title"/></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
<link rel="StyleSheet" href="../css/base/portal_u.css" type="text/css" media="all" />

<script type="text/javascript">

    function setListener() {    
        rankingField = document.getElementById("ranking");
        rankingField.onchange = function() { checkRanking(this); }
        checkRanking(rankingField);
    }
    
    function checkRanking(obj) {
        if (obj.checked == true) {
            document.getElementById("rankMul").style.display = "";
            document.getElementById("rankAdd").style.display = "";
        } else {
            document.getElementById("rankMul").style.display = "none";
            document.getElementById("rankAdd").style.display = "none";
        }
    }
</script>

</head>
<body onLoad="setListener()">
    <div id="header">
        <img src="../images/base/logo.gif" width="168" height="60" alt="Portal U" />
        <h1><fmt:message key="OpensearchConfig.main.configuration"/></h1>
        <%
          java.security.Principal  principal = request.getUserPrincipal();
          if(principal != null && !(principal instanceof IngridPrincipal.SuperAdmin)) {
        %>
            <div id="language"><a href="../base/auth/logout.html"><fmt:message key="OpensearchConfig.main.logout"/></a></div>
        <%
          }
        %>
    </div>
    <div id="help"><a href="#">[?]</a></div>

    <c:set var="active" value="osParams" scope="request"/>
    <c:import url="../base/subNavi.jsp"></c:import>

    <div id="contentBox" class="contentMiddle">
        <h1 id="head">Opensearch Parameter</h1>
        <div class="controls">
            <a href="../base/extras.html">Zurück</a>
            <a href="../base/welcome.html">Abbrechen</a>
            <a href="#" onclick="document.getElementById('osConfig').submit();">Weiter</a>
        </div>
        <div class="controls cBottom">
            <a href="../base/extras.html">Zurück</a>
            <a href="../base/welcome.html">Abbrechen</a>
            <a href="#" onclick="document.getElementById('osConfig').submit();">Weiter</a>
        </div>
        <div id="content">
            <form:form method="post" action="osParams.html" modelAttribute="osConfig">
                <input type="hidden" name="action" value="submit" />
                <input type="hidden" name="id" value="" />
                <table id="konfigForm">
                    <br />
                    <tr>
                        <td colspan="2"><h3>Auswahl der Quelle:</h3></td>
                    </tr>
                    <tr>
                        <td><form:radiobutton path="osDescriptor" value="descriptor" /> Deskriptor</td>
                        <td>
                            <div class="input full"><form:input path="opensearchDescriptorUrl" /></div>
                            <form:errors path="opensearchDescriptorUrl" cssClass="error" element="div" />
                            <br />
                            Bitte geben Sie hier die URL des Opensearch-Deskriptors an, die Sie an das InGrid System anschließen wollen.
                        </td>
                    </tr>
                    <tr>
                        <td><form:radiobutton path="osDescriptor" value="url" /> Url</td>
                        <td>
                            <div class="input full"><form:input path="opensearchUrl" /></div>
                            <form:errors path="opensearchUrl" cssClass="error" element="div" />
                            <br />
                            Bitte geben Sie hier die Opensearch URL mit Platzhaltern an, die Sie an das InGrid System anschließen wollen.
                            Verfügbare Platzhalter sind {searchTerms}, {geo:box}, {startPage}, {count}, {ingridsearch:xml},
                            {ingridsearch:georss}, {ingridsearch:ingrid}
                            <p style="color: gray;">(Beispiel: http://127.0.0.1/query?q={searchTerms}+datatype:default+ranking:score&amp;bbox={geo:box?}&amp;p={startPage?}&amp;h={count?}&amp;xml={ingridsearch:xml?}&amp;georss={ingridsearch:georss?}&amp;ingrid={ingridsearch:ingrid?}&amp;format=rss")</p>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2"><form:errors path="osDescriptor" cssClass="error" element="div" /></td>
                    </tr>
                    <tr>
                        <td colspan="2"><h3>Ranking Parameter:</h3></td>
                    </tr>
                    <tr>
                        <td class="leftCol">Ranking Unterstützung:</td>
                        <td>
                            <form:checkbox path="rankSupport" id="ranking" />
                            <br />
                            Unterstützt die Opensearch Schnittstelle ein Ranking der Suchergebnisse? (Wenn aktiviert, 
                            werden die Ergebnisse in die Hauptergebnisliste des Portals aufgenommen.)
                        </td>
                    </tr>
                    <tr id="rankMul">
                        <td class="leftCol">Adapt. Ranking (Mult.):</td>
                        <td>
                            <div class="input full"><form:input path="rankMultiplier" id="rankMultiplier" /></div>
                            <form:errors path="rankMultiplier" cssClass="error" element="div" />
                            <br />
                            Bitte geben Sie hier den Faktor an, mit dem der ranking score multipliziert werden soll.
                        </td>
                    </tr>
                    <tr id="rankAdd">
                        <td class="leftCol">Adapt. Ranking (Add.):</td>
                        <td>
                            <div class="input full"><form:input path="rankAddition" id="rankAddition" /></div>
                            <form:errors path="rankAddition" cssClass="error" element="div" />
                            <br />
                            Bitte geben Sie hier den Faktor an, mit dem der ranking score addiert werden soll.
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            Weitere Möglichkeiten zur Manipulation des ranking score bestehen in der Aktivierung zusätzlicher RankingModifier in der Datei ./webapp/WEB-INF/spring.xml
                        </td>
                    </tr>
                </table>
            </form:form>
        </div>
    </div>

    <div id="footer" style="height:100px; width:90%"></div>
</body>
</html>

