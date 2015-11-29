<html lang="en-GB">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://nickthecoder.co.uk/webwidgets" prefix="ww" %>

<% request.setAttribute( "server", request.getServerName() ); %>
<tiles:useAttribute name="navigation" ignore="true"/>

<head>
  <title>
	<tiles:insert attribute="title"/>
  </title>
  <ww:styleSheet href="/templates/ntc/style.css"/>
  <link rel="icon" href="<ww:contextPath/>/templates/icon.png"/>
  <link href='http://fonts.googleapis.com/css?family=Arvo' rel='stylesheet' type='text/css' />
</head>

<body>
  <div id="whole">
    <div id="header">
      <div id="logo">
      	<c:choose>
          <c:when test="${server == 'nickthecoder.co.uk'}">
	    <h1>Nick The Coder . co . uk</h1>
	  </c:when>
	  <c:otherwise>
	    <h1>Giddyserv</h1>
	  </c:otherwise>
	</c:choose>
      </div>
      
      <form id="search" method="get" action="/ichneutae/search">
        <input id="searchText" type="text" size="15" name="q" placeholder="search"/>
        <input id="searchSubmit" type="submit" value="&#10140;"/>
      </form>

      <ww:tabs id="tabs">
        <ww:tab useContextPath="false" pattern="/index.jsp"><ww:link href="/wiki/view/Home">Wiki</ww:link></ww:tab>
         <ww:tab useContextPath="false" pattern=".*Music.do"><ww:link href="/gidea/listMusic.do">Music</ww:link></ww:tab>
         <ww:tab useContextPath="false" pattern="/examples/.*"><ww:link href="/examples/">Examples</ww:link></ww:tab>
       </ww:tabs>

    </div>

    <div id="belowTabs">
    </div>
    
    <div id="main">
	  <c:if test="${navigation==null}">
      <div id="full">
	  </c:if>
	  <c:if test="${navigation!=null}">
      <div id="columns">
	  </c:if>

        <div id="content">  	  


          <tiles:insert attribute="content" ignore="true"/>
        </div>

        <div id="navigation">
          <tiles:insert attribute="navigation" ignore="true"/>
        </div>
        
      </div>

      <div id="belowColumns">
      </div>

    </div>

    <div id="footer">        
      &copy; <a href="http://nickthecoder.co.uk">nickthecoder.co.uk</a>
    </div>
        
  </div>

</body>
</html>
