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

<!-- left column -->
<div class="span-24">

    <div id="messagesmsg"></div>

    <div class="boxtitle smokegrey clear">YOUR CONVERSATION WITH <span id="from_user_nickname_upper"></span></div>
    <div class="boxpanel boxpanelfull">
        <div class="messageline messagereplyline" id="messagesreply">
            <p class="messageuser messagereplyuser span-4" id="myusername"></p>
            <textarea class="textarea messagetextarea messagereplytextarea" id="addcommenttext" name="addcommenttext" cols="20" rows="5">Put your reply here...</textarea>
            <span class="span-3 inputbutton messagebutton messagereplybutton" id="addcommentbtn">SEND</span>
        </div>
    </div>

</div> <!-- end left column -->
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
  <script src="js/libs/prevel.min.js"></script>
  <script src="js/modules/base.js"></script>
  <script src="js/modules/companylist.js"></script>
  <script src="js/modules/message.js"></script>
  <script src="js/modules/messagepage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
