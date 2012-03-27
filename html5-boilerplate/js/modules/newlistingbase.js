function NewListingBaseClass() {
    var pages = [ 'basics', 'bmc', 'qa', 'financials', 'media', 'submit' ],
        editableprops = [
            'title', 'category', 'mantra', 'website', 'founders', 'contact_email', 'address',
            'answer1', 'answer2', 'answer3', 'answer4', 'answer5', 'answer6', 'answer7', 'answer8', 'answer9', 'answer10',
            'summary', 'answer11', 'answer12', 'answer13', 'answer14', 'answer15', 'answer16', 'answer17', 'answer18',
            'answer19', 'answer20', 'answer21', 'answer22', 'answer23', 'answer24', 'answer25', 'answer26',
            'presentation_id', 'business_plan_id', 'financials_id',
            'logo', 'video'
        ],
        proppage = {
            title: 'basics',
            category: 'basics',
            mantra: 'basics',
			website: 'basics',
			founders: 'basics',
			contact_email: 'basics',
			address: 'basics',
            answer1: 'bmc',
			answer2: 'bmc',
			answer3: 'bmc',
			answer4: 'bmc',
			answer5: 'bmc',
			answer6: 'bmc',
			answer7: 'bmc',
			answer8: 'bmc',
			answer9: 'bmc',
			answer10: 'bmc',
            summary: 'qa',
			answer11: 'qa',
			answer12: 'qa',
			answer13: 'qa',
			answer14: 'qa',
			answer15: 'qa',
			answer16: 'qa',
			answer17: 'qa',
			answer18: 'qa',
            answer19: 'qa',
			answer20: 'qa',
			answer21: 'qa',
			answer22: 'qa',
			answer23: 'qa',
			answer24: 'qa',
			answer25: 'qa',
			answer26: 'qa',
            presentation_id: 'financials',
			business_plan_id: 'financials',
			financials_id: 'financials',
            logo: 'media',
			video: 'media'
        },
        displayNameOverrides = {
            address: 'LOCATION',
            contact_email: 'EMAIL',
            summary: 'ELEVATOR PITCH',
            answer1: 'KEY ACTIVITIES',
            answer2: 'KEY RESOURCES',
            answer3: 'KEY PARTNERS',
            answer4: 'VALUE PROPOSITIONS',
            answer5: 'CUSTOMER SEGMENTS',
            answer6: 'CHANNELS',
            answer7: 'CUSTOMER RELATIONSHIPS',
            answer8: 'COST STRUCTURE',
            answer9: 'REVENUE STREAMS',
            answer10: 'PROBLEM',
            answer11: 'SOLUTION',
            answer12: 'FEATURES AND BENEFITS',
            answer13: 'COMPANY STATUS',
            answer14: 'MARKET',
            answer15: 'CUSTOMER',
            answer16: 'COMPETITORS',
            answer17: 'COMPETITIVE COMPARISON',
            answer18: 'BUSINESS MODEL',
            answer19: 'MARKETING PLAN',
            answer20: 'TEAM',
            answer21: 'TEAM VALUES',
            answer22: 'CURRENT FINANCIALS',
            answer23: 'FINANCIAL PROJECTIONS',
            answer24: 'OWNERS',
            answer25: 'INVESTMENT',
            answer26: 'TIMELINE AND WRAPUP',
            presentation_id: 'PRESENTATION',
            business_plan_id: 'BUSINESS_PLAN',
            financials: 'FINANCIALS'
        },
        companyTile = new CompanyTileClass({preview: true});
    this.pages = pages;
    this.editableprops = editableprops;
    this.proppage = proppage;
    this.displayNameOverrides = displayNameOverrides;
    this.companyTile = companyTile;
    this.listing = {};
    this.fields = [];
    this.fieldMap = {};
    this.prevPage = '';
    this.nextPage = '';
};
pl.implement(NewListingBaseClass, {
    store: function(json) {
        var self = this;
        if (json) {
            CollectionsClass.prototype.merge(this.listing, json);
            this.loggedin_profile = json.loggedin_profile;
            this.displayCalculated();
        }
    },
    displayCalculated: function() {
        this.displayPctComplete();
        this.displaySummaryPreview();
    },
    genDisplayCalculatedIfValid: function(field) {
        var self = this;
        return function(result, val) {
            var id = field.fieldBase.id;
            if (result === 0) {
                self.listing[id] = val;
                self.displayCalculated();
            }
        };
    },
    pctComplete: function() {
        var self = this,
            numprops = this.editableprops.length,
            filledprops = 0,
            missingprops = [],
            pctcomplete,
            i,
            k;
        for (i = 0; i < numprops; i++) {
            k = this.editableprops[i];
            if (this.listing[k]) {
                filledprops++
            }
            else {
                missingprops.push(k);
            }
        } 
        self.missingprops = missingprops;
        pctcomplete = Math.floor(100 * filledprops / numprops);
        return pctcomplete;
    },
    displayPctComplete: function() {
        var pctcomplete = this.pctComplete(),
            pctwidth = Math.floor(626 * pctcomplete / 100) + 'px';
        pl('#boxsteppct').text(pctcomplete);
        pl('#boxstepn').css({width: pctwidth});
    },
    validate: function() {
        var validmsg = 0,
            validmsgs = [],
            i, field, validMsg, displayName;
        for (i = 0; i < this.fields.length; i++) {
            field = this.fields[i];
            validmsg = field.validate();
            displayName = field.fieldBase.getDisplayName();
            if (validmsg !== 0) {
                validmsgs.push(displayName + ': ' + validmsg);
            }
        }
        return validmsgs;
    },
    bindNavButtons: function(nextValidator) {
        this.bindPrevButton();
        this.bindNextButton(nextValidator);
        this.bindWithdrawButton();
    },
    bindPrevButton: function() {
        var self = this;
        if (!self.prevPage) {
            return;
        }
        pl('#prevbuttonlink').bind({
            click: function() {
                document.location = self.prevPage;
                return false;
            }
        });
    },
    bindNextButton: function(nextValidator) {
        var self = this;
        if (!self.nextPage) {
            return;
        }
        pl('#nextbuttonlink').bind({
            click: function() {
                var validmsgs = nextValidator ? nextValidator() : self.validate();
                if (validmsgs.length > 0) {
                    if (!pl('#newlistingmsg').hasClass('errorcolor')) {
                        pl('#newlistingmsg').addClass('errorcolor');
                    }
                    pl('#newlistingmsg').html('Please correct: ' + validmsgs.join(' '));
                }
                else {
                    document.location = self.nextPage;
                }
                return false;
            }
        });
    },
    bindWithdrawButton: function() {
        pl('#newwithdrawbtn').bind({
            click: function() {
                var completeFunc = function() {
                        document.location = '/';
                    },
                    ajax = new AjaxClass('/listing/delete', 'newwithdrawmsg', completeFunc);
                if (pl('#newwithdrawcancelbtn').css('display') === 'none') { // first call
                    pl('#newwithdrawmsg, #newwithdrawcancelbtn').show();
                }
                else {
                    ajax.setPost();
                    ajax.call();
                }
                return false;
            }
        });
        pl('#newwithdrawcancelbtn').bind({
            click: function() {
                pl('#newwithdrawmsg, #newwithdrawcancelbtn').hide();
                return false;
            }
        });
    },
    displaySummaryPreview: function() {
        this.companyTile.display(this.listing, 'summarypreview');
    },
    getUpdater: function(fieldName, cleaner, postSuccessFunc) {
        var self = this;
        return function(newdata, loadFunc, errorFunc, successFunc) {
            var data = { listing: {} },
                newval = (newdata ? (cleaner ? cleaner(newdata.changeKey) : newdata.changeKey) : undefined),
                pctSuccessFunc = function(json) {
                    successFunc(json);
                    self.displayCalculated();
                    if (postSuccessFunc) {
                        postSuccessFunc(json);
                    }
                };
                ajax = new AjaxClass('/listing/update_field', '', null, pctSuccessFunc, loadFunc, errorFunc);
            data.listing[fieldName] = newval;
            ajax.setPostData(data);
            ajax.call();
        };
    }
});

