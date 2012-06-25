function ModelPageClass() {
    var queryString = new QueryStringClass();
    this.listing_id = queryString.vars.id;
};
pl.implement(ModelPageClass,{
    load: function() {
        var self = this,
            complete = function(json) {
                var header = new HeaderClass(),
                    companybanner = new CompanyBannerClass('model'),
                    bmc = new BMCClass();
                header.setLogin(json);
                companybanner.display(json);
                bmc.display(json.listing);
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            ajax = new AjaxClass('/listing/get/' + this.listing_id, 'bmcmsg', complete);
        ajax.call();
    }
});

(new ModelPageClass()).load();

