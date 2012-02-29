function NewListingPageClass() {};
pl.implement(NewListingPageClass,{
    loadPage: function() {
        var newlisting = new NewListingClass();
        newlisting.load();
    }
});

(new NewListingPageClass()).loadPage();

