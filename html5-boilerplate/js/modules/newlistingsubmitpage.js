function NewListingSubmitClass() {
    var base = new NewListingBaseClass();
    base.prevPage = '/new-listing-financials-page.html';
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
            ajax = new AjaxClass('/listing/post', 'newlistingmsg', completeFunc);
        ajax.setPost();
        ajax.call();
    },
    highlightMissing: function() {
        var self = this,
            msg = '',
            msgs = [],
            errorpages = {},
            missing,
            displayName,
            page,
            i;
        for (i = 0; i < self.base.missingprops.length; i++) {
            missing = self.base.missingprops[i];
            page = self.base.proppage[missing];
            if (!errorpages[page]) {
                errorpages[page] = [];
            }
            displayName = self.base.displayNameOverrides[missing] || missing.toUpperCase();
            errorpages[page].push(displayName);
        }
        for (i = 0; i < self.base.pages.length; i++ ) {
            page = self.base.pages[i];
            self.highlightPage(page, errorpages[page]);
        }
        for (page in errorpages) {
            msg = page.toUpperCase() + ' page: ' + errorpages[page].join(', ');
            msgs.push(msg);
        }
        return msgs.join('; ');
    },
    highlightPage: function(page, iserror) {
        var boxsel = '#' + page + 'boxstep';
        if (iserror && !pl(boxsel).hasClass('boxsteperror')) {
            pl(boxsel).addClass('boxsteperror');
        }
        else if (!iserror && pl(boxsel).hasClass('boxsteperror')) {
            pl(boxsel).removeClass('boxsteperror');
        }
    },
    bindEvents: function() {
        var self = this,
            submitValidator = function() {
                var msg,
                    msgs = [],
                    pctcomplete = self.base.pctComplete();
                if (pctcomplete !== 100) {
                    msg = self.highlightMissing();
                    msgs.push('Missing info: ' + msg);
                }
                else {
                    msgs.push('Submitting listing...');
                    self.postListing();
                }
                return msgs;
            },
            previewurl = '/company-preview-page.html?id=' + self.base.listing.listing_id + '&preview=true';
        pl('#previewiframe').attr({src: previewurl});
        this.base.bindNavButtons(submitValidator);
        this.base.bindTitleInfo();
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

