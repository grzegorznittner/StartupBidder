function ListingPageClass() {
    if (!this.queryString) {
        this.queryString = new QueryStringClass();
        this.queryString.load();
    }
    this.id = this.queryString.vars.id;
};
pl.implement(ListingPageClass,{
    loadPage: function() {
        var listing, bids, comments;
        listing = new ListingClass(this.id);
        //bids = new BidsClass(this.id);
        comments = new CommentsClass(this.id);
        companies = new RelatedCompaniesClass(this.id);
        listing.load();
        //bids.load();
        comments.load();
        companies.load();
    }
});

(new ListingPageClass()).loadPage();

