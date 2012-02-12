pl(function() {

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

    function EmailCheckClass() {}
    pl.implement(EmailCheckClass, {
        emailCheck: function(emailStr) {
            var checkTLD=1;
            var knownDomsPat=/^(com|net|org|edu|int|mil|gov|arpa|biz|aero|name|coop|info|pro|museum)$/;
            var emailPat=/^(.+)@(.+)$/;
            var specialChars="\\(\\)><@,;:\\\\\\\"\\.\\[\\]";
            var validChars="\[^\\s" + specialChars + "\]";
            var quotedUser="(\"[^\"]*\")";
            var ipDomainPat=/^\[(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\]$/;
            var atom=validChars + '+';
            var word="(" + atom + "|" + quotedUser + ")";
            var userPat=new RegExp("^" + word + "(\\." + word + ")*$");
            var domainPat=new RegExp("^" + atom + "(\\." + atom +")*$");
            var matchArray=emailStr.match(emailPat);
            if (matchArray==null) {
                return "Email address seems incorrect (check @ and .'s)";
            }
            var user=matchArray[1];
            var domain=matchArray[2];
            for (i=0; i<user.length; i++) {
                    if (user.charCodeAt(i)>127) {
                            return "This username contains invalid characters.";
                    }
            }
            for (i=0; i<domain.length; i++) {
                    if (domain.charCodeAt(i)>127) {
                            return "This domain name contains invalid characters.";
                    }
            }
            if (user.match(userPat)==null) {
                    return "The username doesn't seem to be valid.";
            }
            var IPArray=domain.match(ipDomainPat);
            if (IPArray!=null) {
                    for (var i=1;i<=4;i++) {
                            if (IPArray[i]>255) {
                                    return "Destination IP address is invalid!";
                            }
                    }
                    return true;
            }
            var atomPat=new RegExp("^" + atom + "$");
            var domArr=domain.split(".");
            var len=domArr.length;
            for (i=0;i<len;i++) {
                    if (domArr[i].search(atomPat)==-1) {
                            return "The domain name does not seem to be valid.";
                    }
            }
            if (checkTLD && domArr[domArr.length-1].length!=2 && 
                            domArr[domArr.length-1].search(knownDomsPat)==-1) {
                    return "The address must end in a well-known domain or two letter country.";
            }
            if (len<2) {
                    return "This address is missing a hostname.";
            }
            return 0;
        }
    });

    function ValidatorClass() {
        this.tests = [];
        this.postValidator = function(result) {};
    }
    pl.implement(ValidatorClass, {
        isNotEmpty: function(str) {
            var trimmedStr;
            trimmedStr = str.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
            if (trimmedStr !== null && trimmedStr.length > 0) {
                return 0;
            }
            else {
                return "Value cannot be empty.";
            }
        },
        isEmail: function(str) {
            var checker;
            checker = new EmailCheckClass();
            return checker.emailCheck(str);
        },
        makePasswordChecker: function(options) {
            return function(pw) {
                var o, property, re, rule, i, matchlen, lower, upper, numbers, qwerty, start, seq;
                o = { /* default options (allows any password) */
                    lower:    0,
                    upper:    0,
                    alpha:    0, /* lower + upper */
                    numeric:  0,
                    special:  0,
                    length:   [0, Infinity],
                    custom:   [ /* regexes and/or functions */ ],
                    badWords: [],
                    badSequenceLength: 0,
                    noQwertySequences: false,
                    noSequential:      false
                };
                for (property in options)
                    o[property] = options[property];
                re = {
                    lower:   /[a-z]/g,
                    upper:   /[A-Z]/g,
                    alpha:   /[A-Z]/gi,
                    numeric: /[0-9]/g,
                    special: /[\W_]/g
                };
                if (pw.length < o.length[0])
                    return "password must be at least " + o.length[0] + " characters";
                if (pw.length > o.length[1])
                    return "password must be no more than " + o.length[1] + " characters";
                for (rule in re) {
                    matchlen = (pw.match(re[rule]) || []).length;
                    if (matchlen < o[rule])
                        return "password must have at least " + o[rule] + " " + rule + " characters";
                }
                for (i = 0; i < o.badWords.length; i++) {
                    if (pw.toLowerCase().indexOf(o.badWords[i].toLowerCase()) > -1)
                        return "password cannot contain the word " + o.badWords[i];
                }
                if (o.noSequential && /([\S\s])\1/.test(pw))
                    return "password cannot contain sequential identical characters";
                if (o.badSequenceLength) {
                    var lower   = "abcdefghijklmnopqrstuvwxyz",
                        upper   = lower.toUpperCase(),
                        numbers = "0123456789",
                        qwerty  = "qwertyuiopasdfghjklzxcvbnm",
                        start   = o.badSequenceLength - 1,
                        seq     = "_" + pw.slice(0, start);
                    for (i = start; i < pw.length; i++) {
                        seq = seq.slice(1) + pw.charAt(i);
                        if (
                            lower.indexOf(seq)   > -1 ||
                            upper.indexOf(seq)   > -1 ||
                            numbers.indexOf(seq) > -1) {
                            return "password cannot have an alphanumeric sequence more than " + o.badSequenceLength + " characters";
                        }
                         if (o.noQwertySequences && qwerty.indexOf(seq) > -1) {
                            return "password cannot have a qwerty sequence more than " + o.badSequenceLength + " characters";
                        }
                    }
                }
                for (i = 0; i < o.custom.length; i++) {
                    rule = o.custom[i];
                    if (rule instanceof RegExp) {
                        if (!rule.test(pw))
                            return "password cannot match the regular expression " + rule;
                    } else if (rule instanceof Function) {
                        if (!rule(pw))
                            return "password failed custom rule " + rule;
                    }
                }
                return 0; // good password
            };
        },
        add: function(test) {
            this.tests.push(test);
        },
        validate: function(str) {
            var i, result;
            result = 0;
            for (i = 0; i < this.tests.length; i++) {
                result = this.tests[i](str);
                if (result !== 0) {
                    break;
                }
            }
            this.postValidator(result);
            return result;
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

    function CompanyTileClass() {}
    pl.implement(CompanyTileClass, {
        setValues: function(json) {
            var date, images, listingdate;
            date = new DateClass();
            images = ['einstein','wave','upgrades','telecom','socialrec','gkleen']; // FIXME
            listingdate = json.listing_date ? date.format(json.listing_date) : 'not posted';
            this.imgClass = images[Math.floor(Math.random()*images.length)];
            this.daysText = json.days_left ? (json.days_left === 0 ? 'closing today!' : (json.days_left < 0 ? 'bidding closed' : json.days_left + ' days left')) : 'closed for bidding';
            this.typeText = Math.floor(Math.random()*2) ? 'INTERNET' : 'SOFTWARE'; // FIXME
            this.votes = json.num_votes || 0;
            this.posted = listingdate;
            this.name = json.title || 'Listed Company';
            this.loc = Math.floor(Math.random()*2) ? 'London, UK' : 'San Jose, CA, USA'; // FIXME
            this.details = json.mantra || json.summary || 'Company details not provided';
            this.url = '/company-page.html?id=' + json.listing_id;
        },
        makeHtml: function(lastClass) {
            var html;
            html = '\
<span class="span-4 '+ (lastClass?lastClass:'') +'">\
<div class="tile">\
    <a href="' + this.url + '"><div class="tileimg ' + this.imgClass + '"></div></a>\
    <div class="tiledays"></div>\
    <div class="tiledaystext">' + this.daysText + '</div>\
    <div class="tiletype"></div>\
    <div class="tiletypetext">' + this.typeText + '</div>\
    <div class="tilepoints"></div>\
    <div class="tilepointstext">\
        <div class="tilevotes">' + this.votes + '</div>\
        <div class="thumbup tilevoteimg"></div>\
        <div class="tileposted">' + this.posted + '</div>\
    </div>\
    <a href="' + this.url + '">\
    <p class="tiledesc">\
        <span class="tilecompany">' + this.name + '</span><br/>\
        <span class="tileloc">' + this.loc + '</span><br/>\
        <span class="tiledetails">' + this.details + '</span>\
    </p>\
    </a>\
</div>\
</span>\
';
            return html;
        }
    });

    function CompanyListClass() {}
    pl.implement(CompanyListClass, {
        storeList: function(json, colsPerRow, companydiv, companykey) {
            var companysel, companies, html;
            colsPerRow = colsPerRow || 4;
            companydiv = companydiv || 'companydiv';
            companykey = companykey || 'listings';
            companysel = '#' + companydiv;
            companies = json && json[companykey] && json[companykey].length > 0 ? json[companykey] : null;
            html = "";
            if (!companies) {
                pl(companysel).html('<span class="attention">No companies found</span>');
                return;
            }
            companies.reverse(); // prevel has a bug, it does each in reverse order
            pl.each(companies, function(i, company) {
                var tile, last, reali;
                tile  = new CompanyTileClass();
                tile.setValues(company);
                reali = companies.length - (i+1);
                last = (reali+1)%colsPerRow === 0 ? 'last' : '';
                html += tile.makeHtml(last);
            });
            pl(companysel).html(html);
        }
    });

    function ListClass() {}
    pl.implement(ListClass, {
        ucFirst: function(str) {
            return str.length > 1 ? str.substr(0,1).toUpperCase() + str.substr(1) : str.toUpperCase();
        },
        spreadOverTwoCols: function(list, divcol1, divcol2) {
            var htmlCol1, htmlCol2, mid, i, item;
            mid = Math.floor(list.length / 2);
            htmlCol1 = '';
            for (i = 0; i < mid; i++) {
                item = this.ucFirst(list[i]);
                htmlCol1 += '<li>'+item+'</li>';
            }
            pl(divcol1).html(htmlCol1);
            htmlCol2 = '';
            for (i = mid; i < list.length; i++) {
                item = this.ucFirst(list[i]);
                htmlCol2 += '<li>'+item+'</li>';
            }
            pl(divcol2).html(htmlCol2);
        }
    });

    function CategoryListClass() {}
    pl.implement(CategoryListClass, {
        storeList: function(json) {
            var categories, lc;
            categories = json && json.categories && json.categories.length > 0
                ? json.categories
                : ['biotech','chemical','electronics','energy','environmental','financial','hardware','healthcare','industrial',
                    'internet','manufacturing','media','medical','pharma','retail','software','telecom','other'];
            if (!categories) {
                pl('#categorydivcol1').html('<li class="notice">No categories found</li>');
                pl('#categorydivcol2').html('');
                return;
            }
            categories.sort();
            lc = new ListClass();
            lc.spreadOverTwoCols(categories, '#categorydivcol1', '#categorydivcol2');
        }
    });

    function LocationListClass() {}
    pl.implement(LocationListClass, {
        storeList: function(json) {
            var locations, lc;
            locations = json && json.locations && json.locations.length > 0
                ? json.locations
                : ['Austin, USA', 'Bangalore, India', 'Beijing, China', 'Berlin, Germany', 'Cambridge, USA', 'Dusseldorf, Germany', 'Herzliya, Israel',
                    'Katowice, Poland', 'London, UK', 'Menlo Park, USA', 'Mountain View, CA', 'New York City, USA', 'Seattle, WA', 'Cambridge, UK'];
            if (!locations) {
                pl('#locationdivcol1').html('<li class="notice">No locations found</li>');
                pl('#locationdivcol2').html('');
                return;
            }
            locations.sort();
            lc = new ListClass();
            lc.spreadOverTwoCols(locations, '#locationdivcol1', '#locationdivcol2');
        }
    });

    function NotifyListClass() {}
    pl.implement(NotifyListClass, {
        storeList: function(json) {
            var notifications, html, i, notification;
            notifications = json && json.notifications && json.notifications.length > 0
                ? json.notifications
                : [];
            if (notifications.length === 0) {
                notification = {};
                notification.url = null;
                notification.type = 'comment';
                notification.text = 'You currently have no notifications';
                notification.date = '';
                notifications[0] = notification;
            }
            html = '';
            for (i = 0; i < notifications.length; i++) {
                notification = notifications[i];
                html += '\
        '+(notification.url ? '<a href="' + notification.url + '">' : '') + '\
            <div class="sideboxnotify sideboxlink">\
                <span class="sideboxicon">\
                    <div class="'+notification.type+'icon"></div>\
                </span>\
                <span class="sideboxnotifytext">\
                    '+notification.text+'\
                    <br/>\
                    <span class="sideboxdate">'+notification.date+'</span>\
                </span>\
            </div>\
        '+(notification.url ? '</a>' : '');
            }
            pl('#notifylist').html(html);
        }
    });

    function BaseCompanyListPageClass() {};
    pl.implement(BaseCompanyListPageClass,{
        setListingsType: function(type) {
            this.type = type;
        },
        getListingsType: function() {
            if (!this.type) {
                if (!this.queryString) {
                    this.queryString = new QueryStringClass();
                    this.queryString.load();
                }
                this.type = this.queryString.vars.type || 'top';
            }
            return this.type;
        },
        getListingsUrl: function(type, listing_id) {
            if (type === 'related') { // FIXME: related unimplemented
                // type = 'related?id='+listing_id
                type = 'top';
            }
            var url = '/listings/' + type;
            return url;
        },
        storeListingsTitle: function(type) {
            var title = type.toUpperCase() + ' COMPANIES';
            pl('#listingstitle').html(title);
        },
        loadPage: function(completeFunc) {
            var type, url, ajax;
            type = this.getListingsType();
            this.storeListingsTitle(type);
            url = this.getListingsUrl(type);
            ajax = new AjaxClass(url, 'companydiv', completeFunc);
            ajax.call();
        }
    });

    function RelatedCompaniesClass(listing_id) {
        this.listing_id = listing_id;
    }
    pl.implement(RelatedCompaniesClass, {
        load: function() {
            var completeFunc, basePage;
            completeFunc = function(json) {
                companyList = new CompanyListClass();
                companyList.storeList(json,2);
            };
            basePage = new BaseCompanyListPageClass();
            basePage.setListingsType('related', this.listing_id);
            basePage.loadPage(completeFunc);
        }
    });

    function MainPageClass() {};
    pl.implement(MainPageClass,{
        loadPage: function() {
            var completeFunc, basePage;
            completeFunc = function(json) {
                var header, companyList, categoryList, locationList;
                header = new HeaderClass();
                companyList = new CompanyListClass();
                categoryList = new CategoryListClass();
                locationList = new LocationListClass();
                header.setLogin(json);
                companyList.storeList(json,4);
                categoryList.storeList(json);
                locationList.storeList(json);
            };
            basePage = new BaseCompanyListPageClass();
            basePage.loadPage(completeFunc);
        }
    });

    function InformationPageClass() {}
    pl.implement(InformationPageClass,{
        loadPage: function() {
            var completeFunc, basePage;
            completeFunc = function(json) {
                var header, companyList;
                header = new HeaderClass();
                companyList = new CompanyListClass();
                header.setLogin(json);
                companyList.storeList(json,2);
            };
            basePage = new BaseCompanyListPageClass();
            basePage.loadPage(completeFunc);
        }
    });

    function ProfileClass() {}
    pl.implement(ProfileClass, {
        setProfile: function(json) {
            var date, joindate, investor;
            date = new DateClass();
            joindate = json.joined_date ? date.format(json.joined_date) : 'unknown';
            investor = json.investor ? 'Accredited Investor' : 'Entrepreneur';
            pl('#profilestatus').html('');
            pl('#name').html(json.name || 'Anonymous');
            pl('#title').html(json.title);
            pl('#organization').html(json.organization);
            pl('#email').html(json.email);
            pl('#phone').html(json.phone || '');
            pl('#address').html(json.address || '');
            pl('#joineddate').html(joindate);
            pl('#investor').html(investor);
            pl('#notifyenabled').html(json.notifyenabled ? 'enabled' : 'disabled');
            pl('#mylistingscount').html(json.posted ? json.posted.length : 0);
            pl('#biddedoncount').html(json.bidon ? json.bidon.length : 0);
            pl('#upvotedcount').html(json.upvoted ? json.upvoted.length : 0);
        }
    });

    function UserMessageClass(id) {
        this.id = id;
        this.sel = '#' + id;
        this.isClear = false;
    }
    pl.implement(UserMessageClass, {
        show: function(cssClass, text) {
            this.isClear = false;
            pl(this.sel).html('').removeClass('attention').removeClass('inprogress').removeClass('successful').addClass(cssClass).html(text);
        },
        clear: function() {
            this.isClear = true;
            pl(this.sel).html('').removeClass('attention').removeClass('inprogress').removeClass('successful');
        }
    });

    function ValidIconClass(id) {
        this.id = id;
        this.sel = '#' + id;
        this.isValid = false;
    }
    pl.implement(ValidIconClass, {
        showValid: function() {
            this.isValid = true;
            this.clearClasses().addClass('checkboxgreenicon');
        },
        showInvalid: function() {
            this.isValid = false;
            this.clearClasses().addClass('checkboxredicon');
        },
        clear: function() {
            this.isValid = false;
            this.clearClasses();
        },
        clearClasses: function() {
            return pl(this.sel).removeClass('checkboxgreenicon').removeClass('checkboxredicon');
        }
    });

    function FieldBaseClass(id, value, updateFunction, msgId) {
        this.id = id;
        this.value = value !== null ? value : '';
        this.updateFunction = updateFunction;
        this.sel = '#' + id;
        this.validator = new ValidatorClass();
        this.msg = new UserMessageClass(msgId);
    }
    pl.implement(FieldBaseClass, {
        addValidator: function(validatorFunc) {
            this.validator.add(validatorFunc);
        },
        getLoadFunc: function() {
            var self = this;
            return function() {
                self.msg.show('inprogress', 'Saving changes to server...');
            };
        },
        getErrorFunc: function(displayFunc) {
            var self = this;
            return function(errorNum) {
                displayFunc();
                self.msg.show('attention', 'Error saving changes to server: ' + errorNum);
            };
        },
        getSuccessFunc: function() {
            var self, newval;
            self = this;
            newval = self.value;
            return function() {
                self.msg.show('successful', 'Saved changes to server');
                self.value = newval;
            };
        },
        disable: function() {
            pl(this.sel).attr({disabled: 'disabled'});
        },
        enable: function() {
            pl(this.sel).removeAttr('disabled');
        }
    });

    function CheckboxFieldClass(id, value, updateFunction, msgId) {
        this.fieldBase = new FieldBaseClass(id, value, updateFunction, msgId);
        this.getDisplayFunc()();
    }
    pl.implement(CheckboxFieldClass, {
        getDisplayFunc: function() {
            var self = this;
            return function() {
                if (self.fieldBase.value) {
                    pl(self.fieldBase.sel).attr({checked: 'checked'});
                }
                else {
                    pl(self.fieldBase.sel).removeAttr('checked');
                }
            };
        },
        bindEvents: function() {
            var self, onchange;
            self = this;
            onchange = function() {
                var newval, changeKey;
                changeKey = self.fieldBaseid;
                newval = pl(self.fieldBase.sel).attr('checked');
                self.fieldBase.updateFunction({ changeKey: newval }, self.fieldBase.getLoadFunc(), self.fieldBase.getErrorFunc(self.getDisplayFunc()), self.fieldBase.getSuccessFunc());
            }
            pl(self.fieldBase.sel).bind({
                change: onchange
            });
        }
    });

    function TextFieldClass(id, value, updateFunction, msgId) {
        this.fieldBase = new FieldBaseClass(id, value, updateFunction, msgId);
        this.getDisplayFunc()();
    }
    pl.implement(TextFieldClass, {
        getDisplayFunc: function() {
            var self = this;
            return function() {
                pl(self.fieldBase.sel).attr({value: self.fieldBase.value});
            };
        },
        bindEvents: function() {
            var self, icon, safeStr, onchange, onfocus, onblur;
            self = this;
            icon = new ValidIconClass(self.fieldBase.id + 'icon');
            safeStr = new SafeStringClass();
            onchange = function() {
                var newval, validMsg;
                newval = safeStr.htmlEntities(pl(self.fieldBase.sel).attr('value'));
                validMsg = self.fieldBase.validator.validate(newval);
                if (validMsg !== 0) {
                    self.fieldBase.msg.show('attention', validMsg);
                    icon.showInvalid();
                    return;
                }
                if (!self.fieldBase.msg.isClear) {
                    self.fieldBase.msg.clear();
                }
                if (!icon.isValid) {
                    icon.showValid();
                }
            };
            onfocus = function() {
                self.value = pl(self.fieldBase.sel).attr('value'); // save the value
            };
            onblur = function(event) { // push to server
                var changeKey, newval, validMsg;
                changeKey = self.fieldBase.id;
                newval = safeStr.htmlEntities(pl(self.fieldBase.sel).attr('value'));
                validMsg = self.fieldBase.validator.validate(newval);
                if (validMsg !== 0) {
                    pl(self.fieldBase.sel).attr('value', self.value); // restore old name
                    self.fieldBase.msg.clear();
                    icon.clear();
                    return;
                }
                icon.clear();
                if (self.value === newval) {
                    //self.fieldBase.msg.clear();
                    return;
                }
                self.fieldBase.updateFunction({ changeKey: newval }, self.fieldBase.getLoadFunc(), self.fieldBase.getErrorFunc(self.getDisplayFunc()), self.fieldBase.getSuccessFunc());
            };
            pl(self.fieldBase.sel).bind({
                blur: onblur,
                focus: onfocus,
                change: onchange,
                keyup: onchange
            });
        }
    });

    function EditProfileClass() {}
    pl.implement(EditProfileClass, {
        deactivateUser: function() {
            var ajax;
            ajax = {
                async: true,
                url: this.deactivateUrl,
                type: 'POST',
                data: {},
                dataType: 'json',
                charset: 'utf-8',
                load: function(){ pl('#deactivatemsg').html('DEACTIVATING...'); },
                error: function() { pl('#deactivatemsg').html('UNABLE TO DEACTIVATE'); pl('#deactivatebutton').html('DEACTIVATE'); },
                success: function() {
                    pl('#deactivatemsg').html('DEACTIVATED, GOING HOME...');
                    pl('#deactivatebutton').hide();
                    setTimeout(function(){window.location='/';}, 4000);
                }
            };
            pl.ajax(ajax);
        },
        getUpdater: function() {
            var self = this;
            return function(newdata, loadFunc, errorFunc, successFunc) {
                var data, field, ajax;
                data = { profile: {
                    profile_id: self.profile_id,
                    username: self.username,
                    stauts: self.status,
                    open_id: self.open_id,
                    name: pl('#name').attr('value'),
                    email: pl('#email').attr('value'),
                    title: pl('#title').attr('value'),
                    organization: pl('#organization').attr('value'),
                    investor: pl('#investor').attr('value') ? 'true' : 'false',
                    facebook:'',
                    twitter:'',
                    linkedin:'',
                } };
                for (field in newdata) {
                    data.profile[field] = newdata[field];
                }
                ajax = new AjaxClass(self.updateUrl, '', null, successFunc, loadFunc, errorFunc);
                ajax.setPostData(data);
                ajax.call();
            };
        },
        setProfile: function(json) {
            var self, properties, updateUrl, i, property, textFields, textFieldId, textFieldObj,
                investorCheckbox, notifyCheckbox, newPassword, passwordOptions, confirmPassword;
            self = this;
            properties = ['profile_id', 'status', 'name', 'username', 'open_id', 'profilestatus', 'title', 'organization', 'email', 'phone', 'address'];
            textFields = ['email', 'name', 'title', 'organization', 'phone', 'address'];
            this.profile_id = json.profile_id;
            this.updateUrl = '/user/update?id=' + this.profile_id;
            this.deactivateUrl = '/user/deactivate?id=' + this.profile_id;
            for (i = 0; i < properties.length; i++) {
                property = properties[i];
                this[property] = json[property];
            }
            for (i = 0; i < textFields.length; i++) {
                textFieldId = textFields[i];
                textFieldObj = new TextFieldClass(textFieldId, json[textFieldId], this.getUpdater(), 'personalinfomsg');
                textFieldObj.fieldBase.addValidator(textFieldObj.fieldBase.validator.isNotEmpty);
                if (textFieldId === 'email') {
                    textFieldObj.fieldBase.addValidator(textFieldObj.fieldBase.validator.isEmail);
                }
                textFieldObj.bindEvents();
            }
            pl('#profilestatus').html('');
            investorCheckbox = new CheckboxFieldClass('investor', json.investor, this.getUpdater(), 'settingsmsg');
            investorCheckbox.bindEvents();
            notifyCheckbox = new CheckboxFieldClass('notifyenabled', json.notifyenabled, this.getUpdater(), 'settingsmsg');
            notifyCheckbox.bindEvents();
            newPassword = new TextFieldClass('newpassword', '', function(){}, 'passwordmsg');
            passwordOptions = {
                length: [8, 32],
                badWords: ['password', this.name, this.username, this.email, (this.email&&this.email.indexOf('@')>0?this.email.split('@')[0]:'')],
                badSequenceLength: 3
            };
            newPassword.fieldBase.addValidator(newPassword.fieldBase.validator.makePasswordChecker(passwordOptions));
            newPassword.fieldBase.validator.postValidator = function(result) {
                if (result === 0) {
                    pl('#confirmpassword').removeAttr('disabled');
                }
                else {
                    pl('#confirmpassword').attr({disabled: 'disabled'});
                }
            };
            newPassword.bindEvents();
            confirmPassword = new TextFieldClass('confirmpassword', '', this.getUpdater(), 'passwordmsg');
            confirmPassword.fieldBase.addValidator(function(val) {
                if (pl('#newpassword').attr('value') === val) {
                    return 0;
                }
                else {
                    return "confirm must match new password";
                }
            });
            confirmPassword.bindEvents();
            pl('#deactivatebutton').bind({click: function(){
                if (pl('#deactivatemsg').html() === '') {
                    pl('#deactivatemsg').html('ARE YOU SURE?');
                    pl('#deactivatebutton').html('YES, DEACTIVATE');
                }
                else {
                    self.deactivateUser();
                }
                return false;
            }});
        }
    });

    function TestCompaniesClass() {
            var randomLen, i;
            this.randomSort = function (a,b) {
                var temp = parseInt( Math.random()*10 );
                var isOddOrEven = temp%2;
                var isPosOrNeg = temp>5 ? 1 : -1;
                return( isOddOrEven*isPosOrNeg );
            };
            this.allCompanies = [{"num":1,"listing_id":"ag1zdGFydHVwYmlkZGVychcLEgdMaXN0aW5nIgpraWxsIGVtYWlsDA","title":"Kill email","suggested_amt":20000,"suggested_pct":50,"suggested_val":40000,"previous_val":40000,"valuation":40000,"median_valuation":75000,"score":3316,"listing_date":"20111018","closing_date":"20111117","status":"active","mantra":"Making email as obsolete as the memo.","summary":"If any startup says it's going to eliminate email, it's destined for failure. You can iterate on the inbox, and try to improve it, but even that's not much of a business. The latest high profile flop in this arena is Google Wave. It was supposed to change email forever. It was going to displace email. Didn't happen.","profile_id":"ag1zdGFydHVwYmlkZGVychkLEgRVc2VyIg9idXNpbmVzc2luc2lkZXIM","profile_username":"Insider","num_comments":38,"num_bids":15,"num_votes":4,"votable":true,"days_ago":95,"days_left":-66,"mockData":true,"business_plan_id":null,"presentation_id":null,"financials_id":null},{"num":2,"listing_id":"ag1zdGFydHVwYmlkZGVychsLEgdMaXN0aW5nIg5sb2NhbG5ld3NzaXRlcww","title":"Local news sites","suggested_amt":9800,"suggested_pct":20,"suggested_val":49000,"previous_val":365217,"valuation":365217,"median_valuation":77000,"score":1326,"listing_date":"20111011","closing_date":"20111110","status":"active","mantra":"Local news made global","summary":"Maybe Tim Armstrong, AOL, and Patch will prove it wrong, but to this point nobody has been able to crack the local news market and make a sustainable business.In theory creating a network of local news sites that people care about is a good idea. You build a community, there's a baked in advertising group with local businesses, and classifieds. But, it appears to be too niche to scale into a big business.","profile_id":"ag1zdGFydHVwYmlkZGVychQLEgRVc2VyIgpkcmFnb25zZGVuDA","profile_username":"The Dragon","num_comments":22,"num_bids":19,"num_votes":11,"votable":false,"days_ago":102,"days_left":-73,"mockData":true,"business_plan_id":null,"presentation_id":null,"financials_id":null},{"num":3,"listing_id":"ag1zdGFydHVwYmlkZGVyciMLEgdMaXN0aW5nIhZjb21wX3VwZ3JhZGluZ19zZXJ2aWNlDA","title":"Computer Upgrading Service","suggested_amt":11550,"suggested_pct":33,"suggested_val":35000,"previous_val":35000,"valuation":35000,"median_valuation":63000,"score":899,"listing_date":"20111009","closing_date":"20111108","status":"active","mantra":"Taking the pain out of managing your technology","summary":"Starting a business that specializes in upgrading existing computer systems with new internal and external equipment is a terrific homebased business to initiate that has great potential to earn an outstanding income for the operator of the business. A computer upgrading service is a very easy business to get rolling, providing you have the skills and equipment necessary to complete upgrading tasks, such as installing more memory into the hard drive, replacing a hard drive, or adding a new disk drive to the computer system. Ideally, to secure the most profitable segment of the potential market, the service should specialize in upgrading business computers as there are many reasons why a business would upgrade a computer system as opposed to replacing the computer system. Additionally, managing the business from a homebased location while providing clients with a mobile service is the best way to keep operating overheads minimized and potentially increases the size of the target market by expanding the service area, due to the fact the business operates on a mobile format.","profile_id":"ag1zdGFydHVwYmlkZGVychkLEgRVc2VyIg9ncnplZ29yem5pdHRuZXIM","profile_username":"Greg","num_comments":34,"num_bids":27,"num_votes":11,"votable":false,"days_ago":104,"days_left":-75,"mockData":true,"business_plan_id":null,"presentation_id":null,"financials_id":null},{"num":4,"listing_id":"ag1zdGFydHVwYmlkZGVychsLEgdMaXN0aW5nIg5zZW1hbnRpY3NlYXJjaAw","title":"Semantic Search","suggested_amt":18000,"suggested_pct":45,"suggested_val":40000,"previous_val":40000,"valuation":40000,"median_valuation":61500,"score":639,"listing_date":"20111005","closing_date":"20111104","status":"active","mantra":"Adding meaning to your search experience","summary":"The fact of the matter is Google, and to a much lesser extent Bing, own the search market. Ask Barry Diller, if you don't believe us.Yet, startups still spring up hoping to disrupt the incumbents. Cuil flopped. Wolfram Alpha is irrelevant. Powerset, which was a semantic search engine was bailed out by Microsoft, which acquired it.","profile_id":"ag1zdGFydHVwYmlkZGVychILEgRVc2VyIghqcGZvd2xlcgw","profile_username":"fowler","num_comments":2,"num_bids":24,"num_votes":4,"votable":false,"days_ago":108,"days_left":-79,"mockData":true,"business_plan_id":null,"presentation_id":null,"financials_id":null},{"num":5,"listing_id":"ag1zdGFydHVwYmlkZGVyciILEgdMaXN0aW5nIhVzb2NpYWxyZWNvbW1lbmRhdGlvbnMM","title":"Social recommendations","suggested_amt":1500,"suggested_pct":10,"suggested_val":15000,"previous_val":15000,"valuation":15000,"median_valuation":42500,"score":412,"listing_date":"20111004","closing_date":"20111103","status":"active","mantra":"Purchasing for status in your peer group","summary":"It's a very tempting idea. Collect data from people about their tastes and preferences. Then use that data to create recommendations for others. Or, use that data to create recommendations for the people that filled in the information. It doesn't work. The latest to try is Hunch and Get Glue.Hunch is pivoting towards non-consumer-facing white label business. Get Glue has had some success of late, but it's hardly a breakout business.","profile_id":"ag1zdGFydHVwYmlkZGVychELEgRVc2VyIgdjaGluZXNlDA","profile_username":"The One","num_comments":42,"num_bids":20,"num_votes":3,"votable":false,"days_ago":109,"days_left":-80,"mockData":true,"business_plan_id":null,"presentation_id":null,"financials_id":null}];
            this.allCompanies.sort(this.randomSort);
            randomLen = Math.floor(Math.random() * this.allCompanies.length);
            this.companies = [];
            for (i = 0; i < randomLen; i++) {
                this.companies[i] = this.allCompanies[i];
            }
    }
    pl.implement(TestCompaniesClass, {
        testJson: function() {
            return this.companies;
        },
        testNotifications: function() {
            var types, dates, i, company, notifications, notification;
            types = ['comment', 'bid'];
            dates = ['2011/11/29 13:12', '2011/12/20 9:46', '2012/01/13 8:37'];
            notifications = [];
            for (i = 0; i < this.companies.length; i++) {
                company = this.companies[i];
                notification = {};
                notification.url = '/company-page.html?id=' + company.listing_id;
                notification.type = types[Math.floor(Math.random() * types.length)]
                notification.text =  'You received a ' + notification.type + ' on ' + company.title;
                notification.date = dates[Math.floor(Math.random() * dates.length)]
                notifications[i] = notification;
            }
            return notifications;
        }
    });

    function ProfilePageClass() {};
    pl.implement(ProfilePageClass,{
        loadPage: function() {
            var completeFunc, ajax;
            completeFunc = function(json) {
                var header, profile, notifyList, companyList, testCompanies;
                header = new HeaderClass();
                profile = new ProfileClass();
                notifyList = new NotifyListClass();
                companyList = new CompanyListClass();
                testCompanies = new TestCompaniesClass(); // FIXME
                if (!json.posted) { // FIXME
                    json.posted = testCompanies.testJson()
                }
                if (!json.bidon) { // FIXME
                    json.bidon = testCompanies.testJson()
                }
                if (!json.upvoted) { // FIXME
                    json.upvoted = testCompanies.testJson()
                }
                if (!json.notifications || !json.notifications.length > 0) {
                    json.notifications = testCompanies.testNotifications();
                }
                header.setLogin(json);
                profile.setProfile(json);
                notifyList.storeList(json);
                companyList.storeList(json, 4, 'posteddiv', 'posted');
                companyList.storeList(json, 4, 'bidondiv', 'bidon');
                companyList.storeList(json, 4, 'upvoteddiv', 'upvoted');
            };
            ajax = new AjaxClass('/user/loggedin', 'profilestatus', completeFunc);
            ajax.call();
        }
    });

    function EditProfilePageClass() {}
    pl.implement(EditProfilePageClass,{
        loadPage: function() {
            var successFunc, ajax;
            successFunc = function(json) {
                var header, profile;
                if (!json) {
                    pl('#profilestatus').html('<span class="notice">Error: null response from server</span>');
                    pl('#profilecolumn').hide();
                    return;
                }
                header = new HeaderClass();
                editProfile = new EditProfileClass();
                header.setLogin(json);
                editProfile.setProfile(json);
            };
            ajax = new AjaxClass('/user/loggedin', 'profilestatus', null, successFunc);
            ajax.call();
        }
    });

    function ListingClass(id) {
        var self = this;
        this.id = id;
        this.url = '/listings/get/' + this.id;
        this.statusId = 'listingstatus';
        this.completeFunc = function(json) {
            var header, listing;
            header = new HeaderClass();
            header.setLogin(json);
            self.store(json);
            self.display();
        };
        this.ajax = new AjaxClass(this.url, this.statusId, this.completeFunc);
    };
    pl.implement(ListingClass, {
        store: function(json) {
            var key;
            if (json && json.listing && json.listing.listing_id) {
                for (key in json.listing) {
                    this[key] = json.listing[key];
                }
            }
            this.dateobj = new DateClass();
            this.currency = new CurrencyClass();
            this.testcompanies = new TestCompaniesClass();
            this.testcompany = this.testcompanies.allCompanies[0]; // FIXME
        },
        load: function() {
            this.ajax.call();
        },
        display: function() {
            this.displayBasics();
            this.displayInfobox();
            this.displayMap();
            this.displayDocuments();
            this.displayFunding();
            this.displaySocial();
            this.displayWithdraw();
        },
        displayBasics: function() {
            this.mantra = this.mantra || this.testcompany.mantra; // FIXME
            this.videourl = this.videourl || this.testcompany.videourl || 'http://www.youtube.com/embed/QoAOzMTLP5s'; // FIXME
            this.logourl = this.logourl || '/img/einstein.jpg';
            pl('#companylogo').attr({src: this.logourl});
            pl('#title').html(this.title);
            pl('title').html('Startupbidder Listing: ' + this.title);
            pl('#profile_username').html(this.profile_username);
            pl('#mantra').html(this.mantra);
            pl('#companystatus').html('Listing is ' + this.status);
            if (this.status === 'withdrawn') {
                pl('#companystatus').addClass('attention');
            }
            pl('#num_votes').html(this.num_votes);
            pl('#num_comments').html(this.num_comments);
            pl('#videopresentation').attr({src: this.videourl});
            pl('#summary').html(this.summary);
            pl('#listingdata').show();
        },
        displayInfobox: function() {
            var legalTypes, url;
            legalTypes = [ 'C Corp', 'S Corp', 'LLC', 'Proprietorship', 'Partnership', 'Limited', 'PLC', 'GmbH', 'SA', 'SRL', 'KK' ];
            this.category = this.category || (Math.floor(Math.random()*2) ? 'INTERNET' : 'SOFTWARE'); // FIXME
            this.legaltype = this.legaltype || legalTypes[Math.floor(Math.random() * legalTypes.length)];
            this.websiteurl = this.websiteurl || 'http://wave.google.com'; // FIXME
            url = new URLClass(this.websiteurl);
            this.domainname = url.getHostname();
            pl('#category').html(this.category);
            pl('#legaltype').html(this.legaltype);
            pl('#listing_date').html(this.listing_date ? this.dateobj.format(this.listing_date) : 'not posted');
            pl('#websitelink').attr({href: this.websiteurl});
            pl('#domainname').html(this.domainname);
        },
        displayMap: function() {
            this.address = this.address || Math.floor(Math.random()*2) ? '221B Baker St, London, UK' : '170W Tasman Dr, San Jose, CA, USA'; // FIXME
            //this.addressurl = this.addressurl || 'http://nominatim.openstreetmap.org/search?q=' + encodeURIComponent(this.address);
            this.addressurl = 'http://maps.google.com/maps?q=' + encodeURI(this.title) + ',' + encodeURI(this.address);
            this.latitude = this.latitude || '51.499117116569'; // FIXME
            this.longitude = this.longitude || '-0.12359619140625'; // FIXME
            //this.mapurl = 'http://ojw.dev.openstreetmap.org/StaticMap/?lat=' + this.latitude + '&lon=' + this.longitude + '&z=5&show=1&fmt=png&w=302&h=302&att=none';
            this.mapurl = 'http://maps.googleapis.com/maps/api/staticmap?center=' + encodeURI(this.address) + '&zoom=10&size=302x298&maptype=roadmap&markers=color:blue%7Clabel:' + encodeURI(this.title) + '%7C' + encodeURI(this.address) + '&sensor=false';
            pl('#address').html(this.address);
            pl('#addresslink').attr({href: this.addressurl});
            pl('#mapimg').attr({src: this.mapurl});
        },
        displayDocumentLink: function(linkId, btnId, docId) {
            var url;
            if (!docId && Math.random() > 0.5) { // FIXME: simulation
                docId = 'ag1zdGFydHVwYmlkZGVych4LEgpMaXN0aW5nRG9jIg5Eb2MtMjExMzY3MzkxOAw';
            }
            if (docId) {
                url = '/file/download/' + docId;
                pl('#'+btnId).addClass('span-3 smallinputbutton').html('DOWNLOAD');
                pl('#'+linkId).attr({href: url});
                
            }
            else {
                pl('#'+btnId).addClass('span-3 doclinkmsg attention').html('NONE');
                pl('#'+linkId).attr({href: '#'}).addClass('nohover').bind({click: function() { return false; }});
            }
        },
        displayDocuments: function() {
            this.displayDocumentLink('presentationlink', 'presentationbtn', this.presentation_id);
            this.displayDocumentLink('businessplanlink', 'businessplanbtn', this.business_plan_id);
            this.displayDocumentLink('financialslink', 'financialsbtn', this.financials_id);
            pl('#documentbox').show();
        },
        displayFunding: function() {
            this.askingFunding = this.askingFunding || (this.suggested_amt > 0 ? true : false);
            if (this.askingFunding) {
                this.suggested_type = this.suggested_type || (Math.floor(Math.random()*2) ? 'COMMON STOCK' : 'PREFERRED STOCK'); // FIXME
                pl('#suggested_amt').html(this.currency.format(this.suggested_amt));
                pl('#suggested_pct').html(this.suggested_pct);
                pl('#suggested_val').html(this.currency.format(this.suggested_val));
                pl('#suggested_type').html(this.suggested_type);
                pl('#closingmsg').html(this.closing_date && this.days_left >= 0 ? 'CLOSES ON ' + this.dateobj.format(closing_date) + ' (' + (this.days_left > 0 ? this.days_left + ' DAYS LEFT' : 'CLOSES TODAY!') + ')' : 'BIDDING CLOSED');
            }
            else {
                pl('#suggestedmsg').html('NOT SEEKING FUNDING');
                pl('#suggestedinfo').hide();
            }
        },
        displaySocial: function() {
            this.displayFacebook();
            this.displayGooglePlus();
            this.displayTwitter();
        },
        displayFacebook: function() {
            this.addFacebookMetaTags();
            (function(d, s, id) {
            var js, fjs = d.getElementsByTagName(s)[0];
            if (d.getElementById(id)) return;
            js = d.createElement(s); js.id = id;
            js.src = "//connect.facebook.net/en_US/all.js#xfbml=1";
            fjs.parentNode.insertBefore(js, fjs);
            }(document, 'script', 'facebook-jssdk'));
        },
        addFacebookMetaTags: function() {
            var metas, prop, content, metatag;
            metas = {
                'og:title': this.title,
                'og:type': 'company',
                'og:url': 'http://starutpbidder.com/company_page.html?id=' + this.id,
                'og:image': 'http://startupbidder.com' + this.logourl,
                'og:site_name': 'startupbidder',
                'fb:app_id': '3063944677997'
            };
            for (prop in metas) {
                content = metas[prop];
                metatag = '<meta property="' + prop + '" content="' + content + '"/>';
                pl('head').append(metatag);
            }
        },
        displayGooglePlus: function() {
            (function() {
              var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;
              po.src = 'https://apis.google.com/js/plusone.js';
              var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(po, s);
            })();
        } ,
        displayTwitter: function() {
            !function(d,s,id){
                var js,fjs=d.getElementsByTagName(s)[0];
                if(!d.getElementById(id)){
                    js=d.createElement(s);
                    js.id=id;
                    js.src="//platform.twitter.com/widgets.js";
                    fjs.parentNode.insertBefore(js,fjs);
                }
            }(document,"script","twitter-wjs");
        },
        displayWithdraw: function() {
            var self;
            self = this;
            if (this.status === 'withdrawn') {
                return;
            }
            pl('#withdrawbtn').bind({
                click: function() {
                    if (pl('#withdrawmsg').html() !== '&nbsp;') {
                        self.withdraw();
                    }
                    else {
                        pl('#withdrawmsg').html('ARE YOU SURE?');
                        pl('#withdrawbtn').html('YES, WITHDRAW');
                    }
                }
            });
            pl('#withdrawbox').bind({
                mouseout: function() {
                    if (pl('#withdrawmsg').html() !== '&nbsp;' && self.status !== 'withdrawn') {
                        pl('#withdrawmsg').html('&nbsp;');
                        pl('#withdrawbtn').html('WITHDRAW LISTING');
                    }
                }
            });
            pl('#withdrawtitle').show();
            pl('#withdrawbox').show();
        },
        withdraw: function() {
            var self, url, completeFunc, ajax;
            self = this;
            url = '/listing/withdraw/' + this.listing_id;
            completeFunc = function() {
                pl('#withdrawmsg').addClass('successful').html('LISTING WITHDRAWN');
                pl('#withdrawbtn').hide();
                self.status = 'withdrawn';
                pl('#companystatus').html('Listing is ' + self.status).addClass('attention');
                 
            };
            ajax = new AjaxClass(url, 'withdrawmsg', completeFunc);
            ajax.setPost();
            ajax.call();
        }
    });

    function BidsClass(listing_id) {
        var self;
        self = this;
        this.listing_id = listing_id;
        this.url = '/bids/listing/' + this.listing_id;
        this.statusId = 'bidsmsg';
        this.completeFunc = function(json) {
            self.store(json);
            self.display();
        };
        this.ajax = new AjaxClass(this.url, this.statusId, this.completeFunc);
        this.date = new DateClass();
        this.safeStr = new SafeStringClass();
        this.currency = new CurrencyClass();
    }
    pl.implement(BidsClass, {
        load: function() {
            this.ajax.call();
        },
        store: function(json) {
            this.bids = json.bids || [];
            this.loggedin_profile_id = json.loggedin_profile ? json.loggedin_profile.profile_id : null;
        },
        display: function() {
            this.displayMakeBid();
            this.displayReviseBid();
            this.displayBids();
        },
        displayMakeBid: function () {
        },
        displayReviseBid: function () {
        },
        displayBids: function() {
            var html, i, bid, mybid;
            if (this.bids.length === 0) {
                pl('#bidslist').hide();
                pl('#bidsmsg').html('No bids').show();
                return;
            }
            html = '';
            withdrawableBids = [];
            for (i = 0; i < this.bids.length; i++) {
                bid = this.bids[i];
                if (bid.profile_id === this.loggedin_profile_id) {
                    mybid = true;
                }
                else {
                    mybid = false;
                }
                html += this.makeBid(bid, mybid);
            }
            pl('#bidsmsg').hide();
            pl('#bidslist').html(html).show();
        },
        makeBid: function(bid, mybid) {
            var actor, action, bidIcon, displayType, bidNote;
            if (mybid) {
                actor = 'You';
            }
            else if (bid.profile_id === bid.listing_profile_id) {
                actor = 'The owner';
            }
            else {
                actor = bid.profile_username;
            }
            if (bid.status === 'accepted') {
                action = 'accepted the bid';
                bidIcon = 'thumbup';
            }
            else if (bid.status === 'rejected') {
                action = 'rejected the bid';
                bidIcon = 'thumbdownicon';
            }
            else if (bid.status === 'withdrawn') {
                action = 'withdrew their bid';
                bidIcon = 'withdrawicon';
            }
            else if (bid.status === 'countered') { // FIXME: unimplemented
                action = 'made a counteroffer for the bid';
                bidIcon = 'countericon';
            }
            else { // active status
                action = 'made a bid';
                bidIcon = 'bidicon';
            }
            if (bid.bid_type === 'note') {
                displayType = 'a convertible note'; // FIXME: interest rate needed
            }
            else if (bid.bid_type === 'preferred') {
                displayType = 'convertible preferred stock'; // FIXME: interest rate needed
            }
            else {
                displayType = 'common stock'
            }
            bidNote = bid.bid_note ? this.safeStr.htmlEntities(bid.bid_note) : (
                actor === 'The owner'
                ? (Math.random() > 0.5 ? 'My company is worth more than this' : 'There is a huge opportunity in investing in me')
                : (Math.random() > 0.5 ? 'This is the best I can offer' : 'I see a lot of potential here')
            ); // FIXME: should usually be bid note
            return '\
<dt id="bid_' + bid.bid_id + '">\
    <div>\
        <div class="normalicon ' + bidIcon + '"></div>\
        ' + actor + ' ' + action + ' on ' + this.date.format(bid.bid_date) + '\
    </div>\
    <div>\
        ' + this.currency.format(bid.amount) + ' for ' + bid.equity_pct + '% equity as ' + displayType +' with company valued at ' + this.currency.format(bid.valuation) + '\
    </div>\
</dt>\
<dd id="bid_dd_' + bid.bid_id + '">' + bidNote + '</dd>\
';
        }
    });

    function CommentsClass(listing_id) {
        var self;
        self = this;
        this.listing_id = listing_id;
        this.url = '/comments/listing/' + this.listing_id;
        this.statusId = 'commentsmsg';
        this.completeFunc = function(json) {
            self.store(json);
            self.display();
        };
        this.ajax = new AjaxClass(this.url, this.statusId, this.completeFunc);
        this.date = new DateClass();
        this.safeStr = new SafeStringClass();
    }
    pl.implement(CommentsClass, {
        load: function() {
            this.ajax.call();
        },
        store: function(json) {
            this.comments = json.comments || [];
            this.loggedin_profile_id = json.loggedin_profile ? json.loggedin_profile.profile_id : null;
        },
        display: function() {
            this.displayAddCommentBox();
            this.displayComments();
        },
        displayAddCommentBox: function() {
            var self;
            self = this;
            if (!this.loggedin_profile_id) {
                return;
            }
            pl('#addcommenttext').bind({
                focus: function() {
                    if (!pl('#addcommenttext').hasClass('edited')) {
                        pl('#addcommenttext').attr({value: ''});
                        pl('#addcommentmsg').html('');
                    }
                },
                change: function() {
                    pl('#addcommenttext').addClass('edited').attr({value: ''});
                    pl('#addcommentmsg').html('');
                },
                blur: function() {
                    if (!pl('#addcommenttext').hasClass('edited')) {
                        pl('#addcommenttext').attr({value: 'Put your comment here...'});
                    }
                },
                keyup: function(event) {
                    var keycode, completeFunc, safeStr, commentText, ajax;
                    keycode = event.keyCode || event.which;
                    if (keycode && keycode === 13) {
                        completeFunc = function() {
                            pl('#addcommenttext').removeClass('edited').get(0).blur();
                            pl('#addcommentmsg').html('Comment posted');
                            self.load();
                        };
                        safeStr = new SafeStringClass();
                        commentText = safeStr.htmlEntities(safeStr.trim(pl('#addcommenttext').attr('value')));
                        ajax = new AjaxClass('/comment/create', 'addcommentmsg', completeFunc);
                        ajax.setPostData({
                            comment: {
                                listing_id: self.listing_id,
                                profile_id: self.loggedin_profile_id,
                                text: commentText
                            }
                        });
                        ajax.call();
                    }
                    return false;
                }
            });
            pl('#addcommenttitle').show();
            pl('#addcommentbox').show();            
        },
        displayComments: function() {
            var html, deletableComments, i, comment, deletable, commentDeleteSel;
            if (this.comments.length === 0) {
                pl('#commentlist').hide();
                pl('#commentsmsg').html('No comments').show();
                return;
            }
            html = '';
            deletableComments = [];
            for (i = 0; i < this.comments.length; i++) {
                comment = this.comments[i];
                deletable = false;
                if (comment.profile_id === this.loggedin_profile_id) {
                    deletableComments.push(comment);
                    deletable = true;
                }
                html += this.makeComment(comment, deletable);
            }
            pl('#commentmsg').hide();
            pl('#commentlist').html(html).show();
            for (i = 0; i < deletableComments.length; i++) {
                comment = this.comments[i];
                commentDeleteSel = '#comment_delete_' + comment.comment_id;
                pl(commentDeleteSel).bind({click: this.deleteCommentGenerator(comment)});
            }
        },
        deleteCommentGenerator: function(comment) {
            var commentId = comment.comment_id;
            return function() {
                var commentmsgId, commentDelUrl, completedFunc, ajax;
                commentmsgId = 'comment_delete_msg_' + commentId;
                commentDelUrl = '/comment/delete/' + commentId;
                completedFuncGenerator = function(commentId) {
                    var commentSel, commentddSel;
                    commentSel = '#comment_' + commentId;
                    commentddSel = '#comment_dd_' + commentId;
                    return function() {
                        pl(commentSel).remove();
                        pl(commentddSel).remove();
                    };
                };
                ajax = new AjaxClass(commentDelUrl, commentmsgId, completedFuncGenerator(commentId));
                ajax.setPost();
                ajax.call();
            };
        },
        makeComment: function(comment, deletable) {
            return '\
<dt id="comment_' + comment.comment_id + '">\
    <div class="commentdttitle">\
    <div class="commentdttitleline">Posted by ' + comment.profile_username + ' on ' + this.date.format(comment.comment_date) + '\
        ' + (deletable ? ' <span id="comment_delete_msg_' + comment.comment_id + '"></span>' : '') + '\
    </div>\
    ' + (deletable ? '<div id="comment_delete_' + comment.comment_id + '" class="commentdelete checkboxredicon"></div>' : '') + '\
    </div>\
</dt>\
<dd id="comment_dd_' + comment.comment_id + '">' + this.safeStr.htmlEntities(comment.text) + '</dd>\
';
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
    
    function ListingPageClass() {
        if (!this.queryString) {
            this.queryString = new QueryStringClass();
            this.queryString.load();
        }
        this.id = this.queryString.vars.id;
    };
    pl.implement(ListingPageClass,{
        loadPage: function() {
            var listing, bids, comments;
            listing = new ListingClass(this.id);
            bids = new BidsClass(this.id);
            comments = new CommentsClass(this.id);
            companies = new RelatedCompaniesClass(this.id);
            listing.load();
            bids.load();
            comments.load();
            companies.load();
        }
    });

    function DispatcherClass() {}
    pl.implement(DispatcherClass,{
        loadPage: function() {
            var classList, i, bodyClass, pageClass, page;
            classList = [
                [ 'main-page', MainPageClass ],
                [ 'profile-page', ProfilePageClass ],
                [ 'edit-profile-page', EditProfilePageClass ],
                [ 'company-page', ListingPageClass ],
                [ 'default', InformationPageClass ] // default must be last
            ];
            for (i = 0; i < classList.length; i++) {
                bodyClass = classList[i][0];
                pageClass = classList[i][1];
                if (pl('body').hasClass(bodyClass)) {
                    break;
                }
            }
            page = new pageClass();
            page.loadPage();
        }
    });

    var dispatcher = new DispatcherClass();
    dispatcher.loadPage();
});
