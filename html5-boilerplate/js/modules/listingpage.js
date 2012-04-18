function ListingPageClass() {
    var queryString = new QueryStringClass();
    this.id = queryString.vars.id;
    this.preview = queryString.vars.preview;
};
pl.implement(ListingPageClass,{
    loadPage: function() {
        if (this.preview) {
            this.loadPreview();
        }
        else {
            this.loadRegular();
        }
    },
    loadPreview: function() {
        var listing = new ListingClass(this.id, this.preview);
        pl('#header').hide();
        pl('#footer').hide();
        listing.load();
    },
    loadRegular: function() {
        var listing = new ListingClass(this.id);
        listing.load();
    }
});

(new ListingPageClass()).loadPage();

