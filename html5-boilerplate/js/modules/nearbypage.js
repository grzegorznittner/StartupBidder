function MapPageClass() {}
pl.implement(MapPageClass,{
    load: function(completeFunc) {
        var ajax = new AjaxClass('/listings/all_listing_locations/', 'listingsmsg', completeFunc);
        ajax.call();
    },
    store: function(json) {
        if (json && json.current_lat && json.current_long) {
            this.current_lat = json.current_lat;
            this.current_long = json.current_long;
            this.current_zoom = 6;
        }
        else {
            this.current_lat = 20;
            this.current_long = 0;
            this.current_zoom = 2;
        }
        this.maplistings = (json && json.map_listings) ? json.map_listings : [];
    },
    display: function() {
        var self = this,
            center = new google.maps.LatLng(this.current_lat, this.current_long);
        this.displayMap(center, this.current_zoom);
/*
        if (navigator && navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                function(position) {
                    var coords = position.coords,
                        center = new google.maps.LatLng(coords.latitude, coords.longitude);
                    self.map.setZoom(6);
                    self.map.panTo(center);
                },
                function() {
                },
                {
                    maximumAge: 6000000
                }
            );
        }
*/
    },
    displayMap: function(center, zoom) {
        var map = new google.maps.Map(pl('#map').get(0), {
                center: center,
                zoom: zoom,
                mapTypeId: google.maps.MapTypeId.ROADMAP
            }),
            i,
            maplisting,
            listingid,
            latitude,
            longitude,
            latLng,
            marker,
            markers = [],
            markerCluster,
            infowindow = new google.maps.InfoWindow(),
            genInfoDisplay = function(listingid, marker, infowindow) {
                return function() {
                    var completeFunc = function(json) {
                            var listing, tile, el;
                            if (json && json.listing) {
                                listing = json.listing;
                                tile = new CompanyTileClass({json: listing}),
                                el = pl('<div>').addClass('infowindowwrapper').html(tile.makeInfoWindowHtml()).get(0);
                                infowindow.setContent(el);
                            }
                        },
                        ajax = new AjaxClass('/listings/get/' + listingid, 'infowindowmsg', completeFunc),
                        el = pl('<div>').addClass('infowindowwrapper').html('<span class="inputmsg inprogress" id="infowindowmsg">Loading...</span>').get(0);
                    infowindow.setContent(el);
                    infowindow.open(map, marker);
                    ajax.call();
                }
            };
        this.map = map;
        for (i = 0; i < this.maplistings.length; i++) {
            maplisting = this.maplistings[i];
            listingid = maplisting[0];
            latitude = maplisting[1];
            longitude = maplisting[2];
            latLng = new google.maps.LatLng(latitude, longitude);
            marker = new google.maps.Marker({
                cursor: 'pointer',
                position: latLng,
                raiseOnDrag: false
            });
            google.maps.event.addListener(marker, 'click', genInfoDisplay(listingid, marker, infowindow));
            markers.push(marker);
        }
        markerCluster = new MarkerClusterer(map, markers, { maxZoom: 15 });
    }
});

function NearbyPageClass() {}
pl.implement(NearbyPageClass,{
    loadPage: function() {
        var mapPage = new MapPageClass(),
            completeFunc = function(json) {
                var header = new HeaderClass();
                header.setLogin(json);
                mapPage.store(json);
                mapPage.display();
            };
        mapPage.load(completeFunc);
    }
});

(new NearbyPageClass()).loadPage();
