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
<%@ include file="/WEB-INF/jsp/base/include.jsp" %><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

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
        mappingField = document.getElementById("mapping");
        mappingField.onchange = function() { checkMapping(this); }
        checkMapping(mappingField);
    }
    
    function checkMapping(obj) {
        if (obj.checked == true) {
            document.getElementById("konfigForm").style.display = "block";
        } else {
            document.getElementById("konfigForm").style.display = "none";
        }
    }
</script>

</head>

<body onLoad="setListener()">

	<div id="header">
        <img src="../images/base/logo.gif" width="168" height="60" alt="Portal" />
        <h1><fmt:message key="OpensearchConfig.main.configuration"/></h1>
        <security:authorize access="isAuthenticated()">
            <div id="language"><a href="../base/auth/logout.html"><fmt:message key="OpensearchConfig.main.logout"/></a></div>
        </security:authorize>
    </div>
    <div id="help"><a href="#">[?]</a></div>

    <c:set var="active" value="osIngridMapping" scope="request"/>
    <c:import url="../base/subNavi.jsp"></c:import>

    <div id="contentBox" class="contentMiddle">
    	<h1 id="head">Opensearch Mapping</h1>
        <div class="controls">
            <a href="../iplug-pages/osParams.html">Zur�ck</a>
            <a href="../base/welcome.html">Abbrechen</a>
            <a href="#" onclick="document.getElementById('mapConfig').submit();">Weiter</a>
        </div>
        <div class="controls cBottom">
            <a href="../iplug-pages/osParams.html">Zur�ck</a>
            <a href="../base/welcome.html">Abbrechen</a>
            <a href="#" onclick="document.getElementById('mapConfig').submit();">Weiter</a>
        </div>
        <div id="content">
        	<form:form method="post" action="osIngridMapping.html" modelAttribute="mapConfig">
				<table>
	            	<tr>
                        <td colspan="2"><h3>Eigenschaften der Quelle:</h3></td>
                    </tr>
                    <tr>
                        <td width="180px" style="vertical-align:top;">Mapping-Unterst�tzung</td>
                        <td>
                        	<form:checkbox path="useMapping" id="mapping" />
                            <br />
                            Aktivieren Sie diese Checkbox, wenn die anzuschlie�ende Opensearch-Schnittstelle die Gruppierung
                            von Ergebnissen unterst�tzt. Es ist dann m�glich nach Dom�ne, Partner und Anbieter zu gruppieren.
                        </td>
                    </tr>
                </table>
                <br />
                <table id="konfigForm">
    				<thead>
    					<tr>
    						<th width="140px" align="left">Mapping aktivieren</th>
    						<th align="left">zu mappen auf:</th>
    						<th width="175px" align="left">als Parameter hinzuf�gen</th>
    					</tr>
    				</thead>
    				<tr>
    					<td><form:checkbox path="forDomain" id="forDomain" label="Domain" /></td>
    					<td>
    						<div class="input full"><form:input path="mapDomain" /></div>
    						<form:errors path="mapDomain" cssClass="error" element="div" />
    					</td>
    					<td><form:checkbox path="domainAsParam" id="domainAsParam" /></td>
    				</tr>
    				<tr>
    					<td><form:checkbox path="forProvider" id="forProvider" label="Anbieter" /></td>
    					<td>
    						<div class="input full"><form:input path="mapProvider" /></div>
    						<form:errors path="mapProvider" cssClass="error" element="div" />
    					</td>
    					<td><form:checkbox path="providerAsParam" id="providerAsParam" /></td>
    				</tr>
    				<tr>
    					<td><form:checkbox path="forPartner" id="forPartner" label="Partner" /></td>
    					<td>
    						<div class="input full"><form:input path="mapPartner" /></div>
    						<form:errors path="mapPartner" cssClass="error" element="div" />
    					</td>
    					<td><form:checkbox path="partnerAsParam" id="partnerAsParam" /></td>
    				</tr>
    				<tr>
    					<td colspan="3">
    						<ul>
                                <li>Wird das Mapping aktiviert, dann wird das entsprechende Feld auf einen hier
                                angegebenen neuen Feldnamen gemappt. Steht in der IngridQuery also "partner:bw",
                                dann wird dies zu "&lt;gemapptes Feld von Partner&gt;:bw".</li>
                                <li>Wird die Checkbox "als Parameter hinzuf�gen" ausgew�hlt, dann wird das gemappte 
                                Feld inkl. Wert als Parameter an die Opensearch-Query angeh�ngt. Ansonsten wird 
                                dieser innerhalb des Suchbegriffs aufgenommen.</li>
                                <li>Wird im Feld "zu mappen auf" die Zeichenfolge '[]' (eckige-Klammer-auf eckige-Klammer-zu) 
                                eingetragen, so wird der entsprechende Parametername weggelassen und der Wert dieses 
                                Parameters als Bestandteil des Suchbegriffs �bertragen. Eine InGridQuery 
                                "site:www.uba.de bodenzustand" wird also zu "www.uba.de bodenzustand" umgeformt.</li>
                            </ul>
    						 
    					</td>
    				</tr>
    			</table>
    		</form:form>
    	</div>
    
    </div>
    <div id="footer" style="height:100px; width:90%"></div>
</body>
</html>
