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
        self.pcttext = self.pct ? PercentClass.prototype.format(self.pct) + '%' : '';
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
            <p class="span-3 sideboxnum">' + self.amttext + '</p>\
            <p class="span-2 sideboxnum">' + self.pcttext + '</p>\
            <p class="span-3 sideboxnum">' + self.valtext + '</p>\
            <p class="span-9 bidnote">\
                '+self.bidtext+'\
            </p>\
            <p class="biddate">'+self.datetext+'</p>\
        </div>\
        ';
    }
});


