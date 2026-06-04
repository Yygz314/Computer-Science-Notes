<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,java.util.Map,java.util.ArrayList,java.net.URLEncoder,java.nio.charset.StandardCharsets,com.webshopping.dao.OrderDao,com.webshopping.model.OrderInfo,com.webshopping.model.OrderItem,com.webshopping.util.HtmlUtil,com.webshopping.util.WebSecurityUtil" %>
<%!
  private String statusLabel(String status) {
    if ("PENDING".equalsIgnoreCase(status)) {
      return "\u5f85\u652f\u4ed8";
    }
    if ("PAID".equalsIgnoreCase(status)) {
      return "\u5df2\u652f\u4ed8";
    }
    if ("CANCELLED".equalsIgnoreCase(status)) {
      return "\u5df2\u53d6\u6d88";
    }
    return "\u5168\u90e8";
  }

  private String statusColor(String status) {
    if ("PENDING".equalsIgnoreCase(status)) {
      return "#B8860B";
    }
    if ("PAID".equalsIgnoreCase(status)) {
      return "#006400";
    }
    if ("CANCELLED".equalsIgnoreCase(status)) {
      return "#8B0000";
    }
    return "#666";
  }
%>
<%
  String user = (String) session.getAttribute("user");
  if (user == null || user.trim().isEmpty()) {
    String redirectMsg = URLEncoder.encode("\u8bf7\u5148\u767b\u5f55\u540e\u518d\u67e5\u770b\u8ba2\u5355\u3002", StandardCharsets.UTF_8.name());
    response.sendRedirect("login.jsp?msg=" + redirectMsg);
    return;
  }

  String status = request.getParameter("status");
  status = status == null ? "" : status.trim().toUpperCase();
  if (!"PENDING".equals(status) && !"PAID".equals(status) && !"CANCELLED".equals(status)) {
    status = "";
  }
  String safeStatus = HtmlUtil.escape(status);

  int pageSize = 5;
  int pageNo = 1;
  try {
    pageNo = Integer.parseInt(request.getParameter("page"));
  } catch (Exception ignore) {
    pageNo = 1;
  }

  String msg = HtmlUtil.escape(request.getParameter("msg"));
  String csrfToken = WebSecurityUtil.csrfToken(session);

  OrderDao orderDao = new OrderDao();
  List<OrderInfo> orders;
  Map<Integer, List<OrderItem>> orderItemMap = java.util.Collections.emptyMap();
  int totalOrders = 0;
  int totalPages = 1;
  int totalAll = 0;
  int totalPending = 0;
  int totalPaid = 0;
  int totalCancelled = 0;
  String error = null;
  try {
    totalAll = orderDao.countOrdersByOwnerAndStatus(user.trim(), "");
    totalPending = orderDao.countOrdersByOwnerAndStatus(user.trim(), "PENDING");
    totalPaid = orderDao.countOrdersByOwnerAndStatus(user.trim(), "PAID");
    totalCancelled = orderDao.countOrdersByOwnerAndStatus(user.trim(), "CANCELLED");

    totalOrders = orderDao.countOrdersByOwnerAndStatus(user.trim(), status);
    totalPages = (int) Math.ceil(totalOrders / (double) pageSize);
    if (totalPages < 1) {
      totalPages = 1;
    }
    if (pageNo < 1) {
      pageNo = 1;
    } else if (pageNo > totalPages) {
      pageNo = totalPages;
    }

    orders = orderDao.listOrdersByOwnerAndStatus(user.trim(), status, pageNo, pageSize);
    if (!orders.isEmpty()) {
      List<Integer> orderIds = new ArrayList<Integer>();
      for (OrderInfo order : orders) {
        orderIds.add(order.getId());
      }
      orderItemMap = orderDao.listOrderItemsByOrderIds(orderIds);
    }
  } catch (Exception e) {
    orders = java.util.Collections.emptyList();
    error = "\u8ba2\u5355\u52a0\u8f7d\u5931\u8d25\u3002";
  }

  String statusQuery = status.isEmpty() ? "" : ("&status=" + status);
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>&#25105;&#30340;&#35746;&#21333;</title>
<link rel="stylesheet" rev="stylesheet" href="css/global.css" type="text/css" media="all" />
<style>
  .orders-panel {
    float: left;
    width: 100%;
    box-sizing: border-box;
    padding: 12px;
    border: 1px solid #E5E5E5;
    background: #fff;
  }
  .orders-filter {
    margin: 12px 0;
    line-height: 2;
  }
  .order-card {
    border: 1px solid #ddd;
    margin-bottom: 12px;
    padding: 10px;
    background: #fff;
    line-height: 1.8;
  }
  .order-meta {
    color: #444;
    margin-bottom: 8px;
  }
  .order-actions {
    margin: 8px 0;
  }
  .order-actions button {
    cursor: pointer;
    padding: 2px 8px;
  }
  .order-items {
    width: 100%;
    border-collapse: collapse;
    margin-top: 8px;
  }
  .order-items th,
  .order-items td {
    border-bottom: 1px solid #eee;
    padding: 6px;
    text-align: left;
  }
