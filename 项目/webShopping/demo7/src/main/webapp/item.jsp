<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.webshopping.model.ProductInfo,com.webshopping.util.WebSecurityUtil,com.webshopping.util.HtmlUtil" %>
<jsp:useBean id="catalog" class="com.webshopping.model.CatalogBean" scope="application" />
<%
  int id = 1;
  try {
    id = Integer.parseInt(request.getParameter("id"));
  } catch (Exception ignore) {
    id = 1;
  }
  catalog.increaseProductClick(id);
  ProductInfo product = catalog.getProductById(id);
  String csrfToken = WebSecurityUtil.csrfToken(session);
  String safeName = product == null ? "" : HtmlUtil.escape(product.getName());
  String msg = HtmlUtil.escape(request.getParameter("msg"));
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>&#21830;&#21697;&#35814;&#24773;</title>
<link rel="stylesheet" rev="stylesheet" href="css/global.css" type="text/css" media="all" />
<style>
  .msg-box {
    margin: 0 0 10px 0;
    padding: 8px 10px;
    border: 1px solid #CFE8CF;
    background: #F4FFF4;
    color: #006400;
  }
  .item-card {
    float: left;
    width: 100%;
    box-sizing: border-box;
    padding: 16px;
    border: 1px solid #E5E5E5;
    background: #fff;
  }
  .item-summary {
    overflow: hidden;
    margin-bottom: 18px;
  }
  .item-cover {
    float: left;
    width: 300px;
  }
  .item-cover img {
    width: 300px;
    height: 225px;
    object-fit: cover;
    border: 1px solid #E6E6E6;
    box-sizing: border-box;
  }
  .item-info {
    float: right;
    width: 370px;
    line-height: 2;
    color: #404040;
  }
  .item-info h3 {
    margin-bottom: 8px;
    font-size: 16px;
  }
  .item-price {
    color: #B22222;
    font-size: 26px;
    font-weight: bold;
  }
  .item-info input[type="number"] {
    width: 60px;
  }
  .item-actions {
    margin-top: 12px;
  }
  .item-actions button {
    padding: 5px 14px;
    cursor: pointer;
  }
  .item-section-title {
    clear: both;
    margin: 14px 0 8px 0;
    border-bottom: 1px dashed #ccc;
    font-weight: bold;
    line-height: 28px;
  }
  .item-params {
    overflow: hidden;
    padding: 10px;
    background: #F6F6F6;
    border: 1px solid #E6E6E6;
    line-height: 2;
  }
  .item-params div {
    float: left;
    width: 225px;
  }
  .item-detail-img {
    margin-top: 14px;
    text-align: center;
  }
  .item-detail-img img {
    max-width: 100%;
    height: auto;
    border: 1px solid #E6E6E6;
    box-sizing: border-box;
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
<%
  if (msg != null && !msg.trim().isEmpty()) {
%>
            <div class="msg-box"><%= msg %></div>
<%
  }
%>
<%
  if (product == null) {
%>
            <div class="item-card" style="color:#B22222;">&#21830;&#21697;&#19981;&#23384;&#22312;&#12290;</div>
<%
  } else {
%>
            <div class="item-card">
              <div class="item-summary">
                <div class="item-cover">
                  <img src="<%= product.getImagePath() %>" alt="<%= safeName %>" />
                </div>

                <div class="item-info">
                  <h3><%= safeName %></h3>
                  <form action="cart" method="post">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
                    <input type="hidden" name="returnUrl" value="item.jsp?id=<%= product.getId() %>">
                    <input type="hidden" name="id" value="<%= product.getId() %>">

                    <div>&#21407;&#20215;&#65306;<span style="text-decoration:line-through;"><%= (int) product.getOriginalPrice() %></span> &#20803;</div>
                    <div>&#20419;&#38144;&#20215;&#65306;<span class="item-price"><%= (int) product.getPrice() %></span> &#20803;</div>
                    <div>&#24050;&#21806;&#20986;&#65306;<strong style="color:#CC6600;"><%= product.getSoldCount() %></strong> &#20214;</div>
                    <div>&#28857;&#20987;&#37327;&#65306;<strong style="color:#1E6AA5;"><%= product.getClickCount() %></strong></div>
                    <div>&#25968;&#37327;&#65306;<input type="number" min="1" max="99" name="quantity" value="1"></div>

                    <div class="item-actions">
                      <button type="submit">&#21152;&#20837;&#36141;&#29289;&#36710;</button>
                      <a class="pageLink" href="cart_view.jsp">&#26597;&#30475;&#36141;&#29289;&#36710;</a>
                    </div>
                  </form>
                </div>
              </div>

              <div class="item-section-title">&#21830;&#21697;&#21442;&#25968;</div>
              <div class="item-params">
                <div>&#21830;&#21697;ID&#65306;<%= product.getId() %></div>
                <div>&#20998;&#31867;&#65306;<%= HtmlUtil.escape(catalog.getCategoryName(product.getCategoryId())) %></div>
                <div>&#25159;&#38754;&#26448;&#36136;&#65306;&#19997;&#32467;</div>
                <div>&#25159;&#39592;&#26448;&#36136;&#65306;&#31481;&#21046;</div>
                <div>&#25159;&#39592;&#25968;&#37327;&#65306;16&#26681;</div>
                <div>&#25159;&#39592;&#38271;&#24230;&#65306;23CM</div>
              </div>

              <div class="item-section-title">&#21830;&#21697;&#35814;&#32454;&#20171;&#32461;</div>
              <div class="item-detail-img">
                <img src="<%= product.getDetailImagePath() %>" alt="<%= safeName %>" />
              </div>
            </div>
<%
  }
%>
          </div>
        </td>
      </tr>
    </table>
  </div>

  <div id="footer"><jsp:include page="bottom.jsp" /></div>
</div>
</body>
</html>
