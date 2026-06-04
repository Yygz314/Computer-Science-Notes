<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Calendar,java.util.List,com.webshopping.model.ProductInfo,com.webshopping.util.HtmlUtil" %>
<jsp:useBean id="loginUser" class="com.webshopping.model.Userinfo" scope="session" />
<jsp:useBean id="catalog" class="com.webshopping.model.CatalogBean" scope="application" />
<%
  List<ProductInfo> hotProducts = catalog.getHotProducts();
%>

<table border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top" width="4" height="4"><img height="4" src="images/line_01.gif" width="4" alt="" /></td>
    <td background="images/line_02.gif" height="4"></td>
    <td valign="top" width="4" height="4"><img height="4" src="images/line_03.gif" width="4" alt="" /></td>
  </tr>
  <tr>
    <td background="images/line_04.gif"></td>
    <td>
      <div id="left_login">
        <img src="images/vip_logo.png" alt="vip" /><br><br>
<%
  String userName = loginUser.getUsername();
  if (userName == null || userName.trim().isEmpty()) {
%>
        <form action="login" method="post">
          <font class="zt1">用户名：</font><input type="text" name="txtName" class="input"><br><br>
          <font class="zt1">密&nbsp;&nbsp;码：</font><input type="password" name="pwd" class="input"><br>
          <a href="reg.jsp"><img src="images/reg_button.gif" border="0" alt="reg" /></a>
          <input name="imageField" type="image" src="images/login_button.gif" alt="login" />
        </form>
<%
  } else {
    Calendar cal = Calendar.getInstance();
    int hour = cal.get(Calendar.HOUR_OF_DAY);
    String greet;
    if (hour >= 5 && hour < 8) {
      greet = "早上好";
    } else if (hour < 11) {
      greet = "上午好";
    } else if (hour < 13) {
      greet = "中午好";
    } else if (hour < 18) {
      greet = "下午好";
    } else if (hour < 23) {
      greet = "晚上好";
    } else {
      greet = "夜深了";
    }
%>
        <span style="color:red"><%= greet %>&nbsp;<%= userName %></span>
        <br><br>
        <form action="exit.jsp" method="post">
          <input type="submit" name="exit" value="退出">
        </form>
<%
  }
%>
      </div>
    </td>
    <td background="images/line_05.gif">&nbsp;</td>
  </tr>
  <tr>
    <td valign="top" width="4" height="4"><img height="4" src="images/line_06.gif" width="4" alt="" /></td>
    <td background="images/line_07.gif"></td>
    <td valign="top" width="4" height="4"><img height="4" src="images/line_08.gif" width="4" alt="" /></td>
  </tr>
</table><br>

<table border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top" width="4" height="4"><img height="4" src="images/line_01.gif" width="4" alt="" /></td>
    <td background="images/line_02.gif" height="4"></td>
    <td valign="top" width="4" height="4"><img height="4" src="images/line_03.gif" width="4" alt="" /></td>
  </tr>
  <tr>
    <td background="images/line_04.gif"></td>
    <td>
      <div id="left_sort">
        <img src="images/sort_logo.png" alt="sort" /><br><br>
        <div id="sort_menu">
          <ul id="menu">
            <li><img src="images/sort_menu.gif" alt="" />&nbsp;&nbsp;<a class="li_sort" href="sort.jsp?id=1">日式女扇</a></li>
            <hr size="1" />
            <li><img src="images/sort_menu.gif" alt="" />&nbsp;&nbsp;<a class="li_sort" href="sort.jsp?id=2">仿古男扇</a></li>
            <hr size="1" />
            <li><img src="images/sort_menu.gif" alt="" />&nbsp;&nbsp;<a class="li_sort" href="sort.jsp?id=3">韩国扇</a></li>
            <hr size="1" />
            <li><img src="images/sort_menu.gif" alt="" />&nbsp;&nbsp;<a class="li_sort" href="sort.jsp?id=4">檀香扇</a></li>
            <hr size="1" />
            <li><img src="images/sort_menu.gif" alt="" />&nbsp;&nbsp;<a class="li_sort" href="sort.jsp?id=5">礼品广告扇</a></li>
          </ul>
        </div>
      </div>
    </td>
    <td background="images/line_05.gif">&nbsp;</td>
  </tr>
  <tr>
    <td valign="top" width="4" height="4"><img height="4" src="images/line_06.gif" width="4" alt="" /></td>
    <td background="images/line_07.gif"></td>
    <td valign="top" width="4" height="4"><img height="4" src="images/line_08.gif" width="4" alt="" /></td>
  </tr>
</table><br>

<table border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top" width="4" height="4"><img height="4" src="images/line_01.gif" width="4" alt="" /></td>
    <td background="images/line_02.gif" height="4"></td>
    <td valign="top" width="4" height="4"><img height="4" src="images/line_03.gif" width="4" alt="" /></td>
  </tr>
  <tr>
    <td background="images/line_04.gif"></td>
    <td>
      <div id="left_sort">
        <img src="images/contact_logo.png" alt="contact" /><br><br>
        <div id="sort_menu">
          邮箱：<a href="mailto:82178712@qq.com">82178712@qq.com</a>
          <hr size="1" />
          QQ：82178712
          <hr size="1" />
          手机：18767168526
        </div>
      </div>
    </td>
    <td background="images/line_05.gif">&nbsp;</td>
  </tr>
  <tr>
    <td valign="top" width="4" height="4"><img height="4" src="images/line_06.gif" width="4" alt="" /></td>
    <td background="images/line_07.gif"></td>
    <td valign="top" width="4" height="4"><img height="4" src="images/line_08.gif" width="4" alt="" /></td>
  </tr>
</table><br>

<table border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top" width="4" height="4"><img height="4" src="images/line_01.gif" width="4" alt="" /></td>
    <td background="images/line_02.gif" height="4"></td>
    <td valign="top" width="4" height="4"><img height="4" src="images/line_03.gif" width="4" alt="" /></td>
  </tr>
  <tr>
    <td background="images/line_04.gif"></td>
    <td>
      <div id="left_sort">
        <img src="images/sale_logo.png" alt="sale" /><br><br>
<%
  for (ProductInfo p : hotProducts) {
    String safeName = HtmlUtil.escape(p.getName());
%>
        <div id="sale_sql">
          <div id="sale_sql_img">
            <a href="item.jsp?id=<%= p.getId() %>"><img src="<%= p.getImagePath() %>" alt="<%= safeName %>" width="50" height="50" /></a>
          </div>
          <div id="sale_sql_desc">
            <a class="hot" href="item.jsp?id=<%= p.getId() %>"><%= safeName %></a><br>
            促销价：<span style="color:#FF6600;font-weight:bold;"><%= (int) p.getPrice() %></span>元
          </div>
          <div style="clear:both;"></div>
        </div>
<%
  }
%>
        <div id="sale_sql_more"><a class="sql_more" href="product.jsp">查看更多商品</a></div>
      </div>
    </td>
    <td background="images/line_05.gif">&nbsp;</td>
  </tr>
  <tr>
    <td valign="top" width="4" height="4"><img height="4" src="images/line_06.gif" width="4" alt="" /></td>
    <td background="images/line_07.gif"></td>
    <td valign="top" width="4" height="4"><img height="4" src="images/line_08.gif" width="4" alt="" /></td>
  </tr>
</table>
