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
    format: function(yyyymmdd) {
        return yyyymmdd ? yyyymmdd.substr(0,4) + '-' + yyyymmdd.substr(4,2) + '-' + yyyymmdd.substr(6,2) : '';
    },
    formatDate: function(dateObj) {
        var year = dateObj.getUTCFullYear(),
            month = dateObj.getUTCMonth()+1,
            date = dateObj.getUTCDate();
        return '' + year + (month < 10 ? 0 : '') + month + (date < 10 ? 0 : '') + date;
    },
    today: function() {
        var today = new Date();
        return DateClass.prototype.formatDate(today);
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
        return NumberClass.prototype.formatText(num, '', '%');
    },
    clean: function(num) {
        return NumberClass.prototype.clean(num);
    },
    isPercent: function(str) {
        var match = str ? str.match(/^[1-9]?[0-9][%]?$/) : false;
        return (match ? 0 : 'Please enter a percent value');
    }
});

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
    var self;
    self = this;
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
    this.loadFunc = loadFunc || function() { pl(self.statusSel).html('<span class="inprogress">Loading...</span>'); };
    this.errorFunc = errorFunc || function(errorNum) { pl(self.statusSel).html('<span class="attention">Error from server: ' + errorNum + '</span>'); };
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
    call: function() {
        pl.ajax(this.ajaxOpts);
    }
});
 
function HeaderClass() {}
pl.implement(HeaderClass, {
    setLogin: function(json) {
        var profile = null;
        if (json && json.loggedin_profile) {
            profile = json.loggedin_profile;
        }
        else if (json && json.profile_id) {
            profile = json;
        }
        this.setHeader(profile, json.login_url, json.logout_url);
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
        var username = profile.username || 'You',
            posttext = profile.edited_listing ? 'Your Post': 'Post New',
            newlistingurl = profile.edited_status === 'posted' ? 'new-listing-submitted-page.html' : 'new-listing-basics-page.html';
        pl('#postlink').attr('href', newlistingurl);
        pl('#posttext').html(posttext);
        if (profile.admin) {
            pl('#main>div.header>div.container>div.span-8,#newlistingmain>div.header>div.container>div.span-8').attr({'class': 'span-5 last loginspan'});
            pl('#main>div.header>div.container>div.span-11,#newlistingmain>div.header>div.container>div.span-11').attr({'class': 'span-14'});
            pl('#postlink').before('\
                <a href="main-page.html?type=posted"><span class="headerlink">Posted</span></a><span class="headertext">&nbsp;&nbsp;|&nbsp;&nbsp;</span>\
                <a href="main-page.html?type=frozen"><span class="headerlink">Frozen</span></a><span class="headertext">&nbsp;&nbsp;|&nbsp;&nbsp;</span>\
            ');
        }
        pl('#loginlink').attr('href', 'profile-page.html');
        pl('#logintext').html(username);
        pl('#logout').show();
        if (logout_url) {
            pl('#logout>a').attr({href: logout_url});
        }
    },
    setLoggedOut: function(login_url) {
        if (login_url && pl('body').hasClass('login-page')) {
            pl('#googleloginlink').attr({href: login_url});
        }
        pl('#postlink').attr('href', 'login-page.html');
        pl('#posttext').html('Submit');
        pl('#loginlink').attr('href', 'login-page.html');
        pl('#logintext').html('Sign In');
        pl('#logout').hide();
    }
});

