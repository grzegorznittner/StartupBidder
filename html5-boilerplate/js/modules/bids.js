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
    this.number = new NumberClass();
    this.html = '';
    this.eventBinders = [];
}
pl.implement(BidsClass, {
    load: function() {
        this.ajax.call();
    },
    store: function(json) {
        pl('.bidtitle,.bidpanel').remove();
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
            if (bid.status === 'posted') { // FIXME: posted has disappeared
                bid.status = 'active';
            }
            if (bid.status === 'active' && !bid.valuation) { // FIXME: sometimes backend has zero bid valuation
                this.recalculateValuation(bid);
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
                bid_id: Math.floor(Math.random()*10000000000), // just need something to be unique
                profile_id: this.loggedin_profile_id,
                profile_username: (this.loggedin_username || 'user'), // FIXME: use user's name
                bid_date: '2011-12-12', // FIXME: today
                listing_profile_id: this.listing.profile_id,
                listing_id: this.listing.listing_id,
                amount: this.listing.suggested_amt,
                equity_pct: this.listing.suggested_pct,
                bid_type: 'preferred', // HARDCODE
                interest_rate: 0, // HARDCODE
                bid_note: 'Note to counterparty...',
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
        if (this.userCanMakeNewBid() && !this.userHasBidHistory()) {
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
        for (i = 0; i < this.eventBinders.length; i++) {
            this.eventBinders[i]();
        }
        this.eventBinders = [];
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
    userHasBidHistory: function() {
        var has = false;
        for (i = 0; i < this.bidorder.length; i++) {
            headbid = this.bidorder[i];
            if (headbid.profile_id === this.loggedin_profile_id) {
                has = true;
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
        if (bid.actions.length > 0) { // actionable bid
            this.makeBidHeader(bid); 
            this.makeActionableBid(bid);
            if (bidlist.length > 1) {
                this.makeBidSpacer();
            }
            start = 1; // skip first bid since we just processed it
        }
        else if (profile_id === this.loggedin_profile_id && this.userCanMakeNewBid()) {
            bid = this.makeNewBid();
            this.makeBidHeader(bid); 
            this.makeActionableBid(bid);
            this.makeBidSpacer();
        }
        else {
            this.makeBidHeader(bid); 
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
        var actor = bid.mybid ? 'You' : ((bid.status === 'accepted' || bid.ownerbid) ? 'Owner' : 'Investor'),
            action = bid.status === 'active' ? 'made' : (bid.status === 'withdrawn' ? 'withdrew' : bid.status),
            bidIcon = 'bid' + bid.status + 'icon',
            bidNote = bid.bid_note ? this.safeStr.htmlEntities(bid.bid_note) : (
                actor === 'Owner'
                ? (Math.random() > 0.5 ? 'My company is worth more than this' : 'There is a huge opportunity in investing in me')
                : (Math.random() > 0.5 ? 'This is the best I can offer' : 'I see a lot of potential here')
            ), // FIXME: should usually be bid note
            displayType = 'preferred stock';
        this.recalculateValuation(bid); // FIXME: backend is screwed up
        this.html += '\
<dt class="bidsummary" id="bid_' + bid.bid_id + '">\
<div>\
    <div class="normalicon ' + bidIcon + '"></div>\
    ' + actor + ' ' + action + ' the bid on ' + this.date.format(bid.bid_date) + '\
</div>\
<div>\
    ' + this.currency.format(bid.amount) + ' for ' + bid.equity_pct + '% equity at a valuation of ' + this.currency.format(bid.valuation) + '\
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
            title = bid.status === 'new' ? 'MAKE A BID' : 'BIDS FROM ' + displayUsername + ' SINCE ' + displayDate;
        this.html += '\
            <div class="boxtitle bidtitle" id="' + bidtitleid + '">' + title + '</div>\
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
                'new': 'Make a bid for your piece of the company',
                'active': (bid.actions && bid.actions[0] === 'accept') ? 'Accept, reject, or counter this bid' : 'Revise or withdraw this bid',
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
            iconType = 'bidactionicon' + ((action === 'counter' || action === 'withdraw') ? 'narrow' : ''),
            icon = 'bid' + action + 'icon',
            textid = 'bid_text_' + action + '_' + bid.bid_id,
            text = action === 'makebid' ? 'MAKE BID' : action.toUpperCase(); 
        this.html += '\
            <span class="' + pushclass + ' span-4 inputbutton bidactionbtn" id="' + id + '">\
                <div class="' + iconType + ' ' + icon + '"></div>\
                <span id="' + textid + '">' + text + '</span>\
            </span>\
        ';
    },
    makeActionButtonEvents: function(bid) {
        var i,
            action,
            actionFunc,
            actionMap = {
                'makebid': this.makeMakeBidEvent,
                'withdraw': this.makeWithdrawBidEvent,
                'revise': this.makeReviseBidEvent
            };
        for (i = 0; i < bid.actions.length; i++) {
            action = bid.actions[i];
            actionFunc = actionMap[action];
            if (actionFunc) {
                actionFunc.call(this, bid);
            }
            else {
                console.log('unsupported action:', action);
            }
        }
    },
    makeMakeBidEvent: function(bid) {
        var action = 'makebid',
            id = 'bid_action_' + action + '_' + bid.bid_id,
            sel = '#' + id,
            self = this;
        self.eventBinders.push(function() {
            pl(sel).bind('click', self.makeMakeBidAPICaller(bid));
        });
    },
    makeMakeBidAPICaller: function(bid) {
        var self = this;
        return function() {
            var bidid = bid.bid_id,
                amt = self.currency.clean(pl('#bidamt_'+bidid).attr('value')),
                pct = self.number.clean(pl('#bidpct_'+bidid).attr('value')),
                note = self.safeStr.clean(pl('#bidnote_'+bidid).attr('value')),
                newbid = {
                    listing_id: bid.listing_id,
                    profile_id: self.loggedin_profile_id,
                    amount: amt,
                    equity_pct: pct,
                    bid_note: note,
                    bid_type: 'preferred',
                    interest_rate: 0
                },
                completeFunc = function(json) {
                    var new_bidid = json.bid_id,
                        activatedFunc = function() { // FIXME: activation unnecessary in new model
                            pl('#bidmsg_'+bidid).html('Bid activated');
                            self.load();
                        },
                        ajax = new AjaxClass('/bid/activate?id='+new_bidid, 'bidmsg_'+bidid, activatedFunc);
                    pl('#bidmsg_'+bidid).html('Bid posted');
                    ajax.setPost();
                    //ajax.call();// doesn't actually work since i'm not the owner
                    activatedFunc(); // FIXME: remove with greg's new stuff
                },
                ajax = new AjaxClass('/bid/create', 'bidmsg_'+bidid, completeFunc);
            ajax.setPostData({bid: newbid});
            ajax.call();
        };
    },
    makeWithdrawBidEvent: function(bid) {
        var action = 'withdraw',
            revaction = 'revise',
            cancelaction = 'cancel',
            id = 'bid_action_' + action + '_' + bid.bid_id,
            sel = '#' + id,
            revid = 'bid_action_' + revaction + '_' + bid.bid_id,
            revsel = '#' + revid,
            cancelid = 'bid_action_' + cancelaction + '_' + bid.bid_id,
            cancelsel = '#' + cancelid,
            msgid = 'bidmsg_' + bid.bid_id,
            msgsel = '#' + msgid,
            textid = 'bid_text_' + action + '_' + bid.bid_id,
            textsel = '#' + textid,
            self = this;
        this.eventBinders.push(function() {
            pl(sel).bind('click', function() {
                var bidid = bid.bid_id,
                    completeFunc = function() {
                        pl(msgsel).html('BID WITHRDAWN');
                        self.load();
                    },
                    ajax = new AjaxClass('/bid/withdraw?id='+bidid, msgid, completeFunc);
                if (!pl(msgsel).hasClass('cancelable')) {
                    pl(msgsel).addClass('cancelable').addClass('attention').html('ARE YOU SURE YOU WANT TO WITHDRAW THIS BID?');
                    pl(revsel).hide();
                    self.html = '';
                    self.makeActionButton(bid, 'cancel', 'push-4');
                    pl(sel).removeClass('push-4').addClass('push-3').after(self.html);
                    self.html = '';
                    pl(cancelsel).bind('click', function() {
                        pl(msgsel).removeClass('cancelable').removeClass('attention').html('Revise or withdraw this bid');
                        pl(cancelsel).remove();
                        pl(sel).removeClass('push-3').addClass('push-4');
                        pl(revsel).show();
                    });
                }
                else {
                    ajax.setPost();
                    ajax.call();
                }
            });
        });
    },
    makeReviseBidEvent: function(bid) {
        // FIXME: implemented as withdraw + make
        var action = 'revise',
            withdrawaction = 'withdraw',
            cancelaction = 'cancel',
            id = 'bid_action_' + action + '_' + bid.bid_id,
            sel = '#' + id,
            withdrawid = 'bid_action_' + withdrawaction + '_' + bid.bid_id,
            withdrawsel = '#' + withdrawid,
            cancelid = 'bid_action_' + cancelaction + '_' + bid.bid_id,
            cancelsel = '#' + cancelid,
            msgid = 'bidmsg_' + bid.bid_id,
            msgsel = '#' + msgid,
            textid = 'bid_text_' + action + '_' + bid.bid_id,
            textsel = '#' + textid,
            self = this;
        this.eventBinders.push(function() {
            pl(sel).bind('click', function() {
                var bidid = bid.bid_id,
                    makebid = self.makeMakeBidAPICaller(bid),
                    completeFunc = function() {
                        makebid();
                    },
                    ajax = new AjaxClass('/bid/withdraw?id='+bidid, msgid, completeFunc);
                if (!pl(msgsel).hasClass('cancelable')) {
                    pl(msgsel).addClass('cancelable').html('Make a revision to your existing bid');
                    pl(withdrawsel).hide();
                    self.html = '';
                    self.makeActionButton(bid, 'cancel', 'push-4');
                    pl(sel).after(self.html);
                    self.html = '';
                    self.enableBidForEdit(bid);
                    pl(cancelsel).bind('click', function() {
                        self.disableBidForEdit(bid);
                        pl(msgsel).removeClass('cancelable').html('Revise or withdraw this bid');
                        pl(cancelsel).remove();
                        pl(withdrawsel).show();
                    });
                }
                else {
                    ajax.setPost();
                    ajax.call();
                }
            });
        });
       
    },
    makeActionableBid: function(bid) {
        var self = this,
            bidamtid = 'bidamt_' + bid.bid_id,
            bidpctid = 'bidpct_' + bid.bid_id,
            bidnoteid = 'bidnote_' + bid.bid_id,
            bidmsgid = 'bidmsg_' + bid.bid_id,
            bidvalid  = 'bidval_' + bid.bid_id,
            bidamt = this.currency.format(bid.amount),
            bidpct = bid.equity_pct,
            bidnote = bid.bid_note || 'Note to counterparty...',
            bidval = this.currency.format(bid.valuation),
            bidmsg = this.bidStatusMsg(bid),
            disabled = bid.status !== 'new' ? ' disabled="disabled"' : '';
        this.html += '\
    <div>\
        <span class="bidformlabel">\
            <input class="text bidinputtitle" type="text" maxlength="8" size="8" id="' + bidamtid + '" name="' + bidamtid + '" value="' + bidamt + '"' + disabled +'></input>\
        </span>\
        <span class="bidformtext span-1">FOR</span>\
        <span class="bidformitem">\
            <input class="text bidinputtextpct" type="text" maxlength="2" size="2" id="' + bidpctid + '" name="' + bidpctid + '" value="' + bidpct + '"' + disabled +'></input>\
        </span>\
        <span class="bidformtext span-7">%&nbsp;&nbsp;EQUITY AS PREFERRED STOCK</span>\
    </div>\
    <div>\
        <textarea class="textarea inputfullwidetextshort" id="' + bidnoteid + '" name="' + bidnoteid + '" rows="20" cols="5" value="' + bidnote + '"' + disabled +'>' + bidnote + '</textarea>\
    </div>\
    <p class="inputmsg bidmsg inprogress" id="' + bidmsgid + '">' + bidmsg + '</p>\
    <div>\
        <span class="bidvallabel"><span id="' + bidvalid + '">' + bidval + '</span> VALUATION</span>\
';
        this.makeActionButtons(bid);
        this.html += '\
    </div>\
';
        this.makeActionableBidEvents(bid);
        this.makeActionButtonEvents(bid);
    },
    enableBidForEdit: function(bid) {
        pl(this.editableFieldSel(bid)).removeAttr('disabled');
    },
    disableBidForEdit: function(bid) {
        pl(this.editableFieldSel(bid)).attr({disabled: 'disabled'});
    },
    editableFieldSel: function(bid) {
        var bidamtsel = '#bidamt_' + bid.bid_id,
            bidpctsel = '#bidpct_' + bid.bid_id,
            bidnotesel = '#bidnote_' + bid.bid_id,
            fieldsel = bidamtsel + ',' + bidpctsel + ',' + bidnotesel;
        return fieldsel;
    },
    recalculateValuation: function(bid) {
        bid.valuation = (bid.amount && bid.equity_pct) ? Math.floor(100 * bid.amount / bid.equity_pct) : 0;
    },
    redisplayValuation: function(bid) {
        var bidval = this.currency.format(bid.valuation);
        pl('#bidval_'+bid.bid_id).html(bidval);
    },
    makeActionableBidEvents: function(bid) {
        var self = this;
        this.eventBinders.push(function() {
            var bidamt = {},
                bidpct = {},
                bidamtid = 'bidamt_' + bid.bid_id,
                bidpctid = 'bidpct_' + bid.bid_id,
                bidnoteid = 'bidnote_' + bid.bid_id,
                bidmsgid = 'bidmsg_' + bid.bid_id,
                bidamtsel = '#' + bidamtid,
                bidpctsel = '#' + bidpctid,
                bidnotesel = '#' + bidnoteid,
                bidmsgsel = '#' + bidmsgid,
                action = '';
            bidamt = new TextFieldClass(bidamtid, self.currency.format(bid.amount), function(){}, bidmsgid);
            bidamt.fieldBase.addValidator(self.currency.isCurrency);
            bidamt.fieldBase.validator.preValidateTransform = self.currency.clean;
            bidamt.fieldBase.validator.postValidator = function(result) {
                var src, cleaned, displayed;
                if (result !== 0) {
                    return;
                }
                src = pl(bidamtsel).attr('value');
                cleaned = self.currency.clean(src);
                displayed = self.currency.format(cleaned);
                pl(bidamtsel).attr({value: displayed});
                bid.amount = cleaned;
                self.recalculateValuation(bid);
                self.redisplayValuation(bid);
            };
            bidamt.bindEvents();
            bidpct = new TextFieldClass(bidpctid, bid.equity_pct, function(){}, bidmsgid);
            pctValidator = function(num) {
                if (num < 5) {
                    return 'Minimum bid is 5% of common';
                }
                else if (num > 50) {
                    return 'Maximum bid is 50% of common';
                }
                else {
                    return 0;
                }
            };
            bidpct.fieldBase.addValidator(self.number.isNumber);
            bidpct.fieldBase.addValidator(pctValidator);
            bidpct.fieldBase.validator.preValidateTransform = self.number.clean;
            bidpct.fieldBase.validator.postValidator = function(result) {
                var src, cleaned, displayed;
                if (result !== 0) {
                    return;
                }
                src = pl(bidpctsel).attr('value');
                cleaned = self.number.clean(src);
                displayed = self.number.format(cleaned);
                pl(bidpctsel).attr({value: displayed});
                bid.equity_pct = cleaned;
                self.recalculateValuation(bid);
                self.redisplayValuation(bid);
            };
            bidpct.bindEvents();
            pl(bidnotesel).bind({
                focus: function() {
                    if (!pl(bidnotesel).hasClass('edited') && pl(bidnotesel).attr('value') === 'Note to counterparty...') {
                        pl(bidnotesel).attr({value: ''});
                        pl(bidmsgsel).html('');
                    }
                },
                change: function() {
                    if (!pl(bidnotesel).hasClass('edited') && pl(bidnotesel).attr('value') !== bid.bid_note) {
                        pl(bidnotesel).addClass('edited');
                    }
                },
                blur: function() {
                    if (!pl(bidnotesel).hasClass('edited')) {
                        pl(bidnotesel).attr({value: bid.bid_note});
                    }
                }
            });
        });
    }
});


