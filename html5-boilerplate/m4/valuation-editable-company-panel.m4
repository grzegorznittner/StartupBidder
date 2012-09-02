`
<div class="boxpanelfull valuationpanel initialhidden" id="valuation_company_wrapper">

    <div class="formitem clear firstinputitem">
        <p class="formhelptext">
            A company&rsquo;s valuation is created from a mix of current numbers and future potential.
            Current valuations are determined from the company&rsquo;s development stage, money
            invested so far, and current revenue.  This is then expanded upon by an analysis of
            market potential and the chance of reaching that potential.
            We&rsquo;ll guide you in the steps below and calculate your valuation on the fly.
        </p>
    </div>
    
    <div class="formitem clear">
        <label class="inputlabel valuationlabel" for="development_stage">DEVELOPMENT<br/>STAGE</label>
        <span class="inputfield valuationfield">
            <select id="development_stage" class="text askinginputtext valuationinput developmentstageselect">
                <option value="concept" selected="selected">Concept&nbsp;</option>
                <option value="team">Team in Place&nbsp;</option>
                <option value="product">Product Ready&nbsp;</option>
                <option value="customers">Customer Gains&nbsp;</option>
                <option value="profitability">Growing Profit&nbsp;</option>
            </select>
        </span>
        <span class="valuationhelptext">
            Your company&rsquo;s stage of development.  A company starts as a concept, assembles a team, creates a product, gains customer traction,
            and finally obtains growth with profitability.  Each step brings a higher valuation.
        </span>
    </div>

    <div class="formitem clear">
        <span class="inputlabel valuationlabel">COST TO<br/>DUPLICATE</span>
        <span class="inputfield valuationfield">
            <input class="text askinginputtext valuationinput" type="text" name="cost_to_duplicate" id="cost_to_duplicate" maxlength="20"></input>
        </span>
        <span class="valuationhelptext">
            How much it would cost an independent entity to recreate your product or service.  Typically this will be at least as much as the total
            personal and business expenses you have had up to this point.
        </span>
    </div>

    <div class="formitem clear">
        <span class="inputlabel valuationlabel">CURRENT<br/>REVENUE</span>
        <span class="inputfield valuationfield">
            <input class="text askinginputtext valuationinput" type="text" name="current_revenue" id="current_revenue" maxlength="20"></input>
        </span>
        <span class="valuationhelptext">
            Enter the total sales revenues from the last twelve months.  If you haven&rsquo;t been around twelve months yet, project your future revenues into
            the future and enter the annual value.  If you have no revenues yet, enter zero.
        </span>
    </div>

    <div class="formitem clear">
        <label class="inputlabel valuationlabel" for="analyze_company_potential">ANALYZE<br/>POTENTIAL?</label>
        <span class="inputfield valuationfield">
            <select id="analyze_company_potential" class="text askinginputtext valuationinput developmentstageselect">
                <option value="true">Yes&nbsp;</option>
                <option value="false" selected="selected">No&nbsp;</option>
            </select>
        </span>
        <span class="valuationhelptext">
            Do you want to analyze your company&rsquo;s potential?  This may aid you in asking for investment.
        </span>
    </div>

    <div class="initialhidden" id="analyze_company_potential_wrapper">

        <div class="formitem clear">
            <span class="inputlabel valuationlabel">MARKET<br/>SIZE</span>
            <span class="inputfield valuationfield">
                <input class="text askinginputtext valuationinput" type="text" name="market_size" id="market_size" maxlength="20"></input>
            </span>
            <span class="valuationhelptext">
                The total number of customers or monetizable actions in your market per year.  This is 150 billion per year for global internet search,
                1.3 billion for global social media users.  This is your dream number to hit if you achive market domination.
            </span>
        </div>
       
        <div class="formitem clear">
            <span class="inputlabel valuationlabel">REVENUE<br/>PER</span>
            <span class="inputfield valuationfield">
                <input class="text askinginputtext valuationinput" type="text" name="revenue_per" id="revenue_per" maxlength="20"></input>
            </span>
            <span class="valuationhelptext">
                The revenue per customer or monetizable item in your target market.  This could be google&rsquo;s $0.20 revenue per search,
                facebook&rsquo;s $5 annually per social media user, or zynga&rsquo;s $4 annually per active gaming customer.
            </span>
        </div>
      
        <div class="formitem clear">
            <span class="inputlabel valuationlabel">EXIT<br/>VALUE</span>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="exit_value"></span>
            </span>
            <span class="valuationhelptext">
            This is the exit value of your company in a best-case scenario.  It&rsquo;s the target market times revenue per times a 30% profit margin
            multiplied by a high-growth price-to-sales ratio of 10.
            </span>
        </div>

    </div>

    <div class="formitem clear">
        <span class="inputlabel valuationlabel">FINAL<br/>VALUATION</span>
        <span class="inputfield valuationfield">
            <span class="valuationtext" id="company_valuation"></span>
        </span>
        <span class="valuationhelptext">
        This is the resulting valuation for your company.  It the average of the cost to duplicate, the development stage estimate, the current revenues
        at a p/s ratio of 7, and the net present exit value discounted 7 years at 10%, risk-adjusted for a 10% exit probability, but never less than
        the current revenue.
        </span>
    </div>
    
</div> <!-- end valuation panel -->
'
