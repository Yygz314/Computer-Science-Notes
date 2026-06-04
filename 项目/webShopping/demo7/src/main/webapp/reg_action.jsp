<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.webshopping.dao.UserDao,com.webshopping.model.Userinfo,java.net.URLEncoder,java.nio.charset.StandardCharsets,java.sql.SQLException" %>
<%
  request.setCharacterEncoding("UTF-8");

  String username = request.getParameter("userName");
  String pwd = request.getParameter("pwd");
  String pwd1 = request.getParameter("pwd1");
  String sex = request.getParameter("sex");
  String[] interests = request.getParameterValues("interest");

  username = username == null ? "" : username.trim();
  pwd = pwd == null ? "" : pwd.trim();
  pwd1 = pwd1 == null ? "" : pwd1.trim();

  if (username.isEmpty() || pwd.isEmpty()) {
    response.sendRedirect("reg.jsp?msg=" + URLEncoder.encode("用户名和密码不能为空。", StandardCharsets.UTF_8.name()));
    return;
  }
  if (username.length() < 3 || username.length() > 30) {
    response.sendRedirect("reg.jsp?msg=" + URLEncoder.encode("用户名长度需在 3-30 之间。", StandardCharsets.UTF_8.name()));
    return;
  }
  if (pwd.length() < 6) {
    response.sendRedirect("reg.jsp?msg=" + URLEncoder.encode("密码长度至少 6 位。", StandardCharsets.UTF_8.name()));
    return;
  }
  if (!pwd.equals(pwd1)) {
    response.sendRedirect("reg.jsp?msg=" + URLEncoder.encode("两次输入的密码不一致。", StandardCharsets.UTF_8.name()));
    return;
  }

  Userinfo regUser = new Userinfo();
  regUser.setUsername(username);
  regUser.setPwd(pwd);
  regUser.setSex((sex == null || sex.trim().isEmpty()) ? "未填写" : sex.trim());
  regUser.setHobby((interests == null || interests.length == 0) ? "无" : String.join("、", interests));
  regUser.setRole("USER");

  UserDao userDao = new UserDao();
  try {
    if (userDao.existsByUsername(username)) {
      response.sendRedirect("reg.jsp?msg=" + URLEncoder.encode("用户名已存在，请更换。", StandardCharsets.UTF_8.name()));
      return;
    }
    if (!userDao.register(regUser)) {
      response.sendRedirect("reg.jsp?msg=" + URLEncoder.encode("注册失败，请稍后重试。", StandardCharsets.UTF_8.name()));
      return;
    }
  } catch (SQLException e) {
    response.sendRedirect("reg.jsp?msg=" + URLEncoder.encode("数据库连接失败，请稍后重试。", StandardCharsets.UTF_8.name()));
    return;
  }

  session.setAttribute("user", regUser.getUsername());
  session.setAttribute("loginUser", regUser);
  request.setAttribute("regUser", regUser);
  request.getRequestDispatcher("reg_success.jsp").forward(request, response);
%>
