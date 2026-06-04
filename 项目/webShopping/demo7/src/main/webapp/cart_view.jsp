<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List,com.webshopping.model.CartItem,com.webshopping.dao.CartDao,com.webshopping.util.WebSecurityUtil,com.webshopping.util.HtmlUtil" %>
<%
  request.setCharacterEncoding("UTF-8");

  String user = (String) session.getAttribute("user");
  String cartOwner = (user == null || user.trim().isEmpty()) ? ("guest_" + session.getId()) : user.trim();
  String csrfToken = WebSecurityUtil.csrfToken(session);

  CartDao cartDao = new CartDao();
  String dbError = null;
  String safeMsg = HtmlUtil.escape(request.getParameter("msg"));

  List<CartItem> items;
  double totalPrice;
  try {
    items = cartDao.listItems(cartOwner);
    totalPrice = cartDao.getTotalPrice(cartOwner);
  } catch (Exception e) {
    items = java.util.Collections.emptyList();
    totalPrice = 0;
    dbError = "\u8d2d\u7269\u8f66\u52a0\u8f7d\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5\u3002";
  }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>&#36141;&#29289;&#36710;</title>
<link rel="stylesheet" rev="stylesheet" href="css/global.css" type="text/css" media="all" />
<style>
  .cart-panel {
    float: left;
    width: 100%;
    box-sizing: border-box;
    padding: 12px;
    border: 1px solid #E5E5E5;
    background: #fff;
  }
  .cart-message {
    margin-bottom: 10px;
    color: #006400;
  }
  .cart-error {
    margin-bottom: 10px;
    color: #B22222;
  }
  .cart-empty {
    height: 100px;
    width: 330px;
    background-image: url('images/empty.png');
    background-repeat: no-repeat;
    text-align: right;
    line-height: 2;
    color: #404040;
    padding: 20px 0 0 0;
  }
  .cart-table {
    width: 100%;
    border-collapse: collapse;
    background: #F1F8FF;
    margin-top: 10px;
  }
  .cart-table th,
  .cart-table td {
    padding: 8px 6px;
    border-bottom: 1px solid #DCEAF6;
    text-align: center;
  }
  .cart-table th {
    color: #696969;
    font-weight: bold;
  }
  .cart-table input[type="number"] {
    width: 58px;
  }
  .cart-actions {
    clear: both;
    text-align: center;
    margin-top: 16px;
    line-height: 2.2;
  }
  .cart-total {
    text-align: right;
    margin: 14px 8px 0 0;
  }
  .cart-total strong {
    font-size: 18px;
    color: #f60;
  }
  .cart-actions button,
  .cart-table button {
    cursor: pointer;
    padding: 2px 8px;
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
            <div class="cart-panel">
              <h3>&#36141;&#29289;&#36710;</h3>
              <hr size="1"><br>
<%
  if (safeMsg != null && !safeMsg.trim().isEmpty()) {
%>
              <div class="cart-message"><%= safeMsg %></div>
<%
  }
  if (dbError != null) {
%>
              <div class="cart-error"><%= HtmlUtil.escape(dbError) %></div>
<%
  }

  if (items.isEmpty()) {
%>
              <div class="cart-empty">
                <h3>&#20320;&#30340;&#36141;&#29289;&#36710;&#36824;&#26159;&#31354;&#30340;&#12290;</h3>
                <h3>&#24555;&#21435;&#25361;&#36873;&#21916;&#27426;&#30340;&#21830;&#21697;&#21543;&#12290;</h3>
              </div>
              <div class="cart-actions">
                <a class="pageLink" href="product.jsp">&#21435;&#36891;&#21830;&#21697;</a>
              </div>
<%
  } else {
%>
              <table class="cart-table">
                <tr>
                  <th width="8%">&#24207;&#21495;</th>
                  <th width="30%">&#21830;&#21697;</th>
                  <th width="12%">&#21333;&#20215;</th>
                  <th width="20%">&#25968;&#37327;</th>
                  <th width="13%">&#23567;&#35745;</th>
                  <th width="17%">&#25805;&#20316;</th>
                </tr>
<%
    for (int i = 0; i < items.size(); i++) {
      CartItem item = items.get(i);
%>
                <tr>
                  <td><%= i + 1 %></td>
                  <td><%= HtmlUtil.escape(item.getName()) %></td>
                  <td><%= (int) item.getPrice() %> &#20803;</td>
                  <td>
                    <form action="cart" method="post">
                      <input type="hidden" name="action" value="update">
                      <input type="hidden" name="productId" value="<%= item.getId() %>">
                      <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
                      <input type="number" name="quantity" min="1" max="99" value="<%= item.getQuantity() %>">
                      <button type="submit">&#26356;&#26032;</button>
                    </form>
                  </td>
                  <td><%= (int) item.getSubtotal() %> &#20803;</td>
                  <td>
                    <form action="cart" method="post">
                      <input type="hidden" name="action" value="remove">
                      <input type="hidden" name="productId" value="<%= item.getId() %>">
                      <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
                      <button type="submit" onclick="return confirm('\u786e\u5b9a\u79fb\u9664\u8be5\u5546\u54c1\u5417\uff1f');">&#31227;&#38500;</button>
                    </form>
                  </td>
                </tr>
<%
    }
%>
              </table>
              <div class="cart-total">&#21512;&#35745;&#37329;&#39069;&#65306;<strong><%= (int) totalPrice %></strong> &#20803;</div>
              <div class="cart-actions">
                <a class="pageLink" href="product.jsp">&#32487;&#32493;&#36141;&#29289;</a>
                <form action="cart" method="post" style="display:inline;">
                  <input type="hidden" name="action" value="checkout">
                  <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
                  <button type="submit" onclick="return confirm('\u786e\u5b9a\u63d0\u4ea4\u8ba2\u5355\u5417\uff1f');">&#21435;&#32467;&#31639;</button>
                </form>
                <a class="pageLink" href="orders.jsp">&#25105;&#30340;&#35746;&#21333;</a>
                <form action="cart" method="post" style="display:inline;">
                  <input type="hidden" name="action" value="clear">
                  <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
                  <button type="submit" onclick="return confirm('\u786e\u5b9a\u6e05\u7a7a\u8d2d\u7269\u8f66\u5417\uff1f');">&#28165;&#31354;&#36141;&#29289;&#36710;</button>
                </form>
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
