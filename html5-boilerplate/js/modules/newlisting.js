function NewListingClass() {
    var editableprops = ['title', 'suggested_amt', 'suggested_pct', 'summary', 'business_plan_url', 'presentation_url',
            'category', 'mantra', 'website', 'address'],
        companyTile = new CompanyTileClass({preview: true}),
        categoryCompleteFunc = function(json) {
            var makeOptions = function(json) {
                    var options = [],
                        sorter = function(a, b) {
                            return a[0] - b[0];
                        },
                        cat,
                        catid;
                    for (catid in json) {
                        cat = json[catid];
                        options.push([cat, catid]);
                    }
                    return options.sort(sorter);
                },
                options = makeOptions(json),
                makeHtml = function(options) {
                    var i, option, cat, catid,
                        html = '<option value="0">Select...</option>\n';
                    for (i = 0; i < options.length; i++) {
                        option = options[i];
                        cat = option[0];
                        catid = option[1];
                        html += '<option value="' + catid + '">' + cat + '</option>\n';
                    }
                    return html;
                },
                optionsHtml = makeHtml(options);
            pl('#category').html(optionsHtml);
        },
        ajax = new AjaxClass('/listing/categories', 'newlistingmsg', categoryCompleteFunc);
    this.editableprops = editableprops;
    this.companyTile = companyTile;
    this.listing = {};
    this.profile = {};
    ajax.call();
};
pl.implement(NewListingClass, {
    load: function() {
        var self = this,
            completeFunc = function(json) {
                var listing = json && json.listing ? json.listing : {},
                    header = new HeaderClass();
                header.setLogin(json);
                self.store(listing);
                self.display();
            };
        ajax = new AjaxClass('/listings/create', 'newlistingmsg', completeFunc);
        ajax.setPost();
        ajax.call();
    },
    store: function(json) {
        var self = this;
        if (json) {
            CollectionsClass.prototype.merge(this.listing, json);
            this.displayCalculated();
        }
    },
    displayCalculated: function() {
        this.displayPctComplete();
        this.displaySummaryPreview();
    },
    genDisplayCalculatedIfValid: function(field) {
        var self = this;
        return function(result, val) {
            var id = field.fieldBase.id;
            if (result === 0) {
                self.listing[id] = val;
                self.displayCalculated();
            }
        };
    },
    displayPctComplete: function() {
        var numprops = this.editableprops.length,
            filledprops = 0,
            pctcomplete,
            i,
            k;
        for (i = 0; i < numprops; i++) {
            k = this.editableprops[i];
            if (this.listing[k]) {
                filledprops++
            }
        } 
        pctcomplete = Math.floor(100 * filledprops / numprops);
        pctwidth = Math.floor(626 * pctcomplete / 100) + 'px';
        pl('#boxsteppct').text(pctcomplete);
        pl('#boxstepn').css({width: pctwidth});
    },
    display: function() {
        if (!this.bound) {
            this.bindEvents();
            this.bound = true;
        }
    },
    addMap: function() {
        var autoField = pl('#address').get(0),
            autoOptions = {
                types: [ 'geocode' ]
            },
            mapField = pl('#addressmap').get(0),
            mapOptions = {
                center: new google.maps.LatLng(-33.8688, 151.2195),
                zoom: 13,
                mapTypeId: google.maps.MapTypeId.ROADMAP
            }, 
            addressAuto = new google.maps.places.Autocomplete(autoField, autoOptions),
            addressMap = new google.maps.Map(mapField, mapOptions),
            marker = new google.maps.Marker({map: this.addressMap});
        addressAuto.bindTo('bounds', addressMap);
        google.maps.event.addListener(addressAuto, 'place_changed', function() {
            var place = addressAuto.getPlace(),
                image;
            if (place && place.geometry && place.geometry.location) {
                if (place.geometry.viewport) {
                    addressMap.fitBounds(place.geometry.viewport);
                    image = new google.maps.MarkerImage(
                        place.icon,
                        new google.maps.Size(71, 71),
                        new google.maps.Point(0, 0),
                        new google.maps.Point(17, 34),
                        new google.maps.Size(35, 35));
                    marker.setIcon(image);
                    marker.setPosition(place.geometry.location);
                }
                else {
                    addressMap.setCenter(place.geometry.location);
                    addressMap.setZoom(17);
                }
            }
        });
    },
    bindEvents: function() {
        var self = this,
            textFields = ['title', 'category', 'mantra', 'address', 'website'],
            validators = {
                title: ValidatorClass.prototype.isNotEmpty,
                category: ValidatorClass.prototype.isSelected,
                mantra: ValidatorClass.prototype.makeLengthChecker(5, 140),
                website: ValidatorClass.prototype.isURL,
                address: ValidatorClass.prototype.isNotEmpty
            },
            id,
            field;
        this.fields = [];
        for (i = 0; i < textFields.length; i++) {
            id = textFields[i];
            field = new TextFieldClass(id, this.listing[id], this.getUpdater(id), 'infomsg');
            if (id === 'address') {
                field.fieldBase.setDisplayName('LOCATION');
            }
            field.fieldBase.addValidator(validators[id]);
            field.fieldBase.validator.postValidator = self.genDisplayCalculatedIfValid(field);
            field.bindEvents();
            this.fields.push(field);
        } 
        this.addMap();
        this.bindNextButton();
        this.bindWithdrawButton();
    },
    bindNextButton: function() {
        var self = this;
        pl('#nextbuttonlink').bind({
            click: function() {
                var validmsg = 0,
                    validmsgs = [],
                    i, field, validMsg, displayName;
                for (i = 0; i < self.fields.length; i++) {
                    field = self.fields[i];
                    validmsg = field.validate();
                    displayName = field.fieldBase.getDisplayName();
                    if (validmsg !== 0) {
                        validmsgs.push(displayName + ': ' + validmsg);
                    }
                }
                if (validmsgs.length > 0) {
                    pl('#infomsg').addClass('errorcolor').html('Please correct: ' + validmsgs.join(' '));
                }
                else {
                    self.loadNextPage();
                }
                return false;
            }
        });
    },
    bindWithdrawButton: function() {
        pl('#newwithdrawbtn').bind({
            click: function() {
                var completeFunc = function() {
                        pl('#newwithdrawmsg, #newwithdrawcancelbtn').hide();
                        document.location = '/';
                    },
                    ajax = new AjaxClass('/listing/delete', 'newwithdrawmsg', completeFunc);
                if (pl('#newwithdrawcancelbtn').css('display') === 'none') { // first call
                    pl('#newwithdrawmsg, #newwithdrawcancelbtn').show();
                }
                else {
                    ajax.setPost();
                    ajax.call();
                }
                return false;
            }
        });
        pl('#newwithdrawcancelbtn').bind({
            click: function() {
                pl('#newwithdrawmsg, #newwithdrawcancelbtn').hide();
                return false;
            }
        });
    },
    loadNextPage: function() {
        document.location = '/new-listing-qa-page.html';
    },
    displaySummaryPreview: function() {
        this.companyTile.display(this.listing, 'summarypreview');
    },
    getUpdater: function(fieldName) {
        var self = this;
        return function(newdata, loadFunc, errorFunc, successFunc) {
            var data = { listing: self.listing },
                pctSuccessFunc = function(json) {
                    successFunc(json);
                    self.displayCalculated();
                }
                ajax = new AjaxClass('/listing/update', '', null, pctSuccessFunc, loadFunc, errorFunc),
                newval = newdata ? newdata.changeKey : undefined;
            if (newdata) {
                data.listing[fieldName] = newdata.changeKey;
            }
            ajax.setPostData(data);
            ajax.call();
        };
    }
});

