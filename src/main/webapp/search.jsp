<%@ page contentType="text/html; charset=utf-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://nickthecoder.co.uk/webwidgets" prefix="ww" %>

<tiles:insert template="${template}" flush="true">

<tiles:put name="title" type="string" >Search</tiles:put>

<tiles:put name="content" type="string" >

    <h1>Search</h1>

    <form id="searchForm" action="search" method="get">
        <input id="largeSearchBox" type="text" name="q" size="50" value="<c:out value="${q}"/>"/>
        <input id="searchButton" type="submit" name="go" value="Go"/>
        
        <div class="categories">
        <c:forEach items="${categoryChoices}" var="categoryChoice">
            <span class="category">
                <input id="category_<c:out value="${categoryChoice.category.code}"/>" type="checkbox" <c:if test="${categoryChoice.selected}">checked="checked"</c:if> name="category" value="<c:out value="${categoryChoice.category.code}"/>"/>
                <span onclick="document.getElementById( 'category_<c:out value="${categoryChoice.category.code}"/>' ).checked = ! document.getElementById( 'category_<c:out value="${categoryChoice.category.code}"/>' ).checked;"><c:out value="${categoryChoice.category.label}"/></span>
            </span>
        </c:forEach>
        </div>
        
    </form>

    <c:if test="${hits != null}">

    <div id="searchResults">
        <c:choose>
            <c:when test="${hits.hitCount == 0}">
                <h3>No Matches Found</h3>
            </c:when>
            <c:otherwise>
               <h2>Results</h2>
            </c:otherwise>
        </c:choose>
        <c:forEach items="${hits.hits}" var="hit">
        <div class="searchResult">
            <h3><a href="<c:out value="${hit.URLString}" />"><c:out value="${hit.title}" /></a></h3>
            <div class="url"><a href="<c:out value="${hit.URLString}" />"><c:out value="${hit.URLString}"/></a></div>
            <p>
                <c:forEach var="section" items="${hit.summarySections}"
                    ><c:choose
                        ><c:when test="${section.matched}"
                            ><b><c:out value="${section}"/></b
                        ></c:when><c:otherwise
                            ><c:out value="${section}"
                        /></c:otherwise
                    ></c:choose
                ></c:forEach><br/>
            </p>
        </div>
        </c:forEach>
    </div>
    
    <c:if test="${hits.pageCount > 1}">
        <hr/>    
        <div class="pager">
        Page : 
        <ww:linkInfo href="search">
          <ww:linkParameter name="q" value="${q}"/>
          <ww:pager pages="${hits.pageCount}" page="${pageNumber}" nextPages="9" previousPages="5">
            <ww:pagerLinks type="before"><ww:link>${page}</ww:link></ww:pagerLinks>
            <ww:pagerLinks type="current"><span class="currentPage">${page}</span></ww:pagerLinks>
            <ww:pagerLinks type="after"><ww:link>${page}</ww:link></ww:pagerLinks>
            <ww:pagerLinks type="next"><ww:link title="Next Page">&gt;</ww:link></ww:pagerLinks>
          </ww:pager>
        </ww:linkInfo>
        </div>
    </c:if>
        
    </c:if>
    
</tiles:put>

</tiles:insert>