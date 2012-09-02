function ValuationClass() {

    this.valuation_data = { application: { is_app_released: true }, company: {}};
    this.selectFields = {
        valuation_type: 1,
        is_app_released: 1,
        analyze_app_potential: 1,
        analyze_company_potential: 1
    };
    this.yesNoFields = {
        is_app_released: 1,
        analyze_app_potential: 1,
        analyze_company_potential: 1
    };
    this.currencyFields = {
        cost_to_duplicate: 1,
        current_revenue: 1,
        revenue_per: 1,
        cost_of_app: 1,
        best_month: 1,
        monthly_arpu: 1
    };
    this.numberFields = {
        market_size: 1,
        months_live: 1,
        target_users: 1
    };
    this.previousValuationType = null;
}

pl.implement(ValuationClass, {

    store: function(listing) {
        var vdata = listing.valuation_data ? JSON.parse(listing.valuation_data) : {},
            type = vdata.valuation_type !== undefined ? vdata.valuation_type : (listing.type || 'company'),
            k, v;
        this.valuation_data.valuation_type = type;
        if (vdata) {
            for (k in vdata) {
                v = vdata[k];
                this.valuation_data[k] = v;
            }
        }
    },

    displayActivePanel: function() {
        if (this.previousValuationType !== this.valuation_data.vaulation_type) {
            pl('.valuationpanel').hide();
        }
        switch (this.valuation_data.valuation_type) {
            case 'company':
                this.valueCompany();
                break;
            case 'application':
                this.valueApplication();
                break;
            default:
                this.valueCompany();
        }
        if (this.previousValuationType !== this.valuation_data.vaulation_type) {
            this.previousValuationType = this.valuation_data.valuation_type;
            pl('#valuation_' + this.valuation_data.valuation_type + '_wrapper').show();
        }
    },

    valueCompany: function() {
        var val = this.valuation_data.company,
            discount_rate = 0.1,
            penetration_rate = 1, // domination
            profit_margin = 0.3,
            exit_probability = 0.1,
            exit_year = 7,
            ps_ratio = 7,
            development_stage_map = {
                concept: 250000,
                team: 500000,
                product: 1000000,
                customers: 2000000,
                profitability: 5000000
            },
            current_value,
            development_stage_value,
            target_market,
            exit_value,
            npv_exit_value,
            npv_exit_value_risk_adjusted,
            company_valuation,
            num_valuations;

        development_stage_value = development_stage_map[val.development_stage];
        current_value = val.current_revenue * ps_ratio;
        target_market = val.market_size * penetration_rate;
        exit_value = target_market * val.revenue_per * profit_margin * ps_ratio;
        npv_exit_value = exit_value * Math.pow((1 - discount_rate), exit_year);
        npv_exit_value_risk_adjusted = npv_exit_value * exit_probability;
        pl('#exit_value').text(CurrencyClass.prototype.format(Math.floor(exit_value)));

        company_valuation = current_value || 0;
        num_valuations = 1;
        if (development_stage_value) {
            company_valuation += development_stage_value;
            num_valuations++;
        }
        if (val.cost_to_duplicate) {
            company_valuation += val.cost_to_duplicate;
            num_valuations++;
        }
        if (val.analyze_company_potential && npv_exit_value_risk_adjusted) {
            company_valuation += npv_exit_value_risk_adjusted;
            num_valuations++;
        }
        if (num_valuations) {
            company_valuation /= num_valuations;
        }
        company_valuation = Math.max(company_valuation, val.current_revenue); // prevent less than current 
        pl('#company_valuation').text(CurrencyClass.prototype.format(Math.floor(company_valuation)));
    },

    valueApplication: function() {
        var val = this.valuation_data.application,
            monthly_growth_rate = 0.35,
            monthly_decline_rate = 0.07,
            discount_rate = 0.1,
            exit_probability = 0.1,
            months_to_best, n, r, growth_ratio,
            future_peak, projected_peak,
            growth_sum_ratio, earnings_to_peak,
            decline_sum_ratio, earnings_after_peak,
            future_earnings,
            monthly_target,
            target_valuation,
            npv,
            npv_risk_adjusted,
            application_valuation,
            num_valuations;

        months_to_best = Math.max(Math.floor(6 - val.months_live), 0);
        n = months_to_best;
        r = 1 + monthly_growth_rate;
        growth_ratio = Math.pow(r, n);
        future_peak = val.best_month * growth_ratio;
        projected_peak = months_to_best > 0 ? future_peak : val.best_month;
        pl('#projected_peak').text(CurrencyClass.prototype.format(Math.floor(projected_peak)));

        if (months_to_best > 0) {
            growth_sum_ratio = (1 - Math.pow(r, (n+1))) / (1 - r);
            earnings_to_peak = projected_peak * growth_sum_ratio;
        }
        else {
            earnings_to_peak = projected_peak;
        }
        r = 1 - monthly_decline_rate;
        decline_sum_ratio = 1 / (1 - r);
        earnings_after_peak = projected_peak * decline_sum_ratio;
        future_earnings = earnings_to_peak + earnings_after_peak - projected_peak; // don't double count
        pl('#future_earnings').text(CurrencyClass.prototype.format(Math.floor(future_earnings)));

        monthly_target = val.target_users * val.monthly_arpu;
        pl('#monthly_target').text(CurrencyClass.prototype.format(Math.floor(monthly_target)));

        target_valuation = (monthly_target * 12) / discount_rate;
        pl('#target_valuation').text(CurrencyClass.prototype.format(Math.floor(target_valuation)));
        npv = target_valuation * Math.pow((1 - discount_rate), 2);
        npv_risk_adjusted = npv * exit_probability;

        application_valuation = future_earnings || 0;
        num_valuations = 1;
        if (val.cost_of_app) {
            application_valuation += val.cost_of_app;
            num_valuations++;
        }
        if (val.analyze_app_potential && npv_risk_adjusted) {
            application_valuation += npv_risk_adjusted;
            num_valuations++;
        }
        if (num_valuations) {
            application_valuation /= num_valuations;
        }
        application_valuation = Math.max(application_valuation, future_earnings); // prevent less than future earnings
        pl('#application_valuation').text(CurrencyClass.prototype.format(Math.floor(application_valuation)));
    },

    displayIsAppReleased: function() {
        if (this.valuation_data.application.is_app_released) {
            pl('#is_app_released_wrapper').show();
        }
        else {
            pl('#is_app_released_wrapper').hide();
        }
    },

    displayAnalyzeAppPotential: function() {
        if (this.valuation_data.application.analyze_app_potential) {
            pl('#analyze_app_potential_wrapper').show();
        }
        else {
            pl('#analyze_app_potential_wrapper').hide();
        }
    },

    displayAnalyzeCompanyPotential: function() {
        if (this.valuation_data.company.analyze_company_potential) {
            pl('#analyze_company_potential_wrapper').show();
        }
        else {
            pl('#analyze_company_potential_wrapper').hide();
        }
    }

});

