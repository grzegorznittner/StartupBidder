pl(function() {

    function QueryStringClass() {}
    pl.implement(QueryStringClass, {
        load: function() {
            var i, pairs, keyval;
            this.vars = new Array();
            pairs = window.location.search ? window.location.search.split( "&" ) : {};
            for (i in pairs) {
                keyval = pairs[ i ].split( "=" );
                this.vars[ keyval[0] ] = keyval[1];
            }
        }
    });

    function HeaderClass() {}
    pl.implement(HeaderClass, {
        setLogin: function(json) {
            var profile = json ? json.loggedin_profile : null;
            var username;
            if (profile) {
                username = profile.username || 'You';
                pl('#loginlink').attr('href', 'profile-page.html');
                pl('#logintext').html(username);
                pl('#logout').css({display:'block'});
            }
            else {
                pl('#loginlink').attr('href', 'login-page.html');
                pl('#logintext').html('Login or Sign Up');
                pl('#logout').css({display:'none'});
            }
        }
    });

    function CompanyTileClass() {}
    pl.implement(CompanyTileClass, {
        setValues: function(json) {
            var images = ['einstein','wave','upgrades','telecom','socialrec','gkleen'];
            this.imgClass = images[Math.floor(Math.random()*6)];
            this.daysText = json.days_left ? (json.days_left === 0 ? 'closing today!' : (json.days_left < 0 ? 'bidding closed' : json.days_left + ' days left')) : 'closed for bidding';
            this.typeText = Math.floor(Math.random()*2) ? 'INTERNET' : 'SOFTWARE'; // FIXME: unimplemented API
            this.votes = json.num_votes || 0;
            this.posted = json.listing_date ? (json.listing_date.substr(0,4) + '-' + json.listing_date.substr(4,2) + '-' + json.listing_date.substr(6,2)) : 'not posted';
            this.name = json.title || 'Listed Company';
            this.loc = Math.floor(Math.random()*2) ? 'London, UK' : 'San Jose, CA, USA'; // FIXME: unimplemented API
            this.details = json.summary || 'Company details not provided';
        },
        makeHtml: function(lastClass) {
            var html = '\
<span class="span-4 '+ (lastClass?lastClass:'') +'">\
<div class="tile">\
    <a href="listing-page.html"><div class="tileimg ' + this.imgClass + '"></div></a>\
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
    <a href="listing-page.html">\
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
/*
orderNumber=1, id=ag1zdGFydHVwYmlkZGVychcLEgdMaXN0aW5nIgpraWxsIGVtYWlsDA, name=Kill email, suggestedAmount=20000, suggestedPercentage=50, suggestedValuation=40000, previousValuation=40000, valuation=40000, medianValuation=75000, score=3316, listedOn=Tue Oct 18 16:50:01 UTC 2011, closingOn=Wed Nov 16 23:00:00 UTC 2011, state=ACTIVE, summary=If any startup says it's going to eliminate email, it's destined for failure. You can iterate on the inbox, and try to improve it, but even that's not much of a business. The latest high profile flop in this arena is Google Wave. It was supposed to change email forever. It was going to displace email. Didn't happen., owner=ag1zdGFydHVwYmlkZGVychkLEgRVc2VyIg9idXNpbmVzc2luc2lkZXIM, ownerName=Insider, numberOfComments=38, numberOfBids=15, numberOfVotes=4, votable=true, daysAgo=86, daysLeft=-57, mockData=true, buinessPlanId=null, presentationId=null, financialsId=null]
*/
/*
<div class="tile">
    <a href="listing-page.html"><div class="tileimg wave"></div></a>
    <div class="tiledays"></div>
    <div class="tiledaystext">30 days left</div>
    <div class="tiletype"></div>
    <div class="tiletypetext">INTERNET</div>
    <div class="tilepoints"></div>
    <div class="tilepointstext">
        <div class="tilevotes">98</div>
        <div class="thumbup tilevoteimg"></div>
        <div class="tileposted">2011-09-16</div>
    </div>
    <a href="listing-page.html">
    <p class="tiledesc">
        <span class="tilecompany">Kill Email Ltd.</span><br/>
        <span class="tileloc">Melbourne, Australia</span><br/>
        <span class="tiledetails">Making email as obsolete as the paper memo through real-time collaborative chat.</span>
    </p>
    </a>
</div>
*/
    function CompanyListClass() {}
    pl.implement(CompanyListClass, {
        storeList: function(json, colsPerRow) {
            var companies, html;
            colsPerRow = colsPerRow || 4;
            companies = json && json.listings && json.listings.length > 0 ? json.listings : null;
            html = "";
            if (!companies) {
                pl('#companydiv').html('<span class="notice">No companies found</span>');
                return;
            }
            //html = '<span class="notice">Found '+companies.length+' companies</span>';
            companies.reverse(); // prevel has a bug, it does each in reverse order
            pl.each(companies, function(i, company) {
                var tile, last, reali;
                tile  = new CompanyTileClass();
                tile.setValues(company);
                reali = companies.length - (i+1);
                last = (reali+1)%colsPerRow === 0 ? 'last' : '';
                html += tile.makeHtml(last);
            });
            pl('#companydiv').html(html);
        }
    });

    function ListClass() {}
    pl.implement(ListClass, {
        ucFirst: function(str) {
            return str.length > 1 ? str.substr(0,1).toUpperCase() + str.substr(1) : str.toUpperCase();
        },
        spreadOverTwoCols: function(list, divcol1, divcol2) {
            var htmlCol1, htmlCol2, mid, i, item;
            mid = Math.floor(list.length / 2);
            htmlCol1 = '';
            for (i = 0; i < mid; i++) {
                item = this.ucFirst(list[i]);
                htmlCol1 += '<li>'+item+'</li>';
            }
            pl(divcol1).html(htmlCol1);
            htmlCol2 = '';
            for (i = mid; i < list.length; i++) {
                item = this.ucFirst(list[i]);
                htmlCol2 += '<li>'+item+'</li>';
            }
            pl(divcol2).html(htmlCol2);
        }
    });

    function CategoryListClass() {}
    pl.implement(CategoryListClass, {
        storeList: function(json) {
            var categories, lc;
            categories = json && json.categories && json.categories.length > 0
                ? json.categories
                : ['biotech','chemical','electronics','energy','environmental','financial','hardware','healthcare','industrial',
                    'internet','manufacturing','media','medical','pharma','retail','software','telecom','other'];
            if (!categories) {
                pl('#categorydivcol1').html('<li class="notice">No categories found</li>');
                pl('#categorydivcol2').html('');
                return;
            }
            categories.sort();
            lc = new ListClass();
            lc.spreadOverTwoCols(categories, '#categorydivcol1', '#categorydivcol2');
        }
    });

    function LocationListClass() {}
    pl.implement(LocationListClass, {
        storeList: function(json) {
            var locations, lc;
            locations = json && json.locations && json.locations.length > 0
                ? json.locations
                : ['Austin, USA', 'Bangalore, India', 'Beijing, China', 'Berlin, Germany', 'Cambridge, USA', 'Dusseldorf, Germany', 'Herzliya, Israel',
                    'Katowice, Poland', 'London, UK', 'Menlo Park, USA', 'Mountain View, CA', 'New York City, USA', 'Seattle, WA', 'Cambridge, UK'];
            if (!locations) {
                pl('#locationdivcol1').html('<li class="notice">No locations found</li>');
                pl('#locationdivcol2').html('');
                return;
            }
            locations.sort();
            lc = new ListClass();
            lc.spreadOverTwoCols(locations, '#locationdivcol1', '#locationdivcol2');
        }
    });

    function BasePageClass() {};
    pl.implement(BasePageClass,{
        getListingsType: function() {
            var type;
            if (!this.queryString) {
                this.queryString = new QueryStringClass();
                this.queryString.load();
            }
            type = this.queryString.vars.type || 'top';
            return type;
        },
        getListingsUrl: function(type) {
            var url = '/listings/' + type;
            return url;
        },
        storeListingsTitle: function(type) {
            var title = type.toUpperCase() + ' COMPANIES';
            pl('#listingstitle').html(title);
        },
        loadPage: function(successFunc) {
            var type = this.getListingsType();
            var url = this.getListingsUrl(type);
            var loadFunc = function() {
                pl('#companydiv').html('<span class="notice">Loading companies...</span>');
            };
            var errorFunc = function(errorNum) {
                pl('#companydiv').html('<span class="attention">Error while loading page: '+errorNum+'</span>');
            };
            var ajax = {
                async: true,
                url: url,
                type: 'GET',
                dataType: 'json',
                charset: 'utf-8',
                load: loadFunc,
                error: errorFunc,
                success: successFunc
            };
            this.storeListingsTitle(type);
            pl.ajax(ajax);
        }
    });

    function MainPageClass() {};
    pl.implement(MainPageClass,{
        loadPage: function() {
            var successFunc, basePage;
            successFunc = function(json) {
                var header, companyList, categoryList, locationList;
                if (!json) {
                    pl('#companydiv').html('<span class="attention">Error: null response from server</span>');
                    return;
                }
                header = new HeaderClass();
                companyList = new CompanyListClass();
                categoryList = new CategoryListClass();
                locationList = new LocationListClass();
                header.setLogin(json);
                companyList.storeList(json,4);
                categoryList.storeList(json);
                locationList.storeList(json);
            };
            basePage = new BasePageClass();
            basePage.loadPage(successFunc);
        }
    });

    function InformationPageClass() {}
    pl.implement(InformationPageClass,{
        loadPage: function() {
            var successFunc, basePage;
            successFunc = function(json) {
                var header, companyList;
                if (!json) {
                    pl('#companydiv').html('<span class="attention">Error: null response from server</span>');
                    return;
                }
                header = new HeaderClass();
                companyList = new CompanyListClass();
                header.setLogin(json);
                companyList.storeList(json,2);
            };
            basePage = new BasePageClass();
            basePage.loadPage(successFunc);
        }
    });

    function DispatcherClass() {}
    pl.implement(DispatcherClass,{
        loadPage: function() {
            var pageClass, page;
            if (pl('body').hasClass('main-page')) {
                pageClass = MainPageClass;
            }
            else {
                pageClass = InformationPageClass;
            }
            page = new pageClass();
            page.loadPage();
        }
    });

    var dispatcher = new DispatcherClass();
    dispatcher.loadPage();
});
