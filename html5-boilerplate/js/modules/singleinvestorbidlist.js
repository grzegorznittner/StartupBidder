function BidClass(bidslist) {
    this.bidslist = bidslist;
    this.typeclassmap = {
        investor_post: 'inprogress',
        investor_counter: 'inprogress',
        investor_accept: 'successful',
        investor_reject: 'errorcolor',
        investor_withdraw: 'errorcolor',
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
        this.pcttext = this.pct ? PercentClass.prototype.format(this.pct) : '';
        this.valtext = this.val ? CurrencyClass.prototype.format(this.val) : '';
        this.typetext = this.type ? this.type.replace(/(investor_|owner_)/, '') : '';
        this.bidtext = this.text ? SafeStringClass.prototype.htmlEntities(this.text) : '&nbsp;';
        this.datetext = this.created_date ? DateClass.prototype.format(this.created_date) : '';
        //this.usertext = this.type.indexOf('INVESTOR') ? this.bidslist.investorusername : this.bidslist.ownerusername;
        this.usertext = this.type && this.type.match(/investor/) ? 'You' : 'Owner';
        this.typeclass = this.typeclassmap[this.type] || '';
        return this;
    },

    setEmpty: function() {
        var emptyJson = {
                amt: null,
                pct: null,
                val: null,
                type: null,
                text: 'No bids',
                created_date: null
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
        var addbuttons = options && options.last ? this.bidslist.makeAddButtons() : '';
        return '\
        <div class="messageline investorbidline ' + this.typeclass + '">\
            <p class="span-2">' + this.usertext + '</p>\
            <p class="span-2">' + this.typetext + '</p>\
            <p class="span-3">' + this.amttext + '</p>\
            <p class="span-2">' + this.pcttext + '</p>\
            <p class="span-3">' + this.valtext + '</p>\
            <p class="span-9 investorbidnote">' + this.bidtext + '</p>\
            <p class="investorbiddate">' + this.datetext + '</p>\
        ' + addbuttons + '\
        </div>\
        ';
    }
});

