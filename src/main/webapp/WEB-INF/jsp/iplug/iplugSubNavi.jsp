<%@ include file="/WEB-INF/jsp/base/include.jsp" %>
<c:choose>
    <c:when test="${plugdescriptionClickable == 'false'}">
        <li
        <c:if test="${active == 'osParams'}">
            class="active"
        </c:if>
        >Opensearch Parameter</li>
    </c:when>
    <c:when test="${active != 'osParams'}">
        <li><a href="../iplug/osParams.html">Opensearch Parameter</a></li>
    </c:when>
    <c:otherwise>
        <li class="active"><a href="<%=request.getContextPath()%>/base/general.html">Opensearch Parameter</a></li>
    </c:otherwise>
</c:choose>

