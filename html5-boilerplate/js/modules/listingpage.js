function ListingPageClass() {
    var queryString = new QueryStringClass();
    this.id = queryString.vars.id;
    this.preview = queryString.vars.preview;
};
pl.implement(ListingPageClass,{
    loadPage: function() {
        console.log('preview',this.preview);
        if (this.preview) {
            this.loadPreview();
        }
        else {
            this.loadRegular();
        }
    },
    loadPreview: function() {
        var listing = new ListingClass(this.id, this.preview);
        pl('#company-header').hide();
        listing.load();
    },
    loadRegular: function() {
        var listing = new ListingClass(this.id),
            comments = new CommentsClass(this.id),
            companies = new RelatedCompaniesClass(this.id);
        listing.load();
        comments.load();
        companies.load();
    }
});

(new ListingPageClass()).loadPage();

