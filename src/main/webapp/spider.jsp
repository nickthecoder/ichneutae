<%@ page contentType="text/html" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://nickthecoder.co.uk/webwidgets" prefix="ww" %>

<tiles:insert template="${template}" flush="true">

<tiles:put name="title" type="string" >Search - Spider</tiles:put>

<tiles:put name="content" type="string" >

    <h1>Spider</h1>

    <c:choose>
        <c:when test="${spidering}">
            <p>
                Spidering in progress...
            </p>
        </c:when>
        
        <c:otherwise>
        <div class="center">
            <form method="post" action="spider">
                <p>
                    <input type="submit" value="Start Spider"/>
                </p>
            </form>
        </div>
        </c:otherwise>
    </c:choose>
    
</tiles:put>

</tiles:insert>
