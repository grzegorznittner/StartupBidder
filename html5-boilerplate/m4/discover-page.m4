`
<!doctype html>
<html lang="en">
'
include(mainhead.m4)
`
<body class="discover-page">
<div id="fb-root"></div> <!-- for fb like button -->
<div id="wrap">
<div id="main">
'
include(header.m4)
`
<div class="banner" id="banner">
    <div class="container">
        <span class="bannertext span-24">
            <div class="welcometitle" id="welcometitle">
                <div>Getting startups funded</div>
            </div> 
            <div class="welcometext" id="welcometext">
                <a href="/about-page.html" class="welcomelink">Learn more...</a>
            </div>
        </span>
    </div>
</div> <!-- end banner -->

<div class="container">

<div class="span-24 preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>

<div class="span-24 initialhidden wrapper">
    <div class="initialhidden" id="existinglisting">
        <div class="addlistingtitle">You have a pending listing</div>
        <div class="addlistingbutton investbutton" id="editlisting">EDIT LISTING</div>
    </div>
</div>

<div class="span-16 initialhidden wrapper">
    <div class="boxtitle">TOP LISTINGS</div>
    <div id="top_listings"></div>

    <div class="boxtitle">JUST LISTED</div>
    <div id="latest_listings"></div>

    <div id="monitored_listings_wrapper" class="initialhidden">
        <div class="boxtitle">WATCHING</div>
        <div id="monitored_listings"></div>
    </div>
 </div> 

<div class="initialhidden wrapper">
'
include(main-rightcol.m4)
`
</div> <!-- end wrapper -->

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script>
(new DiscoverPageClass()).loadPage();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
