<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List,com.webshopping.model.ProductInfo,com.webshopping.util.HtmlUtil" %>
<jsp:useBean id="catalog" class="com.webshopping.model.CatalogBean" scope="application" />
<%
  List<ProductInfo> latestProducts = catalog.getLatestProducts();
%>
<link rel="stylesheet" rev="stylesheet" href="css/center_column.css" type="text/css" media="all" />

<script language="javascript">
function $a(id,tag){var re=(id&&typeof id!="string")?id:document.getElementById(id);if(!tag){return re;}else{return re.getElementsByTagName(tag);}}
function movec(){
  var o=$a("bd1lfimg","");
  var oli=$a("bd1lfimg","dl");
  var oliw=oli[0].offsetWidth;
  var ow=o.offsetWidth-2;
  var dnow=0;
  var olf=oliw-(ow-oliw+10)/2;
  o["scrollLeft"]=olf+(dnow*oliw);
  var extime;
  var rq=$a("bd1lfsj","li");
  for(var i=0;i<rq.length;i++){reg(i);} ;
  oli[dnow].className=rq[dnow].className="show";
  var wwww=setInterval(uu,2000);
  function reg(i){rq[i].onclick=function(){oli[dnow].className=rq[dnow].className="";dnow=i;oli[dnow].className=rq[dnow].className="show";mv();}}
  function mv(){clearInterval(extime);clearInterval(wwww);extime=setInterval(bc,15);wwww=setInterval(uu,5000);} 
  function bc(){var ns=((dnow*oliw+olf)-o["scrollLeft"]);var v=ns>0?Math.ceil(ns/10):Math.floor(ns/10);o["scrollLeft"]+=v;if(v==0){clearInterval(extime);oli[dnow].className=rq[dnow].className="show";}}
  function uu(){if(dnow<oli.length-2){oli[dnow].className=rq[dnow].className="";dnow++;oli[dnow].className=rq[dnow].className="show";}else{oli[dnow].className=rq[dnow].className="";dnow=0;oli[dnow].className=rq[dnow].className="show";}mv();}
  o.onmouseover=function(){clearInterval(extime);clearInterval(wwww);} ;
  o.onmouseout=function(){extime=setInterval(bc,15);wwww=setInterval(uu,5000);} ;
}
</script>

<div id="content">
  <div id="main">
    <div class="sub_box">
      <div id="p-select" class="sub_nav">
        <div class="sub_no" id="bd1lfsj">
          <ul>
            <li class="show">1</li><li class="">2</li>
          </ul>
        </div>
      </div>
      <div id="bd1lfimg">
        <div>
          <dl class="show"></dl>
          <dl class=""><dt><a href="#"><img src="images/asw.jpg" alt="爱尚网扇品"></a></dt></dl>
          <dl class=""><dt><a href="#"><img src="images/summer.jpg" alt="清爽夏日"></a></dt></dl>
        </div>
      </div>
    </div>
    <script type="text/javascript">movec();</script>
  </div>
</div>

<div class="divBorder">
  <div id="select_title">
    <h3>&nbsp;&nbsp;最新商品 <img src="images/new.gif" alt="new" /></h3>
    <hr size="1">
  </div>

<%
  for (ProductInfo p : latestProducts) {
    String safeName = HtmlUtil.escape(p.getName());
%>
  <div id="select_product">
    <div id="select_img"><a href="item.jsp?id=<%= p.getId() %>"><img width="205" height="154" src="<%= p.getImagePath() %>" alt="<%= safeName %>"></a></div>
    <div id="select_about">
      <a class="a" href="item.jsp?id=<%= p.getId() %>">品名：<%= safeName %></a><br>
      促销价：<span style="color:#FF6600;font-weight:bold;"><%= (int) p.getPrice() %></span>元<br>
      已售出：<span style="font-weight:bold;"><%= p.getSoldCount() %></span>件<br>
      点击量：<span style="font-weight:bold;color:#1E6AA5;"><%= p.getClickCount() %></span>
    </div>
  </div>
<%
  }
%>
</div>
