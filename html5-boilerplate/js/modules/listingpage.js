function ListingPageClass() {
    var queryString = new QueryStringClass();
    this.id = queryString.vars.id;
};
pl.implement(ListingPageClass,{
    loadPage: function() {
        var listing = new ListingClass(this.id),
            comments = new CommentsClass(this.id),
            companies = new RelatedCompaniesClass(this.id);
        listing.load();
        comments.load();
        companies.load();
    }
});

(new ListingPageClass()).loadPage();

