`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="company-page">
<div id="fb-root"></div> <!-- for fb like button -->
<div id="wrap">
<div id="main">
'
include(header.m4)
include(company-not-found.m4)
include(company-banner.m4)
companybannermacro(`', `', `', `', `companynavselected', `')
`
<div class="container preloader">
     <div class="preloaderfloater"></div>
     <div class="preloadericon"></div>
</div>

<div class="container initialhidden wrapper">

    <div class="span-24 initialhidden" id="commentswrapper">
        <div class="boxtitle smokegrey clear">PUBLIC COMMENTS</div>
        <div class="boxpanel boxpanelfull" id="commentlistparent">

            <div class="commentline addcommentline initialhidden" id="addcommentbox">
                <div class="commentavatar" id="addcommentavatar"></div>
                <div class="commentheaderline">
                    <span class="commentusername" id="myusername"></span>
                </div>
                <textarea class="textarea commenttextarea"
                    id="addcommenttext" name="addcommenttext" cols="20" rows="5">Put your comment here...</textarea>
                <div class="addcommentspinner preloadericon initialhidden" id="addcommentspinner"></div>
                <span class="span-3 inputbutton messagebutton commentreplybutton" id="addcommentbtn">SEND</span>
                <p class="commenttext" id="commentmsg"></p>
            </div>

        </div>
    </div>

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/comments.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
