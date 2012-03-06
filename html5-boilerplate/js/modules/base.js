
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
    format: function(num) {
        return num ? num.replace(/[^0-9]*/g, '') : '';
    },
    clean: function(str) {
        return NumberClass.prototype.format(str);
    },
    isNumber: function(str) {
        var match = str ? str.match(/^[0-9]*$/) : false;
        return (match ? 0 : 'Please enter a numeric value');
    }
});

function CurrencyClass(symbol) {
    this.symbol = symbol || '$';
}
pl.implement(CurrencyClass, {
    format: function(num) {
        if (!num) {
            return '';
        }
	    var nStr = num + '';
		var x = nStr.split('.');
		var x1 = x[0];
		var x2 = x.length > 1 ? '.' + x[1] : '';
		var rgx = /(\d+)(\d{3})/;
		while (rgx.test(x1)) {
			x1 = x1.replace(rgx, '$1' + ',' + '$2');
		}
		return this.symbol + x1 + x2;
    },
    clean: function(str) {
        if (!str) {
            return '';
        }
        return str.replace(/[^0-9]/g, '');
    },
    isCurrency: function(str) {
        var match = str ? str.match(/^[$]?[0-9]{1,3}(,?[0-9]{3})*$/) : false;
        return (match ? 0 : 'Please enter a currency value');
    }
});

function URLClass(url) {
    this.url = url;
}
pl.implement(URLClass, {
    getHostname: function() {
        if (!this.url) {
            return '';
        }
        var re = new RegExp('^(?:f|ht)tp(?:s)?\://([^/]+)', 'im');
        return this.url.match(re)[1].toString();
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
        pl(self.statusSel).html('');
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
        this.setHeader(profile);
    },
    setHeader: function(profile) {
        if (profile) {
            this.setLoggedIn(profile);
        }
        else {
            this.setLoggedOut();
        }
    },
    setLoggedIn: function(profile) {
        var username = profile.username || 'You',
            listing_id = profile.edited_listing,
            listing_param = listing_id ? '&listing_id=' + listing_id : '',
            posttext = listing_id ? 'Review Submission' : 'Submit New',
            newlistingurl = 'new-listing-basics-page.html?profile_id=' + profile.profile_id + '&username=' + username + listing_param;
        pl('#postlink').attr('href', newlistingurl);
        pl('#posttext').html(posttext);
        pl('#loginlink').attr('href', 'profile-page.html');
        pl('#logintext').html(username);
        pl('#logout').show();
    },
    setLoggedOut: function() {
        pl('#postlink').attr('href', 'login-page.html');
        pl('#posttext').html('Submit New');
        pl('#loginlink').attr('href', 'login-page.html');
        pl('#logintext').html('Login or Sign Up');
        pl('#logout').hide();
    }
});

