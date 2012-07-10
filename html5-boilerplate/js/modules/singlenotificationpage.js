function SingleNotificationPageClass() {
    var queryString = new QueryStringClass();
    this.id = queryString.vars.id;
    this.notification_id = this.id;
    this.json = {};
};
pl.implement(SingleNotificationPageClass,{
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass(),
                    notification = new NotificationClass();
                header.setLogin(json);
                notification.display(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
             },
            ajax = new AjaxClass('/notification/get/' + this.notification_id, 'notificationmsg', completeFunc);
        ajax.call();
    }
});

(new SingleNotificationPageClass()).loadPage();
