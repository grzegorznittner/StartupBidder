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
        self.date = self.create_date || self.sent_date;
        self.datetext = self.date ? DateClass.prototype.format(self.date) : '';
        self.openanchor = self.link ? '<a href="' + self.link + '" class="hoverlink notifylink' + self.messageclass + '">' : '';
        self.closeanchor = self.link ? '</a>' : '';
    },
    setEmpty: function() {
        var self = this,
            emptyJson = {
                notify_type: 'notification',
                title: 'You currently have no notifications.',
                text_1: null,
                create_date: null,
                sent_date: null,
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
        self.more_results_url = self.notifications.length > 0 && json.notifications_props && json.notifications_props.more_results_url;
    },
    bindMoreResults: function() {
        var self = this;
        pl('#moreresults').bind({
            click: function() {
                var completeFunc = function(json) {
	                    var self = this,
	                    html = '',
	                    i,
	                    jsonlist = json && json.notifications ? json.notifications : [],
	                    notification;
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
		                for (i = 0; i < self.notifications.length; i++) {
		                    notification = self.notifications[i];
		                    html += notification.makeHtml();
		                }
		                if (!self.notifications.length) {
		                    notification = new NotificationClass();
		                    notification.setEmpty();
		                    html += notification.makeHtml();
		                }
		                self.more_results_url = self.notifications.length > 0 && json.notifications_props && json.notifications_props.more_results_url;
                        
	                    if (html) {
                            pl('#moreresults').before(html);
                        }
                        if (self.more_results_url) {
                            pl('#moreresultsurl').text(self.more_results_url);
                            pl('#moreresultsmsg').text('More...');
                        }
                        else {
                            pl('#moreresultsmsg').text('');
                            pl('#moreresults').removeClass('hoverlink').unbind();
                        }
                    },
                    more_results_url = pl('#moreresultsurl').text(),
                    ajax,
                    data,
                    i;
                if (more_results_url) {
                    ajax = new AjaxClass(more_results_url, 'moreresultsmsg', completeFunc);
                    ajax.setGetData(data);
                    ajax.call();
                }
                else {
                    pl('#moreresultsmsg').text('');
                    pl('#moreresults').removeClass('hoverlink').unbind();
                }
            }
        });
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
        if (self.more_results_url) {
        	html += '<div class="showmore hoverlink" id="moreresults"><span class="initialhidden" id="moreresultsurl">' + self.more_results_url + '</span><span id="moreresultsmsg">More...</span></div>\n';
        }
        pl('#notifylist').html(html);
        if (self.more_results_url) {
            this.bindMoreResults();
        }
    }
});
