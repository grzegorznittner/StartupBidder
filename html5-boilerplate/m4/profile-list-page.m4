`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="profile-list-page">
<div id="wrap">
<div id="main">
'
include(header.m4)
include(not-found.m4)
`
<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">

<div class="span-24">
    <div class="addlistingtitle">The <span id="typetitle">Users</span> on Startupbidder</div>

    <div class="addlistingsubtitle initialhidden" id="listersubtitle">Visionaries creating great startups and applications.</div>

    <div class="addlistingsubtitle initialhidden" id="dragonsubtitle">Experienced investors looking for great startups and applications.</div>

    <div id="profilelistmsg"></div>

    <div class="addlistingcontainer" id="profilelistcontainer">
        <div class="profilelist" id="profilelist"></div> 
    </div>
</div> <!-- end span-24 -->

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/profile.js"></script>
<script>
(new ProfileListClass()).loadPage();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
