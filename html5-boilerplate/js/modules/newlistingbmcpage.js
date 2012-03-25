function NewListingBMCClass() {
    var base = new NewListingBaseClass();
    base.prevPage = '/new-listing-basics-page.html';
    base.nextPage = '/new-listing-qa-page.html';
    this.base = base;
}
pl.implement(NewListingBMCClass, {
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
        this.displayBMC();
    },
    genDisplayBMC: function(field) {
        var self = this,
            f1 = self.base.genDisplayCalculatedIfValid(field);
        return function(result, val) {
            f1();
            if (result === 0) {
                pl(field.fieldBase.sel + 'bmc').text(val);
            }
        }
    },
    displayBMC: function() {
        var n = 9,
            i,
            field,
            idx,
            val,
            sel;
        for (i = 0; i < n; i++) {
            idx = 1 + i;
            field = 'answer' + idx;
            sel = '#' + field + 'bmc';
            val = this.base.listing[field];
            pl(sel).text(val);
        }
    },
    getUpdater: function(id) {
        var self = this,
            cleaner = null,
            updateBMC = function(json) {
                var bmcsel = '#' + id + 'bmc',
                    newval = json && json.listing && (json.listing[id] !== null) ? json.listing[id] : null;
                if (newval !== null) {
                    pl(bmcsel).html(newval.replace(/\n/g, '<br/>'));
                }
            },
            baseUpdater = this.base.getUpdater(id, cleaner, updateBMC);
        return baseUpdater;
    },
    bindEvents: function() {
        var textFields = [],
            displayName = {},
            answerFields = [
                'KEY ACTIVITIES',
                'KEY RESOURCES',
                'KEY PARTNERS',
                'VALUE PROPOSITIONS',
                'CUSTOMER SEGMENTS',
                'CHANNELS',
                'CUSTOMER RELATIONSHIPS',
                'COST STRUCTURE',
                'REVENUE STREAMS'
            ],
            msgFields = [
                'infrastructuremsg',
                'infrastructuremsg',
                'infrastructuremsg',
                'offeringmsg',
                'customersmsg',
                'customersmsg',
                'customersmsg',
                'financesmsg',
                'financesmsg'
            ],
            id,
            i,
            idx,
            field;
        this.base.fields = [];
        for (i = 0; i < answerFields.length; i++) {
            idx = 1 + i;
            id = 'answer' + idx;
            textFields.push(id);
            displayName[id] = answerFields[i];
        }
        for (i = 0; i < textFields.length; i++) {
            id = textFields[i];
            field = new TextFieldClass(id, this.base.listing[id], this.getUpdater(id), msgFields[i]);
            field.fieldBase.setDisplayName(displayName[id].toUpperCase());
            field.fieldBase.addValidator(ValidatorClass.prototype.makeLengthChecker(16, 1000));
            field.fieldBase.validator.postValidator = this.genDisplayBMC(field);
            field.bindEvents({noEnterKeySubmit: true});
            this.base.fields.push(field);
        } 
        this.base.bindNavButtons();
    }
});

function NewListingPageClass() {};
pl.implement(NewListingPageClass, {
    loadPage: function() {
        var newlisting = new NewListingBMCClass();
        newlisting.load();
    }
});

(new NewListingPageClass()).loadPage();

