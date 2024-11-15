package com.bidsystem.bid.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface PaymentMapper {

    @Insert("INSERT INTO payments (P_INI_PAYMENT, P_AMT, P_GOODS, P_UNAME, P_MOBILE, P_EMAIL, " +
            "P_NOTI_URL, P_HPP_METHOD, P_MID, P_OID) " +
            "VALUES (#{P_INI_PAYMENT}, #{P_AMT}, #{P_GOODS}, #{P_UNAME}, #{P_MOBILE}, #{P_EMAIL}, " +
            "#{P_NOTI_URL}, #{P_HPP_METHOD}, #{P_MID}, CONCAT(#{P_OID}, 'R'))")
    void saveRequest(Map<String, Object> paymentData);

    @Insert("INSERT INTO payments (CARD_CorpFlag, P_NOTEURL, P_CARD_ISSUER_CODE, EventCode, P_UNAME, " +
            "P_CARD_NUM, P_MERCHANT_RESERVED, P_TID, P_CARD_APPLPRICE, P_CARD_INTEREST, P_MNAME, " +
            "P_RMESG2, P_RMESG1, P_NOTI, P_CARD_MEMBER_NUM, P_OID, P_FN_NM, P_AMT, P_MID, " +
            "P_CARD_PURCHASE_NAME, P_TYPE, P_CARD_PRTC_CODE, P_AUTH_DT, " +
            "P_CARD_ISSUER_NAME, P_STATUS, P_CARD_CHECKFLAG, P_FN_CD1, P_CARD_PURCHASE_CODE, P_AUTH_NO) " +
            "VALUES (#{CARD_CorpFlag}, #{P_NOTEURL}, #{P_CARD_ISSUER_CODE}, #{EventCode}, #{P_UNAME}, " +
            "#{P_CARD_NUM}, #{P_MERCHANT_RESERVED}, #{P_TID}, #{P_CARD_APPLPRICE}, #{P_CARD_INTEREST}, #{P_MNAME}, " +
            "#{P_RMESG2}, #{P_RMESG1}, #{P_NOTI}, #{P_CARD_MEMBER_NUM}, CONCAT(#{P_OID}, 'A'), #{P_FN_NM}, #{P_AMT}, #{P_MID}, " +
            "#{P_CARD_PURCHASE_NAME}, #{P_TYPE}, #{P_CARD_PRTC_CODE}, #{P_AUTH_DT}, " +
            "#{P_CARD_ISSUER_NAME}, #{P_STATUS}, #{P_CARD_CHECKFLAG}, #{P_FN_CD1}, #{P_CARD_PURCHASE_CODE}, #{P_AUTH_NO})")
        void saveApprovel(Map<String, Object> paymentData);

        @Insert("INSERT INTO payments (P_UNAME, P_AMT, P_GOODS, P_MOBILE, P_EMAIL, P_MID, P_TIMESTAMP, " +
                        "P_USE_CHKFAKE, P_OID) " +
                        "VALUES (#{buyerName}, #{price}, #{goodName}, #{buyerTel}, #{buyerEmail}, #{mid}, #{timestamp}, " +
        "#{use_chkfake}, CONCAT(#{oid}, 'R'))")
        void savePcRequest(Map<String, Object> paymentData);


        @Insert("INSERT INTO payments (CARD_Quota, CARD_ClEvent, CARD_CorpFlag, P_MOBILE, parentEmail, " +
        "P_AUTH_DT, P_EMAIL, OrgPrice, p_Sub, P_STATUS, P_MID, CARD_UsePoint, P_CARD_NUM, authSignature, " +
        "P_TID, EventCode, P_GOODS, P_AMT, payMethod, CARD_MemberNum, P_OID, CARD_Point, currency, " +
        "CARD_PurchaseCode, CARD_PrtcCode, goodsName, CARD_CheckFlag, FlgNotiSendChk, CARD_Code, " +
        "CARD_BankCode, CARD_TerminalNum, P_FN_NM, P_UNAME, p_SubCnt, applNum, P_RMESG1, CARD_Interest, " +
        "CARD_SrcCode, CARD_ApplPrice, CARD_GWCode, custEmail, CARD_Expire, CARD_PurchaseName, " +
        "CARD_PRTC_CODE, payDevice) " +
        "VALUES (#{CARD_Quota}, #{CARD_ClEvent}, #{CARD_CorpFlag}, #{buyerTel}, #{parentEmail}, " +
        "CONCAT(#{applDate}, #{applTime}), #{buyerEmail}, #{OrgPrice}, #{p_Sub}, #{resultCode}, #{mid}, #{CARD_UsePoint}, " +
        "#{CARD_Num}, #{authSignature}, #{tid}, #{EventCode}, #{goodName}, #{TotPrice}, #{payMethod}, " +
        "#{CARD_MemberNum}, CONCAT(#{MOID}, 'A'), #{CARD_Point}, #{currency}, #{CARD_PurchaseCode}, #{CARD_PrtcCode}, " +
        "#{goodsName}, #{CARD_CheckFlag}, #{FlgNotiSendChk}, #{CARD_Code}, #{CARD_BankCode}, " +
        "#{CARD_TerminalNum}, #{P_FN_NM}, #{buyerName}, #{p_SubCnt}, #{applNum}, #{resultMsg}, #{CARD_Interest}, " +
        "#{CARD_SrcCode}, #{CARD_ApplPrice}, #{CARD_GWCode}, #{custEmail}, #{CARD_Expire}, #{CARD_PurchaseName}, " +
        "#{CARD_PRTC_CODE}, #{payDevice})")
        void savePCApprovel(Map<String, Object> paymentData);
    
    }
