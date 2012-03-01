function NewListingClass() {
    var queryString = new QueryStringClass(),
        virtualProfile = { // FIXME
            loggedin_profile: {
                profile_id: queryString.vars.profile_id,
                username: queryString.vars.username
            }
        },
        listing = {
            title: 'Listing title',
            median_valuation: 0,
            num_votes: 0,
            num_bids: 0,
            num_comments: 0,
            profile_id: queryString.vars.profile_id,
            profile_username: queryString.vars.username,
            listing_date: DateClass.prototype.today(),
            closing_date: DateClass.prototype.todayPlus(30),
            status: 'new',
            suggested_amt: 10000,
            suggested_pct: 10,
            suggested_val: 100000,
            summary: 'Enter listing summary here.',
            business_plan_url: '',
            presentation_url: ''
        },
        header = new HeaderClass();
    this.listing = listing;
    header.setLogin(virtualProfile);
};
pl.implement(NewListingClass, {
    load: function() {
        var self = this,
            completeFunc = function(json) {
                //var header = new HeaderClass();
                // header.setLogin(json); // FIXME
                self.store(json);
                self.display();
            },
            ajax = new AjaxClass('/listings/create', 'newlistingmsg', completeFunc);
        ajax.setPostData({listing: this.listing});
        ajax.call();
    },
    store: function(json) {
        var self = this;
        if (json && json.listing) {
            this.listing = CollectionsClass.prototype.merge(this.listing, json.listing);
            pl('#newlistingmsg').html('Listing loaded');
        }
    },
    display: function() {
    }
});

