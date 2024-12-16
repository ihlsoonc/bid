import{Q as A,a as B}from"./QLayout.5a1e97d2.js";import{t as L,r as S,a as r,c as V,b as l,w as t,d as u,Q as f,e as b,f as d,u as a,h as q,F as N,i as T,j as H,o as U,k as P}from"./index.c44c2320.js";import{Q as R,a as D,b as F,c as O,u as E}from"./QDrawer.e6db0112.js";import{s as j,a as G,A as x}from"./sessionFunctions.3c5066a4.js";import{Q as C}from"./format.4f6c3584.js";import{Q as p}from"./QItem.35fdbb09.js";import{Q as z}from"./QList.72d1a296.js";import{_ as M}from"./plugin-vue_export-helper.21dcd24c.js";import{n as I}from"./navigate.3bf18681.js";import"./QResizeObserver.352c0735.js";import"./touch.9135741d.js";import"./selection.54367025.js";import"./urls.d05eee06.js";const J={__name:"NavBarAdmin",props:{isLoggedIn:{type:Boolean,default:!1},hasSelectedMatch:{type:Boolean,default:!1}},emits:["link-action"],setup(w,{emit:y}){const g=w,$=y,{isLoggedIn:o}=L(g),{hasSelectedMatch:k}=L(g),v=S(!1),n=i=>{v.value=!1,$("link-action",i)},m=()=>{v.value=!v.value};return(i,e)=>(r(),V(N,null,[l(F,{elevated:""},{default:t(()=>[l(R,null,{default:t(()=>[i.$q.screen.lt.md?(r(),u(f,{key:0,flat:"",round:"",dense:"",icon:"menu",onClick:m})):b("",!0),l(D,null,{default:t(()=>e[16]||(e[16]=[d("\uC785\uCC30 \uC2DC\uC2A4\uD15C \uAD00\uB9AC\uC790\uC6A9 \uC2DC\uC2A4\uD15C")])),_:1}),i.$q.screen.gt.md?(r(),u(f,{key:1,flat:"",round:"",dense:"",label:"\uACBD\uAE30\uC7A5\uC120\uD0DD",onClick:e[0]||(e[0]=s=>n("selectVenue")),disable:!a(o)},null,8,["disable"])):b("",!0),e[17]||(e[17]=d("\xA0\xA0\xA0\xA0\xA0\xA0 ")),i.$q.screen.gt.md?(r(),u(f,{key:2,flat:"",round:"",dense:"",label:"\uC785\uCC30\uD604\uD669 \uBC0F \uB099\uCC30\uC9C4\uD589",onClick:e[1]||(e[1]=s=>n("bids")),disable:!a(o)||!a(k)},null,8,["disable"])):b("",!0),e[18]||(e[18]=d("\xA0\xA0\xA0\xA0\xA0\xA0 ")),i.$q.screen.gt.md?(r(),u(f,{key:3,flat:"",round:"",dense:"",label:"\uB300\uD68C\uAD00\uB9AC",onClick:e[2]||(e[2]=s=>n("manageMatch")),disable:!a(o)},null,8,["disable"])):b("",!0),e[19]||(e[19]=d("\xA0\xA0\xA0\xA0\xA0\xA0 ")),i.$q.screen.gt.md?(r(),u(f,{key:4,flat:"",round:"",dense:"",label:"\uB300\uD68C\uC2B9\uC778",onClick:e[3]||(e[3]=s=>n("approveMatch")),disable:!a(o)},null,8,["disable"])):b("",!0),e[20]||(e[20]=d("\xA0\xA0\xA0\xA0\xA0\xA0 ")),i.$q.screen.gt.md?(r(),u(f,{key:5,flat:"",round:"",dense:"",label:"\uC88C\uC11D\uAC00\uACA9\uC785\uB825",onClick:e[4]||(e[4]=s=>n("updateSeatPrice")),disable:!a(o)||!a(k)},null,8,["disable"])):b("",!0),e[21]||(e[21]=d("\xA0\xA0\xA0\xA0\xA0\xA0 ")),i.$q.screen.gt.md?(r(),u(f,{key:6,flat:"",round:"",dense:"",label:"\uACBD\uAE30\uC7A5 \uB4F1\uB85D \uBC0F \uC218\uC815",onClick:e[5]||(e[5]=s=>n("manageVenue")),disable:!a(o)},null,8,["disable"])):b("",!0),e[22]||(e[22]=d("\xA0\xA0\xA0\xA0\xA0\xA0 ")),i.$q.screen.gt.md?(r(),u(f,{key:7,flat:"",round:"",dense:"",icon:"home",onClick:e[6]||(e[6]=s=>n("login"))})):b("",!0),e[23]||(e[23]=d("\xA0\xA0\xA0\xA0\xA0\xA0 ")),i.$q.screen.gt.md?(r(),u(f,{key:8,flat:"",round:"",dense:"",label:"\uB85C\uADF8\uC544\uC6C3",icon:"logout",onClick:e[7]||(e[7]=s=>n("logout")),disable:!a(o)},null,8,["disable"])):b("",!0)]),_:1})]),_:1}),l(O,{modelValue:v.value,"onUpdate:modelValue":e[15]||(e[15]=s=>v.value=s),side:"left",bordered:""},{default:t(()=>[l(z,null,{default:t(()=>[l(p,{clickable:"",onClick:e[8]||(e[8]=s=>n("selectVenue")),disabled:!a(o)},{default:t(()=>[l(C,null,{default:t(()=>e[24]||(e[24]=[d("\uACBD\uAE30\uC7A5 \uC120\uD0DD")])),_:1})]),_:1},8,["disabled"]),l(p,{clickable:"",onClick:e[9]||(e[9]=s=>n("bids")),disable:!a(o)||!a(k)},{default:t(()=>[l(C,null,{default:t(()=>e[25]||(e[25]=[d("\uC785\uCC30\uD604\uD669 \uBC0F \uB099\uCC30\uC9C4\uD589")])),_:1})]),_:1},8,["disable"]),l(p,{clickable:"",onClick:e[10]||(e[10]=s=>n("manageMatch")),disable:!a(o)},{default:t(()=>[l(C,null,{default:t(()=>e[26]||(e[26]=[d("\uB300\uD68C\uAD00\uB9AC")])),_:1})]),_:1},8,["disable"]),l(p,{clickable:"",onClick:e[11]||(e[11]=s=>n("approveMatch")),disable:!a(o)},{default:t(()=>[l(C,null,{default:t(()=>e[27]||(e[27]=[d("\uB300\uD68C\uC2B9\uC778")])),_:1})]),_:1},8,["disable"]),l(p,{clickable:"",onClick:e[12]||(e[12]=s=>n("updateSeatPrice")),disable:!a(o)||!a(k)},{default:t(()=>[l(C,null,{default:t(()=>e[28]||(e[28]=[d("\uC88C\uC11D\uAC00\uACA9\uC785\uB825")])),_:1})]),_:1},8,["disable"]),l(p,{clickable:"",onClick:e[13]||(e[13]=s=>n("manageVenue")),disable:!a(o)},{default:t(()=>[l(C,null,{default:t(()=>e[29]||(e[29]=[d("\uACBD\uAE30\uC7A5 \uB4F1\uB85D \uBC0F \uC218\uC815")])),_:1})]),_:1},8,["disable"]),l(p,{clickable:"",onClick:e[14]||(e[14]=s=>n("logout")),disable:!a(o),icon:"logout"},{default:t(()=>[l(q,{name:"logout"}),e[30]||(e[30]=d("\uB85C\uADF8\uC544\uC6C3 "))]),_:1},8,["disable"])]),_:1})]),_:1},8,["modelValue"])],64))}};var K=M(J,[["__scopeId","data-v-76a576a2"]]);const Q="admin",W="admin",X={__name:"AdminHome",setup(w){const y=T();E();const g=S(!1),$=S(!1),o=m=>{g.value=m.isLoggedIn,$.value=m.hasSelectedMatch},k=()=>{g.value=!1,$.value=!1},v=async()=>{if(!!window.confirm("\uB85C\uADF8\uC544\uC6C3\uD558\uC2DC\uACA0\uC2B5\uB2C8\uAE4C"))try{await G.post(x.USER_LOGOUT,{},{withCredentials:!0}),k()}catch{alert("\uC2DC\uC2A4\uD15C \uC624\uB958\uC785\uB2C8\uB2E4.")}finally{I(y,Q,"login")}},n=async m=>{m==="logout"?await v():I(y,Q,m)};return H(()=>{j(Q,{tableName:W,userContext:Q})}),U(()=>{n("login")}),(m,i)=>{const e=P("router-view");return r(),u(A,{view:"hHh lpR fFf"},{default:t(()=>[l(K,{isLoggedIn:g.value,hasSelectedMatch:$.value,onLinkAction:n},null,8,["isLoggedIn","hasSelectedMatch"]),l(B,null,{default:t(()=>[l(e,{onUpdateStatus:o})]),_:1})]),_:1})}}};var ie=M(X,[["__scopeId","data-v-f9dce8ce"]]);export{ie as default};
