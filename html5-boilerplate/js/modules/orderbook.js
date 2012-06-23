function PublicBidClass(bidslist, bidprop) {
    this.bidslist = bidslist;
    this.bidprop = bidprop;
    this.bidproptitle = {
        'investor_bids': 'Bid',
        'owner_bids': 'Ask',
        'accepted_bids': 'Sale'
    };
    this.amttitle = this.bidproptitle[this.bidprop];
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
pl.implement(PublicBidClass, {
    store: function(json) {
        var k;
        for (k in json) {
            this[k] = json[k];
        }
        this.amttext = this.amt ? CurrencyClass.prototype.format(this.amt) : '';
        this.pcttext = this.pct ? PercentClass.prototype.format(this.pct) : '';
        this.valtext = this.val ? CurrencyClass.prototype.format(this.val) : '';
        this.datetext = this.create_date ? DateClass.prototype.format(this.create_date) : '';
        this.bidclass = '';
        //this.bidclass = this.typeclassmap[this.type] || '';
        return this;
    },

    setEmpty: function() {
        var emptyJson = {
                amt: null,
                pct: null,
                val: null,
                create_date: null
            };
        this.store(emptyJson);
    },

    makeHeader: function() {
        return '\
        <div class="messageline orderbookheader">\
            <p class="span-2">' + this.amttitle + '</p>\
            <p class="span-2">Equity</p>\
            <p class="span-2">Valuation</p>\
            <p class="orderbookdateheader">Date</p>\
        </div>\
        ';
    },

    makeHtml: function() {
        if (this.amt) {
            return '\
            <div class="messageline orderbookline ' + this.bidclass + '">\
                <p class="span-2">' + this.amttext + '</p>\
                <p class="span-2">' + this.pcttext + '%</p>\
                <p class="span-2">' + this.valtext + '</p>\
                <p class="orderbookdate">'+this.datetext+'</p>\
            </div>\
            ';
        }
        else {
            return '\
            <div class="messageline orderbookline ' + this.bidclass + '">\
                <p class="span-6 attention" style="text-align: left; padding-left: 10px; ">No ' + this.amttitle + 's</p>\
                <p class="orderbookdate">'+this.datetext+'</p>\
            </div>\
            ';
        }
    }
});

function OrderBookClass(listing_id) {
    this.listing_id = listing_id;
    this.bidprops = [ 'investor_bids', 'owner_bids', 'accepted_bids' ];
    this.nobidsmap = {
        investor_bids: 'No bids',
        owner_bids: 'No Asks',
        accepted_bids: 'No Sales'
    };
}
pl.implement(OrderBookClass, {
    mock: function(ajax) {
        ajax.mock({
investor_bids: [
    { amt: '20000', pct: '5', val: '400000', type: 'investor_post', create_date: '20120528183623' },

    { amt: '40000', pct: '10', val: '400000', type: 'investor_post', create_date: '20120528213422' },

    { amt: '40000', pct: '10', val: '400000', type: 'investor_counter', create_date: '20120528231215' },

    { amt: '20000', pct: '5', val: '400000', type: 'investor_post', create_date: '20120529183623' },

    { amt: '45000', pct: '25', val: '180000', type: 'investor_post', create_date: '20120529213422' },

    { amt: '45000', pct: '25', val: '180000', type: 'investor_counter', create_date: '20120529231215' },

    { amt: '10000', pct: '5', val: '200000', type: 'investor_post', create_date: '20120530213422' },

    { amt: '15000', pct: '6', val: '250000', type: 'investor_accept', create_date: '20120530230121' }
],
owner_bids: [
    // { amt: '20000', pct: '5', val: '400000', type: 'owner_reject', create_date: '20120528191242' }, 
    { amt: '40000', pct: '5', val: '800000', type: 'owner_counter', create_date: '20120528214814' },

    { amt: '40000', pct: '10', val: '400000', type: 'owner_accept', create_date: '20120528232341' },

    //{ amt: '35000', pct: '15', val: '233333', type: 'owner_reject', create_date: '20120529191242' },

    { amt: '45000', pct: '15', val: '300000', type: 'owner_counter', create_date: '20120529214814' },

    { amt: '45000', pct: '25', val: '180000', type: 'owner_accept', create_date: '20120529232341' },

    { amt: '15000', pct: '6', val: '250000', type: 'owner_counter', create_date: '20120530221237' }
],
accepted_bids: [
    { amt: '40000', pct: '10', val: '400000', type: 'owner_accept', create_date: '20120528232341' },

    { amt: '45000', pct: '25', val: '180000', type: 'owner_accept', create_date: '20120529232341' },

    { amt: '15000', pct: '6', val: '250000', type: 'investor_accept', create_date: '20120530230121' }
]

        });
    },


    load: function() {
        var self = this,
            complete = function(json) {
                self.display(json);
            },

            ajax = new AjaxClass('/listing/order_book/' + this.listing_id, 'orderbooktitlemsg', complete);
        // this.mock(ajax);
        ajax.call();
    },

    storeOfferBid: function(json) {
        var listing = json.listing;
        if (listing) {
            this.ownerofferbid = new PublicBidClass(this, 'owner_bids');
            this.ownerofferbid.store({
                amt: listing.suggested_amt,
                pct: listing.suggested_pct,
                val: ValuationClass.prototype.valuation(listing.suggested_amt, listing.suggested_pct),
                type: 'owner_post',
                create_date: listing.listing_date
            });
        }
    },

    store: function(json) {
        var 
            bidprop,
            jsonlist,
            i,
            j,
            bid;
        this.bids = {};
        this.sortedbids = {};
        this.storeOfferBid(json);
        for (i = 0; i < this.bidprops.length; i++) {
            bidprop = this.bidprops[i];
            this.bids[bidprop] = [];
            this.sortedbids[bidprop] = bidprop === 'owner_bids' ? [ this.ownerofferbid ] : [];
            jsonlist = json[bidprop];
            if (jsonlist && jsonlist.length) {
                for (j = 0; j < jsonlist.length; j++) {
                    bidjson = jsonlist[j];
                    bid = new PublicBidClass(this, bidprop);
                    bid.store(bidjson);
                    this.bids[bidprop].push(bid);
                    this.sortedbids[bidprop].push(bid);
                }
            }
            if (bidprop === 'owner_bids') {
                this.bids[bidprop].push(this.ownerofferbid);
            }
            if (this.bids[bidprop].length) {
                var sorter;
                if (bidprop === 'investor_bids') {
                    sorter = function(a, b) { return b.val - a.val }; // descending valuation
                }
                else if (bidprop === 'owner_bids') {
                    sorter = function(a, b) { return a.val - b.val }; // ascending valuation
                }
                else if (bidprop === 'accepted_bids') {
                    sorter = function(a, b) { return b.create_date - a.create_date }; // descending date
                }
                this.sortedbids[bidprop].sort(sorter);
            }
            else if (bidprop !== 'owner_bids') {
                bid = new PublicBidClass(this, bidprop);
                bid.setEmpty();
                this.sortedbids[bidprop].push(bid);
            }
        }
    },

    display: function(json) {
        var html,
            bidprop,
            bids,
            bid,
            i;
        if (json !== undefined) {
            this.store(json);
        }
        for (bidprop in this.sortedbids) {
            bids = this.sortedbids[bidprop];
            bid = new PublicBidClass(this, bidprop);
            html = bid.makeHeader();
            for (i = 0; i < bids.length; i++) {
                bid = bids[i];
                html += bid.makeHtml();
            }
            pl('#orderbook_' + bidprop).html(html);

            if (!this.bids[bidprop].length) {
                //pl('#last_' + bidprop + '_amt').addClass('errorcolor').text(this.nobidsmap[bidprop]);
                pl('#last_' + bidprop + '_amt').text(this.nobidsmap[bidprop]);
            }
            else {
                bid = this.bids[bidprop][0];
                pl('#last_' + bidprop + '_amt').addClass(bid.bidclass).text(bid.amttext);
                pl('#last_' + bidprop + '_details').html('for <span class="sideboxnum">' + bid.pcttext + '%</span> valued at <span class="sideboxnum">' + bid.valtext + "</span>");
                pl('#last_' + bidprop + '_date').text(bid.datetext);
            }
        }
        pl('#last_bids_wrapper').show();
    }
});
