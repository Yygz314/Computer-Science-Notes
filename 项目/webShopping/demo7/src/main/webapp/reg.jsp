<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.webshopping.util.HtmlUtil" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>用户注册</title>
<link rel="stylesheet" rev="stylesheet" href="css/global.css" type="text/css" media="all" />
</head>
<body>
<div id="page">
  <div id="header">
    <jsp:include page="header.jsp" />
  </div>

  <div id="div_reg">
    <h3>注册新用户</h3><br>
    <hr size="1"><br>
<%
  String msg = HtmlUtil.escape(request.getParameter("msg"));
  if (msg != null && !msg.trim().isEmpty()) {
%>
    <p style="color:#B22222;font-weight:bold;text-align:center;"><%= msg %></p><br>
<%
  }
%>
    <form action="reg_action.jsp" method="post">
      <table align="center">
        <tr>
          <td>用户名：</td>
          <td><input type="text" name="userName" required></td>
        </tr>
        <tr>
          <td>密码：</td>
          <td><input type="password" name="pwd" required></td>
        </tr>
        <tr>
          <td>确认密码：</td>
          <td><input type="password" name="pwd1" required></td>
        </tr>
        <tr>
          <td>性别：</td>
          <td>
            <input type="radio" name="sex" value="男" checked>男
            <input type="radio" name="sex" value="女">女
          </td>
        </tr>
        <tr>
          <td>兴趣：</td>
          <td>
            <input type="checkbox" name="interest" value="爬山">爬山
            <input type="checkbox" name="interest" value="钓鱼">钓鱼
            <input type="checkbox" name="interest" value="购物">购物
          </td>
        </tr>
        <tr>
          <td><input type="submit" value="注册"></td>
          <td><input type="reset" value="重置"></td>
        </tr>
      </table>
    </form>
  </div>

  <div id="footer">
    <jsp:include page="bottom.jsp" />
  </div>
</div>
</body>
</html>
