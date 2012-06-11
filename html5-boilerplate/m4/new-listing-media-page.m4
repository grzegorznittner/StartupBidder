`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="new-listing-media-page">
<div id="wrap">
<div id="newlistingmain">
'
include(header.m4)
`
<div class="container">

<!-- begin banner -->
    <div class="bannertextsmall">NEW LISTING WIZARD - MEDIA</div>

    <div class="span-16">
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
            <span class="boxsteptitle">YOU HAVE FILLED <span id="boxsteppct">0</span>% OF YOUR LISTING PROFILE</span>
        </div>
    </div>
    <div class="span-8 last">
        <a href="#" class="prevbuttonlink">
            <span class="push-1 span-3 smallinputbutton titleprevnextbtn">
                PREV
            </span>
        </a>
        <a href="#" class="nextbuttonlink">
            <span class="push-2 span-3 smallinputbutton titleprevnextbtn">
                NEXT
            </span>
        </a>
    </div>

<!-- end banner -->

<!-- left column -->
<div class="span-16">

    <div class="boxtitle">
        <span class="titletext">LOGO</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">Instructions</label>
            <p>
Your logo and presentation video are your first and best way of capturing investor interest.
Make sure they are professional and eye-catching.  If you do not have the budget or expertise
for a professional logo, just make something simple with text, or use a picture of yourself, 
the product, or a screenshot of your website.  Likewise if you do not have a video ready, just
record a voiceover of a quick run through your slide deck.
            </p>
        </div>
   </div>
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

    <div class="boxtitle">
        <span class="titletext">VIDEO</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">Instructions</label>
            <p>
Your logo and presentation video are your first and best way of capturing investor interest.
Make sure they are professional and eye-catching.  If you do not have the budget or expertise
for a professional logo, just make something simple with text, or use a picture of yourself, 
the product, or a screenshot of your website.  Likewise if you do not have a video ready, just
record a voiceover of a quick run through your slide deck.
            </p>
        </div>
    </div>
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
           <a href="#" class="nextbuttonlink">
                <span class="push-13 span-3 inputbutton">
                    NEXT
                </span>
            </a>
        </div>
    </div>

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">
    <div class="boxtitle clear">TIPS</div>
    <div class="sidebox">
        <p>LOGO - image displayed on the listing summary, can be your project or company logo or a product picture</p>
        <p>VIDEO - public presentation of the project available on youtube, can be a live action blackboard talk, a product demonstration, or a powerpoint with voiceover</p>
    </div>

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
