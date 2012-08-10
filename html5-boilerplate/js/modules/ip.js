function IPClass() {
    this.page = 1;
    this.pagetotal = 20;
    this.bound = false;
}
pl.implement(IPClass, {
    display: function(listing) {
        var self = this,
            logo = 'url(' + (listing.logo || 'img/noimage_146x146.jpg') + ')',
            corp = listing.title || 'Company name here',
            date = listing.created_date ? DateClass.prototype.format(listing.created_date) : DateClass.prototype.today(),
            mantra = listing.mantra || 'Company mantra here',
            name = listing.founders || 'Founders here',
            brief_address = listing.brief_address || 'Postal address here',
            website = listing.website || 'Website here',
            asking = listing.asked_fund
                ? 'Asking ' + CurrencyClass.prototype.format(listing.suggested_amt) + ' for ' + PercentClass.prototype.format(listing.suggested_pct) + '%'
                : 'Not asking for funds at this time.',
            m = 10,
            n = 26,
            i,
            field,
            val,
            sel;
        pl('.mantraip').text(mantra);
        pl('.nameip').text(name);
        pl('.brief_addressip').text(brief_address);
        pl('.websiteip').text(website);
        pl('.websitelinkip').attr({href: website});
        pl('.askingip').text(asking);
        pl('.ipcorpname').text(corp);
        pl('.ipdatetext').text(date);
        pl('.ippagetotal').text(self.pagetotal);
        pl('.ippagenum').text(self.page);
        pl('#summaryip').html(HTMLMarkup.prototype.stylize(listing.summary, 'ip'));
        for (i = m; i <= n; i++) {
            field = 'answer' + i;
            sel = '#' + field + 'ip';
            val = listing[field];
            pl(sel).html(HTMLMarkup.prototype.stylize(val, 'ip'));
        }
        pl('.iplogo').css({background: logo});
    },
    pageRight: function() {
        var self = this,
            newpage = self.page >= self.pagetotal ? self.pagetotal : self.page + 1;
        self.setPage(newpage);
    },
    pageLeft: function() {
        var self = this,
            newpage = self.page <= 1 ? 1 : self.page - 1;
        self.setPage(newpage);
    },
    pageFirst: function() {
        var self = this;
        self.setPage(1);
    },
    setPage: function(newpage) {
        var self = this,
            left = (pl('.ipslide').css('width').replace('px','') * (1 - newpage)) + 'px';
        self.page = newpage;
        pl('.ipslideset').css({left: left})
        pl('.ippagenum').text(newpage);
        if (newpage > 1) {
            pl('.ipleft').show();
            pl('.ipfirst').show();
        }
        else {
            pl('.ipleft').hide();
            pl('.ipfirst').hide();
        }
        if (newpage < self.pagetotal) {
            pl('.ipright').show();
        }
        else {
            pl('.ipright').hide();
        }
    },
    bindButtons: function() {
        var self = this;
        if (self.bound) {
            return;
        }
        pl('.ipleft').bind({
            click: function() {
                self.pageLeft();
                return false;
            }
        });
        pl('.ipright').bind({
            click: function() {
                self.pageRight();
                return false;
            }
        });
        pl('.ipfirst').bind({
            click: function() {
                self.pageFirst();
                return false;
            }
        });
    },
    genDisplay: function(field, displayCalc) {
        return function(result, val) {
            displayCalc();
            if (result === 0) {
                pl(field.fieldBase.sel + 'ip').html(HTMLMarkup.prototype.stylize(val, 'ip'));
            }
        };
    },
    getUpdater: function(id) {
        return function(json) {
            var ipsel = '#' + id + 'ip',
                newval = json && json.listing && (json.listing[id] !== null) ? json.listing[id] : null;
            if (newval !== null) {
                pl(ipsel).html(HTMLMarkup.prototype.stylize(newval, 'ip'));
            }
        };
    }
});

function SlidesPageClass() {
    var queryString = new QueryStringClass();
    this.listing_id = queryString.vars.id;
};
pl.implement(SlidesPageClass,{
    load: function() {
        var self = this,
            complete = function(json) {
                var header = new HeaderClass(),
                    companybanner = new CompanyBannerClass('slides'),
                    ip = new IPClass();
                header.setLogin(json);
                companybanner.display(json);
                ip.display(json.listing);
                ip.bindButtons();
                pl('.preloader').hide();
                pl('#ip, .wrapper').show();
            },
            error = function(errornum, json) {
                (new HeaderClass()).setLogin(json);
                pl('.preloader').hide();
                pl('.errorwrapper').show();
            },
            ajax = new AjaxClass('/listing/get/' + this.listing_id, 'ipmsg', complete, null, null, error);
        ajax.call();
    }
});

