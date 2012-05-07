function NotificationClass() {}
pl.implement(NotificationClass, {
    load: function() {
        var self = this,
            queryString = new QueryStringClass(),
            id = queryString.vars.id,
            completeFunc = function(json) {
                self.store(json);
                self.setPageHtml();
            },
            ajax = new AjaxClass('/notifications/get/' + id, 'notification_message', completeFunc);
        if (id) {
            ajax.call();
        }
        else {
            self.setNotFound();
            self.setPageHtml();
        }
    },
    store: function(json) {
        var self = this;
        for (k in json) {
            self[k] = json[k];
        }
        self.createddate = self.create_date ? DateClass.prototype.format(self.create_date) : '';
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
        else {
            self.type = 'notification';
        }
        self.datetext = self.created_date ? 'Sent on ' + DateClass.prototype.format(self.created_date) : '';
        self.openanchor = self.notify_id ? '<a href="/notification-page.html?id=' + self.notify_id + '" class="hoverlink' + self.messageclass + '">' : '';
        self.closeanchor = self.notify_id ? '</a>' : '';
    },
    setPageHtml: function() {
        var self = this,
            listing = {},
            tile = new CompanyTileClass({ companybannertileclass: 'companybannertilenoborder' });
        pl('#notification_title').text(self.title);
        pl('#notification_date').text(self.datetext);
        pl('#notification_text_2').html(self.text_2||'');
        if (self.listing_id) {
            listing.listing_id = self.listing_id;
            listing.logo = self.listing_logo_url;
            listing.category = self.listing_category;
            listing.title = self.listing_name;
            listing.brief_address = self.listing_brief_address;
            listing.mantra = self.listing_mantra;
            /* self.listing_owner (user id) */
            tile.store(listing);
            //pl('#notification_header').text('NOTIFICATION FOR ' + listing.title.toUpperCase());
            pl('#notification_listing').html(tile.makeFullWidthHtml());
        }
    },
    setEmpty: function() {
        var self = this,
            emptyJson = {
                title: 'You currently have no notifications.',
                read: true,
                notify_type: 'notification',
                notify_id: 0
            };
        self.store(emptyJson);
    },
    setNotFound: function() {
        var self = this,
            emptyJson = {
                title: 'You must pass a notification ID.',
                read: true,
                notify_type: 'notification',
                notify_id: 0
            };
        self.store(emptyJson);
    },
    makeHtml: function() {
        var self = this;
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
