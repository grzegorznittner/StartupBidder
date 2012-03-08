function NewListingFinancialsClass() {
    var base = new NewListingBaseClass();
    base.prevPage = '/new-listing-qa-page.html';
    base.nextPage = '/new-listing-media-page.html';
    this.base = base;
}
pl.implement(NewListingFinancialsClass, {
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
        this.base.bindNavButtons();
    }
});

function NewListingPageClass() {};
pl.implement(NewListingPageClass, {
    loadPage: function() {
        var newlisting = new NewListingFinancialsClass();
        newlisting.load();
    }
});

(new NewListingPageClass()).loadPage();

