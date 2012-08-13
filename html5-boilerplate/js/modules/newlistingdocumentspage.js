function NewListingDocumentsClass() {
    var queryString = new QueryStringClass();
    this.id = queryString.vars.id;
    this.listing_id = this.id;
    this.base = new NewListingBaseClass();
}
pl.implement(NewListingDocumentsClass, {
    load: function() {
        var self = this,
            url = this.id
                ? '/listing/get/' + this.id
                : '/listings/create',
            complete = function(json) {
                var listing = json && json.listing ? json.listing : {},
                    header = new HeaderClass();
                header.setLogin(json);
                self.base.store(listing);
                self.display();
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            error = function(errornum, json) {
                (new HeaderClass()).setLogin(json);
                pl('.preloader').hide();
                pl('.errorwrapper').show();
            },
            ajax = new AjaxClass(url, 'newlistingmsg', complete, null, null, error);
        if (url === '/listings/create') {
            ajax.setPost();
        }
        ajax.call();
    },
    display: function() {
        var status = this.base.listing.status;
        if (status !== 'new' && status !== 'posted' && status !== 'active') {
            document.location = '/company-page.html?id=' + this.base.listing.id;
        }
        if (!this.bound) {
            this.bindEvents();
            this.bound = true;
        }
    },
    displayUpload: function(id) {
        var self = this,
            fieldname = id + '_id',
            val = this.base.listing[fieldname],
            //imgclass = id + 'img',
            //imgsel = '#' + imgclass,
            formsel = '#' + id + 'uploadform',
            //linksel = '#' + id + 'link',
            //downloadsel = '#' + id + 'downloadbg, #' + id + 'downloadtext',
            deletesel = '#' + id + 'deletelink span',
            uploadurl = this.base.listing.upload_url;
            //linkurl = val ? '/file/download/' + val : '';
        if (val) {
            //pl(downloadsel).show();
            pl(deletesel).show();
            //pl(imgsel).attr({'class': 'tileimg ' + imgclass});
            //pl(linksel).unbind().attr({href: linkurl});
            self.bindDelete(id, val);
        }
        else {
            //pl(downloadsel).hide();
            pl(deletesel).hide();
            //pl(imgsel).attr({'class': 'tileimg noimage'});
            /*
            pl(linksel).unbind().attr({href: ''}).bind({
                click: function() {
                    return false;
                }
            });
            */
        }
        if (uploadurl) {
            pl(formsel).attr({action: uploadurl});
        }
    },
    bindDelete: function(id, val) {
        var self = this,
            deletesel = '#' + id + 'deletelink';
        pl(deletesel).unbind('click').bind({
            click: function() {
                var completeFunc = function() {
                    var fieldname = id + '_id',
                        //imgclass = id + 'img',
                        //imgsel = '#' + imgclass,
                        //linksel = '#' + id + 'link',
                        deletesel = '#' + id + 'deletelink span';
                        //downloadsel = '#' + id + 'downloadbg, #' + id + 'downloadtext';
                    self.base.listing[fieldname] = null;
                    //pl(downloadsel).hide();
                    pl(deletesel).hide();
                    //pl(imgsel).attr({'class': 'tileimg noimage'});
                    /*
                    pl(linksel).unbind().attr({href: ''}).bind({
                        click: function() {
                            return false;
                        }
                    });
                    */
                    pl('#' + id + 'uploadfile').removeAttr('value');
                    pl('#' + id + 'msg').removeClass('errorcolor').removeClass('inprogress').addClass('successful').text('Document deleted');
                };
                ajax = new AjaxClass('/listings/delete_file/?id=' + self.base.listing.listing_id + '&type='+id.toUpperCase(), id+'msg', completeFunc);
                ajax.setPost();
                ajax.call();
                return false;
            }
        });
    },
    bindUploadField: function(id) {
        var self = this;
            iframesel = '#' + id + 'uploadiframe',
            browsesel = '#' + id + 'uploadfile',
            displayname = id.toUpperCase().replace('_',' ') + ' URL',
            urlid = id + '_url',
            msgid = id + 'msg',
            genPostUpload = function(id) {
                var fieldname = id + '_id',
                    fieldurl = id + '_url',
                    uploadId = id;
                return function(json) {
                    var uploadurl = json && json.listing && json.listing.upload_url || null,
                        val = json && json.listing && json.listing[fieldname] ? json.listing[fieldname] : null;
                    if (uploadurl) {
                        self.base.listing.upload_url = uploadurl;
                        self.setUploadUrls();
                    }
                    if (val) {
                        self.base.listing[fieldname] = val;
                        self.displayUpload(uploadId);
                        self.base.displayCalculated();
                        pl('#' + fieldurl).attr({value: ''});
                        pl('#' + id + 'msg').removeClass('errorcolor').removeClass('inprogress').addClass('successful').text('Document uploaded');
                    }
                };
            },
            updater = self.base.getUpdater(urlid, null, genPostUpload(id)),
            field = new TextFieldClass(urlid, null, updater, msgid),
            genIframeLoad = function(id) {
                var iframesel = '#' + id + 'uploadiframe',
                    formsel = '#' + id + 'uploadform',
                    fieldname = id + '_id';
                return function() {
                    var iframe = pl(iframesel).get(0),
                        iframehtml = iframe && iframe.contentDocument && iframe.contentDocument.body ? iframe.contentDocument.body.innerHTML : '',
                        uploadurlmatch = iframehtml.match(/upload_url&gt;(.*)&lt;\/upload_url/),
                        uploadurl = uploadurlmatch && uploadurlmatch.length === 2 ? uploadurlmatch[1] : null,
                        valmatch = iframehtml.match(/value&gt;(.*)&lt;\/value/),
                        val = valmatch && valmatch.length === 2 ? valmatch[1] : null,
                        iframeloc = iframe.contentWindow.location,
                        errorMsg = iframeloc && iframeloc.search && iframeloc.search ? decodeURIComponent(iframeloc.search.replace(/^[?]errorMsg=/, '')) : null;
                    if (uploadurl && uploadurl !== 'null') {
                        self.base.listing.upload_url = uploadurl;
                        self.setUploadUrls();
                    }
                    if (uploadurl && val && !errorMsg) {
                        self.base.listing[fieldname] = val;
                        pl('#'+id+'msg').removeClass('errorcolor').removeClass('inprogress').addClass('successful').text('Document uploaded');
                        pl('#'+id+'_url, #'+id+'uploadfile').attr({value: ''});
                        self.displayUpload(id);
                        self.base.displayCalculated();
                    }
                    else {
                        self.base.listing[fieldname] = null;
                        pl('#'+id+'msg').addClass('errorcolor').text(errorMsg || 'Unable to upload document');
                    }
                };
            },
            genBrowseChange = function(id) {
                var formsel = '#' + id + 'uploadform';
                return function() {
                    pl('#' + id + 'msg').removeClass('inprogress').addClass('inprogress').text('Uploading...');
                    pl(formsel).get(0).submit();
                    return false;
                };
            };
        pl(iframesel).bind({
            load: genIframeLoad(id)
        });
        pl(browsesel).bind({
            change: genBrowseChange(id)
        });
        field.fieldBase.setDisplayName(displayname);
        field.fieldBase.addValidator(ValidatorClass.prototype.isURLEmptyOk);
        field.fieldBase.isEmptyNoUpdate = true;
        field.bindEvents();
        self.displayUpload(id);
    },
    bindEvents: function() {
        var self = this,
            uploadFields = ['presentation', 'business_plan', 'financials'],
            id,
            cleaner,
            field;
        self.base.fields = [];
        self.base.fieldMap = {};
        for (i = 0; i < uploadFields.length; i++) {
            id = uploadFields[i];
            this.bindUploadField(id);
        }
        this.base.bindNavButtons();
        this.base.bindTitleInfo();
        pl('#newlistingdocumentswrapper').show();
    },
    setUploadUrls: function() {
        pl('#presentationuploadform, #business_planuploadform, #financialsuploadform').attr({action: this.base.listing.upload_url});
    }
});

function NewListingPageClass() {};
pl.implement(NewListingPageClass, {
    loadPage: function() {
        var newlisting = new NewListingDocumentsClass();
        newlisting.load();
    }
});

(new NewListingPageClass()).loadPage();

