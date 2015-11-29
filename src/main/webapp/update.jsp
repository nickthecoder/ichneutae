<%@ page contentType="text/html" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://nickthecoder.co.uk/webwidgets" prefix="ww" %>

<tiles:insert template="${template}" flush="true">

<tiles:put name="title" type="string" >Search - Update Page</tiles:put>

<tiles:put name="content" type="string" >

    <h1>Update</h1>

Message ${message}

    <c:if test="${message != null}">
    Yes
        <p>
            <c:out value="${message}"/>
            <c:if test="${url != null}">
                <a href="<c:out value="${url}"/>"><c:out value="${url}"/></a>
            </c:if>
        </p>
        <hr/>
    </c:if>

    <div class="center">
        <form method="post" action="update">
            URL : <input type="text" size="50" name="url" value="<c:out value="${url}"/>"/>
            <input type="submit" value="Update"/>
        </form>
    </div>

</tiles:put>

</tiles:insert>
