<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Cat Mash</title>
<link rel="stylesheet" type="text/css" href="catmash.css">
</head>
<body>

<jsp:useBean id = "catMashBean" class = "beans.CatMashBean" />
<jsp:useBean id = "resultBean" class = "beans.ResultBean" scope="application"/>

<% catMashBean.generateRandomCats(resultBean); //catMashBean a besoin de récupérer la liste des chat du resultBean %>

<div class="hcenter" style="width: 200px;"><img src="logo.png" width="200" height="200" /></div>
<div class="hcenter" style="width: 650px;"><h2>Veuillez cliquer sur le chat que vous trouvez le plus mignon</h2></div>
<div class="hcenter" style="width: 710px; margin-top: 50px;" >
    <div class="heart image-left" >
		<a href="result.jsp?vote=<%= catMashBean.getFirstCatId()+";;"+catMashBean.getSecondCatId() %>"><img src="<%= catMashBean.getFirstCatUrl() %>" class="limit-image" /></a>
	</div>
	<div class="heart image-right" >
		<a href="result.jsp?vote=<%= catMashBean.getSecondCatId()+";;"+catMashBean.getFirstCatId() %>"><img src="<%= catMashBean.getSecondCatUrl() %>" class="limit-image" /></a>
	</div>
</div>
<div class="hcenter" style="width: 200px;  clear: both; margin-top: 500px;"><a href="result.jsp"> Voir les chats les plus mignons</a></div>

</body>
</html>