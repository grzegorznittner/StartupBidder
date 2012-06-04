function InvestorBidGroupClass(bidslist) {
    this.bidslist = bidslist;
    this.listing_id = bidslist.listing_id;
    this.typeclassmap = {
        investor_post: 'inprogress',
        investor_counter: 'inprogress',
        investor_accept: 'successful',
        investor_reject: 'errorcolor',
        investor_withdraw: 'errorcolor',
        owner_post: 'inprogress',
        owner_accept: 'successful',
        owner_reject: 'errorcolor',
        owner_counter: 'inprogress',
        owner_withdraw: 'errorcolor'
    };
}

pl.implement(InvestorBidGroupClass, {
    store: function(json) {
        var amt = json.last_amt || json.amt,
            pct = json.last_pct || json.pct,
            val = json.last_val || json.val,
            type = json.last_type || json.type,
            countertext = json.counter ? ' (' + json.counter + ')' : '',
            k;
        for (k in json) {
            this[k] = json[k];
        }
        this.messageclass = this.read ? '' : ' inputmsg'; // unread
        this.amttext = amt ? CurrencyClass.prototype.format(amt) : '';
        this.pcttext = pct ? PercentClass.prototype.format(pct) + '%' : '';
        this.valtext = val ? CurrencyClass.prototype.format(val) : '';
        this.typetext = type ? type.replace(/(investor_|owner_)/, '') : '';
        this.bidtext = this.last_text ? SafeStringClass.prototype.htmlEntities(this.last_text) : 'None';
        this.datetext = this.last_date ? DateClass.prototype.format(this.last_date) : '';
        this.usertext = this.investor_nickname + countertext;
        this.typeclass = this.typeclassmap[this.last_type] || '';
        this.url = this.investor_id ? '/owner-bids-page.html'
            + '?listing_id=' + this.listing_id
            + '&investor_id=' + this.investor_id
            + '&investor_nickname=' + encodeURIComponent(this.investor_nickname) : '';
        this.openanchor = this.url ? '<a href="' + this.url + '" class="hoverlink' + this.messageclass + ' ' + this.typeclass + '">' : '';
        this.closeanchor = this.url ? '</a>' : '';
    },

    setEmpty: function() {
        var self = this,
            emptyJson = {
                investor_id: null,
                investor_nickname: '',
                last_amt: null,
                last_pct: null,
                last_val: null,
                last_type: null,
                last_text: 'You currently have no bids.',
                last_date: null,
                read: true
            };
        self.store(emptyJson);
    },

    makeHeader: function() {
        return '\
        <style>\
            .investorgroupheader { background: #49515a !important; }\
            .investorgroupheader p { color: white; font-weight: bold !important; text-align: center; }\
            .investorgroupline p { font-weight: bold; text-align: center; }\
            .investorgroupnote { text-align: left !important; font-weight: normal !important; }\
            .investorgroupdateheader { float: left; width: 140px; }\
            .investorgroupdate { float: left; width: 140px; text-align: right; font-size: 12px; padding-top: 2px; }\
        </style>\
        <div class="messageline investorgroupheader">\
            <p class="span-4">Investor</p>\
            <p class="span-2">Action</p>\
            <p class="span-3">Bid</p>\
            <p class="span-1" style="position: relative; left:-10px;">Equity</p>\
            <p class="span-3">Valuation</p>\
            <p class="span-8">Note</p>\
            <p class="investorgroupdateheader">Date</p>\
        </div>\
        ';
    },

    makeHtml: function() {
        return '\
        <div class="messageline investorgroupline">\
            ' + this.openanchor + '\
            <p class="span-4">' + this.usertext + '</p>\
            <p class="span-2">' + this.typetext + '</p>\
            <p class="span-3">' + this.amttext + '</p>\
            <p class="span-1">' + this.pcttext + '</p>\
            <p class="span-3">' + this.valtext + '</p>\
            <p class="span-8 investorgroupnote">' + this.bidtext + '</p>\
            <p class="investorgroupdate">'+this.datetext+'</p>\
            ' + this.closeanchor + '\
        </div>\
        ';
    }
});

