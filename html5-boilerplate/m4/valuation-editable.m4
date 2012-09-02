`
<div class="bannertextsmall">HOW MUCH IS YOUR <span id="listingtype"></span> WORTH?</div>

<div class="span-24">
    <a href="#" class="backbuttonlink">
        <span class="push-1 span-3 inputbutton backbutton">
            &lt;&lt; BACK
        </span>
    </a>
   
<div class="boxpanelfull valuationtypepanel">

    <div class="formitem clear firstinputitem">
        <p class="formhelptext">
            Choosing a value for your listing can be difficult, but we will give you some help.  Just fill in the fields below
            and we will calculate the valuation on the fly.
        </p>
    </div>

    <div class="formitem clear">
        <label class="inputlabel valuationlabel" for="valuation_type">VALUATION<br/>TYPE</label>
        <span class="inputfield valuationfield">
            <select id="valuation_type" class="text askinginputtext valuationtypeselect">
                <option value="company">Company&nbsp;</option>
                <option value="application">Application&nbsp;</option>
            </select>
        </span>
        </p>
        <span class="valuationhelptext">
                The type of valuation to perform.  For actual companies such as startups, select Company,   For mobile, tablet, web and
                desktop applications, select Application.
        </span>
    </div>

</div> 
'
include(valuation-editable-company-panel.m4)
include(valuation-editable-application-panel.m4)
`    

<div class="boxpanelfull">
    <div class="formitem clear">
        <span class="newlistingmsgsmall" id="newlistingmsg">&nbsp;</span>
    </div>
</div>

</div> <!-- end span-24 -->
'
