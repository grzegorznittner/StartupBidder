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
        return self;
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
        self.otheruserprofileid = json && json.other_user_profile && json.other_user_profile.profile_id ? json.other_user_profile.profile_id : '',
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
            message = new MessageClass(self);
            message.setEmpty();
            html += message.makeHtml();
        }
        self.bindAddBox();
        pl('#messagesend').before(html).show();
    },
    bindAddBox: function() {
        var self = this;
        if (pl('#messagetext').hasClass('bound')) {
            return;
        }
        pl('#messagetext').bind({
            focus: function() {
                if (!pl('#messagetext').hasClass('edited')) {
                    pl('#messagetext').attr({value: ''});
                    pl('#messagemsg').html('&nbsp;');
                }
            },
            keyup: function() {
                if (!pl('#messagetext').hasClass('edited')) {
                    pl('#messagetext').addClass('edited');
                    pl('#messagemsg').html('&nbsp;');
                }
                else if (pl('#messagetext').attr('value').length >= 5 && !pl('#messagebtn').hasClass('editenabled')) {
                    pl('#messagebtn').addClass('editenabled');
                }
                else if (pl('#messagetext').attr('value').length < 5 && pl('#messagebtn').hasClass('editenabled')) {
                    pl('#messagebtn').removeClass('editenabled');
                }
                return false;
            },
            blur: function() {
                if (!pl('#messagetext').hasClass('edited')) {
                    pl('#messagetext').attr({value: 'Put your message here...'});
                    pl('#messagebtn').removeClass('editenabled');
                }
            }
        });
        pl('#messagebtn').bind({
            click: function(event) {
                var completeFunc = function(json) {
                        var html = (new MessageClass()).store(json).makeHtml();
                        pl('#messagetext').removeClass('edited').attr({value: 'Put your message here...'});
                        pl('#messagebtn').removeClass('editenabled');
                        pl('#messagemsg').html('Message posted');
                        pl('#messagesend').before(html);
                    },
                    data = {
                        send: {
                            profile_id: self.otheruserprofileid,
                            text: SafeStringClass.prototype.clean(pl('#messagetext').attr('value') || '')
                        }
                    },
                    ajax = new AjaxClass('/user/send_message', 'messagemsg', completeFunc);
                if (!pl('#messagebtn').hasClass('editenabled')) {
                    return false;
                }
                ajax.setPostData(data);
                ajax.call();
                return false;
            }
        });
        pl('#messagetext').addClass('bound');
    }
});
