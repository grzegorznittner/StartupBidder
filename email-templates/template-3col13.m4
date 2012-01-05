define([[layout3col13]], [[<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

        <!-- Facebook sharing information tags -->
        <meta property="og:title" content="$1" />

        <title>$1</title>
		<style type="text/css">
/* Client-specific Styles */
#outlook a{padding:0;} /* Force Outlook to provide a "view in browser" button. */
body{width:100% !important;} .ReadMsgBody{width:100%;} .ExternalClass{width:100%;} /* Force Hotmail to display emails at full width */
body{-webkit-text-size-adjust:none;} /* Prevent Webkit platforms from changing default text sizes. */

/* Reset Styles */
body{margin:0; padding:0;}
img{border:0; height:auto; line-height:100%; outline:none; text-decoration:none;}
table td{border-collapse:collapse;}
#backgroundTable{height:100% !important; margin:0; padding:0; width:100% !important;}

/* Template Styles */

/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: COMMON PAGE ELEMENTS /\/\/\/\/\/\/\/\/\/\ */

/**
* @tab Page
* @section background color
* @tip Set the background color for your email. You may want to choose one that matches your company's branding.
* @theme page
*/
body, #backgroundTable{
        /*@editable*/ background-color:#F2F2F2;
}

/**
* @tab Page
* @section email border
* @tip Set the border for your email.
*/
#templateContainer{
	/*@editable*/ border: 1px solid #DDDDDD;
}

/**
* @tab Page
* @section heading 1
* @tip Set the styling for all first-level headings in your emails. These should be the largest of your headings.
* @style heading 1
*/
h1, .h1{
	/*@editable*/ color:#49515a;
	display:block;
	/*@editable*/ font-family:"Helvetica Neue",Arial,Helvetica,sans-serif;
	/*@editable*/ font-size:14px;
	/*@editable*/ font-weight:bold;
	/*@editable*/ line-height:100%;
	margin-top:0;
	margin-right:0;
	margin-bottom:10px;
	margin-left:0;
	/*@editable*/ text-align:center;
	/*GREGN @editable background:#49515a;*/
}

/**
* @tab Page
* @section heading 2
* @tip Set the styling for all second-level headings in your emails.
* @style heading 2
*/
h2, .h2{
        /*@editable*/ color:#333333;
	display:block;
        /*@editable*/ font-family:"Helvetica Neue",Arial,Helvetica,sans-serif;
        /*@editable*/ font-size:24px;
        /*@editable*/ font-weight:normal;
	/*@editable*/ line-height:100%;
	margin-top:0;
	margin-right:0;
	margin-bottom:10px;
	margin-left:0;
	/*@editable*/ text-align:left;
	/*@editable*/ text-decoration:none;
}

h2 a:link, h2 a:visited, .h2 a:link, .h2 a:visited{
	/*@editable*/ text-decoration:none;
}

/**
* @tab Page
* @section heading 3
* @tip Set the styling for all third-level headings in your emails.
* @style heading 3
*/
h3, .h3{
	/*@editable*/ color:#202020;
	display:block;
        /*@editable*/ font-family:"Helvetica Neue",Arial,Helvetica,sans-serif;
        /*@editable*/ font-size:14px;
        /*@editable*/ font-weight:normal;
	/*@editable*/ line-height:100%;
	margin-top:0;
	margin-right:0;
	margin-bottom:10px;
	margin-left:0;
	/*@editable*/ text-align:left;
}

/**
* @tab Page
* @section heading 4
* @tip Set the styling for all fourth-level headings in your emails. These should be the smallest of your headings.
* @style heading 4
*/
h4, .h4{
        /*@editable*/ color:#4083A9;
	display:block;
        /*@editable*/ font-family:"Helvetica Neue",Arial,Helvetica,sans-serif;
        /*@editable*/ font-size:10px;
        /*@editable*/ font-weight:normal;
	/*@editable*/ line-height:100%;
	margin-top:0;
	margin-right:0;
	margin-bottom:10px;
	margin-left:0;
	/*@editable*/ text-align:left;
}

/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: PREHEADER /\/\/\/\/\/\/\/\/\/\ */

/**
* @tab Header
* @section preheader style
* @tip Set the background color for your email's preheader area.
* @theme page
*/
#templatePreheader{
        /*@editable*/ background-color:#EFFFFF;
}

