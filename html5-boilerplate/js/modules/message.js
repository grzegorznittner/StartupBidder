function MessageClass() {}
pl.implement(MessageClass, {
    store: function(json) {
        var self = this;
        for (k in json) {
            self[k] = json[k];
        }
        self.messagetext = self.last_text ? SafeStringClass.prototype.htmlEntities(self.last_text) : '';
        self.messageclass = self.read ? '' : ' inputmsg'; // unread
        self.datetext = self.last_date ? DateClass.prototype.format(self.last_date) : '';
        self.openanchor = self.from_user_id ? '<a href="/message_thread_page.html?from_user_id=' + self.from_user_id + '" class="hoverlink' + self.messageclass + '">' : '';
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
            <p class="messagetext span-14">\
                '+self.openanchor+'\
                '+self.messagetext+'\
                '+self.closeanchor+'\
            </p>\
            <p class="messagedate">'+self.datetext+'</p>\
        </div>\
        ';
    }
});

function MessageListClass() {}
pl.implement(MessageListClass, {
    store: function(json) {
        var self = this,
            jsonlist = json && json.users ? json.users : [],
            message,
            i;
        self.messages = [];
        if (jsonlist.length) {
            for (i = 0; i < jsonlist.length; i++) {
                message = new MessageClass();
                message.store(jsonlist[i]);
                self.messages.push(message);
            }
        }
        else {
            message = new MessageClass();
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
            message = new MessageClass();
            message.setEmpty();
            html += message.makeHtml();
        }
        pl('#messagelist').html(html);
    }
});
