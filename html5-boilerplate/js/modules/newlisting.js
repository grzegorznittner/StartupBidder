function NewListingClass() {
    var self = this;
    self.statusId = 'newlistingmsg';
    self.completeFunc = function(json) {
        var header = new HeaderClass();
        header.setLogin(json);
        self.store(json);
        self.display();
    };
    self.ajax = new AjaxClass('/listings/create', self.statusId, self.completeFunc);
};
pl.implement(NewListingClass, {
    load: function() {
        var self = this;
        self.ajax.setPostData({
        });
        self.ajax.call();
    },
    store: function(json) {
        var self = this;
        if (json && json.listing) {
            self.listing = json.listing;
            pl('#'+self.statusId).html('Listing loaded');
        }
    },
    display: function() {
    }
});
