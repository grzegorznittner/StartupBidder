function NewListingSubmitClass() {
    var base = new NewListingBaseClass();
    base.prevPage = '/new-listing-media-page.html';
    base.nextPage = '/new-listing-submitted-page.html';
    this.base = base;
}
pl.implement(NewListingSubmitClass, {
    load: function() {
        var self = this,
            completeFunc = function(json) {
                var listing = json && json.listing ? json.listing : {},
                    header = new HeaderClass();
                header.setLogin(json);
                self.base.store(listing);
                self.display();
            },
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
    postListing: function() {
        var self = this,
            completeFunc = function(json) {
                document.location = self.base.nextPage;
            },
            // FIXME - add error class to handle error_msg field
            ajax = new AjaxClass('/listing/post', 'newlistingmsg', completeFunc);
        ajax.setPost();
        ajax.call();
    },
    bindEvents: function() {
        var self = this,
            submitValidator = function() {
                var msgs = [],
                    pctcomplete = self.base.pctComplete();
                if (pctcomplete !== 100) {
                    msgs.push('You must complete all fields before submitting, missing fields: ' + self.base.missingprops.join(', '));
                }
                else {
                    msgs.push('Submitting listing...');
                    self.postListing();
                }
                return msgs;
            },
            previewurl = '/company-page.html?id=' + self.base.listing.listing_id + '&preview=true';
        pl('#previewiframe').attr({src: previewurl});
        this.base.bindNavButtons(submitValidator);
    }
});

function NewListingPageClass() {};
pl.implement(NewListingPageClass, {
    loadPage: function() {
        var newlisting = new NewListingSubmitClass();
        newlisting.load();
    }
});

(new NewListingPageClass()).loadPage();

