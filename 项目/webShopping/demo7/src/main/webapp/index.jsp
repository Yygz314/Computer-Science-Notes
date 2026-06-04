<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>爱尚网扇品</title>
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
            <div id="left_column"><jsp:include page="left_column.jsp" /></div>
          </td>
          <td width="738" valign="top">
            <div id="center_column"><jsp:include page="center_column.jsp" /></div>
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

