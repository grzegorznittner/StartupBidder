function ListingPageClass() {
    if (!this.queryString) {
        this.queryString = new QueryStringClass();
        this.queryString.load();
    }
    this.id = this.queryString.vars.id;
};
pl.implement(ListingPageClass,{
    loadPage: function() {
        var listing, comments, companies;
        listing = new ListingClass(this.id);
        comments = new CommentsClass(this.id);
        companies = new RelatedCompaniesClass(this.id);
        listing.load();
        comments.load();
        companies.load();
    }
});

(new ListingPageClass()).loadPage();

