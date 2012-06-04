function BidsPageClass() {
    var queryString = new QueryStringClass();
    this.listing_id = queryString.vars.id;
};
pl.implement(BidsPageClass,{
    load: function() {
        var self = this,
            complete = function(json) {
                var header = new HeaderClass(),
                    companybanner = new CompanyBannerClass('bids'),
                    orderbook = new OrderBookClass(json.listing.listing_id, json.listing.suggested_amt, json.listing.suggested_pct, json.listing.listing_date);
                header.setLogin(json);
                companybanner.display(json);
                orderbook.load();
            },
            ajax = new AjaxClass('/listing/get/' + this.listing_id, 'orderbooktitlemsg', complete);
        ajax.call();
    }
});

(new BidsPageClass()).load();