function ValuationPageClass() {
    this.listing_id = (new QueryStringClass()).vars.id;
    this.valuation = new ValuationClass();
}
pl.implement(ValuationPageClass, {
    load: function() {
        var self = this,
            complete = function(json) {
                var header = new HeaderClass(),
                    companybanner = new CompanyBannerClass('model');
                header.setLogin(json);
                companybanner.display(json);
                self.display(json.listing);
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            error = function(errornum, json) {
                (new HeaderClass()).setLogin(json);
                pl('.preloader, .companyheader').hide();
                pl('.errorwrapper').show();
            },
            ajax = new AjaxClass('/listing/get/' + this.listing_id, 'valuationmsg', complete, null, null, error);
        ajax.call();
    },

    display: function(listing) {
        this.valuation.store(listing);
        this.displayValuationData();
        this.valuation.displayIsAppReleased();
        this.valuation.displayAnalyzeAppPotential();
        this.valuation.displayAnalyzeCompanyPotential();
        this.valuation.displayActivePanel();
    },

    displayValuationData: function() {
        var vdata = this.valuation.valuation_data;
        this.displaySelectField('valuation_type', vdata.valuation_type);
        this.displayFieldMap(vdata.application);
        this.displayFieldMap(vdata.company);
    },

    displayFieldMap: function(map) {
        var k, v;
        for (k in map) {
            v = map[k];
            if (k in this.valuation.selectFields) {
                this.displaySelectField(k, v);
            }
            else if (k in this.valuation.currencyFields) {
                this.displayCurrencyField(k, v);
            }
            else if (k in this.valuation.numberFields) {
                this.displayNumberField(k, v);
            }
            else {
                this.displayTextField(k, v);
            }
        }
    },

    displaySelectField: function(id, val) {
        var yesnoval = val ? 'yes' : 'no',
            useval = this.valuation.yesNoFields[id] ? yesnoval : val,
            displayVal = useval ? SafeStringClass.prototype.ucfirst(useval.toString()) : '';
        pl('#' + id).text(displayVal);
    },

    displayCurrencyField: function(id, val) {
        pl('#' + id).text(val !== undefined ? CurrencyClass.prototype.format(val) : '');
    },

    displayNumberField: function(id, val) {
        pl('#' + id).text(val !== undefined ? NumberClass.prototype.formatText(val) : '');
    },

    displayTextField: function(id, val) {
        pl('#' + id).text(SafeStringClass.prototype.htmlEntities(val.toString()));
    }

});

function NewListingValuationClass() {
    this.listing_id = (new QueryStringClass()).vars.id;
    this.base = new NewListingBaseClass();
    this.bound = {};
    this.valuation = new ValuationClass();
}
pl.implement(NewListingValuationClass, {
    load: function() {
        var self = this,
            url = this.listing_id
                ? '/listing/get/' + this.listing_id
                : '/listings/create',

            complete = function(json) {
                var listing = json && json.listing ? json.listing : {},
                    header = new HeaderClass();
                header.setLogin(json);
                self.base.store(listing);
                self.valuation.store(listing);
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
        pl('#listingtype').text(this.base.listing.type.toUpperCase());
        this.displayValuationData();
        this.valuation.displayIsAppReleased();
        this.valuation.displayAnalyzeAppPotential();
        this.valuation.displayAnalyzeCompanyPotential();
        this.loadValuesFromInput();
        this.valuation.displayActivePanel();
        this.base.bindBackButton();
        this.base.bindPreviewButton();
        this.bindSaveButton();
        this.bindValuationTypeSelect();
        this.bindIsAppReleasedSelect();
        this.bindAnalyzeAppPotentialSelect();
        this.bindAnalyzeCompanyPotentialSelect();
        this.bindPanels();
    },

    displayValuationData: function() {
        var vdata = this.valuation.valuation_data;
        this.displaySelectField('valuation_type', vdata.valuation_type);
        this.displayFieldMap(vdata.application);
        this.displayFieldMap(vdata.company);
    },

    displayFieldMap: function(map) {
        var k, v;
        for (k in map) {
            v = map[k];
            if (k in this.valuation.selectFields) {
                this.displaySelectField(k, v);
            }
            else if (k in this.valuation.currencyFields) {
                this.displayCurrencyField(k, v);
            }
            else if (k in this.valuation.numberFields) {
                this.displayNumberField(k, v);
            }
            else {
                this.displayTextField(k, v);
            }
        }
    },

    displaySelectField: function(id, val) {
        return this.valuation.yesNoFields[id] ? this.displayBooleanSelectField(id, val) : this.displayRegularSelectField(id, val);
    },

    displayRegularSelectField: function(id, val) {
        var field = pl('#' + id).get(0),
            options = field.options,
            option,
            i;
        for (i = 0; i < options.length; i++) {
            option = options[i];
            if (option.value == val) {
                field.selectedIndex = i;
                break;
            }
        }
    },

    displayBooleanSelectField: function(id, val) {
        var isselected = val !== undefined && (val === true || val === 'true') ? true : false,
            field = pl('#' + id).get(0);
        if (isselected) {
            field.selectedIndex = 0;
        }
        else {
            field.selectedIndex = 1;
        }
    },

    displayCurrencyField: function(id, val) {
        pl('#' + id).attr('value', val !== undefined ? CurrencyClass.prototype.format(val) : '');
    },

    displayNumberField: function(id, val) {
        pl('#' + id).attr('value', val !== undefined ? NumberClass.prototype.formatText(val) : '');
    },

    displayTextField: function(id, val) {
        pl('#' + id).attr('value', SafeStringClass.prototype.htmlEntities(val.toString()));
    },

    bindSaveButton: function() {
        var self = this;
        this.base.bindSaveButton(function() {
            var data = {
                    listing: {
                        id: self.listing_id,
                        valuation_data: JSON.stringify(self.valuation.valuation_data)
                    }
                },
                complete = function(json) {
                    pl('#savebuttonspinner').hide();
                    pl('#savebutton').text('SAVED').show();
                    setTimeout(function() {
                        pl('#savebutton').text('SAVE').show();
                    }, 1000);
                },
                ajax = new AjaxClass('/listing/update_field/.json', 'newlistingmsg', complete);
            pl('#savebutton').hide();
            pl('#savebuttonspinner').show();
            ajax.setPostData(data);
            ajax.call();
            return false;
        });
    },

    getSelectedValuationType: function() {
        var selectfield = pl('#valuation_type').get(0),
            options = selectfield.options,
            selectedindex = selectfield.selectedIndex,
            option = selectedindex >= 0 ? options[selectedindex] : null,
            value = option ? option.value : 'company';
        return value;
    },

    isAppReleasedSelected: function() {
        return this.isBooleanSelectSelected('#is_app_released');
    },

    isAnalyzeAppPotentialSelected: function() {
        return this.isBooleanSelectSelected('#analyze_app_potential');
    },

    isAnalyzeCompanyPotentialSelected: function() {
        return this.isBooleanSelectSelected('#analyze_company_potential');
    },

    isBooleanSelectSelected: function(sel) {
        var selectfield = pl(sel).get(0),
            options = selectfield.options,
            selectedindex = selectfield.selectedIndex,
            option = selectedindex >= 0 ? options[selectedindex] : null,
            value = option && option.value && option.value === 'true' ? true : false;
        return value;
    },

    loadValuesFromInput: function() {
        var companyval = this.valuation.valuation_data.company,
            appval = this.valuation.valuation_data.application;

        companyval.market_size = Math.max(NumberClass.prototype.clean(pl('#market_size').attr('value')), 0);
        companyval.revenue_per = Math.max(CurrencyClass.prototype.clean(pl('#revenue_per').attr('value')), 0);
        companyval.current_revenue = Math.max(CurrencyClass.prototype.clean(pl('#current_revenue').attr('value')), 0);
        companyval.cost_to_duplicate = Math.max(Math.floor(NumberClass.prototype.clean(pl('#cost_to_duplicate').attr('value'))), 0);
        companyval.development_stage = pl('#development_stage').get(0)[pl('#development_stage').get(0).selectedIndex || 0].value;

        appval.cost_of_app = Math.max(NumberClass.prototype.clean(pl('#cost_of_app').attr('value')), 0);
        appval.months_live = Math.max(NumberClass.prototype.clean(pl('#months_live').attr('value')), 0);
        appval.best_month = Math.max(NumberClass.prototype.clean(pl('#best_month').attr('value')), 0);
        appval.target_users = Math.max(NumberClass.prototype.clean(pl('#target_users').attr('value')), 0);
        appval.monthly_arpu = Math.max(NumberClass.prototype.clean(pl('#monthly_arpu').attr('value')), 0);
    },

    bindIsAppReleasedSelect: function() {
        var self = this;
        pl('#is_app_released').bind('change', function() {
            self.valuation.valuation_data.application.is_app_released = self.isAppReleasedSelected();
            self.valuation.displayIsAppReleased();
            return false;
        });
    },

    bindAnalyzeAppPotentialSelect: function() {
        var self = this;
        pl('#analyze_app_potential').bind('change', function() {
            self.valuation.valuation_data.application.analyze_app_potential = self.isAnalyzeAppPotentialSelected();
            self.valuation.displayAnalyzeAppPotential();
            return false;
        });
    },

    bindAnalyzeCompanyPotentialSelect: function() {
        var self = this;
        pl('#analyze_company_potential').bind('change', function() {
            self.valuation.valuation_data.company.analyze_company_potential = self.isAnalyzeCompanyPotentialSelected();
            self.valuation.displayAnalyzeCompanyPotential();
            return false;
        });
    },

    bindValuationTypeSelect: function() {
        var self = this;
        pl('#valuation_type').bind('change', function() {
            self.valuation.valuation_data.valuation_type = self.getSelectedValuationType();
            self.valuation.displayActivePanel();
            return false;
        });
    },

    bindPanels: function() {
        var self = this,
            evaluate = function() {
                self.loadValuesFromInput();
                self.valuation.displayActivePanel();
                return false;
            };
        pl('.valuationinput').bind({
            focus: evaluate,
            blur: evaluate,
            keyup: evaluate,
            change: evaluate
        });
    }

});


