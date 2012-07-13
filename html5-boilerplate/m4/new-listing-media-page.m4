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
<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">

<!-- begin banner -->
    <div class="bannertextsmall">NEW LISTING WIZARD - MEDIA</div>

    <div class="span-16">
        <a href="#" class="backbuttonlink">
            <span class="push-1 span-3 inputbutton backbutton">
                << BACK
            </span>
        </a>
    </div>

<!-- end banner -->

<div class="initialhidden" id="newlistingmediawrapper">

<!-- left column -->
<div class="span-16">

    <div class="boxtitle">
        <span class="titletext">LOGO</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">Instructions</label>
            <p>
Your logo and product images are your first and best way of capturing investor interest.
Make sure they are professional and eye-catching.  If you do not have the budget or expertise
for a professional logo, just make something simple with text.  For images, upload product
images, screenshots, store photographs, anything visual regarding your product or service.
You may also optionally link to a video, such as a voiceover of a quick run through your slide deck.
            </p>
        </div>
   </div>
   <div class="boxpanel newlistingpanel">
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
                        <input class="text uploadinputbutton" id="logouploadfile" name="LOGO" size="32" type="file"></input>
                        <iframe id="logouploadiframe" name="logouploadiframe" src="" class="uploadiframe"></iframe>
                    </form>
                </span>
            </div>
            <div class="formitem clear">
                <span class="uploadinfo" id="logomsg">146x146 PNG, JPG, or GIF, cropped and resized</span>
            </div>
        </div>
    </div>
'
include(images-panel-editable.m4)
`
    <div class="boxtitle">
        <span class="titletext">VIDEO - OPTIONAL</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">Instructions</label>
            <p>
Link to a youtube, vimeo, or dailymotion video presenting your product, service or company.
If you do not have a video ready, just record a voiceover of a quick run through your slide deck.
            </p>
        </div>
    </div>
    <div class="boxpanel newlistingpanel">
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
      	    <iframe width="622" height="350" id="videoiframe" src="" frameborder="0" allowfullscreen></iframe> 
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

    <div class="boxtitle picuploadtitle clear">UPLOAD IMAGE <span id="picnum">1</span></div>
    <div class="sidebox">
        <div class="formitem">
            <span class="uploadinfo">Enter an image URL and press enter or upload a file.</span>
        </div>
        <div class="formitem clear">
            <span class="inputfield">
                <input class="text picinputlink" type="text" maxlength="255" name="pic_url" id="pic_url" value=""></input>
            </span>
            <span class="uploadinputicon">
                <div id="pic_urlicon"></div>
            </span>
        </div>
        <div class="formitem">
            <span class="inputfield">
                <form id="picuploadform" method="post" enctype="multipart/form-data" target="picuploadiframe" action="#">
                    <input class="text picinputlink" id="picuploadfile" name="PIC1" size="16" type="file"></input>
                    <iframe id="picuploadiframe" name="picuploadiframe" src="" class="uploadiframe"></iframe>
                </form>
            </span>
        </div>
        <div class="formitem clear">
            <span class="uploadinfo" id="picmsg">622px by 452px max JPG, PNG or GIF</span>
        </div>
    </div>

</div>
<!-- end right column -->

</div>

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
  <script src="js/modules/imagepanel.js"></script>
  <script src="js/modules/newlistingbase.js"></script>
  <script src="js/modules/newlistingmediapage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
