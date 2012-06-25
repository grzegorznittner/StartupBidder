function SlidesPageClass() {
    var queryString = new QueryStringClass();
    this.listing_id = queryString.vars.id;
};
pl.implement(SlidesPageClass,{
    load: function() {
        var self = this,
            complete = function(json) {
                var header = new HeaderClass(),
                    companybanner = new CompanyBannerClass('slides'),
                    ip = new IPClass();
                header.setLogin(json);
                companybanner.display(json);
                ip.display(json.listing);
                ip.bindButtons();
                pl('.preloader').hide();
                pl('#ip, .wrapper').show();
            },
            ajax = new AjaxClass('/listing/get/' + this.listing_id, 'ipmsg', complete);
        ajax.call();
    }
});

(new SlidesPageClass()).load();

