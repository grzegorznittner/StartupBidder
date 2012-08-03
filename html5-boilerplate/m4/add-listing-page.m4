`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="new-listing-basics-page">
<div id="wrap">
<div id="newlistingmain">
'
include(header.m4)
`
<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">

<!-- begin banner -->
    <div class="span-24">
        <div class="formitem clear">
            <span class="inputmsg" id="addlistingmsg"></span>
        </div>
    </div>
<!-- end banner -->

<div class="span-24">

<style>
.addlistingtitle { 
    font-family: "Adelle","Helvetica Neue",Helvetica,Arial,sans-serif;
    text-align: center;
    color: #666;
    font-size: 30px;
    margin-top: 20px;
}
.addlistingbutton {
    text-align: center;
    width: 300px;
    margin: 20px auto;
}
.addlistingmsg {
    text-align: center;
    font-size: 30px;
    margin-top: 10px;
}
.addlistinglogin {
    float: left;
}
.addlistingbottom {
    margin-bottom: 35px;
}
.addlistingbuttonrow { }
.addlistingbuttoncol { }
.addlistingloginrow {
    width: 120px;
    margin: 25px auto;
    padding-bottom: 40px;
}
</style>

    <div class="initialhidden" id="notloggedin">
        <div class="addlistingtitle">Log in to add a listing to startupbidder.</div>

        <div class="addlistingloginrow">
            <a id="google_login" href="">
                <div class="addlistinglogin headericon headersignin"></div>
            </a>
            <a id="twitter_login" href="">
                <div class="addlistinglogin headericon headertwittersignin"></div>
            </a>
            <a id="fb_login" href="">
                <div class="addlistinglogin headericon headerfbsignin"></div>
            </a>
        </div>

        <div class="clear addlistingtitle addlistingbottom">You will be redirected afterwards to the add listing page.</div>
    </div>

    <div class="initialhidden" id="existinglisting">
        <div class="addlistingtitle">You have a pending listing on startupbidder.</div>

        <div class="addlistingbutton investbutton" id="editlisting">EDIT LISTING</div>

        <div class="addlistingtitle">or</div>

        <div class="addlistingmsg deletemsg attention" id="deletemsg">Are you sure?<br/>This cannot be undone.</div>
        <div class="addlistingbutton investbutton" id="deletebtn">DELETE</div>
        <div class="addlistingbutton investbutton initialhidden" id="deletecancelbtn">CANCEL</div>

        <div class="addlistingtitle addlistingbottom">and start over</div>

        </div>
    </div>

    <div class="initialhidden" id="newlisting">
        <div class="addlistingtitle">Add a new listing to startupbidder.</div>
    
        <div class="addlistingtitle">Import your application from</div>
    
        <div class="addlistingbuttonrow">
            <a href="/import-query.html?type=AppStore">
                <div class="addlistingbutton addlistingbuttoncol investbutton">App Store</div>
            </a>
            <a href="/import-query.html?type=GooglePlay">
                <div class="addlistingbutton addlistingbuttoncol investbutton">Google Play</div>
            </a>
            <a href="/import-query.html?type=WindowsMarketplace">
                <div class="addlistingbutton addlistingbuttoncol investbutton">Windows Marketplace</div>
            </a>
        </div>
    
        <div class="addlistingtitle">or</div>
    
        <div class="addlistingtitle">Import your company from</div>
    
        <div class="addlistingbuttonrow">
            <a href="/import-query.html?type=CrunchBase">
                <div class="addlistingbutton addlistingbuttoncol investbutton">CrunchBase</div>
            </a>
            <a href="/import-query.html?type=Angelco">
                <div class="addlistingbutton addlistingbuttoncol investbutton">Angel.co</div>
            </a>
            <a href="/import-query.html?type=Startuply">
                <div class="addlistingbutton addlistingbuttoncol investbutton">Startuply</div>
            </a>
        </div>
    
        <div class="addlistingtitle">or</div>
    
        <a href="/new-listing-basics-page.html">
            <div class="addlistingbutton investbutton">CREATE</div>
        </a>

        <div class="addlistingtitle addlistingbottom">a new listing</div>

    </div>

</div> <!-- end span-24 -->
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
  <script src="http://maps.googleapis.com/maps/api/js?libraries=places&sensor=true&language=en-GB"></script>
  <script src="js/libs/prevel.min.js"></script>
  <script src="js/modules/base.js"></script>
  <script src="js/modules/addlistingpage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
