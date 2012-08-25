function MessageClass(messagelist) {
    this.messagelist = messagelist;
}
pl.implement(MessageClass, {
    store: function(json) {
        for (k in json) {
            this[k] = json[k];
        }
        return this;
    },

    setEmpty: function() {
        var emptyJson = {
                direction: 'received',
                text: 'You currently have no messages with this user.',
                create_date: null
            };
        this.store(emptyJson);
    },

    makeHtml: function() {
        var profile = this.direction === 'sent' ? this.messagelist.loggedin_profile : this.messagelist.other_user_profile,
            avatarstyle = profile.avatar
                ? ' style="background-image: url(' + profile.avatar + ')"'
                : '',
            usertext = '<span class="commentusername">' + profile.username + '</span>',
			userclasstext =  profile.user_class
                ? '<span class="profilelistuserclass">' + ProfileUserClass.prototype.format(profile.user_class) + '</span>'
                : '',
            datetext = '<span class="commentinlinedate">'
                + (this.ago_text || DateClass.prototype.agoText(this.create_date)) + '</span>',
            text = HTMLMarkup.prototype.stylize(SafeStringClass.prototype.htmlEntities(this.text)),
            messageclass = this.read ? '' : ' inputmsg';
        return '\
        <div class="commentline">\
            <a href="/profile-page.html?id=' + profile.profile_id + '">\
            <div class="commentavatar"' + avatarstyle + '></div>\
            <div class="commentheaderline">\
                ' + usertext + userclasstext + ' ' + datetext + '\
            </div>\
            </a>\
            <p class="commenttext">'
                  + text + '\
            </p>\
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
        self.loggedin_profile = json.loggedin_profile || {};
        self.other_user_profile = json.other_user_profile || {};
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
        if (self.more_results_url) {
        	html += '<div class="showmore hoverlink" id="moreresults"><span class="initialhidden" id="moreresultsurl">'
                + self.more_results_url + '</span><span id="moreresultsmsg">Earlier messages...</span></div>\n';
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
                        pl('#messagespinner').hide();
                        pl('#messagetext').removeClass('edited').removeAttr('disabled').attr({value: 'Put your message here...'});
                        pl('#messagebtn').removeClass('editenabled').show();
                        pl('#messagemsg').text('');
                        pl('#messagesend').before(html);
                    },

                    text = SafeStringClass.prototype.clean(pl('#messagetext').attr('value') || ''),

                    data = {
                        message: {
                            profile_id: self.other_user_profile.profile_id,
                            text: text
                        }
                    },

                    ajax = new AjaxClass('/user/send_message', 'messagemsg', completeFunc);
                if (pl('#messagebtn').hasClass('editenabled') && text) {
                    pl('#messagebtn').hide();
                    pl('#messagetext').attr('disabled', 'disabled');
                    pl('#messagespinner').show();
                    ajax.setPostData(data);
                    ajax.call();
                }
                return false;
            }
        });
        pl('#messagetext').addClass('bound');
    }
});

function MessagePageClass() {
    this.json = {};
    this.queryString = new QueryStringClass();
    this.from_user_id = this.queryString.vars.from_user_id || this.queryString.vars.to_user_id || '';
    this.from_user_nickname = decodeURIComponent(this.queryString.vars.from_user_nickname || this.queryString.vars.to_user_nickname || '');
};
pl.implement(MessagePageClass,{
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass(),
                    messageList = new MessageListClass();
                header.setLogin(json);
                if (!json.loggedin_profile) { // must be logged in for this page
                    window.location = '/';
                }
                messageList.display(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
             },

             ajax = new AjaxClass('/user/messages/' + this.from_user_id, 'messagemsg', completeFunc);
        pl('#from_user_nickname_upper').text(this.from_user_nickname.toUpperCase());
        ajax.setGetData({ max_results: 20 });
        ajax.call();
    }
});

