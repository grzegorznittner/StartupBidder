function ListClass() {}
pl.implement(ListClass, {
    ucFirst: function(str) {
        return str.length > 1 ? str.substr(0,1).toUpperCase() + str.substr(1) : str.toUpperCase();
    },
    spreadOverOneCol: function(list, divcol1) {
        var htmlCol1, i, item;
        htmlCol1 = '';
        for (i = 0; i < list.length; i++) {
            item = this.ucFirst(list[i]);
            htmlCol1 += '<li>'+item+'</li>';
        }
        pl(divcol1).html(htmlCol1);
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

function BaseListClass(url, id, over) {
    console.log(url, id);
    this.url = url;
    this.msgid = id + 'msg';
    this.col1id = id + 'divcol1';
    this.col2id = id + 'divcol2';
    this.over = over ? over : 1;
    console.log( this.url, this.msgid, this.col1id, this.col2id);
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
                lc = new ListClass(),
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
                lc.spreadOverOneCol(list, '#'+self.col1id);
            }
        }
    }
});

function MainPageClass() {};
pl.implement(MainPageClass,{
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass(),
                    companyList = new CompanyListClass();
                header.setLogin(json);
                companyList.storeList(json,4);
            },
            basePage = new BaseCompanyListPageClass(),
            categoryList = new BaseListClass('/listings/categories', 'category', 2),
            locationList = new BaseListClass('/listings/locations', 'location');
        basePage.loadPage(completeFunc);
        categoryList.load();
        locationList.load();
    }
});

(new MainPageClass()).loadPage();
