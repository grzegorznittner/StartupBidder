function CompanyTileClass(options) {
    this.options = options || {};
    if (this.options.json) {
        this.setValues(this.options.json);
    }
}
pl.implement(CompanyTileClass, {
    setValues: function(json) {
        var date = json.listing_date || json.created_date,
            closingText = json.status === 'new' ? 'New listing' : (json.days_left === 0 ? 'Closing today!' : (json.days_left < 0 ? 'Bidding closed' : json.days_left + ' days left')),
            listingText = json.status === 'new' ? 'New listing' : (json.asked_fund ? closingText : (json.days_ago ? json.days_ago + ' days ago' : 'Listed today'));
            url = json.website ? new URLClass(json.website) : null;
        this.status = json.status;
        this.daysText = listingText;
        this.imgClass = json.logo ? '' : 'noimage';
        this.imgStyle = json.logo ? 'background: url(' + json.logo + ') no-repeat scroll left top' : '';
        this.category = json.category || 'Other';
        this.categoryUC = json.category ? json.category.toUpperCase() : 'OTHER';
        this.posted = date ? DateClass.prototype.format(date) : 'not posted';
        this.name = json.title || 'No Company Name';
        this.brief_address = json.brief_address || 'No Address';
        this.address = json.address || 'No Address';
        this.suggested_amt = json.asked_fund && json.suggested_amt ? CurrencyClass.prototype.format(json.suggested_amt) : '';
        this.suggested_text = this.suggested_amt || 'Not seeking funds';
        this.finance_line = this.daysText + (this.suggested_amt ? ' at ' + this.suggested_amt : '');
        this.mantra = json.mantra || 'No Mantra';
        this.founders = json.founders || 'No Founders';
        this.url = this.status === 'new' ? '/new-listing-submit-page.html' : (this.status === 'posted' ? '/new-listing-submitted-page.html' : '/company-page.html?id=' + json.listing_id);
        if (this.options.admin) {
            this.url = '/company-page.html?id=' + json.listing_id;
        }
        this.websitelink = json.website || '#';
        this.websitedomain = url ? url.getHostname() : 'No Website';
        this.openanchor = this.options.preview ? '' : '<a href="' + this.url + '">';
        this.closeanchor = this.options.preview ? '' : '</a>';
     },
    store: function(json) {
        this.setValues(json);
    },
    makeHtml: function(lastClass) {
            html = '\
<span class="span-4 '+ (lastClass?lastClass:'') +'">\
<div class="tile">\
' + this.openanchor + '\
<div class="tileimg hoverlink" style="' + this.imgStyle + '"></div>\
' + this.closeanchor + '\
<div class="tiledays"></div>\
<div class="tiledaystext">' + this.daysText + '</div>\
<div class="tiletype"></div>\
<div class="tiletypetext">' + this.categoryUC + '</div>\
<div class="tilepoints"></div>\
<div class="tilepointstext">\
    <div class="tileposted">' + this.suggested_amt + '</div>\
</div>\
<p class="tiledesc">\
' + this.openanchor + '\
    <span class="tilecompany hoverlink">' + this.name + '</span><br/>\
' + this.closeanchor + '\
    <span class="tileloc">' + this.brief_address + '</span><br/>\
    <span class="tiledetails">' + this.mantra + '</span>\
</p>\
</div>\
</span>\
';
        return html;
    },
    makeInfoWindowHtml: function() {
        return '\
<div class="infowindow">\
' + this.openanchor + '\
<div class="tileimg hoverlink" style="' + this.imgStyle + '"></div>\
' + this.closeanchor + '\
<p>\
    ' + this.openanchor + '\
    <div class="infotitle hoverlink">' + this.name + '</div>\
    ' + this.closeanchor + '\
    <div>' + this.address + '</div>\
    <div class="infomantra">' + this.mantra + '</div>\
    <div><span class="infolabel">Asking:</span> ' + this.suggested_text + '</div>\
    <div><span class="infolabel">Bidding:</span> ' + this.daysText + '</div>\
    <div><span class="infolabel">Industry:</span> ' + this.category + '</div>\
    <div><span class="infolabel">Founders:</span> ' + this.founders + '</div>\
</p>\
</div>\
';
    },
    makeFullWidthHtml: function() {
        return '\
<div class="companybannertile last">\
' + this.openanchor + '\
    <div class="companybannerlogo tileimg noimage hoverlink" style="' + this.imgStyle + '"></div>\
' + this.closeanchor + '\
' + this.openanchor + '\
    <div class="companybannertitle hoverlink">' + this.name + '</div>\
' + this.closeanchor + '\
    <div class="companybannertextgrey">\
        ' + (this.category==='Other' ? 'A' : (this.category.match(/^[AEIOU]/) ? 'An '+this.category : 'A '+this.category)) +  ' company in ' + this.brief_address + '\
    </div>\
    <div class="companybannertextgrey">Founded by ' + this.founders + '</div>\
    <div class="companybannertextgrey">' + this.finance_line + '</div>\
    <div class="companybannermantra">' + this.mantra + '</div>\
</div>\
';
    },
    makeHalfWidthHtml: function(lastClass) {
        return '\
<div class="companyhalftile' + (lastClass?' companyhalftilelast '+lastClass:'') + '">\
' + this.openanchor + '\
    <div class="companyhalflogo tileimg noimage hoverlink" style="' + this.imgStyle + '"></div>\
    <div class="halftiledays"></div>\
    <div class="halftiledaystext">' + this.daysText + '</div>\
    <div class="halftiletype"></div>\
    <div class="halftiletypetext">' + this.suggested_amt + '</div>\
' + this.closeanchor + '\
' + this.openanchor + '\
    <div class="companyhalftitle hoverlink">' + this.name + '</div>\
' + this.closeanchor + '\
    <div class="companyhalftext">\
        ' + (this.category==='Other' ? '' : (this.category.match(/^[AEIOU]/) ? 'An '+this.category+' company' : 'A '+this.category+' company')) +  '\
    </div>\
    <div class="companyhalftext">\
        ' + this.brief_address + '\
    </div>\
    <div class="companyhalfmantra">' + this.mantra + '</div>\
</div>\
';
    },
    display: function(listing, divid) {
        this.setValues(listing);
        pl('#'+divid).html(this.makeHtml('last'));
    }
});

