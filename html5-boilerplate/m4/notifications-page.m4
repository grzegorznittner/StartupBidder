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

<!-- left column -->
<div class="span-24">

    <div id="notificationmsg"></div>

    <div class="boxtitle smokegrey clear">YOUR NOTIFICATIONS</div>
    <div class="boxpanel boxpanelfull" id="notifylist">
        <div class="span-24 preloadershort">
            <div class="preloaderfloater"></div>
            <div class="preloadericon"></div>
        </div>
    </div>

</div> <!-- end left column -->
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
  <script src="js/libs/prevel.min.js"></script>
  <script src="js/modules/base.js"></script>
  <script src="js/modules/companylist.js"></script>
  <script src="js/modules/notification.js"></script>
  <script src="js/modules/notificationpage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
