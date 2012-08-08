`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="messages-page">
<div id="wrap">
<div id="main">
'
include(header.m4)
`
<div class="container">

<div class="span-24 preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>

<div class="span-24 initialhidden wrapper">

    <div class="boxtitle smokegrey clear">YOUR CONVERSATION WITH <span id="from_user_nickname_upper"></span></div>
    <div class="boxpanel boxpanelfull">
        <div class="messageline messagereplyline initialhidden" id="messagesend">
            <p class="messageuser messagereplyuser span-4" id="myusername"></p>
            <textarea class="textarea messagetextarea messagereplytextarea" id="messagetext" name="messagetext" cols="20" rows="5">Put your reply here...</textarea>
            <span class="span-3 inputbutton messagebutton messagereplybutton" id="messagebtn">SEND</span>
            <p class="messagereplymsg inputmsg successful" id="messagemsg"></p>
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
<script src="js/modules/messages.js"></script>
<script>
(new MessagePageClass()).loadPage();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
