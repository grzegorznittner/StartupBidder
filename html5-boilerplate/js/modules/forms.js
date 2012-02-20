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
        if (this.preValidateTransform) {
            str = this.preValidateTransform(str);
        }
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
