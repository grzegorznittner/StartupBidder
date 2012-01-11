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
newlistingbanner(`boxstepcomplete',`boxstepcomplete',`boxstepcomplete',`',`',`YOU HAVE FILLED 60% OF YOUR COMPANY PROFILE')

include(new-listing-financials-panel.m4)
include(new-listing-sidebar.m4)
newlistingsidebar(`
<p>ASKING - amount of money you want for investment, between $5,000 and $500,000 USD</p>
<p>FOR - what percentage of common equity you are selling in exchange for the investment, between 5% and 50%</p>
<p>AS - what kind of investment do you prefer: pure common equity, convertible debt, or preferred stock?</p>
<p>BY - end date for your bidding between 30 and 90 days in the future</p>
<p>PLAN - your business plan, a Word or PDF document that describes your business in detail</p>
<p>SLIDE DECK - your presentation to investors, a Powerpoint or PDF document, keep it short</p>
<p>FINANCIALS - your most current financial statements, if you have no operating history, then list current capital and runway</p>
')
`
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(new-listing-footer.m4)
newlistingfooter(`boxstepcomplete',`boxstepcomplete',`boxstepcomplete',`',`')
`
</body>
</html>
'
