function CompanyTileClass() {}
pl.implement(CompanyTileClass, {
    setValues: function(json) {
        var date, images, listingdate;
        date = new DateClass();
        images = ['einstein','wave','upgrades','telecom','socialrec','gkleen']; // FIXME
        listingdate = json.listing_date ? date.format(json.listing_date) : 'not posted';
        this.imgClass = images[Math.floor(Math.random()*images.length)];
        this.daysText = json.days_left ? (json.days_left === 0 ? 'closing today!' : (json.days_left < 0 ? 'bidding closed' : json.days_left + ' days left')) : 'closed for bidding';
        this.typeText = Math.floor(Math.random()*2) ? 'INTERNET' : 'SOFTWARE'; // FIXME
        this.votes = json.num_votes || 0;
        this.posted = listingdate;
        this.name = json.title || 'Listed Company';
        this.loc = Math.floor(Math.random()*2) ? 'London, UK' : 'San Jose, CA, USA'; // FIXME
        this.details = json.mantra || json.summary || 'Company details not provided';
        this.url = '/company-page.html?id=' + json.listing_id;
    },
    makeHtml: function(lastClass) {
        var html;
        html = '\
<span class="span-4 '+ (lastClass?lastClass:'') +'">\
<div class="tile">\
<a href="' + this.url + '"><div class="tileimg ' + this.imgClass + '"></div></a>\
<div class="tiledays"></div>\
<div class="tiledaystext">' + this.daysText + '</div>\
<div class="tiletype"></div>\
<div class="tiletypetext">' + this.typeText + '</div>\
<div class="tilepoints"></div>\
<div class="tilepointstext">\
    <div class="tilevotes">' + this.votes + '</div>\
    <div class="thumbup tilevoteimg"></div>\
    <div class="tileposted">' + this.posted + '</div>\
</div>\
<a href="' + this.url + '">\
<p class="tiledesc">\
    <span class="tilecompany">' + this.name + '</span><br/>\
    <span class="tileloc">' + this.loc + '</span><br/>\
    <span class="tiledetails">' + this.details + '</span>\
</p>\
</a>\
</div>\
</span>\
';
        return html;
    }
});

function CompanyListClass() {}
pl.implement(CompanyListClass, {
    storeList: function(json, colsPerRow, companydiv, companykey) {
        var companysel, companies, html;
        colsPerRow = colsPerRow || 4;
        companydiv = companydiv || 'companydiv';
        companykey = companykey || 'listings';
        companysel = '#' + companydiv;
        companies = json && json[companykey] && json[companykey].length > 0 ? json[companykey] : null;
        html = "";
        if (!companies) {
            pl(companysel).html('<span class="attention">No companies found</span>');
            return;
        }
        companies.reverse(); // prevel has a bug, it does each in reverse order
        pl.each(companies, function(i, company) {
            var tile, last, reali;
            tile  = new CompanyTileClass();
            tile.setValues(company);
            reali = companies.length - (i+1);
            last = (reali+1)%colsPerRow === 0 ? 'last' : '';
            html += tile.makeHtml(last);
        });
        pl(companysel).html(html);
    }
});

