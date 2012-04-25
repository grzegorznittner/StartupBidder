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

<!-- left column -->
<div class="span-16">

    <div id="notificationmsg"></div>

    <div class="boxtitle smokegrey">NOTIFICATION</div>
    <div class="boxpanel">
        <div class="inputmsg" id="notification_title"></div>
        <div class="" id="notification_message"></div>
    </div>

</div> <!-- end left column -->
'
include(profilerightbar.m4)
`
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
  <script src="js/libs/prevel.min.js"></script>
  <script src="js/modules/base.js"></script>
  <script src="js/modules/companylist.js"></script>
  <script src="js/modules/profile.js"></script>
  <script src="js/modules/notification.js"></script>
  <script src="js/modules/notificationpage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
