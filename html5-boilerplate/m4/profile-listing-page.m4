`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="profile-page">
<div id="wrap">
<div id="main">
'
include(header.m4)
include(profile-not-found.m4)
`
<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">

<!-- left column -->
<div class="span-16">
    <div id="profilemsg"></div>
    <div class="boxtitle"><span class="titleyour">YOUR </span><span class="titletype"></span> LISTINGS<span class="titleusername"></span></div>
    <div id="companydiv"></div>
</div>
<!-- end left column -->
'
include(profilerightbar.m4)
`
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/profile.js"></script>
<script>
(new ProfileListingPageClass()).loadPage();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
