define(`tilediv',`
<div class="tile">
    <div class="tileimg $1"></div>
    <div class="tiledays"></div>
    <div class="tiledaystext">$2</div>
    <div class="tiletype"></div>
    <div class="tiletypetext">$3</div>
    <div class="tilepoints">
        <div class="tilevotes">$4</div>
        <div class="thumbup tilevoteimg"></div>
        <div class="tileposted">on $5</div>
    </div>
    <p class="tiledesc">
        <span class="tilecompany">$6</span><br/>
        <span class="tileloc">$7</span><br/>
        <span class="tiledetails">$8</span>
    </p>
</div>
')
define(`tilespan',`
`<span class="span-4">'tilediv($@)`</span>'
')
define(`tilespanlast',`
`<span class="span-4 last">'tilediv($@)`</span>'
')
