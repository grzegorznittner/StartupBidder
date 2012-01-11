`<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
'
include(head.m4)
`
<body>
<div id="wrap">
<div id="newlistingmain">
'
include(header-loggedin.m4)
`
<div class="container">
'
include(new-listing-banner.m4)
newlistingbanner(`boxstepcomplete',`boxstepcomplete',`boxstepcomplete',`boxstepcomplete',`',`YOU HAVE FILLED 80% OF YOUR COMPANY PROFILE')

include(new-listing-media-panel.m4)
include(new-listing-sidebar.m4)
newlistingsidebar(`
<p>LOGO - image displayed on the listing summary, can be your company logo or a product picture</p>
<p>VIDEO - public presentation of the company available on youtube, can be a live action blackboard talk, a product demonstration, or a powerpoint with voiceover</p>
')
`
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(new-listing-footer.m4)
newlistingfooter(`boxstepcomplete',`boxstepcomplete',`boxstepcomplete',`boxstepcomplete',`')
`
</body>
</html>
'
