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
        <style>\
            .investorgroupheader { background: #49515a !important; }\
            .investorgroupheader p { color: white; font-weight: bold !important; text-align: center; }\
            .investorgroupline p { font-weight: bold; text-align: center; }\
            .investorgroupnote { text-align: left !important; font-weight: normal !important; }\
            .investorgroupdateheader { float: left; width: 140px; }\
            .investorgroupdate { float: left; width: 140px; text-align: right; font-size: 12px; padding-top: 2px; }\
        </style>\
        <div class="messageline investorgroupheader">\
            <p class="span-4">Investor</p>\
            <p class="span-2">Action</p>\
            <p class="span-3">Bid</p>\
            <p class="span-1" style="position: relative; left:-10px;">Equity</p>\
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
            completeFunc = function(json) {
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

            ajax = new AjaxClass('/listing/investors/' + this.listing_id, 'bidstitlemsg', completeFunc);
        ajax.setGetData({ max_results: 20 });
        ajax.call();
    }
});

(new InvestorBidGroupListClass()).load();
