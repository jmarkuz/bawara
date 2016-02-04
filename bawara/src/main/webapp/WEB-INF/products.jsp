<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<t:template>
	<table>
		<thead>
			<tr>
				<th>Id</th>
				<th>Title</th>
				<th>Description</th>
				<th>Count</th>
				<th>Price</th>
				<th>Category Id</th>
				<th>Category title</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${products}" var="product">
				<tr>
					<td>${product.id}</td>
					<td>${product.title}</td>
					<td>${product.description}</td>
					<td>${product.count}</td>	
					<td>${product.price}</td>	
					<td>${product.productCategory.id}</td>	
					<td>${product.productCategory.title}</td>		
				</tr>
			</c:forEach>
		</tbody>
	</table>
</t:template>