
function SafeStringClass() {}
pl.implement(SafeStringClass, {
    trim: function(str) {
        return str.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
    },
    htmlEntities: function (str) {
        return str.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
    }
});

function DateClass() {}
pl.implement(DateClass, {
    format: function(yyyymmdd) {
        return yyyymmdd ? yyyymmdd.substr(0,4) + '-' + yyyymmdd.substr(4,2) + '-' + yyyymmdd.substr(6,2) : '';
    }
});

function CurrencyClass(symbol) {
    this.symbol = symbol || '$';
}
pl.implement(CurrencyClass, {
    format: function(num) {
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
        var cleaned;
        cleaned = str.replace(/[^0-9]/g, '');
        return cleaned;
    },
    isCurrency: function(str) {
        if (str.match(/^[$]?[0-9]{1,3}(,?[0-9]{3})*$/)) {
            return 0;
        }
        else {
            return 'Please enter a currency value';
        }
    }
});

function URLClass(url) {
    this.url = url;
}
pl.implement(URLClass, {
    getHostname: function() {
        var re = new RegExp('^(?:f|ht)tp(?:s)?\://([^/]+)', 'im');
        return this.url.match(re)[1].toString();
    }
});

function QueryStringClass() {}
pl.implement(QueryStringClass, {
    load: function() {
        var i, pairs, keyval;
        this.vars = new Array();
        pairs = window.location.search ? window.location.search.substr(1).split( "&" ) : {};
        for (i in pairs) {
            keyval = pairs[ i ].split( "=" );
            this.vars[ keyval[0] ] = keyval[1];
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
        var profile, username;
        if (json) {
            if (json.loggedin_profile) {
                profile = json.loggedin_profile;
            }
            else if (json.profile_id) {
                profile = json;
            }
            else {
                profile = null;
            }
        }
        else {
            profile = null;
        }
        if (profile) {
            username = profile.username || 'You';
            pl('#loginlink').attr('href', 'profile-page.html');
            pl('#logintext').html(username);
            pl('#logout').css({display:'block'});
        }
        else {
            pl('#loginlink').attr('href', 'login-page.html');
            pl('#logintext').html('Login or Sign Up');
            pl('#logout').css({display:'none'});
        }
    }
});

