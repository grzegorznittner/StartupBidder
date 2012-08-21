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
                <p class="span-6 attention investorgroupattention">No ' + this.amttitle + 's</p>\
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

function CompanyBidsPageClass() {
    var queryString = new QueryStringClass();
    this.listing_id = queryString.vars.id;
};
pl.implement(CompanyBidsPageClass,{
    load: function() {
        var self = this,
            complete = function(json) {
                var header = new HeaderClass(),
                    companybanner = new CompanyBannerClass('bids'),
                    orderbook = new OrderBookClass(json.listing.listing_id);
                header.setLogin(json);
                companybanner.display(json);
                orderbook.display(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            error = function(errornum, json) {
                (new HeaderClass()).setLogin(json);
                pl('.preloader').hide();
                pl('.errorwrapper').show();
            },
            ajax = new AjaxClass('/listing/order_book/' + this.listing_id, 'orderbooktitlemsg', complete, null, null, error);
        ajax.call();
    }
});

function BidClass(bidslist) {
    this.bidslist = bidslist;
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

pl.implement(BidClass, {
    store: function(json) {
        var k;
        for (k in json) {
            this[k] = json[k];
        }
        this.amttext = this.amt ? CurrencyClass.prototype.format(this.amt) : '';
        this.pcttext = this.pct ? PercentClass.prototype.format(this.pct) + '%' : '';
        this.valtext = this.val ? CurrencyClass.prototype.format(this.val) : '';
        this.typetext = this.type ? this.type.replace(/(investor_|owner_)/, '') : '';
        this.bidtext = this.text ? SafeStringClass.prototype.htmlEntities(this.text) : 'None';
        this.datetext = this.create_date ? DateClass.prototype.format(this.create_date) : '';
        this.usertext = this.type && this.type.match(/investor/) ? 'You' : 'Owner';
        this.typeclass = '';
        //this.typeclass = this.typeclassmap[this.type] || '';
        return this;
    },

    setEmpty: function() {
        var emptyJson = {
                amt: null,
                pct: null,
                val: null,
                type: null,
                text: 'No bids',
                create_date: null
            };
        this.store(emptyJson);
    },

    makeHeader: function() {
        return '\
        <div class="messageline investorbidheader">\
            <p class="span-2">Actor</p>\
            <p class="span-2">Action</p>\
            <p class="span-3">Bid</p>\
            <p class="span-2">Equity</p>\
            <p class="span-3">Valuation</p>\
            <p class="span-9">Note</p>\
            <p class="investorbiddateheader">Date</p>\
        </div>\
        ';
    },

    makeHtml: function(options) {
        var addnote    = options && options.last && this.bidslist.listing.status === 'active' ? this.bidslist.makeAddNote() : ''; // removing note from accept/reject/withdraw actions
            addbuttons = options && options.last && this.bidslist.listing.status === 'active' ? this.bidslist.makeAddButtons() : '';
            amtidattr  = options && options.last && this.bidslist.listing.status === 'active' ? ' id="existing_bid_amt"' : '';
            pctidattr  = options && options.last && this.bidslist.listing.status === 'active' ? ' id="existing_bid_pct"' : '';
            validattr  = options && options.last && this.bidslist.listing.status === 'active' ? ' id="existing_bid_val"' : '';
        return '\
        <div class="messageline investorbidline ' + this.typeclass + '">\
            <p class="span-2">' + this.usertext + '</p>\
            <p class="span-2">' + this.typetext + '</p>\
            <p class="span-3 sideboxnum"' + amtidattr + '>' + this.amttext + '</p>\
            <p class="span-2 sideboxnum"' + pctidattr + '>' + this.pcttext + '</p>\
            <p class="span-3 sideboxnum"' + validattr + '>' + this.valtext + '</p>\
            <p class="span-9 investorbidnote">' + this.bidtext + '</p>\
            <p class="investorbiddate">' + this.datetext + '</p>\
        ' + addbuttons + '\
        </div>\
        ';
    }
});

function SingleInvestorBidListClass() {
    var queryString = new QueryStringClass();
    this.listing_id = queryString.vars.id;
    this.investor_id = queryString.vars.investor_id;
    this.investor_nickname = queryString.vars.investor_nickname;
    this.confirmtext = {
        investor_accept: 'You hereby agree to accept this bid according to the <a href="/terms-page.html">terms and conditions</a>.',
        investor_reject: 'You hereby agree to reject this bid according to the <a href="/terms-page.html">terms and conditions</a>.',
        investor_withdraw: 'You hereby agree to withdraw this bid according to the <a href="/terms-page.html">terms and conditions</a>.',
        investor_post: 'You hereby agree to make this bid according to the <a href="/terms-page.html">terms and conditions</a>.',
        investor_counter: 'You hereby agree to make this counter offer according to the <a href="/terms-page.html">terms and conditions</a>.',
        owner_accept: 'You hereby agree to accept this bid according to the <a href="/terms-page.html">terms and conditions</a>.',
        owner_reject: 'You hereby agree to reject this bid according to the <a href="/terms-page.html">terms and conditions</a>.',
        owner_withdraw: 'You hereby agree to withdraw this bid according to the <a href="/terms-page.html">terms and conditions</a>.',
        owner_post: 'You hereby agree to make this bid according to the <a href="/terms-page.html">terms and conditions</a>.',
        owner_counter: 'You hereby agree to make this counter offer according to the <a href="/terms-page.html">terms and conditions</a>.'
    };
    this.successtext = {
        investor_post: 'Bid posted',
        investor_counter: 'Counter-offer posted',
        investor_accept: 'Bid accepted',
        investor_reject: 'Bid rejected',
        investor_withdraw: 'Bid withdrawn',
        owner_post: 'Bid posted',
        owner_counter: 'Counter-offer posted',
        owner_accept: 'Bid accepted',
        owner_reject: 'Bid rejected',
        owner_withdraw: 'Bid withdrawn'
    };
}
pl.implement(SingleInvestorBidListClass, {
    load: function() {
        var self = this,
            complete = function(json) {
                var header = new HeaderClass(),
                    companybanner = new CompanyBannerClass('bids'),
                    orderbook;
                if (!json.listing) {
                    json.listing = {};
                    json.listing.listing_id = self.listing_id;
                }
                orderbook = new OrderBookClass(json.listing.listing_id);
                header.setLogin(json);
                companybanner.display(json);
                orderbook.display(json);
                self.display(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            error = function(errornum, json) {
                (new HeaderClass()).setLogin(json);
                pl('.preloader').hide();
                pl('.errorwrapper').show();
            },
            ajax = new AjaxClass('/listing/bids/' + this.listing_id, 'bidtitlemsg', complete, null, null, error);
        ajax.setGetData({ max_results: 10 });
        ajax.call();
    },

    store: function(json) {
        var validactions = json && json.valid_actions || [],
            bidsprops = json && json.bids_props || {},
            jsonlist = json && json.bids || [],
            bid,
            i;
        this.bidsprops = bidsprops;
        this.validactions = validactions;
        this.bids = [];
        this.listing = json.listing || {};
        if (jsonlist.length) {
            jsonlist.reverse(); // we want orderdd by date
            for (i = 0; i < jsonlist.length; i++) {
                bid = new BidClass(this);
                bid.store(jsonlist[i]);
                this.bids.push(bid);
            }
        }
        else {
            bid = new BidClass(this);
            bid.setEmpty();
            this.bids.push(bid);
        }
        this.more_results_url = this.bids.length > 0 && json.bids_props && json.bids_props.more_results_url;
    },

    display: function(json) {
        var html = '',
            i,
            bid;
        if (json !== undefined) {
            this.store(json);
        }
        if (this.bids.length) {
            html = BidClass.prototype.makeHeader();
            if (this.more_results_url) {
            	html += '<div class="showmore hoverlink" id="moreresults"><span class="initialhidden" id="moreresultsurl">' + self.more_results_url + '</span><span id="moreresultsmsg">Earlier bids...</span></div>\n';
            }
            for (i = 0; i < this.bids.length; i++) {
                bid = this.bids[i];
                html += bid.makeHtml({ last: (i === this.bids.length - 1)});
            }
        }
        else {
            bid = new BidClass(this);
            bid.setEmpty();
            html += bid.makeHtml({ last: true });
        }
        pl('#bidlistlast').before(html);
        if (this.listing.status === 'active') {
            this.bindBidBox();
        }
        if (this.more_results_url) {
            this.bindMoreResults();
        }
        pl('#bidsloggedin').show();
    },

    bindMoreResults: function() {
        var self = this;
        pl('#moreresults').bind({
            click: function() {
            	var completeFunc = function(json) {
	            		var validactions = json && json.valid_actions || [],
	                    bidsprops = json && json.bids_props || {},
	                    jsonlist = json && json.bids || [],
	                    html = '',
	                    bid,
	                    i;
		                this.investor_nickname = json.investor && json.investor.username || 'Anonymous';
		                this.bidsprops = bidsprops;
		                this.validactions = validactions;
		                this.bids = [];
		                if (jsonlist.length) {
		                    jsonlist.reverse(); // we want orderded by date
		                    for (i = 0; i < jsonlist.length; i++) {
		                        bid = new BidClass(self);
		                        bid.store(jsonlist[i]);
		                        this.bids.push(bid);
		                    }
		                    for (i = 0; i < this.bids.length; i++) {
		                        bid = this.bids[i];
		                        html += bid.makeHtml({ last: (i === this.bids.length - 1)});
		                    }
		                }
		                self.more_results_url = this.bids.length > 0 && json.bids_props && json.bids_props.more_results_url;
                
	                    if (html) {
                            pl('#moreresults').after(html);
                        }
                        if (self.more_results_url) {
                            pl('#moreresultsurl').text(self.more_results_url);
                            pl('#moreresultsmsg').text('Earlier bids...');
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
                if (self.more_results_url) {
                    ajax = new AjaxClass(self.more_results_url, 'moreresultsmsg', completeFunc);
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

    displayCalculatedIfValid: function() {
        var amt = CurrencyClass.prototype.clean(pl('#new_bid_amt').attr('value')) || 0,
            pct = PercentClass.prototype.clean(pl('#new_bid_pct').attr('value')) || 0,
            val = pct ? Math.floor(Math.floor(100 * amt / pct)) : 0,
            cur = CurrencyClass.prototype.format(CurrencyClass.prototype.clean(val)),
            dis = cur || '';
        pl('#new_bid_val').removeClass('inprogress').addClass('successful').text(dis);
    },

    getUpdater: function(fieldName, cleaner) {
        var self = this;
        return function(newdata, loadFunc, errorFunc, successFunc) {
            var newval = (newdata ? (cleaner ? cleaner(newdata.changeKey) : newdata.changeKey) : undefined);
            if (newval) {
                this.newval = newval;
                this.value = newval;
                this.msg.show('successful', '');
                self.displayCalculatedIfValid();
            }
        };
    },

    genDisplayCalculatedIfValid: function(field) {
        var self = this;
        return function(result, val) {
            var id = field.fieldBase.id;
            if (result === 0) {
                self.displayCalculated();
            }
        };
    },

    genDisplayCalculatedIfValidAmt: function(field) {
        var self = this;
            f1 = this.genDisplayCalculatedIfValid(field);
        return function(result, val) {
            self.displayIfValidAmt(result, val);
            f1();
            self.displayCalculatedIfValid();
        }
    },

    genDisplayCalculatedIfValidPct: function(field) {
        var self = this;
            f1 = this.genDisplayCalculatedIfValid(field);
        return function(result, val) {
            self.displayIfValidPct(result, val);
            f1();
            self.displayCalculatedIfValid();
        }
    },

    displayIfValidAmt: function(result, val) {
        var fmt = CurrencyClass.prototype.format(val);
        if (result === 0) {
            pl('#new_bid_amt').attr({value: fmt});
        }
    },

    displayIfValidPct: function(result, val) {
        var fmt = PercentClass.prototype.format(val);
        if (result === 0) {
            pl('#new_bid_pct').attr({value: fmt});
        }
    },

    bindFields: function() {
        var amtfield = new TextFieldClass('new_bid_amt', null, this.getUpdater('new_bid_amt', CurrencyClass.prototype.clean), 'new_bid_msg'),
            pctfield = new TextFieldClass('new_bid_pct', null, this.getUpdater('new_bid_pct', PercentClass.prototype.clean), 'new_bid_msg');
        amtfield.fieldBase.setDisplayName('AMOUNT');
        pctfield.fieldBase.setDisplayName('PERCENT');
        amtfield.fieldBase.addValidator(ValidatorClass.prototype.genIsNumberBetween(100, 500000));
        pctfield.fieldBase.addValidator(ValidatorClass.prototype.genIsNumberBetween(1, 100));
        amtfield.fieldBase.validator.preValidateTransform = CurrencyClass.prototype.clean;
        pctfield.fieldBase.validator.preValidateTransform = PercentClass.prototype.clean;
        amtfield.fieldBase.validator.postValidator = this.genDisplayCalculatedIfValidAmt(amtfield);
        pctfield.fieldBase.validator.postValidator = this.genDisplayCalculatedIfValidPct(pctfield);
        amtfield.bindEvents();
        pctfield.bindEvents();
        this.amtfield = amtfield;
        this.pctfield = pctfield;
        this.bindTextNote('new_bid_text', 'new_bid_msg');
    },

    bindTextNote: function(textid, msgid) {
        var textsel = '#' + textid,
            msgsel = '#' + msgid;
        pl(textsel).bind({
            focus: function() {
                if (!pl(textsel).hasClass('edited')) {
                    pl(textsel).attr({value: ''});
                    pl(msgsel).html('&nbsp;');
                }
            },

            keyup: function() {
                var val = pl(textsel).attr('value');
                if (!pl(textsel).hasClass('edited')) {
                    pl(textsel).addClass('edited');
                    pl(msgsel).html('&nbsp;');
                }
                return false;
            },

            blur: function() {
                if (!pl(textsel).hasClass('edited')) {
                    pl(textsel).attr({value: 'Put your note to the owner here...'});
                }
            }
        });
    },

    makeAddNote: function() {
        return '\
            <div class="notebidactionline initialhidden" id="existingbidnotebox">\
                <span class="span-4">&nbsp;</span>\
                <span class="span-15">\
                    <div class="formitem clear">\
                        <label class="inputlabel" for="note">NOTE</label>\
                        <span class="inputfield">\
                            <textarea class="textarea new_bid_textarea" name="note" id="existing_bid_text" cols="20" rows="5">Put your note to the owner here...</textarea>\
                        </span>\
                        <span class="inputicon">\
                            <div id="new_bid_texticon"></div>\
                        </span>\
                    </div>\
                </span>\
            </div>\
        ';
    },

    makeAddButtons: function() {
        return '\
<div class="bidactionline" id="existingbidbuttons">\
    <span class="span-3 inputbutton bidactionbutton initialhidden" id="investor_withdraw_btn">WITHDRAW</span>\
    <span class="span-3 inputbutton bidactionbutton initialhidden" id="investor_reject_btn">REJECT</span>\
    <span class="span-3 inputbutton bidactionbutton initialhidden" id="investor_accept_btn">ACCEPT</span>\
    <span class="span-11 bidconfirmmessage" id="existingbidmsg"></span>\
</div>\
<div class="bidactionline initialhidden" id="existingconfirmbuttons">\
    <span class="span-3 inputbutton bidactionbutton" id="investor_existing_cancel_btn">CANCEL</span>\
    <span class="span-3 inputbutton bidactionbutton" id="investor_existing_confirm_btn">CONFIRM</span>\
    <span class="span-14 bidconfirmmessage" id="investor_existing_msg"></span>\
</div>\
        ';
    },

    bindButtons: function() {
        var i,
            action,
            actionfuncname,
            newbidaction = false,
            existingbidaction = false;
        for (i = 0; i < this.validactions.length; i++) {
            action = this.validactions[i];
            actionfuncname = 'bind_' + action;
            this[actionfuncname]();
            if (action === 'investor_post' || action === 'investor_counter') {
                newbidaction = true;
            }
            else if (action === 'investor_accept' || action === 'investor_reject' || action === 'investor_withdraw') {
                existingbidaction = true;
            }
        }
        if (newbidaction) {
            pl('#new_bid_boxtitle, #new_bid_boxparent').show();
        }
        if (existingbidaction) {
            this.bindTextNote('existing_bid_text', 'existingbidmsg');
            pl('#existingbidnotebox').show();
        }
    },

    newBidAction: function(type) {
        this.makeBidAction(type, 'new');
    },
    
    existingBidAction: function(type) {
        this.makeBidAction(type, 'existing');
    },

    makeBidAction: function(type, neworexisting) {    
        var self = this,
            complete = function(json) {
                pl('#investor_' + neworexisting + '_msg').addClass('successful').text(self.successtext[type] + ', reloading...');
                setTimeout(function() { location.reload(); }, 3000);
            },

            text = pl('#' + neworexisting + '_bid_text').hasClass('edited')
                && SafeStringClass.prototype.clean(pl('#' + neworexisting + '_bid_text').attr('value') || '') || '',
            rawamt = neworexisting === 'new' ? pl('#new_bid_amt').attr('value') : pl('#existing_bid_amt').text(),
            rawpct = neworexisting === 'new' ? pl('#new_bid_pct').attr('value') : pl('#existing_bid_pct').text(),
            amt = CurrencyClass.prototype.clean(rawamt),
            pct = PercentClass.prototype.clean(rawpct),
            val = CurrencyClass.prototype.clean(pl('#' + neworexisting + '_bid_val').text()),
            data = {
                bid: {
                    listing_id: self.listing_id,
                    //investor_id: self.investor_profile_id, // only passed for owner
                    amt: amt,
                    pct: pct,
                    val: val,
                    type: type,
                    text: text
                }
            },

            ajax = new AjaxClass('/listing/make_bid', 'investor_' + neworexisting + '_msg', complete);
        ajax.setPostData(data);
        ajax.call();
    },

    showNewBidConfirmButtons: function(type) {
        var self = this,
            btnid = type + '_btn',
            btnsel = '#' + btnid;
        pl('#new_bid_amt, #new_bid_pct, #new_bid_text').attr('disabled', 'disabled');
        pl('#newbidbuttons').hide();
        pl('#investor_new_confirm_btn').unbind().bind('click', function() {
            if (!pl('#investor_new_confirm_btn').hasClass('submitting')) {
                pl('#investor_new_confirm_btn, #investor_new_cancel_btn').css({visibility: 'hidden'});
                pl('#investor_new_confirm_btn').addClass('submitting');
                pl('#investor_new_msg').addClass('inprogress').text('Submitting...');
                self.newBidAction(type);
            }
        });
        pl('#investor_new_cancel_btn').unbind().bind('click', function() {
            if (!pl('#investor_new_confirm_btn').hasClass('submitting')) {
                pl('#newconfirmbuttons').hide();
                pl('#newbidbuttons').show();
                pl('#new_bid_amt, #new_bid_pct, #new_bid_text').removeAttr('disabled');
            }
        });
        pl('#investor_new_msg').html(this.confirmtext[type]);
        pl('#newconfirmbuttons').show();
    },

    showExistingBidConfirmButtons: function(type) {
        var self = this,
            btnid = type + '_btn',
            btnsel = '#' + btnid;
        pl('#existing_bid_text').attr('disabled', 'disabled');
        pl('#existingbidbuttons').hide();
        pl('#investor_existing_confirm_btn').unbind().bind('click', function() {
            if (!pl('#investor_existing_confirm_btn').hasClass('submitting')) {
                pl('#investor_existing_confirm_btn').addClass('submitting');
                pl('#investor_existing_confirm_btn, #investor_existing_cancel_btn').css({visibility: 'hidden'});
                pl('#investor_existing_msg').addClass('inprogress').text('Submitting...');
                self.existingBidAction(type);
            }
        });
        pl('#investor_existing_cancel_btn').unbind().bind('click', function() {
            if (!pl('#investor_existing_confirm_btn').hasClass('submitting')) {
                pl('#existingconfirmbuttons').hide();
                pl('#existingbidbuttons').show();
                pl('#existing_bid_text').removeAttr('disabled');
            }
        });
        pl('#investor_existing_msg').html(this.confirmtext[type]);
        pl('#existingconfirmbuttons').show();
    },

    bindNewBidActionButton: function(type) {
        var self = this,
            btnid = type + '_btn',
            btnsel = '#' + btnid;
        pl('#new_bid_amt, #new_bid_pct, #new_bid_text').removeAttr('disabled');
        pl(btnsel).bind('click', function() {
            var validamt = self.amtfield.validate(),
                validpct = self.pctfield.validate(),
                validmsg = '' + (validamt ? 'AMOUNT: ' + validamt + ' ' : '') + (validpct ? 'PERCENT: ' + validpct : '');
            if (validmsg) {
                self.amtfield.fieldBase.msg.show('attention', validmsg);
            }
            else {
                self.showNewBidConfirmButtons(type);
            }
            return false;
        }).show();
    },

    bindExistingBidActionButton: function(type) {
        var self = this,
            btnid = type + '_btn',
            btnsel = '#' + btnid;
        pl(btnsel).bind('click', function() {
            self.showExistingBidConfirmButtons(type);
            return false;
        }).show();
    },

    bind_investor_post: function(type) {
        this.bindNewBidActionButton('investor_post');
    },

    bind_investor_counter: function() {
        this.bindNewBidActionButton('investor_counter');
    },

    bind_investor_accept: function() {
        this.bindExistingBidActionButton('investor_accept');
    },

    bind_investor_reject: function() {
        this.bindExistingBidActionButton('investor_reject');
    },

    bind_investor_withdraw: function() {
        this.bindExistingBidActionButton('investor_withdraw');
    },
/*
    bind_owner_accept: function() {
    },

    bind_owner_reject: function() {
    },

    bind_owner_counter: function() {
    },

    bind_owner_withdraw: function() {
    },
*/
    bindBidBox: function() {
        if (!pl('#new_bid_box').hasClass('bound')) {
            this.bindFields();
            this.bindButtons();
            pl('#new_bid_box').addClass('bound');
        }
    }
});

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
        this.typeclass = '';
        //this.typeclass = this.typeclassmap[this.last_type] || '';
        this.url = this.investor_id ? '/company-owner-investor-bids-page.html'
            + '?id=' + this.listing_id
            + '&investor_id=' + this.investor_id : '#';
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
                last_text: 'No bids received for this listing.',
                last_date: null,
                read: true
            };
        self.store(emptyJson);
    },

    makeHeader: function() {
        return '\
        <div class="messageline investorgroupheader">\
            <p class="span-4">Investor</p>\
            <p class="span-2">Action</p>\
            <p class="span-3">Bid</p>\
            <p class="span-1 investorgroupequityheader">Equity</p>\
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
        var jsonlist = json && json.investors || [],
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
        this.more_results_url = this.investors.length > 0 && json.investors_props && json.investors_props.more_results_url;
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
        if (this.more_results_url) {
        	html += '<div class="showmore hoverlink" id="moreresults"><span class="initialhidden" id="moreresultsurl">' + self.more_results_url + '</span><span id="moreresultsmsg">More investors...</span></div>\n';
        }
        pl('#investorgrouplist').html(html);
        if (this.more_results_url) {
            this.bindMoreResults();
        }
        pl('#bidsownergroup').show();
    },

    bindMoreResults: function() {
        var self = this;
        pl('#moreresults').bind({
            click: function() {
		            	var completeFunc = function(json) {
		            		var jsonlist = json && json.investors || [],
		            		html = '',
		                    investor,
		                    i;
		                this.investors = [];
		                if (jsonlist.length) {
		                    for (i = 0; i < jsonlist.length; i++) {
		                        investor = new InvestorBidGroupClass(self);
		                        investor.store(jsonlist[i]);
		                        this.investors.push(investor);
		                    }
		                    for (i = 0; i < this.investors.length; i++) {
		                        investor = this.investors[i];
		                        html += investor.makeHtml();
		                    }
		                }
		                else {
		                    investor = new InvestorBidGroupClass(this);
		                    investor.setEmpty();
		                    this.investors.push(investor);
		                }
		                self.more_results_url = this.investors.length > 0 && json.investors_props && json.investors_props.more_results_url;
                
	                    if (html) {
                            pl('#moreresults').before(html);
                        }
                        if (self.more_results_url) {
                            pl('#moreresultsurl').text(self.more_results_url);
                            pl('#moreresultsmsg').text('Earlier bids...');
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
                if (self.more_results_url) {
                    ajax = new AjaxClass(self.more_results_url, 'moreresultsmsg', completeFunc);
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

    load: function() {
        var self = this,
            complete = function(json) {
                var header = new HeaderClass(),
                    companybanner = new CompanyBannerClass('bids'),
                    orderbook;
                if (!json.listing) {
                    json.listing = {};
                    json.listing.listing_id = self.listing_id;
                }
                orderbook = new OrderBookClass(json.listing.listing_id);
                header.setLogin(json);
                companybanner.display(json);
                orderbook.display(json);
                self.display(json); 
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            error = function(errornum, json) {
                (new HeaderClass()).setLogin(json);
                pl('.preloader').hide();
                pl('.errorwrapper').show();
            },
            ajax = new AjaxClass('/listing/investors/' + this.listing_id, 'bidstitlemsg', complete, null, null, error);
        ajax.setGetData({ max_results: 20 });
        ajax.call();
    }
});

function OwnerSingleInvestorBidListClass() {
    var queryString = new QueryStringClass();
    this.listing_id = queryString.vars.id;
    this.investor_id = queryString.vars.investor_id;
    this.confirmtext = {
        investor_accept: 'You hereby agree to accept this bid according to the <a href="/terms-page.html">terms and conditions</a>.',
        investor_reject: 'You hereby agree to reject this bid according to the <a href="/terms-page.html">terms and conditions</a>.',
        investor_withdraw: 'You hereby agree to withdraw this bid according to the <a href="/terms-page.html">terms and conditions</a>.',
        investor_post: 'You hereby agree to make this bid according to the <a href="/terms-page.html">terms and conditions</a>.',
        investor_counter: 'You hereby agree to make this counter offer according to the <a href="/terms-page.html">terms and conditions</a>.',
        owner_accept: 'You hereby agree to accept this bid according to the <a href="/terms-page.html">terms and conditions</a>.',
        owner_reject: 'You hereby agree to reject this bid according to the <a href="/terms-page.html">terms and conditions</a>.',
        owner_withdraw: 'You hereby agree to withdraw this bid according to the <a href="/terms-page.html">terms and conditions</a>.',
        owner_post: 'You hereby agree to make this bid according to the <a href="/terms-page.html">terms and conditions</a>.',
        owner_counter: 'You hereby agree to make this counter offer according to the <a href="/terms-page.html">terms and conditions</a>.'
    };
    this.successtext = {
        investor_post: 'Bid posted',
        investor_counter: 'Counter-offer posted',
        investor_accept: 'Bid accepted',
        investor_reject: 'Bid rejected',
        investor_withdraw: 'Bid withdrawn',
        owner_post: 'Bid posted',
        owner_counter: 'Counter-offer posted',
        owner_accept: 'Bid accepted',
        owner_reject: 'Bid rejected',
        owner_withdraw: 'Bid withdrawn'
    };
}
pl.implement(OwnerSingleInvestorBidListClass, {
    load: function() {
        var self = this,
            complete = function(json) {
                var header = new HeaderClass(),
                    companybanner = new CompanyBannerClass('bids'),
                    orderbook;
                if (!json.listing) {
                    json.listing = {};
                    json.listing.listing_id = self.listing_id;
                }
                orderbook = new OrderBookClass(json.listing.listing_id);
                header.setLogin(json);
                companybanner.display(json);
                orderbook.display(json);
                self.display(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            error = function(errornum, json) {
                (new HeaderClass()).setLogin(json);
                pl('.preloader').hide();
                pl('.errorwrapper').show();
            },
            ajax = new AjaxClass('/listing/bids/' + this.listing_id + '/' + this.investor_id, 'bidtitlemsg', complete, null, null, error);
        ajax.setGetData({ max_results: 10 });
        ajax.call();
    },

    store: function(json) {
        var validactions = json && json.valid_actions || [],
            bidsprops = json && json.bids_props || {},
            jsonlist = json && json.bids || [],
            bid,
            i;
        this.listing = json.listing || {};
        this.investor_nickname = json.investor && json.investor.username || 'Anonymous';
        this.bidsprops = bidsprops;
        this.validactions = validactions;
        this.bids = [];
        if (jsonlist.length) {
            jsonlist.reverse(); // we want orderdd by date
            for (i = 0; i < jsonlist.length; i++) {
                bid = new BidClass(this);
                bid.store(jsonlist[i]);
                this.bids.push(bid);
            }
        }
        else {
            bid = new BidClass(this);
            bid.setEmpty();
            this.bids.push(bid);
        }
        this.more_results_url = this.bids.length > 0 && json.bids_props && json.bids_props.more_results_url;
    },

    display: function(json) {
        var html = '',
            i,
            bid;
        if (json !== undefined) {
            this.store(json);
        }
        pl('#investor_nickname').text(this.investor_nickname.toUpperCase());
        if (this.bids.length) {
            html = BidClass.prototype.makeHeader();
            if (this.more_results_url) {
            	html += '<div class="showmore hoverlink" id="moreresults"><span class="initialhidden" id="moreresultsurl">' + self.more_results_url + '</span><span id="moreresultsmsg">Earlier bids...</span></div>\n';
            }
            for (i = 0; i < this.bids.length; i++) {
                bid = this.bids[i];
                html += bid.makeHtml({ last: (i === this.bids.length - 1)});
            }
        }
        else {
            bid = new BidClass(this);
            bid.setEmpty();
            html += bid.makeHtml({ last: true });
        }
        pl('#bidlistlast').before(html);
        if (this.listing.status === 'active') {
            this.bindBidBox();
        }
        if (this.more_results_url) {
            this.bindMoreResults();
        }
        pl('#bidsloggedin').show();
    },

    bindMoreResults: function() {
        var self = this;
        pl('#moreresults').bind({
            click: function() {
            	var completeFunc = function(json) {
	            		var validactions = json && json.valid_actions || [],
	                    bidsprops = json && json.bids_props || {},
	                    jsonlist = json && json.bids || [],
	                    html = '',
	                    bid,
	                    i;
		                this.investor_nickname = json.investor && json.investor.username || 'Anonymous';
		                this.bidsprops = bidsprops;
		                this.validactions = validactions;
		                this.bids = [];
		                if (jsonlist.length) {
		                    jsonlist.reverse(); // we want orderded by date
		                    for (i = 0; i < jsonlist.length; i++) {
		                        bid = new BidClass(self);
		                        bid.store(jsonlist[i]);
		                        this.bids.push(bid);
		                    }
		                    for (i = 0; i < this.bids.length; i++) {
		                        bid = this.bids[i];
		                        html += bid.makeHtml({ last: (i === this.bids.length - 1)});
		                    }
		                }
		                self.more_results_url = this.bids.length > 0 && json.bids_props && json.bids_props.more_results_url;
                
	                    if (html) {
                            pl('#moreresults').after(html);
                        }
                        if (self.more_results_url) {
                            pl('#moreresultsurl').text(self.more_results_url);
                            pl('#moreresultsmsg').text('Earlier bids...');
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
                if (self.more_results_url) {
                    ajax = new AjaxClass(self.more_results_url, 'moreresultsmsg', completeFunc);
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

    displayCalculatedIfValid: function() {
        var amt = CurrencyClass.prototype.clean(pl('#new_bid_amt').attr('value')) || 0,
            pct = PercentClass.prototype.clean(pl('#new_bid_pct').attr('value')) || 0,
            val = pct ? Math.floor(Math.floor(100 * amt / pct)) : 0,
            cur = CurrencyClass.prototype.format(CurrencyClass.prototype.clean(val)),
            dis = cur || '';
        pl('#new_bid_val').removeClass('inprogress').addClass('successful').text(dis);
    },

    getUpdater: function(fieldName, cleaner) {
        var self = this;
        return function(newdata, loadFunc, errorFunc, successFunc) {
            var newval = (newdata ? (cleaner ? cleaner(newdata.changeKey) : newdata.changeKey) : undefined);
            if (newval) {
                this.newval = newval;
                this.value = newval;
                this.msg.show('successful', '');
                self.displayCalculatedIfValid();
            }
        };
    },

    genDisplayCalculatedIfValid: function(field) {
        var self = this;
        return function(result, val) {
            var id = field.fieldBase.id;
            if (result === 0) {
                self.displayCalculated();
            }
        };
    },

    genDisplayCalculatedIfValidAmt: function(field) {
        var self = this;
            f1 = this.genDisplayCalculatedIfValid(field);
        return function(result, val) {
            self.displayIfValidAmt(result, val);
            f1();
            self.displayCalculatedIfValid();
        }
    },

    genDisplayCalculatedIfValidPct: function(field) {
        var self = this;
            f1 = this.genDisplayCalculatedIfValid(field);
        return function(result, val) {
            self.displayIfValidPct(result, val);
            f1();
            self.displayCalculatedIfValid();
        }
    },

    displayIfValidAmt: function(result, val) {
        var fmt = CurrencyClass.prototype.format(val);
        if (result === 0) {
            pl('#new_bid_amt').attr({value: fmt});
        }
    },

    displayIfValidPct: function(result, val) {
        var fmt = PercentClass.prototype.format(val);
        if (result === 0) {
            pl('#new_bid_pct').attr({value: fmt});
        }
    },

    bindFields: function() {
        var amtfield = new TextFieldClass('new_bid_amt', null, this.getUpdater('new_bid_amt', CurrencyClass.prototype.clean), 'new_bid_msg'),
            pctfield = new TextFieldClass('new_bid_pct', null, this.getUpdater('new_bid_pct', PercentClass.prototype.clean), 'new_bid_msg');
        amtfield.fieldBase.setDisplayName('AMOUNT');
        pctfield.fieldBase.setDisplayName('PERCENT');
        amtfield.fieldBase.addValidator(ValidatorClass.prototype.genIsNumberBetween(100, 500000));
        pctfield.fieldBase.addValidator(ValidatorClass.prototype.genIsNumberBetween(1, 100));
        amtfield.fieldBase.validator.preValidateTransform = CurrencyClass.prototype.clean;
        pctfield.fieldBase.validator.preValidateTransform = PercentClass.prototype.clean;
        amtfield.fieldBase.validator.postValidator = this.genDisplayCalculatedIfValidAmt(amtfield);
        pctfield.fieldBase.validator.postValidator = this.genDisplayCalculatedIfValidPct(pctfield);
        amtfield.bindEvents();
        pctfield.bindEvents();
        this.amtfield = amtfield;
        this.pctfield = pctfield;
        this.bindTextNote('new_bid_text', 'new_bid_msg');
    },

    bindTextNote: function(textid, msgid) {
        var textsel = '#' + textid,
            msgsel = '#' + msgid;
        pl(textsel).bind({
            focus: function() {
                if (!pl(textsel).hasClass('edited')) {
                    pl(textsel).attr({value: ''});
                    pl(msgsel).html('&nbsp;');
                }
            },

            keyup: function() {
                var val = pl(textsel).attr('value');
                if (!pl(textsel).hasClass('edited')) {
                    pl(textsel).addClass('edited');
                    pl(msgsel).html('&nbsp;');
                }
                return false;
            },

            blur: function() {
                if (!pl(textsel).hasClass('edited')) {
                    pl(textsel).attr({value: 'Put your note to the owner here...'});
                }
            }
        });
    },

    makeAddNote: function() {
        return '\
            <div class="notebidactionline initialhidden" id="existingbidnotebox">\
                <span class="span-4">&nbsp;</span>\
                <span class="span-15">\
                    <div class="formitem clear">\
                        <label class="inputlabel" for="note">NOTE</label>\
                        <span class="inputfield">\
                            <textarea class="textarea new_bid_textarea" name="note" id="existing_bid_text" cols="20" rows="5">Put your note to the owner here...</textarea>\
                        </span>\
                        <span class="inputicon">\
                            <div id="new_bid_texticon"></div>\
                        </span>\
                    </div>\
                </span>\
            </div>\
        ';
    },

    makeAddButtons: function() {
        return '\
<div class="bidactionline" id="existingbidbuttons">\
    <span class="span-3 inputbutton bidactionbutton initialhidden" id="owner_withdraw_btn">WITHDRAW</span>\
    <span class="span-3 inputbutton bidactionbutton initialhidden" id="owner_reject_btn">REJECT</span>\
    <span class="span-3 inputbutton bidactionbutton initialhidden" id="owner_accept_btn">ACCEPT</span>\
    <span class="span-11 bidconfirmmessage" id="existingbidmsg"></span>\
</div>\
<div class="bidactionline initialhidden" id="existingconfirmbuttons">\
    <span class="span-3 inputbutton bidactionbutton" id="owner_existing_cancel_btn">CANCEL</span>\
    <span class="span-3 inputbutton bidactionbutton" id="owner_existing_confirm_btn">CONFIRM</span>\
    <span class="span-14 bidconfirmmessage" id="owner_existing_msg"></span>\
</div>\
        ';
    },

    bindButtons: function() {
        var i,
            action,
            actionfuncname,
            newbidaction = false,
            existingbidaction = false;
        for (i = 0; i < this.validactions.length; i++) {
            action = this.validactions[i];
            actionfuncname = 'bind_' + action;
            this[actionfuncname]();
            if (action === 'owner_post' || action === 'owner_counter') {
                newbidaction = true;
            }
            else if (action === 'owner_accept' || action === 'owner_reject' || action === 'owner_withdraw') {
                existingbidaction = true;
            }
        }
        if (newbidaction) {
            pl('#new_bid_amt, #new_bid_pct, #new_bid_text').removeAttr('disabled');
            pl('#new_bid_boxtitle, #new_bid_boxparent').show();
        }
        if (existingbidaction) {
            this.bindTextNote('existing_bid_text', 'existingbidmsg');
            pl('#existingbidnotebox').show();
        }
    },

    newBidAction: function(type) {
        this.makeBidAction(type, 'new');
    },
    
    existingBidAction: function(type) {
        this.makeBidAction(type, 'existing');
    },

    makeBidAction: function(type, neworexisting) {    
        var self = this,
            complete = function(json) {
                pl('#owner_' + neworexisting + '_msg').addClass('successful').text(self.successtext[type] + ', reloading...');
                setTimeout(function() { location.reload(); }, 3000);
            },

            text = pl('#' + neworexisting + '_bid_text').hasClass('edited')
                && SafeStringClass.prototype.clean(pl('#' + neworexisting + '_bid_text').attr('value') || '') || '',
            rawamt = neworexisting === 'new' ? pl('#new_bid_amt').attr('value') : pl('#existing_bid_amt').text(),
            rawpct = neworexisting === 'new' ? pl('#new_bid_pct').attr('value') : pl('#existing_bid_pct').text(),
            amt = CurrencyClass.prototype.clean(rawamt),
            pct = PercentClass.prototype.clean(rawpct),
            val = CurrencyClass.prototype.clean(pl('#' + neworexisting + '_bid_val').text()),
            data = {
                bid: {
                    listing_id: self.listing_id,
                    investor_id: self.investor_id, // only passed for owner
                    amt: amt,
                    pct: pct,
                    val: val,
                    type: type,
                    text: text
                }
            },

            ajax = new AjaxClass('/listing/make_bid', 'owner_' + neworexisting + '_msg', complete);
        ajax.setPostData(data);
        ajax.call();
    },

    showNewBidConfirmButtons: function(type) {
        var self = this,
            btnid = type + '_btn',
            btnsel = '#' + btnid;
        pl('#new_bid_amt, #new_bid_pct, #new_bid_text').attr('disabled', 'disabled');
        pl('#newbidbuttons').hide();
        pl('#owner_new_confirm_btn').unbind().bind('click', function() {
            if (!pl('#owner_new_confirm_btn').hasClass('submitting')) {
                pl('#owner_new_confirm_btn, #owner_new_cancel_btn').css({visibility: 'hidden'});
                pl('#owner_new_confirm_btn').addClass('submitting');
                pl('#owner_new_msg').addClass('inprogress').text('Submitting...');
                self.newBidAction(type);
            }
        });
        pl('#owner_new_cancel_btn').unbind().bind('click', function() {
            if (!pl('#owner_new_confirm_btn').hasClass('submitting')) {
                pl('#newconfirmbuttons').hide();
                pl('#newbidbuttons').show();
                pl('#new_bid_amt, #new_bid_pct, #new_bid_text').removeAttr('disabled');
            }
        });
        pl('#owner_new_msg').html(this.confirmtext[type]);
        pl('#newconfirmbuttons').show();
    },

    showExistingBidConfirmButtons: function(type) {
        var self = this,
            btnid = type + '_btn',
            btnsel = '#' + btnid;
        pl('#existing_bid_text').attr('disabled', 'disabled');
        pl('#existingbidbuttons').hide();
        pl('#owner_existing_confirm_btn').unbind().bind('click', function() {
            if (!pl('#owner_existing_confirm_btn').hasClass('submitting')) {
                pl('#owner_existing_confirm_btn').addClass('submitting');
                pl('#owner_existing_confirm_btn, #owner_existing_cancel_btn').css({visibility: 'hidden'});
                pl('#owner_existing_msg').addClass('inprogress').text('Submitting...');
                self.existingBidAction(type);
            }
        });
        pl('#owner_existing_cancel_btn').unbind().bind('click', function() {
            if (!pl('#owner_existing_confirm_btn').hasClass('submitting')) {
                pl('#existingconfirmbuttons').hide();
                pl('#existingbidbuttons').show();
                pl('#existing_bid_text').removeAttr('disabled');
            }
        });
        pl('#owner_existing_msg').html(this.confirmtext[type]);
        pl('#existingconfirmbuttons').show();
    },

    bindNewBidActionButton: function(type) {
        var self = this,
            btnid = type + '_btn',
            btnsel = '#' + btnid;
        pl(btnsel).bind('click', function() {
            var validamt = self.amtfield.validate(),
                validpct = self.pctfield.validate(),
                validmsg = '' + (validamt ? 'AMOUNT: ' + validamt + ' ' : '') + (validpct ? 'PERCENT: ' + validpct : '');
            if (validmsg) {
                self.amtfield.fieldBase.msg.show('attention', validmsg);
            }
            else {
                self.showNewBidConfirmButtons(type);
            }
            return false;
        }).show();
    },

    bindExistingBidActionButton: function(type) {
        var self = this,
            btnid = type + '_btn',
            btnsel = '#' + btnid;
        pl(btnsel).bind('click', function() {
            self.showExistingBidConfirmButtons(type);
            return false;
        }).show();
    },

    bind_owner_post: function(type) {
        this.bindNewBidActionButton('owner_post');
    },

    bind_owner_counter: function() {
        this.bindNewBidActionButton('owner_counter');
    },

    bind_owner_accept: function() {
        this.bindExistingBidActionButton('owner_accept');
    },

    bind_owner_reject: function() {
        this.bindExistingBidActionButton('owner_reject');
    },

    bind_owner_withdraw: function() {
        this.bindExistingBidActionButton('owner_withdraw');
    },

    bindBidBox: function() {
        if (!pl('#new_bid_box').hasClass('bound')) {
            this.bindFields();
            this.bindButtons();
            pl('#new_bid_box').addClass('bound');
        }
    }
});

