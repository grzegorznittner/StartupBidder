function NewListingMediaClass() {
    var base = new NewListingBaseClass();
    base.prevPage = '/new-listing-financials-page.html';
    base.nextPage = '/new-listing-submit-page.html';
    this.base = base;
}
pl.implement(NewListingMediaClass, {
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
/*
        var textFields = ['logo_url', 'suggested_amt', 'suggested_pct'],
            msgids = {
                asked_fund: 'newlistingmsg',
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
            id,
            cleaner,
            field;
        this.base.fields = [];
        this.base.fieldMap = {};
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
*/
        this.base.bindNavButtons();
    }
});

function NewListingPageClass() {};
pl.implement(NewListingPageClass, {
    loadPage: function() {
        var newlisting = new NewListingMediaClass();
        newlisting.load();
    }
});

(new NewListingPageClass()).loadPage();

