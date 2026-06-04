<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,com.webshopping.dao.MessageDao,com.webshopping.model.MessageInfo,com.webshopping.model.Userinfo,com.webshopping.util.HtmlUtil,com.webshopping.util.WebSecurityUtil" %>
<%
  String msg = HtmlUtil.escape(request.getParameter("msg"));
  String csrfToken = WebSecurityUtil.csrfToken(session);
  Userinfo loginUser = (Userinfo) session.getAttribute("loginUser");
  String defaultAuthor = "\u6e38\u5ba2";
  if (loginUser != null && loginUser.getUsername() != null && !loginUser.getUsername().trim().isEmpty()) {
    defaultAuthor = loginUser.getUsername().trim();
  }

  List<MessageInfo> messages;
  String error = null;
  MessageDao messageDao = new MessageDao();
  try {
    messages = messageDao.listPublicMessages(100);
  } catch (Exception e) {
    messages = java.util.Collections.emptyList();
    error = "\u7559\u8a00\u52a0\u8f7d\u5931\u8d25\u3002";
  }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>&#22312;&#32447;&#30041;&#35328;</title>
<link rel="stylesheet" rev="stylesheet" href="css/global.css" type="text/css" media="all" />
<style>
  .message-form-table {
    width: 100%;
    margin-top: 12px;
  }
  .message-form-table td {
    padding: 6px 8px;
    vertical-align: top;
  }
  .message-form-table input[type="text"],
  .message-form-table textarea {
    border: 1px solid #999;
    box-sizing: border-box;
    font-family: "Microsoft YaHei", SimSun, Arial, sans-serif;
    font-size: 13px;
  }
  .message-form-table textarea {
    width: 100%;
    min-height: 78px;
  }
  .message-item {
    border: 1px solid #ddd;
    padding: 10px;
    margin-bottom: 10px;
    background: #fff;
    line-height: 1.7;
  }
  .message-time {
    color: #888;
    margin-left: 8px;
  }
  .reply-box {
    margin-top: 8px;
    padding: 8px;
    background: #F6F6F6;
    border-left: 3px solid #F0AD4E;
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
            <div class="divBorder" style="padding:12px;">
              <h3>&#22312;&#32447;&#30041;&#35328;</h3>
              <hr size="1"><br>

<%
  if (msg != null && !msg.trim().isEmpty()) {
%>
              <div style="color:#006400;margin-bottom:10px;"><%= msg %></div>
<%
  }
  if (error != null) {
%>
              <div style="color:#B22222;margin-bottom:10px;"><%= HtmlUtil.escape(error) %></div>
<%
  }
%>

              <form action="message" method="post" style="margin-bottom:16px;">
                <input type="hidden" name="action" value="add">
                <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
                <table class="message-form-table">
                  <tr>
                    <td width="70">&#31216;&#21628;&#65306;</td>
                    <td><input type="text" name="author" value="<%= HtmlUtil.escape(defaultAuthor) %>" style="width:220px;" maxlength="50"></td>
                    <td width="90">&#32852;&#31995;&#26041;&#24335;&#65306;</td>
                    <td><input type="text" name="contact" style="width:220px;" maxlength="100" placeholder="QQ / &#37038;&#31665; / &#25163;&#26426;"></td>
                  </tr>
                  <tr>
                    <td>&#30041;&#35328;&#65306;</td>
                    <td colspan="3"><textarea name="content" rows="4" maxlength="1000" required></textarea></td>
                  </tr>
                  <tr>
                    <td></td>
                    <td colspan="3"><button type="submit">&#25552;&#20132;&#30041;&#35328;</button></td>
                  </tr>
                </table>
              </form>

<%
  if (messages.isEmpty()) {
%>
              <div style="color:#666;">&#26242;&#26080;&#30041;&#35328;&#65292;&#27426;&#36814;&#21457;&#24067;&#31532;&#19968;&#26465;&#12290;</div>
<%
  } else {
    for (MessageInfo m : messages) {
      String safeAuthor = HtmlUtil.escape(m.getAuthor());
      String safeContact = HtmlUtil.escape(m.getContact());
      String safeContent = HtmlUtil.escape(m.getContent());
      String safeReply = HtmlUtil.escape(m.getReply());
%>
              <div class="message-item">
                <div>
                  <strong><%= safeAuthor %></strong>
                  <span class="message-time"><%= m.getCreatedAt() %></span>
<%
      if (m.getContact() != null && !m.getContact().trim().isEmpty()) {
%>
                  <span style="color:#666;margin-left:12px;">&#32852;&#31995;&#26041;&#24335;&#65306;<%= safeContact %></span>
<%
      }
%>
                </div>
                <div style="margin-top:6px;color:#333;"><%= safeContent %></div>
<%
      if (m.getReply() != null && !m.getReply().trim().isEmpty()) {
%>
                <div class="reply-box">
                  <strong>&#31649;&#29702;&#21592;&#22238;&#22797;&#65306;</strong><%= safeReply %>
                </div>
<%
      }
%>
              </div>
<%
    }
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
