define(`tilediv',`
<div class="tile">
    <a href="listing-page.html"><div class="tileimg $1"></div></a>
    <div class="tiledays"></div>
    <div class="tiledaystext">$2</div>
    <div class="tiletype"></div>
    <div class="tiletypetext">$3</div>
    <div class="tilepoints"></div>
    <div class="tilepointstext">
        <div class="tilevotes">$4</div>
        <div class="thumbup tilevoteimg"></div>
        <div class="tileposted">$5</div>
    </div>
    <a href="listing-page.html">
    <p class="tiledesc">
        <span class="tilecompany">$6</span><br/>
        <span class="tileloc">$7</span><br/>
        <span class="tiledetails">$8</span>
    </p>
    </a>
</div>
')
define(`tilespan',`
`<span class="span-4">'tilediv($@)`</span>'
')
define(`tilespanlast',`
`<span class="span-4 last">'tilediv($@)`</span>'
')