function InvestorBidGroupListClass() {
    var queryString = new QueryStringClass();
    this.listing_id = queryString.vars.id;
}
pl.implement(InvestorBidGroupListClass, {
    store: function(json) {
        var jsonlist = json && json.users ? json.users: [],
            investor,
            i;
        this.investors = [];
        if (jsonlist.length) {
            for (i = 0; i < jsonlist.length; i++) {
                investor = new InvestorBidGroupClass(this);
                investor.store(jsonlist[i]);
                this.investors.push(investor);
            }
        }
        else {
            investor = new InvestorBidGroupClass(this);
            investor.setEmpty();
            this.investors.push(investor);
        }
    },

    display: function(json) {
        var html = '',
            i,
            investor;
        if (json !== undefined) {
            this.store(json);
        }
        investor = new InvestorBidGroupClass(this);
        html += investor.makeHeader();
        for (i = 0; i < this.investors.length; i++) {
            investor = this.investors[i];
            html += investor.makeHtml();
        }
        if (!this.investors.length) {
            investor = new InvestorBidGroupClass();
            investor.setEmpty();
            html += investor.makeHtml();
        }
        pl('#investorgrouplist').html(html);
        pl('#bidsownergroup').show();
    },

    load: function() {
        var self = this,
            completeFunc = function(json) {
                var header = new HeaderClass(),
                    companybanner = new CompanyBannerClass('bids'),
                    orderbook;
                if (!json.listing) {
                    json.listing = {};
                    json.listing.listing_id = self.listing_id;
                }
                orderbook = new OrderBookClass(json.listing.listing_id, json.listing.suggested_amt, json.listing.suggested_pct, json.listing.listing_date);
                header.setLogin(json);
                companybanner.display(json);
                orderbook.load();
                self.display(json); 
            },

            ajax = new AjaxClass('/listing/bid_users/' + this.listing_id, 'bidstitlemsg', completeFunc);
        //this.mock(ajax);
        ajax.call();
    },

    mock: function(ajax) {
        ajax.mock({ 
    "login_url": null,
    "logout_url": "/_ah/logout?continue=http%3A%2F%2Flocalhost%3A7777",
    "loggedin_profile": {
        "profile_id": "ag1zdGFydHVwYmlkZGVycg4LEgZTQlVzZXIY3pIBDA",
        "username": "test",
        "name": null,
        "email": "test@example.com",
        "investor": false,
        "edited_listing": "ag1zdGFydHVwYmlkZGVycg8LEgdMaXN0aW5nGLqUAQw",
        "edited_status": null,
        "num_notifications": 0,
        "votable": false,
        "mockData": false,
        "admin": false
    },

    "error_code": 0,
    "error_msg": null,
    "users": [
        {
            investor_id: 'abca89708a7oe0u',
            investor_nickname: 'theotherguy',
            last_amt: '20000',
            last_pct: '5',
            last_val: '400000',
            last_type: 'investor_post',
            last_text: "I'm not sure if this is the right investment for me, but I'll be in town next week, if we could meet that would be great.",
            last_date: "20120428121845",
            read: false
        },

        {
            investor_id: 'def',
            investor_nickname: 'fowler',
            last_amt: '10000',
            last_pct: '5',
            last_val: '200000',
            last_type: 'investor_counter',
            last_text: "Do you have a method for increasing efficiency of TPS reports?  I need to know more before accepting.",
            last_date: "20120324092322",
            read: true
        },

        {
            investor_id: ':uid',
            investor_nickname: 'MadMax',
            last_amt: '15000',
            last_pct: '5',
            last_val: '300000',
            last_type: 'investor_accept',
            last_text: "This idea you've got about bacon and martinis is so crazy that it just might work!  Count me in!",
            last_date: "20120322072212",
            read: true
        },

        {
            investor_id: ':uid',
            investor_nickname: 'jenny',
            last_amt: '50000',
            last_pct: '20',
            last_val: '250000',
            last_type: 'investor_withdraw',
            last_text: "Would you like to discuss this further over dinner for two down at the shore?  Then perhaps I'll place a bid.",
            last_date: "20120318172238",
            read: true
        },

        {
            investor_id: ':uid',
            investor_nickname: 'arley',
            last_amt: '10000',
            last_pct: '5',
            last_val: '200000',
            last_type: 'investor_reject',
            last_text: "Mr. Madison, what you've just said is one of the most insanely idiotic things I have ever heard. At no point in your rambling, incoherent response were you even close to anything that could be considered a rational thought. Everyone in this room is now dumber for having listened to it. I award you no points, and may God have mercy on your soul.",
            last_date: "20120318164617",
            read: true
        },

        {
            investor_id: ':uid',
            investor_nickname: 'The One',
            last_amt: '20000',
            last_pct: '5',
            last_val: '400000',
            last_type: 'owner_counter',
            last_text: "Really I need more cash to get this idea off the ground, what do you say?",
            last_date: "20120319164617",
            read: true
        }
    ],
    "users_props": {
        "start_index": 1,
        "max_results": 20,
        "num_results": 5,
        "more_results_url": null
    },

    "profile": {
        "profile_id": "ag1zdGFydHVwYmlkZGVycg4LEgZTQlVzZXIY3pIBDA",
        "username": "test",
        "name": null,
        "email": "test@example.com",
        "investor": false,
        "edited_listing": "ag1zdGFydHVwYmlkZGVycg8LEgdMaXN0aW5nGLqUAQw",
        "edited_status": null,
        "num_notifications": 0,
        "votable": false,
        "mockData": false,
        "admin": false
    }});
    }
});

(new InvestorBidGroupListClass()).load();
