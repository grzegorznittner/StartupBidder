function StartupBidder() {
	$(function() {
		var $ = jQuery;

        // create singleton objects
        var importRefs = {
		    util : Util(),
		    statistics : Statistics(),
		    backend : Backend(),
		    user : User(),
		    uploader : Uploader(),
		    header : Header(),
		    searchListings : SearchListings(),
		    searchUsers : SearchUsers(),
		    userbox : Userbox(),
		    profiles : Profiles(),
		    listingObj : Listings(),
		    comments : Comments(),
		    bids : Bids()
        };
        // and pass down references to objects for internal use by other classes
        var imports = new Imports();
        imports.crossImport(importRefs);

		// setup window
		importRefs.util.applyResizer(importRefs.header.resizeStyles);
		$('#mainbody').addTouch(); // enable for touch devices
		$(window).hashchange(importRefs.util.hashchange);

		// call main window handler to display page
		$(window).hashchange();
	});
}