function SingleInvestorBidListClass(listing_id, investor_profile_id, investor_profile_username) {
    this.listing_id = listing_id;
    this.investor_profile_id = investor_profile_id;
    this.investor_profile_username = investor_profile_username;
    this.confirmtext = {
        investor_accept: 'You hereby agree to accept this bid according to the <a href="/terms-page.html">terms and conditions</a>.',
        investor_reject: 'You hereby agree to reject this bid according to the <a href="/terms-page.html">terms and conditions</a>.',
        investor_withdraw: 'You hereby agree to withdraw this bid according to the <a href="/terms-page.html">terms and conditions</a>.',
        investor_post: 'You hereby agree to make this bid according to the <a href="/terms-page.html">terms and conditions</a>.',
        investor_counter: 'You hereby agree to make this counter offer according to the <a href="/terms-page.html">terms and conditions</a>.'
    };
}
pl.implement(SingleInvestorBidListClass, {
    mock: function(ajax) {
        ajax.mock({
bids:
[
    {
        amt: '20000',
        pct: '5',
        val: '400000',
        type: 'investor_post',
        text: 'Is is a great idea, let us see if we can make it happen',
        created_date: '20120528183623'
    },

    {
        amt: '20000',
        pct: '5',
        val: '400000',
        type: 'owner_reject',
        text: 'Not enough money for me to proceed, but thank you for your interest',
        created_date: '20120528191242'
    },

    {
        amt: '40000',
        pct: '10',
        val: '400000',
        type: 'investor_post',
        text: 'Here is a little more money, naturally I will require more shares as part of the deal',
        created_date: '20120528213422'
    },

    {
        amt: '40000',
        pct: '5',
        val: '800000',
        type: 'owner_counter',
        text: 'Well not that much equity, but it is looking a little more in line with what I have been thinking.',
        created_date: '20120528214814'
    },

    {
        amt: '40000',
        pct: '10',
        val: '400000',
        type: 'investor_counter',
        text: 'It looks like the money is agreeable, however as I indicated before I need more equity upside to be compensated fairly',
        created_date: '20120528231215'
    },

    {
        amt: '40000',
        pct: '10',
        val: '400000',
        type: 'owner_accept',
        text: 'Okay I am comfortable with these terms, we have a deal',
        created_date: '20120528232341'
    }
],

bids_props: {
     "start_index": 7,
     "max_results": 0,
     "num_results": 7,
     "more_results_url": null
},

valid_actions: [ "investor_post", "investor_counter", "investor_reject", "investor_accept", "investor_withdraw" ]
        });
    },

    mock_make_bid: function(ajax, data) {
        ajax.mock({
bids:
[
    {
        amt: data.bid.amt,
        pct: data.bid.pct,
        val: data.bid.val,
        type: data.bid.type,
        text: data.bid.text,
        created_date: DateClass.prototype.now()
    }
],

bids_props: {
     "start_index": 0,
     "max_results": 1,
     "num_results": 1,
     "more_results_url": null
},

valid_actions: [ "investor_counter", "investor_reject", "investor_accept", "investor_withdraw" ]
        });
    },

    load: function() {
        var self = this,
            complete = function(json) {
                self.display(json);
            },

            ajax = new AjaxClass('/listing/bids/' + this.listing_id + '/' + this.investor_profile_id, 'bidtitlemsg', complete);
        this.mock(ajax); // FIXME
        ajax.call();
    },

    store: function(json) {
        var validactions = json && json.valid_actions || [],
            bidsprops = json && json.bids_props || {},
            jsonlist = json && json.bids || [],
            bid,
            i;
        this.investorusername = this.investor_profile_username;
        this.ownerusername = 'owner';
        this.bidsprops = bidsprops;
        this.validactions = validactions;
        this.bids = [];
        if (jsonlist.length) {
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
    },

    displayCalculatedIfValid: function() {
        var amt = CurrencyClass.prototype.clean(pl('#makebidamt').attr('value')) || 0,
            pct = PercentClass.prototype.clean(pl('#makebidpct').attr('value')) || 0,
            val = pct ? Math.floor(Math.floor(100 * amt / pct)) : 0,
            cur = CurrencyClass.prototype.format(CurrencyClass.prototype.clean(val)),
            dis = cur || '';
        pl('#makebidval').removeClass('inprogress').addClass('successful').text(dis);
    },

    getUpdater: function(fieldName, cleaner) {
        var self = this;
        return function(newdata, loadFunc, errorFunc, successFunc) {
            var newval = (newdata ? (cleaner ? cleaner(newdata.changeKey) : newdata.changeKey) : undefined);
            if (newval) {
                console.log(this);
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
            pl('#makebidamt').attr({value: fmt});
        }
    },

    displayIfValidPct: function(result, val) {
        var fmt = PercentClass.prototype.format(val);
        if (result === 0) {
            pl('#makebidpct').attr({value: fmt});
        }
    },

    bindFields: function() {
        var amtfield = new TextFieldClass('makebidamt', null, this.getUpdater('makebidamt', CurrencyClass.prototype.clean), 'makebidmsg'),
            pctfield = new TextFieldClass('makebidpct', null, this.getUpdater('makebidpct', PercentClass.prototype.clean), 'makebidmsg');
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
        pl('#makebidtext').bind({
            focus: function() {
                if (!pl('#makebidtext').hasClass('edited')) {
                    pl('#makebidtext').attr({value: ''});
                    pl('#makebidmsg').html('&nbsp;');
                }
            },

            keyup: function() {
                var val = pl('#makebidtext').attr('value');
                if (!pl('#makebidtext').hasClass('edited')) {
                    pl('#makebidtext').addClass('edited');
                    pl('#makebidmsg').html('&nbsp;');
                }
                return false;
            },

            blur: function() {
                if (!pl('#makebidtext').hasClass('edited')) {
                    pl('#makebidtext').attr({value: 'Put your note to the owner here...'});
                }
            }
        });
    },

    makeAddButtons: function() {
        return '\
<div class="bidactionline" id="existingbidbuttons">\
    <span class="span-3 inputbutton bidactionbutton initialhidden" id="investor_withdraw_btn">WITHDRAW</span>\
    <span class="span-3 inputbutton bidactionbutton initialhidden" id="investor_reject_btn">REJECT</span>\
    <span class="span-3 inputbutton bidactionbutton initialhidden" id="investor_accept_btn">ACCEPT</span>\
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
            actionfuncname;
        for (i = 0; i < this.validactions.length; i++) {
            action = this.validactions[i];
            actionfuncname = 'bind_' + action;
            this[actionfuncname]();
            console.log(action, actionfuncname);
        }
    },

    makeBidAction: function(type) {    
        var complete = function(json) {
                var bids = json.bids || [],
                    html = (new BidClass(self)).store(bids[0]).makeHtml();
                self.bidsprops = json.bidsprops || {};
                self.validactions = json.validactions || [];
                pl('#makebidamt, #makebidpct').attr({value: ''});
                pl('#makebidval').removeClass('successful').addClass('inprogress').text('N/A');
                pl('#makebidtext').removeClass('edited').attr({value: 'Put your note to the owner here...'});
                pl('#newbidtitlemsg').addClass('successful').text('Bid posted');
                pl('#bidlistlast').before(html);
            },

            text = pl('#makebidtext').hasClass('edited') && SafeStringClass.prototype.clean(pl('#makebidtext').attr('value') || '') || '',
            amt = CurrencyClass.prototype.clean(pl('#makebidamt').attr('value')),
            pct = PercentClass.prototype.clean(pl('#makebidpct').attr('value')),
            val = CurrencyClass.prototype.clean(pl('#makebidval').text()),
            data = {
                bid: {
                    listing_id: self.listing_id,
                    investor_id: self.investor_profile_id,
                    amt: amt,
                    pct: pct,
                    val: val,
                    type: type,
                    text: text
                }
            },

            ajax = new AjaxClass('/listing/make_bid', 'newbidtitlemsg', complete),
            validamt = self.amtfield.validate(),
            validpct = self.pctfield.validate(),
            validmsg = '' + (validamt ? 'AMOUNT: ' + validamt + ' ' : '') + (validpct ? 'PERCENT: ' + validpct : '');
        if (validmsg) {
            self.amtfield.fieldBase.msg.show('attention', validmsg);
            return false;
        }
        console.log('data');
        ajax.setPostData(data);
        self.mock_make_bid(ajax, data); // FIXME
        ajax.call();
    },

    showExistingBidConfirmButtons: function(type) {
        pl('#existingbidbuttons').hide();
        pl('#investor_existing_confirm_btn').unbind().bind('click', function() {
            alert('confirm action');
        });
        pl('#investor_existing_cancel_btn').unbind().bind('click', function() {
            pl('#existingconfirmbuttons').hide();
            pl('#existingbidbuttons').show();
        });
        console.log(type, this, this.confirmtext);
        pl('#investor_existing_msg').html(this.confirmtext[type]);
        pl('#existingconfirmbuttons').show();
    },

    showNewBidConfirmButtons: function(type) {
        pl('#newbidbuttons').hide();
        pl('#investor_new_confirm_btn').unbind().bind('click', function() {
            alert('confirm action');
        });
        pl('#investor_new_cancel_btn').unbind().bind('click', function() {
            pl('#newconfirmbuttons').hide();
            pl('#newbidbuttons').show();
        });
        console.log(type, this, this.confirmtext);
        pl('#investor_new_msg').html(this.confirmtext[type]);
        pl('#newconfirmbuttons').show();
    },

    bindNewBidActionButton: function(type) {
        var self = this,
            type = type || 'investor_post',
            btnid = type + '_btn',
            btnsel = '#' + btnid;
        pl(btnsel).bind('click', function() {
            if (pl(btnsel).hasClass('confirmed')) {
                self.makeBidAction(type);
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
            if (pl(this).hasClass('confirmed')) {
                alert('do: ' + type);
            }
            else {
                self.showExistingBidConfirmButtons(type);
            }
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
        if (!pl('#makebidbox').hasClass('bound')) {
            this.bindFields();
            this.bindButtons();
            pl('#makebidbox').addClass('bound');
        }
    }
});
