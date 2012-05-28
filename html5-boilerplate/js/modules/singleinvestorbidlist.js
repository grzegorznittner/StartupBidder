function BidClass(bidslist) {
    this.bidslist = bidslist;
}
pl.implement(BidClass, {
    store: function(json) {
        var self = this;
        for (k in json) {
            self[k] = json[k];
        }
        self.amttext = self.amt ? CurrencyClass.prototype.format(self.amt) : '';
        self.pcttext = self.pct ? PercentClass.prototype.format(self.pct) : '';
        self.valtext = self.val ? CurrencyClass.prototype.format(self.val) : '';
        self.typetext = self.type ? self.type.replace(/(INVESTOR_|OWNER_)/, '').toLowerCase() : '';
        self.bidtext = self.text ? SafeStringClass.prototype.htmlEntities(self.text) : 'No bids';
        self.datetext = self.created_date ? DateClass.prototype.format(self.created_date) : '';
        self.usertext = self.type.indexOf('INVESTOR') ? self.bidslist.investorusername : self.bidslist.ownerusername;
        return self;
    },
    setEmpty: function() {
        var self = this,
            emptyJson = {
                amt: null,
                pct: null,
                val: null,
                type: null,
                text: null,
                created_date: null
            };
        self.store(emptyJson);
    },
    makeHeader: function() {
        return '\
        <div class="messageline darkblue">\
            <p class="messageuser span-4">Actor</p>\
            <p class="messageuser span-2">Action</p>\
            <p class="messageuser span-2">Bid</p>\
            <p class="messageuser span-2">Equity</p>\
            <p class="messageuser span-2">Valuation</p>\
            <p class="messageuser span-6">Note</p>\
            <p class="messagedate">Date</p>\
        </div>\
        ';
    },
    makeHtml: function() {
        var self = this;
        return '\
        <div class="messageline">\
            <p class="messageuser span-4">' + self.usertext + '</p>\
            <p class="messageuser span-2">' + self.typetext + '</p>\
            <p class="messageuser span-2">' + self.amttext + '</p>\
            <p class="messageuser span-2">' + self.pcttext + '</p>\
            <p class="messageuser span-2">' + self.valtext + '</p>\
            <p class="messagetext span-6">\
                '+self.bidtext+'\
            </p>\
            <p class="messagedate">'+self.datetext+'</p>\
        </div>\
        ';
    }
});

function SingleInvestorBidListClass(listing_id, investor_profile_id, investor_profile_username) {
    this.listing_id = listing_id;
    this.investor_profile_id = investor_profile_id;
    this.investor_profile_username = investor_profile_username;
}
pl.implement(SingleInvestorBidListClass, {
    load: function() {
        var self = this,
            complete = function(json) {
                self.display(json);
            },
            ajax = new AjaxClass('/listing/bids/' + this.listing_id + '/' + this.investor_profile_id, 'bidtitlemsg', complete);
        ajax.mock({
bids:
[
    {
        amt: 20000,
        pct: 5,
        val: 400000,
        type: 'INVESTOR_POST',
        text: 'Is is a great idea, let us see if we can make it happen',
        created_date: 20120528183623
    },
    {
        amt: 20000,
        pct: 5,
        val: 400000,
        type: 'OWNER_REJECT',
        text: 'Not enough money for me to proceed, but thank you for your interest',
        created_date: 20120528191242
    },
    {
        amt: 40000,
        pct: 10,
        val: 400000,
        type: 'INVESTOR_POST',
        text: 'Here is a little more money, naturally I will require more shares as part of the deal',
        created_date: 20120528213422
    },
    {
        amt: 40000,
        pct: 5,
        val: 800000,
        type: 'OWNER_COUNTER',
        text: 'Well not that much equity, but it is looking a little more in line with what I have been thinking.',
        created_date: 20120528214814
    },
    {
        amt: 40000,
        pct: 10,
        val: 400000,
        type: 'INVESTOR_COUNTER',
        text: 'It looks like the money is agreeable, however as I indicated before I need more equity upside to be compensated fairly',
        created_date: 20120528231215
    },
    {
        amt: 40000,
        pct: 10,
        val: 400000,
        type: 'OWNER_ACCEPT',
        text: 'Okay I am comfortable with these terms, we have a deal',
        created_date: 20120528232341
    }
]
        }); // FIXME
        ajax.call();
    },
    store: function(json) {
        var self = this,
            jsonlist = json && json.bids ? json.bids : [],
            bid,
            i;
        self.investorusername = this.investor_profile_username;
        self.ownerusername = 'owner';
        self.bids = [];
        if (jsonlist.length) {
            for (i = 0; i < jsonlist.length; i++) {
                bid = new BidClass(this);
                bid.store(jsonlist[i]);
                self.bids.push(bid);
            }
        }
        else {
            bid = new BidClass(this);
            bid.setEmpty();
            self.bids.push(bid);
        }
    },
    display: function(json) {
        var self = this,
            html = '',
            i,
            bid;
        if (json !== undefined) {
            self.store(json);
        }
        if (self.bids.length) {
            html = BidClass.prototype.makeHeader();
            for (i = 0; i < self.bids.length; i++) {
                bid = self.bids[i];
                html += bid.makeHtml();
            }
        }
        else {
            bid = new BidClass(self);
            bid.setEmpty();
            html += bid.makeHtml();
        }
        //self.bindAddBox();
        pl('#makebidbox').before(html).show();
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
                        var html = (new BidClass(self)).store(json).makeHtml();
                        pl('#messagetext').removeClass('edited').attr({value: 'Put your message here...'});
                        pl('#messagebtn').removeClass('editenabled');
                        pl('#messagemsg').addClass('successful').text('Bid posted');
                        pl('#messagesend').before(html);
                    },
                    text = SafeStringClass.prototype.clean(pl('#messagetext').attr('value') || ''),
                    data = {
                        send: {
                            listing_id: self.listing_id,
                            text: text
                        }
                    },
                    ajax = new AjaxClass('/user/send_message', 'messagemsg', completeFunc);
                if (!pl('#messagebtn').hasClass('editenabled') || !text) {
                    return false;
                }
                ajax.setPostData(data);
                ajax.mock({
                    direction: 'sent',
                    text: data.send.text,
                    create_date: DateClass.prototype.now()
                }); // FIXME
                ajax.call();
                return false;
            }
        });
        pl('#messagetext').addClass('bound');
    }
});
