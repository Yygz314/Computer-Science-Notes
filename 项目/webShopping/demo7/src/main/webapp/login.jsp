<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.webshopping.util.HtmlUtil" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>用户登录</title>
<link rel="stylesheet" rev="stylesheet" href="css/global.css" type="text/css" media="all" />
</head>

<body>
<div id="page">
  <div id="header">
    <jsp:include page="header.jsp" />
  </div>

  <div id="div_reg">
    <h3>会员登录</h3><br>
    <hr size="1">
    <div id="div_login">
      <div id="div_login_leftimg">
        <img src="images/boy.gif" alt="boy" />
      </div>
      <div id="div_login_form">
<%
  String msg = request.getParameter("msg");
  msg = HtmlUtil.escape(msg);
%>
        <form action="login" method="post">
<%
  if (msg != null && !msg.trim().isEmpty()) {
%>
          <font class="zt2"><%= msg %></font><br>
<%
  } else {
%>
          <font class="zt2">请输入用户名和密码</font><br>
<%
  }
%>
          <label for="txtName">用户名：</label>
          <input type="text" name="txtName" id="txtName" class="input" required><br><br>
          <label for="passWord">密&nbsp;&nbsp;&nbsp;&nbsp;码：</label>
          <input type="password" name="pwd" id="passWord" class="input" required><br><br>
          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          <input name="imageField" type="image" src="images/login_button.gif" alt="login" />
          <a href="reg.jsp"><img src="images/reg_button.gif" border="0" alt="reg" /></a>
        </form>
      </div>
      <div id="div_login_rightimg">
        <img src="images/girl.gif" alt="girl" />
      </div>
    </div>
  </div>

  <div id="footer">
    <jsp:include page="bottom.jsp" />
  </div>
</div>
</body>

</html>
