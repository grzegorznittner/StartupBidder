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

<!-- right column -->
<div class="span-8 last">

    <div class="boxtitle">YOUR PROFILE</div>
    <div class="sidebox profilebox">
        <div class="formfull">
            <span class="formtitle" id="username"></span>
        </div>
        <div class="formcol1 clear">
            <div><span class="profiletext" id="email"></span></div>
            <div><span class="profiletext" id="name"></span></div>
            <div>
                <span id="investor" class="span-4"></span>
                <a href="edit-profile-page.html">
                    <span class="span-3 inputbutton" id="editprofilebutton">
                        EDIT
                    </span>
                </a>
            </div>
       </div>
    </div>

    <div class="boxtitle clear">YOUR NOTIFICATIONS</div>
    <div class="sidebox" id="notifylist"></div>

</div> <!-- end right column -->

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
