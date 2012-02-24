function BidsClass(listing_id) {
    var self;
    self = this;
    this.listing_id = listing_id;
    this.url = '/bids/listing/' + this.listing_id;
    this.statusId = 'bidsmsg';
    this.completeFunc = function(json) {
        self.store(json);
        self.display();
    };
    this.ajax = new AjaxClass(this.url, this.statusId, this.completeFunc);
    this.date = new DateClass();
    this.safeStr = new SafeStringClass();
    this.currency = new CurrencyClass();
    this.html = '';
    this.eventBinders = [];
}
pl.implement(BidsClass, {
    load: function() {
        this.ajax.call();
    },
    store: function(json) {
        this.listing = json.listing || {};
        this.loggedin_profile_id = json.loggedin_profile ? json.loggedin_profile.profile_id : null;
        this.bids = json.bids || [];
        this.filterBids();
        this.enhanceBids();
        this.makeBidProfileMap();
        this.makeBidOrder();
    },
    enhanceBids: function() {
        var i, bid;
        for (i = 0; i < this.bids.length; i++) {
            bid = this.bids[i];
            bid.mybid = (bid.profile_id === this.loggedin_profile_id);
            bid.ownerbid = (bid.profile_id === bid.listing_profile_id);
        }
    },
    filterBids: function() { // remove invisible status bids FIXME
        var filteredbids, i, bid;
        filteredbids = [];
        for (i = 0; i < this.bids.length; i++) {
            bid = this.bids[i];
            if (this.listing.status !== 'active') { // FIXME: backend workaround after closing
                bid.status = 'closed';
            }
            if (bid.status !== 'posted') { // FIXME: posted bids should be eliminated???
                filteredbids.push(bid);
            }
        }
        this.bids = filteredbids;
    },
    makeBidProfileMap: function() {
        var i, bid, profile_id,
            bidmap = {};
        for (i = 0; i < this.bids.length; i++) {
            bid = this.bids[i];
            profile_id = bid.profile_id;
            if (!bidmap[profile_id]) {
                bidmap[profile_id] = [];
            }
            bidmap[profile_id].push(bid);
        }
        var biddatesorter = function(bida, bidb) { // bid date desc
            return bidb.bid_date - bida.bid_date;
        };
        for (profile_id in bidmap) {
            bidmap[profile_id].sort(biddatesorter);
        }
        this.bidmap = bidmap;
    },
    makeBidOrder: function() {
        var headbid,
            profile_id,
            bidrec,
            bidorder = [],
            bidrecsorter = function(bidreca, bidrecb) { // bid date desc
                return bidrecb.bid_date - bidreca.bid_date;
            };
        for (profile_id in this.bidmap) {
            headbid = this.bidmap[profile_id][0];
            bidrec = {
                bid_date: headbid.bid_date,
                profile_id: profile_id,
                status: headbid.status
            }
            bidorder.push(bidrec);
        }
        bidorder.sort(bidrecsorter);
        this.bidorder = bidorder;
    },
    display: function() {
        /* 
        if (this.listing.status !== 'active') {
            //this.displayBidClosed(); // FIXME: uncomment for production
            //return;
        }
        */
        this.displayBids();
    },
    displayBidClosed: function () {
        var str;
        str = "Bidding closed " + -1 * this.listing.days_left + " days ago."; // FIXME: state the winner, what was accepted
        pl('#bidclosedmsg').html(str);
        pl('#bidclosedtitle').show();
        pl('#bidclosedbox').show();
    },
    makeNewBidBox: function() {
        var bid = this.makeNewBid();
        this.makeBidHeader(bid)
        this.makeActionableBid(bid)
        this.makeBidFooter();
    },
    makeNewBid: function() {
        var bid = {
                status: 'new',
                bid_id: '123new', // FIXME: should come from backend
                profile_id: this.loggedin_profile_id,
                profile_username: (this.loggedin_username || 'user'), // FIXME: use user's name
                bid_date: '2011-12-12', // FIXME: today
                listing_profile_id: this.listing.profile_id,
                listing_id: this.listing.listing_id,
                amount: this.listing.suggested_amt,
                equity_pct: this.listing.suggested_pct,
                bid_type: (this.listing.suggested_type || 'common'),
                interest_rate: (this.listing.suggested_rate || 0),
                bid_note: 'Note to the owner...',
                valuation: this.listing.suggested_val
            };
        bid.actions = this.bidActions(bid);
        return bid;
    },
    displayBids: function() {
        var i,
            headbid,
            profile_id,
            bidsForProfile;
        if (this.loggedin_profile_id && !this.userIsOwner() && !this.userHasActiveBid()) {
            this.makeNewBidBox();
        }
        for (i = 0; i < this.bidorder.length; i++) {
            headbid = this.bidorder[i];
            profile_id = headbid.profile_id;
            bidsForProfile = this.bidmap[profile_id];
            if (bidsForProfile.length > 0) {
                this.makeBidsForOneProfile(profile_id);
            }
        }
        pl('#bidclosedbox').after(this.html);
        this.html = '';
    },
    userCanMakeNewBid: function() {
        return (this.loggedin_profile_id && !this.userIsOwner() && !this.userHasActiveBid());
    },
    userHasActiveBid: function() {
        var has = false;
        for (i = 0; i < this.bidorder.length; i++) {
            headbid = this.bidorder[i];
            if (headbid.profile_id === this.loggedin_profile_id) {
                if (headbid.status === 'active' || headbid.status === 'counterowner') { // FIXME: counter status undecided
                    has = true;
                }
                break;
            }
        }
        return has;
    },
    makeBidsForOneProfile: function(profile_id) {
        var i,
            bid,
            bidlist = this.bidmap[profile_id],
            start = 0;
        bid = bidlist[0];
        bid.actions = this.bidActions(bid); // only the first item can have acitons
        this.makeBidHeader(bid); 
        if (bid.actions.length > 0) { // actionable bid
            this.makeActionableBid(bid);
            if (bidlist.length > 1) {
                this.makeBidSpacer();
            }
            start = 1; // skip first bid since we just processed it
        }
        this.startBidList();
        for (i = start; i < bidlist.length; i++) {
            bid = bidlist[i];
            this.makeBidSummary(bid);
        }
        this.endBidList();
        this.makeBidFooter();
    },
    userIsOwner: function() {
        return (this.loggedin_profile_id === this.listing.profile_id);
    },
    bidActions: function(bid) {
        var actions = [];
        if (bid.status === 'new') {
            actions = ['makebid'];
        }
        else if (bid.status === 'active') {
            if (bid.mybid) {
                actions = ['revise', 'withdraw']; // withdraw my bid or counter
            }
            // FIXME: there's currently no way in the current API to know which side countered last
            else if (this.userIsOwner()) {
                actions = ['accept', 'reject', 'counter']; // it's another user's bid
            }
            else if (bid.ownerbid) {
                actions = ['accept', 'reject', 'counter']; // owner counteroffer
            }
            else {
                actions = []; // other investor's bid
            }
        }
        return actions;
    },
    makeBidSummary: function(bid) {
        var actor, action, bidIcon, displayType, bidNote;
        if (bid.mybid) {
            actor = 'You';
        }
        else if (bid.ownerbid) {
            actor = 'Owner';
        }
        else {
            actor = 'Investor';
        }
        if (bid.status === 'accepted') {
            actor = bid.mybid ? 'You' : 'The Owner';
            action = 'accepted the bid';
            bidIcon = 'thumbup';
        }
        else if (bid.status === 'rejected') {
            action = 'rejected the bid';
            bidIcon = 'thumbdownicon';
        }
        else if (bid.status === 'withdrawn') {
            action = 'withdrew the bid';
            bidIcon = 'withdrawicon';
        }
        else if (bid.status === 'countered') { // FIXME: unimplemented
            action = 'made a counteroffer for the bid';
            bidIcon = 'countericon';
        }
        else { // active status
            action = 'made a bid';
            bidIcon = 'bidicon';
        }
        if (bid.bid_type === 'note') {
            displayType = 'a convertible note'; // FIXME: interest rate needed
        }
        else if (bid.bid_type === 'preferred') {
            displayType = 'convertible preferred stock'; // FIXME: interest rate needed
        }
        else {
            displayType = 'common stock';
        }
        bidNote = bid.bid_note ? this.safeStr.htmlEntities(bid.bid_note) : (
            actor === 'The owner'
            ? (Math.random() > 0.5 ? 'My company is worth more than this' : 'There is a huge opportunity in investing in me')
            : (Math.random() > 0.5 ? 'This is the best I can offer' : 'I see a lot of potential here')
        ); // FIXME: should usually be bid note
        this.html += '\
<dt class="bidsummary" id="bid_' + bid.bid_id + '">\
<div>\
    <div class="normalicon ' + bidIcon + '"></div>\
    ' + actor + ' ' + action + ' on ' + this.date.format(bid.bid_date) + '\
</div>\
<div>\
    ' + this.currency.format(bid.amount) + ' for ' + bid.equity_pct + '% equity as ' + displayType +' with company valued at ' + this.currency.format(bid.valuation) + '\
</div>\
</dt>\
<dd class="bidsummary" id="bid_dd_' + bid.bid_id + '">' + bidNote + '</dd>\
';
    },
    makeBidHeader: function(bid) {
        var bidtitleid = 'bidtitle_' + bid.bid_id;
            bidboxid = 'bidbox_' + bid.bid_id;
            displayUsername = bid.mybid ? 'YOU' : bid.profile_username.toUpperCase();
            displayDate = this.date.format(bid.bid_date),
            title = bid.status === 'new' ? 'MAKE A BID' : 'BID FROM ' + displayUsername + ' ON ' + displayDate;
        this.html += '\
            <div class="boxtitle" id="' + bidtitleid + '">' + title + '</div>\
            <div class="boxpanel uneditabletext bidpanel" id="' + bidboxid + '">\
        ';
    },
    makeBidSpacer: function() {
        this.html += '<div class="bidspacer">PREVIOUS BIDS</div>';
    },
    startBidList: function() {
        this.html += '<dl>';
    },
    endBidList: function() {
        this.html += '</dl>';
    },
    makeBidFooter: function() {
        this.html += '</div>';
    },
    bidStatusMsg: function(bid) {
        var statusMap = {
                'new': '',
                'accepted': 'BID ACCEPTED',
                'withdrawn': 'BID WITHDRAWN',
                'countered': 'BID COUNTERED'
            };
        return statusMap[bid.status] || '';
    },
    makeActionButtons: function(bid) {
        var i,
            action, // accept, reject, counter, or withdraw
            pushclass = [];
        if (bid.actions.length === 3) {
            pushclass = ['', '', ''];
        }
        else if (bid.actions.length === 2) {
            pushclass[0] = 'push-3';
            pushclass[1] = 'push-4';
        }
        else {
            pushclass[0] = 'push-8';
        }
        for (i = 0; i < bid.actions.length; i++) {
            action = bid.actions[i];
            this.makeActionButton(bid, action, pushclass[i]);
        }
    },
    makeActionButton: function(bid, action, pushclass) {
        var id = 'bid_action_' + action + '_' + bid.bid_id,
            iconType = 'bidactionicon' + (action === 'counter' ? 'narrow' : ''),
            icon = 'bid' + action + 'icon',
            text = action === 'makebid' ? 'MAKE BID' : action.toUpperCase(); 
        this.html += '\
            <a href="#">\
                <span class="' + pushclass + ' span-4 inputbutton bidactionbtn" id="' + id + '"><div class="' + iconType + ' ' + icon + '"></div>' + text + '</span>\
            </a>\
            ';
    },
    makeActionableBid: function(bid) {
        var self = this,
            bidamtid = 'bidamt_' + bid.bid_id,
            bidpctid = 'bidpct_' + bid.bid_id,
            bidtypeid = 'bidtype_' + bid.bid_id,
            bidrateboxid = 'bidratebox_' + bid.bid_id,
            bidrateid = 'bidrate_' + bid.bid_id,
            bidnoteid = 'bidnote_' + bid.bid_id,
            bidmsgid = 'bidmsg_' + bid.bid_id,
            bidvalid  = 'bidval_' + bid.bid_id,
            bidamt = this.currency.format(bid.amount),
            bidpct = bid.equity_pct,
            bidtype = bid.bid_type,
            bidrate = bid.interest_rate,
            bidnote = bid.bid_note || 'random bid note',
            bidval = this.currency.format(bid.valuation),
            bidmsg = this.bidStatusMsg(bid);
        this.html += '\
    <div>\
        <span class="bidformlabel">\
            <input class="text bidinputtitle" type="text" maxlength="8" size="8" id="' + bidamtid + '" name="' + bidamtid + '" value="' + bidamt + '"></input>\
        </span>\
        <span class="bidformtext span-1">FOR</span>\
        <span class="bidformitem">\
            <input class="text bidinputtextpct" type="text" maxlength="2" size="2" id="' + bidpctid + '" name="' + bidpctid + '" value="' + bidpct + '"></input>\
        </span>\
        <span class="bidformtext span-1">%&nbsp;&nbsp;AS</span>\
        <span class="bidformitem">\
            <select class="bidinputselect" id="' + bidtypeid + '" name="' + bidtypeid + '" value="' + bidtype + '">\
                <option value="common">COMMON STOCK</option>\
                <option value="preferred">PREFERRED STOCK</option>\
                <option value="note">CONVERTIBLE NOTE</option>\
            </select>\
        </span>\
'+(bidrate > 0 ? '\
        <span class="bidformitem" id="' + bidrateboxid + '">\
            <span class="bidformtext">@</span>\
            <input class="text bidinputtextpct pctwide" type="text" maxlength="2" size="2" id="' + bidrateid + '" name="' + bidrateid + '" value="' + bidrate +'"></input>\
            <span class="bidformtext">%</span>\
        </span>\
' : '') + '\
    </div>\
    <div>\
        <textarea class="textarea inputfullwidetextshort" id="' + bidnoteid + '" name="' + bidnoteid + '" rows="20" cols="5" value="' + bidnote + '">' + bidnote + '</textarea>\
    </div>\
    <p class="inputmsg" id="' + bidmsgid + '">' + bidmsg + '</p>\
    <div>\
        <span class="bidvallabel"><span id="' + bidvalid + '">' + bidval + '</span> VALUATION</span>\
';
        this.makeActionButtons(bid);
        this.html += '\
    </div>\
';
        this.eventBinders.push(this.makeActionableBidEvents(bid));
    },
    makeActionableBidEvents: function(bid) {
        var self = this;
        return function() {
            var bidAmt = {},
                bidamtid = 'bidamt_' + bid.bid_id,
                bidamtsel = '#' + bidamtid,
                bidmsgid = 'bidmsg_' + bid.bid_id;
            bidAmt = new TextFieldClass(bidamtid, self.currency.format(self.listing.suggested_amt), function(){}, bidmsgid);
            bidAmt.fieldBase.addValidator(self.currency.isCurrency);
            bidAmt.fieldBase.validator.preValidateTransform = self.currency.clean;
            bidAmt.fieldBase.validator.postValidator = function(result) {
                var src, cleaned, displayed;
                if (result === 0) {
                    src = pl(bidamtsel).attr('value');
                    cleaned = self.currency.clean(src);
                    displayed = self.currency.format(cleaned);
                    pl(bidamtsel).attr({value: displayed});
                }
            };
            bidAmt.bindEvents();
        };
    }
});


