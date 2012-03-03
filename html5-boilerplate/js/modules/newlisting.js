function NewListingClass() {
    var queryString = new QueryStringClass(),
        virtualProfile = { // FIXME
            loggedin_profile: {
                profile_id: queryString.vars.profile_id,
                username: queryString.vars.username
            }
        },
        listing = {
            title: 'Your Company Name',
            median_valuation: 0,
            num_votes: 0,
            num_bids: 0,
            num_comments: 0,
            profile_id: queryString.vars.profile_id,
            profile_username: queryString.vars.username,
            listing_date: DateClass.prototype.today(),
            closing_date: DateClass.prototype.todayPlus(30),
            status: 'new',
            suggested_amt: 10000,
            suggested_pct: 10,
            suggested_val: 100000,
            summary: 'Your Elevator Pitch.',
            business_plan_url: '',
            presentation_url: ''
        },
        header = new HeaderClass();
    this.listing = listing;
    this.profile = virtualProfile.loggedin_profile;
    header.setLogin(virtualProfile);
};
pl.implement(NewListingClass, {
    load: function() {
        var self = this,
            completeFunc = function(json) {
                //var header = new HeaderClass();
                // header.setLogin(json); // FIXME
                self.store(json);
                self.display();
            },
            ajax = new AjaxClass('/listings/create', 'newlistingmsg', completeFunc);
        ajax.setPostData({listing: this.listing});
        ajax.call();
    },
    store: function(json) {
        var self = this;
        if (json) {
            CollectionsClass.prototype.merge(this.listing, json);
            pl('#newlistingmsg').html('Listing loaded');
        }
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
        // FIXME: add fields: category, mantra, address, website
        var self = this,
            textFields = ['title', 'category', 'mantra', 'address', 'website'],
            msgid = 'infomsg',
            id,
            field;
        this.fields = [];
        for (i = 0; i < textFields.length; i++) {
            id = textFields[i];
            field = new TextFieldClass(id, this.listing[id], this.getUpdater(), msgid);
            if (id === 'address') {
                field.fieldBase.setDisplayName('LOCATION');
            }
            field.fieldBase.addValidator(field.fieldBase.validator.isNotEmpty);
            field.bindEvents();
            this.fields.push(field);
        } 
        this.addMap();
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
                    pl('#'+msgid).addClass('errorcolor').html('Please correct: ' + validmsgs.join(' '));
                }
                else {
                    self.loadNextPage();
                }
                return false;
            }
        });
        pl('#'+msgid).html('&nbsp;');
    },
    loadNextPage: function() {
        var lid = this.listing.listing_id,
            pid = this.profile.profile_id,
            usr = this.profile.username,
            url = '/new-listing-qa-page.html?listing_id=' + lid + '&profile_id=' + pid + '&username=' + usr;
        document.location = url;
    },
    getUpdater: function() {
        var self = this;
        return function(newdata, loadFunc, errorFunc, successFunc) {
            var data = { listing: self.listing },
                ajax = new AjaxClass('/listing/update', '', null, successFunc, loadFunc, errorFunc),
                field;
            for (field in newdata) {
                data.listing[field] = newdata[field];
            }
            ajax.setPostData(data);
            ajax.call();
        };
    }
});

