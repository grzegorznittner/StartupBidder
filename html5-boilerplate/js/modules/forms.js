function URLCheckClass() {}
pl.implement(URLCheckClass, {
    check: function(str) {
        var regex = /[-a-zA-Z0-9@:%_\+.~#?&//=]{2,256}\.[a-z]{2,4}\b(\/[-a-zA-Z0-9@:%_\+.~#?&//=]*)?/gi;
        if (!str) {
            return 'Must not be empty.';
        }
        if (!str.match(regex)) {
            return 'Must be a valid URL.';
        }
        return 0;
    },
    checkEmptyOk: function(str) {
        if (!str) {
            return 0;
        }
        else {
            return URLCheckClass.prototype.check(str);
        }
    }
});

function VideoCheckClass() {}
pl.implement(VideoCheckClass, {
    preformat: function(str) {
        var matcher = {
                yahoo: {
                    regex: /(https?:\/\/)?[^\/]*youtube.com\/(embed\/|.*v=)([^\/&]*)/,
                    prefix: 'http://www.youtube.com/embed/',
                    postfix: ''
                },
                vimeo: {
                    regex: /(https?:\/\/)?[^\/]*vimeo.com\/(video\/)?([^\/&]*)/,
                    prefix: 'http://player.vimeo.com/video/',
                    postfix: '?title=0&byline=0&portrait=0'
                },
                dailymotion: {
                    regex: /(https?:\/\/)?[^\/]*dailymotion.com\/(.*#)?video[\/=]([^\/&]*)/,
                    prefix: 'http://www.dailymotion.com/embed/video/',
                    postfix: ''
                }
            },
            service,
            serviceopts,
            matches,
            videoid,
            url = '';
        for (service in matcher) {
            serviceopts = matcher[service];
            matches = str ? str.match(serviceopts.regex) : [];
            if (matches && matches.length === 4) {
                videoid = matches[3];
                url = serviceopts.prefix + videoid + serviceopts.postfix;
                break;
            }
        }
        return url;
    },
    check: function(str) {
        var url = VideoCheckClass.prototype.preformat(str);
        if (!url) {
            return 'Must be a valid youtube, vimeo, or dailymotion URL.';
        }
        return 0;
    }
});
 
function EmailCheckClass() {}
pl.implement(EmailCheckClass, {
    check: function(emailStr) {
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
        var matchArray = emailStr ? emailStr.match(emailPat) : '';
        if (!emailStr) {
            return "Email address must not be empty";
        }
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
                return "This address does not have a hostname.";
        }
        return 0;
    }
});

function ValidatorClass() {
    this.tests = [];
    this.postValidator = function(result, val) {};
}
pl.implement(ValidatorClass, {
    isCheckedVal: function(bool) {
        if (bool === true) {
            return 0;
        }
        else if (bool === false) {
            return 0;
        }
        else {
            return 'Must be checked or unchecked';
        }
    },
    isNotEmpty: function(str) {
        var trimmedStr = str ? str.replace(/^\s\s*/, '').replace(/\s\s*$/, '') : '';
        if (trimmedStr !== null && trimmedStr.length > 0) {
            return 0;
        }
        else {
            return "Value cannot be empty.";
        }
    },
    isSelected: function(val) {
        var str = '' + val;
        if (str !== 'Select...') {
            return 0;
        }
        else {
            return "Selection must be made.";
        }
    },
    makeLengthChecker: function(m, n) {
        var min = m,
            max = n;
        return function(str) {
            if (min > 0 && (!str || str.length === 0)) {
                return "Cannot be empty.";
            }
            if (min > 0 && str.length < min) {
                return "Must be at least " + min + " characters.";
            }
            if (max > 0 && str.length > max) {
                return "Must be no mare than " + max + " characters.";
            }
            return 0;
        }
    },
    isEmail: function(str) {
        return EmailCheckClass.prototype.check(str);
    },
    isURL: function(str) {
        return URLCheckClass.prototype.check(str);
    },
    isURLEmptyOk: function(str) {
        return URLCheckClass.prototype.checkEmptyOk(str);
    },
    isVideoURL: function(str) {
        return VideoCheckClass.prototype.check(str);
    },
    isCurrency: function(str) {
        var match = str ? str.match(/^[$]?[0-9]{1,3}(,?[0-9]{3})*$/) : false;
        return (match ? 0 : 'Please enter a currency value');
    },
    isPercent: function(str) {
        var match = str ? str.match(/^[1-9]?[0-9][%]?$/) : false;
        return (match ? 0 : 'Please enter a percent value');
    },
    genIsNumberBetween: function(min, max) {
        return function(n) {
            if (!ValidatorClass.prototype.isNumber(n)) {
                return 'Please enter a number';
            }
            if (n < min) {
                return 'You must enter at least ' + min;
            }
            if (n > max) {
                return 'You must enter at most ' + max;
            }
            return 0;
        }
    },
    isNumber: function(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
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
        var i,
            result = 0,
            val = str;
        if (this.preValidateTransform) {
            val = this.preValidateTransform(val);
        }
        for (i = 0; i < this.tests.length; i++) {
            result = this.tests[i](val);
            if (result !== 0) {
                break;
            }
        }
        if (this.postValidator) {
            this.postValidator(result, val);
        }
        return result;
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
    this.value = value;
    this.updateFunction = updateFunction;
    this.sel = '#' + id;
    this.validator = new ValidatorClass();
    this.msg = new UserMessageClass(msgId);
}
pl.implement(FieldBaseClass, {
    setDisplayName: function(name) {
        this.displayName = name;
    },
    getDisplayName: function() {
        return this.displayName || this.id.toUpperCase();
    },
    addValidator: function(validatorFunc) {
        this.validator.add(validatorFunc);
    },
    getLoadFunc: function() {
        var self = this;
        return function() {
            self.msg.show('inprogress', 'Saving changes...');
        };
    },
    getErrorFunc: function(displayFunc) {
        var self = this;
        return function(errorNum) {
            displayFunc();
            self.msg.show('attention', 'Error saving changes: ' + errorNum);
        };
    },
    getSuccessFunc: function() {
        var self = this;
        return function() {
            self.msg.show('successful', 'Saved changes');
            self.value = self.newval;
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
    validate: function() {
        var value = pl(this.fieldBase.sel).attr('checked');
        return this.fieldBase.validator.validate(value);
    },
    bindEvents: function() {
        var self = this;
        pl(self.fieldBase.sel).bind({
            change: function() {
                var changeKey = self.fieldBase.id,
                    newval = pl(self.fieldBase.sel).attr('checked'),
                    msg;
                    msg = self.fieldBase.validator.validate(newval);
                if (msg === 0) {
                    self.fieldBase.updateFunction({ changeKey: newval }, self.fieldBase.getLoadFunc(), self.fieldBase.getErrorFunc(self.getDisplayFunc()), self.fieldBase.getSuccessFunc());
                }
                else {
                    self.msg.show('attention', msg);
                }
                return false;
            }
        });
    }
});

function SelectFieldClass(id, value, updateFunction, msgId) {
    this.fieldBase = new FieldBaseClass(id, value, updateFunction, msgId);
    this.getDisplayFunc()();
}
pl.implement(SelectFieldClass, {
    setOptions: function(options) {
        var self = this,
            field = pl(self.fieldBase.sel).get(0),
            opts = options || [],
            val = self.fieldBase.value,
            i,
            optval;
        field.options.length = 0;
        field.options[0] = new Option('Select...', 'Select...', true, false);
        for (i = 0; i < opts.length; i++) {
            optval = opts[i];
            field.options[i+1] = new Option(optval, optval, false, (val === optval ? true : false));
        }
    },
    getDisplayFunc: function() {
        var self = this;
        return function() {
            if (self.fieldBase.value) {
                self.selectValue(self.fieldBase.value);
            }
        };
    },
    selectValue: function(value) {
        var self = this,
            field = pl(self.fieldBase.sel).get(0),
            options = field.options || [ value ],
            i;
        for (i = 0; i < options.length; i++) {
            if (options[i] === value) {
                field.selectedIndex = i;
                field.text = value;
                break;
            }
        }
    },
    getValue: function() {
        var self = this,
            field = pl(self.fieldBase.sel).get(0),
            options = field.options || [],
            selectedIndex = field.selectedIndex || 0,
            value = options && options[selectedIndex] ? options[selectedIndex].value : self.fieldBase.value;
        return value;
    },
    validate: function() {
        var self = this,
            value = self.getValue();
        return self.fieldBase.validator.validate(value);
    },
    bindEvents: function() {
        var self = this;
        pl(self.fieldBase.sel).bind({
            change: function() {
                var icon = new ValidIconClass(self.fieldBase.id + 'icon'),
                    changeKey = self.fieldBase.id,
                    newval = self.getValue(),
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
                self.fieldBase.updateFunction({ changeKey: newval }, self.fieldBase.getLoadFunc(), self.fieldBase.getErrorFunc(self.getDisplayFunc()), self.fieldBase.getSuccessFunc());
                return false;
            }
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
            var val = self.fieldBase.value,
                sel = self.fieldBase.sel;
            if (val !== null) {
                pl(sel).attr({value: val});
            }
        };
    },
    validate: function() {
        var self = this,
            value = pl(self.fieldBase.sel).attr('value');
        return self.fieldBase.validator.validate(value);
    },
    bindEvents: function(optionsparam) {
        var self = this,
            icon = new ValidIconClass(self.fieldBase.id + 'icon'),
            safeStr = new SafeStringClass(),
            sel = self.fieldBase.sel;
            self.options = optionsparam || {};
        pl(sel).bind({
            paste: function() {
                var newval = pl(sel).attr('value');
                if (!self.options.noAutoUpdate) {
                    self.inpaste = true;
                }
                return true;
            },
            focus: function() {
                var domval = pl(sel).attr('value');
                if (domval === pl(sel).get(0).defaultValue) { // blank out on edit
                    pl(sel).attr({value: ''});
                    self.value = null;
                }
                else {
                    self.value = pl(sel).attr('value'); // save the value
                }
                return false;
            },
            blur: function() { // push to server
                if (!self.options.noAutoUpdate) {
                    self.update();
                }
                return false;
            },
            change: function() {
                var newval = safeStr.htmlEntities(pl(sel).attr('value')),
                    validMsg = self.fieldBase.validator.validate(newval);
                if (validMsg !== 0) {
                    self.fieldBase.msg.show('attention', validMsg);
                    icon.showInvalid();
                }
                else {
                    if (!self.fieldBase.msg.isClear) {
                        self.fieldBase.msg.clear();
                    }
                    if (!icon.isValid) {
                        icon.showValid();
                    }
                    if (self.inpaste) {
                        self.inpaste = false;
                        self.update();
                    }
                }
                return false;
            },
            keyup: function(e) {
                if (e.keyCode === 13 && !self.options.noEnterKeySubmit) {
                    self.update();
                    return false;
                }
                else {
                    var newval = safeStr.htmlEntities(pl(sel).attr('value')),
                        validMsg = self.fieldBase.validator.validate(newval);
                    if (validMsg !== 0) {
                        self.fieldBase.msg.show('attention', validMsg);
                        icon.showInvalid();
                    }
                    else {
                        if (!self.fieldBase.msg.isClear) {
                            self.fieldBase.msg.clear();
                        }
                        if (!icon.isValid) {
                            icon.showValid();
                        }
                        if (self.inpaste) {
                            self.inpaste = false;
                            self.update();
                        }
                    }
                }
                return true;
            }
        });
    },
    update: function() {
        var self = this,
            icon = new ValidIconClass(self.fieldBase.id + 'icon'),
            safeStr = new SafeStringClass(),
            sel = self.fieldBase.sel;
            changeKey = self.fieldBase.id,
            domval = pl(sel).attr('value'),
            newval = safeStr.htmlEntities(domval),
            defval = pl(sel).get(0).defaultValue,
            validMsg = self.fieldBase.validator.validate(newval);
        if (validMsg !== 0) {
            self.fieldBase.msg.show('attention', validMsg);
            icon.showInvalid();
/*
            if (!self.value && defval) {
                pl(sel).attr({'value': defval}); // restore default
            }
            else {
                pl(sel).attr('value', self.value); // restore previous
            }
            self.fieldBase.msg.clear();
            icon.clear();
*/
            return;
        }
        icon.clear();
        if (self.value === newval) {
            //self.fieldBase.msg.clear();
            return;
        }
        self.fieldBase.newval = newval;
        self.fieldBase.updateFunction({ changeKey: newval },
            self.fieldBase.getLoadFunc(), self.fieldBase.getErrorFunc(self.getDisplayFunc()), self.fieldBase.getSuccessFunc());
    }
});
