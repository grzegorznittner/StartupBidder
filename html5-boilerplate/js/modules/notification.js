function NotificationClass() {}
pl.implement(NotificationClass, {
    store: function(json) {
        var self = this;
        for (k in json) {
            self[k] = json[k];
        }
        self.openanchor = self.url ? '<a href="' + self.url + '" class="hoverlink">' : '';
        self.closeanchor = self.url ? '</a>' : '';
        self.createddate = self.date ? DateClass.prototype.format(self.date) : '';
        self.message = self.text ? SafeStringClass.prototype.htmlEntities(self.text) : '';
        self.listingurl = self.listing && self.listing.listing_id ? '/company-page.html?id=' + self.listing.listing_id : '';
        self.listingtext = self.listing && self.listing.title ? SafeStringClass.prototype.htmlEntities(self.listing.title) : '';
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
                url: null,
                type: 'comment',
                text: 'You currently have no notifications.',
                date: ''
            };
        self.store(emptyJson);
    },
    makeHtml: function() {
        var self = this,
            listinghtml = self.listingurl && self.listingtext ? '<span class="sideboxlisting"><a href="'+self.listingurl+'" class="hoverlink">'+self.listingtext+'</a></span>' : '';
        return '\
        <div class="sideboxnotify sideboxlink">\
            <span class="sideboxicon">\
                <div class="'+self.type+'icon"></div>\
            </span>\
            <span class="sideboxnotifytext">\
                '+self.openanchor+'\
                '+self.message+'\
                '+self.closeanchor+'\
                <br/>\
                <span class="sideboxdate">'+self.createddate+'</span>\
                '+listinghtml+'\
            </span>\
        </div>\
        ';
    }
});

function NotifyListClass() {}
pl.implement(NotifyListClass, {
    store: function(json) {
        var self = this,
            jsonlist = json && json.notifications && json.notifications.length > 0 ? json.notifications : [],
            notification,
            i;
        self.notifications = [];
        if (jsonlist.length === 0) {
            notification = new NotificationClass();
            //notification.setEmpty();
            notification.setTest(); // FIXME test
            self.notifications.push(notification);
        }
        else {
            for (i = 0; i < jsonlist.length; i++) {
                notification = new NotificationClass();
                notification.store(jsonlist[i]);
                self.notifications.push(notification);
            }
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
