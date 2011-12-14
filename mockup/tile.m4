define(`tilediv',`
<div class="tile silverbg">
    <div class="tileimg $1"></div>
    <div class="tiledays"></div>
    <div class="tiledaystext white">$2</div>
    <div class="tiletype"></div>
    <div class="tiletypetext white">$3</div>
    <div class="tilepoints darkblue">
        <div class="tilevotes">$4</div>
        <div class="thumbup tilevoteimg"></div>
        <div class="eye tilewatchimg"></div>
        <div class="tilewatch">$5</div>
    </div>
    <p class="tiledesc darkblue">
        <span class="tilecompany">$6</span><br/>
        <span class="tileloc">$7</span><br/>
        $8
    </p>
</div>
')
define(`tilespan',`
`<span class="span-4">'tilediv($@)`</span>'
')
define(`tilespanlast',`
`<span class="span-4 last">'tilediv($@)`</span>'
')
