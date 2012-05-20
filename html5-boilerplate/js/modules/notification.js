function NotificationClass() {}
pl.implement(NotificationClass, {
    store: function(json) {
        var self = this;
        for (k in json) {
            self[k] = json[k];
        }
        self.message = self.title ? SafeStringClass.prototype.htmlEntities(self.title) : '';
        self.messageclass = self.read ? '' : ' inputmsg'; // unread
        if (!self.notify_type) {
            self.type = 'notification';
        }
        else if (self.notify_type.match('comment')) {
            self.type = 'comment';
        }
        else if (self.notify_type.match('bid')) {
            self.type = 'bid';
        }
        else if (self.notify_type.match('ask_listing_owner')) {
            self.type = 'ask_listing_owner';
        }
        else if (self.notify_type.match('private_message')) {
            self.type = 'private_message';
        }
        else {
            self.type = 'notification';
        }
        self.datetext = self.create_date ? DateClass.prototype.format(self.create_date) : '';
        self.openanchor = self.link ? '<a href="' + self.link + '" class="hoverlink' + self.messageclass + '">' : '';
        self.closeanchor = self.link ? '</a>' : '';
    },
    setEmpty: function() {
        var self = this,
            emptyJson = {
                notify_type: 'notification',
                title: 'You currently have no notifications.',
                text_1: null,
                create_date: null,
                read: true,
                link: null
            };
        self.store(emptyJson);
    },
    makeHtml: function() {
        var self = this;
        return '\
        <div class="notifyline">\
            <span class="sideboxicon" style="overflow:visible;">\
                <div class="'+self.type+'icon" style="overflow:visible;"></div>\
            </span>\
            <span class="notifytext">\
                '+self.openanchor+'\
                '+self.message+'\
                '+self.closeanchor+'\
            </span>\
            <span class="notifydate">'+self.datetext+'</span>\
        </div>\
        ';
    }
});

function NotifyListClass() {}
pl.implement(NotifyListClass, {
    store: function(json) {
        var self = this,
            jsonlist = json && json.notifications ? json.notifications : [],
            notification,
            i;
        self.notifications = [];
        if (jsonlist.length) {
            for (i = 0; i < jsonlist.length; i++) {
                notification = new NotificationClass();
                notification.store(jsonlist[i]);
                self.notifications.push(notification);
            }
        }
        else {
            notification = new NotificationClass();
            notification.setEmpty();
            self.notifications.push(notification);
        }
    },
    display: function(json) {
        var self = this,
            html = '',
            i,
            notification;
        if (json !== undefined) {
            self.store(json);
        }
        for (i = 0; i < self.notifications.length; i++) {
            notification = self.notifications[i];
            html += notification.makeHtml();
        }
        if (!self.notifications.length) {
            notification = new NotificationClass();
            notification.setEmpty();
            html += notification.makeHtml();
        }
        pl('#notifylist').html(html);
    }
});
