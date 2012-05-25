function MessageGroupClass() {}
pl.implement(MessageGroupClass, {
    store: function(json) {
        var self = this;
        for (k in json) {
            self[k] = json[k];
        }
        self.messagetext = self.last_text ? SafeStringClass.prototype.htmlEntities(self.last_text) : '';
        self.messageclass = self.read ? '' : ' inputmsg'; // unread
        self.datetext = self.last_date ? DateClass.prototype.format(self.last_date) : '';
        self.url = self.from_user_id ? '/messages-page.html?from_user_id=' + self.from_user_id + '&from_user_nickname=' + encodeURIComponent(self.from_user_nickname) : '';
        self.openanchor = self.url ? '<a href="' + self.url + '" class="hoverlink' + self.messageclass + '">' : '';
        self.closeanchor = self.from_user_id ? '</a>' : '';
    },
    setEmpty: function() {
        var self = this,
            emptyJson = {
                from_user_id: null,
                from_user_nickname: '',
                last_text: 'You currently have no messages.',
                last_date: null,
                read: true
            };
        self.store(emptyJson);
    },
    makeHtml: function() {
        var self = this;
        return '\
        <div class="messageline">\
            <p class="messageuser span-4">' + self.from_user_nickname + '</p>\
            <p class="messagetext span-14 darkblue">\
                '+self.openanchor+'\
                '+self.messagetext+'\
                '+self.closeanchor+'\
            </p>\
            <p class="messagedate">'+self.datetext+'</p>\
        </div>\
        ';
    }
});

function MessageGroupListClass() {}
pl.implement(MessageGroupListClass, {
    store: function(json) {
        var self = this,
            jsonlist = json && json.users ? json.users : [],
            message,
            i;
        self.messages = [];
        if (jsonlist.length) {
            for (i = 0; i < jsonlist.length; i++) {
                message = new MessageGroupClass();
                message.store(jsonlist[i]);
                self.messages.push(message);
            }
        }
        else {
            message = new MessageGroupClass();
            message.setEmpty();
            self.messages.push(message);
        }
    },
    display: function(json) {
        var self = this,
            html = '',
            i,
            message;
        if (json !== undefined) {
            self.store(json);
        }
        for (i = 0; i < self.messages.length; i++) {
            message = self.messages[i];
            html += message.makeHtml();
        }
        if (!self.messages.length) {
            message = new MessageGroupClass();
            message.setEmpty();
            html += message.makeHtml();
        }
        pl('#messagegrouplist').html(html);
    }
});