/**
* @tab Header
* @section preheader text
* @tip Set the styling for your email's preheader text. Choose a size and color that is easy to read.
*/
.preheaderContent div{
        /*@editable*/ color:#4083A9;
        /*@editable*/ font-family:"Helvetica Neue",Arial,Helvetica,sans-serif;
	/*@editable*/ font-size:10px;
	/*@editable*/ line-height:100%;
	/*@editable*/ text-align:left;
}

/**
* @tab Header
* @section preheader link
* @tip Set the styling for your email's preheader links. Choose a color that helps them stand out from your text.
*/
.preheaderContent div a:link, .preheaderContent div a:visited, /* Yahoo! Mail Override */ .preheaderContent div a .yshortcuts /* Yahoo! Mail Override */{
        /*@editable*/ color:#4083A9;
	/*@editable*/ font-weight:normal;
	/*@editable*/ text-decoration:underline;
}



/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: HEADER /\/\/\/\/\/\/\/\/\/\ */

/**
* @tab Header
* @section header style
* @tip Set the background color and border for your email's header area.
* @theme header
*/
#templateHeader{
	/*@editable*/ background-color:#FFFFFF;
	/*@editable*/ border-bottom:0;
}

/**
* @tab Header
* @section header text
* @tip Set the styling for your email's header text. Choose a size and color that is easy to read.
*/
.headerContent{
	/*@editable*/ color:#202020;
	/*@editable*/ font-family:"Helvetica Neue",Arial,Helvetica,sans-serif;
	/*@editable*/ font-size:34px;
	/*@editable*/ font-weight:bold;
	/*@editable*/ line-height:100%;
	/*@editable*/ padding:0;
	/*@editable*/ text-align:center;
	/*@editable*/ vertical-align:middle;
}

/**
* @tab Header
* @section header link
* @tip Set the styling for your email's header links. Choose a color that helps them stand out from your text.
*/
.headerContent a:link, .headerContent a:visited, /* Yahoo! Mail Override */ .headerContent a .yshortcuts /* Yahoo! Mail Override */{
	/*@editable*/ color:#336699;
	/*@editable*/ font-weight:normal;
	/*@editable*/ text-decoration:underline;
}

#headerImage{
	height:auto;
	max-width:600px !important;
}

/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: MAIN BODY /\/\/\/\/\/\/\/\/\/\ */

/**
* @tab Body
* @section body style
* @tip Set the background color for your email's body area.
*/
#templateContainer, .bodyContent{
	/*@editable*/ background-color:#FFFFFF;
}

/**
* @tab Body
* @section body text
* @tip Set the styling for your email's main content text. Choose a size and color that is easy to read.
* @theme main
*/
.bodyContent div{
	/*@editable*/ color:#505050;
	/*@editable*/ font-family:"Helvetica Neue",Arial,Helvetica,sans-serif;
	/*@editable*/ font-size:14px;
	/*@editable*/ line-height:150%;
	/*@editable*/ text-align:left;
}

/**
* @tab Body
* @section body link
* @tip Set the styling for your email's main content links. Choose a color that helps them stand out from your text.
*/
.bodyContent div a:link, .bodyContent div a:visited, /* Yahoo! Mail Override */ .bodyContent div a .yshortcuts /* Yahoo! Mail Override */{
	/*@editable*/ color:#505050;
	/*@editable*/ font-weight:normal;
	/*@editable*/ text-decoration:underline;
}

.bodyContent img{
	display:inline;
	height:auto;
}

/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: COLUMNS; LEFT, CENTER, RIGHT /\/\/\/\/\/\/\/\/\/\ */

/**
* @tab Columns
* @section left column text
* @tip Set the styling for your email's left column text. Choose a size and color that is easy to read.
*/
.leftColumnContent{
	/*@editable*/ background-color:#E3E3E3;
}

/**
* @tab Columns
* @section left column text
* @tip Set the styling for your email's left column text. Choose a size and color that is easy to read.
*/
.leftColumnContent div{
	/*@editable*/ color:#505050;
	/*@editable*/ font-family:"Helvetica Neue",Arial,Helvetica,sans-serif;
	/*@editable*/ font-size:14px;
	/*@editable*/ line-height:150%;
	/*@editable*/ text-align:left;
}

