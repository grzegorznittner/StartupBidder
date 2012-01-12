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
newlistingbanner(`boxstepcomplete',`boxstepcomplete',`boxstepcomplete',`boxstepcomplete',`boxstepcomplete',`YOU HAVE FILLED 100% OF YOUR COMPANY PROFILE')

include(new-listing-submit-panel.m4)
include(new-listing-sidebar.m4)
newlistingsidebarnotips()
`
<!-- preview overlay text -->
<div class="previewtext">PREVIEW</div>
<div class="previewtext previewtext2">PREVIEW</div>
<div class="previewtext previewtext3">PREVIEW</div>
<div class="previewtext previewtext4">PREVIEW</div>

<!-- listing preview -->
<div class="container">
'
include(listing-panel.m4)
include(listing-sidebar.m4)
`
</div>
<!-- end preview -->


</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(new-listing-footer.m4)
newlistingfooter(`boxstepcomplete',`boxstepcomplete',`boxstepcomplete',`boxstepcomplete',`boxstepcomplete')
`
</body>
</html>
'
