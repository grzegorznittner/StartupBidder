function NewListingBMCClass() {
    var base = new NewListingBaseClass();
    base.prevPage = '/new-listing-media-page.html';
    base.nextPage = '/new-listing-qa-page.html';
    this.base = base;
    this.bmc = new BMCClass();
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
        this.bmc.display(this.base.listing);
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
            field = new TextFieldClass(id, this.base.listing[id], this.base.getUpdater(id, null, this.bmc.getUpdater(id)), msgFields[i]);
            if (this.base.displayNameOverrides[id]) {
                field.fieldBase.setDisplayName(this.base.displayNameOverrides[id]);
            }
            field.fieldBase.addValidator(ValidatorClass.prototype.makeLengthChecker(16, 1000));
            field.fieldBase.validator.postValidator = this.bmc.genDisplay(field, this.base.genDisplayCalculatedIfValid(field));
            field.bindEvents({noEnterKeySubmit: true});
            this.base.fields.push(field);
        } 
        this.base.bindInfoButtons();
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

