<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,com.webshopping.model.ProductInfo,com.webshopping.util.HtmlUtil" %>
<jsp:useBean id="catalog" class="com.webshopping.model.CatalogBean" scope="application" />
<%
  int categoryId = 1;
  try {
    categoryId = Integer.parseInt(request.getParameter("id"));
  } catch (Exception ignore) {
    categoryId = 1;
  }
  List<ProductInfo> products = catalog.getProductsByCategoryId(categoryId);
  String categoryName = HtmlUtil.escape(catalog.getCategoryName(categoryId));
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>分类商品</title>
<link rel="stylesheet" href="css/global.css" type="text/css" media="all" />
</head>
<body>
<div id="page">
  <div id="header"><jsp:include page="header.jsp" /></div>
  <div id="main_content">
    <table width="950" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="212" valign="top"><div id="left_column"><jsp:include page="left_column.jsp" /></div></td>
        <td width="738" valign="top">
          <div id="center_column">
            <div class="divBorder">
              <div id="select_title">
                <h3>&nbsp;&nbsp;分类：<%= categoryName %></h3>
                <hr size="1">
              </div>
<%
  if (products == null || products.isEmpty()) {
%>
              <div style="padding:20px;color:#666;">当前分类暂无商品。</div>
<%
  } else {
    for (ProductInfo p : products) {
      String safeName = HtmlUtil.escape(p.getName());
%>
              <div id='sort_product'>
                <ul>
                  <li><p class='gpic'><a href='item.jsp?id=<%= p.getId() %>'><img width='205' height='154' src='<%= p.getImagePath() %>' alt='<%= safeName %>'></a></p></li>
                  <li><p class='gbt'><a href='item.jsp?id=<%= p.getId() %>'>品名：<%= safeName %></a></p></li>
                  <li><p class='gprice'>促销价：<span style='color:#FF6600;font-weight:bold;'><%= (int) p.getPrice() %></span>元</p></li>
                  <li><p class='gsale'>点击量：<span style='font-weight:bold;color:#1E6AA5;'><%= p.getClickCount() %></span></p></li>
                </ul>
              </div>
<%
    }
  }
%>
              <div id='page_next'><a class='pageLink' href='product.jsp'>查看全部商品</a></div>
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

