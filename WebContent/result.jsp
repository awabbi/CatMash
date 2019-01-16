<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Cat Mash Results</title>
<link rel="stylesheet" type="text/css" href="catmash.css">
</head>
<body>
<jsp:useBean id = "resultBean" class = "beans.ResultBean" scope="application"/>
<jsp:setProperty name = "resultBean" property = "vote" />
<!-- 
	En principe, on doit utiliser un deuxième bean de scope "session" pour controller l'opération de vote. Sinon, on peut voter à l'infini en rafraichissant cette page. 
	Ce bean doit fonctionner comme suite: si le même utilisateur a déjà fait ce vote (ou le vote dans l'autre sens), on ne doit pas le prendre en compte.
	On n'a pas implémenter ce contrôl pour laisser la possibilité à l'Atelier de tester sans conditions ... 
 -->

<div class="hcenter" style="width: 200px;"><img src="logo.png" width="200" height="200" /></div>
<div class="hcenter" style="width: 480px;"><h2>Voici la liste des chats ordonnée après <%= resultBean.getTotalVotesCounter() %> votes</h2></div>
<div class="hcenter" style="width: 90px;  clear: both; margin-bottom: 50px;"><a href="index.jsp"> Voter encore</a></div>
<% 
	String[] cats= resultBean.getRankedCats();
	for(int i=0; i<cats.length; i++) {
%>
	<div class="hcenter limit300" >
		<img src="<%= cats[i] %>" class="limit-image" />
	</div>	
<%
	}
%>
</body>
</html>