/**
* @tab Columns
* @section left column link
* @tip Set the styling for your email's left column links. Choose a color that helps them stand out from your text.
*/
.leftColumnContent div a:link, .leftColumnContent div a:visited, /* Yahoo! Mail Override */ .leftColumnContent div a .yshortcuts /* Yahoo! Mail Override */{
	/*@editable*/ color:#505050;
	/*@editable*/ font-weight:normal;
	/*@editable*/ text-decoration:none;
}

.leftColumnContent img{
	display:inline;
	height:auto;
}

/**
* @tab Columns
* @section center column text
* @tip Set the styling for your email's center column text. Choose a size and color that is easy to read.
*/
.centerColumnContent{
	/*@editable*/ background-color:#E3E3E3;
}

/**
* @tab Columns
* @section center column text
* @tip Set the styling for your email's center column text. Choose a size and color that is easy to read.
*/
.centerColumnContent div{
	/*@editable*/ color:#505050;
	/*@editable*/ font-family:"Helvetica Neue",Arial,Helvetica,sans-serif;
	/*@editable*/ font-size:14px;
	/*@editable*/ line-height:150%;
	/*@editable*/ text-align:left;
}

/**
* @tab Columns
* @section center column link
* @tip Set the styling for your email's center column links. Choose a color that helps them stand out from your text.
*/
.centerColumnContent div a:link, .centerColumnContent div a:visited, /* Yahoo! Mail Override */ .centerColumnContent div a .yshortcuts /* Yahoo! Mail Override */{
	/*@editable*/ color:#505050;
	/*@editable*/ font-weight:normal;
	/*@editable*/ text-decoration:none;
}

.centerColumnContent img{
	display:inline;
	height:auto;
}

/**
* @tab Columns
* @section right column text
* @tip Set the styling for your email's right column text. Choose a size and color that is easy to read.
*/
.rightColumnContent{
	/*@editable*/ background-color:#E3E3E3;
}

/**
* @tab Columns
* @section right column text
* @tip Set the styling for your email's right column text. Choose a size and color that is easy to read.
*/
.rightColumnContent div{
	/*@editable*/ color:#505050;
	/*@editable*/ font-family:"Helvetica Neue",Arial,Helvetica,sans-serif;
	/*@editable*/ font-size:14px;
	/*@editable*/ line-height:150%;
	/*@editable*/ text-align:left;
}

/**
* @tab Columns
* @section right column link
* @tip Set the styling for your email's right column links. Choose a color that helps them stand out from your text.
*/
.rightColumnContent div a:link, .rightColumnContent div a:visited, /* Yahoo! Mail Override */ .rightColumnContent div a .yshortcuts /* Yahoo! Mail Override */{
	/*@editable*/ color:#505050;
	/*@editable*/ font-weight:normal;
	/*@editable*/ text-decoration:none;
}

.rightColumnContent img{
	display:inline;
	height:auto;
}

/* /\/\/\/\/\/\/\/\/\/\ STANDARD STYLING: FOOTER /\/\/\/\/\/\/\/\/\/\ */

/**
* @tab Footer
* @section footer style
* @tip Set the background color and top border for your email's footer area.
* @theme footer
*/
#templateFooter{
	/*@editable*/ background-color:#FFFFFF;
	/*@editable*/ border-top:0;
}

/**
* @tab Footer
* @section footer text
* @tip Set the styling for your email's footer text. Choose a size and color that is easy to read.
* @theme footer
*/
.footerContent div{
	/*@editable*/ color:#707070;
	/*@editable*/ font-family:"Helvetica Neue",Arial,Helvetica,sans-serif;
	/*@editable*/ font-size:12px;
	/*@editable*/ line-height:125%;
	/*@editable*/ text-align:left;
}

/**
* @tab Footer
* @section footer link
* @tip Set the styling for your email's footer links. Choose a color that helps them stand out from your text.
*/
.footerContent div a:link, .footerContent div a:visited, /* Yahoo! Mail Override */ .footerContent div a .yshortcuts /* Yahoo! Mail Override */{
	/*@editable*/ color:#336699;
	/*@editable*/ font-weight:normal;
	/*@editable*/ text-decoration:underline;
}

.footerContent img{
	display:inline;
}

