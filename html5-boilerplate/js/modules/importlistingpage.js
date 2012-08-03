function ImportListingClass() {
    var apptypes = {
        'AppStore': 1,
        'GooglePlay': 1,
        'WindowsMarketplace': 1
    };
    var displaytypes = {
        'AppStore': 'the App Store',
        'GooglePlay': 'Google Play',
        'WindowsMarketplace': 'Windows Marketplace',
        'CrunchBase': 'CrunchBase',
        'Angelco': 'Angel.co',
        'Startuply': 'Startuply'
    };
    this.type = (new QueryStringClass()).vars.type || 'AppStore';
    this.displaytype = displaytypes[this.type];
    this.corporapp = apptypes[this.type] ? 'app' : 'company';
}

pl.implement(ImportListingClass, {

    load: function() {
        var self = this,
            completeFunc = function(json) {
                var header = new HeaderClass();
                header.setLogin(json);
                self.display(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            // ajax = new AjaxClass('/user/loggedin', 'importlistingmsg', completeFunc); // once greg fixes
            ajax = new AjaxClass('/listing/discover', 'importlistingmsg', completeFunc);
        ajax.call();
    },

    store: function(json) {
        if (json) {
            CollectionsClass.prototype.merge(this, json);
        }
    },
   
    display: function(json) {
        if (json) {
            this.store(json);
        }
        if (!this.loggedin_profile) {
            document.location = '/add-listing-page.html';
        }
        pl('#importtype').text(this.displaytype);
        pl('#importcorporapp').text(this.corporapp);
        this.bindSearch();
    },

    bindSearch: function() {
        var self = this,
            doSearch = function() {
                self.doSearch();
            };
        pl('#importquery').bind({
            focus: function() {
                if (pl('#importquery').attr('value') === 'Search') {
                    pl('#importquery').attr({value: ''});
                }
            },
            keyup: function(e) {
                var evt = new EventClass(e);
                if (evt.keyCode() === 13) {
                    self.doSearch();
                    return false;
                }
                return true;
            }
        });
        pl('#importbutton').bind('click', function() {
            self.doSearch();
        });
    },

    doSearch: function() {
        var self = this,
            data = {
                type: this.type,
                query: pl('#importquery').attr('value')
            },
            success = function(json) {
                if (json && json.query_results) {
                    self.displayList(json.query_results);
                }
                else {
                    self.displayEmptyList();
                }
            },
            load = function() {
                self.displayLoadingList();
            },
            error = function(errorCode, json) {
                self.displayErrorList(errorCode, json);
            },
            ajax = new AjaxClass('/listing/query_import', 'importlist', null, success, load, error);
        ajax.setGetData(data);
        ajax.call();
    },

    displayLoadingList: function() {
        var listhtml = '\
            <div class="messageline preloadershort">\
                <div class="preloaderfloater"></div>\
                <div class="preloadericon"></div>\
            </div>\
            ';
        pl('#importlist').html(listhtml);
        pl('#importcontainer').show();
    },

    displayErrorList: function(errorCode, json) {
        var error = json && json.error_msg || 'Error',
            listhtml = '\
            <div class="messageline">\
                <p class="messagetext"><span class="errorcolor">' + error + '</span></p>\
            </div>\
            ';
        pl('#importlist').html(listhtml);
    },

    displayEmptyList: function() {
        var listhtml = '\
            <div class="messageline">\
                <p class="messagetext"><i>No results found</i></p>\
            </div>\
            ';
        pl('#importlist').html(listhtml);
    },

    displayList: function(results) {
        var results = results,
            sorter = function(a, b) {
                return a[1].localeCompare(b[1]);
            },
            list = [],
            listhtml = '',
            listitem,
            importid,
            importtext,
            i;
        for (importid in results) {
            importtext = results[importid] || '';
            listitem = [ importid || '', importtext ];
            list.push(listitem);
        }
        list.sort(sorter);
        for (i = 0; i < list.length; i++) {
            listitem = list[i];
            listhtml += this.makeListItem(listitem);
        }
        if (listhtml) {
            pl('#importlist').html(listhtml);
        }
        else {
            this.displayEmptyList();
        }
    },

    makeListItem: function(listitem) {
        var importid = listitem[0],
            importtext = listitem[1],
            url = '/new-listing-basics-page.html?importtype=' + this.type + '&importid=' + importid,
            html = '\
            <div class="messageline">\
                <a href="' + url + '">\
                    <p class="messagetext">' + importtext + '</p>\
                </a>\
            </div>\
            ';
        return html;
    }

});

(new ImportListingClass()).load();

