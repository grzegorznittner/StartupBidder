function NewListingFinancialsClass() {
    var base = new NewListingBaseClass();
    base.prevPage = '/new-listing-qa-page.html';
    base.nextPage = '/new-listing-media-page.html';
    this.base = base;
}
pl.implement(NewListingFinancialsClass, {
    load: function() {
        var self = this,
            completeFunc = function(json) {
                var listing = json && json.listing ? json.listing : {},
                    header = new HeaderClass();
                header.setLogin(json);
                self.base.store(listing);
                self.display();
            };
        ajax = new AjaxClass('/listings/create', 'newlistingmsg', completeFunc);
        ajax.setPost();
        ajax.call();
    },
    display: function() {
        if (this.base.listing.status !== 'new') {
            document.location = 'new-listing-submitted-page.html';
        }
        if (!this.bound) {
            this.bindEvents();
            this.bound = true;
        }
    },
    displayUpload: function(id) {
        var fieldname = id + '_id',
            val = this.base.listing[fieldname],
            imgclass = id + 'img',
            imgsel = '#' + imgclass,
            formsel = '#' + id + 'uploadform',
            linksel = '#' + id + 'link',
            downloadsel = '#' + id + 'downloadbg, #' + id + 'downloadtext',
            uploadfield = id + '_upload',
            uploadurl = this.base.listing[uploadfield],
            calcclass = val ? imgclass : 'noimage',
            classes = 'tileimg ' + calcclass,
            linkurl = val ? '/file/download/' + val : '#';
        pl(imgsel).attr({'class': classes});
        pl(linksel).attr({href: linkurl});
        if (val) {
            pl(downloadsel).show();
        }
        if (uploadurl) {
            pl(formsel).attr({action: uploadurl});
        }
    },
    bindUploadField: function(id) {
        var self = this;
            uploadfield = id + '_upload',
            iframesel = '#' + id + 'uploadiframe',
            browsesel = '#' + id.toUpperCase(),
            buttonsel = '#' + id + 'loadurlbutton',
            displayname = id.toUpperCase().replace('_',' ') + ' UPLOAD URL',
            urlid = id + '_url',
            msgid = id + 'msg',
            genPostUpload = function(id) {
                var fieldname = id + '_id',
                    uploadId = id;
                return function(json) {
                    var val = json && json.listing && json.listing[fieldname] ? json.listing[fieldname] : null;
                    if (val) {
                        self.base.listing[fieldname] = val;
                        self.displayUpload(uploadId);
                        self.base.displayCalculated();
                    }
                };
            },
            updater = self.base.getUpdater(urlid, null, genPostUpload(id)),
            field = new TextFieldClass(urlid, null, updater, msgid),
            genIframeLoad = function(id) {
                var iframesel = '#' + id + 'uploadiframe',
                    formsel = '#' + id + 'uploadform',
                    fieldname = id + '_id',
                    uploadfield = id + '_url';
                return function() {
                    var iframe = pl(iframesel).get(0).contentDocument.body.innerHTML,
                        uploadurlmatch = iframe.match(/upload_url.*(https?:\/\/.*\/upload\/[A-Za-z0-9]*).*upload_url/),
                        valmatch = iframe.match(/value&gt;(.*)&lt;\/value/),
                        uploadurl = uploadurlmatch && uploadurlmatch.length === 2 ? uploadurlmatch[1] : null,
                        val = valmatch && valmatch.length === 2 ? valmatch[1] : null;
                    if (uploadurl) {
                        self.base.listing[uploadfield] = uploadurl;
                        pl(formsel).attr({action: uploadurl});
                    }
                    if (val) {
                        self.base.listing[fieldname] = val;
                        self.displayUpload(id);
                        self.base.displayCalculated();
                    }
                };
            },
            genBrowseChange = function(id) {
                var formsel = '#' + id + 'uploadform';
                return function() {
                    pl(formsel).get(0).submit();
                    return false;
                };
            },
            genURLUpload = function(id, field) {
                var msgsel = '#' + id + 'msg';
                return function() {
                    var msg = field.validate();
                    if (msg === 0) {
                        field.update();
                    }   
                    else {
                        pl(msgsel).text(msg);
                    }
                };
            };
        pl(iframesel).bind({
            load: genIframeLoad(id)
        });
        pl(browsesel).bind({
            change: genBrowseChange(id)
        });
        pl(buttonsel).bind({
            click: genURLUpload(id, field)
        });
        field.fieldBase.setDisplayName(displayname);
        field.fieldBase.addValidator(ValidatorClass.prototype.isURL);
        field.bindEvents({noAutoUpdate: true});
        self.displayUpload(id);
    },
    bindEvents: function() {
        var self = this,
            textFields = ['asked_fund', 'suggested_amt', 'suggested_pct'],
            msgids = {
                asked_fund: 'newlistingaskmsg',
                suggested_amt: 'newlistingoffermsg',
                suggested_pct: 'newlistingoffermsg'
            },
            validators = {
                asked_fund: ValidatorClass.prototype.isCheckedVal,
                suggested_amt: ValidatorClass.prototype.genIsNumberBetween(5000, 500000),
                suggested_pct: ValidatorClass.prototype.genIsNumberBetween(5, 50)
            },
            classes = {
                asked_fund: CheckboxFieldClass,
                suggested_amt: TextFieldClass,
                suggested_pct: TextFieldClass
            },
            names = {
                asked_fund: 'ALLOW BIDS',
                suggested_amt: 'ASKING',
                suggested_pct: 'PERCENT'
            },
            preValidators = {
                suggested_amt: CurrencyClass.prototype.clean,
                suggested_pct: PercentClass.prototype.clean
            },
            uploadFields = ['presentation', 'business_plan', 'financials'],
            id,
            cleaner,
            field;
        self.base.fields = [];
        self.base.fieldMap = {};
        for (i = 0; i < textFields.length; i++) {
            id = textFields[i];
            cleaner = preValidators[id];
            field = new (classes[id])(id, this.base.listing[id], this.base.getUpdater(id, cleaner), msgids[id]);
            field.fieldBase.setDisplayName(names[id]);
            field.fieldBase.addValidator(validators[id]);
            if (preValidators[id]) {
                field.fieldBase.validator.preValidateTransform = preValidators[id];
            }
            if (id === 'asked_fund') {
                field.fieldBase.validator.postValidator = this.genDisplayAskedEffects(field);
            }
            else if (id === 'suggested_amt') {
                field.fieldBase.validator.postValidator = this.genDisplayCalculatedIfValidAmt(field);
            }
            else {
                field.fieldBase.validator.postValidator = this.genDisplayCalculatedIfValidPct(field);
            }
            field.bindEvents();
            this.base.fields.push(field);
            this.base.fieldMap[id] = field;
        } 
        for (i = 0; i < uploadFields.length; i++) {
            id = uploadFields[i];
            this.bindUploadField(id);
        }
        this.base.fieldMap['suggested_amt'].validate();
        this.base.fieldMap['suggested_pct'].validate();
        this.displayCalculatedIfValid();
        this.displayAskedEffects();
        this.base.bindNavButtons(this.genNextValidator());
    },
    genNextValidator: function() {
        var self = this;
        return function() {
            var asked_fund = pl('#asked_fund').attr('checked') ? true : false,
                msgs = asked_fund ? self.base.validate() : [];
            if (!self.base.listing.presentation_id) {
                msgs.push("SLIDE DECK: you must have a presentation.");
            }
            if (!self.base.listing.business_plan_id) {
                msgs.push("BUSINESS PLAN: you must have a business plan.");
            }
            if (!self.base.listing.presentation_id) {
                msgs.push("FINANCIALS: you must have a financial document.");
            }
            return msgs;
        };
    },
    genDisplayAskedEffects: function(field) {
        var f1 = this.base.genDisplayCalculatedIfValid(field);
        var self = this;
        return function(result) {
            f1();
            self.displayAskedEffects();
        }
    },
    displayAskedEffects: function() {
        var fnd = pl('#asked_fund').attr('checked') ? true : false;
        if (fnd) {
            pl('#suggested_amt, #suggested_pct').removeAttr('disabled');
            pl('#offerbox').css({opacity: 1});
        }
        else {
            pl('#suggested_amt, #suggested_pct').attr({disabled: true});
            pl('#offerbox').css({opacity: 0.5});
        }
    },
    genDisplayCalculatedIfValidAmt: function(field) {
        var self = this;
            f1 = this.base.genDisplayCalculatedIfValid(field);
        return function(result, val) {
            self.displayIfValidAmt(result, val);
            f1();
            self.displayCalculatedIfValid();
        }
    },
    genDisplayCalculatedIfValidPct: function(field) {
        var self = this;
            f1 = this.base.genDisplayCalculatedIfValid(field);
        return function(result, val) {
            self.displayIfValidPct(result, val);
            f1();
            self.displayCalculatedIfValid();
        }
    },
    displayIfValidAmt: function(result, val) {
        var fmt = CurrencyClass.prototype.format(val);
        if (result === 0) {
            pl('#suggested_amt').attr({value: fmt});
        }
    },
    displayIfValidPct: function(result, val) {
        var fmt = PercentClass.prototype.format(val);
        if (result === 0) {
            pl('#suggested_pct').attr({value: fmt});
        }
    },
    displayCalculatedIfValid: function() {
        var fnd = pl('#asked_fund').attr('checked') ? true : false,
            amt = CurrencyClass.prototype.clean(pl('#suggested_amt').attr('value')) || 0,
            pct = PercentClass.prototype.clean(pl('#suggested_pct').attr('value')) || 0,
            val = pct ? Math.floor(Math.floor(100 * amt / pct)) : 0,
            cur = CurrencyClass.prototype.format(CurrencyClass.prototype.clean(val)),
            dis = fnd && cur ? cur : '';
        pl('#suggested_val').text(dis);
    }
});

function NewListingPageClass() {};
pl.implement(NewListingPageClass, {
    loadPage: function() {
        var newlisting = new NewListingFinancialsClass();
        newlisting.load();
    }
});

(new NewListingPageClass()).loadPage();

