<%--
  Created by IntelliJ IDEA.
  User: V. Mecko
  Date: 30.3.2017
  Time: 13:04
  To change this template use File | Settings | File Templates.
--%>

<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

<table border="1">
    <thead>
    <tr>
        <th>Full name</th>
        <th>Address</th>
        <th>Phone Number</th>
    </tr>
    </thead>
    <c:forEach items="${guests}" var="guest">
        <tr>
            <td><c:out value="${guest.fullName}"/></td>
            <td><c:out value="${guest.address}"/></td>
            <td><c:out value="${guest.phoneNumber}"/></td>
            <td><form method="post" action="${pageContext.request.contextPath}/guests/delete?guestID=${guest.guestID}"
                      style="margin-bottom: 0;"><input type="submit" value="Delete"></form></td>
            <td><form method="post" action="${pageContext.request.contextPath}/guests/edit?guestID=${guest.guestID}"
                      style="margin-bottom: 0"><input type="submit" value="Edit"></form></td>
        </tr>
    </c:forEach>
</table>

<h2>Enter Guest</h2>
<c:if test="${not empty error}">
    <div style="border: solid 2px #000000; background-color: #e1ee08; padding: 10px">
        <c:out value="${error}"/>
    </div>
</c:if>

<form action="${pageContext.request.contextPath}/guests/add" method="post">
    <table>
        <tr>
            <th>Full name:</th>
            <td><input type="text" name="fullName" value="<c:out value='${param.fullName}'/>"/></td>
        </tr>
        <tr>
            <th>Address:</th>
            <td><input type="text" name="address" value="<c:out value='${param.address}'/>"/></td>
        </tr>
        <tr>
            <th>Phone number:</th>
            <td><input type="text" name="phoneNumber" value="<c:out value='${param.phoneNumber}'/>"/></td>
        </tr>
    </table>
    <input type="Submit" value="Submit" />
</form>

<h2>Edit Guest</h2>
<form action="${pageContext.request.contextPath}/guests/edit" method="post">
    <table>
        <tr>
            <th>Full name:</th>
            <td><input type="text" name="fullName" value="<c:out value='${param.fullName}'/>"/></td>
        </tr>
        <tr>
            <th>Address:</th>
            <td><input type="text" name="address" value="<c:out value='${param.address}'/>"/></td>
        </tr>
        <tr>
            <th>Phone number:</th>
            <td><input type="text" name="phoneNumber" value="<c:out value='${param.phoneNumber}'/>"/></td>
        </tr>
    </table>
    <input type="Submit" value="Update" />
</form>

</body>
</html>
