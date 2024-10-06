const crypto = require('crypto');
const request = require('request');
const { getUserInfo, updateBid } = require('./DbFunctions');


const url = {               
    return : 'http://localhost:5000/api/pgreturnpost',  //서버프로그램
    close : "http://localhost:5000/api/pgclose",    //서버 프로그램
}

const view = {
    start : 'INIstdpay_pc_req',             //결제창 시작전 확인화면
    return : 'INIstdpay_pc_return' ,        //승인후 리턴 화면
    close :'INIpay_pc_close',
}

const pgParams = {
    version : "1.0",
    mid: "INIpayTest", // 실제 값으로 대체
    currency : "WON",
    acceptmethod : "HPP(1):va_receipt:below1000:centerCd(Y)",
    gopaymethod : "Card:DirectBank:VBank:HPP",
    oid : "INIpayTest_01234", // 실제 값으로 대체
    signKey : "SU5JTElURV9UUklQTEVERVNfS0VZU1RS", // 실제 값으로 대체
    use_chkfake: "Y"  // 실제 값으로 대체
}

// getUrl 모듈 가져오기
const getUrl = require('./properties');

// pgstart에서 결제진행을 누르면 post /pgstart가 필요 없으면 오류발생 (이유는 ????)
exports.pgstartpost = (req,res) =>{
    console.log("pgstartpost", req.body)
    return
    
}

exports.pgstart = async (req,res) =>{
    console.log('pgstart........................................');
    console.log(req.query)
    const { userId, price, goodname } = req.query;
    try {
        const userInfo = await getUserInfo(userId);         // 사용자 이름, 전화번호, 이메일 조회
        user = userInfo[0];
        // console.log('user',user);
        // const oid = "INIpayTest_01234"; // 실제 OID 값으로 대체
        // const signKey = "SU5JTElURV9UUklQTEVERVNfS0VZU1RS";
        const timestamp = new Date().getTime();       // 타임스템프 [TimeInMillis(Long형)]
        const mKey = crypto.createHash("sha256").update(pgParams.signKey).digest('hex'); // SHA256 Hash값 [대상: mid와 매칭되는 signkey                   
        const signature  = crypto.createHash("sha256").update("oid="+pgParams.oid+"&price="+price+"&timestamp="+timestamp).digest('hex'); //SHA256 Hash값 [대상: oid, price, timestamp]
        const verification = crypto.createHash("sha256").update("oid="+pgParams.oid+"&price="+price+"&signKey="+pgParams.signKey+"&timestamp="+timestamp).digest('hex'); //SHA256 Hash값 [대상: oid, price, signkey, timestamp]
    
        const paymentData = {
            version: pgParams.version,
            gopaymethod: pgParams.gopaymethod,
            mid: pgParams.mid,
            oid: pgParams.oid,
            price: price || '', 
            timestamp: timestamp, // 실제 타임스탬프 값으로 대체
            use_chkfake: pgParams.use_chkfake, 
            signature: signature, // 실제 값으로 대체
            verification: verification, // 실제 값으로 대체
            mKey: mKey, // 실제 값으로 대체
            currency: pgParams.currency,
            goodname: goodname || '',
            buyername: user.username || '',
            buyertel: user.telno || '',
            buyeremail: user.email || '',
            returnUrl: url.return,
            closeUrl: url.close,
            acceptmethod: pgParams.acceptmethod
        };
        console.log("module pgstart : paymentData", paymentData)
        console.log("module pgstart : view start", view.start)
        res.render(view.start , {
            paymentData:paymentData
        });
        // 적절한 응답을 반환합니다.
    } catch (err) {
        console.error('pgstart',err)
        res.status(500).json({ error: '서버 오류' }); // 에러 발생 시 500 상태 코드와 오류 메시지 응답
    }
};


