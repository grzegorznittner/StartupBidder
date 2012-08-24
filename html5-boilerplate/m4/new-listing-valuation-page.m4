`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="new-listing-valuation-page">
<div id="wrap">
<div id="newlistingmain">
'
include(header.m4)
include(company-not-found.m4)
`
<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">

<!-- begin company valuation -->
<div class="initialhidden" id="newlistingcompanywrapper">

    <div class="bannertextsmall">HOW MUCH IS YOUR COMPANY WORTH?</div>

    <div class="span-24">
        <a href="#" class="backbuttonlink">
            <span class="push-1 span-3 inputbutton backbutton">
                &lt;&lt; BACK
            </span>
        </a>
    
        <div class="boxtitle offertitle">
            <span class="titletext">VALUATION</span>
            <div class="titleinfobtn"></div>
            <div class="titleinfo">
                <label class="titleinfoheader">Valuation</label>
                <p>
                 Choosing a value for your company can be difficult, but we will give you some help.
                 From the data you enter, we derive a likely price for your company.  As companies are difficult to value,
                especially in the startup stage, this valuation is only meant to be a rough guide to help you in the funding process.
                </p>
            </div>
        </div>
    
        <div class="boxpanelfull valuationpanel">
            <div class="formitem clear firstinputitem">
                <p class="formhelptext">
                    Choosing a value for your company  can be difficult, but we will give you some help.  Just fill in the fields below
                    and we will calculate the valuation on the fly.
                </p>
            </div>
    
            <div class="formitem sideinfoitem clear">
                <span class="inputlabel revenuelabel">MARKET SIZE</span>
                <span class="inputfield">
                    <input class="text askinginputtext valuationinput" type="text" name="market_size" id="market_size" maxlength="20"></input>
                </span>
                <p class="sideinfo">
                    <label class="sideinfoheader">Market Size</label>
                    <br/>
                    The total number of customers or monetizable actions in your market per year.  For instance for internet search, there are around 150 billion searches a year.
                    For social media, there are around 1.3 billion global users.  This is your "moonshot" market you are hoping to gain, think big.
                </p>
                <span class="inputicon">
                    <div id="market_sizeicon"></div>
                </span>
            </div>
    
            <div class="formitem sideinfoitem clear">
                <span class="inputlabel revenuelabel">PENETRATION<br/>RATE</span>
                <span class="inputfield">
                    <input class="text askinginputtext valuationinput" type="text" name="penetration_rate" id="penetration_rate" maxlength="4"></input>%
                </span>
                <p class="sideinfo">
                    <label class="sideinfoheader">Market Penetration Rate</label>
                    <br/>
                    Market Penetration Rate.  The percent of the market you hope to grab in a best-case scenario.  May be high if you are going
                    for complete market domination.  For instance, for Google it&rsquo;s around 80% of the global market.  For facebook it&rsquo;s 60% of 
                    global social media visits.  Around 20% may be a typical number for good measure.
                </p>
                <span class="inputicon">
                    <div id="penetration_rateicon"></div>
                </span>
           </div>
    
           <div class="formitem sideinfoitem clear">
                <span class="inputlabel revenuelabel">REVENUE PER</span>
                <span class="inputfield">
                    <input class="text askinginputtext valuationinput" type="text" name="revenue_per" id="revenue_per" maxlength="20"></input>
                </span>
                <p class="sideinfo">
                    <label class="sideinfoheader">Revenue Per</label>
                    <br/>
                    The revenue per customer or monetizable item in your target market.  This could be revenue per search in the case of google, which is about $0.20.  It could be annual revenue
                    per social media user, which for facebook is $1.20, or annual revenue per gaming customer, like zynga which has $4 ARPU.
                </p>
                <span class="inputicon">
                    <div id="revenue_pericon"></div>
                </span>
            </div>
    
           <div class="formitem sideinfoitem clear">
                <span class="inputlabel revenuelabel">PROFIT<br/>MARGIN</span>
                <span class="inputfield">
                    <input class="text askinginputtext valuationinput" type="text" name="profit_margin" id="profit_margin" maxlength="3"></input>%
                </span>
                <p class="sideinfo">
                    <label class="sideinfoheader">Profit Margin</label>
                    <br/>
                    Your net profit margin.  For instance, if you make $10,000
                    in business income a month, and before taxes have expenses of $8,000, and no debt payments, then you have a net profit profit of $2,000 a month, which
                    is 20% of your income.  So your margin would be 20%.  Google, for instance, is around 30%.  This is your eventual profit when you reach your target market size,
                    since often startup companies are not profitable in their early years.  If you aren&rsquo;t sure, make it 20%.
                </p>
                <span class="inputicon">
                    <div id="profit_marginicon"></div>
                </span>
           </div>
   
           <div class="formitem sideinfoitem clear">
                <span class="inputlabel revenuelabel">EXIT<br/>YEAR</span>
                <span class="inputfield">
                    <input class="text askinginputtext valuationinput" type="text" name="exit_year" id="exit_year" maxlength="2" value="7"></input>
                </span>
                <p class="sideinfo">
                    <label class="sideinfoheader">Exit Year</label>
                    <br/>
                    The number of years from now when the company is expected to be sold.  For a venture capital startup, this is around 7 years.  If you want to sell out
                    now, put 0.  If you&rsquo;re not sure what to put, leave it at 7 years.
                </p>
                <span class="inputicon">
                    <div id="exit_yearicon"></div>
                </span>
            </div>
    
            <div class="formitem sideinfoitem clear">
                <span class="inputlabel revenuelabel">EXIT<br/>PROBABILITY</span>
                <span class="inputfield">
                    <input class="text askinginputtext valuationinput" type="text" name="exit_probability" id="exit_probability" maxlength="3" value="25"></input>%
                </span>
                <p class="sideinfo">
                    <label class="sideinfoheader">Exit Probability</label>
                    <br/>
                    The exit probability, that is, the chance you hit your exit value.  Statistically, around 25% of venture-backed companies end up having a positive equity
                    value with an average of $4m going to the founders.  However, the likelihood of large exits, such as those of reaching domination in a large market,
                    is much smaller.  If you&rsquo;re not sure, leave it at 25%.
                </p>
                <span class="inputicon">
                    <div id="exit_probabilityicon"></div>
                </span>
            </div>
    
            <div class="formitem clear">
                <span class="inputlabel">EXIT VALUE</span>
                <span class="inputfield valuationfield">
                    <span class="valuationtext" id="exit_value"></span>
                </span>
                This is the exit value of your company in a best-case scenario.  It&rsquo;s the target market times revenue per times profit margin divided by discount rate.
            </div>

            <div class="formitem clear">
                <span class="inputlabel">VALUATION</span>
                <span class="inputfield valuationfield">
                    <span class="valuationtext" id="company_valuation"></span>
                </span>
                This is the net present value of your company risk-adjusted for exit probability, which is the exit value times exit probability times one minus a 10% discount rate
                raised to the exponent exit year.
                It is a good starting number to attract investments.
            </div>
    
            <div class="formitem clear">
                <span class="newlistingmsgsmall" id="newlistingmsg">&nbsp;</span>
            </div>

        </div> <!-- end valuation panel -->
    
    </div> <!-- end span-24 -->

