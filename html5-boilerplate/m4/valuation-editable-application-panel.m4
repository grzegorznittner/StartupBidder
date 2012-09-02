`
<div class="boxpanelfull valuationpanel initialhidden" id="valuation_application_wrapper">

    <div class="formitem clear firstinputitem">
        <p class="formhelptext">
            Application valuation is determined by a mix of current results and future potential.  If your app is
            beyond the concept stage and already released, we look at how much money it cost to create and how
            much revenue it&rsquo;s been achieving so far.  We then add to this an analysis into the future potential
            of the app with sufficient investment and resources.
            We&rsquo;ll guide you in the steps below and calculate your valuation on the fly.
        </p>
    </div>
    
    <div class="formitem clear">
        <label class="inputlabel valuationlabel" for="is_app_released">IS APP<br/>RELEASED?</label>
        <span class="inputfield valuationfield">
            <select id="is_app_released" class="text askinginputtext valuationinput developmentstageselect">
                <option value="true" selected="selected">Yes&nbsp;</option>
                <option value="false">No&nbsp;</option>
            </select>
        </span>
        <span class="valuationhelptext">
            Answer yes if you have released your app to the app store, no if it is still just a concept.
        </span>
    </div>

    <div class="initialhidden" id="is_app_released_wrapper">
    
        <div class="formitem clear">
            <label class="inputlabel valuationlabel" for="cost_of_app">COST OF<br/>APP</label>
            <span class="inputfield valuationfield">
                <input class="text askinginputtext valuationinput" type="text" name="cost_of_app" id="cost_of_app" maxlength="20"></input>
            </span>
            <span class="valuationhelptext">
                The amount of money you have spent creating the application, including personal time calculated at a market rate.  Usually$5,000 for a simple app,
                $50,000 for more complex apps and $150,000+ for professional apps.
            </span>
        </div>
    
        <div class="formitem clear">
            <label class="inputlabel valuationlabel" for="months_live">MONTHS<br/>LIVE</label>
            <span class="inputfield valuationfield">
                <input class="text askinginputtext valuationinput" type="text" name="months_live" id="months_live" maxlength="20"></input>
            </span>
            <span class="valuationhelptext">
                How many months has it been since your application was live and available to customers?
            </span>
        </div>
    
        <div class="formitem clear">
            <label class="inputlabel valuationlabel" for="best_month">BEST<br/>MONTH</label>
            <span class="inputfield valuationfield">
                <input class="text askinginputtext valuationinput" type="text" name="best_month" id="best_month" maxlength="20"></input>
            </span>
            <span class="valuationhelptext">
                What was your total monthly application revenue in your best month since you went live?  Include earnings from all
                sources including paid downloads, in-app purchases, advertising income, and subscriptions, after any app store fees.
            </span>
        </div>
    
        <div class="formitem clear">
            <span class="inputlabel valuationlabel">PROJECTED<br/>PEAK</span>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="projected_peak"></span>
            </span>
            <span class="valuationhelptext">
            The projected peak monthly revenue your app will achieve.  Based on research, revenue peaks sometime in the first
            12 months usually within 6 months.  If you have been live less than 6 months the peak is 35% per month increase
            up to the 6 month mark.
            </span>
        </div>
    
        <div class="formitem clear">
            <span class="inputlabel valuationlabel">FUTURE<br/>EARNINGS</span>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="future_earnings"></span>
            </span>
            <span class="valuationhelptext">
            Projected total future earnings for your app.  This includes this month&rsquo;s revenues and all revenues up
            to the peak, then declining at a rate of 7% per month thereafter.  It is not unlike an oil field production curve in this respect.
            </span>
        </div>
    
    </div>

    <div class="formitem clear">
        <label class="inputlabel valuationlabel" for="analyze_app_potential">ANALYZE<br/>POTENTIAL?</label>
        <span class="inputfield valuationfield">
            <select id="analyze_app_potential" class="text askinginputtext valuationinput developmentstageselect">
                <option value="true">Yes&nbsp;</option>
                <option value="false" selected="selected">No&nbsp;</option>
            </select>
        </span>
        <span class="valuationhelptext">
            Do you want to analyze the potential of this application?  This may aid you in asking for investment.
        </span>
    </div>

    <div class="initialhidden" id="analyze_app_potential_wrapper">

        <div class="formitem clear">
            <label class="inputlabel valuationlabel" for="target_users">TARGET<br/>USERS</label>
            <span class="inputfield valuationfield">
                <input class="text askinginputtext valuationinput" type="text" name="target_users" id="target_users" maxlength="20"></input>
            </span>
            <span class="valuationhelptext">
                The target number of active users, in a best case scenario, if you had investment backing for future development
                and marketing.  For Angry Birds across all platforms this number is around 200 million, 30% of their total lifetime downloads.
            </span>
        </div>
     
        <div class="formitem clear">
            <label class="inputlabel valuationlabel" for="monthly_arpu">MONTHLY<br/>ARPU</label>
            <span class="inputfield valuationfield">
                <input class="text askinginputtext valuationinput" type="text" name="monthly_arpu" id="monthly_arpu" maxlength="20"></input>
            </span>
            <span class="valuationhelptext">
                The average revenue per user, ARPU, is how much revenue you expect to have per active user in a given month from all sources,
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
            The monthly target revenue to be achieved if you receive investment and hit your agressive goals of number of target users.
            </span>
        </div>
    
        <div class="formitem clear">
            <span class="inputlabel valuationlabel">TARGET<br/>VALUATION</span>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="target_valuation"></span>
            </span>
            <span class="valuationhelptext">
            The valuation you would be likely to receive if you meet your user and revenue targets, a best-case scenario.  Calculated as the
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
        This is the resulting valuation for your application.  It is the average of the cost of the app, the projected future revenue,
        and the risk-adjusted net present value of your target valuation, discounted for two years at 10%
        with a 10% success rate, but never less than the future earnings.
        </span>
    </div>
    
</div> <!-- end valuation panel -->
'
