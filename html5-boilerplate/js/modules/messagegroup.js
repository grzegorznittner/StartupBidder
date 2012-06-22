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
        self.openanchor = self.url ? '<a href="' + self.url + '" class="hoverlink messagelink' + self.messageclass + '">' : '';
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
		                
		                for (i = 0; i < self.messages.length; i++) {
		                    message = self.messages[i];
		                    html += message.makeHtml();
		                }
		                if (!self.messages.length) {
		                    message = new MessageGroupClass();
		                    message.setEmpty();
		                    html += message.makeHtml();
		                }
		                self.more_results_url = self.messages.length > 0 && json.messages_props && json.messages_props.more_results_url;
                        
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
        if (self.more_results_url) {
        	html += '<div class="showmore hoverlink" id="moreresults"><span class="initialhidden" id="moreresultsurl">' + self.more_results_url + '</span><span id="moreresultsmsg">More...</span></div>\n';
        }
        pl('#messagegrouplist').html(html);
        if (self.more_results_url) {
            this.bindMoreResults();
        }
    }
});
