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

    <div class="boxtitle clear"><a name="posted"></a>YOU POSTED</div>
    <div id="posteddiv"></div>

    <div class="boxtitle clear"><a name="bidon"></a>YOU BID ON</div>
    <div id="bidondiv"></div>

    <div class="boxtitle clear"><a name="upvoted"></a>YOU UPVOTED</div>
    <div id="upvoteddiv"></div>

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">
    <div class="boxtitle">YOUR COMPANIES</div>
    <div class="sidebox">
        <a href="#posted">
        <!-- <div class="sideboxcompanylink sideboxselected clear"> -->
        <div class="sideboxcompanylink clear">
            <span class="sideboxtitle" id="mylistingscount">0</span>
            <span class="sideboxtitledetail">YOU POSTED</span>
        </div>
        </a>
        <a href="#bidon">
        <div class="sideboxcompanylink">
            <span class="sideboxtitle" id="biddedoncount">0</span>
            <span class="sideboxtitledetail">YOU BID ON</span>
        </div>
        </a>
        <a href="#upvoted">
        <div class="sideboxcompanylink">
            <span class="sideboxtitle" id="upvotedcount">0</span>
            <span class="sideboxtitledetail">YOU UPVOTED</span>
        </div>
        </a>
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
  <script src="js/modules/profilepage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
