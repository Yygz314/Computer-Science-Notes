<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.webshopping.model.Userinfo,com.webshopping.util.HtmlUtil" %>
<%
  Userinfo regUser = (Userinfo) request.getAttribute("regUser");
  if (regUser == null) {
    regUser = (Userinfo) session.getAttribute("loginUser");
  }
  String username = regUser == null ? "" : HtmlUtil.escape(regUser.getUsername());
  String sex = regUser == null ? "" : HtmlUtil.escape(regUser.getSex());
  String hobby = regUser == null ? "" : HtmlUtil.escape(regUser.getHobby());
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>注册成功</title>
<link rel="stylesheet" href="css/global.css" type="text/css" media="all" />
</head>
<body>
<div id="page">
  <div id="header"><jsp:include page="header.jsp" /></div>
  <div id="div_reg">
    <img src="images/reg_success.png" alt="reg success" />
    <h3>注册成功</h3><br>
    <hr size="1"><br>
    <p>用户名：<%= username %></p><br>
    <p>性别：<%= sex %></p><br>
    <p>兴趣：<%= hobby %></p><br>
    <a href="index.jsp">返回首页</a>
    &nbsp;|&nbsp;
    <a href="product.jsp">去逛商品</a>
  </div>
  <div id="footer"><jsp:include page="bottom.jsp" /></div>
</div>
</body>
</html>
