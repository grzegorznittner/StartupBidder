function NewListingSubmittedClass() {
    var base = new NewListingBaseClass();
    this.base = base;
}
pl.implement(NewListingSubmittedClass, {
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
        if (this.base.listing.status === 'new') {
            document.location = 'new-listing-submit-page.html';
        }
    }
});

function NewListingPageClass() {};
pl.implement(NewListingPageClass, {
    loadPage: function() {
        var newlisting = new NewListingSubmittedClass();
        newlisting.load();
    }
});

(new NewListingPageClass()).loadPage();

