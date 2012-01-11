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
newlistingbanner(`boxstepcomplete',`',`',`',`',`YOU HAVE FILLED 20% OF YOUR COMPANY PROFILE')

include(new-listing-basics-panel.m4)
include(new-listing-sidebar.m4)
newlistingsidebar(`
<p>NAME - short name for your company</p>
<p>CATEGORY - your company industry classification</p>
<p>MANTRA - tagline for your company, generally a single sentence, up to 140 characters</p>
<p>LOCATION - e.g. San Jose, CA, USA, where your business is located, or has a nexus</p>
<p>WEBSITE - your company website, if you don&rsquo;t have one then a link to your facebook page or google profile</p>
')
`
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(new-listing-footer.m4)
newlistingfooter(`boxstepcomplete',`',`',`',`')
`
</body>
</html>
'
