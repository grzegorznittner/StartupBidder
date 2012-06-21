function CompanyTileClass(options) {
    this.options = options || {};
    this.companybannertileclass = options.companybannertileclass || 'companybannertile';
    if (this.options.json) {
        this.store(this.options.json);
    }
}
pl.implement(CompanyTileClass, {
    store: function(json) {
        this.status = json.status;
        if (!this.status) {
            this.daystext = '';
        }
        else if (this.status === 'active' && json.asked_fund) {
            this.daystext = 'Bidding open';
        }
        else if (this.status === 'active' && !json.asked_fund && json.listing_date) {
            this.daysago = DateClass.prototype.daysBetween(DateClass.prototype.dateFromYYYYMMDD(json.listing_date), DateClass.prototype.todayDate());
            this.daystext = this.daysago === 0 ? 'Listed today' : this.daysago + ' days ago';
        }
        else {
            this.daystext = SafeStringClass.prototype.ucfirst(this.status);
        }
        this.imgClass = json.logo ? '' : 'noimage';
        this.imgStyle = json.logo ? 'background: url(' + json.logo + ') no-repeat scroll left top' : '';
        this.category = json.category || 'Other';
        this.categoryUC = json.category ? json.category.toUpperCase() : 'OTHER';
        this.posted = json.posted_date ? DateClass.prototype.format(json.posted_date) : 'not posted';
        this.name = json.title || 'No Company Name';
        this.brief_address = json.brief_address
            ? '<a class="hoverlink" href="/main-page.html?type=location&val=' + encodeURIComponent(json.brief_address) + '">'
                + '<div class="locicon"></div><span class="loctext">' + json.brief_address + '</span></a>'
            : '<span class="loctext">No Address</span>';
        this.brief_address_inp = json.brief_address
            ? '<a class="hoverlink" href="/main-page.html?type=location&val=' + encodeURIComponent(json.brief_address) + '">'
                + '<img src="../img/icons/location_16x16.gif" class="lociconinp"></img>&nbsp;<span class="loctextinp">' + json.brief_address + '</span></a>'
            : '<span class="loctext">No Address</span>';
        this.address = json.address || 'No Address';
        if (this.status && json.asked_fund && json.suggested_amt && json.suggested_pct) {
            this.suggested_amt = CurrencyClass.prototype.format(json.suggested_amt);
            this.suggested_pct = PercentClass.prototype.format(json.suggested_pct) + '%';
            this.suggested_text = this.suggested_amt + ' for ' + this.suggested_pct;
            this.finance_line = this.daystext + ' at ' + this.suggested_text;
        }
        else if (this.status) {
            this.suggested_amt = '';
            this.suggested_text = 'Not raising funds';
            this.finance_line = this.daystext;
        }
        else {
            this.suggested_amt = '';
            this.suggested_text = '';
            this.finance_line = '';
        }
        this.mantra = json.mantra || 'No Mantra';
        this.mantraplussuggest = this.mantra + '<br/>' + this.suggested_text;
        this.founders = json.founders || 'No Founders';
        this.foundertext = json.founders ? 'Founded by ' + json.founders : '';
        if (this.status === 'new') {
            this.url = '/new-listing-submit-page.html';
        }
        else if (this.status === 'posted' && !this.options.admin) {
            this.url = '/new-listing-submitted-page.html';
        }
        else {
            this.url = '/company-page.html?id=' + json.listing_id;
        }
        this.websitelink = json.website || '#';
        this.websiteurl = json.website ? new URLClass(json.website) : null;
        this.websitedomain = this.websiteurl ? this.websiteurl.getHostname() : 'No Website';
        this.openanchor = this.options.preview ? '' : '<a href="' + this.url + '">';
        this.closeanchor = this.options.preview ? '' : '</a>';
    },
    makeHtml: function(lastClass) {
            html = '\
<span class="span-4 '+ (lastClass?lastClass:'') +'">\
<div class="tile">\
' + this.openanchor + '\
<div class="tileimg hoverlink" style="' + this.imgStyle + '"></div>\
' + this.closeanchor + '\
<!--\
<div class="tiledays"></div>\
<div class="tiledaystext">' + this.daystext + '</div>\
<div class="tiletype"></div>\
<div class="tiletypetext">' + this.categoryUC + '</div>\
<div class="tilepoints"></div>\
-->\
<div class="tilepointstext">\
    <div class="tileposted">' + this.suggested_text + '</div>\
</div>\
<p class="tiledesc">\
' + this.openanchor + '\
    <span class="tilecompany hoverlink">' + this.name + '</span><br/>\
' + this.closeanchor + '\
    <span class="tileloc">' + this.brief_address_inp + '</span><br/>\
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
    <div><span class="infolabel">Bidding:</span> ' + this.daystext + '</div>\
    <div><span class="infolabel">Industry:</span> ' + this.category + '</div>\
    <div><span class="infolabel">Founders:</span> ' + this.founders + '</div>\
</p>\
</div>\
';
    },
    makeFullWidthHtml: function() {
        return '\
<div class="' + this.companybannertileclass + ' last">\
' + this.openanchor + '\
    <div class="companybannerlogo tileimg noimage hoverlink" style="' + this.imgStyle + '"></div>\
' + this.closeanchor + '\
' + this.openanchor + '\
    <div class="companybannertitle hoverlink">' + this.name + '</div>\
' + this.closeanchor + '\
    <div class="companybannertextgrey companybannermapline">\
        <span class="loctext">' + (this.category==='Other' ? 'A' : (this.category.match(/^[AEIOU]/) ? 'An '+this.category : 'A '+this.category)) +  ' company in&nbsp;</span>' + this.brief_address + '\
    </div>\
    <div class="companybannertextgrey">' + this.foundertext + '</div>\
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
<!--\
    <div class="halftiledays"></div>\
    <div class="halftiledaystext">' + this.daystext + '</div>\
    <div class="halftiletype"></div>\
    <div class="halftiletypetext">' + this.suggested_text + '</div>\
-->\
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
    <div class="companyhalfmantra">' + this.mantraplussuggest + '</div>\
</div>\
';
    },
    display: function(listing, divid) {
        this.store(listing);
        pl('#'+divid).html(this.makeHtml('last'));
    }
});

