`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="company-page">
<div id="fb-root"></div> <!-- for fb like button -->
<div id="wrap">
<div id="main">
'
include(header.m4)
include(company-banner.m4)
companybannermacro(`companynavselected', `', `', `', `', `')
`
<div class="container">
'
include(company-basics.m4)
`
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
  <!-- JavaScript at the bottom for fast page loading -->
  <script src="js/libs/prevel.min.js"></script>
  <script src="js/modules/base.js"></script>
  <script src="js/modules/companybanner.js"></script>
  <script src="js/modules/listing.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie-nosocial.m4)
`
</body>
</html>
'
