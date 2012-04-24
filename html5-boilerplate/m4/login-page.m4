`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="login-page">
<div id="wrap">
<div id="main">
'
include(header.m4)
`
<div class="container">

<!-- left column -->
<div class="span-16">
    <div class="boxtitle">SIGN IN WITH YOUR GOOGLE ACCOUNT</div>
    <div class="boxpanel">
        <div class="formitem">
            <span class="inputlabel"></span>
            <span class="inputhelp inputmsg">Use your google account to connect to startupbidder.</span>
        </div>
        <div class="formitem clear">
	            <span class="inputlabel">
	            </span>
                <span class="inputhelp span-9">
                    <div class="inputgooglesignin spritebg google"></div>
                    <span class="inputlink inputmsg">Link your account to startupbidder</span>
                </span>
                <a href="" class="initialhidden" id="googleloginlink">
                    <span class="span-3 inputbutton">SIGN IN</span>
                </a>
        </div>
    </div>
<!--
    <div class="boxtitle">OR LOGIN WITH YOUR STARTUPBIDDER ACCOUNT</div>
    <div class="boxpanel">
        <div class="formitem">
            <span class="inputlabel">EMAIL</span>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="loginemail" id="loginemail"></input>
            </span>
            <span class="inputicon">
                <div class="checkboxredicon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <span class="inputlabel">PASSWORD</span>
            <span class="inputfield">
                <input class="text inputwidetext" type="password" name="loginpassword" id="loginpassword"></input>
            </span>
            <span class="inputicon">
                <div class="checkboxredicon"></div>
            </span>
        </div>
        <div class="formitem clear">
	        <span class="inputlabel"></span>
            <span class="inputhelp span-9"><a href="forgot-password.html"><span class="inputlink">Forgot your password?</span></a></span>
            <a href="profile-page.html">
                <span class="span-3 inputbutton" id="loginbutton">
                    LOGIN
                </span>
            </a>
        </div>
    </div>

    <div class="boxtitle">OR SIGN UP FOR A STARTUPBIDDER ACCOUNT</div>
    <div class="boxpanel">
        <div class="formitem">
	    <span class="inputlabel"></span>
            <span class="inputhelp">
                If you don&rsquo;t have a startupbidder account, join today!
            </span>
        </div>

        <div class="formitem clear">
            <span class="inputlabel">NAME</span>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="signupname" id="signupname"></input>
            </span>
        </div>
        <div class="formitem clear">
            <span class="inputlabel">LOCATION</span>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="signuplocation" id="signuplocation"></input>
            </span>
            <span class="inputfield">Address, City<br>State/Province, Country</span>
        </div>

        <div class="formitem clear">
            <span class="inputlabel">EMAIL</span>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="signupemail" id="signupemail"></input>
            </span>
            <span class="inputicon">
                <div class="checkboxredicon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <span class="inputlabel">PASSWORD</span>
            <span class="inputfield">
                <input class="text inputwidetext" type="password" name="signuppassword" id="signuppassword"></input>
            </span>
            <span class="inputicon">
                <div class="checkboxredicon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <span class="inputlabel">CONFIRM</span>
            <span class="inputfield">
                <input class="text inputwidetext" type="password" name="signupconfirm" id="signupconfirm"></input>
            </span>
            <span class="inputicon">
                <div class="checkboxredicon"></div>
            </span>
        </div>

        <div class="formitem clear">
            <span class="inputlabel">INVESTOR</span>
            <span class="inputfield">
                <input class="checkbox inputcheckbox" type="checkbox" value="false" name="signupinvestor" id="signupinvestor"></input>
            </span>
            <span class="inputhelp">Only check if you are an <a href="terms-page.html#accredited">accredited investor</a> in your location</span>
        </div>

        <div class="formitem clear">
            <span class="inputlabel"></span>
            <span class="inputcaptcha">
  	        <script type="text/javascript" src="http://www.google.com/recaptcha/api/challenge?k=6LdaiMsSAAAAAM1nDtyXO1iXqRc15_qZ0D81_s0K"></script>
  	        <noscript>
                <iframe src="http://www.google.com/recaptcha/api/noscript?k=6LdaiMsSAAAAAM1nDtyXO1iXqRc15_qZ0D81_s0K" height="300" width="500" frameborder="0"></iframe><br>
                <textarea name="recaptcha_challenge_field" rows="3" cols="40"></textarea>
                <input type="hidden" name="recaptcha_response_field" value="manual_challenge">
            </noscript>
            </span>
	    </div>
        <div class="formitem clear">
	        <span class="inputlabel"></span>
            <span class="inputhelp span-9">
                You hereby agree to the <a href="terms-page.html">Terms and Conditions</a>.
            </span>
            <span class="span-3 inputbutton" id="joinbutton">
                JOIN
            </span>
        </div>
    </div>
-->

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">
    <div class="boxtitle">A PIECE OF THE ACTION</div>
    <div class="sidebox">
        <p>
With Startupbidder, you&rsquo;re plugged into the pulse of the startup community.
Keep up to date on all the latest startups.  Post your own startup as an
entrepreneuer, getting feedback and exposure to investors worldwide.  As an
accredited investor, you can bid for a piece of the action.
        </p>
    </div>

    <div class="boxtitle" id="listingstitle"></div>

    <!-- companydiv -->
    <div id="companydiv">
        loading...
    </div>
    <!-- end companydiv -->

</div> <!-- end right column -->

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
  <!-- JavaScript at the bottom for fast page loading -->
  <script src="js/libs/prevel.min.js"></script>
  <script src="js/modules/base.js"></script>
  <script src="js/modules/companylist.js"></script>
  <script src="js/modules/infopage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
