function MapPageClass() {
    this.url = '/listings/top';
    this.data = { max_results: 20 };
};
pl.implement(MapPageClass,{
    load: function(completeFunc) {
        var title = 'NEARBY COMPANIES',
            ajax = new AjaxClass(this.url, 'listingsmsg', completeFunc);
        pl('#listingstitle').html(title);
        pl('#welcometitle').html('Find a startup near you!');
        pl('#welcometext').html('Browse the map below to see companies nearby you and around the world');
        ajax.ajaxOpts.data = this.data;
        ajax.call();
    },
    store: function(json) {
        this.listings = (json && json.listings) ? json.listings : [];
    },
    display: function() {
        var self = this,
            defaultCenter = new google.maps.LatLng(20, 0),
            defaultZoom = 2;
        this.displayMap(defaultCenter, defaultZoom);
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
    },
    displayMap: function(center, zoom) {
        var map = new google.maps.Map(pl('#map').get(0), {
                center: center,
                zoom: zoom,
                mapTypeId: google.maps.MapTypeId.ROADMAP
            }),
            i,
            listing,
            latLng,
            marker,
            markers = [],
            markerCluster,
            infowindow = new google.maps.InfoWindow(),
            genInfoDisplay = function(listing, marker, infowindow) {
                var tile = new CompanyTileClass({json: listing}),
                    el = pl('<div>').html(tile.makeInfoWindowHtml()).get(0);
                return function() {
                    infowindow.setContent(el);
                    infowindow.open(map, marker);
                }
            };
        this.map = map;
        for (i = 0; i < this.listings.length; i++) {
            listing = this.listings[i];
            latLng = new google.maps.LatLng(listing.latitude, listing.longitude);
            marker = new google.maps.Marker({
                cursor: 'pointer',
                position: latLng,
                raiseOnDrag: false,
                title: listing.title
            });
            google.maps.event.addListener(marker, 'click', genInfoDisplay(listing, marker, infowindow));
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
