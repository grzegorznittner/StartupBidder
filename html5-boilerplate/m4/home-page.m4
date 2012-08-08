`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="home-page">
<div id="wrap">
<div id="newlistingmain">
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

<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">

<!-- begin banner -->
    <div class="span-24">
        <div class="formitem clear">
            <span class="inputmsg" id="homemsg"></span>
        </div>
    </div>
<!-- end banner -->

<div class="span-24">

    <div class="initialhidden" id="haslistings">

        <div class="initialhidden" id="existinglisting">
            <div class="addlistingtitle">You have a pending listing</div>
    
            <div class="addlistingbutton investbutton" id="editlisting">EDIT LISTING</div>
        
        </div>

        <div class="span-16">
            <div id="edited_listing_wrapper" class="initialhidden">
                <div class="boxtitle smokegrey">YOUR PENDING LISTING</div>
                <div id="edited_listing"></div>
            </div>

             <div id="active_listings_wrapper" class="initialhidden">
                <div class="boxtitle smokegrey">YOUR ACTIVE LISTINGS</div>
                <div id="active_listings"></div>
            </div>
        
            <div id="monitored_listings_wrapper" class="initialhidden">
                <div class="boxtitle smokegrey">WATCHING</div>
                <div id="monitored_listings"></div>
            </div>
         
        </div>
        <div class="span-8 last">
            <a href="/discover-page.html">
                <div class="addlistingbutton investbutton firstsidebox">FIND</div>
            </a>
    
            <div class="addlistingtitle">a company or app for investment</div>
        
            <div class="addlistingtitle">or</div>
    
            <a href="/add-listing-page.html">
                <div class="addlistingbutton investbutton">ADD</div>
            </a>
    
            <div class="addlistingtitle addlistingbottom">yours and get funded</div>
        </div> 
    </div>

    <div class="initialhidden" id="nolistings">
        <div class="addlistingtitle">Welcome to startupbidder!</div>

        <a href="/discover-page.html">
            <div class="addlistingbutton investbutton">FIND</div>
        </a>

        <div class="addlistingtitle">a company or app for investment</div>
    
        <div class="addlistingtitle">or</div>

        <a href="/add-listing-page.html">
            <div class="addlistingbutton investbutton">ADD</div>
        </a>

        <div class="addlistingtitle addlistingbottom">yours and get funded</div>
    
    </div>

</div> <!-- end span-24 -->
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script>
(new HomePageClass()).load();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