function CompanyListClass(options) {
    this.options = options || {};
    this.options.colsPerRow = this.options.colsPerRow || ( this.options.fullWidth ? 1 : 4 );
    this.options.companydiv = this.options.companydiv || 'companydiv';
    this.options.propertykey = this.options.propertykey || 'listings';
}
pl.implement(CompanyListClass, {
    storeList: function(json) {
        var companiesval = json && json[this.options.propertykey],
            postnowlink = json && json.loggedin_profile ? 'new-listing-basics-page.html' : json.login_url,
            isadmin = json && json.loggedin_profile && json.loggedin_profile.admin,
            tileoptions = { admin: isadmin },
            more_results_url = json.listings_props && json.listings_props.more_results_url,
            html = "",
            seeall,
            companies,
            company,
            tile,
            last,
            i;
        if (this.options.propertyissingle) {
            companies = companiesval ? [ companiesval ] : [];
        }
        else {
            companies = companiesval || [];
        }
        seeall = this.options.seeall && companies && (companies.length >= this.options.colsPerRow),
        pl('#postnowtextlink,#postnowbtnlink').attr({href: postnowlink});
        if (!companies.length) {
            pl('#'+this.options.companydiv).html('<span class="attention">No companies found</span>');
            return;
        }
        for (i = 0; i < companies.length; i++) {
            company = companies[i];
            tile  = new CompanyTileClass(tileoptions);
            tile.store(company);
            if (this.options.fullWidth || (this.options.colsPerRow === 4 && companies.length === 1)) {
                html += tile.makeFullWidthHtml();
            }
            else if (this.options.colsPerRow === 4 && companies.length === 2 && i === 0) {
                html += tile.makeHalfWidthHtml();
            }
            else if (this.options.colsPerRow === 4 && companies.length === 2 && i === 1) {
                html += tile.makeHalfWidthHtml('last');
            }
            else if (this.options.colsPerRow === 4 && companies.length === 3 && i < 2) {
                html += tile.makeHtml();
            }
            else if (this.options.colsPerRow === 4 && companies.length === 3 && i === 2) {
                html += tile.makeHalfWidthHtml('last');
            }
            else {
                last = (i+1) % this.options.colsPerRow === 0 ? 'last' : '';
                html += tile.makeHtml(last);
            }
        }
        if (more_results_url) {
            html += '<div class="showmore hoverlink" id="moreresults"><span class="initialhidden" id="moreresultsurl">' + more_results_url + '</span><span id="moreresultsmsg">More...</span></div>\n';
        }
        else if (seeall) {
            html += '<div class="showmore"><a href="' + this.options.seeall + '">See all...</a></div>\n';
        }
        pl('#'+this.options.companydiv).html(html);
        if (more_results_url) {
            this.bindMoreResults();
        }
    },
    bindMoreResults: function() {
        var self = this;
        pl('#moreresults').bind({
            click: function() {
                var completeFunc = function(json) {
                        var companies = json.listings || [],
                            more_results_url = companies.length > 0 && json.listings_props && json.listings_props.more_results_url,
                            html = '',
                            company,
                            tile,
                            last,
                            i;
                        for (i = 0; i < companies.length; i++) {
                            company = companies[i];
                            tile  = new CompanyTileClass(self.options);
                            tile.store(company);
                            last = (i+1) % self.options.colsPerRow === 0 ? 'last' : '';
                            html += tile.makeHtml(last);
                        }
                        if (html) {
                            pl('#moreresults').before(html);
                        }
                        if (more_results_url) {
                            pl('#moreresultsurl').text(more_results_url);
                            pl('#moreresultsmsg').text('More...');
                        }
                        else {
                            pl('#moreresultsmsg').text('');
                            pl('#moreresults').removeClass('hoverlink').unbind();
                        }
                    },
                    more_results_url = pl('#moreresultsurl').text(),
                    index = more_results_url ? more_results_url.indexOf('?') : -1,
                    components = more_results_url && index >= 0 ? [ more_results_url.slice(0, index), more_results_url.slice(index+1) ] : [ more_results_url, null ],
                    url = components[0],
                    parameters = components[1] ? components[1].split('&') : null,
                    ajax,
                    data,
                    keyval,
                    i;
                if (more_results_url) {
                    ajax = new AjaxClass(url, 'moreresultsmsg', completeFunc);
                    if (parameters) {
                        data = {};
                        for (i = 0; i < parameters.length; i++) {
                            keyval = parameters[i].split('=');
                            data[keyval[0]] = keyval[1];
                        }
                        ajax.setGetData(data);
                    }
                    ajax.call();
                }
                else {
                    pl('#moreresultsmsg').text('');
                    pl('#moreresults').removeClass('hoverlink').unbind();
                }
            }
        });
    }
});

