function MessageClass(messagelist) {
    this.messagelist = messagelist;
}
pl.implement(MessageClass, {
    store: function(json) {
        var self = this;
        for (k in json) {
            self[k] = json[k];
        }
        self.messagetext = self.text ? SafeStringClass.prototype.htmlEntities(self.text) : '';
        self.datetext = self.create_date ? DateClass.prototype.format(self.create_date) : '';
        self.usertext = self.direction === 'sent' ? self.messagelist.myusername : self.messagelist.otherusername;
    },
    setEmpty: function() {
        var self = this,
            emptyJson = {
                direction: 'received',
                text: 'You currently have no messages with this user.',
                create_date: null
            };
        self.store(emptyJson);
    },
    makeHtml: function() {
        var self = this;
        return '\
        <div class="messageline">\
            <p class="messageuser span-4">' + self.usertext + '</p>\
            <p class="messagetext span-14">\
                '+self.messagetext+'\
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
            jsonlist = json && json.messages ? json.messages : [],
            message,
            i;
        self.myusername = json && json.loggedin_profile && json.loggedin_profile.username ? json.loggedin_profile.username : '',
        self.otherusername = json && json.other_user_profile && json.other_user_profile.username ? json.other_user_profile.username : '',
        self.messages = [];
        if (jsonlist.length) {
            for (i = 0; i < jsonlist.length; i++) {
                message = new MessageClass(this);
                message.store(jsonlist[i]);
                self.messages.push(message);
            }
        }
        else {
            message = new MessageClass(this);
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
        pl('#myusername').text(self.myusername || 'You');
        for (i = 0; i < self.messages.length; i++) {
            message = self.messages[i];
            html += message.makeHtml();
        }
        if (!self.messages.length) {
            message = new MessageClass();
            message.setEmpty();
            html += message.makeHtml();
        }
        pl('#messagesreply').before(html);
    }
});
