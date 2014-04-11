<%@ page import = "javax.servlet.jsp.*" isThreadSafe="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<link href="<c:url value="/resources/css/tramps.css" />"
	rel="stylesheet" type="text/css" media="screen" />
<title>Listing Tramps</title>
</head>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%
  String sServerInfo   = "";
  String sHsgwUrl   = "";
  if( request.getParameterNames().hasMoreElements() == true )
  {
	  sServerInfo = request.getParameter( "serverInfo" );
	  sHsgwUrl = request.getParameter( "hsgwUrl" );
	  
  }
  sHsgwUrl = (String) pageContext.getAttribute("hsgwUrl",2);
  if ( sHsgwUrl == null)
	  sHsgwUrl = (String) request.getAttribute("hsgwUrl");
	  

%>
	Hallo ${serverTime}
	<br> Session ${Session}
	<div class="container">
		<h1>Server information</h1>
		<table class="normal-table">
			<thead>
				<tr>
					<th>Key</th>
					<th>Value</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>serverInfo</td>
					<td><c:out value="${serverInfo}"/></td>
				</tr>
				<tr>
					<td>serverInfo2</td>
					<td><%= getServletConfig().getServletContext().getRealPath( request.getRequestURI() ) %></td>
				</tr>
				<tr>
					<td>sHsgwUrl</td>
					<td><%= sHsgwUrl %></td>
				</tr>
				<tr>
					<td>version</td>
					<td>${version}</td>
				</tr>
				<tr>
					<td>App. name</td>
					<td>${appname}</td>
				</tr>
				<tr>
					<td>Biuldtime</td>
					<td>${buildtime}</td>
				</tr>

				<tr>
					<td>contextPath</td>
					<td>${contextPath}</td>
				</tr>
				<tr>
					<td>realPath</td>
					<td>${realPath}</td>
				</tr>
				<tr>
					<td>data source</td>
					<td>${dataSource}</td>
				</tr>
				<tr>
					<td>hsgw</td>
					<td>${hsgwUrl}</td>
				</tr>
				<tr>
					<td>session-timeout</td>
					<td>${session_timeout}</td>
				</tr>
			</tbody>
		</table>
	</div>

	<div class="container">
		<h1>Statistik</h1>
		<c:if test="${not empty statistik}">

			<table class="normal-table">
				<thead>
					<tr>
						<th>text</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="item" items="${statistik}">
						<tr>
							<td>${item}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>

	</div>


</body>
</html>