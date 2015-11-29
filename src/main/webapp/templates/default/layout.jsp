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
  <ww:styleSheet href="/templates/default/style.css"/>
  <link rel="icon" href="<ww:contextPath/>/templates/icon.png"/>
  <link href='http://fonts.googleapis.com/css?family=Arvo' rel='stylesheet' type='text/css' />
</head>

<body>
  <div id="whole">
    <div id="header">
      <div id="logo">
      <form id="search" method="get" action="/ichneutae/search">
        <input id="searchText" type="text" size="15" name="q" placeholder="search"/>
        <input id="searchSubmit" type="submit" value="&#10140;"/>
      </form>
	    <h1>Ichneutae</h1>
      </div>      
    </div>

    <div id="main">
      <div id="full">
        <div id="content">  	  
          <tiles:insert attribute="content" ignore="true"/>
        </div>
      </div>
    </div>

    <div id="footer">        
        Powered by <a href="http://nickthecoder.co.uk/software/view/Ichneutae">Ichneutae</a>
    </div>
        
  </div>

</body>
</html>
