<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Alfresco Java Plugin Demo</title>
    <link href="<c:url value="/resources/css/theme.css" />" rel="stylesheet"/>
    <link rel="shortcut icon" href="https://www.byu.edu/templates/2.1.5/images/favicon.ico">
</head>
<body>
<div id="sessionInfo">Logged into ${environment} as ${user}</div>
<ul id="nav" class="tabs" data-tab>
    <li class="tab-title"><a href="<c:url value="/byPath"/>"><b>Get by Name</b></a></li>
    <li class="tab-title"><a href="<c:url value="/filterByType"/>"><b>Filter by file type</b></a></li>
    <li class="tab-title active"><a href="<c:url value="/upload"/>"><b>Upload</b></a></li>
    <li class="tab-title"> <a href="<c:url value="/selectFromList"/>"><b>Select and Edit</b></a></li>
    <li class="tab-title"><a href="<c:url value="/videoDemo"/>"><b>Video</b></a></li>
    <li class="tab-title"><a class="logout" href="<c:url value="/auth"/>"><b>Logout</b></a></li>
</ul>


<!-- form -->
<div id="form">
    <p>Please provide the path to a local file you wish to upload to the Alfresco server. <br> Your user root folder has been hardcoded to store your file.</p>
<form:form method="POST" action="./upload">
    <form:label path="uploadPath">Path to File: </form:label>
    <form:input path="uploadPath"/><br>
    <form:label path="description">Description: </form:label>
    <form:textarea path="description" rows="5"/><br>
    <input type="submit" value="Submit"/>

</form:form>
</div>


<!-- Result  -->
<div id="results">
    <c:choose>
        <c:when test="${docName!=null}">
            <p>Upload succeeded!</p>
            <p><b>Document uploaded:</b> ${docName}</p>
            <p><a href=${fullUrl}> View Document </a></p>
        </c:when>
        <c:when test="${invalid == true}">
            <div class="errorMessage"><p>Invalid file, please check to make sure your file path is correct, and local to your machine.</p></div>
        </c:when>
    </c:choose>


</div>
</body>