function BaseCompanyListPageClass() {
    this.queryString = new QueryStringClass();
    this.type = this.queryString.vars.type || 'top';
    this.val = this.queryString.vars.val ? decodeURIComponent(this.queryString.vars.val) : '';
    this.searchtext = this.queryString.vars.searchtext ? decodeURIComponent(this.queryString.vars.searchtext) : '';
    this.data = { max_results: 20 };
    this.setListingSearch();
};
pl.implement(BaseCompanyListPageClass,{
    setListingSearch: function() {
        var searchtype = 'top';
        if (this.type === 'category') {
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
        if (this.type === 'valuation') {
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
            name,
            count,
            itemurl;
        for (i = 0; i < list.length; i++) {
            item = list[i];
            name = item[0];
            count = item[1];
            itemurl = '/main-page.html?type=' + this.options.type + '&amp;val=' + encodeURIComponent(name);
            htmlCol1 +=
                 '<a href="' + itemurl + '" class="hoverlink">'
                +   '<li>'
                +     '<span class="sideboxlistnum span-1">' + count + '</span>'
                +     '<span class="sideboxlistname last">' + name + '</span>'
                +   '</li>'
                + '</a>';
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
    this.wrapperid = id + 'wrapper';
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
            list.push([k, v]);
        }
        list.sort(function(a, b) {
            if (a[1] === b[1]) {
                return a[0] - b[0];
            }
            else {
                return b[1] - a[1];
            }
        });
        if (self.over === 2) {
            lc.spreadOverTwoCols(list, '#'+self.col1id, '#'+self.col2id);
        }
        else {
            lc.spreadOverOneCol(list, '#'+self.col1id, this.type);
        }
        pl('#' + this.wrapperid).show();
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

