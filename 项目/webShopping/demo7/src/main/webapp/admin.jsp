<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,java.net.URLEncoder,java.nio.charset.StandardCharsets,com.webshopping.dao.UserDao,com.webshopping.dao.MessageDao,com.webshopping.model.Userinfo,com.webshopping.model.ProductInfo,com.webshopping.model.MessageInfo,com.webshopping.util.HtmlUtil,com.webshopping.util.WebSecurityUtil" %>
<jsp:useBean id="loginUser" class="com.webshopping.model.Userinfo" scope="session" />
<jsp:useBean id="catalog" class="com.webshopping.model.CatalogBean" scope="application" />
<%!
  private String messageStatusLabel(String status) {
    if (MessageDao.STATUS_REPLIED.equalsIgnoreCase(status)) {
      return "\u5df2\u56de\u590d";
    }
    if (MessageDao.STATUS_HIDDEN.equalsIgnoreCase(status)) {
      return "\u9690\u85cf";
    }
    return "\u5f85\u5904\u7406";
  }
%>
<%
  if (!loginUser.isAdmin()) {
    String redirectMsg = URLEncoder.encode("\u9700\u8981\u7ba1\u7406\u5458\u6743\u9650\u3002", StandardCharsets.UTF_8.name());
    response.sendRedirect("login.jsp?msg=" + redirectMsg);
    return;
  }

  String msg = HtmlUtil.escape(request.getParameter("msg"));
  String csrfToken = WebSecurityUtil.csrfToken(session);

  List<Userinfo> users;
  List<ProductInfo> products;
  List<MessageInfo> messages;
  String error = null;
  try {
    users = new UserDao().listUsers();
    products = catalog.getProducts();
    messages = new MessageDao().listAllMessages(200);
  } catch (Exception e) {
    users = java.util.Collections.emptyList();
    products = java.util.Collections.emptyList();
    messages = java.util.Collections.emptyList();
    error = "\u540e\u53f0\u6570\u636e\u52a0\u8f7d\u5931\u8d25\u3002";
  }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>&#21518;&#21488;&#31649;&#29702;</title>
