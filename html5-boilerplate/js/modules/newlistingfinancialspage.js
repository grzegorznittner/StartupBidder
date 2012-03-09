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
        if (!this.bound) {
            this.bindEvents();
            this.bound = true;
        }
    },
    bindEvents: function() {
        var textFields = ['asked_fund', 'suggested_amt', 'suggested_pct', 'closing_date'],
            validators = {
                asked_fund: ValidatorClass.prototype.isCheckedVal,
                suggested_amt: ValidatorClass.prototype.genIsNumberBetween(5000, 500000),
                suggested_pct: ValidatorClass.prototype.genIsNumberBetween(5, 50),
                closing_date: ValidatorClass.prototype.isNotEmpty
            },
            classes = {
                asked_fund: CheckboxFieldClass,
                suggested_amt: TextFieldClass,
                suggested_pct: TextFieldClass,
                closing_date: TextFieldClass
            },
            names = {
                asked_fund: 'ALLOW BIDS',
                suggested_amt: 'ASKING',
                suggested_pct: 'PERCENT',
                closing_date: 'CLOSING'
            },
            postValidators = {
                asked_fund: NewListingFinancialsClass.prototype.genDisplayAskedEffects,
                suggested_amt: NewListingFinancialsClass.prototype.genDisplayCalculatedIfValidAmt,
                suggested_pct: NewListingFinancialsClass.prototype.genDisplayCalculatedIfValid,
                closing_date: NewListingBaseClass.prototype.genDisplayCalculatedIfValid
            },
            preValidators = {
                suggested_amt: CurrencyClass.prototype.clean,
                suggested_pct: CurrencyClass.prototype.clean
            },
            id,
            field;
        this.base.fields = [];
        this.base.fieldMap = {};
        for (i = 0; i < textFields.length; i++) {
            id = textFields[i];
            field = new (classes[id])(id, this.base.listing[id], this.base.getUpdater(id), 'newlistingmsg');
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
                field.fieldBase.validator.postValidator = this.genDisplayCalculatedIfValid(field);
            }
            field.bindEvents();
            this.base.fields.push(field);
            this.base.fieldMap[id] = field;
        } 
        this.displayCalculatedIfValid();
        this.displayAskedEffects();
        this.base.bindNavButtons();
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
            pl('#suggested_amt, #suggested_pct, #closing_date').removeAttr('disabled');
            pl('#offerbox').css({opacity: 1});
        }
        else {
            pl('#suggested_amt, #suggested_pct, #closing_date').attr({disabled: true});
            pl('#offerbox').css({opacity: 0.5});
        }
    },
    genDisplayCalculatedIfValidAmt: function(field) {
        var self = this;
            f1 = this.base.genDisplayCalculatedIfValid(field);
        return function(result, val) {
            var fmt = CurrencyClass.prototype.format(val);
            if (result === 0) {
                pl('#suggested_amt').attr({value: fmt});
            }
            f1();
            self.displayCalculatedIfValid();
        }
    },
    genDisplayCalculatedIfValid: function(field) {
        var self = this;
            f1 = this.base.genDisplayCalculatedIfValid(field);
        return function() {
            f1();
            self.displayCalculatedIfValid();
        }
    },
    displayCalculatedIfValid: function() {
        var fnd = pl('#asked_fund').attr('checked') ? true : false,
            amt = CurrencyClass.prototype.clean(pl('#suggested_amt').attr('value')) || 0,
            pct = CurrencyClass.prototype.clean(pl('#suggested_pct').attr('value')) || 0,
            val = pct ? Math.floor(Math.floor(100 * amt / pct)) : 0,
            cur = CurrencyClass.prototype.format(CurrencyClass.prototype.clean(val)),
            dis = fnd && cur ? cur : '';
        console.log('pct:',pl('#suggested_pct').attr('value'));
        console.log(fnd, amt, pct, val, cur, dis);
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