function CompanyListClass(options) {
    this.options = options || {};
}
pl.implement(CompanyListClass, {
    storeList: function(json, _colsPerRow, _companydiv, _companykey) {
        var i, company, tile, last,
            colsPerRow = _colsPerRow ? _colsPerRow : ( this.options.fullWidth ? 1 : 4),
            companydiv = _companydiv || this.options.listingsdiv || 'companydiv',
            companykey = _companykey || this.options.propertykey || 'listings',
            companysel = '#' + companydiv,
            companies = this.options.propertyissingle ? (json && json[companykey] ? [ json[companykey] ] : null ) : (json && json[companykey] && json[companykey].length > 0 ? json[companykey] : null),
            showmore = this.options.showmore && companies && (companies.length >= colsPerRow),
            profile = json && json.loggedin_profile ? json.loggedin_profile : null,
            postnowlink = profile ? 'new-listing-basics-page.html' : 'login-page.html',
            options = json.loggedin_profile && json.loggedin_profile.admin ? { admin: true } : {},
            html = "";
        pl('#postnowtextlink,#postnowbtnlink').attr({href: postnowlink});
        if (!companies) {
            pl(companysel).html('<span class="attention">No companies found</span>');
            return;
        }
        for (i = 0; i < companies.length; i++) {
            company = companies[i];
            tile  = new CompanyTileClass(options);
            tile.setValues(company);
            if (this.options.fullWidth || (colsPerRow === 4 && companies.length === 1)) {
                html += tile.makeFullWidthHtml();
            }
            else if (colsPerRow === 4 && companies.length === 2 && i === 0) {
                html += tile.makeHalfWidthHtml();
            }
            else if (colsPerRow === 4 && companies.length === 2 && i === 1) {
                html += tile.makeHalfWidthHtml('last');
            }
            else if (colsPerRow === 4 && companies.length === 3 && i < 2) {
                html += tile.makeHtml();
            }
            else if (colsPerRow === 4 && companies.length === 3 && i === 2) {
                html += tile.makeHalfWidthHtml('last');
            }
            else {
                last = (i+1) % colsPerRow === 0 ? 'last' : '';
                html += tile.makeHtml(last);
            }
        }
        if (showmore) {
            html += '<div class="showmore"><a href="' + this.options.showmore + '">More...</a></div>\n';
        }
        pl(companysel).html(html);
    }
});

