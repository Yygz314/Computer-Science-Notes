<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.webshopping.util.HtmlUtil,com.webshopping.util.WebSecurityUtil" %>
<%
  String orderNoRaw = request.getParameter("orderNo");
  String orderNoRawSafe = orderNoRaw == null ? "" : orderNoRaw.trim();
  String orderNo = HtmlUtil.escape(orderNoRawSafe);
  String csrfToken = WebSecurityUtil.csrfToken(session);
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>订单创建成功</title>
<link rel="stylesheet" rev="stylesheet" href="css/global.css" type="text/css" media="all" />
</head>
<body>
<div id="page">
  <div id="header"><jsp:include page="header.jsp" /></div>
  <div id="div_reg">
    <h3>订单创建成功</h3>
    <hr size="1"><br>
<%
  if (orderNoRawSafe.isEmpty()) {
%>
    <p style="color:#B22222;">订单号缺失，请前往订单列表查看。</p>
    <p><a href="orders.jsp">查看我的订单</a></p>
<%
  } else {
%>
    <p>你的订单已创建，当前状态为待支付。</p>
    <p>订单号：<strong><%= orderNo %></strong></p>
    <p>订单状态：<strong>待支付</strong></p>
    <br>
    <form action="order" method="post" style="display:inline;">
      <input type="hidden" name="action" value="pay">
      <input type="hidden" name="orderNo" value="<%= orderNo %>">
      <input type="hidden" name="returnStatus" value="PENDING">
      <input type="hidden" name="returnPage" value="1">
      <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
      <button type="submit">立即支付</button>
    </form>
    &nbsp;|&nbsp;
    <a href="orders.jsp">查看我的订单</a>
    &nbsp;|&nbsp;
    <a href="product.jsp">继续购物</a>
<%
  }
%>
  </div>
  <div id="footer"><jsp:include page="bottom.jsp" /></div>
</div>
</body>
</html>
