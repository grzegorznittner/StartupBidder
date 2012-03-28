function NewListingBMCClass() {
    var base = new NewListingBaseClass();
    base.prevPage = '/new-listing-media-page.html';
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
            m = 1,
            n = 9,
            i,
            id,
            field;
        this.base.fields = [];
        for (i = m; i <= n; i++) {
            id = 'answer' + i;
            textFields.push(id);
        }
        for (i = 0; i < textFields.length; i++) {
            id = textFields[i];
            field = new TextFieldClass(id, this.base.listing[id], this.getUpdater(id), msgFields[i]);
            if (this.base.displayNameOverrides[id]) {
                field.fieldBase.setDisplayName(this.base.displayNameOverrides[id]);
            }
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

