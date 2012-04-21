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
`
<div class="container">

<!-- left column -->
<div class="span-16">
    <div class="boxtitle">YOUR PROFILE</div>
    <div class="boxpanel">
        <div id="profilestatus"></div>
        <div class="formfull">
            <span class="formtitle" id="name"></span>
        </div>
        <div class="formcol1 clear">
            <span id="title"></span><br/>
            <span id="organization"></span><br/>
            <br/>
            <span id="email"></span><br/>
            <span id="phone"></span><br/>
            <span id="address"></span><br/>
        </div>
        <div class="formcol2">
            Member since <span id="joineddate"></span><br/>
            <span id="investor"></span><br/>
            Email notifications <span id="notifyenabled"></span><br/>
            <br/>
            <a href="edit-profile-page.html">
                <span class="push-4 span-3 inputbutton" id="editprofilebutton">
                    EDIT
                </span>
            </a>
        </div>
    </div>

    <div id="no_listings_wrapper" class="initialhidden">
        <div class="boxtitle smokegrey">YOUR LISTINGS</div>
        <div class="boxpanel">
            You currently have no listings.
            <a href="/new-listing-basics-page.html" class="inputmsg hoverlink">Post</a> a new listing or
            <a href="/main-page.html?type=closing" class="inputmsg hoverlink">invest</a> in one today!
        </div>
    </div>

    <div id="edited_listing_wrapper" class="initialhidden">
        <div class="boxtitle smokegrey">YOUR IN-PROGRESS LISTING</div>
        <div id="edited_listing"></div>
    </div>

    <div id="active_listings_wrapper" class="initialhidden">
        <div class="boxtitle smokegrey">YOUR ACTIVE LISTINGS</div>
        <div id="active_listings"></div>
    </div>

    <div id="monitored_listings_wrapper" class="initialhidden">
        <div class="boxtitle smokegrey">YOUR MONITORED LISTINGS</div>
        <div id="monitored_listings"></div>
    </div>

    <div id="closed_listings_wrapper" class="initialhidden">
        <div class="boxtitle smokegrey">YOUR CLOSED LISTINGS</div>
        <div id="closed_listings"></div>
    </div>

    <div id="withdrawn_listings_wrapper" class="initialhidden">
        <div class="boxtitle smokegrey">YOUR WITHDRAWN LISTINGS</div>
        <div id="withdrawn_listings"></div>
    </div>

    <div id="frozen_listings_wrapper" class="initialhidden">
        <div class="boxtitle smokegrey">YOUR FROZEN LISTINGS</div>
        <div id="frozen_listings"></div>
    </div>

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">

    <!--   
    <div class="boxtitle clear">YOUR NOTIFICATIONS</div>
    <div class="sidebox" id="notifylist"></div>
    -->

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
  <script src="js/modules/profilepage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
