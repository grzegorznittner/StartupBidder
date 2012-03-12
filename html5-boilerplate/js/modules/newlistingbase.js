function NewListingBaseClass() {
    var editableprops = [
            'title', 'category', 'mantra', 'website', 'address',
            'summary', 'answer1', 'answer2', 'answer3', 'answer4', 'answer5', 'answer6', 'answer7', 'answer8', 'answer9', 'answer10', 'answer11', 'answer12', 'answer13',
            'suggested_amt', 'suggested_pct', 'presentation_id', 'business_plan_id', 'financials_id'
        ],
        companyTile = new CompanyTileClass({preview: true});
    this.editableprops = editableprops;
    this.companyTile = companyTile;
    this.listing = {};
    this.profile = {};
    this.prevPage = '';
    this.nextPage = '';
};
pl.implement(NewListingBaseClass, {
    store: function(json) {
        var self = this;
        if (json) {
            CollectionsClass.prototype.merge(this.listing, json);
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
    displayPctComplete: function() {
        var numprops = this.editableprops.length,
            filledprops = 0,
            pctcomplete,
            i,
            k;
        for (i = 0; i < numprops; i++) {
            k = this.editableprops[i];
            if (this.listing[k]) {
                filledprops++
            }
        } 
        pctcomplete = Math.floor(100 * filledprops / numprops);
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
    bindNavButtons: function() {
        this.bindPrevButton();
        this.bindNextButton();
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
    bindNextButton: function() {
        var self = this;
        if (!self.nextPage) {
            return;
        }
        pl('#nextbuttonlink').bind({
            click: function() {
                var validmsgs = self.validate();
                if (validmsgs.length > 0) {
                    pl('#newlistingmsg').addClass('errorcolor').html('Please correct: ' + validmsgs.join(' '));
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
    getUpdater: function(fieldName, cleaner) {
        var self = this;
        return function(newdata, loadFunc, errorFunc, successFunc) {
            var data = { listing: {} },
                newval = (newdata ? (cleaner ? cleaner(newdata.changeKey) : newdata.changeKey) : undefined),
                pctSuccessFunc = function(json) {
                    successFunc(json);
                    self.displayCalculated();
                }
                ajax = new AjaxClass('/listing/update_field', '', null, pctSuccessFunc, loadFunc, errorFunc);
            data.listing[fieldName] = newval;
            ajax.setPostData(data);
            ajax.call();
        };
    }
});

