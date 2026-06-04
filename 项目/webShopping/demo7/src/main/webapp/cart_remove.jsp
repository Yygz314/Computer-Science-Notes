<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  response.sendRedirect("cart_view.jsp?msg=" + java.net.URLEncoder.encode("Entry upgraded. Please remove/clear items directly on cart page.", "UTF-8"));
%>
