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
newlistingbanner(`boxstepcomplete',`boxstepcomplete',`',`',`',`YOU HAVE FILLED 40% OF YOUR COMPANY PROFILE')

include(new-listing-qa-panel.m4)
include(new-listing-sidebar.m4)
newlistingsidebar(`
<p>
CUSTOMER - who is the customer?  It is not everybody, it is not all
mobile phone developers.  It is Bob.  He is 28 years old and works in the
development group of a large telecommunications company.  He has an iPhone,
an iMac, and a MacBook.  He plays in a band on the side and self-publishes
his own music on the side.  From time to time he visits his family in Bilbao.
Make your customer specific and bring them to life.
</p>
')
`
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(new-listing-footer.m4)
newlistingfooter(`boxstepcomplete',`boxstepcomplete',`',`',`')
`
</body>
</html>
'