/**
* @tab Footer
* @section social bar style
* @tip Set the background color and border for your email's footer social bar.
* @theme footer
*/
#social{
	/*@editable*/ background-color:#FAFAFA;
	/*@editable*/ border:0;
}

/**
* @tab Footer
* @section social bar style
* @tip Set the background color and border for your email's footer social bar.
*/
#social div{
	/*@editable*/ text-align:center;
}

/**
* @tab Footer
* @section utility bar style
* @tip Set the background color and border for your email's footer utility bar.
* @theme footer
*/
#utility{
	/*@editable*/ background-color:#FFFFFF;
	/*@editable*/ border:0;
}

/**
* @tab Footer
* @section utility bar style
* @tip Set the background color and border for your email's footer utility bar.
*/
#utility div{
	/*@editable*/ text-align:center;
}

#monkeyRewards img{
	max-width:190px;
}
		</style>
	</head>
    <body leftmargin="0" marginwidth="0" topmargin="0" marginheight="0" offset="0">
    	<center>
        	<table border="0" cellpadding="0" cellspacing="0" height="100%" width="100%" id="backgroundTable">
            	<tr>
                	<td align="center" valign="top">
                        <!-- // Begin Template Preheader \\ -->
                        <table border="0" cellpadding="10" cellspacing="0" width="600" id="templatePreheader">
                            <tr>
                                <td valign="top" class="preheaderContent">

                                	<!-- // Begin Module: Standard Preheader \ -->
                                    <table border="0" cellpadding="10" cellspacing="0" width="100%">
                                    	<tr>
                                        	<td valign="top">
                                            	<div>
                                                         $2
                                                </div>
                                            </td>
                                            <!-- *|IFNOT:ARCHIVE_PAGE|* -->
											<td valign="top" width="190">
                                            	<div>
                                                	Is this email not displaying correctly?<br /><a href="$3" target="_blank">View it in your browser</a>.
                                                </div>
                                            </td>
											<!-- *|END:IF|* -->
                                        </tr>
                                    </table>
                                	<!-- // End Module: Standard Preheader \ -->

                                </td>
                            </tr>
                        </table>
                        <!-- // End Template Preheader \\ -->
                    	<table border="0" cellpadding="0" cellspacing="0" width="600" id="templateContainer">
                        	<tr>
                            	<td align="center" valign="top">
                                    <!-- // Begin Template Header \\ -->
                                	<table border="0" cellpadding="0" cellspacing="0" width="600" id="templateHeader">
                                        <tr>
                                            <td class="headerContent">

                                            	<!-- // Begin Module: Standard Header Image \\ -->
                                                <img src="$4" style="max-width:600px;" id="headerImage campaign-icon" alt="$1"/>
                                            	<!-- // End Module: Standard Header Image \\ -->

                                            </td>
                                        </tr>
                                        </table>
                                    <!-- // End Template Header \\ -->
                                </td>
                        	<tr>
                            	<td align="center" valign="top">
                                    <!-- // Begin Template Body \\ -->
                                	<table border="0" cellpadding="0" cellspacing="0" width="600" id="templateBody">
                                    	<tr>
                                        	<td colspan="3" valign="top" class="bodyContent">

                                                <!-- // Begin Module: Standard Content \\ -->
                                                <table border="0" cellpadding="20" cellspacing="0" width="100%">
                                                    <tr>
                                                        <td valign="top">
                                                            <div>
                                                            	<h1 class="h1">$5</h1>
                                                            	<br/>
                                                                $6
                                                            </div>
							</td>
                                                    </tr>
                                                </table>
                                                <!-- // End Module: Standard Content \\ -->

                                            </td>
                                        </tr>
                                        <!-- BEGIN GREGN -->
                                    	<tr>
                                        	<td colspan="3" valign="top" class="bodyContent">
                                    		<!-- // Begin Template Header \\ -->
                                		<table border="0" cellpadding="0" cellspacing="0" width="600" id="templateHeader">
                                        	<tr>
                                            		<td class="headerContent">
                                            		<!-- // Begin Module: Standard Header Image \\ -->
                                                	<img src="$8" style="max-width:600px;" id="headerImage campaign-icon" alt="Featured companies"/>
                                            		<!-- // End Module: Standard Header Image \\ -->
                                            		</td>
                                        	</tr>
                                    		</table>
                                            </td>
                                        </tr>
                                        <!-- END GREGN -->
                                    	<tr>
                                        	<td valign="top" width="180" class="leftColumnContent">

                                                <!-- // Begin Module: Top Image with Content \\ -->
                                                <table border="0" cellpadding="20" cellspacing="0" width="100%">
                                                    <tr>
                                                        <td valign="top">
                                                            <a href="http://startupbidder.com/l/345345345.html"><img src="$9" style="max-width:160px;"/></a>
                                                            <div>
 	                                                       <h4 class="h4">SOFTWARE <br>Waltham, MA, USA</h4>
                                                               <a href="http://startupbidder.com/l/345345345.html"><strong>Semantic Search</strong></a><br>Semantic search seeks to improve search accuracy by understanding searcher intent and the contextual.
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </table>
                                                <!-- // End Module: Top Image with Content \\ -->

                                            </td>
                                        	<td valign="top" width="180" class="centerColumnContent">

                                                <!-- // Begin Module: Top Image with Content \\ -->
                                                <table border="0" cellpadding="20" cellspacing="0" width="100%">
                                                    <tr>
                                                        <td valign="top">
                                                            <a href="http://startupbidder.com/l/345345345.html"><img src="$10" style="max-width:160px;"/></a>
                                                            <div>
 	                                                       <h4 class="h4">INTERNET <br>London, UK</h4>
                                                               <a href="http://startupbidder.com/l/345345345.html"><strong>Social Recommendations</strong></a><br>Identifying with your peer group through joint purchases.
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </table>
                                                <!-- // End Module: Top Image with Content \\ -->

                                            </td>
                                        	<td valign="top" width="180" class="rightColumnContent">

                                                <!-- // Begin Module: Top Image with Content \\ -->
                                                <table border="0" cellpadding="20" cellspacing="0" width="100%">
                                                    <tr>
                                                        <td valign="top">
                                                            <a href="http://startupbidder.com/l/345345345.html"><img src="$11" style="max-width:160px;"/></a>
                                                            <div>
 	                                                       <h4 class="h4">INTERNET <br>Dusseldorf, Germany</h4>
                                                               <a href="http://startupbidder.com/l/345345345.html"><strong>gKleen</strong></a><br>High volume google mail (gmail) management the easy way.
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </table>
                                                <!-- // End Module: Top Image with Content \\ -->

                                            </td>
                                        </tr>
                                    </table>
                                    <!-- // End Template Body \\ -->
                                </td>
                            </tr>
                        	<tr>
                            	<td align="center" valign="top">
                                    <!-- // Begin Template Footer \\ -->
                                	<table border="0" cellpadding="10" cellspacing="0" width="600" id="templateFooter">
                                    	<tr>
                                        	<td valign="top" class="footerContent">

                                                <!-- // Begin Module: Standard Footer \\ -->
                                                <table border="0" cellpadding="10" cellspacing="0" width="100%">
                                                    <tr>
                                                        <td colspan="2" valign="middle" id="social">
                                                            <div>
                                                                &nbsp;<a href="*|TWITTER:PROFILEURL|*">follow on Twitter</a> | <a href="*|FACEBOOK:PROFILEURL|*">friend on Facebook</a> | <a href="*|FORWARD|*">forward to a friend</a>&nbsp;
                                                            </div>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td valign="top" width="350">
                                                            <div>
								<em>Copyright &copy; 2011 Startupbidder, All rights reserved.</em>
								<br />

								<br />
								<strong>Our mailing address is:</strong>
								<br />
								$7
                                                            </div>
                                                        </td>
                                                        <td valign="top" width="190" id="monkeyRewards">
                                                            <div>

                                                            </div>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td colspan="2" valign="middle" id="utility">
                                                            <div>
                                                                &nbsp;<a href="*|UPDATE_PROFILE|*">update email notification preferences</a>&nbsp;
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </table>
                                                <!-- // End Module: Standard Footer \\ -->

                                            </td>
                                        </tr>
                                    </table>
                                    <!-- // End Template Footer \\ -->
                                </td>
                            </tr>
                        </table>
                        <br />
                    </td>
                </tr>
            </table>
        </center>
    </body>
</html>
]])
