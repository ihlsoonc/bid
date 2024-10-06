<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<script language="javascript" type="text/javascript" src="https://stdpay.inicis.com/stdjs/INIStdPay_close.js" charset="UTF-8"></script>

<!DOCTYPE html>
<html lang="ko">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>오류 발생</title>
        
        <script type="text/javascript">
            function redirectToLogin() {
                window.location.href = "http://localhost:8080/userlogin";
            }
        </script>
    </head>
    <body>
        <div class="error-message">
            <div>사용자에 의해 결제 요청이 취소되었습니다.</div>
        </div>

        <!-- 리다이렉트 버튼 -->
        <button class="redirect-button" onclick="redirectToLogin()">로그인 페이지로 이동</button>
    </body>
</html>
