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
`
<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">

<div class="span-24">
    <div class="addlistingtitle">All the Users on Startupbidder</div>

    <div id="profilelistmsg"></div>

    <div class="addlistingcontainer" id="profilelistcontainer">
        <div class="addlistinglist" id="profilelist"></div> 
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