</style>
</head>
<body>
<div id="page">
  <div id="header"><jsp:include page="header.jsp" /></div>

  <div id="main_content">
    <table width="950" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="212" valign="top">
          <div id="left_column"><jsp:include page="left_column.jsp" /></div>
        </td>
        <td width="738" valign="top">
          <div id="center_column">
            <div class="orders-panel">
              <h3>&#25105;&#30340;&#35746;&#21333;</h3>
              <hr size="1">

              <div class="orders-filter">
                <strong>&#32479;&#35745;&#65306;</strong>
                <a href="orders.jsp" style="<%= status.isEmpty() ? "font-weight:bold;" : "" %>">&#20840;&#37096;(<%= totalAll %>)</a>
                &nbsp;|&nbsp;
                <a href="orders.jsp?status=PENDING" style="<%= "PENDING".equals(status) ? "font-weight:bold;color:#B8860B;" : "color:#B8860B;" %>">&#24453;&#25903;&#20184;(<%= totalPending %>)</a>
                &nbsp;|&nbsp;
                <a href="orders.jsp?status=PAID" style="<%= "PAID".equals(status) ? "font-weight:bold;color:#006400;" : "color:#006400;" %>">&#24050;&#25903;&#20184;(<%= totalPaid %>)</a>
                &nbsp;|&nbsp;
                <a href="orders.jsp?status=CANCELLED" style="<%= "CANCELLED".equals(status) ? "font-weight:bold;color:#8B0000;" : "color:#8B0000;" %>">&#24050;&#21462;&#28040;(<%= totalCancelled %>)</a>
              </div>

              <form method="get" action="orders.jsp" class="orders-filter">
                &#29366;&#24577;&#31579;&#36873;&#65306;
                <select name="status">
                  <option value="" <%= status.isEmpty() ? "selected" : "" %>>&#20840;&#37096;</option>
                  <option value="PENDING" <%= "PENDING".equals(status) ? "selected" : "" %>>&#24453;&#25903;&#20184;</option>
                  <option value="PAID" <%= "PAID".equals(status) ? "selected" : "" %>>&#24050;&#25903;&#20184;</option>
                  <option value="CANCELLED" <%= "CANCELLED".equals(status) ? "selected" : "" %>>&#24050;&#21462;&#28040;</option>
                </select>
                <button type="submit">&#26597;&#35810;</button>
              </form>

