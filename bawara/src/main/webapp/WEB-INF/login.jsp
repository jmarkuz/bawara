<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<t:template>
	
	<form method="POST" action="/auth.htm">
		<input type="hidden" name="_spring_security_remember_me" value="true"/>
		<div>
			<label>Username: </label>
			<input type="text" name="username" value="" placeholder="Your name"/>
		</div>
		<div>
			<label>Password: </label>
			<input type="password" name="password" value=""/>
		</div>
		<div>
			<input type="submit" name="login" value="Log in"/>
		</div>	
	</form>
	
</t:template>