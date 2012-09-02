`
<div class="boxpanelfull valuationpanel initialhidden" id="valuation_application_wrapper">

    <p>
            Application valuation is determined by a mix of current results and future potential.  If the app is
            beyond the concept stage and already released, we look at how much money it cost to create and how
            much revenue it&rsquo;s been achieving so far.  We then optionally add to this an analysis into the
            future potential of the app with sufficient investment and resources.
    </p>
    
    <div class="formitem clear">
        <label class="inputlabel valuationlabel" for="is_app_released">IS APP<br/>RELEASED?</label>
        <span class="inputfield valuationfield">
            <span class="valuationtext" id="is_app_released"></span>
        </span>
        <span class="valuationhelptext">
            Has the app has been released to the market?
        </span>
    </div>

    <div class="initialhidden" id="is_app_released_wrapper">
    
        <div class="formitem clear">
            <label class="inputlabel valuationlabel" for="cost_of_app">COST OF<br/>APP</label>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="cost_of_app"></span>
            </span>
            <span class="valuationhelptext">
                The amount of money spent on the application so far.  Typically around $5,000 for a simple app,
                $50,000 for more complex apps and $150,000+ for professional apps.
            </span>
        </div>
    
        <div class="formitem clear">
            <label class="inputlabel valuationlabel" for="months_live">MONTHS<br/>LIVE</label>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="months_live"></span>
            </span>
            <span class="valuationhelptext">
                How many months it has been since the application was live and available to customers.
            </span>
        </div>
    
        <div class="formitem clear">
            <label class="inputlabel valuationlabel" for="best_month">BEST<br/>MONTH</label>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="best_month"></span>
            </span>
            <span class="valuationhelptext">
                The total monthly application revenue in the best month since release.  Includes earnings from all
                sources including paid downloads, in-app purchases, advertising income, and subscriptions, after any app store fees.
            </span>
        </div>
    
        <div class="formitem clear">
            <span class="inputlabel valuationlabel">PROJECTED<br/>PEAK</span>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="projected_peak"></span>
            </span>
            <span class="valuationhelptext">
            The projected peak monthly revenue the app will achieve.  Based on research, this is the peak from the first
            twelve months, or if it has been live less than 6 months, a peak of 35% per month growth from the best
            month to the 6 month mark.
            </span>
        </div>
    
        <div class="formitem clear">
            <span class="inputlabel valuationlabel">FUTURE<br/>EARNINGS</span>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="future_earnings"></span>
            </span>
            <span class="valuationhelptext">
            Projected total future earnings for the app.  This includes this month&rsquo;s revenues and all revenues up
            to the peak, then declining at a rate of 7% per month thereafter.  It is not unlike an oil field production curve in this respect.
            </span>
        </div>
    
    </div>

    <div class="initialhidden" id="analyze_app_potential_wrapper">

        <div class="formitem clear">
            <label class="inputlabel valuationlabel" for="target_users">TARGET<br/>USERS</label>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="target_users"></span>
            </span>
            <span class="valuationhelptext">
                The target number of active users, in a best case scenario, if this app had investment backing for future development
                and marketing.  For Angry Birds across all platforms this number is around 200 million, 30% of their total lifetime downloads.
            </span>
        </div>
     
        <div class="formitem clear">
            <label class="inputlabel valuationlabel" for="monthly_arpu">MONTHLY<br/>ARPU</label>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="monthly_arpu"></span>
            </span>
            <span class="valuationhelptext">
                The average revenue per user, ARPU, is how much revenue projected per active user per month from all sources,
                including paid downloads, in-app purchase, advertising, and subscriptions.  For Angry Birds it&rsquo;s about $0.04, for facebook $0.40,
                for google $1.50, for Verizon $50, for salesforce.com $125.
            </span>
        </div>
     
        <div class="formitem clear">
            <span class="inputlabel valuationlabel">MONTHLY<br/>TARGET</span>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="monthly_target"></span>
            </span>
            <span class="valuationhelptext">
            The monthly target revenue to be achieved if the app receives investment and hits the number of target users.
            </span>
        </div>
    
        <div class="formitem clear">
            <span class="inputlabel valuationlabel">TARGET<br/>VALUATION</span>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="target_valuation"></span>
            </span>
            <span class="valuationhelptext">
            The valuation if the app meets its user and revenue targets in a best-case scenario.  Calculated as the
            monthly target annualized with a high-growth price to sales ratio of 10.
            </span>
        </div>

    </div>
        
    <div class="formitem clear">
        <span class="inputlabel valuationlabel">FINAL<br/>VALUATION</span>
        <span class="inputfield valuationfield">
            <span class="valuationtext" id="application_valuation"></span>
        </span>
        <span class="valuationhelptext">
        This is the resulting valuation for the application.  It is the average of the cost of the app, the projected future revenue,
        and the risk-adjusted net present value of the target valuation, discounted for two years at 10%
        with a 10% success rate, but never less than the future earnings.
        </span>
    </div>
    
</div> <!-- end valuation panel -->
'