function BaseCompanyListPageClass() {
    this.queryString = new QueryStringClass();
    this.type = this.queryString.vars.type || 'top';
    this.val = this.queryString.vars.val ? decodeURIComponent(this.queryString.vars.val) : '';
    this.searchtext = this.queryString.vars.searchtext ? decodeURIComponent(this.queryString.vars.val) : '';
    this.data = { max_results: 20 };
    this.setListingSearch();
};
pl.implement(BaseCompanyListPageClass,{
    setListingSearch: function() {
        var searchtype = 'top';
        if (this.type === 'related') { // FIXME: related unimplemented
            // type = 'related?id='+this.listing_id
            searchtype = 'top';
            this.data.max_results = 4;
        }
        else if (this.type === 'category') {
            searchtype = 'keyword';
            this.data.text = 'category:' + this.val;
        }
        else if (this.type === 'location') {
            searchtype = 'keyword';
            this.data.text = 'location:' + this.val;
        }
        else if (this.type === 'keyword') {
            searchtype = 'keyword';
            this.data.text = this.searchtext;
        }
        else {
            searchtype = this.type;
        }
        this.url = '/listings/' + searchtype;
    },
    loadPage: function(completeFunc) {
        var titleroot = (this.type === 'category' || this.type === 'location') ? this.val.toUpperCase() : this.type.toUpperCase(),
            title = this.type === 'keyword' ? 'SEARCH RESULTS' : ((this.type === 'location') ? 'COMPANIES IN ' + titleroot : titleroot + ' COMPANIES'),
            ajax;
        this.setListingSearch();
        ajax = new AjaxClass(this.url, 'companydiv', completeFunc);
        pl('#listingstitle').html(title);
        if (this.type === 'closing') {
            pl('#welcometitle').html('Invest in a startup today!');
            pl('#welcometext').html('The companies below are ready for investment and open for bidding');
            pl('#investbox').hide();
        }
        if (this.type === 'category') {
            pl('#welcometitle').html('Find companies in your industry!');
            pl('#welcometext').html('Below are the top companies in the ' + this.val + ' industry');
            pl('#investbox').hide();
        }
        if (this.type === 'location') {
            pl('#welcometitle').html('Find companies in your area!');
            pl('#welcometext').html('Below are the top companies in ' + this.val);
            pl('#investbox').hide();
        }
        ajax.ajaxOpts.data = this.data;
        ajax.call();
    }
});

function RelatedCompaniesClass(listing_id) {
    this.listing_id = listing_id;
}
pl.implement(RelatedCompaniesClass, {
    load: function() {
        var completeFunc, basePage;
        completeFunc = function(json) {
            companyList = new CompanyListClass();
            companyList.storeList(json,2);
        };
        basePage = new BaseCompanyListPageClass();
        basePage.type = 'related';
        basePage.listing_id = this.listing_id;
        basePage.loadPage(completeFunc);
    }
});