function MessageGroupClass(messagegrouplist) {
    this.messagegrouplist = messagegrouplist;
    this.loggedin_profile = messagegrouplist.loggedin_profile;
}
pl.implement(MessageGroupClass, {
    store: function(json) {
        var self = this;
        for (k in json) {
            self[k] = json[k];
        }
    },

    setEmpty: function() {
        var emptyJson = {
                from_user_id: this.loggedin_profile.profile_id,
                from_user_nickname: this.loggedin_profile.username,
                from_user_avatar: this.loggedin_profile.avatar,
                from_user_class: this.loggedin_profile.user_class,
                last_text: 'You currently have no messages.',
                last_date: null,
                is_empty: true,
                read: true
            };
        this.store(emptyJson);
    },

    makeHtml: function() {
        var avatarstyle = this.from_user_avatar
                ? ' style="background-image: url(' + this.from_user_avatar + ')"'
                : '',
            usertext = '<span class="commentusername">' + this.from_user_nickname + '</span>',
			userclasstext =  this.from_user_class
                ? '<span class="profilelistuserclass">' + ProfileUserClass.prototype.format(this.from_user_class) + '</span>'
                : '',
            datetext = '<span class="commentinlinedate">'
                + (this.ago_text || (this.last_date ? DateClass.prototype.agoText(this.last_date) : '')) + '</span>',
            text = HTMLMarkup.prototype.stylize(SafeStringClass.prototype.htmlEntities(this.last_text)),
            messageclass = this.read ? '' : ' inputmsg',
            url = this.from_user_id
                ? (this.is_empty
                    ? '/profile-page.html?id=' + this.from_user_id
                    : '/messages-page.html?from_user_id=' + this.from_user_id + '&from_user_nickname=' + encodeURIComponent(this.from_user_nickname)
                  )
                : '',
            openanchor = url ? '<a href="' + url + '" class="messagelink' + messageclass + '">' : '',
            closeanchor = url ? '</a>' : '';
        return '\
        <div class="commentline messageline">\
        ' + openanchor + '\
            <div class="commentavatar"' + avatarstyle + '></div>\
            <div class="commentheaderline">\
                ' + usertext + userclasstext + ' ' + datetext + '\
            </div>\
            <p class="commenttext">'
                  + text + '\
            </p>\
        ' + closeanchor + '\
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
        self.loggedin_profile = json.loggedin_profile;
        self.messages = [];
        if (jsonlist.length) {
            for (i = 0; i < jsonlist.length; i++) {
                message = new MessageGroupClass(self);
                message.store(jsonlist[i]);
                self.messages.push(message);
            }
        }
        else {
            message = new MessageGroupClass(self);
            message.setEmpty();
            self.messages.push(message);
        }
        self.more_results_url = self.messages.length > 0 && json.messages_props && json.messages_props.more_results_url;
    },

    bindMoreResults: function() {
        var self = this;
        pl('#moreresults').bind({
            click: function() {
		                var completeFunc = function(json) {
		                	var self = this,
		                    jsonlist = json && json.users ? json.users : [],
		                    html = '',
		                    message,
		                    i;
		                self.messages = [];
		                if (jsonlist.length) {
		                    for (i = 0; i < jsonlist.length; i++) {
		                        message = new MessageGroupClass(self);
		                        message.store(jsonlist[i]);
		                        self.messages.push(message);
		                    }
		                }
		                else {
		                    message = new MessageGroupClass(self);
		                    message.setEmpty();
		                    self.messages.push(message);
		                }
		                
		                for (i = 0; i < self.messages.length; i++) {
		                    message = self.messages[i];
		                    html += message.makeHtml();
		                }
		                if (!self.messages.length) {
		                    message = new MessageGroupClass(self);
		                    message.setEmpty();
		                    html += message.makeHtml();
		                }
		                self.more_results_url = self.messages.length > 0 && json.messages_props && json.messages_props.more_results_url;
                        
	                    if (html) {
                            pl('#messagesend').before(html);
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
            message;
        if (json !== undefined) {
            self.store(json);
        }
        for (i = 0; i < self.messages.length; i++) {
            message = self.messages[i];
            html += message.makeHtml();
        }
        if (!self.messages.length) {
            message = new MessageGroupClass(self);
            message.setEmpty();
            html += message.makeHtml();
        }
        if (self.more_results_url) {
        	html += '<div class="showmore hoverlink" id="moreresults"><span class="initialhidden" id="moreresultsurl">'
                + self.more_results_url + '</span><span id="moreresultsmsg">More...</span></div>\n';
        }
        pl('#messagegrouplist').html(html);
        if (self.more_results_url) {
            this.bindMoreResults();
        }
    }
});

function MessageGroupPageClass() {
    this.json = {};
};
pl.implement(MessageGroupPageClass,{
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass(),
                    messageList = new MessageGroupListClass();
                header.setLogin(json);
                if (!json.loggedin_profile) { // must be logged in for this page
                    window.location = '/';
                }
                messageList.display(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
            },

            ajax = new AjaxClass('/user/message_users', 'messagemsg', completeFunc);
        ajax.setGetData({ max_results: 20 });
        ajax.call();
    }
});