<link rel="stylesheet" rev="stylesheet" href="css/global.css" type="text/css" media="all" />
<style>
  .admin-panel {
    float: left;
    width: 100%;
    box-sizing: border-box;
    padding: 12px;
    border: 1px solid #E5E5E5;
    background: #fff;
  }
  .admin-section {
    margin-top: 18px;
  }
  .admin-section h4 {
    margin-bottom: 8px;
  }
  .admin-scroll {
    width: 100%;
    overflow-x: auto;
  }
  .admin-table {
    width: 100%;
    min-width: 700px;
    border-collapse: collapse;
    margin-bottom: 14px;
  }
  .admin-table.wide {
    min-width: 1180px;
  }
  .admin-table th,
  .admin-table td {
    border: 1px solid #ddd;
    padding: 6px;
    font-size: 12px;
    vertical-align: top;
    background: #fff;
  }
  .admin-table th {
    background: #f8f8f8;
    white-space: nowrap;
  }
  .readonly-tag {
    color: #888;
    font-size: 12px;
    margin-left: 4px;
  }
  .img-preview {
    display: block;
    width: 60px;
    height: 45px;
    object-fit: cover;
    border: 1px solid #ddd;
    margin-bottom: 4px;
    background: #fafafa;
  }
  .file-input {
    width: 150px;
    font-size: 12px;
  }
  .small-input {
    width: 56px;
  }
  .name-input {
    width: 150px;
  }
  .path-tip {
    color: #777;
    font-size: 11px;
    line-height: 1.4;
    margin-top: 2px;
    word-break: break-all;
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
            <div class="admin-panel">
              <h3>&#21518;&#21488;&#31649;&#29702;</h3>
              <hr size="1">

<%
  if (msg != null && !msg.trim().isEmpty()) {
%>
              <div style="color:#006400;margin-top:10px;"><%= msg %></div>
<%
  }
  if (error != null) {
%>
              <div style="color:#B22222;margin-top:10px;"><%= HtmlUtil.escape(error) %></div>
<%
  }
%>

              <div class="admin-section">
                <h4>1. &#29992;&#25143;&#35282;&#33394;&#31649;&#29702;</h4>
                <div class="admin-scroll">
                  <table class="admin-table">
                    <tr>
                      <th>&#29992;&#25143;&#21517;</th>
                      <th>&#24615;&#21035;</th>
                      <th>&#29233;&#22909;</th>
                      <th>&#24403;&#21069;&#35282;&#33394;</th>
                      <th>&#25805;&#20316;</th>
                    </tr>
<%
  for (Userinfo u : users) {
    boolean defaultAdmin = "admin".equalsIgnoreCase(u.getUsername());
%>
                    <tr>
                      <td><%= HtmlUtil.escape(u.getUsername()) %></td>
                      <td><%= HtmlUtil.escape(u.getSex()) %></td>
                      <td><%= HtmlUtil.escape(u.getHobby()) %></td>
                      <td><%= HtmlUtil.escape(u.getRole()) %></td>
                      <td>
                        <form action="admin" method="post" style="display:inline;">
                          <input type="hidden" name="action" value="setRole">
                          <input type="hidden" name="username" value="<%= HtmlUtil.escape(u.getUsername()) %>">
                          <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
                          <select name="role" <%= defaultAdmin ? "disabled" : "" %>>
                            <option value="USER" <%= "USER".equalsIgnoreCase(u.getRole()) ? "selected" : "" %>>&#26222;&#36890;&#29992;&#25143;</option>
                            <option value="ADMIN" <%= "ADMIN".equalsIgnoreCase(u.getRole()) ? "selected" : "" %>>&#31649;&#29702;&#21592;</option>
                          </select>
<%
    if (defaultAdmin) {
%>
                          <span class="readonly-tag">&#40664;&#35748;&#31649;&#29702;&#21592;&#19981;&#21487;&#20462;&#25913;</span>
<%
    } else {
%>
                          <button type="submit">&#20445;&#23384;</button>
<%
    }
%>
                        </form>
                      </td>
                    </tr>
<%
  }
%>
                  </table>
                </div>
              </div>

              <div class="admin-section">
                <h4>2. &#21830;&#21697;&#31649;&#29702;</h4>
                <div class="admin-scroll">
                  <table class="admin-table wide">
                    <tr>
                      <th>ID</th>
                      <th>&#21517;&#31216;</th>
                      <th>&#21806;&#20215;</th>
                      <th>&#21407;&#20215;</th>
                      <th>&#24050;&#21806;</th>
                      <th>&#28857;&#20987;&#37327;</th>
                      <th>&#20998;&#31867;ID</th>
                      <th>&#23553;&#38754;&#22270;</th>
                      <th>&#35814;&#24773;&#22270;</th>
                      <th>&#25805;&#20316;</th>
                    </tr>
<%
  for (ProductInfo p : products) {
    String safeName = HtmlUtil.escape(p.getName());
    String safeImagePath = HtmlUtil.escape(p.getImagePath());
    String safeDetailImagePath = HtmlUtil.escape(p.getDetailImagePath());
    String formId = "productForm_" + p.getId();
%>
                    <tr>
                      <td>
                        <%= p.getId() %>
                        <form id="<%= formId %>" action="admin" method="post" enctype="multipart/form-data">
                          <input type="hidden" name="action" value="updateProduct">
                          <input type="hidden" name="id" value="<%= p.getId() %>">
                          <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
                          <input type="hidden" name="imagePath" value="<%= safeImagePath %>">
                          <input type="hidden" name="detailImagePath" value="<%= safeDetailImagePath %>">
                        </form>
                      </td>
                      <td><input class="name-input" form="<%= formId %>" type="text" name="productName" value="<%= safeName %>"></td>
                      <td><input class="small-input" form="<%= formId %>" type="text" name="price" value="<%= p.getPrice() %>"></td>
                      <td><input class="small-input" form="<%= formId %>" type="text" name="originalPrice" value="<%= p.getOriginalPrice() %>"></td>
                      <td><input class="small-input" form="<%= formId %>" type="text" name="soldCount" value="<%= p.getSoldCount() %>"></td>
                      <td><%= p.getClickCount() %></td>
                      <td><input class="small-input" form="<%= formId %>" type="text" name="categoryId" value="<%= p.getCategoryId() %>"></td>
                      <td>
                        <img class="img-preview" src="<%= safeImagePath %>" alt="cover">
                        <input class="file-input" form="<%= formId %>" type="file" name="coverImage" accept="image/*">
                        <div class="path-tip">&#24403;&#21069;&#65306;<%= safeImagePath %></div>
                      </td>
                      <td>
                        <img class="img-preview" src="<%= safeDetailImagePath %>" alt="detail">
                        <input class="file-input" form="<%= formId %>" type="file" name="detailImage" accept="image/*">
                        <div class="path-tip">&#24403;&#21069;&#65306;<%= safeDetailImagePath %></div>
                      </td>
                      <td><button type="submit" form="<%= formId %>">&#26356;&#26032;</button></td>
                    </tr>
<%
  }
%>
                  </table>
                </div>
              </div>

              <div class="admin-section">
                <h4>3. &#22312;&#32447;&#30041;&#35328;&#31649;&#29702;</h4>
                <div class="admin-scroll">
                  <table class="admin-table">
                    <tr>
                      <th>ID</th>
                      <th>&#30041;&#35328;&#20154;</th>
                      <th>&#32852;&#31995;&#26041;&#24335;</th>
                      <th>&#30041;&#35328;&#20869;&#23481;</th>
                      <th>&#31649;&#29702;&#21592;&#22238;&#22797;</th>
                      <th>&#29366;&#24577;</th>
                      <th>&#21019;&#24314;&#26102;&#38388;</th>
                      <th>&#25805;&#20316;</th>
                    </tr>
<%
  for (MessageInfo m : messages) {
    String formId = "messageForm_" + m.getId();
    String mStatus = m.getStatus() == null ? "" : m.getStatus().trim().toUpperCase();
%>
                    <tr>
                      <td>
                        <%= m.getId() %>
                        <form id="<%= formId %>" action="admin" method="post">
                          <input type="hidden" name="action" value="replyMessage">
                          <input type="hidden" name="id" value="<%= m.getId() %>">
                          <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
                        </form>
                      </td>
                      <td><%= HtmlUtil.escape(m.getAuthor()) %></td>
                      <td><%= HtmlUtil.escape(m.getContact()) %></td>
                      <td style="max-width:180px;"><%= HtmlUtil.escape(m.getContent()) %></td>
                      <td><textarea form="<%= formId %>" name="reply" rows="2" cols="24"><%= HtmlUtil.escape(m.getReply()) %></textarea></td>
                      <td>
                        <select form="<%= formId %>" name="status">
                          <option value="<%= MessageDao.STATUS_OPEN %>" <%= MessageDao.STATUS_OPEN.equalsIgnoreCase(mStatus) ? "selected" : "" %>><%= messageStatusLabel(MessageDao.STATUS_OPEN) %></option>
                          <option value="<%= MessageDao.STATUS_REPLIED %>" <%= MessageDao.STATUS_REPLIED.equalsIgnoreCase(mStatus) ? "selected" : "" %>><%= messageStatusLabel(MessageDao.STATUS_REPLIED) %></option>
                          <option value="<%= MessageDao.STATUS_HIDDEN %>" <%= MessageDao.STATUS_HIDDEN.equalsIgnoreCase(mStatus) ? "selected" : "" %>><%= messageStatusLabel(MessageDao.STATUS_HIDDEN) %></option>
                        </select>
                      </td>
                      <td><%= m.getCreatedAt() %></td>
                      <td><button type="submit" form="<%= formId %>">&#20445;&#23384;</button></td>
                    </tr>
<%
  }
%>
                  </table>
                </div>
              </div>
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
