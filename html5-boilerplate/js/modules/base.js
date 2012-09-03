/* prevel compressed library */
(function(win,doc,proto,ael,ge,cn,nn,u,newRegExp,n,ef,uf){(function(){var types={"function":"fn",object:"obj",number:"int",string:"str","boolean":"bool",regexp:"regexp",date:"date","undefined":u,array:"arr"},op=Object[proto],accessors=!!op.__lookupGetter__&&!!op.__lookupSetter__&&!!op.__defineGetter__,trim=!!"".trim,indexOf=!![].indexOf,toString=op.toString,json=win.JSON&&win.JSON.parse,pl=function(){return function(a,b,c){return pl.fn?new pl.fn.init(a,b,c):uf}}();pl.extend=function(a,b,c){b||(b=a,a=pl);var d=a;if(accessors){var e,f;for(var g in b){e=b.__lookupGetter__(g),f=b.__lookupSetter__(g);if(e||f)e&&a.__defineGetter__(g,e),f&&a.__defineSetter__(g,f);else if(!a[g]||a[g]&&c)a[g]=b[g]}}else for(var g in b)if(!a[g]||a[g]&&c)a[g]=b[g];return d===pl.fn&&pl.implement(pl.fn.init,pl.fn),d=u,a};var ua=win.navigator.userAgent.toLowerCase(),opera=/opera/i.test(ua),chrome=/chrome/i.test(ua),browsers={opera:opera,ie:!opera&&/msie/i.test(ua),ie6:!opera&&/msie 6/i.test(ua),ie7:!opera&&/msie 7/i.test(ua),ie8:!opera&&/msie 8/i.test(ua),firefox:/firefox/i.test(ua),chrome:chrome,safari_khtml:!chrome&&/khtml/i.test(ua),safari:!chrome&&/webkit|safari/i.test(ua)};pl.extend({navigator:[]});for(var key in browsers)browsers[key]&&pl.navigator.push(key);pl.extend({implement:function(a,b,c){return pl.extend(a[proto],b,c)},isArray:Array.isArray||function(a){return pl.type(a,"arr")},type:function(a,b){var c=a===n?nn:a===uf?u:class2type[toString.call(a)]||"obj";return b?b===c:c},empty:function(a){if(pl.type(a,"obj")){for(var b in a)return!1;return!0}return pl.type(a,nn)||pl.type(a,u)||!a.length},trim:function(a){return a=a||"",trim?a.trim():a.replace(/^\s\s*/,"").replace(/\s\s*$/,"")},each:function(a,b){var c=-1,d=a.length;while(++c<d)b.call(a[c],c,a[c])},filter:function(a,b){var c=[];return pl.each(a,function(a,d){b(d)&&c.push(d)}),c},inArray:function(a,b,c,d){if(indexOf)return b.indexOf(a,c);for(c=c>0||-1,d=-1;++c<b.length&&!~d;d=b[c]===a?c:d);return d},error:function(a){throw new Error(a)},JSON:function(data){return json?win.JSON.parse(data):!/[^,:{}[]0-9.-+Eaeflnr-u nrt]/.test(data.replace(/"(.|[^"])*"/g,""))&&eval("("+data+")")},browser:function(a){return a?!!~pl.inArray(a,pl.navigator):pl.navigator[0]}});var class2type={};pl.each("Array Boolean Number String Function Date RegExp Object".split(" "),function(a,b){class2type["[object "+b+"]"]=types[b.toLowerCase()]}),win.pl=pl})(),function(){pl.extend({create:function(a,b){var c=doc.createElement(a);for(var d in b)c[pl.fixAttr?pl.fixAttr[d]||d:d]=b[d];return c},parent:function(a,b){return b>0?pl.parent(a.parentNode,--b):a},__self__:uf}),pl.extend({fn:{},find:function(a,b){return doc.querySelectorAll(b?b+" "+a:a)}}),pl.extend(pl.fn,{init:function(){return function(a,b,c){var d;switch(pl.type(a)){case"str":var e=a.match(newRegExp);if(e)d=[pl.create(e[1],b)];else switch(pl.type(b)){case"str":switch(pl.type(c)){case"int":d=[pl.find(a,b)[c]];break;default:case u:d=pl.find(a)}break;case"int":d=[pl.find(a)[b]];break;default:case u:d=pl.find(a)}break;case"fn":pl.events.ready(a);break;case"obj":d=a[b||0]?a:[a]}return this.elements=d,this.selector=arguments,pl.__self__=this,this}}(),len:function(){return this.elements.length},last:function(){var a=this.elements.length;return this.elements=[a&&!pl.type(this.elements[a-1],u)?this.elements[a-1]:n],this},get:function(a){var b=this.elements;return b.length===1?b[0]:pl.type(a,u)?b:b[a]},parent:function(a){return this.elements=[pl.parent(this.elements[0],a||1)],this},remove:function(){return pl.each(this.elements,function(){this.parentNode.removeChild(this)}),this},each:function(a){return pl.each(pl.__self__.elements,function(){a.call(this)}),this}})}(),function(){pl.extend({camelCase:function(a){if(!a.match("-"))return a;var b=a.split("-");return b[0]+b[1].charAt(0).toUpperCase()+b[1].substr(1)},curCSS:{rmvPostFix:{zIndex:!0,fontWeight:!0,opacity:!0,zoom:!0,lineHeight:!0},get:function(a,b){return a.currentStyle?a.currentStyle[b]:win.getComputedStyle(a,n).getPropertyValue(b)}}}),pl.extend(pl.fn,{css:function(a,b){if(!pl.type(b,"undef"))a=pl.camelCase(a),pl.type(b,"int")&&!pl.curCSS.rmvPostFix[a]&&(b+="px"),pl.each(this.elements,function(){this.style[a]=b});else{if(pl.type(a,"str"))return pl.curCSS.get(this.elements[0],a);for(var c in a)pl.fn.css.call(this,c,a[c])}return this}})}(),function(){pl.extend({toParams:function(a){if(pl.type(a,"str"))return a;var b=[];for(var c in a)b.push(encodeURIComponent(c)+"="+encodeURIComponent(a[c]));return b.join("&")},ajax:function(a){var b,c=function(){if(win.XMLHttpRequest)b=new XMLHttpRequest,b.overrideMimeType&&b.overrideMimeType("text/html");else if(win.ActiveXObject)try{b=new ActiveXObject("Msxml2.XMLHTTP")}catch(c){try{b=new ActiveXObject("Microsoft.XMLHTTP")}catch(d){}}b||pl.error("Could not create a XMLHttpRequest instance."),b.onreadystatechange=function(c){if(b.readyState===1)(a.load||ef)();else if(b.readyState===4){var d=b.responseText;a.dataType==="json"&&(d=pl.JSON(d)),b.status>199&&b.status<300||b.status===304?(a.success||ef)(d,b.status):(a.error||ef)(b.status,d)}a.always=a.always||ef;try{a.always(b.readyState,b.status,d)}catch(c){a.always(b.readyState)}}},d=function(c){b.setRequestHeader("X-Requested-With","XMLHttpRequest"),c&&b.setRequestHeader("Content-type","application/x-www-form-urlencoded; charset="+(a.charset||"utf-8"))};a.data=pl.toParams(a.data||{}),a.async=a.async||!0,c(),a.type==="POST"?(b.open("POST",a.url,a.async),d(1),b.send(a.data)):(b.open("GET",a.url+(pl.empty(a.data)?"":(a.url.match(/\?/)?"&":"?")+a.data),a.async),d(),b.send(n))}})}(),function(){pl.extend({fixAttr:{"class":"className","float":"cssFloat","for":"htmlFor"}}),pl.extend(pl.fn,{addClass:function(a){return pl.each(this.elements,function(){if(~pl.inArray(a,this[cn].split(" ")))return;this[cn]+=(this[cn]?" ":"")+a}),this},hasClass:function(a){return this.elements[0]&&this.elements[0][cn]?!!~pl.inArray(a,this.elements[0][cn].split(" ")):!1},removeClass:function(a){return pl.each(this.elements,function(){if(!this[cn])return;var b=this[cn].split(" "),d=pl.inArray(a,b);if(!~d)return;b.splice(d,1),this[cn]=(pl.empty(b)?b.slice(d,1):b).join(" ")}),this},attr:function(a,b){a=pl.fixAttr[a]||a;if(!pl.type(b,"undef"))pl.each(this.elements,function(){this[a]=b});else{if(pl.type(a,"str"))return this.elements[0][a]||this.elements[0].getAttribute(a);for(var c in a)pl.fn.attr.call(this,c,a[c])}return this},removeAttr:function(a){return a=pl.fixAttr[a]||a,pl.each(this.elements,function(){this[a]=n}),this}})}(),function(){pl.extend({events:{ready:function(){this.readyList=[],this.bindReady=function(a){function c(){if(b)return;b=!0,a()}var b=!1;if(doc[ael])pl.events.attaches.bind(doc,"DOMContentLoaded",c);else if(doc.attachEvent){if(doc.documentElement.doScroll&&win===win.top){function d(){if(b)return;if(!doc.body)return;try{doc.documentElement.doScroll("left"),c()}catch(a){setTimeout(d,0)}}d()}pl.events.attaches.bind(doc,"readystatechange",function(){doc.readyState==="complete"&&c()})}pl.events.attaches.bind(win,"load",c)};var a=this;return function(b){a.readyList.length||a.bindReady(function(){pl.each(a.readyList,function(a){this()})}),a.readyList.push(b)}}(),mend:function(a){a=a||win.event;if(a.fixed)return a;a.fixed=!0,a.preventDefault=a.preventDefault||function(){this.returnValue=!1},a.stopPropagation=a.stopPropagation||function(){this.cancelBubble=!0},a.target||(a.target=a.srcElement);if(a.pageX==n&&a.clientX!=n){var b=doc.documentElement,c=doc.body;a.pageX=a.clientX+(b&&b.scrollLeft||c&&c.scrollLeft||0)-(b.clientLeft||0),a.pageY=a.clientY+(b&&b.scrollTop||c&&c.scrollTop||0)-(b.clientTop||0)}return pl.type(a.which,u)&&(a.which=a.button&1?1:a.button&2?3:a.button&4?2:0),a},attaches:function(){function b(a){a=pl.events.mend(a);var b=this.evt[a.type];for(var c in b){var d=b[c].call(this,a);d===!1&&(a.preventDefault(),a.stopPropagation())}}var a=0;return{bind:function(c,d,e){if(c.setInterval&&!c.frameElement){c!==win&&(c=win);if(~pl.inArray(d,pl.__fwe__))return window.onload=function(){pl(doc.body).bind(d,e)}}e.turnID||(e.turnID=++a),c.evt||(c.evt={},c.handleEvt=function(a){if(!pl.type(pl.events.attaches,u))return b.call(c,a)}),c.evt[d]||(c.evt[d]={},c[ael]?c[ael](d,c.handleEvt,!1):c.attachEvent("on"+d,c.handleEvt)),c.evt[d][e.turnID]=e},unbind:function(a,b,c){var d=a.evt;if(pl.type(c,u)){if(!d)return;for(var e in d)if(pl.type(b,u)||b===e)for(var f in d[e])pl.events.attaches.unbind(a,e,d[e][f]);return}d=d&&d[b];if(!d)return;delete d[c.turnID];for(var f in d)return;a.removeEventListener?a.removeEventListener(b,a.handleEvt,!1):a.detachEvent("on"+b,a.handleEvt),delete a.evt[b];for(var f in a.evt)return;try{delete a.handleEvt,delete a.evt}catch(g){a.removeAttribute("handleEvt"),a.removeAttribute("evt")}}}}(),routeEvent:function(a,b,c){if(pl.type(a,"obj"))for(var d in a)pl.events.routeEvent(d,a[d],c);else if(b&&a||!b&&a||!b&&!a)c?pl.each(pl.__self__.elements,function(){pl.events.attaches.bind(this,a,b)}):pl.each(pl.__self__.elements,function(){pl.events.attaches.unbind(this,a,b)});return pl.__self__}},__fwe__:["click","mouseover","mouseout","keyup","keydown","dblclick","mousedown","mouseup","keypress"]}),pl.extend(pl.fn,{bind:function(a,b){return pl.events.routeEvent(a,b,1)},unbind:function(a,b){return pl.events.routeEvent(a,b,0)}})}(),function(){var a=!!doc[ge+"sByClassName"],b=!!doc.querySelectorAll;pl.find=function(c){return pl.extend(c,{attr:{"":function(a,b){return!!a.getAttribute(b)},"=":function(a,b,c){return(b=a.getAttribute(b))&&b===c},"&=":function(a,b,c){return},"^=":function(a,b,c){return},"$=":function(a,b,c){return},"*=":function(a,b,c){return},"|=":function(a,b,c){return},"!=":function(a,b,c){return}},mods:{"first-child":function(a){return a.parentNode.getElementsByTagName("*")[0]!==a},"last-child":function(a){var b=a;while((b=b.nextSibling)&&b.nodeType!=1);return!!b},root:function(a){return a.nodeName.toLowerCase()!=="html"},"nth-child":function(a,b){var c=a.nodeIndex||0,d=b[3]=b[3]?(b[2]==="%"?-1:1)*b[3]:0,e=b[1];if(c)return!((c+d)%e);var f=a.parentNode.firstChild;c++;do if(f.nodeType==1&&(f.nodeIndex=++c)&&a===f&&(c+d)%e)return 0;while(f=f.nextSibling);return 1},"nth-last-child":function(a,b){var c=a.nodeIndexLast||0,d=b[3]?(b[2]==="%"?-1:1)*b[3]:0,e=b[1];if(c)return!((c+d)%e);var f=a.parentNode.lastChild;c++;do if(f.nodeType==1&&(f.nodeLastIndex=c++)&&a===f&&(c+d)%e)return 0;while(f=f.previousSibling);return 1},empty:function(a){return!!a.firstChild},parent:function(a){return!a.firstChild},"only-child":function(a){return a.parentNode[ge+"sByTagName"]("*").length!=1},checked:function(a){return!a.checked},lang:function(a,b){return a.lang!==b&&doc.documentElement.lang!==b},enabled:function(a){return a.disabled||a.type==="hidden"},disabled:function(a){return!a.disabled},selected:function(a){return child.parentNode.selectedIndex,!child.selected}}}),function(d,e){e&&(d=e+" "+d),e=doc;var f=[];if(d==="body *")return doc.body[ge+"sByTagName"]("*");if(/^[\w[:#.][\w\]*^|=!]*$/.test(d)){var g=0;switch(d.charAt(0)){case"#":g=d.slice(1),f=doc[ge+"ById"](g),pl.browser("ie")&&f&&f.id!==g&&(f=doc.all[g]),f=f?[f]:[];break;case".":var h=d.slice(1);if(a)f=(g=(f=e[ge+"sByClassName"](h)).length)?f:[];else{h=" "+h+" ";var i=e[ge+"sByTagName"]("*"),j=0,k;while(k=i[j++])(" "+k[cn]+" ").indexOf(h)!=-1&&(f[g++]=k);f=g?f:[]}break;case":":var k,i=e[ge+"sByTagName"]("*"),j=0,l=d.replace(/[^(]*\(([^)]*)\)/,"$1"),m=d.replace(/\(.*/,"");while(k=i[j++])c.mods[m]&&!c.mods[m](k,l)&&(f[g++]=k);f=g?f:[];break;case"[":var i=e[ge+"sByTagName"]("*"),k,j=0,o=/\[([^!~^*|$ [:=]+)([$^*|]?=)?([^ :\]]+)?\]/.exec(d),p=o[1],q=o[2]||"",r=o[3];while(k=i[j++])console.log("216:",q),c.attr[q]&&(c.attr[q](k,p,r)||p==="class"&&c.attr[q](k,cn,r))&&(f[g++]=k);f=g?f:[];break;default:f=(g=(f=e[ge+"sByTagName"](d)).length)?f:[]}}else if(b&&!~d.indexOf("!="))f=e.querySelectorAll(d.replace(/=([^\]]+)/,'="$1"'));else{var s=d.split(/ *, */),t=s.length-1,u=!!t,v,w,x,y,j,z,i,A,B,h,p,q,m,l,C,g,D,E,F,G,H,I;while(v=s[t--]){x=(w=v.replace(/(\([^)]*)\+/,"$1%").replace(/(\[[^\]]+)~/,"$1&").replace(/(~|>|\+)/," $1 ").split(/ +/)).length,j=0,z=" ",i=[e];while(y=w[j++])if(y!==" "&&y!==">"&&y!=="~"&&y!=="+"&&i){y=y.match(/([^[:.#]+)?(?:#([^[:.#]+))?(?:\.([^[:.]+))?(?:\[([^!&^*|$[:=]+)([!$^*|&]?=)?([^:\]]+)?\])?(?:\:([^(]+)(?:\(([^)]+)\))?)?/),A=y[1]||"*",B=y[2],h=y[3]?" "+y[3]+" ":"",p=y[4],q=y[5]||"",m=y[7],l=m==="nth-child"||m==="nth-last-child"?/(?:(-?\d*)n)?(?:(%|-)(\d*))?/.exec(y[8]==="even"&&"2n"||y[8]==="odd"&&"2n%1"||!/\D/.test(y[8])&&"0n%"+y[8]||y[8]):y[8],C=[],g=D=0,F=j==x;while(E=i[D++])switch(z){case" ":G=E[ge+"sByTagName"](A),I=0,console.log("304: ...");while(H=G[I++])(!B||H.id===B)&&(!h||(" "+H[cn]+" ").indexOf(h)!=-1)&&(!p||c.attr[q]&&(c.attr[q](H,p,y[6])||p==="class"&&c.attr[q](H,cn,y[6])))&&!H.yeasss&&(c.mods[m]?!c.mods[m](H,l):!m)&&(console.log("Passed."),F&&(H.yeasss=1),C[g++]=H);break;case"~":A=A.toLowerCase();while((E=E.nextSibling)&&!E.yeasss)E.nodeType==1&&(A==="*"||E.nodeName.toLowerCase()===A)&&(!B||E.id===B)&&(!h||(" "+E[cn]+" ").indexOf(h)!=-1)&&(!p||c.attr[q]&&(c.attr[q](H,p,y[6])||p==="class"&&c.attr[q](H,cn,y[6])))&&!E.yeasss&&(c.mods[m]?!c.mods[m](E,l):!m)&&(F&&(E.yeasss=1),C[g++]=E);break;case"+":while((E=E.nextSibling)&&E.nodeType!=1);E&&(E.nodeName.toLowerCase()===A.toLowerCase()||A==="*")&&(!B||E.id===B)&&(!h||(" "+H[cn]+" ").indexOf(h)!=-1)&&(!p||c.attr[q]&&(c.attr[q](H,p,y[6])||p==="class"&&c.attr[q](H,cn,y[6])))&&!E.yeasss&&(c.mods[m]?!c.mods[m](E,l):!m)&&(F&&(E.yeasss=1),C[g++]=E);break;case">":G=E[ge+"sByTagName"](A),j=0;while(H=G[j++])H.parentNode===E&&(!B||H.id===B)&&(!h||(" "+H[cn]+" ").indexOf(h)!=-1)&&(!p||c.attr[q]&&(c.attr[q](H,p,y[6])||p==="class"&&c.attr[q](H,cn,y[6])))&&!H.yeasss&&(c.mods[m]?!c.mods[m](H,l):!m)&&(F&&(H.yeasss=1),C[g++]=H)}i=C}else z=y;if(u){if(!i.concat){C=[],I=0;while(H=i[I])C[I++]=H;i=C}f=i.concat(f.length==1?f[0]:f)}else f=i}g=f.length;while(g--)f[g].yeasss=f[g].nodeIndex=f[g].nodeIndexLast=n}return f}}({})}(),function(){pl.extend({innerText:pl.browser("ie")?"innerText":"textContent",innerContent:{midst:function(a,b,c,d){var e=a,a=e.elements[0];if(pl.type(c,u))return a[b];if(pl.type(c,"obj")){var f=doc.createElement("div");f.appendChild(c),c=f.innerHTML}return pl.each(e.elements,function(){d?~d?this[b]+=c:this[b]=c+this[b]:this[b]=c}),e},edge:function(a,b,c,d,e){var f=pl.clean(b);for(var g=d<0?f.length-1:0;g!=(d<0?d:f.length);g+=d)e(a,f[g])}},clean:function(a){var b=[],c=a.length;for(var d=0;d<c;++d)if(pl.type(a[d],"str")){var e="";if(!a[d].indexOf("<thead")||!a[d].indexOf("<tbody"))e="thead",a[d]="<table>"+a[d]+"</table>";else if(!a[d].indexOf("<tr"))e="tr",a[d]="<table>"+a[d]+"</table>";else if(!a[d].indexOf("<td")||!a[d].indexOf("<th"))e="td",a[d]="<table><tbody><tr>"+a[d]+"</tr></tbody></table>";var f=doc.createElement("div");f.innerHTML=a[d],e&&(f=f.firstChild,e!=="thead"&&(f=f.firstChild),e==="td"&&(f=f.firstChild));var g=f.childNodes.length;for(var h=0;h<g;++h)b.push(f.childNodes[h])}else a[d]!==n&&b.push(a[d].nodeType?a[d]:doc.createTextNode(a[d].toString()));return b}}),pl.extend(pl.fn,{html:function(a,b){return pl.innerContent.midst(this,"innerHTML",a,b)},text:function(a,b){return pl.innerContent.midst(this,pl.innerText,a,b)},after:function(){var a=arguments;return pl.each(this.elements,function(){pl.innerContent.edge(this,a,!1,-1,function(a,b){a.parentNode.insertBefore(b,a.nextSibling)})}),this},before:function(){var a=arguments;return pl.each(this.elements,function(){pl.innerContent.edge(this,a,!1,1,function(a,b){a.parentNode.insertBefore(b,a)})}),this},append:function(){var a=arguments;return pl.each(this.elements,function(){pl.innerContent.edge(this,a,!0,1,function(a,b){a.appendChild(b)})}),this},prepend:function(){var a=arguments;return pl.each(this.elements,function(){pl.innerContent.edge(this,a,!0,-1,function(a,b){a.insertBefore(b,a.firstChild)})}),this},appendTo:function(a,b,c){return pl.each(this.elements,function(){pl(a,b,c).append(this)}),this},prependTo:function(a,b,c){return pl.each(this.elements,function(){pl(a,b,c).prepend(this)}),this}})}(),function(){pl.extend(pl.fn,{show:function(){return pl.each(this.elements,function(){this.style.display=this.plrd?this.plrd:"",pl.curCSS.get(this,"display")==="none"&&(this.style.display="block")}),this},hide:function(){return pl.each(this.elements,function(){this.plrd=this.plrd||pl.curCSS.get(this,"display"),this.plrd==="none"&&(this.plrd="block"),this.style.display="none"}),this}})}()})(this,document,"prototype","addEventListener","getElement","className","null","undef","<([A-z]+[1-6]*)>",null,function(){})

/* startupbidder base library */

function SafeStringClass() {}
pl.implement(SafeStringClass, {
    trim: function(str) {
        if (!str) {
            return '';
        }
        return str.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
    },
    htmlEntities: function (str) {
        if (!str) {
            return '';
        }
        return str.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
    },
    clean: function(str) {
        return SafeStringClass.prototype.htmlEntities(SafeStringClass.prototype.trim(str));
    },
    ucfirst: function(str) {
        return str.charAt(0).toUpperCase() + str.slice(1);
    }
});

function EventClass(e) {
    this.e = e || window.event;
}
pl.implement(EventClass, {
    target: function() {
        var targ;
        if (this.e.target) {
            targ = this.e.target;
        }
        else if (e.srcElement) {
            targ = this.e.srcElement;
        }
        if (targ.nodeType == 3) { // defeat Safari bug
            targ = targ.parentNode;
        }
        return targ;
    },
    keyCode: function() {
        var code;
        if (this.e.keyCode) {
            code = this.e.keyCode;
        }
        else if (this.e.which) {
            code = this.e.which;
        }
        return code;
    },
    rightClick: function() {
        var rightclick;
        if (this.e.which) {
            rightclick = (this.e.which == 3);
        }
        else if (this.e.button) {
            rightclick = (e.button == 2);
        }
        return rightclick;
    },
    mousePos: function() {
        var posx = 0;
        var posy = 0;
        if (this.e.pageX || this.e.pageY) {
            posx = this.e.pageX;
            posy = this.e.pageY;
        }
        else if (this.e.clientX || this.e.clientY) {
            posx = this.e.clientX + document.body.scrollLeft
                + document.documentElement.scrollLeft;
            posy = this.e.clientY + document.body.scrollTop
                + document.documentElement.scrollTop;
        }
        return [ posx, posy ];
    }
});

function DateClass() {}
pl.implement(DateClass, {
    format: function(datestr) {
        if (!datestr) {
            return '';
        }
        else if (datestr.length === 8) {
            return DateClass.prototype.formatDateStr(datestr);
        }
        else if (datestr.length === 14) {
            return DateClass.prototype.formatDatetimeStr(datestr);
        }
        else {
            return '';
        }
    },
    formatDateStr: function(yyyymmdd) {
        return yyyymmdd ? yyyymmdd.substr(0,4) + '-' + yyyymmdd.substr(4,2) + '-' + yyyymmdd.substr(6,2) : '';
    },
    formatDatetimeStr: function(yyyymmddhh24mmss) {
        return yyyymmddhh24mmss ? yyyymmddhh24mmss.substr(0,4) + '-' + yyyymmddhh24mmss.substr(4,2) + '-' + yyyymmddhh24mmss.substr(6,2)
            + ' ' + yyyymmddhh24mmss.substr(8,2) + ':' + yyyymmddhh24mmss.substr(10,2) + ':' + yyyymmddhh24mmss.substr(12,2) : '';
    },
    formatDate: function(dateObj) {
        var year = dateObj.getUTCFullYear(),
            month = dateObj.getUTCMonth()+1,
            date = dateObj.getUTCDate();
        return '' + year + (month < 10 ? 0 : '') + month + (date < 10 ? 0 : '') + date;
    },
    zeroPad: function(num, length) {
        var str = '' + num,
            len = length || 2;
        while (str.length < len) {
            str = '0' + str;
        }
        return str;
    },
    formatDatetime: function(dateObj) {
        var year = dateObj.getUTCFullYear(),
            month = DateClass.prototype.zeroPad(dateObj.getUTCMonth()+1),
            date = DateClass.prototype.zeroPad(dateObj.getUTCDate()),
            hour = DateClass.prototype.zeroPad(dateObj.getUTCHours()),
            min = DateClass.prototype.zeroPad(dateObj.getUTCMinutes()),
            sec = DateClass.prototype.zeroPad(dateObj.getUTCSeconds());
        return '' + year + month + date + hour + min + sec;
    },
    todayDate: function() {
        return new Date();
    },
    dateFromYYYYMMDD: function(yyyymmdd) {
        var date = yyyymmdd || DateClass.prototype.formatDate(DateClass.prototype.nowUTC()),
            yyyy = date.substr(0,4),
            mm = date.substr(4,2) - 1,
            dd = date.substr(6,2);
        return new Date(yyyy, mm, dd);
    },
    dateFromYYYYMMDDHHMMSS: function(yyyymmddhhmmss) {
        var datetime = yyyymmddhhmmss || DateClass.prototype.formatDatetime(DateClass.prototype.nowUTC()),
            yyyy = datetime.substr(0,4),
            mo = datetime.substr(4,2) - 1,
            dd = datetime.substr(6,2),
            hh = datetime.substr(8,2),
            mm = datetime.substr(10,2),
            ss = datetime.substr(12,2),
            date = new Date(yyyy, mo, dd, hh, mm, ss);
        return date;
    },
    today: function() {
        var today = new Date();
        return DateClass.prototype.formatDate(today);
    },
    now: function() {
        var now = new Date();
        return DateClass.prototype.formatDatetime(now);
    },
    nowUTC: function() {
        var now = new Date(),
            now_utc = new Date(now.getUTCFullYear(), now.getUTCMonth(), now.getUTCDate(),  now.getUTCHours(), now.getUTCMinutes(), now.getUTCSeconds());
        return now_utc;
    },
    todayPlus: function(days) {
        var today = new Date();
            todayPlus = DateClass.prototype.addDays(today, days);
        return DateClass.prototype.formatDate(todayPlus);
    },
    addDays: function(dateObj, days) {
        var t1 = dateObj.getTime(),
            d = 86400 * days,
            t2 = t1 + d,
            newDate = new Date();
        newDate.setTime(t2);
        return newDate;
    },
    daysBetween: function(d1, d2) {
        var d1num = DateClass.prototype.formatDate(d1),
            d2num = DateClass.prototype.formatDate(d2);
        return Math.floor(d2num - d1num);
    },
    agoText: function(yyyymmsshhmmss) { // assumed UTC passed in
        var date = DateClass.prototype.dateFromYYYYMMDDHHMMSS(yyyymmsshhmmss),
            today = DateClass.prototype.nowUTC(),
            dateTime = date.getTime(),
            todayTime = today.getTime(),
            diffTime = todayTime >= dateTime ? todayTime - dateTime : 0, // handle accidental future
            diffDays = Math.floor(diffTime / 86400000),
            diffHours = Math.floor(diffTime / 3600000),
            diffMinutes = Math.floor(diffTime / 60000),
            agoText = diffDays
                ? diffDays + ' day' + (diffDays > 1 ? 's' : '') + ' ago'
                : ( diffHours
                    ? diffHours + ' hour' + (diffHours > 1 ? 's' : '') + ' ago'
                    : ( diffMinutes
                        ? diffMinutes + ' minute' + (diffMinutes > 1 ? 's' : '') + ' ago' : 'just now'));
        return agoText;
    }
});

function NumberClass() {}
pl.implement(NumberClass, {
    formatText: function(num, _prefix, _postfix, _thousandsep, _decimalpoint) {
        var prefix = _prefix || '',
            postfix = _postfix || '',
            thousandsep = _thousandsep || ',',
            decimalpoint = _decimalpoint || '.',
            nStr, x, x1, x2, rgx, text;
        if (!num) {
            return '';
        }
	    nStr = NumberClass.prototype.clean(num);
		x = nStr.split(decimalpoint);
		x1 = x[0];
		x2 = x.length > 1 ? decimalpoint + x[1] : '';
		rgx = /(\d+)(\d{3})/;
		while (rgx.test(x1)) {
			x1 = x1.replace(rgx, '$1' + thousandsep + '$2');
		}
        text = prefix + x1 + x2 + postfix;
		return text;
    },
    format: function(num) {
        return NumberClass.prototype.clean(num);
    },
    clean: function(num) {
        var str = '' + num;
            numstr = str.replace(/[^0-9\.]*/g, ''),
            noleadzerostr = numstr.replace(/^0*/, '');
        return noleadzerostr;
    },
    isNumber: function(str) {
        var match = str ? str.match(/^[0-9]*$/) : false;
        return (match ? 0 : 'Please enter a numeric value');
    }
});

function CurrencyClass() {}
pl.implement(CurrencyClass, {
    format: function(num) {
        return NumberClass.prototype.formatText(num, '$');
    },
    formatNoSymbol: function(num) {
        return NumberClass.prototype.formatText(num, '');
    },
    clean: function(num) {
        return NumberClass.prototype.clean(num);
    },
    isCurrency: function(str) {
        var match = str ? str.match(/^[$]?[0-9]{1,3}(,?[0-9]{3})*$/) : false;
        return (match ? 0 : 'Please enter a currency value');
    }
});

function PercentClass() {}
pl.implement(PercentClass, {
    format: function(num) {
        return NumberClass.prototype.formatText(num, '', '');
    },
    clean: function(num) {
        return NumberClass.prototype.clean(num);
    },
    isPercent: function(str) {
        var match = str ? str.match(/^[1-9]?[0-9][%]?$/) : false;
        return (match ? 0 : 'Please enter a percent value');
    }
});

function ValuationClass() {}
ValuationClass.prototype.valuation = function(amt, pct) {
    return amt && pct ? Math.floor((100 / pct) * amt) : 0;
}

function URLClass(url) {
    this.url = url;
}
pl.implement(URLClass, {
    getHostname: function() {
        var re = new RegExp('^(?:f|ht)tp(?:s)?\://([^/]+)', 'im'),
            url = this.url || '',
            matches = url.match(re),
            hostname = matches && matches.length >= 1 && matches[1] ? matches[1].toString() : '';
        return hostname;
    },
    getURL: function() {
        return this.url;
    }
});

function QueryStringClass() {
    var pairs = window.location.search ? window.location.search.substr(1).split( "&" ) : {},
        i,
        keyval;
    this.vars = {};
    for (i in pairs) {
        keyval = pairs[ i ].split( "=" );
        this.vars[ keyval[0] ] = keyval[1];
    }
}

function CollectionsClass() {}
pl.implement(CollectionsClass, {
    merge : function(o1, o2) {
        for (k in o2) {
            o1[k] = o2[k];
        }
    }
});

function HTMLMarkup() {}
pl.implement(HTMLMarkup, {
    stylize: function(text, styleprefix) {
        var stylized = text ? '' + text : '',
            spacerclass = styleprefix ? styleprefix + 'spacer' : '',
            listclass = styleprefix ? styleprefix + 'list' : '';
        if (stylized) {
            stylized = SafeStringClass.prototype.htmlEntities(stylized);
            stylized = stylized.replace(/^[ \t]*[*][ \t]*([^\n]*)/g, '<ul class="' + listclass + '"><li>$1</li></ul>');
            stylized = stylized.replace(/\n[ \t]*[*][ \t]*([^\n]*)/g, '\n<ul class="' + listclass + '"><li>$1</li></ul>');
            stylized = stylized.replace(/(<\/ul>)\n/g, '$1');
            stylized = stylized.replace(/\n/g, '<br/>');
        }
        return stylized;
    }
});

function AjaxClass(url, statusId, completeFunc, successFunc, loadFunc, errorFunc) {
    var self = this;
    this.url = url;
    this.statusId = statusId;
    this.statusSel = '#' + statusId;
    this.completeFunc = completeFunc || function(json) {};
    this.successFunc = successFunc || function(json) {
        if (!json) {
            pl('#listingstatus').html('<span class="attention">Error: null response from server</span>');
            return;
        }
        pl(self.statusSel).text('');
        self.completeFunc(json);
    };
    // this.loadFunc = loadFunc || function() { pl(self.statusSel).html('<span class="inprogress">Loading...</span>'); };
    this.loadFunc = loadFunc || function() { };
    this.errorFunc = errorFunc || function(errorNum, json) {
        var errorStr = (json && json.error_msg) ? 'Error: ' + json.error_msg : 'Error from server: ' + errorNum;
        pl(self.statusSel).html('<span class="attention">' + errorStr + '</span>');
    };
    this.ajaxOpts = {
        async: true,
        url: this.url,
        type: 'GET',
        dataType: 'json',
        charset: 'utf-8',
        load: this.loadFunc,
        error: this.errorFunc,
        success: this.successFunc
    };
}
pl.implement(AjaxClass, {
    setPost: function() {
        this.ajaxOpts.type = 'POST';
    },
    setGetData: function(data) {
        this.ajaxOpts.data = data;
    },
    setPostData: function(data) { // for post operations
        var property, propertyData, serializedData;
        this.setPost();
        serializedData = {};
        for (property in data) {
            propertyData = data[property];
            serializedData[property] = JSON.stringify(propertyData);
        }
        this.ajaxOpts.data = serializedData;
    },
    mock: function(json) {
        this.mockData = json;
    },
    call: function() {
        if (this.mockData) {
            this.successFunc(this.mockData);
        }
        else {
            pl.ajax(this.ajaxOpts);
        }
    }
});

function SearchBoxClass() {}
pl.implement(SearchBoxClass, {
    bindEvents: function() {
        var qs = new QueryStringClass(),
            val = (qs && qs.vars && qs.vars.searchtext) ? qs.vars.searchtext : 'Search',
            displayVal = decodeURIComponent(val).replace(/\+/g, ' ');
        pl('#searchtext').attr({value: displayVal});
        pl('#searchtext').bind({
            focus: function() {
                if (pl('#searchtext').attr('value') === 'Search') {
                    pl('#searchtext').attr({value: ''});
                }
            },
            keyup: function(e) {
                var evt = new EventClass(e);
                if (evt.keyCode() === 13) {
                    pl('#searchform').get(0).submit();
                    return false;
                }
                return true;
            }
        });
    }
});

function HeaderClass() {}
pl.implement(HeaderClass, {
    setLogin: function(json) {
        var profile = null,
            searchbox = new SearchBoxClass();
        if (json && json.loggedin_profile) {
            profile = json.loggedin_profile;
        }
        else if (json && json.profile_id) {
            profile = json;
        }
        this.setHeader(profile, json.login_url, json.logout_url, json.twitter_login_url, json.fb_login_url);
        searchbox.bindEvents();
    },
    setHeader: function(profile, login_url, logout_url, twitter_login_url, fb_login_url) {
        if (profile) {
            this.setLoggedIn(profile, logout_url);
        }
        else {
            this.setLoggedOut(login_url, twitter_login_url, fb_login_url);
        }
    },
    setLoggedIn: function(profile, logout_url) {
        var num_notifications = profile.num_notifications || 0,
            num_messages = profile.num_messages || 0,
            notificationlinktext = num_notifications ? num_notifications + ' unread notifications' : 'no unread notifications';
        if (num_messages) {
            pl('#headernummessages').text(num_messages).addClass('headernumdisplay');
        }
        else {
            pl('#headernummessages').text('').removeClass('headernumdisplay');
        }
        if (num_notifications) {
            pl('#headernumnotifications').text(num_notifications).addClass('headernumdisplay');
        }
        else {
            pl('#headernumnotifications').text('').removeClass('headernumdisplay');
        }
        if (profile.admin) {
            pl('#adminfooterlinks').show();
        }
        if (profile.avatar) {
            pl('#headeravatar').css('background-image', 'url(' + profile.avatar + ')');
        }
        if (logout_url) {
            pl('#logoutlink').attr({href: logout_url});
        }
        pl('#headerloggedin').show();
    },
    setLoggedOut: function(login_url, twitter_login_url, fb_login_url) {
        if (login_url) {
            pl('#loginlink').attr({href: login_url});
            if (twitter_login_url) {
                pl('#twitter_loginlink').attr({href: twitter_login_url}).show();
            } else {
                pl('#twitter_loginlink').hide();
            }
            if (fb_login_url) {
                pl('#fb_loginlink').attr({href: fb_login_url}).show();
            } else {
                pl('#fb_loginlink').hide();
            }
        }
        pl('#headernotloggedin').show();
    }
});

function CookieClass() {}
CookieClass.prototype.createCookie = function(name,value,days,domain) {
    if (days) {
        var date = new Date();
        date.setTime(date.getTime()+(days*24*60*60*1000));
        var expires = "; expires="+date.toGMTString();
    }
    else var expires = "";
    document.cookie = name+"="+value+expires+"; path=/" + (domain ? '; domain=' + domain : '');
}
CookieClass.prototype.readCookie = function(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}
CookieClass.prototype.eraseCookie = function(name) {
    createCookie(name,"",-1);
}

function PlatformClass() {}
PlatformClass.prototype.displayName = function(platform) {
    var map = {
        ios: 'iOS',
        android: 'Android',
        windows_phone: 'Windows Phone',
        desktop: 'Desktop',
        website: 'Web',
        other: 'Other'
    };
    return map[platform];
}

function ScriptClass() {}
ScriptClass.prototype.load = function(url, callback) {
    var script = document.createElement("script")
    script.type = "text/javascript";

    if (script.readyState){  //IE
        script.onreadystatechange = function(){
            if (script.readyState == "loaded" ||
                    script.readyState == "complete"){
                script.onreadystatechange = null;
                callback();
            }
        };
    }
    else {  //Others
        script.onload = function(){
            callback();
        };
    }

    script.src = url;
    document.getElementsByTagName("head")[0].appendChild(script);
}

function MicroListingClass() {}
pl.implement(MicroListingClass, {
    getHasValuation: function(listing) {
        return listing.valuation_data ? true : false;
    },

    getHasBmc: function(listing) {
        return listing.has_bmc ? true : false;
    },

    getHasIp: function(listing) {
        return listing.has_ip ? true : false;
    },

    getHasDoc: function(listing) {
        var docFields = [ 'presentation_id', 'business_plan_id', 'financials_id' ],
            hasDoc = false,
            fieldName,
            i;
        for (i = 0; i < docFields.length; i++) {
            fieldName = docFields[i];
            if (listing[fieldName]) {
                hasDoc = true;
                break;
            }
        }
        return hasDoc;
    }
});

function CompanyFormatClass() {}
CompanyFormatClass.prototype.daysText = function(listing) {
    var daystext = '',
        daysago;
    if (listing.status === 'new') {
        daystext = 'Awaiting submission';
    }
    else if (listing.status === 'posted') {
        daystext = 'Awaiting approval';
    }
    else if (listing.status === 'active' && listing.asked_fund) {
        daystext = 'Bidding open';
    }
    else if (listing.status === 'active' && !listing.asked_fund && listing.listing_date) {
        daysago = DateClass.prototype.daysBetween(DateClass.prototype.dateFromYYYYMMDD(listing.listing_date), DateClass.prototype.todayDate());
        daystext = daysago === 0 ? 'Listed today' : 'Listed ' + daysago + ' day' + (daysago > 1 ? 's' : '') + ' ago';
    }
    else {
        daystext = SafeStringClass.prototype.ucfirst(listing.status);
    }
    return daystext;
};
CompanyFormatClass.prototype.suggestedText = function(listing) {
    var suggested_text = '',
        suggested_amt,
        suggested_pct;
    if (listing.asked_fund && listing.suggested_amt && listing.suggested_pct) {
        suggested_amt = CurrencyClass.prototype.format(listing.suggested_amt);
        suggested_pct = PercentClass.prototype.format(listing.suggested_pct) + '%';
        suggested_text = suggested_amt + ' for ' + suggested_pct;
    }
    else {
        suggested_text = 'Not raising funds';
    }
    return suggested_text;
};
CompanyFormatClass.prototype.financeLine = function(listing) {
    var finance_line = '';
    if (listing.asked_fund && listing.suggested_amt && listing.suggested_pct) {
        finance_line = CompanyFormatClass.prototype.daysText(listing) + ' at ' + CompanyFormatClass.prototype.suggestedText(listing);
    }
    else {
        finance_line = CompanyFormatClass.prototype.daysText(listing);
    }
    return finance_line;
};

/* company list stuff follows */
function CompanyTileClass(options) {
    this.options = options || {};
    this.companybannertileclass = options.companybannertileclass || 'companybannertile';
    if (this.options.json) {
        this.store(this.options.json);
    }
}
pl.implement(CompanyTileClass, {
    store: function(json) {
        var cat,
            catprefix,
            catlink,
            platform,
            platformtext,
            categorytext,
            platformprefix,
            stagetext,
            typetext,
            locprefix,
            profilelinked,
            addr;
        this.status = json.status;
        this.daystext = CompanyFormatClass.prototype.daysText(json);
        this.imgClass = json.logo ? '' : 'noimage';
        this.imgStyle = json.logo ? 'background: url(' + json.logo + ') no-repeat scroll center center transparent' : '',
        this.posted = json.posted_date ? DateClass.prototype.format(json.posted_date) : 'not posted';
        this.name = json.title || 'No Company / App';
    
        this.type = json.type || 'venture';
        this.category = json.category || 'Other';
        this.categoryUC = json.category ? json.category.toUpperCase() : 'OTHER';
        cat = this.category || '';
        catprefix = !cat || (cat !== 'Other' && !cat.match(/^[aeiou]/i)) ? 'A' : 'An';
        catlink = cat && cat !== 'Other' ? '<a href="/main-page.html?type=category&val=' + encodeURIComponent(cat) + '">' + cat + '</a>' : '';

        this.platform = json.platform;
        platform = json.platform || '';
        platformtext = platform && platform !== 'other' ? PlatformClass.prototype.displayName(platform) + ' ' : '';
        categorytext = platform && platform !== 'other' && cat === 'Software' ? '' : catprefix + ' ' + catlink + ' ';
        platformprefix = categorytext ? '' : (platform.match(/^[aeiou]/i) ? 'An ' : 'A ');
        stagetext = this.stage && this.stage !== 'established' ? this.stage : '';
        typetext = this.type === 'application' ? this.type + ' ' + stagetext : (stagetext || 'company');
        this.catlinked = categorytext + platformprefix + platformtext + typetext;

        addr = json.brief_address;
        this.brief_address = json.brief_address
            ? '<a class="hoverlink" href="/main-page.html?type=location&val=' + encodeURIComponent(json.brief_address) + '">'
                + '<div class="locicon"></div><span class="loctext">' + json.brief_address + '</span></a>'
            : '<span class="loctext">No Address</span>';
        this.brief_address_inp = json.brief_address
            ?     '</p>'
                + '<a class="hoverlink" href="/main-page.html?type=location&val=' + encodeURIComponent(json.brief_address) + '">'
                + '<div class="lociconinp"></div>&nbsp;<span class="loctextinp">' + json.brief_address + '</span>'
                + '</a>'
                + '<p>'
            : '<span class="loctext">No Address</span>';
        this.address = json.address || 'No Address';
        locprefix = this.type === 'company' ? 'in' : 'from';
        this.addrlinked = !addr ? '' : ' ' + locprefix + ' <a href="/main-page.html?type=location&val=' + encodeURIComponent(addr) + '">' + addr + '</a>';
        profilelinked = !json.profile_id ? '' : ' by <a href="/profile-page.html?id=' + json.profile_id + '">' + (json.profile_username || 'owner') + '</a>';
        this.categoryaddresstext = this.catlinked + this.addrlinked + profilelinked;
        this.suggested_text = CompanyFormatClass.prototype.suggestedText(json);
        this.finance_line = CompanyFormatClass.prototype.financeLine(json);
        this.mantra = json.mantra || 'No Mantra';
        this.mantraplussuggest = this.mantra + '<br/>' + this.suggested_text;
        this.url = '/company-page.html?id=' + json.listing_id;
        this.websitelink = json.website || '#';
        this.websiteurl = json.website ? new URLClass(json.website) : null;
        this.websitedomain = this.websiteurl ? this.websiteurl.getHostname() : 'No Website';
        this.openanchor = this.options.preview ? '' : '<a href="' + this.url + '">';
        this.closeanchor = this.options.preview ? '' : '</a>';
    },
    makeHtml: function(lastClass) {
            html = '\
<span class="span-4 '+ (lastClass?lastClass:'') +'">\
<div class="tile">\
' + this.openanchor + '\
<div class="tileimg fourthtileimage hoverlink" style="' + this.imgStyle + '"></div>\
' + this.closeanchor + '\
<!--\
<div class="tiledays"></div>\
<div class="tiledaystext">' + this.daystext + '</div>\
<div class="tiletype"></div>\
<div class="tiletypetext">' + this.categoryUC + '</div>\
<div class="tilepoints"></div>\
-->\
<div class="tilepointstext">\
    <div class="tileposted">' + this.suggested_text + '</div>\
</div>\
<div class="tiledesc">\
' + this.openanchor + '\
    <span class="tilecompany hoverlink">' + this.name + '</span><br/>\
' + this.closeanchor + '\
    <span class="tileloc">' + this.catlinked + '</span><br/>\
    <span class="tileloc">' + this.brief_address + '</span><br/>\
    <span class="tiledetails">' + this.mantra + '</span>\
</div>\
</div>\
</span>\
';
        return html;
    },
    makeInfoWindowHtml: function() {
        return '\
<div class="infowindow">\
' + this.openanchor + '\
<div class="tileimg hoverlink" style="' + this.imgStyle + '"></div>\
' + this.closeanchor + '\
<p>\
    ' + this.openanchor + '\
    <div class="infotitle hoverlink">' + this.name + '</div>\
    ' + this.closeanchor + '\
    <div>' + this.address + '</div>\
    <div class="infomantra">' + this.mantra + '</div>\
    <div><span class="infolabel">Type:</span> ' + SafeStringClass.prototype.ucfirst(this.type) + '</div>\
    ' + (this.type !== 'application' ? '' : '<div><span class="infolabel">Platform:</span> ' + PlatformClass.prototype.displayName(this.platform) + '</div>') + '\
    <div><span class="infolabel">Industry:</span> ' + this.category + '</div>\
    <div><span class="infolabel">Asking:</span> ' + this.suggested_text + '</div>\
</p>\
</div>\
';
    },
    makeFullWidthHtml: function() {
        return '\
<div class="' + this.companybannertileclass + ' last">\
' + this.openanchor + '\
    <div class="companybannerlogo tileimg fulltileimg noimage hoverlink" style="' + this.imgStyle + '"></div>\
' + this.closeanchor + '\
' + this.openanchor + '\
    <div class="companybannertitle companybannertiletitle ' + (this.name && this.name.length > 20 ? 'companybannertiletitlelong ' : '') + 'hoverlink">' + this.name + '</div>\
' + this.closeanchor + '\
    <div class="companybannertextgrey companybannermapline">\
        ' + this.categoryaddresstext + '\
    </div>\
    <div class="companybannertextgrey">' + this.finance_line + '</div>\
    <div class="companybannertextgrey companybannermantratile">' + this.mantra + '</div>\
</div>\
';
    },
    makeHalfWidthHtml: function(lastClass) {
        return '\
<div class="companyhalftile' + (lastClass?' companyhalftilelast '+lastClass:'') + '">\
' + this.openanchor + '\
    <div class="companyhalflogo tileimg halftileimg noimage hoverlink" style="' + this.imgStyle + '"></div>\
<!--\
    <div class="halftiledays"></div>\
    <div class="halftiledaystext">' + this.daystext + '</div>\
    <div class="halftiletype"></div>\
    <div class="halftiletypetext">' + this.suggested_text + '</div>\
-->\
' + this.closeanchor + '\
' + this.openanchor + '\
    <div class="companyhalftitle hoverlink">' + this.name + '</div>\
' + this.closeanchor + '\
    <div class="companyhalftext">\
        ' + this.catlinked +  '\
    </div>\
    <div class="companyhalftext">\
        ' + this.brief_address_inp + '\
    </div>\
    <div class="companyhalftext">' + this.mantraplussuggest + '</div>\
</div>\
';
    },
    display: function(listing, divid) {
        this.store(listing);
        pl('#'+divid).html(this.makeHtml('last'));
    }
});

function CompanyListClass(options) {
    this.options = options || {};
    this.options.colsPerRow = this.options.colsPerRow || ( this.options.fullWidth ? 1 : 4 );
    this.options.companydiv = this.options.companydiv || 'companydiv';
    this.options.propertykey = this.options.propertykey || 'listings';
}

pl.implement(CompanyListClass, {

    shouldDisplay: function(company) {
        var shouldDisplay = true;
        if ((this.options.propertykey === 'top_listings'
            || this.options.propertykey === 'active_listings'
            || this.options.propertykey === 'latest_listings')
            && company.status !== 'active') {
            shouldDisplay = false;        
        }
        return shouldDisplay;
    },

    storeList: function(json) {
        var companiesval = json && json[this.options.propertykey],
            isadmin = json && json.loggedin_profile && json.loggedin_profile.admin,
            tileoptions = { admin: isadmin },
            more_results_url = json.listings_props && json.listings_props.more_results_url,
            html = "",
            seeall,
            companies,
            company,
            tile,
            last,
            i;
        if (this.options.propertyissingle) {
            companies = companiesval ? [ companiesval ] : [];
        }
        else {
            companies = companiesval || [];
        }
        seeall = this.options.seeall && companies && (companies.length >= this.options.colsPerRow);
        if (!companies.length) {
            pl('#'+this.options.companydiv).html('<span class="identedtext attention">No companies found</span>');
            return;
        }
        if (this.options.exponential) { // display full width, then two half width, then the rest single width
        }
        for (i = 0; i < companies.length; i++) {
            company = companies[i];
            if (!this.shouldDisplay(company)) {
                continue;
            }
            tile  = new CompanyTileClass(tileoptions);
            tile.store(company);
            if (this.options.fullWidth || this.options.colsPerRow === 4 && companies.length === 1 || this.options.exponential && i === 0) {
                html += tile.makeFullWidthHtml();
            }
            else if (this.options.colsPerRow === 4 && companies.length === 2 && i === 0 || this.options.exponential && i === 1) {
                html += tile.makeHalfWidthHtml();
            }
            else if (this.options.colsPerRow === 4 && companies.length === 2 && i === 1 || this.options.exponential && i === 2) {
                html += tile.makeHalfWidthHtml('last');
            }
            else if (this.options.colsPerRow === 4 && companies.length === 3 && i < 2 && !this.options.exponential) {
                html += tile.makeHtml();
            }
            else if (this.options.colsPerRow === 4 && companies.length === 3 && i === 2 && !this.options.exponential) {
                html += tile.makeHalfWidthHtml('last');
            }
            else if ((i+1) % this.options.colsPerRow === 0 && !this.options.exponential) {
                html += tile.makeHtml('last');
            }
            else if (this.options.exponential && this.options.propertykey === 'top_listings' && companies.length === 4 && i >= 3) {
                // ignore so we don't have blank space
            }
            else if ((i+2) % this.options.colsPerRow === 0 && this.options.exponential) {
                html += tile.makeHtml('last');
            }
            else {
                html += tile.makeHtml();
            }
        }
        if (more_results_url) {
            html += '<div class="showmore hoverlink" id="moreresults"><span class="initialhidden" id="moreresultsurl">' + more_results_url + '</span><span id="moreresultsmsg">More...</span></div>\n';
        }
        else if (seeall) {
            html += '<div class="showmore"><a href="' + this.options.seeall + '">See all...</a></div>\n';
        }
        pl('#'+this.options.companydiv).html(html);
        if (more_results_url) {
            this.bindMoreResults();
        }
    },
    bindMoreResults: function() {
        var self = this;
        pl('#moreresults').bind({
            click: function() {
                var completeFunc = function(json) {
                        var companies = json.listings || [],
                            more_results_url = companies.length > 0 && json.listings_props && json.listings_props.more_results_url,
                            html = '',
                            company,
                            tile,
                            last,
                            i;
                        for (i = 0; i < companies.length; i++) {
                            company = companies[i];
                            tile  = new CompanyTileClass(self.options);
                            tile.store(company);
                            last = (i+1) % self.options.colsPerRow === 0 ? 'last' : '';
                            html += tile.makeHtml(last);
                        }
                        if (html) {
                            pl('#moreresults').before(html);
                        }
                        if (more_results_url) {
                            pl('#moreresultsurl').text(more_results_url);
                            pl('#moreresultsmsg').text('More...');
                        }
                        else {
                            pl('#moreresultsmsg').text('');
                            pl('#moreresults').removeClass('hoverlink').unbind();
                        }
                    },
                    more_results_url = pl('#moreresultsurl').text(),
                    index = more_results_url ? more_results_url.indexOf('?') : -1,
                    components = more_results_url && index >= 0 ? [ more_results_url.slice(0, index), more_results_url.slice(index+1) ] : [ more_results_url, null ],
                    url = components[0],
                    parameters = components[1] ? components[1].split('&') : null,
                    ajax,
                    data,
                    keyval,
                    i;
                if (more_results_url) {
                    ajax = new AjaxClass(url, 'moreresultsmsg', completeFunc);
                    if (parameters) {
                        data = {};
                        for (i = 0; i < parameters.length; i++) {
                            keyval = parameters[i].split('=');
                            data[keyval[0]] = keyval[1];
                        }
                        ajax.setGetData(data);
                    }
                    ajax.call();
                }
                else {
                    pl('#moreresultsmsg').text('');
                    pl('#moreresults').removeClass('hoverlink').unbind();
                }
            }
        });
    }
});

function BaseCompanyListPageClass(options) {
    this.options = options || {};
    this.queryString = new QueryStringClass();
    this.type = this.queryString.vars.type || 'top';
    this.val = this.queryString.vars.val ? decodeURIComponent(this.queryString.vars.val) : '';
    this.searchtext = this.queryString.vars.searchtext ? decodeURIComponent(this.queryString.vars.searchtext) : '';
    this.options.max_results = this.options.max_results || 20;
    this.data = { max_results: this.options.max_results };
    this.setListingSearch();
};
pl.implement(BaseCompanyListPageClass,{
    setListingSearch: function() {
        var searchtype = this.type;
        if (this.type === 'keyword') {
            this.data.text = this.searchtext;
        }
        else if (this.type === 'category') {
            this.data.category = this.val;
        }
        else if (this.type === 'location') {
            this.data.location = this.val;
        }
        this.url = '/listings/' + searchtype;
    },
    loadPage: function(completeFunc) {
        var titleroot = (this.type === 'category' || this.type === 'location') ? this.val.toUpperCase() : this.type.toUpperCase(),
            title = this.type === 'keyword' ? 'SEARCH RESULTS' : ((this.type === 'location') ? 'LISTINGS IN ' + titleroot : titleroot + ' LISTINGS'),
            ajax;
        this.setListingSearch();
        ajax = new AjaxClass(this.url, 'companydiv', completeFunc);
        pl('#listingstitle').html(title);
        if (this.type === 'top') {
            //pl('#banner').addClass('topbanner');
            pl('#welcometitle').html('Only the best');
            pl('#welcometext').html('The highest ranking listings on startupbidder');
        }
        else if (this.type === 'valuation') {
            //pl('#banner').addClass('valuationbanner');
            pl('#welcometitle').html('Invest in a startup today');
            pl('#welcometext').html('The listings below are ready for investment and open for bidding');
        }
        else if (this.type === 'keyword') {
            //pl('#banner').addClass('keywordbanner');
            pl('#welcometitle').html('Search for a startup');
            pl('#welcometext').html('Matching listings');
        }
        else if (this.type === 'latest') {
            //pl('#banner').addClass('latestbanner');
            pl('#welcometitle').html("What's fresh?");
            pl('#welcometext').html('The most recent listings on startupbidder');
        }
        else if (this.type === 'category') {
            //pl('#banner').addClass('categorybanner');
            pl('#welcometitle').html(this.val);
            pl('#welcometext').html('The latest listings in this industry');
        }
        else if (this.type === 'location') {
            //pl('#banner').addClass('locationbanner');
            pl('#welcometitle').html(this.val);
            pl('#welcometext').html('The latest listings from this location');
        }
        ajax.ajaxOpts.data = this.data;
        ajax.call();
    }
});

function ListClass(options) {
    this.options = options || {};
}
pl.implement(ListClass, {
    ucFirst: function(str) {
        return str.length > 1 ? str.substr(0,1).toUpperCase() + str.substr(1) : str.toUpperCase();
    },
    spreadOverOneCol: function(list, divcol1) {
        var htmlCol1 = '',
            i,
            item,
            name,
            count,
            itemurl;
        for (i = 0; i < list.length; i++) {
            item = list[i];
            name = item[0];
            count = item[1];
            itemurl = '/main-page.html?type=' + this.options.type + '&amp;val=' + encodeURIComponent(name);
            htmlCol1 +=
                 '<a href="' + itemurl + '" class="hoverlink">'
                +   '<li>'
                +     '<span class="sideboxlistname last">' + name + '</span>'
                +   '</li>'
                + '</a>';
        }
        pl(divcol1).html(htmlCol1);
    },
    spreadOverTwoCols: function(list, divcol1, divcol2) {
        var mid = Math.floor((list.length + 1) / 2);
        this.spreadOverOneCol(list.slice(0, mid), divcol1);
        if (list.length > 1) {
            this.spreadOverOneCol(list.slice(mid, list.length), divcol2);
        }
    }
});

function BaseListClass(kvlist, id, over, type) {
    this.kvlist = kvlist;
    this.msgid = id + 'msg';
    this.col1id = id + 'divcol1';
    this.col2id = id + 'divcol2';
    this.wrapperid = id + 'wrapper';
    this.over = over ? over : 1;
    this.type = type;
}
pl.implement(BaseListClass, {
    display: function() {
        var self = this,
            list = [], 
            lc = new ListClass({type: self.type}),
            k,
            v,
            keys = [];
        for (k in self.kvlist) {
            v = self.kvlist[k];
            list.push([k, v]);
            keys.push(k);
        }
        list.sort(function(a, b) {
            if (a[0] === b[0]) {
                return 0;
            }
            else if (a[0] < b[0]) {
                return -1;
            }
            else {
                return 1;
            }
        });
        keys.sort();
        if (self.over === 2) {
            lc.spreadOverTwoCols(list, '#'+self.col1id, '#'+self.col2id);
        }
        else {
            lc.spreadOverOneCol(list, '#'+self.col1id, this.type);
        }
        pl('#' + this.wrapperid).show();
    }
});

/* company banner code */
function CompanyBannerClass(tab) {
    this.tab = tab || 'basics';
    this.pages = [ 'basics', 'bmc', 'qa', 'financials', 'media', 'submit' ],
    this.mandatoryprops = [ 'title', 'type', 'platform', 'category', 'address', 'mantra', 'summary', 'logo', 'pic1' ],
    this.proppage = {
        title: 'basics',
        type: 'basics',
        platform: 'basics',
        category: 'basics',
        mantra: 'basics',
        summary: 'basics',
		address: 'basics',
        logo: 'basics',
		pic1: 'basics',
		//contact_email: 'basics',
        answer1: 'bmc',
		answer2: 'bmc',
		answer3: 'bmc',
		answer4: 'bmc',
		answer5: 'bmc',
		answer6: 'bmc',
		answer7: 'bmc',
		answer8: 'bmc',
		answer9: 'bmc',
		answer10: 'bmc',
		answer11: 'qa',
		answer12: 'qa',
		answer13: 'qa',
		answer14: 'qa',
		answer15: 'qa',
		answer16: 'qa',
		answer17: 'qa',
		answer18: 'qa',
        answer19: 'qa',
		answer20: 'qa',
		answer21: 'qa',
		answer22: 'qa',
		answer23: 'qa',
		answer24: 'qa',
		answer25: 'qa',
		answer26: 'qa',
        suggested_amt: 'financials',
        suggested_pct: 'financials',
		founders: 'financials',
        presentation_id: 'financials',
		business_plan_id: 'financials',
		financials_id: 'financials',
		website: 'media',
        video: 'media'
    };
    this.displayNameOverrides = {
        logo: 'LOGO IMAGE',
        pic1: 'UPLOADED IMAGE 1',
        website: 'WEBSITE',
        founders: 'FOUNDERS',
        address: 'LOCATION',
        contact_email: 'EMAIL',
        summary: 'SUMMARY',
        answer1: 'KEY ACTIVITIES',
        answer2: 'KEY RESOURCES',
        answer3: 'KEY PARTNERS',
        answer4: 'VALUE PROPOSITIONS',
        answer5: 'CUSTOMER SEGMENTS',
        answer6: 'CHANNELS',
        answer7: 'CUSTOMER RELATIONSHIPS',
        answer8: 'COST STRUCTURE',
        answer9: 'REVENUE STREAMS',
        answer10: 'PROBLEM',
        answer11: 'SOLUTION',
        answer12: 'FEATURES AND BENEFITS',
        answer13: 'COMPANY STATUS',
        answer14: 'MARKET',
        answer15: 'CUSTOMER',
        answer16: 'COMPETITORS',
        answer17: 'COMPETITIVE COMPARISON',
        answer18: 'BUSINESS MODEL',
        answer19: 'MARKETING PLAN',
        answer20: 'TEAM',
        answer21: 'TEAM VALUES',
        answer22: 'CURRENT FINANCIALS',
        answer23: 'FINANCIAL PROJECTIONS',
        answer24: 'OWNERS',
        answer25: 'INVESTMENT',
        answer26: 'TIMELINE AND WRAPUP',
        suggested_amt: 'AMOUNT',
        suggested_pct: 'PERCENT',
        presentation_id: 'PRESENTATION',
        business_plan_id: 'BUSINESS_PLAN',
        financials: 'FINANCIALS'
    };
};
pl.implement(CompanyBannerClass, {
    store: function(json) {
        var key;
        if (!json) {
            return;
        }
        if (json.listing && json.listing.listing_id) {
            for (key in json.listing) {
                this[key] = json.listing[key];
            }
        }
        this.loggedin_profile = json.loggedin_profile;
        this.loggedin_profile_id = this.loggedin_profile && this.loggedin_profile.profile_id;
    },

    displayMinimal: function(json) {
        this.store(json);
        this.displayBanner();
        pl('.preloadercompanybanner').hide();
        pl('.companybannerwrapper').show();
    },

    display: function(json) {
        this.store(json);
        this.displayBanner();
        this.displayStatusNotification();
        this.displaySubmit();
        this.displayFollow();
        this.displayTabs();
        pl('.preloadercompanybanner').hide();
        pl('.companybannerwrapper').show();
    },

    displayBanner: function() {
        var logobg = this.logo ? 'url(' + this.logo + ') no-repeat scroll center center transparent' : null,
            prefixedurl = this.website ? (this.website.indexOf('http') === 0 ? this.website : ('http://' + this.website)) : null,
            url = prefixedurl ? new URLClass(prefixedurl) : null,
            cat = this.category || '',
            addr = this.brief_address,
            catprefix = !cat || (cat !== 'Other' && !cat.match(/^[aeiou]/i)) ? 'A' : 'An',
            catlink = cat && cat !== 'Other' ? '<a href="/main-page.html?type=category&val=' + encodeURIComponent(cat) + '">' + cat + '</a>' : '',
            type = this.type || 'company',
            platform = this.platform && this.platform !== 'other' ? PlatformClass.prototype.displayName(this.platform) + ' ' : '',
            categorytext = this.platform && this.platform !== 'other' && this.category === 'Software' ? '' : catprefix + ' ' + catlink + ' ',
            platformprefix = categorytext ? '' : (platform.match(/^[aeiou]/i) ? 'An ' : 'A '),
            stagetext = this.stage && this.stage !== 'established' ? this.stage : '',
            typetext = this.type === 'application' ? this.type + ' ' + stagetext : (stagetext || 'company'),
            catlinked = categorytext + platformprefix + platform + typetext,
            locprefix  = type === 'company' ? 'in' : 'from',
            addrlinked = !addr ? '' : ' ' + locprefix + ' <a href="/main-page.html?type=location&val=' + encodeURIComponent(addr) + '">' + addr + '</a>',
            profilelinked = !this.profile_id ? '' : ' by <a href="/profile-page.html?id=' + this.profile_id + '">' + (this.profile_username || 'owner') + '</a>',
            categoryaddresstext = catlinked + addrlinked + profilelinked,
            website = this.website || '/company-page.html?id=' + this.listing_id,
            listingdatetext = CompanyFormatClass.prototype.financeLine(this) + (url ? ' from ' : '');
        if (logobg) {
            pl('#companylogo').removeClass('noimage').css({background: logobg});
        }
        pl('#title').text(this.title || 'Company / App Name');
        if (this.title && this.title.length > 25) {
            pl('#title').addClass('companybannertitlelong');
        }
        pl('title').text('Startupbidder Listing: ' + (this.title || 'Company / App Name'));
        pl('#mantra').text(this.mantra || 'Mantra here');
        pl('#categoryaddresstext').html(categoryaddresstext);
        pl('#listing_date_text').html(listingdatetext);
        if (url) {
            pl('#websitelink').attr({href: url.getURL()});
            pl('#domainname').text(url.getHostname());
            pl('#websitelinkicon').bind('click', function() {
                window.open(website);
            });
        }
        else {
            pl('#domainname').text('');
            pl('#websitelinkicon').hide();
        }
    },

    displayStatusNotification: function() {
        var statusmsg = '';
        if (this.loggedin_profile && this.loggedin_profile_id === this.profile_id) {
            if (this.status === 'new') {
                statusmsg = '<span class="normal">To list publicly, submit to admin for approval</span>';
            }
            else if (this.status === 'posted') {
                statusmsg = '<span class="inprogress">An admin is reviewing your listing for activation</span>';
            }
            else if (this.status === 'withdrawn') {
                statusmsg = '<span class="errorcolor">Your listing is withdrawn and no longer active</span>';
            }
            else if (this.status === 'frozen') {
                statusmsg = '<span class="errorcolor">An admin has frozen your listing pending review</span>';
            }
            /*
            else if (this.status === 'active') {
                statusmsg = '<span class="normal">Your listing is active</span>';
            }
            */
        }
        else {
            if (this.status === 'new') {
                statusmsg = '<span class="normal">To list publicly, submit to admin for approval</span>';
            }
            else if (this.status === 'posted') {
                statusmsg = '<span class="inprogress">An admin is reviewing this listing for activation</span>';
            }
            else if (this.status === 'withdrawn') {
                statusmsg = '<span class="errorcolor">This listing is withdrawn and no longer active</span>';
            }
            else if (this.status === 'frozen') {
                statusmsg = '<span class="errorcolor">An admin has frozen this listing pending review</span>';
            }
            /*
            else if (this.status === 'active') {
                statusmsg = '<span class="normal">This listing is active</span>';
            }
            */
        }
        if (this.status === 'active') {
            pl('#submiterrormsg').hide();
        }
        else {
            if (!this.shouldDisplaySubmit()) {
                pl('#submiterrormsg').addClass('companybannerstatusmsg');
            }
            pl('#submiterrormsg').html(statusmsg);
        }
    },
            
    postListing: function() {
        var self = this,
            completeFunc = function(json) {
                document.location = '/company-page.html?id=' + self.listing_id;
            },
            ajax = new AjaxClass('/listing/post', 'newlistingmsg', completeFunc);
        ajax.setPost();
        ajax.call();
    },

    bindSubmitButton: function() {
        var self = this,
            submitValidator = function() {
                var msg,
                    msgs = [],
                    pctcomplete = self.pctComplete();
                if (pctcomplete !== 100) {
                    msg = self.highlightMissing();
                    msgs.push('Missing info: ' + msg);
                }
                return msgs;
            };
        pl('#submitbutton').bind({
            click: function() {
                var validmsgs = submitValidator();
                if (validmsgs.length > 0) {
                    pl('#submiterrormsg').addClass('errorcolor');
                    pl('#submiterrormsg').html('Please correct: ' + validmsgs.join(' '));
                }
                else {
                    pl('#submiterrormsg').removeClass('errorcolor').addClass('inprogress').text('Submitting listing...');
                    self.postListing();
                }
                return false;
            }
        }).show();
    },

    shouldDisplaySubmit: function() {
        return this.loggedin_profile && this.loggedin_profile.profile_id === this.profile_id && this.status === 'new';
    },

    displaySubmit: function() {
        if (this.shouldDisplaySubmit()) {
            this.bindSubmitButton();
        }
    },

    bindFollow: function() {
        var self = this;
        pl('#followbtn').bind({
            click: function() {
            var following = self.monitored;
            if (following) {
                self.unfollow();
                }
                else {
                    self.follow();
                }
            }
        });
    },

    unfollow: function() {
        var self = this,
            completeFunc = function(json) {
                self.monitored = false;
                self.displayFollow();
            },

            ajax = new AjaxClass('/monitor/deactivate/' + this.listing_id, 'followmsg', completeFunc);
        ajax.setPost();
        ajax.call();
    },

    follow: function() {
        var self = this,
            completeFunc = function(json) {
                self.monitored = true;
                self.displayFollow();
            },

            ajax = new AjaxClass('/monitor/set/' + this.listing_id, 'followmsg', completeFunc);
        ajax.setPost();
        ajax.call();
    },

    displayFollow: function() {
        var following = this.monitored;
        if (this.loggedin_profile && this.loggedin_profile.profile_id !== this.profile_id && this.status === 'active') {
            if (following) {
                this.displayFollowing();
            }
            else {
                this.displayNotFollowing();
            }
            this.bindFollow();
        }
    },

    displayFollowing: function() {
            pl('#followbtn').text('UNFOLLOW');
            pl('#followbtn').show();
    },

    displayNotFollowing: function() {
            pl('#followbtn').text('FOLLOW').show();
    },

    displayTabs: function() {
        var self = this;
        pl('.companynavlink').each(function() {
            var tabid = pl(this).attr('id'),
                tab = tabid.replace(/tab$/,''),
                page,
                url;
            if (tab === 'basics') {
                page = 'company-page.html';
            }
            else if (tab === 'bids' && self.loggedin_profile_id) {
                if (self.loggedin_profile_id === self.profile_id) {
                    page = 'company-owner-bids-page.html';
                }
                else {
                    page = 'company-investor-bids-page.html';
                }
            }
            else if (tab === 'presentation') {
                page = 'company-slides-page.html';
            }
            else {
                page = 'company-' + tab + '-page.html';
            }
            url = '/' + page + '?id=' + self.listing_id;
            pl(this).attr({href: url});
        });
        if (!this.asked_fund) {
            pl('#bidstab').hide();
        }
        if (!MicroListingClass.prototype.getHasValuation(this)) {
            pl('#valuationtab').hide();
        }
        if (!MicroListingClass.prototype.getHasBmc(this)) {
            pl('#modeltab').hide();
        }
        if (!MicroListingClass.prototype.getHasIp(this)) {
            pl('#presentationtab').hide();
        }
        if (this.loggedin_profile && this.loggedin_profile_id !== this.profile_id) {
            pl('#sendmessagelink').attr({href: '/messages-page.html?to_user_id=' + (this.profile_id || '') }).css({display: 'inline'});
        }
        pl('#companynavcontainer').show();
    },

    highlightMissing: function() {
        var msg = '',
            msgs = [],
            errorpages = {},
            missing,
            displayName,
            page,
            i;
        for (i = 0; i < this.missingprops.length; i++) {
            missing = this.missingprops[i];
            page = this.proppage[missing];
            if (!errorpages[page]) {
                errorpages[page] = [];
            }
            displayName = this.displayNameOverrides[missing] || missing.toUpperCase();
            errorpages[page].push(displayName);
        }
        for (i = 0; i < this.pages.length; i++ ) {
            page = this.pages[i];
            //self.highlightPage(page, errorpages[page]);
        }
        for (page in errorpages) {
            //msg = page.toUpperCase() + ' page: ' + errorpages[page].join(', ');
            msg = errorpages[page].join(', ');
            msgs.push(msg);
        }
        return msgs.join('; ');
    },

    pctComplete: function() {
        var numprops = this.mandatoryprops.length,
            filledprops = 0,
            missingprops = [],
            pctcomplete,
            i,
            k;
        for (i = 0; i < numprops; i++) {
            k = this.mandatoryprops[i];
            if (this[k]) {
                filledprops++
            }
            else {
                missingprops.push(k);
            }
        } 
        this.missingprops = missingprops;
        pctcomplete = Math.floor(100 * filledprops / numprops);
        return pctcomplete;
    }

});

function ImagePanelClass(options) {
    this.options = options || {}; // set options.editmode to true for an editable panel
    this.listing = this.options.listing || {};
    this.runningSlideshow = false;
}

pl.implement(ImagePanelClass, {
    setListing: function(listing) {
        this.listing = listing;
        return this;
    },

    enableImage: function(i) {
        var pic = 'pic' + i,
            cachebust = pl('#' + pic + 'nav').hasClass('dotnavempty') ? '' : '?id=' + Math.floor(Math.random()*1000000000),
            url = '/listing/picture/' + this.listing.listing_id + '/' + i + cachebust;
        pl('#' + pic + 'nav').removeClass('dotnavempty');
        pl('#' + pic + ' div').remove();
        pl('#' + pic).removeClass('picblank').css({ 'background-image': 'url(' + url + ')' });
        if (this.options.editmode && pl('#picnum').text() === i) {
            pl('#deleteimagebutton').show();
        }
    },

    enableImageLoading: function(i) {
        var pic = 'pic' + i;
        pl('#' + pic + 'nav').removeClass('dotnavempty');
        pl('#' + pic).addClass('picblank').html('<div class="preloaderfloater"></div><div class="preloadericon"></div>');
    },

    deleteImage: function(i) {
        var self = this,
            pic = 'pic' + i,
            url = '/listing/delete_file?id=' + self.listing.listing_id + '&type=' + 'PIC' + i,
            complete = function() {
                pl('#' + pic).removeClass('picblank').html('');
                pl('#picmsg').removeClass('errorcolor').removeClass('inprogress').addClass('successful').text('Image deleted');
                self.listing[pic] = null;
                self.deleting = false;
            },
            error = function(errornum, json) {
                var msg = json.error_msg || 'Error from server: ' + errornum;
                pl('#' + pic).removeClass('picblank').html('');
                pl('#picmsg').removeClass('successful').removeClass('inprogress').addClass('errorcolor').text(msg);
                self.listing[pic] = null;
                self.deleting = false;
            },
            ajax = new AjaxClass(url, 'picmsg', complete, null, null, error);
        if (self.deleting) {
            return;
        }
        pl('#deleteimagebutton').hide();
        self.deleting = true;
        pl('#' + pic).css('background-image', '');
        pl('#' + pic).addClass('picblank').html('<div class="preloaderfloater"></div><div class="preloadericon"></div>');
        ajax.setPost();
        ajax.call();
    },

    display: function() {
        var self = this,
            firstpic = this.options.editmode ? 'pic1' : undefined,
            numpics = 5,
            slideshowstart = 1,
            pic,
            picval,
            i;
        self.numPics = 0;
        for (i = 1; i <= numpics; i++) {
            pic = 'pic' + i;
            picval = this.listing[pic];
            if (picval) {
                if (picval === 'importing') {
                    self.enableImageLoading(i);
                }
                else {
                    self.enableImage(i);
                }
                if (!firstpic) {
                    firstpic = pic;
                }
                self.numPics++;
            }
            else if (this.options.editmode) {
                pl('#' + pic + 'nav').removeClass('dotnavempty');
                self.numPics++;
            }
        }
        if (!(firstpic && !this.options.editmode && self.numPics <= 1)) {
            pl('.dotnav').unbind().bind('click', function() {
                var ul = pl(this).hasClass('dotnav') ? this : this.parentNode,
                    navid = ul.id,
                    picnum = navid.replace(/pic|nav/g, '');
                self.runningSlideshow = false;
                self.advanceRight(picnum);
            });
            pl('.picslide').unbind().bind('click', function() {
                self.runningSlideshow = false;
                self.advanceRight();
            });
        }
        if (firstpic && !this.options.editmode && self.numPics <= 1) {
            pl('#imagetitle').text('IMAGE');
            pl('.dotnavwrapper').hide();
        }
        else if (firstpic && !this.options.editmode) {
            pl('#' + firstpic + 'nav').addClass('dotnavfilled');
            if (!self.runningSlideshow) {
                self.runningSlideshow = true;
                setTimeout(function(){ self.advanceSlideshow(); }, 5000);
            }
        }
        else { // default highlight first
            pl('#pic1nav').addClass('dotnavfilled');
            if (self.options.editmode && self.listing['pic1']) {
                pl('#deleteimagebutton').show();
            }
        }

        if (pl('#picslideset .picblank').len()) {
            setTimeout(function(){
                var complete = function(json) {
                        var i, pic;
                        for (i = 1; i <= 5; i++) {
                            pic = 'pic' + i;
                            self.listing[pic] = json.listing[pic];
                        }
                        self.display();
                    },
                    ajax = new AjaxClass('/listing/get/' + self.listing.listing_id, 'picmsg', complete);
                ajax.call();
            }, 5000);
        }
    },

    advanceSlideshow: function() {
        var self = this;
        if (self.runningSlideshow) {
            self.advanceRight();
            setTimeout(function() { self.advanceSlideshow() }, 5000);
        }
    },
  
    advanceRight: function(picnum) {    
        var left = 1 * pl('#picslideset').css('left').replace(/px/, ''),
            slidewidth = 1 * pl('#pic1').css('width').replace(/px/, ''),
            fullwidth = slidewidth * this.numPics,
            newleft = picnum ? slidewidth * ( 1 - picnum ) : Math.floor((left - slidewidth) % fullwidth),
            newleftpx = newleft + 'px',
            newpicnum = picnum || (Math.floor(Math.abs(newleft) / slidewidth) + 1),
            onboundary = Math.floor(left % slidewidth) === 0;
        if (onboundary) { // prevent in-transition movements
            if (this.options.editmode) {
                pl('#picnum').text(newpicnum);
                pl('#picuploadfile').attr({name: 'PIC' + newpicnum});
                if (this.listing['pic' + newpicnum]) {
                    pl('#deleteimagebutton').show();
                }
                else {
                    pl('#deleteimagebutton').hide();
                }
            }
            pl('.dotnav').removeClass('dotnavfilled');
            pl('#pic' + newpicnum + 'nav').addClass('dotnavfilled');
            pl('#picslideset').css({left: newleftpx});
        }
    }

});

/* home page */
function HomePageClass() { }

pl.implement(HomePageClass, {

    load: function() {
        var self = this,
            completeFunc = function(json) {
                (new HeaderClass()).setLogin(json);
                self.display(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            ajax = new AjaxClass('/listing/discover_user', 'homepagemsg', completeFunc);
        ajax.call();
    },

    store: function(json) {
        if (json) {
            CollectionsClass.prototype.merge(this, json);
        }
    },
   
    display: function(json) {
        if (json) {
            this.store(json);
        }
        if (this.hasListings()) {
            this.displayListings();
            if (this.loggedin_profile && this.loggedin_profile.edited_listing) {
                this.displayExistingListing();
            }
            pl('#haslistings').show();
        }
        else {
            pl('#nolistings').show();
        }
    },
   
    hasListings: function() {
        var has = false;
        if (this.loggedin_profile) {
            if (this.loggedin_profile.edited_listing) {
                has = true;
            }
            else if (this.active_listings && this.active_listings.length > 0) {
                has = true;
            }
            else if (this.monitored_listings && this.monitored_listings.length > 0) {
                has = true;
            }
        }
        return has;
    }, 

    displayListings: function() {
        var editedListing = new CompanyListClass({ propertykey: 'edited_listing', propertyissingle: true, companydiv: 'edited_listing', fullWidth: true }),
            activeListings = new CompanyListClass({ propertykey: 'active_listings', companydiv: 'active_listings', seeall: '/profile-listing-page.html?type=active', fullWidth: true }),
            monitoredListings = new CompanyListClass({ propertykey: 'monitored_listings', companydiv: 'monitored_listings', seeall: '/profile-listing-page.html?type=monitored', fullWidth: true });
        if (this.edited_listing) {
            pl('#edited_listing_wrapper').show();
            editedListing.storeList(this);
        }
        if (this.active_listings && this.active_listings.length > 0) {
            pl('#active_listings_wrapper').show();
            activeListings.storeList(this);
        }
        if (this.monitored_listings && this.monitored_listings.length > 0) {
           pl('#monitored_listings_wrapper').show();
           monitoredListings.storeList(this);
        }
    },

    displayExistingListing: function() {
        var self = this;
        pl('#editlisting').bind('click', function() {
            var url = self.loggedin_profile && self.loggedin_profile.edited_status === 'new'
                ? '/new-listing-basics-page.html'
                : '/company-page.html?id=' + self.loggedin_profile.edited_listing;
            document.location = url;
        });
        pl('#existinglisting').show();
    }

});

/* info pages */
function InformationPageClass() {}
pl.implement(InformationPageClass,{
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass(),
                    
                    companyList = new CompanyListClass({ colsPerRow: 2});
                header.setLogin(json);
                companyList.storeList(json);
            },
            maxResults = pl('body').hasClass('about-page') ? 12 : ( pl('body').hasClass('help-page') ? 8 : 20),
            basePage = new BaseCompanyListPageClass({ max_results: maxResults });
        basePage.loadPage(completeFunc);
    }
});

/* discover page */
function DiscoverPageClass() {}
pl.implement(DiscoverPageClass,{
    loadPage: function() {
        var self = this,
            complete = function(json) {
                (new HeaderClass()).setLogin(json);
                self.display(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            ajax = new AjaxClass('/listings/discover/', 'top_listings', complete);
        ajax.call();
    },

    store: function(json) {
        if (json) {
            CollectionsClass.prototype.merge(this, json);
        }
    },
 
    display: function(json) {
        if (json) {
            this.store(json);
        }
        this.displayExistingListing();
        this.displayListings();
        this.displayCategories();
        this.displayLocations();
    },

    displayListings: function() {
        var monitoredListings = new CompanyListClass({
                propertykey: 'monitored_listings', companydiv: 'monitored_listings', seeall: '/profile-listing-page.html?type=monitored', fullWidth: true }),
            topListings = new CompanyListClass({
                propertykey: 'top_listings', companydiv: 'top_listings', seeall: '/main-page.html?type=top', exponential: true, fullWidth: true }),
            latestListings = new CompanyListClass({
                propertykey: 'latest_listings', companydiv: 'latest_listings', seeall: '/main-page.html?type=latest', fullWidth: true });
        topListings.storeList(this);
        latestListings.storeList(this);
        if (this.monitored_listings && this.monitored_listings.length > 0) {
           monitoredListings.storeList(this);
           pl('#monitored_listings_wrapper').show();
        }
    },

    displayCategories: function() {
        var categories = this.categories || {},
            categoryList = new BaseListClass(categories, 'category', 1, 'category');
        categoryList.display();
    },

    displayLocations: function() {
        var locations = this.top_locations || {},
            locationList = new BaseListClass(locations, 'location', 1, 'location');
        locationList.display();
    },

    displayExistingListing: function() {
        var self = this;
        if (this.loggedin_profile && this.loggedin_profile.edited_listing) {
            pl('#editlisting').bind('click', function() {
                var url = self.loggedin_profile && self.loggedin_profile.edited_status === 'new'
                    ? '/new-listing-basics-page.html'
                    : '/company-page.html?id=' + self.loggedin_profile.edited_listing;
                document.location = url;
            });
            pl('#existinglisting').show();
        }
    }
 });

/* add listing page */
function AddListingClass() { }

pl.implement(AddListingClass, {

    load: function() {
        var self = this,
            completeFunc = function(json) {
                var header = new HeaderClass();
                header.setLogin(json);
                self.display(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            // ajax = new AjaxClass('/user/loggedin', 'addlistingmsg', completeFunc); // once greg fixes
            ajax = new AjaxClass('/listing/discover_user', 'addlistingmsg', completeFunc);
        ajax.call();
    },

    store: function(json) {
        if (json) {
            CollectionsClass.prototype.merge(this, json);
        }
    },
   
    display: function(json) {
        if (json) {
            this.store(json);
        }
        if (!this.loggedin_profile) {
            this.displayLoggedOut();
        }
        else if (this.loggedin_profile && this.loggedin_profile.edited_listing) {
            this.displayExistingListing();
        }
        else {
            this.displayNewListing();
        }
    },

    displayLoggedOut: function() {
        var nexturl = '/add-listing-page.html',
            login_url = this.login_url,
            twitter_login_url = this.twitter_login_url,
            fb_login_url = this.fb_login_url;
        if (login_url) {
            pl('#google_login').attr({href: login_url + encodeURIComponent(nexturl)});
        } else {
            pl('#google_login').hide();
        }
        if (twitter_login_url) {
            pl('#twitter_login').attr({href: twitter_login_url + '?url=' + encodeURIComponent(nexturl)}).show();
        } else {
            pl('#twitter_login').hide();
        }
        if (fb_login_url) {
            pl('#fb_login').attr({href: fb_login_url + '?url=' + encodeURIComponent(nexturl)}).show();
        } else {
            pl('#fb_login').hide();
        }
        pl('#notloggedin').show();
    },

    displayExistingListing: function() {
        var self = this;
        pl('#editlisting').bind('click', function() {
            var url = this.loggedin_profile && this.loggedin_profile.edited_status === 'new'
                ? '/new-listing-basics-page.html'
                : '/company-page.html?id=' + self.loggedin_profile.edited_listing;
            document.location = url;
        });
        pl('#deletebtn').bind('click', function() {
            var complete = function() {
                    pl('#deletebtn, #deletecancelbtn').hide();
                    pl('#deletemsg').text('Listing deleted, reloading...').show();
                    setTimeout(function() {
                        window.location = '/add-listing-page.html';
                    }, 2000);
                },
                url = '/listing/delete',
                ajax = new AjaxClass(url, 'deletemsg', complete);
            if (pl('#deletecancelbtn').css('display') === 'none') { // first call
                pl('#editblock, #editblock2').hide();
                pl('#deletemsg, #deletecancelbtn').show();
            }
            else {
                ajax.setPost();
                ajax.call();
            }
            return false;
        });
        pl('#deletecancelbtn').bind('click', function() {
            pl('#deletemsg, #deletecancelbtn').hide();
            pl('#editblock, #editblock2').show();
            return false;
        });
        pl('#existinglisting').show();
    },

    displayNewListing: function() {
        pl('#newlisting').show();
    }

});

function MainPageClass() {}
pl.implement(MainPageClass,{
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass(),
                    companyList = new CompanyListClass({ fullWidth: true }),
                    categories = json.categories || {},
                    locations = json.top_locations || {},
                    categoryList = new BaseListClass(categories, 'category', 1, 'category'),
                    locationList = new BaseListClass(locations, 'location', 1, 'location');
                header.setLogin(json);
                categoryList.display();
                locationList.display();
                companyList.storeList(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            basePage = new BaseCompanyListPageClass();
        basePage.loadPage(completeFunc);
    }
});

function ProfileUserClass() {}
ProfileUserClass.prototype.format = function(user_class) {
    return user_class.replace(/[_-]/g, ' ').toUpperCase();
}

/* google analytics tracking code */
window._gaq = [['_setAccount','UA-23942052-1'],['_trackPageview'],['_trackPageLoadTime']];
(function() {
  var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
  ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
  var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
})();

