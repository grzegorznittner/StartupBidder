function NewListingQAClass() {
    var base = new NewListingBaseClass();
    base.prevPage = '/new-listing-basics-page.html';
    base.nextPage = '/new-listing-financials-page.html';
    this.base = base;
}
pl.implement(NewListingQAClass, {
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
    },
    bindEvents: function() {
        var textFields = ['summary'],
            displayName = { summary: 'ELEVATOR PITCH' },
            answerFields = [
'CUSTOMER',
'PROBLEM',
'SOLUTION',
'INNOVATION',
'PAYMENT',
'EXPENSES',
'STICKINESS',
'MARKETING',
'ADVERTISING',
'AFFILIATES',
'VENDORS',
'COMPETITORS',
'COMPETITIVE COMPARISON',
'INTELLECTUAL PROPERTY',
'STATUS',
'PLAN OF ATTACK',
'KEY ACTIVITIES',
'GOVERNMENT',
'TEAM',
'TEAM RELOCATION',
'TEAM VALUES',
'FINANCIAL PROJECTIONS',
'CURRENT FINANCIALS',
'OWNERS',
'INVESTMENT',
'TIMELINE AND WRAPUP'
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
            field = new TextFieldClass(id, this.base.listing[id], this.base.getUpdater(id), 'newlistingmsg');
            field.fieldBase.setDisplayName(displayName[id].toUpperCase());
            field.fieldBase.addValidator(ValidatorClass.prototype.makeLengthChecker(5, 1000));
            field.fieldBase.validator.postValidator = this.base.genDisplayCalculatedIfValid(field);
            field.bindEvents();
            this.base.fields.push(field);
        } 
        this.base.bindNavButtons();
    }
});

function NewListingPageClass() {};
pl.implement(NewListingPageClass, {
    loadPage: function() {
        var newlisting = new NewListingQAClass();
        newlisting.load();
    }
});

(new NewListingPageClass()).loadPage();

