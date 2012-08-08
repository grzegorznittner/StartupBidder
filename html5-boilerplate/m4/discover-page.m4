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
                <div>Find a startup and invest</div>
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

<!-- left column -->
<div class="span-16">
    <div class="initialhidden wrapper">

        <div class="boxtitle smokegrey">TOP LISTINGS</div>
        <div id="top_listings"></div>
    <!--
        <div class="boxtitle smokegrey">MOST FUNDED</div>
        <div id="valuation_listings"></div>
    -->
        <div class="boxtitle smokegrey">JUST LISTED</div>
        <div id="latest_listings"></div>

    </div> <!-- end wrapper -->
    
</div> <!-- end left column -->

<div class="initialhidden wrapper">

    <div class="span-8 preloader">
        <div class="preloaderfloater"></div>
        <div class="preloadericon"></div>
    </div>

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
