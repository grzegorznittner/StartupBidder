function ListingClass(id) {
    var self = this;
    this.id = id;
    this.url = '/listings/get/' + this.id;
    this.statusId = 'listingstatus';
    this.completeFunc = function(json) {
        var header, listing;
        header = new HeaderClass();
        header.setLogin(json);
        self.store(json);
        self.display();
    };
    this.ajax = new AjaxClass(this.url, this.statusId, this.completeFunc);
};
pl.implement(ListingClass, {
    store: function(json) {
        var key;
        if (json && json.listing && json.listing.listing_id) {
            for (key in json.listing) {
                this[key] = json.listing[key];
            }
        }
        this.dateobj = new DateClass();
        this.currency = new CurrencyClass();
        this.testcompanies = new TestCompaniesClass();
        this.testcompany = this.testcompanies.allCompanies[0]; // FIXME
    },
    load: function() {
        this.ajax.call();
    },
    display: function() {
        this.displayBasics();
        this.displayInfobox();
        this.displayMap();
        this.displayDocuments();
        this.displayFunding();
        this.displaySocial();
        this.displayWithdraw();
    },
    displayBasics: function() {
        this.mantra = this.mantra || this.testcompany.mantra; // FIXME
        this.videourl = this.videourl || this.testcompany.videourl || 'http://www.youtube.com/embed/QoAOzMTLP5s'; // FIXME
        this.logourl = this.logourl || '/img/einstein.jpg';
        pl('#companylogo').attr({src: this.logourl});
        pl('#title').html(this.title);
        pl('title').html('Startupbidder Listing: ' + this.title);
        pl('#profile_username').html(this.profile_username);
        pl('#mantra').html(this.mantra);
        pl('#companystatus').html('Listing is ' + this.status);
        if (this.status === 'withdrawn') {
            pl('#companystatus').addClass('attention');
        }
        pl('#num_votes').html(this.num_votes);
        pl('#num_comments').html(this.num_comments);
        pl('#videopresentation').attr({src: this.videourl});
        pl('#summary').html(this.summary);
        pl('#listingdata').show();
    },
    displayInfobox: function() {
        var legalTypes, url;
        legalTypes = [ 'C Corp', 'S Corp', 'LLC', 'Proprietorship', 'Partnership', 'Limited', 'PLC', 'GmbH', 'SA', 'SRL', 'KK' ];
        this.category = this.category || (Math.floor(Math.random()*2) ? 'INTERNET' : 'SOFTWARE'); // FIXME
        this.legaltype = this.legaltype || legalTypes[Math.floor(Math.random() * legalTypes.length)];
        this.websiteurl = this.websiteurl || 'http://wave.google.com'; // FIXME
        url = new URLClass(this.websiteurl);
        this.domainname = url.getHostname();
        pl('#category').html(this.category);
        pl('#legaltype').html(this.legaltype);
        pl('#listing_date').html(this.listing_date ? this.dateobj.format(this.listing_date) : 'not posted');
        pl('#websitelink').attr({href: this.websiteurl});
        pl('#domainname').html(this.domainname);
    },
    displayMap: function() {
        this.address = this.address || Math.floor(Math.random()*2) ? '221B Baker St, London, UK' : '170W Tasman Dr, San Jose, CA, USA'; // FIXME
        //this.addressurl = this.addressurl || 'http://nominatim.openstreetmap.org/search?q=' + encodeURIComponent(this.address);
        this.addressurl = 'http://maps.google.com/maps?q=' + encodeURI(this.title) + ',' + encodeURI(this.address);
        this.latitude = this.latitude || '51.499117116569'; // FIXME
        this.longitude = this.longitude || '-0.12359619140625'; // FIXME
        //this.mapurl = 'http://ojw.dev.openstreetmap.org/StaticMap/?lat=' + this.latitude + '&lon=' + this.longitude + '&z=5&show=1&fmt=png&w=302&h=302&att=none';
        this.mapurl = 'http://maps.googleapis.com/maps/api/staticmap?center=' + encodeURI(this.address) + '&zoom=10&size=302x298&maptype=roadmap&markers=color:blue%7Clabel:' + encodeURI(this.title) + '%7C' + encodeURI(this.address) + '&sensor=false';
        pl('#address').html(this.address);
        pl('#addresslink').attr({href: this.addressurl});
        pl('#mapimg').attr({src: this.mapurl});
    },
    displayDocumentLink: function(linkId, btnId, docId) {
        var url;
        if (!docId && Math.random() > 0.5) { // FIXME: simulation
            docId = 'ag1zdGFydHVwYmlkZGVych4LEgpMaXN0aW5nRG9jIg5Eb2MtMjExMzY3MzkxOAw';
        }
        if (docId) {
            url = '/file/download/' + docId;
            pl('#'+btnId).addClass('span-3 smallinputbutton').html('DOWNLOAD');
            pl('#'+linkId).attr({href: url});
            
        }
        else {
            pl('#'+btnId).addClass('span-3 doclinkmsg attention').html('NONE');
            pl('#'+linkId).attr({href: '#'}).addClass('nohover').bind({click: function() { return false; }});
        }
    },
    displayDocuments: function() {
        this.displayDocumentLink('presentationlink', 'presentationbtn', this.presentation_id);
        this.displayDocumentLink('businessplanlink', 'businessplanbtn', this.business_plan_id);
        this.displayDocumentLink('financialslink', 'financialsbtn', this.financials_id);
        pl('#documentbox').show();
    },
    displayFunding: function() {
        this.askingFunding = this.askingFunding || (this.suggested_amt > 0 ? true : false);
        if (this.askingFunding) {
            pl('#suggested_amt').html(this.currency.format(this.suggested_amt));
            pl('#suggested_pct').html(this.suggested_pct);
            pl('#suggested_val').html(this.currency.format(this.suggested_val));
            pl('#closingmsg').html(this.closing_date && this.days_left >= 0 ? 'CLOSES ON ' + this.dateobj.format(closing_date) + ' (' + (this.days_left > 0 ? this.days_left + ' DAYS LEFT' : 'CLOSES TODAY!') + ')' : 'BIDDING CLOSED');
            if (this.num_bids && this.num_bids > 0) {
                this.best_bid_pct = 10 + 5*Math.floor(8*Math.random()); // FIXME
                this.best_bid_amt = this.valuation ? Math.floor((this.best_bid_pct / 100) * this.valuation) : 1000 + 1000*Math.floor(99*Math.random());
                this.best_bid_val = this.valuation || (100 * this.best_bid_amt / this.best_bid_pct); // FIXME
                pl('#num_bids').html(this.num_bids);
                pl('#best_bid_amt').html(this.currency.format(this.best_bid_amt));
                pl('#best_bid_pct').html(this.best_bid_pct);
                pl('#best_bid_val').html(this.currency.format(this.best_bid_val));
                pl('#bidboxinfo').show();
            }
            else {
                pl('#bidboxstatus').html('NO BIDS').show();
            }
            pl('#suggestedinfo').show();
            pl('#bidboxtitle').show();
            pl('#bidbox').show();
        }
        else {
            pl('#suggestedmsg').html('NOT SEEKING FUNDING').show();
        }
    },
    displaySocial: function() {
        this.displayFacebook();
        this.displayGooglePlus();
        this.displayTwitter();
    },
    displayFacebook: function() {
        this.addFacebookMetaTags();
        (function(d, s, id) {
        var js, fjs = d.getElementsByTagName(s)[0];
        if (d.getElementById(id)) return;
        js = d.createElement(s); js.id = id;
        js.src = "//connect.facebook.net/en_US/all.js#xfbml=1";
        fjs.parentNode.insertBefore(js, fjs);
        }(document, 'script', 'facebook-jssdk'));
    },
    addFacebookMetaTags: function() {
        var metas, prop, content, metatag;
        metas = {
            'og:title': this.title,
            'og:type': 'company',
            'og:url': 'http://starutpbidder.com/company_page.html?id=' + this.id,
            'og:image': 'http://startupbidder.com' + this.logourl,
            'og:site_name': 'startupbidder',
            'fb:app_id': '3063944677997'
        };
        for (prop in metas) {
            content = metas[prop];
            metatag = '<meta property="' + prop + '" content="' + content + '"/>';
            pl('head').append(metatag);
        }
    },
    displayGooglePlus: function() {
        (function() {
          var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;
          po.src = 'https://apis.google.com/js/plusone.js';
          var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(po, s);
        })();
    } ,
    displayTwitter: function() {
        !function(d,s,id){
            var js,fjs=d.getElementsByTagName(s)[0];
            if(!d.getElementById(id)){
                js=d.createElement(s);
                js.id=id;
                js.src="//platform.twitter.com/widgets.js";
                fjs.parentNode.insertBefore(js,fjs);
            }
        }(document,"script","twitter-wjs");
    },
    displayWithdraw: function() {
        var self;
        self = this;
        if (this.status === 'withdrawn') {
            return;
        }
        pl('#withdrawbtn').bind({
            click: function() {
                if (pl('#withdrawmsg').html() !== '&nbsp;') {
                    self.withdraw();
                }
                else {
                    pl('#withdrawmsg').html('ARE YOU SURE?');
                    pl('#withdrawbtn').html('YES, WITHDRAW');
                }
            }
        });
        pl('#withdrawbox').bind({
            mouseout: function() {
                if (pl('#withdrawmsg').html() !== '&nbsp;' && self.status !== 'withdrawn') {
                    pl('#withdrawmsg').html('&nbsp;');
                    pl('#withdrawbtn').html('WITHDRAW LISTING');
                }
            }
        });
        pl('#withdrawtitle').show();
        pl('#withdrawbox').show();
    },
    withdraw: function() {
        var self, url, completeFunc, ajax;
        self = this;
        url = '/listing/withdraw/' + this.listing_id;
        completeFunc = function() {
            pl('#withdrawmsg').addClass('successful').html('LISTING WITHDRAWN');
            pl('#withdrawbtn').hide();
            self.status = 'withdrawn';
            pl('#companystatus').html('Listing is ' + self.status).addClass('attention');
             
        };
        ajax = new AjaxClass(url, 'withdrawmsg', completeFunc);
        ajax.setPost();
        ajax.call();
    }
});

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
}
pl.implement(BidsClass, {
    load: function() {
        this.ajax.call();
    },
    store: function(json) {
        this.listing = json.listing || {};
        this.loggedin_profile_id = json.loggedin_profile ? json.loggedin_profile.profile_id : null;
        this.bids = this.enhanceBids(this.filterBids(json.bids || []));
        this.bidmap = this.getBidProfileMap();
        this.bidorder = this.getBidOrder();
    },
    enhanceBids: function(bids) {
        var i, bid;
        for (i = 0; i < bids.length; i++) {
            bid = bids[i];
            bid.mybid = (bid.profile_id === this.loggedin_profile_id);
            bid.ownerbid = (bid.profile_id === bid.listing_profile_id);
        }
        return bids;
    },
    filterBids: function(bids) { // remove invisible status bids FIXME
        var filteredbids, i, bid;
        filteredbids = [];
        for (i = 0; i < bids.length; i++) {
            bid = bids[i];
            if (this.listing.status !== 'active') { // FIXME: backend workaround after closing
                bid.status = 'closed';
            }
            if (bid.status !== 'posted') { // FIXME: posted bids should be eliminated???
                filteredbids.push(bid);
            }
        }
        return filteredbids;
    },
    getBidProfileMap: function() {
        var bidmap, i, bid, profile_id;
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
        return bidmap;
    },
    getBidOrder: function() {
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
        return bidorder;
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
/*
        makebidAmt = new TextFieldClass('makebid_amt', this.currency.format(this.listing.suggested_amt), function(){}, 'makebid_msg');
        makebidAmt.fieldBase.addValidator(this.currency.isCurrency);
        makebidAmt.fieldBase.validator.preValidateTransform = this.currency.clean;
        makebidAmt.fieldBase.validator.postValidator = function(result) {
            var src, cleaned, displayed;
            if (result === 0) {
                src = pl('#makebid_amt').attr('value');
                cleaned = self.currency.clean(src);
                displayed = self.currency.format(cleaned);
                pl('#makebid_amt').attr({value: displayed});
            }
        };
        makebidAmt.bindEvents();
*/
        var bid = this.makeNewBid(),
            html = this.makeBidHeader(bid)
                + this.makeActionableBid(bid)
                + this.makeBidFooter();
        return html;
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
            bidsForProfile,
            html = '';
        if (this.loggedin_profile_id && !this.userIsOwner() && !this.userHasActiveBid()) {
            html += this.makeNewBidBox();
        }
        for (i = 0; i < this.bidorder.length; i++) {
            headbid = this.bidorder[i];
            profile_id = headbid.profile_id;
            bidsForProfile = this.bidmap[profile_id];
            if (bidsForProfile.length > 0) {
                html += this.makeBidsForOneProfile(profile_id);
            }
        }
        pl('#bidclosedbox').after(html);
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
            html = '',
            start = 0;
        bid = bidlist[0];
        bid.actions = this.bidActions(bid); // only the first item can have acitons
        html += this.makeBidHeader(bid); 
        if (bid.actions.length > 0) { // actionable bid
            html += this.makeActionableBid(bid);
            if (bidlist.length > 1) {
                html += this.makeBidSpacer();
            }
            start = 1; // skip first bid since we just processed it
        }
        html += this.startBidList();
        for (i = start; i < bidlist.length; i++) {
            bid = bidlist[i];
            html += this.makeBidSummary(bid);
        }
        html += this.endBidList();
        html += this.makeBidFooter();
        return html;
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
                actions = ['withdraw']; // withdraw my bid or counter
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
        return '\
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
        return '\
            <div class="boxtitle" id="' + bidtitleid + '">' + title + '</div>\
            <div class="boxpanel uneditabletext bidpanel" id="' + bidboxid + '">\
        ';
    },
    makeBidSpacer: function() {
        return '<div class="bidspacer">PREVIOUS BIDS</div>';
    },
    startBidList: function() {
        return '<dl>';
    },
    endBidList: function() {
        return '</dl>';
    },
    makeBidFooter: function() {
        return '</div>';
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
            pushclass = bid.actions.length < 3 ? 'push-' + (4*(3-bid.actions.length)) : 0,
            html = '';
        for (i = 0; i < bid.actions.length; i++) {
            action = bid.actions[i];
            html += this.makeActionButton(bid, action, (i === 0 ? pushclass : ''));
        }
        return html;
    },
    makeActionButton: function(bid, action, pushclass) {
        var id = 'bid_action_' + action + '_' + bid.bid_id,
            iconType = 'bidactionicon' + (action === 'counter' ? 'narrow' : ''),
            icon = 'bid' + action + 'icon',
            text = action === 'makebid' ? 'MAKE BID' : action.toUpperCase(); 
        return '\
            <a href="#">\
                <span class="' + pushclass + ' span-4 inputbutton bidactionbtn" id="' + id + '"><div class="' + iconType + ' ' + icon + '"></div>' + text + '</span>\
            </a>\
            ';
    },
    makeActionableBid: function(bid) {
        var bidamtid = 'bidamt_' + bid.bid_id,
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
            bidmsg = this.bidStatusMsg(bid),
            buttons = this.makeActionButtons(bid);
return '<div>\
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
' + buttons + '\
    </div>\
';
    }
});