exports.pgreturnpost =  async (req , res) => {
    console.log('pgreturnpost........................................');
    
    if(req.body.resultCode === "0000"){

        //############################################
		//1.전문 필드 값 설정(***가맹점 개발수정***)
		//############################################
        // console.log(' pgreturnpost return 값', req.body)
        const mid = req.body.mid;                       // 상점아이디
        const authToken = req.body.authToken;           // 승인요청 검증 토큰
        const netCancelUrl = req.body.netCancelUrl;     // 망취소요청 Url 
        const merchantData = req.body.merchantData;
        const timestamp = new Date().getTime();         // 타임스템프 [TimeInMillis(Long형)]
        const charset = "UTF-8";                        // 리턴형식[UTF-8,EUC-KR](가맹점 수정후 고정)
        const format = "JSON";                          // 리턴형식[XML,JSON,NVP](가맹점 수정후 고정)

        //##########################################################################
        // 승인요청 API url (authUrl) 리스트 는 properties 에 세팅하여 사용합니다.
        // idc_name 으로 수신 받은 센터 네임을 properties 에서 include 하여 승인 요청하시면 됩니다.
        //##########################################################################   

        const idc_name = req.body.idc_name;             
        const authUrl = req.body.authUrl;               // 승인요청 Url
        const authUrl2 = getUrl.getAuthUrl(idc_name);
        console.log(' pgreturnpost authUrl 값', authUrl)
        console.log(' pgreturnpost authUrl2 값', authUrl2)

        // SHA256 Hash값 [대상: authToken, timestamp]
        const signature  = crypto.createHash("sha256").update("authToken="+authToken+"&timestamp="+timestamp).digest('hex');

        // SHA256 Hash값 [대상: authToken, signKey, timestamp]
        const verification  = crypto.createHash("sha256").update("authToken="+authToken+"&signKey="+pgParams.signKey+"&timestamp="+timestamp).digest('hex');
        
        //결제 승인 요청 
        let options = { 
                mid : mid,
                authToken : authToken, 
                timestamp : timestamp,
                signature : signature,
                verification : verification,
                charset : charset,
                format : format
        };
        console.log('inisys approve called.......................................');

        if(authUrl == authUrl2) {
            request.post({method: 'POST', uri: authUrl2, form: options, json: true}, (err,httpResponse,body) =>{ 
                try{
                    let jsoncode = (err) ? err : JSON.stringify(body);
                    // console.log("pereturnpost 모듈에서 body:", body)
                    const approveData ={
                        resultCode : body.resultCode,
                        resultMsg :body.resultMsg,
                        tid : body.tid,
                        MOID : body.MOID,
                        TotPrice: body.TotPrice,
                        goodName : body.goodName,
                        applDate : body.applDate,
                        applTime : body.applTime,
                        payMethod : body.payMethod,
                        applDate : body.applDate,
                        applTime : body.applTime,
                        buyerTel : body.buyerTel
                    }
                    updateParams = {
                        buyerTel :approveData.buyerTel,
                        seatNo :'',
                        TotPrice :approveData.TotPrice,
                        tid: approveData.tid,
                        payMethod:approveData.payMethod
                    }
                    // 사용자 정보를 전화번호로 조회
                    try {
                        console.log("pereturnpost 모듈에서 updateParams:", updateParams)
                        const results = updateBid(updateParams);
                        res.render(view.return, {approveData})
                    } catch (e)  {
                        console.error('pereturnpost 모듈에서 데이터베이스 오류 발생, err정보:', err);
                        return res.status(500).json({ message: 'pereturnpost 모듈에서 데이터베이스 오류 발생' });
                    }
                } catch (e){
                    /*
                        가맹점에서 승인결과 전문 처리 중 예외발생 시 망취소 요청할 수 있습니다.
                        승인요청 전문과 동일한 스펙으로 진행되며, 인증결과 수신 시 전달받은 "netCancelUrl" 로 망취소요청합니다.

                        ** 망취소를 일반 결제취소 용도로 사용하지 마십시오.
                        일반 결제취소는 INIAPI 취소/환불 서비스를 통해 진행해주시기 바랍니다.
                    */
                    console.error(e);
                    const netCancelUrl2 = getUrl.getNetCancel(idc_name)
                    console.log(' pgreturnpost netCancelUrl 값', netCancelUrl)
                    console.log(' pgreturnpost netCancelUrl2 값', netCancelUrl2)
                    if(netCancelUrl == netCancelUrl2) {
                        request.post({method: 'POST', uri: netCancelUrl2, form: options, json: true}, (err,httpResponse,body) =>{
                            let result = (err) ? err : JSON.stringify(body);
                            console.log("<p>"+result+"</p>");
                        });
                    }
                }
                });
        }
    } else {
        const approveData ={
            resultCode : body.resultCode,
            resultMsg :body.resultMsg,
            tid : body.tid,
            MOID : body.MOID,
            TotPrice: body.TotPrice,
            goodName : body.goodName,
            applDate : body.applDate,
            applTime : body.applTime
        }
        console.log(' pgreturnpost approveData 값', approveData)
        res.render(view.return, {
            approveData
        });
        // res.status(200).json(approveData)

    }
};

exports.pgclose = (req, res) => {
    console.log('pgclose called');
    res.render(view.close, {
    });
};


//client에서 inicis결제창을 띄우는 경우 mid등 데이터만 넘겨줌.  client에서 결제창을 띄우면 결제창 요청 도메인과 리턴 도메인이 달라 오류 발생함
exports.pgstartold = (req,res) =>{
    const { price } = req.query;
    // INIAPI key : ItEQKi3rY7uvDS8l
    // INIAPI iv   : HYb3yQ4f65QL89==
    // 모바일 hashkey : 3CB8183A4BE283555ACC8363C0360223
  
    const timestamp = new Date().getTime();                                 // 타임스템프 [TimeInMillis(Long형)]
    const mKey = crypto.createHash("sha256").update(signKey).digest('hex'); // SHA256 Hash값 [대상: mid와 매칭되는 signkey]
    const signature  = crypto.createHash("sha256").update("oid="+pgParams.oid+"&price="+price+"&timestamp="+timestamp).digest('hex'); //SHA256 Hash값 [대상: oid, price, timestamp]
    const verification = crypto.createHash("sha256").update("oid="+pgParams.oid+"&price="+price+"&signKey="+signKey+"&timestamp="+timestamp).digest('hex'); //SHA256 Hash값 [대상: oid, price, signkey, timestamp]
    const sendData = {
        mid : pgParams.mid,
        oid : pgParams.oid,
        timestamp : timestamp,
        mKey : mKey,
        use_chkfake : pgParams.use_chkfake,
        signature : signature,
        verification : verification

    }
    res.status(200).json(sendData)
  }

