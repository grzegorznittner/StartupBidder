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
        var self = this;
        for (k in json) {
            self[k] = json[k];
        }
        self.amttext = self.amt ? CurrencyClass.prototype.format(self.amt) : '';
        self.pcttext = self.pct ? PercentClass.prototype.format(self.pct) : '';
        self.valtext = self.val ? CurrencyClass.prototype.format(self.val) : '';
        self.typetext = self.type ? self.type.replace(/(investor_|owner_)/, '') : '';
        self.bidtext = self.text ? SafeStringClass.prototype.htmlEntities(self.text) : '&nbsp;';
        self.datetext = self.created_date ? DateClass.prototype.format(self.created_date) : '';
        //self.usertext = self.type.indexOf('INVESTOR') ? self.bidslist.investorusername : self.bidslist.ownerusername;
        self.usertext = self.type && self.type.match(/investor/) ? 'You' : 'Owner';
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
                text: 'No bids',
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
            <p class="span-2">Actor</p>\
            <p class="span-2">Action</p>\
            <p class="span-3">Bid</p>\
            <p class="span-2">Equity</p>\
            <p class="span-3">Valuation</p>\
            <p class="span-9">Note</p>\
            <p class="biddateheader">Date</p>\
        </div>\
        ';
    },

    makeHtml: function() {
        var self = this;
        return '\
        <div class="messageline bidline ' + self.typeclass + '">\
            <p class="span-2">' + self.usertext + '</p>\
            <p class="span-2">' + self.typetext + '</p>\
            <p class="span-3">' + self.amttext + '</p>\
            <p class="span-2">' + self.pcttext + '</p>\
            <p class="span-3">' + self.valtext + '</p>\
            <p class="span-9 bidnote">\
                '+self.bidtext+'\
            </p>\
            <p class="biddate">'+self.datetext+'</p>\
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
                html += bid.makeHtml();
            }
        }
        else {
            bid = new BidClass(this);
            bid.setEmpty();
            html += bid.makeHtml();
        }
        this.bindBidBox();
        pl('#bidlistlast').before(html);
    },

    displayCalculatedIfValid: function() {
        var amt = CurrencyClass.prototype.clean(pl('#makebidamt').attr('value')) || 0,
            pct = PercentClass.prototype.clean(pl('#makebidpct').attr('value')) || 0,
            val = pct ? Math.floor(Math.floor(100 * amt / pct)) : 0,
            cur = CurrencyClass.prototype.format(CurrencyClass.prototype.clean(val)),
            dis = cur || '';
        pl('#makebidval').text(dis);
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
    
    bind_investor_post: function(type) {
        var self = this,
            type = type || 'investor_post',
            btnid = type + '_btn';
        pl('#' + btnid).bind({
            click: function(event) {
                var complete = function(json) {
                        var bids = json.bids || [],
                            html = (new BidClass(self)).store(bids[0]).makeHtml();
                        self.bidsprops = json.bidsprops || {};
                        self.validactions = json.validactions || [];
                        pl('#makebidamt, #makebidpct').attr({value: ''});
                        pl('#makebidval').text('');
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

                    ajax = new AjaxClass('/listing/make_bid', 'makebidmsg', complete),
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
                return false;
            }
        }).show();
    },

    bind_investor_counter: function() {
        this.bind_investor_post('investor_counter');
    },

    bind_investor_accept: function() {
    },

    bind_investor_reject: function() {
    },

    bind_investor_withdraw: function() {
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
