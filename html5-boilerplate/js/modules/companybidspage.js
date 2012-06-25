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
                    orderbook = new OrderBookClass(json.listing.listing_id);
                header.setLogin(json);
                companybanner.display(json);
                orderbook.display(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            ajax = new AjaxClass('/listing/order_book/' + this.listing_id, 'orderbooktitlemsg', complete);
        ajax.call();
    }
});

(new BidsPageClass()).load();

