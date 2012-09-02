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
companybannermacro(`', `', `', `', `', `', `companynavselected')
`
<div class="container preloader">
     <div class="preloaderfloater"></div>
     <div class="preloadericon"></div>
</div>

<div class="container initialhidden wrapper">

    <div class="span-24 initialhidden" id="qandaswrapper">
        <div class="boxtitle smokegrey clear">QUESTIONS AND ANSWERS WITH THE OWNER</div>
        <div class="boxpanel boxpanelfull" id="qandalistparent">

            <div class="commentline addcommentline initialhidden" id="addqandabox">
                <textarea class="textarea commenttextarea"
                    id="addqandatext" name="addqandatext" cols="20" rows="5">Put your question to the owner here...</textarea>
                <div class="addcommentspinner preloadericon initialhidden" id="addqandaspinner"></div>
                <span class="span-3 inputbutton messagebutton commentreplybutton" id="addqandabtn">SEND</span>
                <p class="commenttext" id="qandamsg"></p>
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
<script src="js/modules/questions.js"></script>
<script>
(new QuestionClass()).load();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
