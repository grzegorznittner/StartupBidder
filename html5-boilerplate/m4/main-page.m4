`
<!doctype html>
<html lang="en">
'
include(mainhead.m4)
`
<body class="main-page">
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
<div class="initialhidden wrapper">

<!-- left column -->
<div class="span-16">
    <div class="boxtitle smokegrey" id="listingstitle"></div>
    <div id="companydiv"></div>
</div>
<!-- end left column -->
'
include(main-rightcol.m4)
`
</div>
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script>
(new MainPageClass()).loadPage();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
