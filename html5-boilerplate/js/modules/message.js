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
            for (i = jsonlist.length - 1; i >= 0 ; i--) {
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
        self.more_results_url = self.messages.length > 0 && json.messages_props && json.messages_props.more_results_url;
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
        if (self.more_results_url) {
        	html += '<div class="showmore hoverlink" id="moreresults"><span class="initialhidden" id="moreresultsurl">' + self.more_results_url + '</span><span id="moreresultsmsg">Earlier messages...</span></div>\n';
        }
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
        if (self.more_results_url) {
            self.bindMoreResults();
        }
    },
    bindMoreResults: function() {
        var self = this;
        pl('#moreresults').bind({
            click: function() {
            	var completeFunc = function(json) {
		            	var self = this,
		                jsonlist = json && json.messages ? json.messages : [],
		                html = '',
		                message,
		                i;
			            self.myusername = json && json.loggedin_profile && json.loggedin_profile.username ? json.loggedin_profile.username : '',
			            self.otherusername = json && json.other_user_profile && json.other_user_profile.username ? json.other_user_profile.username : '',
			            self.otheruserprofileid = json && json.other_user_profile && json.other_user_profile.profile_id ? json.other_user_profile.profile_id : '',
			            self.messages = [];
			            if (jsonlist.length) {
			                for (i = jsonlist.length - 1; i >= 0 ; i--) {
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
			            for (i = 0; i < self.messages.length; i++) {
			                message = self.messages[i];
			                html += message.makeHtml();
			            }
			            self.more_results_url = self.messages.length > 0 && json.messages_props && json.messages_props.more_results_url;
	                    if (html) {
                            pl('#moreresults').after(html);
                        }
                        if (self.more_results_url) {
                            pl('#moreresultsurl').text(self.more_results_url);
                            pl('#moreresultsmsg').text('Earlier messages...');
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
                var val = pl('#messagetext').attr('value');
                if (!pl('#messagetext').hasClass('edited')) {
                    pl('#messagetext').addClass('edited');
                    pl('#messagemsg').html('&nbsp;');
                }
                if (val && val.length >= 1) {
                    pl('#messagebtn').addClass('editenabled');
                }
                else if (val && val.length < 1) {
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
                        var html = (new MessageClass(self)).store(json).makeHtml();
                        pl('#messagetext').removeClass('edited').attr({value: 'Put your message here...'});
                        pl('#messagebtn').removeClass('editenabled');
                        pl('#messagemsg').addClass('successful').text('Message posted');
                        pl('#messagesend').before(html);
                    },
                    text = SafeStringClass.prototype.clean(pl('#messagetext').attr('value') || ''),
                    data = {
                        message: {
                            profile_id: self.otheruserprofileid,
                            text: text
                        }
                    },
                    ajax = new AjaxClass('/user/send_message', 'messagemsg', completeFunc);
                if (!pl('#messagebtn').hasClass('editenabled') || !text) {
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
