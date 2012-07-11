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
        var yyyy = yyyymmdd.substr(0,4),
            mm = yyyymmdd.substr(4,2) - 1,
            dd = yyyymmdd.substr(6,2);
        return new Date(yyyy, mm, dd);
    },
    today: function() {
        var today = new Date();
        return DateClass.prototype.formatDate(today);
    },
    now: function() {
        var now = new Date();
        return DateClass.prototype.formatDatetime(now);
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
            numstr = str.replace(/[^0-9]*/g, ''),
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
/*
        pl('#searchbutton').bind({
            click: function() {
                pl('#searchform').get(0).submit();
            }
        });
*/
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
        this.setHeader(profile, json.login_url, json.logout_url);
        searchbox.bindEvents();
    },
    setHeader: function(profile, login_url, logout_url) {
        if (profile) {
            this.setLoggedIn(profile, logout_url);
        }
        else {
            this.setLoggedOut(login_url);
        }
    },
    setLoggedIn: function(profile, logout_url) {
        // var username = profile.username || 'You',
        var username = 'You',
            num_notifications = profile.num_notifications || 0,
            num_messages = profile.num_messages || 0,
            notificationlinktext = num_notifications ? num_notifications + ' unread notifications' : 'no unread notifications',
            newlistingurl = (!profile.edited_listing || profile.edited_status === 'new') ? 'new-listing-basics-page.html' : 'new-listing-submitted-page.html';
        // pl('#topheaderline').html('You have <a href="/notifications-page.html" class="topheaderlink hoverlink">' + notificationlinktext + '</a>');
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
        pl('#postlink').attr('href', newlistingurl);
        if (profile.admin) {
            pl('#adminsetup,#adminhello').css({visibility:'visible'});
        }
        pl('#headerusername').html(username);
        if (logout_url) {
            pl('#logoutlink').attr({href: logout_url});
        }
        pl('#headerloggedin').show();
    },
    setLoggedOut: function(login_url) {
        var post_login_url = login_url + encodeURIComponent('/new-listing-basics-page.html');
        // pl('#topheaderline').html('Want to raise money for startups or invest in one? <a href="/about-page.html" class="topheaderlink hoverlink">We&rsquo;ll tell you how!</a>');
        if (login_url) {
            pl('#postlink').attr({href: post_login_url});
            pl('#loginlink').attr({href: login_url});
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