<%
  if (msg != null && !msg.trim().isEmpty()) {
%>
              <div style="color:#006400;margin-bottom:10px;"><%= msg %></div>
<%
  }
  if (error != null) {
%>
              <div style="color:#B22222;"><%= HtmlUtil.escape(error) %></div>
<%
  } else if (orders.isEmpty()) {
%>
              <div style="color:#666;">&#24403;&#21069;&#27809;&#26377;&#35746;&#21333;&#12290;</div>
<%
  } else {
    for (OrderInfo order : orders) {
      List<OrderItem> items = orderItemMap.get(order.getId());
      if (items == null) {
        items = java.util.Collections.emptyList();
      }

      String safeOrderNo = HtmlUtil.escape(order.getOrderNo());
      String statusCode = order.getStatus() == null ? "" : order.getStatus().trim().toUpperCase();
%>
              <div class="order-card">
                <div class="order-meta">
                  &#35746;&#21333;&#21495;&#65306;<strong><%= safeOrderNo %></strong><br>
                  &#29366;&#24577;&#65306;<strong style="color:<%= statusColor(statusCode) %>;"><%= statusLabel(statusCode) %></strong>
                  &nbsp;|&nbsp;&#21512;&#35745;&#65306;<strong><%= (int) order.getTotalAmount() %></strong> &#20803;<br>
                  &#21019;&#24314;&#26102;&#38388;&#65306;<%= order.getCreatedAt() %>
                  &nbsp;|&nbsp;&#26356;&#26032;&#26102;&#38388;&#65306;<%= order.getUpdatedAt() %>
                </div>

<%
      if ("PAID".equals(statusCode) && order.getPaidAt() != null) {
%>
                <div style="color:#006400;">&#25903;&#20184;&#26102;&#38388;&#65306;<%= order.getPaidAt() %></div>
<%
      } else if ("CANCELLED".equals(statusCode) && order.getCancelledAt() != null) {
%>
                <div style="color:#8B0000;">&#21462;&#28040;&#26102;&#38388;&#65306;<%= order.getCancelledAt() %></div>
<%
      }
      if ("PENDING".equals(statusCode)) {
%>
                <div class="order-actions">
                  <form action="order" method="post" style="display:inline;">
                    <input type="hidden" name="action" value="pay">
                    <input type="hidden" name="orderNo" value="<%= safeOrderNo %>">
                    <input type="hidden" name="returnStatus" value="<%= safeStatus %>">
                    <input type="hidden" name="returnPage" value="<%= pageNo %>">
                    <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
                    <button type="submit">&#31435;&#21363;&#25903;&#20184;</button>
                  </form>
                  <form action="order" method="post" style="display:inline;margin-left:8px;">
                    <input type="hidden" name="action" value="cancel">
                    <input type="hidden" name="orderNo" value="<%= safeOrderNo %>">
                    <input type="hidden" name="returnStatus" value="<%= safeStatus %>">
                    <input type="hidden" name="returnPage" value="<%= pageNo %>">
                    <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
                    <button type="submit" onclick="return confirm('\u786e\u5b9a\u53d6\u6d88\u8be5\u8ba2\u5355\u5417\uff1f');">&#21462;&#28040;&#35746;&#21333;</button>
                  </form>
                </div>
<%
      }
%>

                <table class="order-items">
                  <tr>
                    <th>&#21830;&#21697;&#21517;&#31216;</th>
                    <th>&#21333;&#20215;</th>
                    <th>&#25968;&#37327;</th>
                    <th>&#23567;&#35745;</th>
                  </tr>
<%
      if (items.isEmpty()) {
%>
                  <tr>
                    <td colspan="4" style="color:#888;">&#35813;&#35746;&#21333;&#26242;&#26080;&#21830;&#21697;&#26126;&#32454;&#12290;</td>
                  </tr>
<%
      } else {
        for (OrderItem item : items) {
%>
                  <tr>
                    <td><%= HtmlUtil.escape(item.getProductName()) %></td>
                    <td><%= (int) item.getUnitPrice() %> &#20803;</td>
                    <td><%= item.getQuantity() %></td>
                    <td><%= (int) item.getSubtotal() %> &#20803;</td>
                  </tr>
<%
        }
      }
%>
                </table>
              </div>
<%
    }
%>
              <div id="page_next">
<%
    int prev = pageNo > 1 ? pageNo - 1 : 1;
    int next = pageNo < totalPages ? pageNo + 1 : totalPages;
%>
                <a class="pageLink" href="orders.jsp?page=<%= prev %><%= statusQuery %>">&#19978;&#19968;&#39029;</a>&nbsp;
                <span style="color:#666;">&#31532; <%= pageNo %> / <%= totalPages %> &#39029;</span>&nbsp;
                <a class="pageLink" href="orders.jsp?page=<%= next %><%= statusQuery %>">&#19979;&#19968;&#39029;</a>
              </div>
<%
  }
%>
            </div>
          </div>
        </td>
      </tr>
    </table>
  </div>

  <div id="footer"><jsp:include page="bottom.jsp" /></div>
</div>
</body>
</html>