function TestCompaniesClass() { // FIXME: remove after implementation fixed
        var randomLen, i;
        this.randomSort = function (a,b) {
            var temp = parseInt( Math.random()*10 );
            var isOddOrEven = temp%2;
            var isPosOrNeg = temp>5 ? 1 : -1;
            return( isOddOrEven*isPosOrNeg );
        };
        this.allCompanies = [{"num":1,"listing_id":"ag1zdGFydHVwYmlkZGVychcLEgdMaXN0aW5nIgpraWxsIGVtYWlsDA","title":"Kill email","suggested_amt":20000,"suggested_pct":50,"suggested_val":40000,"previous_val":40000,"valuation":40000,"median_valuation":75000,"score":3316,"listing_date":"20111018","closing_date":"20111117","status":"active","mantra":"Making email as obsolete as the memo.","summary":"If any startup says it's going to eliminate email, it's destined for failure. You can iterate on the inbox, and try to improve it, but even that's not much of a business. The latest high profile flop in this arena is Google Wave. It was supposed to change email forever. It was going to displace email. Didn't happen.","profile_id":"ag1zdGFydHVwYmlkZGVychkLEgRVc2VyIg9idXNpbmVzc2luc2lkZXIM","profile_username":"Insider","num_comments":38,"num_bids":15,"votable":true,"days_ago":95,"days_left":-66,"mockData":true,"business_plan_id":null,"presentation_id":null,"financials_id":null},{"num":2,"listing_id":"ag1zdGFydHVwYmlkZGVychsLEgdMaXN0aW5nIg5sb2NhbG5ld3NzaXRlcww","title":"Local news sites","suggested_amt":9800,"suggested_pct":20,"suggested_val":49000,"previous_val":365217,"valuation":365217,"median_valuation":77000,"score":1326,"listing_date":"20111011","closing_date":"20111110","status":"active","mantra":"Local news made global","summary":"Maybe Tim Armstrong, AOL, and Patch will prove it wrong, but to this point nobody has been able to crack the local news market and make a sustainable business.In theory creating a network of local news sites that people care about is a good idea. You build a community, there's a baked in advertising group with local businesses, and classifieds. But, it appears to be too niche to scale into a big business.","profile_id":"ag1zdGFydHVwYmlkZGVychQLEgRVc2VyIgpkcmFnb25zZGVuDA","profile_username":"The Dragon","num_comments":22,"num_bids":19,"votable":false,"days_ago":102,"days_left":-73,"mockData":true,"business_plan_id":null,"presentation_id":null,"financials_id":null},{"num":3,"listing_id":"ag1zdGFydHVwYmlkZGVyciMLEgdMaXN0aW5nIhZjb21wX3VwZ3JhZGluZ19zZXJ2aWNlDA","title":"Computer Upgrading Service","suggested_amt":11550,"suggested_pct":33,"suggested_val":35000,"previous_val":35000,"valuation":35000,"median_valuation":63000,"score":899,"listing_date":"20111009","closing_date":"20111108","status":"active","mantra":"Taking the pain out of managing your technology","summary":"Starting a business that specializes in upgrading existing computer systems with new internal and external equipment is a terrific homebased business to initiate that has great potential to earn an outstanding income for the operator of the business. A computer upgrading service is a very easy business to get rolling, providing you have the skills and equipment necessary to complete upgrading tasks, such as installing more memory into the hard drive, replacing a hard drive, or adding a new disk drive to the computer system. Ideally, to secure the most profitable segment of the potential market, the service should specialize in upgrading business computers as there are many reasons why a business would upgrade a computer system as opposed to replacing the computer system. Additionally, managing the business from a homebased location while providing clients with a mobile service is the best way to keep operating overheads minimized and potentially increases the size of the target market by expanding the service area, due to the fact the business operates on a mobile format.","profile_id":"ag1zdGFydHVwYmlkZGVychkLEgRVc2VyIg9ncnplZ29yem5pdHRuZXIM","profile_username":"Greg","num_comments":34,"num_bids":27,"votable":false,"days_ago":104,"days_left":-75,"mockData":true,"business_plan_id":null,"presentation_id":null,"financials_id":null},{"num":4,"listing_id":"ag1zdGFydHVwYmlkZGVychsLEgdMaXN0aW5nIg5zZW1hbnRpY3NlYXJjaAw","title":"Semantic Search","suggested_amt":18000,"suggested_pct":45,"suggested_val":40000,"previous_val":40000,"valuation":40000,"median_valuation":61500,"score":639,"listing_date":"20111005","closing_date":"20111104","status":"active","mantra":"Adding meaning to your search experience","summary":"The fact of the matter is Google, and to a much lesser extent Bing, own the search market. Ask Barry Diller, if you don't believe us.Yet, startups still spring up hoping to disrupt the incumbents. Cuil flopped. Wolfram Alpha is irrelevant. Powerset, which was a semantic search engine was bailed out by Microsoft, which acquired it.","profile_id":"ag1zdGFydHVwYmlkZGVychILEgRVc2VyIghqcGZvd2xlcgw","profile_username":"fowler","num_comments":2,"num_bids":24,"votable":false,"days_ago":108,"days_left":-79,"mockData":true,"business_plan_id":null,"presentation_id":null,"financials_id":null},{"num":5,"listing_id":"ag1zdGFydHVwYmlkZGVyciILEgdMaXN0aW5nIhVzb2NpYWxyZWNvbW1lbmRhdGlvbnMM","title":"Social recommendations","suggested_amt":1500,"suggested_pct":10,"suggested_val":15000,"previous_val":15000,"valuation":15000,"median_valuation":42500,"score":412,"listing_date":"20111004","closing_date":"20111103","status":"active","mantra":"Purchasing for status in your peer group","summary":"It's a very tempting idea. Collect data from people about their tastes and preferences. Then use that data to create recommendations for others. Or, use that data to create recommendations for the people that filled in the information. It doesn't work. The latest to try is Hunch and Get Glue.Hunch is pivoting towards non-consumer-facing white label business. Get Glue has had some success of late, but it's hardly a breakout business.","profile_id":"ag1zdGFydHVwYmlkZGVychELEgRVc2VyIgdjaGluZXNlDA","profile_username":"The One","num_comments":42,"num_bids":20,"votable":false,"days_ago":109,"days_left":-80,"mockData":true,"business_plan_id":null,"presentation_id":null,"financials_id":null}];
        this.allCompanies.sort(this.randomSort);
        randomLen = Math.floor(Math.random() * this.allCompanies.length);
        this.companies = [];
        for (i = 0; i < randomLen; i++) {
            this.companies[i] = this.allCompanies[i];
        }
}
pl.implement(TestCompaniesClass, {
    testJson: function() {
        return this.companies;
    },
    testNotifications: function() {
        var types, dates, i, company, notifications, notification;
        types = ['comment', 'bid'];
        dates = ['2011/11/29 13:12', '2011/12/20 9:46', '2012/01/13 8:37'];
        notifications = [];
        for (i = 0; i < this.companies.length; i++) {
            company = this.companies[i];
            notification = {};
            notification.url = '/company-page.html?id=' + company.listing_id;
            notification.type = types[Math.floor(Math.random() * types.length)]
            notification.text =  'You received a ' + notification.type + ' on ' + company.title;
            notification.date = dates[Math.floor(Math.random() * dates.length)]
            notifications[i] = notification;
        }
        return notifications;
    }
});

