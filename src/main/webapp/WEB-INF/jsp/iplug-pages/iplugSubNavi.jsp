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