function BaseCompanyListPageClass() {};
pl.implement(BaseCompanyListPageClass,{
    setListingsType: function(type) {
        this.type = type;
    },
    getListingsType: function() {
        if (!this.type) {
            if (!this.queryString) {
                //console.log('about to include...');
                //this.queryString = new (pl.include('Base').QueryStringClass)();
                this.queryString = new QueryStringClass();
                //console.log('did it...');
                this.queryString.load();
            }
            this.type = this.queryString.vars.type || 'top';
        }
        return this.type;
    },
    getListingsUrl: function(type, listing_id) {
        if (type === 'related') { // FIXME: related unimplemented
            // type = 'related?id='+listing_id
            type = 'top';
        }
        var url = '/listings/' + type;
        return url;
    },
    storeListingsTitle: function(type) {
        var title = type.toUpperCase() + ' COMPANIES';
        pl('#listingstitle').html(title);
    },
    loadPage: function(completeFunc) {
        var type, url, ajax;
        type = this.getListingsType();
        this.storeListingsTitle(type);
        url = this.getListingsUrl(type);
        ajax = new AjaxClass(url, 'companydiv', completeFunc);
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
        basePage.setListingsType('related', this.listing_id);
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
        this.allCompanies = [{"num":1,"listing_id":"ag1zdGFydHVwYmlkZGVychcLEgdMaXN0aW5nIgpraWxsIGVtYWlsDA","title":"Kill email","suggested_amt":20000,"suggested_pct":50,"suggested_val":40000,"previous_val":40000,"valuation":40000,"median_valuation":75000,"score":3316,"listing_date":"20111018","closing_date":"20111117","status":"active","mantra":"Making email as obsolete as the memo.","summary":"If any startup says it's going to eliminate email, it's destined for failure. You can iterate on the inbox, and try to improve it, but even that's not much of a business. The latest high profile flop in this arena is Google Wave. It was supposed to change email forever. It was going to displace email. Didn't happen.","profile_id":"ag1zdGFydHVwYmlkZGVychkLEgRVc2VyIg9idXNpbmVzc2luc2lkZXIM","profile_username":"Insider","num_comments":38,"num_bids":15,"num_votes":4,"votable":true,"days_ago":95,"days_left":-66,"mockData":true,"business_plan_id":null,"presentation_id":null,"financials_id":null},{"num":2,"listing_id":"ag1zdGFydHVwYmlkZGVychsLEgdMaXN0aW5nIg5sb2NhbG5ld3NzaXRlcww","title":"Local news sites","suggested_amt":9800,"suggested_pct":20,"suggested_val":49000,"previous_val":365217,"valuation":365217,"median_valuation":77000,"score":1326,"listing_date":"20111011","closing_date":"20111110","status":"active","mantra":"Local news made global","summary":"Maybe Tim Armstrong, AOL, and Patch will prove it wrong, but to this point nobody has been able to crack the local news market and make a sustainable business.In theory creating a network of local news sites that people care about is a good idea. You build a community, there's a baked in advertising group with local businesses, and classifieds. But, it appears to be too niche to scale into a big business.","profile_id":"ag1zdGFydHVwYmlkZGVychQLEgRVc2VyIgpkcmFnb25zZGVuDA","profile_username":"The Dragon","num_comments":22,"num_bids":19,"num_votes":11,"votable":false,"days_ago":102,"days_left":-73,"mockData":true,"business_plan_id":null,"presentation_id":null,"financials_id":null},{"num":3,"listing_id":"ag1zdGFydHVwYmlkZGVyciMLEgdMaXN0aW5nIhZjb21wX3VwZ3JhZGluZ19zZXJ2aWNlDA","title":"Computer Upgrading Service","suggested_amt":11550,"suggested_pct":33,"suggested_val":35000,"previous_val":35000,"valuation":35000,"median_valuation":63000,"score":899,"listing_date":"20111009","closing_date":"20111108","status":"active","mantra":"Taking the pain out of managing your technology","summary":"Starting a business that specializes in upgrading existing computer systems with new internal and external equipment is a terrific homebased business to initiate that has great potential to earn an outstanding income for the operator of the business. A computer upgrading service is a very easy business to get rolling, providing you have the skills and equipment necessary to complete upgrading tasks, such as installing more memory into the hard drive, replacing a hard drive, or adding a new disk drive to the computer system. Ideally, to secure the most profitable segment of the potential market, the service should specialize in upgrading business computers as there are many reasons why a business would upgrade a computer system as opposed to replacing the computer system. Additionally, managing the business from a homebased location while providing clients with a mobile service is the best way to keep operating overheads minimized and potentially increases the size of the target market by expanding the service area, due to the fact the business operates on a mobile format.","profile_id":"ag1zdGFydHVwYmlkZGVychkLEgRVc2VyIg9ncnplZ29yem5pdHRuZXIM","profile_username":"Greg","num_comments":34,"num_bids":27,"num_votes":11,"votable":false,"days_ago":104,"days_left":-75,"mockData":true,"business_plan_id":null,"presentation_id":null,"financials_id":null},{"num":4,"listing_id":"ag1zdGFydHVwYmlkZGVychsLEgdMaXN0aW5nIg5zZW1hbnRpY3NlYXJjaAw","title":"Semantic Search","suggested_amt":18000,"suggested_pct":45,"suggested_val":40000,"previous_val":40000,"valuation":40000,"median_valuation":61500,"score":639,"listing_date":"20111005","closing_date":"20111104","status":"active","mantra":"Adding meaning to your search experience","summary":"The fact of the matter is Google, and to a much lesser extent Bing, own the search market. Ask Barry Diller, if you don't believe us.Yet, startups still spring up hoping to disrupt the incumbents. Cuil flopped. Wolfram Alpha is irrelevant. Powerset, which was a semantic search engine was bailed out by Microsoft, which acquired it.","profile_id":"ag1zdGFydHVwYmlkZGVychILEgRVc2VyIghqcGZvd2xlcgw","profile_username":"fowler","num_comments":2,"num_bids":24,"num_votes":4,"votable":false,"days_ago":108,"days_left":-79,"mockData":true,"business_plan_id":null,"presentation_id":null,"financials_id":null},{"num":5,"listing_id":"ag1zdGFydHVwYmlkZGVyciILEgdMaXN0aW5nIhVzb2NpYWxyZWNvbW1lbmRhdGlvbnMM","title":"Social recommendations","suggested_amt":1500,"suggested_pct":10,"suggested_val":15000,"previous_val":15000,"valuation":15000,"median_valuation":42500,"score":412,"listing_date":"20111004","closing_date":"20111103","status":"active","mantra":"Purchasing for status in your peer group","summary":"It's a very tempting idea. Collect data from people about their tastes and preferences. Then use that data to create recommendations for others. Or, use that data to create recommendations for the people that filled in the information. It doesn't work. The latest to try is Hunch and Get Glue.Hunch is pivoting towards non-consumer-facing white label business. Get Glue has had some success of late, but it's hardly a breakout business.","profile_id":"ag1zdGFydHVwYmlkZGVychELEgRVc2VyIgdjaGluZXNlDA","profile_username":"The One","num_comments":42,"num_bids":20,"num_votes":3,"votable":false,"days_ago":109,"days_left":-80,"mockData":true,"business_plan_id":null,"presentation_id":null,"financials_id":null}];
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