function ListClass(options) {
    this.options = options || {};
}
pl.implement(ListClass, {
    ucFirst: function(str) {
        return str.length > 1 ? str.substr(0,1).toUpperCase() + str.substr(1) : str.toUpperCase();
    },
    spreadOverOneCol: function(list, divcol1) {
        var htmlCol1 = '',
            i,
            item,
            itemurl;
        for (i = 0; i < list.length; i++) {
            item = list[i];
            itemurl = '/main-page.html?type=' + this.options.type + '&amp;val=' + encodeURIComponent(item);
            htmlCol1 += '<a href="' + itemurl + '"><li>'+item+'</li></a>';
        }
        pl(divcol1).html(htmlCol1);
    },
    spreadOverTwoCols: function(list, divcol1, divcol2) {
        var mid = Math.floor((list.length + 1) / 2);
        this.spreadOverOneCol(list.slice(0, mid), divcol1);
        if (list.length > 1) {
            this.spreadOverOneCol(list.slice(mid, list.length), divcol2);
        }
    }
});

function BaseListClass(kvlist, id, over, type) {
    this.kvlist = kvlist;
    this.msgid = id + 'msg';
    this.col1id = id + 'divcol1';
    this.col2id = id + 'divcol2';
    this.over = over ? over : 1;
    this.type = type;
}
pl.implement(BaseListClass, {
    display: function() {
        var self = this,
            list = [], 
            lc = new ListClass({type: self.type}),
            k,
            v;
        for (k in self.kvlist) {
            v = self.kvlist[k];
            list.push(k); // ignore v for now
        }
        list.sort();
        if (self.over === 2) {
            lc.spreadOverTwoCols(list, '#'+self.col1id, '#'+self.col2id);
        }
        else {
            lc.spreadOverOneCol(list, '#'+self.col1id, type);
        }
    }
});

function SearchBoxClass() {}
pl.implement(SearchBoxClass, {
    bindEvents: function() {
        var qs = new QueryStringClass(),
            val = (qs && qs.vars && qs.vars.searchtext) ? qs.vars.searchtext : 'Search';
        pl('#searchtext').attr({value: val});
        pl('#searchtext').bind({
            focus: function() {
                if (pl('#searchtext').attr('value') === 'Search') {
                    pl('#searchtext').attr({value: ''});
                }
            },
            keyup: function(e) {
                var evt = new EventClass(e);
                if (evt.keyCode() === 13) {
                    pl('#searchform').get(0).submit();
                    return false;
                }
                return true;
            }
        });
        pl('#searchbutton').bind({
            click: function() {
                pl('#searchform').get(0).submit();
            }
        });
    }
});


