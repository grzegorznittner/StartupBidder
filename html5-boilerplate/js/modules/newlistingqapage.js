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
        if (!this.bound) {
            this.bindEvents();
            this.bound = true;
        }
    },
    bindEvents: function() {
        var textFields = ['summary', 'answer1', 'answer2', 'answer3', 'answer4', 'answer5', 'answer6', 'answer7', 'answer8', 'answer9', 'answer10', 'answer11', 'answer12', 'answer13'],
            displayName = {
                'summary': 'elevator pitch',
                'answer1': 'customer',
                'answer2': 'problem',
                'answer3': 'solution',
                'answer4': 'payment',
                'answer5': 'marketing',
                'answer6': 'advertising',
                'answer7': 'team',
                'answer8': 'competitors',
                'answer9': 'relocation',
                'answer10': 'investment',
                'answer11': 'sourcing',
                'answer12': 'property',
                'answer13': 'methodology'
            },
            id,
            field;
        this.base.fields = [];
        for (i = 0; i < textFields.length; i++) {
            id = textFields[i];
            field = new TextFieldClass(id, this.base.listing[id], this.base.getUpdater(id), 'newlistingmsg');
            field.fieldBase.setDisplayName(displayName[id].toUpperCase());
            field.fieldBase.addValidator(ValidatorClass.prototype.makeLengthChecker(5, 2000));
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

