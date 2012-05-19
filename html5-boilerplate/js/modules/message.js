function MessageClass() {}
pl.implement(MessageClass, {
    store: function(json) {
        var self = this;
        for (k in json) {
            self[k] = json[k];
        }
        self.createddate = self.create_date ? DateClass.prototype.format(self.create_date) : '';
        self.message = self.title ? SafeStringClass.prototype.htmlEntities(self.title) : '';
        self.messageclass = self.read ? '' : ' inputmsg'; // unread
        if (!self.message_type) {
            self.type = 'message';
        }
        else if (self.message_type.match('comment')) {
            self.type = 'comment';
        }
        else if (self.message_type.match('bid')) {
            self.type = 'bid';
        }
        else {
            self.type = 'message';
        }
        self.datetext = self.created_date ? 'Sent on ' + DateClass.prototype.format(self.created_date) : '';
        self.openanchor = self.message_id ? '<a href="/message-page.html?id=' + self.message_id + '" class="hoverlink' + self.messageclass + '">' : '';
        self.closeanchor = self.message_id ? '</a>' : '';
    },
    setPageHtml: function() {
        var self = this,
            listing = {},
            tile = new CompanyTileClass({ companybannertileclass: 'companybannertilenoborder' });
        pl('#message_title').text(self.title);
        pl('#message_date').text(self.datetext);
        pl('#message_text_2').html(self.text_2||'');
        if (self.listing_id) {
            listing.listing_id = self.listing_id;
            listing.logo = self.listing_logo_url;
            listing.category = self.listing_category;
            listing.title = self.listing_name;
            listing.brief_address = self.listing_brief_address;
            listing.mantra = self.listing_mantra;
            /* self.listing_owner (user id) */
            tile.store(listing);
            //pl('#message_header').text('NOTIFICATION FOR ' + listing.title.toUpperCase());
            pl('#message_listing').html(tile.makeFullWidthHtml());
        }
    },
    setEmpty: function() {
        var self = this,
            emptyJson = {
                title: 'You currently have no messages.',
                read: true,
                message_type: 'message',
                message_id: 0
            };
        self.store(emptyJson);
    },
    setNotFound: function() {
        var self = this,
            emptyJson = {
                title: 'You must pass a message ID.',
                read: true,
                message_type: 'message',
                message_id: 0
            };
        self.store(emptyJson);
    },
    makeHtml: function() {
        var self = this;
        return '\
        <div class="sideboxmessage sideboxlink">\
            <span class="sideboxicon" style="overflow:visible;">\
                <div class="'+self.type+'icon" style="overflow:visible;"></div>\
            </span>\
            <span class="sideboxmessagetext">\
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
