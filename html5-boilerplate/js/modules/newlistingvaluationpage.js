function NewListingValuationClass() {
    var queryString = new QueryStringClass();
    this.id = queryString.vars.id;
    this.listing_id = this.id;
    this.base = new NewListingBaseClass();
}
pl.implement(NewListingValuationClass, {
    load: function() {
        var self = this,
            url = this.id
                ? '/listing/get/' + this.id
                : '/listings/create',

            complete = function(json) {
                var listing = json && json.listing ? json.listing : {},
                    header = new HeaderClass();
                header.setLogin(json);
                self.base.store(listing);
                self.display();
                pl('.preloader').hide();
                pl('.wrapper').show();
            },

            error = function(errornum, json) {
                (new HeaderClass()).setLogin(json);
                pl('.preloader, .companyheader').hide();
                pl('.errorwrapper').show();
            },

            ajax = new AjaxClass(url, 'newlistingmsg', complete, null, null, error);

        if (url === '/listings/create') {
            ajax.setPost();
        }
        ajax.call();
    },

    display: function() {
        this.bindShared();
        if (this.base.listing.type === 'application') {
            this.displayAppValuation();
        }
        else {
            this.displayCompanyValuation();
        }
    },

    bindShared: function() {
        //this.base.bindNavButtons(this.genNextValidator());
        this.base.bindNavButtons(null);
        this.base.bindTitleInfo();
        this.base.bindInfoButtons();
    },

    displayAppValuation: function() {
        this.bindAppValuationFields();
        this.valueApp();
        pl('#newlistingappwrapper').show();
    },

    displayCompanyValuation: function() {
        this.bindCompanyValuationFields();
        this.valueCompany();
        pl('#newlistingcompanywrapper').show();
    },

/*
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
        this.bindAskingButtons();
        this.base.bindNavButtons(this.genNextValidator());
        this.base.bindTitleInfo();
        this.base.bindInfoButtons();
    },

    bindAskingButtons: function() {
        var self = this;
        pl('.askingamtbtn').bind('click', function(e) {
            var amt = e.target && pl(e.target).text();
            if (amt) {
                pl('#suggested_amt').attr('value', amt);
                self.base.fieldMap.suggested_amt.update()
            }
        });
        pl('.askingpctbtn').bind('click', function(e) {
            var pct = e.target && pl(e.target).text();
            if (pct) {
                pl('#suggested_pct').attr('value', pct);
                self.base.fieldMap.suggested_pct.update()
            }
        });
    },
*/

    valueCompany: function() {
        // market size * penetration rate = target market
        // target market *  revenue per customer * profit margin / discount rate = exit value
        // exit value * probability of exit * (1 - discount rate)^exit_year = valuation
        this.grabCompanyValues();
        this.calcCompanyValues();
        this.displayCompanyValues();
    },

    grabCompanyValues: function() {
        this.market_size = Math.max(NumberClass.prototype.clean(pl('#market_size').attr('value')), 0);
        this.penetration_rate = Math.max(NumberClass.prototype.clean(pl('#penetration_rate').attr('value')) / 100, 0);
        this.revenue_per = Math.max(CurrencyClass.prototype.clean(pl('#revenue_per').attr('value')), 0);
        this.profit_margin = Math.max(PercentClass.prototype.clean(pl('#profit_margin').attr('value')) / 100, 0);
        this.discount_rate = 0.1;
        this.exit_year = Math.max(Math.floor(NumberClass.prototype.clean(pl('#exit_year').attr('value'))), 0);
        this.exit_probability = Math.max(PercentClass.prototype.clean(pl('#exit_probability').attr('value') / 100), 0);
    },

    calcCompanyValues: function() {
        this.target_market = this.market_size * this.penetration_rate;
        this.exit_value = this.target_market * this.revenue_per * this.profit_margin / this.discount_rate;
        this.npv_exit_value = this.exit_value * Math.pow((1 - this.discount_rate), this.exit_year);
        this.company_valuation = this.npv_exit_value * this.exit_probability;
    },

    displayCompanyValues: function() {
        pl('#exit_value').text(CurrencyClass.prototype.format(Math.floor(this.exit_value)));
        pl('#company_valuation').text(CurrencyClass.prototype.format(Math.floor(this.company_valuation)));
    },

    bindCompanyValuationFields: function() {
        var self = this,
            evaluate = function() {
                self.valueCompany();
                return false;
            };
        pl('#newlistingcompanywrapper .valuationinput').bind({
            focus: evaluate,
            blur: evaluate,
            keyup: evaluate
        });
    },

    valueApp: function() {
        this.grabAppValues();
        this.calcAppValues();
        this.displayAppValues();
    },

    grabAppValues: function() {
        this.target_downloads = Math.max(NumberClass.prototype.clean(pl('#target_downloads').attr('value')), 0);
        this.conversion_rate = Math.max(NumberClass.prototype.clean(pl('#conversion_rate').attr('value')) / 100, 0);
        this.download_price = Math.max(CurrencyClass.prototype.clean(pl('#download_price').attr('value')), 0);
        this.ad_revenue_per_download = Math.max(CurrencyClass.prototype.clean(pl('#ad_revenue_per_download').attr('value')), 0);
        this.revenue_retention = 0.7;
        this.target_probability = Math.max(PercentClass.prototype.clean(pl('#target_probability').attr('value') / 100), 0);
    },

    calcAppValues: function() {
        this.total_revenue_per_download = this.download_price * this.conversion_rate + this.ad_revenue_per_download;
        this.app_valuation = this.target_downloads * this.total_revenue_per_download * this.target_probability * this.revenue_retention;
    },

    displayAppValues: function() {
        pl('#app_valuation').text(CurrencyClass.prototype.format(Math.floor(this.app_valuation)));
    },

    bindAppValuationFields: function() {
        var self = this,
            evaluate = function() {
                self.valueApp();
                return false;
            };
        pl('#newlistingappwrapper .valuationinput').bind({
            focus: evaluate,
            blur: evaluate,
            keyup: evaluate
        });
    }

});

(new NewListingValuationClass()).load();

