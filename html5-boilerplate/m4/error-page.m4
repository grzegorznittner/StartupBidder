`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="error-page">
<div id="wrap">
<div id="main">
'
include(header.m4)
`
<div class="container">

<div class="span-16">
    <div class="boxtitle">NOT FOUND</div>
    <div class="boxpanel">
	       <p>Sorry, but the page you were trying to view does not exist.</p>
           <p>Return <a href="/">home</a>.</p>
    </div>
</div>

<div class="span-8 last">
    <div class="boxtitle" id="listingstitle"></div>
    <div id="companydiv"></div>
</div>

</div>
</div>
</div>
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script>
(new InformationPageClass()).loadPage();
</script>
'
include(promptie.m4)
`	
</body>
</html>
'
