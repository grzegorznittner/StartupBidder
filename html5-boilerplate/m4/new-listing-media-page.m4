`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="new-listing-media-page">
<div id="wrap">
<div id="newlistingmain">

<div class="header">
  <div class="container">
    <div class="span-5">
      <a href="main-page.html"><span class="logotextblue">startup</span><span class="logotextgrey">bidder</span></a>
    </div>
    <div class="span-11">
      <a href="main-page.html?type=top"><span class="headerlink">Top</span></a>
      <span class="headertext">&nbsp;&nbsp;|&nbsp;&nbsp;</span>
      <a href="main-page.html?type=latest"><span class="headerlink">Latest</span></a>
      <span class="headertext">&nbsp;&nbsp;|&nbsp;&nbsp;</span>
      <a href="main-page.html?type=closing"><span class="headerlink">Closing</span></a>
      <span class="headertext">&nbsp;&nbsp;|&nbsp;&nbsp;</span>
      <a id="postlink" href="login-page.html"><span class="headerlink" id="posttext"></span></a>
    </div>
    <div class="span-8 last loginspan">
      <div><a id="loginlink" href="login-page.html"><span class="headerlink" id="logintext"></span></a></div>
      <div id="logout" class="logout"><a href="/_ah/logout?continue=%2F">logout</a></div>
    </div>
  </div>
</div>

<div class="container">

<!-- begin banner -->
    <div class="bannertextsmall">NEW COMPANY WIZARD - MEDIA</div>

    <div class="boxstep last">
        <a href="new-listing-basics-page.html"><span class="boxstep1"><div class="boxsteptext">BASICS</div></span></a>
        <span class="boxstep2 boxstepcomplete"><div class="boxsteptext">MEDIA</div></span>
        <a href="new-listing-bmc-page.html"><span class="boxstep3"><div class="boxsteptext">BMC</div></span></a>
        <a href="new-listing-qa-page.html"><span class="boxstep4"><div class="boxsteptext">Q &amp; A</div></span></a>
        <a href="new-listing-financials-page.html"><span class="boxstep5"><div class="boxsteptext">FINANCIALS</div></span></a>
        <a href="new-listing-submit-page.html"><span class="boxstep6"><div class="boxsteptext">SUBMIT</div></span></a>
    </div>

    <div class="boxstep last">
        <span class="boxstepn" id="boxstepn"></span>
        <span class="boxsteptitle">YOU HAVE FILLED <span id="boxsteppct">0</span>% OF YOUR COMPANY PROFILE</span>
    </div>
<!-- end banner -->

<!-- left column -->
<div class="span-16">

    <div class="boxtitle">INSTRUCTIONS</div>
    <div class="boxpanel">
    <p>
Your logo and presentation video are your first and best way of capturing investor interest.
Make sure they are professional and eye-catching.  If you do not have the budget or expertise
for a professional logo, just make something simple with text, or use a picture of yourself, 
the product, or a screenshot of your website.  Likewise if you do not have a video ready, just
record a voiceover of a quick run through your slide deck.
    </p>
    </div>

    <div class="boxtitle">LOGO</div>
    <div class="boxpanel">
        <div class="mediacol1">
            <div class="tileimg" id="logoimgwrapper">
                <img class="tileimg" id="logoimg" src=""></img>
            </div>
        </div>
        <div class="mediacol2">
            <div class="formitem">
                <span class="uploadinfo">Enter an image URL and press enter or upload a file.</span>
            </div>
            <div class="formitem clear">
                <span class="inputfield">
                    <input class="text mediainputlink" type="text" maxlength="255" name="logo_url" id="logo_url" value=""></input>
                </span>
                <span class="uploadinputicon">
                    <div id="logo_urlicon"></div>
                </span>
            </div>
            <div class="formitem">
                <span class="inputfield">
                    <form id="logouploadform" method="post" enctype="multipart/form-data" target="logouploadiframe" action="#">
                        <input class="text uploadinputbutton" id="LOGO" name="LOGO" size="18" type="file"></input>
                        <iframe id="logouploadiframe" name="logouploadiframe" src="" class="uploadiframe"></iframe>
                    </form>
                </span>
            </div>
            <div class="formitem clear">
                <span class="uploadinfo" id="logomsg">146x146 PNG, JPG, or GIF, cropped and resized</span>
            </div>
        </div>
    </div>

    <div class="boxtitle">VIDEO</div>
    <div class="boxpanel">
        <div class="formitem">
            <span class="inputfield">
                <input class="text mediainputlink videourl" type="text" maxlength="255" name="video" id="video" value=""></input>
            </span>
            <span class="videoinputicon">
                <div id="videoicon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <span class="uploadinfo" id="videomsg">
                Enter a url youtube, vimeo, or dailymotion with your presentation.
            </span>
        </div>
	    <div class="videocontainer">
      	    <iframe width="622" height="452" id="videoiframe" src="" frameborder="0" allowfullscreen></iframe> 
	    </div>
    </div>

    <div class="formitem clear">
        <span class="newlistingmsgsmall" id="newlistingmsg">&nbsp;</span>
    </div>

    <div>
        <div class="formitem clear">
            <a href="#" id="prevbuttonlink">
                <span class="span-3 inputbutton" id="prevbutton">
                    PREV
                </span>
            </a>
            <a href="#" id="nextbuttonlink">
                <span class="push-10 span-3 inputbutton" id="nextbutton">
                    NEXT
                </span>
            </a>
        </div>
    </div>

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">

    <div class="boxtitle">SUMMARY PREVIEW</div>
    <div id="summarypreview"></div>

    <div class="boxtitle clear">TIPS</div>
    <div class="sidebox">
        <p>LOGO - image displayed on the listing summary, can be your company logo or a product picture</p>
        <p>VIDEO - public presentation of the company available on youtube, can be a live action blackboard talk, a product demonstration, or a powerpoint with voiceover</p>
    </div>

    <div class="boxtitle">WITHDRAW POST</div>
    <div class="newwithdrawmsg attention" id="newwithdrawmsg">Are you sure?<br/>This cannot be undone.</div>
    <a href="#" id="newwithdrawbtn"><div class="sideboxbutton sideboxbuttonsmall">WITHDRAW</div></a>
    <a href="#" class="newwithdrawcancelbtn" id="newwithdrawcancelbtn"><div class="sideboxbutton sideboxbuttonsmall">CANCEL</div></a>

</div>
<!-- end right column -->

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
  <script src="js/libs/prevel.min.js"></script>
  <script src="js/modules/base.js"></script>
  <script src="js/modules/companylist.js"></script>
  <script src="js/modules/forms.js"></script>
  <script src="js/modules/newlistingbase.js"></script>
  <script src="js/modules/newlistingmediapage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'