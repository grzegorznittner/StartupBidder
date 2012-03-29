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
            // anti-spam
            // contact_email = listing.contact_email || 'Email address here',
            // contact_emaillink = contact_email ? 'mailto:' + contact_email :'',
            brief_address = listing.brief_address || 'Postal address here',
            website = listing.website || 'Website here',
            asking = listing.asked_funds ? 'Asking ' + CurrencyClass.prototype.format(suggested_amt) + ' for ' + PercentClass.prototype.format(suggested_pct) : 'Not asking for funds at this time.',
            m = 10,
            n = 26,
            i,
            field,
            val,
            sel;
        pl('#mantraip').text(mantra);
        pl('#nameip,#name2ip').text(name);
        // anti-spam
        // pl('#contact_emailip,#contact_email2ip').text(contact_email);
        // pl('#contact_emaillinkip,#contact_email2linkip').attr({href: contact_emaillink});
        pl('#brief_addressip,#brief_address2ip').text(brief_address);
        pl('#websiteip,#website2ip').text(website);
        pl('#websitelinkip,#website2linkip').attr({href: website});
        pl('#created_dateip').text(date);
        pl('#askingip').text(asking);
        pl('#ipcorp').text(corp);
        pl('#ipdate,#ipdate2').text(date);
        pl('#ippagetotal').text(self.pagetotal);
        pl('#ippage').text(self.page);
        pl('#iptitle1').text(corp);
        pl('#summaryip').html(HTMLMarkup.prototype.stylize(listing.summary, 'ip'));
        for (i = m; i <= n; i++) {
            field = 'answer' + i;
            sel = '#' + field + 'ip';
            val = listing[field];
            pl(sel).html(HTMLMarkup.prototype.stylize(val, 'ip'));
        }
        for (i = 1; i <= self.pagetotal; i++) {
            sel = '#iplogo' + i;
            pl(sel).css({background: logo});
        }
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
    setPage: function(newpage) {
        var self = this,
            left = (960 * (1 - newpage)) + 'px';
        self.page = newpage;
        pl('#ipslideset').css({left: left})
        pl('#ippage').text(newpage);
    },
    bindButtons: function() {
        var self = this;
        if (self.bound) {
            return;
        }
        pl('#ipleft').bind({
            click: function() {
                self.pageLeft();
                return false;
            }
        });
        pl('#ipright').bind({
            click: function() {
                self.pageRight();
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
