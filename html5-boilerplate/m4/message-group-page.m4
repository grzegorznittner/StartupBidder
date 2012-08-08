`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="message-group-page">
<div id="wrap">
<div id="main">
'
include(header.m4)
`
<div class="container">

    <div class="span-24 preloader preloadershort">
        <div class="preloaderfloater"></div>
        <div class="preloadericon"></div>
    </div>

<!-- left column -->
<div class="span-24 initialhidden wrapper">

    <div id="messagesmsg"></div>

    <div class="boxtitle smokegrey clear">YOUR CONVERSATIONS</div>
    <div class="boxpanel boxpanelfull" id="messagegrouplist"></div>

</div> <!-- end left column -->
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/messages.js"></script>
<script>
(new MessageGroupPageClass()).loadPage();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