</div> <!-- end new listing company wrapper -->

<!-- begin applicaiton valuation -->
<div class="initialhidden" id="newlistingappwrapper">

    <div class="bannertextsmall">HOW MUCH IS YOUR APP WORTH?</div>

    <div class="span-24">
        <a href="#" class="backbuttonlink">
            <span class="push-1 span-3 inputbutton backbutton">
                &lt;&lt; BACK
            </span>
        </a>

        <div class="boxtitle offertitle">
            <span class="titletext">VALUATION</span>
            <div class="titleinfobtn"></div>
            <div class="titleinfo">
                <label class="titleinfoheader">Valuation</label>
                <p>
                 Choosing a value for your app can be difficult, but we will give you some help.
                 From the data you enter, we derive a likely price for your app.  As apps are difficult to value,
                 this valuation is only meant to be a rough guide to help you in the funding process.
                </p>
            </div>
        </div>
    
        <div class="boxpanelfull valuationpanel">
            <div class="formitem clear firstinputitem">
                <p class="formhelptext">
                    Choosing a value for your application can be difficult, but we will give you some help.  Just fill in the fields below
                    and we will calculate the valuation on the fly.
                </p>
            </div>
    
            <div class="formitem sideinfoitem clear">
                <span class="inputlabel revenuelabel">TARGET<br/>DOWNLOADS</span>
                <span class="inputfield">
                    <input class="text askinginputtext valuationinput" type="text" name="target_downloads" id="target_downloads" maxlength="20"></input>
                </span>
                <p class="sideinfo">
                    <label class="sideinfoheader">Target Downloads</label>
                    <br/>
                    The total number of downloads you want to achive over the app lifetime, before you need to create a new version of the
                    app with significant investment.  
                    For the original Angry Birds for the iPhone on the App Store, this number is around 20 million downloads.  If you
                    have a web-based non-downloadable application, this number will be the number of users registered on your site.
                </p>
                <span class="inputicon">
                    <div id="target_downloadsicon"></div>
                </span>
            </div>
    
            <div class="formitem sideinfoitem clear">
                <span class="inputlabel revenuelabel">CONVERSION<br/>RATE</span>
                <span class="inputfield">
                    <input class="text askinginputtext valuationinput" type="text" name="conversion_rate" id="conversion_rate" maxlength="4"></input>%
                </span>
                <p class="sideinfo">
                    <label class="sideinfoheader">Conversion Rate to Paid App</label>
                    <br/>
                    The percentage of all users who become revenue-paying users.  They may pay either by downloading a paid version of your app or
                    performing one or more in-app purchases.  If you only support paid downloads, make this number 100%.  If you only have a free
                    version, make this 0%. For the original Angry Birds on the iPhone this number is around 30%.
                </p>
                <span class="inputicon">
                    <div id="conversion_rateicon"></div>
                </span>
           </div>
    
           <div class="formitem sideinfoitem clear">
                <span class="inputlabel revenuelabel">DOWNLOAD<br/>PRICE</span>
                <span class="inputfield">
                    <input class="text askinginputtext valuationinput" type="text" name="download_price" id="download_price" maxlength="20"></input>
                </span>
                <p class="sideinfo">
                    <label class="sideinfoheader">Download Price</label>
                    <br/>
                    The cost to download a paid version of your app or to use a paid version of your software.  If you have a free download or trial
                    with an in-app purchase unlock key, use this price.
                    For Angry Birds on the App Store, this number is $0.99.  The cost here is the retail price, not including the typical 30% fee
                    charged by an app store vendor or your hosting costs.
                </p>
                <span class="inputicon">
                    <div id="downloid_priceicon"></div>
                </span>
            </div>
    
           <div class="formitem sideinfoitem clear">
                <span class="inputlabel revenuelabel">AD $ PER<br/>DOWNLOAD</span>
                <span class="inputfield">
                    <input class="text askinginputtext valuationinput" type="text" name="ad_revenue_per_download" id="ad_revenue_per_download" maxlength="10"></input>
                </span>
                <p class="sideinfo">
                    <label class="sideinfoheader">Ad Revenue Per Download</label>
                    <br/>
                    The lifetime advertising revenue you receive per application download.  Typically this is revenue you make from the free ad-supported version
                    of your app.  Your monthly ad revenue divided by your monthly total downloads
                    is a good approximation.  If you&rsquo;re not sure what to put, just put your download price divided by three.
                    If you don&rsquo;t have an ad-supported app version, set this field to 0.
                </p>
                <span class="inputicon">
                    <div id="ad_revenue_per_downloadicon"></div>
                </span>
            </div>
    
            <div class="formitem sideinfoitem clear">
                <span class="inputlabel revenuelabel">TARGET<br/>PROBABILITY</span>
                <span class="inputfield">
                    <input class="text askinginputtext valuationinput" type="text" name="target_probability" id="target_probability" maxlength="3" value="25"></input>%
                </span>
                <p class="sideinfo">
                    <label class="sideinfoheader">Target Probability</label>
                    <br/>
                    The probability you reach your target number of downloads or users.  If your downloads are currently 10,000 a month and you already have 50,000 downloads, the
                    chance of reaching a targeted 100,000 downloads may be very high, say 90%.  Given the same situation and a target of 10,000,000 downloads your chance
                    may be much less, only 5%.  If you&rsquo;re not sure what to put, leave it at 25%.
                </p>
                <span class="inputicon">
                    <div id="target_probabilityicon"></div>
                </span>
            </div>
    
            <div class="formitem clear">
                <span class="inputlabel">VALUATION</span>
                <span class="inputfield valuationfield">
                    <span class="valuationtext" id="app_valuation"></span>
                </span>
                This is the value of your company or application risk-adjusted for exit probability.
                In detail, it is the target number of downloads times the total revenue per download times the target
                probability times revenue retention rate after a thirty percent vendor fee.
                It is a good starting point for investment offers.
            </div>
    
            <div class="formitem clear">
                <span class="newlistingmsgsmall" id="newlistingmsg">&nbsp;</span>
            </div>

        </div> <!-- end valuation panel -->
    
    </div> <!-- end span-16 -->

</div> <!-- end new listing company wrapper -->


</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/forms.js"></script>
<script src="js/modules/newlistingbase.js"></script>
<script src="js/modules/newlistingvaluationpage.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
