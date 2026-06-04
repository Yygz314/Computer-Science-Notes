<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.webshopping.util.HtmlUtil,com.webshopping.stats.SiteStats" %>
<jsp:useBean id="loginUser" class="com.webshopping.model.Userinfo" scope="session" />
<%
  String userName = loginUser.getUsername();
  String q = request.getParameter("q");
  String safeQ = HtmlUtil.escape(q == null ? "" : q.trim());
  String safeUserName = HtmlUtil.escape(userName == null ? "" : userName.trim());
  boolean loggedIn = !safeUserName.isEmpty();
  boolean isAdmin = loggedIn && loginUser.isAdmin();
  long startupMillis = SiteStats.getStartupTimeMillis();
%>
<script>
function openWin(url,width,height){
  window.open(url,'','width='+width+',height='+height+',left='+(screen.width-width)/2+',top='+(screen.height-height)/2);
}

function pad2(n){
  return n < 10 ? ('0' + n) : ('' + n);
}

function formatNow(d){
  return d.getFullYear() + '-' + pad2(d.getMonth() + 1) + '-' + pad2(d.getDate())
    + ' ' + pad2(d.getHours()) + ':' + pad2(d.getMinutes()) + ':' + pad2(d.getSeconds());
}

function formatDuration(ms){
  var totalSeconds = Math.max(0, Math.floor(ms / 1000));
  var days = Math.floor(totalSeconds / 86400);
  totalSeconds = totalSeconds % 86400;
  var hours = Math.floor(totalSeconds / 3600);
  totalSeconds = totalSeconds % 3600;
  var minutes = Math.floor(totalSeconds / 60);
  var seconds = totalSeconds % 60;
  return days + '\u5929 ' + hours + '\u5c0f\u65f6 ' + minutes + '\u5206 ' + seconds + '\u79d2';
}

var siteStartupMillis = <%= startupMillis %>;

function refreshHeaderClock(){
  var now = new Date();
  var nowNode = document.getElementById('live_now_time');
  var runNode = document.getElementById('live_uptime');
  if (nowNode) {
    nowNode.innerHTML = formatNow(now);
  }
  if (runNode) {
    runNode.innerHTML = formatDuration(now.getTime() - siteStartupMillis);
  }
}

window.setInterval(refreshHeaderClock, 1000);
window.addEventListener('load', refreshHeaderClock);
</script>

<div id="logo">
  <a href="index.jsp"><img src="images/logo.png" border="0" alt="ASW" /></a>
</div>

<div id="header_stats">
  <div class="header-stat-line">&#24403;&#21069;&#65306;<span id="live_now_time" class="header-stat-value"></span></div>
  <div class="header-stat-line">&#36816;&#34892;&#65306;<span id="live_uptime" class="header-stat-value"></span></div>
  <div class="header-stat-line">
    &#22312;&#32447;&#65306;<span class="header-stat-value header-stat-online"><%= SiteStats.getOnlineVisitorCount() %></span>
    &#35775;&#23458;&#65306;<span class="header-stat-value"><%= SiteStats.getTotalVisitorCount() %></span>
    &#27983;&#35272;&#65306;<span class="header-stat-value"><%= SiteStats.getTotalPageViewCount() %></span>
  </div>
</div>

<div id="header_right">
<%
  if (!loggedIn) {
%>
  &#27426;&#36814;&#20809;&#20020;&#65306;<a href="reg.jsp">&#27880;&#20876;</a>/<a href="login.jsp">&#30331;&#24405;</a>
<%
  } else {
%>
  <%= safeUserName %>&#65306;<span style="color:red;">&#27426;&#36814;&#22238;&#26469;!</span>
<%
  }
%>
  <br>
  <img src="images/chat.png" alt="contact" />&nbsp;<a onclick="openWin('contact.jsp',300,200)" style="cursor:pointer">&#32852;&#31995;&#25105;&#20204;</a>
  <img src="images/order.png" alt="cart" />&nbsp;<a href="cart_view.jsp">&#36141;&#29289;&#36710;</a>
<%
  if (loggedIn) {
%>
  &nbsp;|&nbsp;<a href="orders.jsp">&#25105;&#30340;&#35746;&#21333;</a>
<%
  }
  if (isAdmin) {
%>
  &nbsp;|&nbsp;<a href="admin.jsp" style="color:#B22222;font-weight:bold;">&#21518;&#21488;&#31649;&#29702;</a>
<%
  }
%>
</div>

<div id="headermenu">
  <ul id="menu">
    <li><a class="li" href="index.jsp"><img src="images/dh_1.png" border="0" alt="" />&nbsp;&#39318;&#39029;</a></li>
    <li><a class="li" href="#"><img src="images/dh_2.png" border="0" alt="" />&nbsp;&#21830;&#22478;&#20844;&#21578;</a></li>
    <li><a class="li" href="product.jsp"><img src="images/dh_3.png" border="0" alt="" />&nbsp;&#20840;&#37096;&#21830;&#21697;</a></li>
    <li><a class="li" href="#"><img src="images/dh_4.png" border="0" alt="" />&nbsp;&#20184;&#27454;&#26041;&#24335;</a></li>
    <li><a class="li" href="contact.jsp"><img src="images/dh_5.png" border="0" alt="" />&nbsp;&#20851;&#20110;&#25105;&#20204;</a></li>
    <li><a class="li" href="messages.jsp"><img src="images/dh_6.png" border="0" alt="" />&nbsp;&#22312;&#32447;&#30041;&#35328;</a></li>
  </ul>
</div>

<div id="search">
  <form id="form1" name="search" method="get" action="product.jsp">
    <input type="text" name="q" style="color:#333;vertical-align:middle;" value="<%= safeQ %>" placeholder="&#35831;&#36755;&#20837;&#20851;&#38190;&#35789;" />&nbsp;
    <input name="imageField" type="image" align="absmiddle" src="images/search.gif" alt="search" />
  </form>
</div>
