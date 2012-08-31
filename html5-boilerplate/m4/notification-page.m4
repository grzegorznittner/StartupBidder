`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="notification-page">
<div id="wrap">
<div id="main">
'
include(header.m4)
`
<div class="container">

<div class="span-24 preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>

<!-- left column -->
<div class="span-24 initialhidden wrapper">

    <div id="notificationmsg"></div>

    <div class="span-24 last notificationtitle">Notification from <span class="lightblue">startup</span>bidder</div>

    <div class="span-24 notificationmessage" id="notificationmessage"></div>
    <div class="span-24 notificationview initialhidden" id="notificationview">In order to view, please visit the <a href="" id="notificationlink">page</a>.</div>
</div> <!-- end left column -->
</div> <!-- end container -->

<div class="initialhidden" id="notificationlistingwrapper">
'
include(company-banner.m4)
companybannermacro(`', `', `', `', `', `')
`
</div> <!-- end notificationlistingwrapper -->

</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/notifications.js"></script>
<script>
(new SingleNotificationPageClass()).loadPage();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
