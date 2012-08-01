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

    <div class="boxtitle basicstitle">
        <span class="titletext">VIDEO</span>
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
        <div class="formitem sideinfoitem clear">
            <span class="inputfield">
                <input class="text mediainputlink videourl" style="width: 568px !important" type="text" maxlength="255" name="video" id="video" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Video</label>
                <br />
Link to a youtube, vimeo, or dailymotion video presenting your product, service or company.
If you do not have a video ready, just record a voiceover of a quick run through your slide deck.
            </p>
            <span class="videoinputicon">
                <div id="videoicon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <span class="uploadinfo" id="videomsg">
                Enter a link to your youtube, vimeo or dailymotion video
            </span>
        </div>
	    <div class="videocontainer">
      	    <iframe width="622" height="350" id="videoiframe" src="" frameborder="0" allowfullscreen></iframe> 
	    </div>
    </div>

    <div class="boxpanel newlistingpanel">
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="website">WEBSITE</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="website" id="website" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Link</label>
                <br />
                A link to the website for your application or company, or if you don't have one, then a link to your facebook page, twitter page, google profile, or blog.
            </p>
            <span class="inputicon">
                <div id="websiteicon"></div>
            </span>
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
  <script src="js/modules/newlistingbase.js"></script>
  <script src="js/modules/newlistingmediapage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
