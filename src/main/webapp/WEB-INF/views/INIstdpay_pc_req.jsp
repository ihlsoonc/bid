<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.*"%>

<!DOCTYPE html>
<html lang="ko">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport"
        content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>KG이니시스 결제샘플</title>

    <!-- 테스트 JS -->
    <script language="javascript" type="text/javascript" src="https://stgstdpay.inicis.com/stdjs/INIStdPay.js"
        charset="UTF-8"></script>
    <!-- 운영 JS
    <script language="javascript" type="text/javascript" src="https://stdpay.inicis.com/stdjs/INIStdPay.js" charset="UTF-8"></script> -->
    <script type="text/javascript">
        function paybtn() {
            INIStdPay.pay('SendPayForm_id');
        }
    </script>
</head>

<body>

    <!-- 본문 -->
    <main>
        <!-- 페이지타이틀 -->
        <section>
            <div>
                <h2>일반결제</h2>
                <p>KG이니시스 결제창을 호출하여 다양한 지불수단으로 안전한 결제를 제공하는 서비스</p>
            </div>
        </section>
        <!-- //페이지타이틀 -->

        <!-- 카드CONTENTS -->
        <section>
            <div>
                <div>
                    <h3>PC 일반결제 리퀘스트</h3>
                </div>

                <!-- 유의사항 -->
                <div>
                    <h4>※ 유의사항</h4>
                    <ul>
                        <li>테스트MID 결제시 실 승인되며, 당일 자정(24:00) 이전에 자동으로 취소처리 됩니다.</li>
                        <li>가상계좌 채번 후 입금할 경우 자동환불되지 않사오니, 가맹점관리자 내 "입금통보테스트" 메뉴를
                            이용부탁드립니다.<br>(실 입금하신 경우 별도로 환불요청해주셔야 합니다.)</li>
                        <li>국민카드 정책상 테스트 결제가 불가하여 오류가 발생될 수 있습니다. 국민, 카카오뱅크 외 다른
                            카드로 테스트결제 부탁드립니다.</li>
                    </ul>
                </div>
                <!-- //유의사항 -->

                <form name="" id="SendPayForm_id" method="post">
                    <div>
                        <input type="hidden" name="version" value="1.0">

                        <label>gopaymethod</label>
                        <input type="text" name="gopaymethod" value="Card:Directbank:vbank">

                        <label>mid</label>
                        <input type="text" name="mid" value="${mid}">
                        <br/>
                        <label>oid</label>
                        <input type="text" name="oid" value="${oid}">
                        <br/>
                        <label>금액</label>
                        <input type="text" name="price" value="${price}">
                        <br/>
                        <label>결제시각</label>
                        <input type="text" name="timestamp" value="${timestamp}">
                        <br/>
                        <input type="hidden" name="use_chkfake" value="${use_chkfake}">
                        <input type="hidden" name="signature" value="${signature}">
                        <input type="hidden" name="verification" value="${verification}">
                        <input type="hidden" name="mKey" value="${mKey}">
                        <input type="hidden" name="currency" value="WON">
                        <br/>
                        <label>내역</label>
                        <input type="text" name="goodname" value="${goodname}">
                        <br/>
                        <label>구매자</label>
                        <input type="text" name="buyername" value="${buyername}">
                        <br/>
                        <label>전화번호</label>
                        <input type="text" name="buyertel" value="${buyertel}">
                        <br/>
                        <label>이메일</label>
                        <input type="text" name="buyeremail" value="${buyeremail}">
                        <br/>
                        <input type="hidden" name="returnUrl" value="${returnUrl}">
                        <input type="hidden" name="closeUrl" value="${closeUrl}">

                        <label>acceptmethod</label>
                        <input type="text" name="acceptmethod" value="HPP(1):below1000:centerCd(Y)">
                    </div>
                </form>

                <button onclick="location.href='http://localhost:8080/bidseats?userId=${userId}'">결제 요청</button>
            </div>
        </section>

    </main>

</body>

</html>
