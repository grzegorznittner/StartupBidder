`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="notifications-page">
<div id="wrap">
<div id="main">
'
include(header.m4)
`
<div class="container">

<div class="span-24 preloader preloadershort">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>

<!-- left column -->
<div class="span-24 initialhidden wrapper">

    <div id="notificationmsg"></div>

    <div class="boxtitle smokegrey clear">YOUR NOTIFICATIONS</div>
    <div class="boxpanel boxpanelfull" id="notifylist">
    </div>

</div> <!-- end left column -->
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/notifications.js"></script>
<script>
(new NotificationPageClass()).loadPage();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
