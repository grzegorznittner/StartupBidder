`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="help-page">
<div id="wrap">
<div id="main">
'
include(header.m4)
`
<div class="container">

<!-- left column -->
<div class="span-16">
    <div class="boxtitle">NOT FOUND</div>
    <div class="boxpanel">
	       <p>Sorry, but the page you were trying to view does not exist.</p>
	       <p>It looks like this was the result of either:</p>
	       <ul>
		   <li>a mistyped address</li>
		   <li>an out-of-date link</li>
	       </ul>
           <p>Return <a href="/">home</p>.</p>
    </div>
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
    </div>
    <!-- end companydiv -->

</div> <!-- end right column -->

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
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
