function NotificationClass() {}
pl.implement(NotificationClass, {
    store: function(json) {
        var self = this;
        for (k in json) {
            self[k] = json[k];
        }
        self.createddate = self.create_date ? DateClass.prototype.format(self.create_date) : '';
        self.message = self.text ? SafeStringClass.prototype.htmlEntities(self.text) : '';
        self.messageclass = self.read ? '' : ' inputmsg'; // unread
        self.listingurl = self.listing && self.listing.listing_id ? '/company-page.html?id=' + self.listing.listing_id : ''; // FIXME
        self.listingtext = self.listing && self.listing.title ? SafeStringClass.prototype.htmlEntities(self.listing.title) : ''; // FIXME
        if (!self.notify_type) {
            self.type = 'notification';
        }
        else if (self.notify_type.match('comment')) {
            self.type = 'comment';
        }
        else if (self.notify_type.match('bid')) {
            self.type = 'bid';
        }
        else {
            self.type = 'notification';
        }
        self.openanchor = self.link ? '<a href="' + self.link + '" class="hoverlink' + self.messageclass + '">' : '';
        self.closeanchor = self.link ? '</a>' : '';
    },
    setTest: function() {
        var self = this,
            testJson = {
                url: '/notification-page.html?id=fubar',
                type: 'comment',
                text: 'You received a comment on foo',
                date: '201204121344',
                listing: {
                    listing_id: 'ag1zdGFydHVwYmlkZGVycg4LEgdMaXN0aW5nGOAPDA',
                    title: 'Computer Training Camp'
                }
            };
        self.store(testJson);
    },
    setEmpty: function() {
        var self = this,
            emptyJson = {
                create_date: null,
                text: 'You currently have no notifications.',
                read: true,
                notify_type: 'notification',
                link: null,
                listing: null
            };
        self.store(emptyJson);
    },
    makeHtml: function() {
        var self = this,
            listinghtml = self.listingurl && self.listingtext ? '<span class="sideboxlisting"><a href="'+self.listingurl+'" class="hoverlink">'+self.listingtext+'</a></span>' : ''; // FIXME
        return '\
        <div class="sideboxnotify sideboxlink">\
            <span class="sideboxicon" style="overflow:visible;">\
                <div class="'+self.type+'icon" style="overflow:visible;"></div>\
            </span>\
            <span class="sideboxnotifytext">\
                '+self.openanchor+'\
                '+self.message+'\
                '+self.closeanchor+'\
                <br/>\
                <span class="sideboxdate">'+self.createddate+'</span>\
            </span>\
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
        pl('#notifylist').html(html);
    }
});
