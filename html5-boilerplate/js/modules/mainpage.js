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

function BaseListClass(url, id, over, type) {
    this.url = url;
    this.msgid = id + 'msg';
    this.col1id = id + 'divcol1';
    this.col2id = id + 'divcol2';
    this.over = over ? over : 1;
    this.type = type;
}
pl.implement(BaseListClass, {
    load: function() {
        var ajax = new AjaxClass(this.url, this.msgid, this.genStore());
        ajax.call();
    },
    genStore: function() {
        var self = this;
        return function(json) {
            var list = [], 
                lc = new ListClass({type: self.type}),
                k,
                v;
            for (k in json) {
                v = json[k];
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

function MainPageClass() {}
pl.implement(MainPageClass,{
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass(),
                    searchbox = new SearchBoxClass(),
                    companyList = new CompanyListClass();
                header.setLogin(json);
                searchbox.bindEvents();
                companyList.storeList(json,4);
            },
            basePage = new BaseCompanyListPageClass(),
            categoryList = new BaseListClass('/listings/categories', 'category', 2, 'category'),
            locationList = new BaseListClass('/listings/locations', 'location', 2, 'location');
        basePage.loadPage(completeFunc);
        categoryList.load();
        locationList.load();
    }
});

(new MainPageClass()).loadPage();
