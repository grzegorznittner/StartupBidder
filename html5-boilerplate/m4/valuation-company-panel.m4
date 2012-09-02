`
<div class="boxpanelfull valuationpanel initialhidden" id="valuation_company_wrapper">

    <p>
            A company&rsquo;s valuation is created from a mix of current numbers and future potential.
            Current valuations are determined from the company&rsquo;s development stage, money
            invested so far, and current revenue.  This is then expanded upon by an analysis of
            market potential and the chance of reaching that potential.
    </p>
    
    <div class="formitem clear">
        <label class="inputlabel valuationlabel" for="development_stage">DEVELOPMENT<br/>STAGE</label>
        <span class="inputfield valuationfield">
            <span class="valuationtext" id="development_stage"></span>
        </span>
        <span class="valuationhelptext">
            The stage of development the company has reached.  A company starts as a concept, assembles a team, creates a product, gains customer traction,
            and obtains revenue growth with profitability.  Each advanacement to the next stage brings a higher valuation.
        </span>
    </div>

    <div class="formitem clear">
        <span class="inputlabel valuationlabel">COST TO<br/>DUPLICATE</span>
        <span class="inputfield valuationfield">
            <span class="valuationtext" id="cost_to_duplicate"></span>
        </span>
        <span class="valuationhelptext">
            This is how much it would cost an independent entity to recreate the product or service.  Typically this will be at least as much as the total
            investment expended so far.
        </span>
    </div>

    <div class="formitem clear">
        <span class="inputlabel valuationlabel">CURRENT<br/>REVENUE</span>
        <span class="inputfield valuationfield">
            <span class="valuationtext" id="current_revenue"></span>
        </span>
        <span class="valuationhelptext">
            The total sales revenues from the last twelve months, or zero if there are no sales yet.
        </span>
    </div>

    <div class="initialhidden" id="analyze_company_potential_wrapper">

        <div class="formitem clear">
            <span class="inputlabel valuationlabel">MARKET<br/>SIZE</span>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="market_size"></span>
            </span>
            <span class="valuationhelptext">
                The total number of customers or monetizable actions in the target market per year.  This is 150 billion per year for global internet search,
                1.3 billion for global social media users.  This is the dream number to hit if the company achives market domination.
            </span>
        </div>
       
        <div class="formitem clear">
            <span class="inputlabel valuationlabel">REVENUE<br/>PER</span>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="revenue_per"></span>
            </span>
            <span class="valuationhelptext">
                The revenue per customer or monetizable item in the target market.  This could be google&rsquo;s $0.20 revenue per search,
                facebook&rsquo;s $5 annually per social media user, or zynga&rsquo;s $4 annually per active gaming customer.
            </span>
        </div>
      
        <div class="formitem clear">
            <span class="inputlabel valuationlabel">EXIT<br/>VALUE</span>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="exit_value"></span>
            </span>
            <span class="valuationhelptext">
            This is the company exit value in a best-case scenario.  It&rsquo;s the target market times revenue per times a 30% profit margin
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
        This is the resulting valuation for the company.  It the average of the cost to duplicate, the development stage estimate, the current revenues
        at a p/s ratio of 7, and the net present exit value discounted 7 years at 10%, risk-adjusted for a 10% exit probability, but never less than
        the current revenue.
        </span>
    </div>
    
</div> <!-- end valuation panel -->
'
