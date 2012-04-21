`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="help-page">
<div id="wrap">
<div id="main">
'
include(header.m4)
`
<div class="container">

<!-- left column -->
<div class="span-16">
    <div class="boxtitle">HELP AND FAQ</div>
    <div class="boxpanel">
        <dl>
            <dt>What is Startupbidder?</dt>
            <dd>
                        A crowdsourced startup company listing and funding web application. On it you can place your business plan,
                        comment on business plans, and bid for a stake in the company!
            </dd>

            <dt>Who runs Startupbidder?</dt>
            <dd>
                        Greg Nittner and John Arley Burns, two guys who wanted to bring more visibility to the otherwise opaque world of
                        startup business plan valuation.  We've run startups, worked for startups, invested in startups, received investment
                        for startups, and we felt there had to be a better way.  So this is our shot at trying to improve the startup
                        community, specifically the early-stage exposure and funding.
            </dd>

            <dt>How can I contact you?</dt>
            <dd>
                        See the <a href="contact-page.html">contact page</a>.
            </dd>

            <dt>What is the algorithm you use to rank top listings?</dt>
            <dd>
Ranking is similar to the exponential time decay as used on many social-ranking sites.  Specifically, we calculate the listing score as follows:
<pre>
v = median valuation
c = number of comments
b = number of bids
t = number of days ago listing was created
s = score
</pre>
putting it together:
<pre>
s = (v/1000 + c/10 + b) / (t + 2)^1.5
</pre>
Reference:
<a target="_blank" href="http://www.seomoz.org/blog/reddit-stumbleupon-delicious-and-hacker-news-algorithms-exposed">seomoz.org</a>
             </dd>

            <dt>What happens when an entrepreneur accepts a bid?</dt>
            <dd>
                When the owner of the listing accepts a bid, the lister
                and the investor who made the bid are then notified via email of the bid
                acceptance.  The lister will then be billed a an acceptance charge of 2% of the
                funding amount.  For instance, funding of $10,000 would have an acceptance
                charge of $200.  It is then the responsibility of the lister and investor to
                follow up with each other for a formal legal funding agreement.  

            </dd>

            <dt>Are you offering shares in these companies for sale?</dt>
            <dd>
                No.  Startupbidder is an informational site linking startup entpreneurs with investors.
                We are not offering shares for sale or promoting any shares via this site.
                All investments must be concluded between the parties themselves with proper legal representation.
                Furthermore, only accredited investors may notify the lister of their interest in bidding.
            </dd>

            <dt>What stops people from using this site to steal my idea?</dt>
            <dd>

The short answer is that the Terms of Use of this site forbid violation of any
copyrights or intellectual property that you may have posted to this site.  The
long answer is that nothing stops someone from stealing your idea in the
startup community, but this is not important.  For one, ideas are not
patentable or copyrightable, and trade secrets seldom remain so for long.  If
you really need protection for your idea, you must devise an embodied method
and apparatus for it, and going through the long and difficult and expensive
process of acquiring a patent in all marketed jurisdictions is the only way to
protect your invention.  But more importantly, ideas are always floating around
and being shared.  What is more important in the startup community is not your
idea, but your implementation and the team that is driving the business.  Just
ask any venture capitalist, what matters more to a startup, the idea, or the
team. Team, team, team, team, team, I even like just saying the word, team.
Marketing, sales, strategy, operations, these are what make or break a company.
Microsoft was not the first desktop application provider, Google was not the
first search engine, and Facebook was not the first social network.  Rather,
they were able to implement and execute on existing ideas in a new and better
way.  And this site is aimed to help you do just that.

            </dd>
 
        </dl>
    </div>

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">
    <div class="boxtitle">A PIECE OF THE ACTION</div>
    <div class="sidebox">
        <p>
With Startupbidder, you&rsquo;re plugged into the pulse of the startup community.
Keep up to date on all the latest startups.  Post your own startup as an
entrepreneuer, getting feedback and exposure to investors worldwide.  As an
accredited investor, you can bid for a piece of the action. 
        </p>
    </div>

    <div class="boxtitle" id="listingstitle"></div>

    <!-- companydiv -->
    <div id="companydiv">
    </div>
    <!-- end companydiv -->

</div> <!-- end right column -->

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
  <!-- JavaScript at the bottom for fast page loading -->
  <script src="js/libs/prevel.min.js"></script>
  <script src="js/modules/base.js"></script>
  <script src="js/modules/companylist.js"></script>
  <script src="js/modules/infopage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`	
</body>
</html>
'