function CommentsClass(listing_id) {
    var self;
    self = this;
    this.listing_id = listing_id;
    this.url = '/comments/listing/' + this.listing_id;
    this.statusId = 'commentsmsg';
    this.completeFunc = function(json) {
        self.store(json);
        self.display();
    };
    this.ajax = new AjaxClass(this.url, this.statusId, this.completeFunc);
    this.date = new DateClass();
    this.safeStr = new SafeStringClass();
}
pl.implement(CommentsClass, {
    load: function() {
        this.ajax.call();
    },
    store: function(json) {
        this.comments = json.comments || [];
        this.loggedin_profile_id = json.loggedin_profile ? json.loggedin_profile.profile_id : null;
    },
    display: function() {
        this.displayAddCommentBox();
        this.displayComments();
    },
    displayAddCommentBox: function() {
        var self;
        self = this;
        if (!this.loggedin_profile_id) {
            return;
        }
        pl('#addcommenttext').bind({
            focus: function() {
                if (!pl('#addcommenttext').hasClass('edited')) {
                    pl('#addcommenttext').attr({value: ''});
                    pl('#addcommentmsg').html('');
                }
            },
            change: function() {
                pl('#addcommenttext').addClass('edited').attr({value: ''});
                pl('#addcommentmsg').html('');
            },
            blur: function() {
                if (!pl('#addcommenttext').hasClass('edited')) {
                    pl('#addcommenttext').attr({value: 'Put your comment here...'});
                }
            },
            keyup: function(event) {
                var keycode, completeFunc, safeStr, commentText, ajax;
                keycode = event.keyCode || event.which;
                if (keycode && keycode === 13) {
                    completeFunc = function() {
                        pl('#addcommenttext').removeClass('edited').get(0).blur();
                        pl('#addcommentmsg').html('Comment posted');
                        self.load();
                    };
                    safeStr = new SafeStringClass();
                    commentText = safeStr.htmlEntities(safeStr.trim(pl('#addcommenttext').attr('value')));
                    ajax = new AjaxClass('/comment/create', 'addcommentmsg', completeFunc);
                    ajax.setPostData({
                        comment: {
                            listing_id: self.listing_id,
                            profile_id: self.loggedin_profile_id,
                            text: commentText
                        }
                    });
                    ajax.call();
                }
                return false;
            }
        });
        pl('#addcommenttitle').show();
        pl('#addcommentbox').show();            
    },
    displayComments: function() {
        var html, deletableComments, i, comment, deletable, commentDeleteSel;
        if (this.comments.length === 0) {
            pl('#commentlist').hide();
            pl('#commentsmsg').html('No comments').show();
            return;
        }
        html = '';
        deletableComments = [];
        for (i = 0; i < this.comments.length; i++) {
            comment = this.comments[i];
            deletable = false;
            if (comment.profile_id === this.loggedin_profile_id) {
                deletableComments.push(comment);
                deletable = true;
            }
            html += this.makeComment(comment, deletable);
        }
        pl('#commentmsg').hide();
        pl('#commentlist').html(html).show();
        for (i = 0; i < deletableComments.length; i++) {
            comment = this.comments[i];
            commentDeleteSel = '#comment_delete_' + comment.comment_id;
            pl(commentDeleteSel).bind({click: this.deleteCommentGenerator(comment)});
        }
    },
    deleteCommentGenerator: function(comment) {
        var commentId = comment.comment_id;
        return function() {
            var commentmsgId, commentDelUrl, completedFunc, ajax;
            commentmsgId = 'comment_delete_msg_' + commentId;
            commentDelUrl = '/comment/delete/' + commentId;
            completedFuncGenerator = function(commentId) {
                var commentSel, commentddSel;
                commentSel = '#comment_' + commentId;
                commentddSel = '#comment_dd_' + commentId;
                return function() {
                    pl(commentSel).remove();
                    pl(commentddSel).remove();
                };
            };
            ajax = new AjaxClass(commentDelUrl, commentmsgId, completedFuncGenerator(commentId));
            ajax.setPost();
            ajax.call();
        };
    },
    makeComment: function(comment, deletable) {
        return '\
<dt id="comment_' + comment.comment_id + '">\
<div class="commentdttitle">\
<div class="commentdttitleline">Posted by ' + comment.profile_username + ' on ' + this.date.format(comment.comment_date) + '\
    ' + (deletable ? ' <span id="comment_delete_msg_' + comment.comment_id + '"></span>' : '') + '\
</div>\
' + (deletable ? '<div id="comment_delete_' + comment.comment_id + '" class="commentdelete checkboxredicon"></div>' : '') + '\
</div>\
</dt>\
<dd id="comment_dd_' + comment.comment_id + '">' + this.safeStr.htmlEntities(comment.text) + '</dd>\
';
    }
});

function ListingPageClass() {
    if (!this.queryString) {
        this.queryString = new QueryStringClass();
        this.queryString.load();
    }
    this.id = this.queryString.vars.id;
};
pl.implement(ListingPageClass,{
    loadPage: function() {
        var listing, bids, comments;
        listing = new ListingClass(this.id);
        bids = new BidsClass(this.id);
        comments = new CommentsClass(this.id);
        companies = new RelatedCompaniesClass(this.id);
        listing.load();
        bids.load();
        comments.load();
        companies.load();
    }
});

(new ListingPageClass()).loadPage();

