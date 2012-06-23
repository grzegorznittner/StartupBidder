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
        var addnote = options && options.last ? this.bidslist.makeAddNote() : ''; // removing note from accept/reject/withdraw actions
            addbuttons = options && options.last ? this.bidslist.makeAddButtons() : '';
            amtidattr = options && options.last ? ' id="existing_bid_amt"' : '';
            pctidattr = options && options.last ? ' id="existing_bid_pct"' : '';
            validattr = options && options.last ? ' id="existing_bid_val"' : '';
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
            },
            ajax = new AjaxClass('/listing/bids/' + this.listing_id, 'bidtitlemsg', complete);
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
        this.bindBidBox();
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
        amtfield.fieldBase.addValidator(ValidatorClass.prototype.genIsNumberBetween(1000, 500000));
        pctfield.fieldBase.addValidator(ValidatorClass.prototype.genIsNumberBetween(1, 50));
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
        console.log(data);
        ajax.setPostData(data);
        //self.mock_make_bid(ajax, data); // FIXME
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

(new SingleInvestorBidListClass()).load();
