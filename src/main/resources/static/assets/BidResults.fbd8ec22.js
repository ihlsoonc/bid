import{a as b,c as T,b as o,w as l,af as N,ae as p,d as B,f as c,ah as s,Q as k,e as E,F as it,aZ as rt,aj as ut,i as dt,r,o as ct}from"./index.c44c2320.js";import{Q as mt}from"./QPage.80b32476.js";import{f as bt,b as _t,a as h,A as g}from"./sessionFunctions.3c5066a4.js";import{B as ft}from"./BidStatus.bec4e2ae.js";import{Q as P,a as yt,S as wt}from"./SeatMap.3e05a527.js";import{Q as vt,m as Y}from"./messageCommon.5db82364.js";import{Q as St,a as v}from"./QTable.50d33692.js";import{Q as ht}from"./format.4f6c3584.js";import{Q as gt}from"./QItem.35fdbb09.js";import{n as U}from"./navigate.3bf18681.js";import{s as j}from"./dialogUtils.7f45a23c.js";import{_ as Bt}from"./plugin-vue_export-helper.21dcd24c.js";import"./QSelect.3898a8f6.js";import"./selection.54367025.js";import"./rtl.276c3f1b.js";import"./formatTimeToLocal.04abce99.js";import"./QList.72d1a296.js";import"./urls.d05eee06.js";const Ct={class:"bids-list"},At={__name:"BidsListAdmin",props:{seats:Array,tableColumns:Array,selectedHistoryButton:Number,totalWinAmount:Number,totalWinCount:Number,totalBidAmount:Number},emits:["toggleHistory"],setup(f,{emit:D}){const H=f,n=D,C=d=>new Date(d).toLocaleString(),S=d=>{n("toggleHistory",d)},u=d=>H.selectedHistoryButton===d.seat_no;return(d,y)=>(b(),T("div",Ct,[o(ut,{class:"q-pa-md q-mb-md"},{default:l(()=>[o(N,null,{default:l(()=>y[0]||(y[0]=[p("h6",null,"\uC785\uCC30 \uBC0F \uB099\uCC30 \uD604\uD669",-1)])),_:1}),f.seats.length===0?(b(),B(N,{key:0},{default:l(()=>[o(vt,{color:"warning"},{default:l(()=>y[1]||(y[1]=[c("\uC785\uCC30 \uB0B4\uC5ED\uC774 \uC5C6\uC2B5\uB2C8\uB2E4.")])),_:1})]),_:1})):(b(),B(N,{key:1},{default:l(()=>[o(St,{rows:f.seats,columns:f.tableColumns,"row-key":"seat_no",flat:""},{body:l(a=>[o(P,{props:a},{default:l(()=>[o(v,{class:"important-text"},{default:l(()=>[c(s(a.row.bidWonStatus),1)]),_:2},1024),o(v,null,{default:l(()=>[c(s(a.row.seat_no)+" ("+s(a.row.row_no)+" \uC5F4 "+s(a.row.col_no)+"\uBC88) "+s(a.row.paidStatus),1)]),_:2},1024),o(v,null,{default:l(()=>[c(s(a.row.username),1)]),_:2},1024),o(v,null,{default:l(()=>[c(s(a.row.bid_amount.toLocaleString())+"\uC6D0 : "+s(C(a.row.bid_at)),1)]),_:2},1024),o(v,null,{default:l(()=>[c(s(a.row.total_bidders)+"\uBA85\xA0\xA0\xA0 "+s(a.row.total_bids)+"\uAC74",1)]),_:2},1024),o(v,null,{default:l(()=>[a.row.historyButtonEnabled?(b(),B(k,{key:0,onClick:_=>S(a.row),color:u(a.row)?"primary":"secondary",size:"xs",icon:u(a.row)?"keyboard_arrow_up":"keyboard_arrow_down"},{default:l(()=>[o(yt,{color:u(a.row)?"primary":"secondary"},{default:l(()=>[c(" \uC785\uCC30\uC774\uB825 "+s(a.row.bidHistory.length)+"\uAC74 ",1)]),_:2},1032,["color"])]),_:2},1032,["onClick","color","icon"])):E("",!0)]),_:2},1024)]),_:2},1032,["props"]),a.row.historyShow?(b(),B(P,{key:0},{default:l(()=>[o(v,{colspan:"4"},{default:l(()=>[(b(!0),T(it,null,rt(a.row.bidHistory,(_,A)=>(b(),B(gt,{key:A},{default:l(()=>[o(ht,null,{default:l(()=>[c(s(_.username)+"\xA0\xA0 "+s(_.bid_amount.toLocaleString())+"\uC6D0 : "+s(C(_.bid_at)),1)]),_:2},1024)]),_:2},1024))),128))]),_:2},1024)]),_:2},1024)):E("",!0)]),_:1},8,["rows","columns"]),p("div",null," \uCD1D "+s(f.totalWinCount)+"\uAC74 \xA0\xA0\xA0\xA0\xA0 \uC785\uCC30\uAE08\uC561 \uD569\uACC4: "+s(f.totalBidAmount.toLocaleString())+"\uC6D0 ",1)]),_:1}))]),_:1})]))}};const kt={key:0,class:"q-message q-pa-md bg-warning text-white"},pt={key:1,class:"colomnflex-container"},Dt={class:"spaced-span"},Ht={class:"columnflex-container q-gutter-md"},G="Y",Lt="O",Nt="C",K="F",Tt="Y",Et="Y",It={__name:"BidResults",setup(f){const D=dt();localSessionData=bt(["matchNumber","userClass"]);let H={},n=0,C=0;const S=r(-1),u=r({}),d=r([]),y=r(!1),a=r(!1),_=r(!1),A=r(!1),I=r([]),x=r([]),Q=r(0),W=r(0),M=r(0),V=r(0),L=r(""),z=r([{name:"bidWonStatus",required:!0,label:"\uB099\uCC30 \uC0C1\uD0DC",align:"left",field:t=>t.bidWonStatus},{name:"seat_info",required:!0,label:"\uC88C\uC11D \uC815\uBCF4",align:"left",field:t=>`${t.seat_no} (${t.row_no}\uC5F4 ${t.col_no}\uBC88) ${t.paidStatus}`},{name:"user_info",label:"\uCD5C\uACE0 \uC751\uCC30\uC790",align:"left",field:t=>`${t.username} :`},{name:"bid_amount",label:"\uC785\uCC30 \uAE08\uC561 \uBC0F \uC2DC\uAC04",align:"left",field:t=>`${t.bid_amount.toLocaleString()}\uC6D0 : ${formatTimeToLocal(t.bid_at)}`},{name:"total_bidders",label:"\uC785\uCC30\uC790 \uC218",align:"left",field:t=>`${t.total_bidders} :`},{name:"total_bids",label:"\uC804\uCCB4 \uB0B4\uC6A9 \uBCF4\uAE30",align:"left",field:t=>`${t.total_bids} :`}]),Z=()=>{U(D,localSessionData.userClass,"selectVenue")},J=async()=>{await R(n),await F(),await $(n),await q(n),O()},X=async()=>{if(!a.value){alert("\uB099\uCC30\uC744 \uC9C4\uD589\uD560 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4. \uC785\uCC30 \uC885\uB8CC \uD6C4 \uAC00\uB2A5\uD569\uB2C8\uB2E4.");return}j({title:"\uB099\uCC30 \uC9C4\uD589 \uD655\uC778",message:"\uB099\uCC30\uC744 \uC9C4\uD589\uD558\uACA0\uC2B5\uB2C8\uAE4C?",okLabel:"\uC608",cancelLabel:"\uC544\uB2C8\uC624",onOk:()=>{tt()},onCancel:()=>{alert("\uB099\uCC30\uC9C4\uD589\uC774 \uCDE8\uC18C\uB418\uC5C8\uC2B5\uB2C8\uB2E4.")}})},tt=async()=>{try{const t=await h.post(g.AWARD_BID,{matchNumber:n});t.status===200&&(alert("\uC131\uACF5\uC801\uC73C\uB85C \uB099\uCC30 \uCC98\uB9AC\uAC00 \uB418\uC5C8\uC2B5\uB2C8\uB2E4."),L.value=t.data.message,await R(n),await $(n),await q(n),O())}catch(t){w(t)}},et=async()=>{j({title:"\uC54C\uB9BC\uD1A1 \uC804\uC1A1 \uD655\uC778",message:"\uC54C\uB9BC\uD1A1\uC744 \uBCF4\uB0B4\uC2DC\uACA0\uC2B5\uB2C8\uAE4C?",okLabel:"\uC608",cancelLabel:"\uC544\uB2C8\uC624",onOk:()=>{at()},onCancel:()=>{alert("\uC54C\uB9BC\uD1A1 \uC804\uC1A1\uC774 \uCDE8\uC18C\uB418\uC5C8\uC2B5\uB2C8\uB2E4.")}})},at=async()=>{try{await h.post(g.SEND_KAKAO_ALIMTALK,{matchNumber:n},{withCredentials:!0}),alert("\uC54C\uB9BC\uD1A1\uC774 \uC804\uC1A1\uB418\uC5C8\uC2B5\uB2C8\uB2E4."),_.value=!1}catch(t){w(t)}},R=async t=>{try{const e=await h.get(g.GET_BIDSTATUS,{params:{matchNumber:t}},{withCredentials:!0});e.status===200&&(u.value=e.data)}catch(e){w(e)}},$=async t=>{try{const e=await h.get(g.GET_HIGHEST_BIDS,{params:{matchNumber:t},withCredentials:!0});d.value=e.data.map(i=>(u.value.bid_open_status===K&&i.bid_won===G&&(M.value+=i.bid_amount||0,V.value++),{...i,bidWonStatus:u.value.bid_open_status===K?i.bid_won===G?"\uB099\uCC30":"\uC720\uCC30":" ",paidStatus:u.value.bid_open_status===G?i.bid_paid===Tt?"\uACB0\uC81C\uC644\uB8CC":"\uBBF8\uACB0\uC81C":" ",historyButtonEnabled:!1,historyShow:!1})),W.value=d.value.reduce((i,m)=>i+(m.bid_amount||0),0)}catch(e){w(e)}},q=async t=>{try{(await h.get(g.GET_ALL_BIDS,{params:{matchNumber:t},withCredentials:!0})).data.forEach(i=>{const m=d.value.find(nt=>String(nt.seat_no)===String(i.seat_no)),st=i.bidHistory||[{bid_amount:i.bid_amount,bid_at:i.bid_at,username:i.username}];m?(m.bidHistory=m.bidHistory||[],m.bidHistory=[...m.bidHistory,...st],m.historyButtonEnabled=m.bidHistory.length>1,m.historyShow=!1):console.log("\uC624\uB958: \uC88C\uC11D\uBC88\uD638\uAC00 match\uB418\uC9C0 \uC54A\uB294 \uC774\uB825\uC774 \uC788\uC2B5\uB2C8\uB2E4.:",i)})}catch(e){console.error("\uC5D0\uB7EC \uBC1C\uC0DD:",e),w(e)}},F=async()=>{try{const t=await h.get(g.GET_BID_TALLIES,{params:{telno:H.telno,matchNumber:n}});x.value=t.data.map(e=>{const i=Number(e.seat_no);return e.total_bidders>0&&(C+=1),{...e,uniqueSeatId:i}}),I.value=x.value.filter(e=>e.total_bidders>0),Q.value=I.value.length}catch(t){w(t)}},ot=t=>{S.value===t.seat_no?(t.historyShow=!1,S.value=-1):(S.value=t.seat_no,t.historyShow=!0)},lt=t=>{},O=()=>{a.value=!1,_.value=!1,A.value=!1,u.value.bidStatusCode==Nt&&C>0&&(a.value=!0),u.value.bidStatusCode==K&&u.value.alimtalk_sent!==Et&&(_.value=!0),u.value.bidStatusCode==Lt&&(A.value=!0)},w=t=>{L.value=t.response?t.response.data:t.request?Y.ERR_NETWORK:Y.ERR_ETC};return ct(async()=>{if(n=localSessionData.matchNumber,n)try{H=await _t(localSessionData.userClass),await R(n),await F(),O(),await $(n),await q(n)}catch(t){w(t)}else alert("\uACBD\uAE30\uB97C \uBA3C\uC800 \uC120\uD0DD\uD574\uC8FC\uC138\uC694."),U(D,localSessionData.userClass,"selectMatch")}),(t,e)=>(b(),B(mt,{class:"common-container"},{default:l(()=>[o(ft,{bidStatus:u.value},null,8,["bidStatus"]),L.value?(b(),T("div",kt,s(L.value),1)):E("",!0),o(wt,{selectedSeats:I.value,onSeatClick:lt,disabled:y.value,bidsArray:x.value},null,8,["selectedSeats","disabled","bidsArray"]),e[3]||(e[3]=p("br",null,null,-1)),Q.value>0?(b(),T("div",pt,[p("div",Dt," \uC785\uCC30 \uC88C\uC11D\uC218\xA0: "+s(Q.value)+"\uAC1C \xA0 \uC785\uCC30\uAE08\uC561 \uD569\uACC4: "+s(W.value)+"\uC6D0 ",1)])):E("",!0),p("div",Ht,[o(N,null,{default:l(()=>[o(k,{push:"",color:"white","text-color":"blue-grey-14",label:"\uB099\uCC30 \uC9C4\uD589",onClick:X,disable:!a.value},null,8,["disable"]),e[0]||(e[0]=c("\xA0\xA0\xA0\xA0\xA0 ")),o(k,{push:"",color:"white","text-color":"blue-grey-14",label:"\uB099\uCC30 \uACB0\uACFC \uC54C\uB9BC\uD1A1 \uBCF4\uB0B4\uAE30",onClick:et,disable:!_.value},null,8,["disable"]),e[1]||(e[1]=c("\xA0\xA0\xA0\xA0\xA0 ")),o(k,{push:"",color:"white","text-color":"blue-grey-14",label:"\uACBD\uAE30 \uB2E4\uC2DC \uC120\uD0DD\uD558\uAE30",onClick:Z}),e[2]||(e[2]=c("\xA0\xA0\xA0\xA0\xA0 ")),o(k,{push:"",color:"white","text-color":"deep-orange-14",label:"\uB370\uC774\uD130 \uB2E4\uC2DC \uBD88\uB7EC\uC624\uAE30",onClick:J,disable:!A.value},null,8,["disable"])]),_:1}),o(At,{seats:d.value,tableColumns:z.value,selectedHistoryButton:S.value,totalWinAmount:M.value,totalWinCount:V.value,totalBidAmount:W.value,onToggleHistory:ot},null,8,["seats","tableColumns","selectedHistoryButton","totalWinAmount","totalWinCount","totalBidAmount"])])]),_:1}))}};var Jt=Bt(It,[["__scopeId","data-v-6d84bc80"]]);export{Jt as default};
