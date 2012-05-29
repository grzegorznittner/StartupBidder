function PublicBidClass(bidslist) {
    this.bidslist = bidslist;
    this.typeclassmap = {
        INVESTOR_POST: 'inprogress',
        INVESTOR_COUNTER: 'inprogress',
        INVESTOR_ACCEPT: 'successful',
        INVESTOR_REJECT: 'errorcolor',
        INVESTOR_WITHDRAW: 'errorcolor',
        OWNER_ACCEPT: 'successful',
        OWNER_REJECT: 'errorcolor',
        OWNER_COUNTER: 'inprogress',
        OWNER_WITHDRAW: 'errorcolor'
    }
}
pl.implement(PublicBidClass, {
    store: function(json) {
        var self = this;
        for (k in json) {
            self[k] = json[k];
        }
        self.amttext = self.amt ? CurrencyClass.prototype.format(self.amt) : '';
        self.pcttext = self.pct ? PercentClass.prototype.format(self.pct) : '';
        self.valtext = self.val ? CurrencyClass.prototype.format(self.val) : '';
        self.typetext = self.type || '';
        self.datetext = self.created_date ? DateClass.prototype.format(self.created_date) : '';
        self.typeclass = self.typeclassmap[self.type] || '';
        return self;
    },
    setEmpty: function() {
        var self = this,
            emptyJson = {
                amt: null,
                pct: null,
                val: null,
                type: null,
                created_date: null
            };
        self.store(emptyJson);
    },
    makeHeader: function() {
        return '\
        <style>\
            .bidheader { background: #49515a !important; }\
            .bidheader p { color: white; font-weight: bold !important; text-align: center; }\
            .bidline p { font-weight: bold; text-align: center; }\
            .bidnote { text-align: left !important; font-weight: normal !important; }\
            .biddateheader { float: left; width: 140px; }\
            .biddate { float: left; width: 140px; text-align: right; font-size: 12px; padding-top: 2px; }\
        </style>\
        <div class="messageline bidheader">\
            <p class="span-4">Action</p>\
            <p class="span-3">Bid</p>\
            <p class="span-2">Equity</p>\
            <p class="span-3">Valuation</p>\
            <p class="biddateheader">Date</p>\
        </div>\
        ';
    },
    makeHtml: function() {
        var self = this;
        return '\
        <div class="messageline bidline">\
            <p class="span-4 ' + self.typeclass + '">' + self.typetext + '</p>\
            <p class="span-3 ' + self.typeclass + '">' + self.amttext + '</p>\
            <p class="span-2 ' + self.typeclass + '">' + self.pcttext + '</p>\
            <p class="span-3 ' + self.typeclass + '">' + self.valtext + '</p>\
            <p class="biddate">'+self.datetext+'</p>\
        </div>\
        ';
    }
});

function OrderBookClass(listing_id) {
    this.listing_id = listing_id;
}
pl.implement(OrderBookClass, {
    load: function() {
        var self = this,
            complete = function(json) {
                self.display(json);
            },
            ajax = new AjaxClass('/listing/order_book/' + this.listing_id, 'orderbooktitlemsg', complete);
        ajax.mock({
bids:
[
    {
        amt: '20000',
        pct: '5',
        val: '400000',
        type: 'INVESTOR_POST',
        created_date: '20120528183623'
    },
    {
        amt: '20000',
        pct: '5',
        val: '400000',
        type: 'OWNER_REJECT',
        created_date: '20120528191242'
    },
    {
        amt: '40000',
        pct: '10',
        val: '400000',
        type: 'INVESTOR_POST',
        created_date: '20120528213422'
    },
    {
        amt: '40000',
        pct: '5',
        val: '800000',
        type: 'OWNER_COUNTER',
        created_date: '20120528214814'
    },
    {
        amt: '40000',
        pct: '10',
        val: '400000',
        type: 'INVESTOR_COUNTER',
        created_date: '20120528231215'
    },
    {
        amt: '40000',
        pct: '10',
        val: '400000',
        type: 'OWNER_ACCEPT',
        created_date: '20120528232341'
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
        self.bids = [];
        if (jsonlist.length) {
            for (i = 0; i < jsonlist.length; i++) {
                bid = new PublicBidClass(this);
                bid.store(jsonlist[i]);
                self.bids.push(bid);
            }
        }
        else {
            bid = new PublicBidClass(this);
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
            html = PublicBidClass.prototype.makeHeader();
            for (i = 0; i < self.bids.length; i++) {
                bid = self.bids[i];
                html += bid.makeHtml();
            }
        }
        else {
            bid = new PublicBidClass(self);
            bid.setEmpty();
            html += bid.makeHtml();
        }
        pl('#orderbookparent').html(html).show();
    }
});
