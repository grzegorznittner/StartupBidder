function NewListingFinancialsClass() {
    this.base = new NewListingBaseClass();
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
                pl('.preloader').hide();
                pl('.wrapper').show();
            };
        ajax = new AjaxClass('/listings/create', 'newlistingmsg', completeFunc);
        ajax.setPost();
        ajax.call();
    },
    display: function() {
        var status = this.base.listing.status;
        if (status !== 'new' && status !== 'posted') {
            document.location = '/company-page.html?id=' + this.base.listing.id;
        }
        if (!this.bound) {
            this.bindEvents();
            this.bound = true;
        }
    },
    bindEvents: function() {
        var self = this,
            textFields = ['asked_fund', 'suggested_amt', 'suggested_pct', 'founders'],
            msgids = {
                asked_fund: 'newlistingaskmsg',
                suggested_amt: 'newlistingoffermsg',
                suggested_pct: 'newlistingoffermsg',
                founders: 'newlistingfoundersmsg'
            },
            validators = {
                asked_fund: ValidatorClass.prototype.isCheckedVal,
                suggested_amt: ValidatorClass.prototype.genIsNumberBetween(100, 500000),
                suggested_pct: ValidatorClass.prototype.genIsNumberBetween(1, 100),
                founders: ValidatorClass.prototype.isNotEmpty
            },
            classes = {
                asked_fund: CheckboxFieldClass,
                suggested_amt: TextFieldClass,
                suggested_pct: TextFieldClass,
                founders: TextFieldClass
            },
            names = {
                asked_fund: 'ALLOW BIDS',
                suggested_amt: 'ASKING',
                suggested_pct: 'PERCENT',
                founders: 'FOUNDERS'
            },
            preValidators = {
                suggested_amt: CurrencyClass.prototype.clean,
                suggested_pct: PercentClass.prototype.clean
            },
            id,
            cleaner,
            offerboxdisplay = function() {
                self.displayOfferBox();
            },
            field;
        self.base.fields = [];
        self.base.fieldMap = {};
        for (i = 0; i < textFields.length; i++) {
            id = textFields[i];
            cleaner = preValidators[id];
            if (id === 'asked_fund') {
                field = new (classes[id])(id, this.base.listing[id], this.base.getUpdater(id, cleaner, offerboxdisplay), msgids[id]);
            }
            else if (cleaner) {
                field = new (classes[id])(id, this.base.listing[id], this.base.getUpdater(id, cleaner), msgids[id]);
            }
            else {
                field = new (classes[id])(id, this.base.listing[id], this.base.getUpdater(id), msgids[id]);
            }
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
            else if (id === 'suggested_pct') {
                field.fieldBase.validator.postValidator = this.genDisplayCalculatedIfValidPct(field);
            }
            field.bindEvents();
            this.base.fields.push(field);
            this.base.fieldMap[id] = field;
        } 
        this.base.fieldMap['suggested_amt'].validate();
        this.base.fieldMap['suggested_pct'].validate();
        this.displayCalculatedIfValid();
        this.displayOfferBox();
        this.base.bindNavButtons(this.genNextValidator());
        this.base.bindTitleInfo();
        this.base.bindInfoButtons();
        pl('#newlistingfinancialswrapper').show();
    },
    genNextValidator: function() {
        var self = this;
        return function() {
            var asked_fund = pl('#asked_fund').attr('checked') ? true : false,
                msgs = asked_fund ? self.base.validate() : [];
            /*
            if (!self.base.listing.presentation_id) {
                msgs.push("SLIDE DECK: you must have a presentation.");
            }
            if (!self.base.listing.business_plan_id) {
                msgs.push("BUSINESS PLAN: you must have a business plan.");
            }
            if (!self.base.listing.presentation_id) {
                msgs.push("FINANCIALS: you must have a financial document.");
            }
            */
            return msgs;
        };
    },
    genDisplayAskedEffects: function(field) {
        var f1 = this.base.genDisplayCalculatedIfValid(field);
        var self = this;
        return function(result) {
            f1();
            self.displayOfferBox();
        }
    },
    displayOfferBox: function() {
        var fnd = this.base.fieldMap.asked_fund.fieldBase.value;
        if (fnd) {
            pl('#offerwrapper').addClass('offerwrapperdisplay');
        }
        else {
            pl('#offerwrapper').removeClass('offerwrapperdisplay');
        }
        this.displayCalculatedIfValid();
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
        var fnd = pl('#asked_fund').hasClass('checkboxcheckedicon') ? true : false,
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

