pl(function() {

    function SafeStringClass() {}
    pl.implement(SafeStringClass, {
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

    function QueryStringClass() {}
    pl.implement(QueryStringClass, {
        load: function() {
            var i, pairs, keyval;
            this.vars = new Array();
            pairs = window.location.search ? window.location.search.split( "&" ) : {};
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
                            numbers.indexOf(seq) > -1 ||
                            (o.noQwertySequences && qwerty.indexOf(seq) > -1)
                        ) {
                            return "password cannot contain an alphanumeric or qwerty sequence of more than " + o.badSequenceLength + " characters";
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
            this.typeText = Math.floor(Math.random()*2) ? 'INTERNET' : 'SOFTWARE'; // FIXME: unimplemented API
            this.votes = json.num_votes || 0;
            this.posted = listingdate;
            this.name = json.title || 'Listed Company';
            this.loc = Math.floor(Math.random()*2) ? 'London, UK' : 'San Jose, CA, USA'; // FIXME: unimplemented API
            this.details = json.summary || 'Company details not provided';
        },
        makeHtml: function(lastClass) {
            var html = '\
<span class="span-4 '+ (lastClass?lastClass:'') +'">\
<div class="tile">\
    <a href="listing-page.html"><div class="tileimg ' + this.imgClass + '"></div></a>\
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
    <a href="listing-page.html">\
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

    function BasePageClass() {};
    pl.implement(BasePageClass,{
        getListingsType: function() {
            var type;
            if (!this.queryString) {
                this.queryString = new QueryStringClass();
                this.queryString.load();
            }
            type = this.queryString.vars.type || 'top';
            return type;
        },
        getListingsUrl: function(type) {
            var url = '/listings/' + type;
            return url;
        },
        storeListingsTitle: function(type) {
            var title = type.toUpperCase() + ' COMPANIES';
            pl('#listingstitle').html(title);
        },
        loadPage: function(successFunc) {
            var type = this.getListingsType();
            var url = this.getListingsUrl(type);
            var loadFunc = function() {
                pl('#companydiv').html('<span class="notice">Loading companies...</span>');
            };
            var errorFunc = function(errorNum) {
                pl('#companydiv').html('<span class="notice">Error while loading page: '+errorNum+'</span>');
            };
            var ajax = {
                async: true,
                url: url,
                type: 'GET',
                dataType: 'json',
                charset: 'utf-8',
                load: loadFunc,
                error: errorFunc,
                success: successFunc
            };
            this.storeListingsTitle(type);
            pl.ajax(ajax);
        }
    });

    function MainPageClass() {};
    pl.implement(MainPageClass,{
        loadPage: function() {
            var successFunc, basePage;
            successFunc = function(json) {
                var header, companyList, categoryList, locationList;
                if (!json) {
                    pl('#companydiv').html('<span class="notice">Error: null response from server</span>');
                    return;
                }
                header = new HeaderClass();
                companyList = new CompanyListClass();
                categoryList = new CategoryListClass();
                locationList = new LocationListClass();
                header.setLogin(json);
                companyList.storeList(json,4);
                categoryList.storeList(json);
                locationList.storeList(json);
            };
            basePage = new BasePageClass();
            basePage.loadPage(successFunc);
        }
    });

    function InformationPageClass() {}
    pl.implement(InformationPageClass,{
        loadPage: function() {
            var successFunc, basePage;
            successFunc = function(json) {
                var header, companyList;
                if (!json) {
                    pl('#companydiv').html('<span class="notice">Error: null response from server</span>');
                    return;
                }
                header = new HeaderClass();
                companyList = new CompanyListClass();
                header.setLogin(json);
                companyList.storeList(json,2);
            };
            basePage = new BasePageClass();
            basePage.loadPage(successFunc);
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
            var self = this;
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
                    self.fieldBase.msg.clear();
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
                investor: pl('#investor').attr('value') || false,
                facebook:'',
                twitter:'',
                linkedin:'',
            } };
            for (field in newdata) {
                data.profile[field] = newdata[field];
            }
            ajax = {
                async: true,
                url: self.updateUrl,
                type: 'POST',
                data: data,
                dataType: 'json',
                charset: 'utf-8',
                load: loadFunc,
                error: errorFunc,
                success: successFunc
            };
            pl.ajax(ajax);
            };
        },
        setProfile: function(json) {
            var properties, updateUrl, i, property, textFields, textFieldId, textFieldObj, investorCheckbox, notifyCheckbox;
            properties = ['profile_id', 'status', 'name', 'username', 'open_id', 'profilestatus', 'title', 'organization', 'email', 'phone', 'address'];
            textFields = ['email', 'name', 'title', 'organization', 'phone', 'address'];
            this.profile_id = json.profile_id;
            this.updateUrl = '/user/update?id=' + this.profile_id;
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
            var newPassword = new TextFieldClass('newpassword', '', function(){}, 'passwordmsg');
            var passwordOptions = {
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
            var confirmPassword = new TextFieldClass('confirmpassword', '', this.getUpdater(), 'passwordmsg');
            confirmPassword.fieldBase.addValidator(function(val) {
                if (pl('#newpassword').attr('value') === val) {
                    return 0;
                }
                else {
                    return "confirm must match new password";
                }
            });
            confirmPassword.bindEvents();
        }
    });

    function TestCompaniesClass() {}
    pl.implement(TestCompaniesClass, {
        testJson: function() {
            var randomSort, allCompanies, randomLen, companies, i;
            randomSort = function (a,b) {
                var temp = parseInt( Math.random()*10 );
                var isOddOrEven = temp%2;
                var isPosOrNeg = temp>5 ? 1 : -1;
                return( isOddOrEven*isPosOrNeg );
            };
            allCompanies = [{"num":1,"listing_id":"ag1zdGFydHVwYmlkZGVychcLEgdMaXN0aW5nIgpraWxsIGVtYWlsDA","title":"Kill email","suggested_amt":20000,"suggested_pct":50,"suggested_val":40000,"previous_val":40000,"valuation":40000,"median_valuation":75000,"score":3316,"listing_date":"20111018","closing_date":"20111117","status":"active","summary":"If any startup says it's going to eliminate email, it's destined for failure. You can iterate on the inbox, and try to improve it, but even that's not much of a business. The latest high profile flop in this arena is Google Wave. It was supposed to change email forever. It was going to displace email. Didn't happen.","profile_id":"ag1zdGFydHVwYmlkZGVychkLEgRVc2VyIg9idXNpbmVzc2luc2lkZXIM","profile_username":"Insider","num_comments":38,"num_bids":15,"num_votes":4,"votable":true,"days_ago":95,"days_left":-66,"mockData":true,"business_plan_id":null,"presentation_id":null,"financials_id":null},{"num":2,"listing_id":"ag1zdGFydHVwYmlkZGVychsLEgdMaXN0aW5nIg5sb2NhbG5ld3NzaXRlcww","title":"Local news sites","suggested_amt":9800,"suggested_pct":20,"suggested_val":49000,"previous_val":365217,"valuation":365217,"median_valuation":77000,"score":1326,"listing_date":"20111011","closing_date":"20111110","status":"active","summary":"Maybe Tim Armstrong, AOL, and Patch will prove it wrong, but to this point nobody has been able to crack the local news market and make a sustainable business.In theory creating a network of local news sites that people care about is a good idea. You build a community, there's a baked in advertising group with local businesses, and classifieds. But, it appears to be too niche to scale into a big business.","profile_id":"ag1zdGFydHVwYmlkZGVychQLEgRVc2VyIgpkcmFnb25zZGVuDA","profile_username":"The Dragon","num_comments":22,"num_bids":19,"num_votes":11,"votable":false,"days_ago":102,"days_left":-73,"mockData":true,"business_plan_id":null,"presentation_id":null,"financials_id":null},{"num":3,"listing_id":"ag1zdGFydHVwYmlkZGVyciMLEgdMaXN0aW5nIhZjb21wX3VwZ3JhZGluZ19zZXJ2aWNlDA","title":"Computer Upgrading Service","suggested_amt":11550,"suggested_pct":33,"suggested_val":35000,"previous_val":35000,"valuation":35000,"median_valuation":63000,"score":899,"listing_date":"20111009","closing_date":"20111108","status":"active","summary":"Starting a business that specializes in upgrading existing computer systems with new internal and external equipment is a terrific homebased business to initiate that has great potential to earn an outstanding income for the operator of the business. A computer upgrading service is a very easy business to get rolling, providing you have the skills and equipment necessary to complete upgrading tasks, such as installing more memory into the hard drive, replacing a hard drive, or adding a new disk drive to the computer system. Ideally, to secure the most profitable segment of the potential market, the service should specialize in upgrading business computers as there are many reasons why a business would upgrade a computer system as opposed to replacing the computer system. Additionally, managing the business from a homebased location while providing clients with a mobile service is the best way to keep operating overheads minimized and potentially increases the size of the target market by expanding the service area, due to the fact the business operates on a mobile format.","profile_id":"ag1zdGFydHVwYmlkZGVychkLEgRVc2VyIg9ncnplZ29yem5pdHRuZXIM","profile_username":"Greg","num_comments":34,"num_bids":27,"num_votes":11,"votable":false,"days_ago":104,"days_left":-75,"mockData":true,"business_plan_id":null,"presentation_id":null,"financials_id":null},{"num":4,"listing_id":"ag1zdGFydHVwYmlkZGVychsLEgdMaXN0aW5nIg5zZW1hbnRpY3NlYXJjaAw","title":"Semantic Search","suggested_amt":18000,"suggested_pct":45,"suggested_val":40000,"previous_val":40000,"valuation":40000,"median_valuation":61500,"score":639,"listing_date":"20111005","closing_date":"20111104","status":"active","summary":"The fact of the matter is Google, and to a much lesser extent Bing, own the search market. Ask Barry Diller, if you don't believe us.Yet, startups still spring up hoping to disrupt the incumbents. Cuil flopped. Wolfram Alpha is irrelevant. Powerset, which was a semantic search engine was bailed out by Microsoft, which acquired it.","profile_id":"ag1zdGFydHVwYmlkZGVychILEgRVc2VyIghqcGZvd2xlcgw","profile_username":"fowler","num_comments":2,"num_bids":24,"num_votes":4,"votable":false,"days_ago":108,"days_left":-79,"mockData":true,"business_plan_id":null,"presentation_id":null,"financials_id":null},{"num":5,"listing_id":"ag1zdGFydHVwYmlkZGVyciILEgdMaXN0aW5nIhVzb2NpYWxyZWNvbW1lbmRhdGlvbnMM","title":"Social recommendations","suggested_amt":1500,"suggested_pct":10,"suggested_val":15000,"previous_val":15000,"valuation":15000,"median_valuation":42500,"score":412,"listing_date":"20111004","closing_date":"20111103","status":"active","summary":"It's a very tempting idea. Collect data from people about their tastes and preferences. Then use that data to create recommendations for others. Or, use that data to create recommendations for the people that filled in the information. It doesn't work. The latest to try is Hunch and Get Glue.Hunch is pivoting towards non-consumer-facing white label business. Get Glue has had some success of late, but it's hardly a breakout business.","profile_id":"ag1zdGFydHVwYmlkZGVychELEgRVc2VyIgdjaGluZXNlDA","profile_username":"The One","num_comments":42,"num_bids":20,"num_votes":3,"votable":false,"days_ago":109,"days_left":-80,"mockData":true,"business_plan_id":null,"presentation_id":null,"financials_id":null}];
            allCompanies.sort(randomSort);
            randomLen = Math.floor(Math.random() * allCompanies.length);
            companies = [];
            for (i = 0; i < randomLen; i++) {
                companies[i] = allCompanies[i];
            }
            return companies;
        },
        testNotifications: function() {
            var types, dates, companies, i, company, notifications, notification;
            types = ['comment', 'bid'];
            dates = ['2011/11/29 13:12', '2011/12/20 9:46', '2012/01/13 8:37'];
            companies = this.testJson();
            notifications = [];
            for (i = 0; i < companies.length; i++) {
                company = companies[i];
                notification = {};
                notification.url = '/listing_page.html?id=' + company.listing_id;
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
            var url, loadFunc, errorFunc, successFunc, ajax;
            url = '/user/loggedin';
            loadFunc = function() {
                pl('#profilestatus').html('<span class="notice">Loading profile...</span>');
            };
            errorFunc = function(errorNum) {
                pl('#profilestatus').html('<span class="notice">Error while loading profile: '+errorNum+'</span>');
            };
            successFunc = function(json) {
                var header, profile, notifyList, companyList, testCompanies;
                if (!json) {
                    pl('#profilestatus').html('<span class="notice">Error: null response from server</span>');
                    return;
                }
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
            ajax = {
                async: true,
                url: url,
                type: 'GET',
                dataType: 'json',
                charset: 'utf-8',
                load: loadFunc,
                error: errorFunc,
                success: successFunc
            };
            pl.ajax(ajax);
        }
    });

    function EditProfilePageClass() {};
    pl.implement(EditProfilePageClass,{
        loadPage: function() {
            var url, loadFunc, errorFunc, successFunc, ajax;
            url = '/user/loggedin';
            loadFunc = function() {
                pl('#profilestatus').html('<span class="notice">Loading profile...</span>');
            };
            errorFunc = function(errorNum) {
                pl('#profilestatus').html('<span class="notice">Error while loading profile: '+errorNum+'</span>');
            };
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
            ajax = {
                async: true,
                url: url,
                type: 'GET',
                dataType: 'json',
                charset: 'utf-8',
                load: loadFunc,
                error: errorFunc,
                success: successFunc
            };
            pl.ajax(ajax);
        }
    });

    function DispatcherClass() {}
    pl.implement(DispatcherClass,{
        loadPage: function() {
            var pageClass, page;
            if (pl('body').hasClass('main-page')) {
                pageClass = MainPageClass;
            }
            else if (pl('body').hasClass('profile-page')) {
                pageClass = ProfilePageClass;;
            }
            else if (pl('body').hasClass('edit-profile-page')) {
                pageClass = EditProfilePageClass;;
            }
            else {
                pageClass = InformationPageClass;
            }
            page = new pageClass();
            page.loadPage();
        }
    });

    var dispatcher = new DispatcherClass();
    dispatcher.loadPage();
});
