<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List,java.net.URLEncoder,java.nio.charset.StandardCharsets,com.webshopping.model.ProductInfo,com.webshopping.util.HtmlUtil" %>
<jsp:useBean id="catalog" class="com.webshopping.model.CatalogBean" scope="application" />
<%
  int pageSize = 9;
  int pageNo = 1;
  try {
    pageNo = Integer.parseInt(request.getParameter("page"));
  } catch (Exception ignore) {
    pageNo = 1;
  }

  String keyword = request.getParameter("q");
  keyword = (keyword == null) ? "" : keyword.trim();
  String safeKeyword = HtmlUtil.escape(keyword);
  String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8.name());

  int totalProducts = catalog.getTotalProductsByKeyword(keyword);
  int totalPages = (int) Math.ceil(totalProducts / (double) pageSize);
  if (totalPages < 1) {
    totalPages = 1;
  }
  if (pageNo < 1) {
    pageNo = 1;
  } else if (pageNo > totalPages) {
    pageNo = totalPages;
  }

  List<ProductInfo> products = catalog.getProductsByPageAndKeyword(pageNo, pageSize, keyword);
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>全部商品</title>
<link rel="stylesheet" rev="stylesheet" href="css/global.css" type="text/css" media="all" />
</head>
<body>

<div id="page">
  <div id="header">
    <jsp:include page="header.jsp" />
  </div>

  <div id="main_content">
    <table width="950" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="212" valign="top">
          <div id="left_column">
            <jsp:include page="left_column.jsp" />
          </div>
        </td>
        <td width="738" valign="top">
          <div id="center_column">
            <div class="divBorder">
              <img src="images/all_fans.gif" alt="all" /><br>

<%
  if (!keyword.isEmpty()) {
%>
              <div style="padding:8px 20px;color:#555;">
                当前搜索：<strong><%= safeKeyword %></strong>，共 <%= totalProducts %> 条结果
              </div>
<%
  }
%>

<%
  if (products == null || products.isEmpty()) {
%>
              <div style="padding:20px;color:#666;">当前没有商品数据。</div>
<%
  } else {
    for (ProductInfo p : products) {
      String safeName = HtmlUtil.escape(p.getName());
%>
              <div id="sort_product">
                <ul>
                  <li><p class="gpic"><a href="item.jsp?id=<%= p.getId() %>"><img width="205" height="154" src="<%= p.getImagePath() %>" alt="<%= safeName %>"></a></p></li>
                  <li><p class="gbt"><a href="item.jsp?id=<%= p.getId() %>">品名：<%= safeName %></a></p></li>
                  <li><p class="gprice">促销价：<span style="color:#FF6600;font-weight:bold;"><%= (int) p.getPrice() %></span>元</p></li>
                  <li><p class="gsale">已售出：<span style="font-weight:bold;"><%= p.getSoldCount() %></span>&nbsp;件</p></li>
                  <li><p class="gsale">点击量：<span style="font-weight:bold;color:#1E6AA5;"><%= p.getClickCount() %></span></p></li>
                </ul>
              </div>
<%
    }
  }
%>
              <div id="page_next">
<%
  int prev = pageNo > 1 ? pageNo - 1 : 1;
  int next = pageNo < totalPages ? pageNo + 1 : totalPages;
  String qPart = keyword.isEmpty() ? "" : ("&q=" + encodedKeyword);
%>
                <a class="pageLink" href="product.jsp?page=<%= prev %><%= qPart %>">上一页</a>&nbsp;
                <span style="color:#666;">第 <%= pageNo %> / <%= totalPages %> 页</span>&nbsp;
                <a class="pageLink" href="product.jsp?page=<%= next %><%= qPart %>">下一页</a>
              </div>
            </div>
          </div>
        </td>
      </tr>
    </table>
  </div>

  <div id="footer">
    <jsp:include page="bottom.jsp" />
  </div>
</div>
</body>
</html>

