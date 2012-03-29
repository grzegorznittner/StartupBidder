function BMCClass() {
}
pl.implement(BMCClass, {
    display: function(listing) {
        var n = 9,
            i,
            field,
            idx,
            sel,
            val,
            html;
        for (i = 0; i < n; i++) {
            idx = 1 + i;
            field = 'answer' + idx;
            sel = '#' + field + 'bmc';
            val = listing[field];
            html = HTMLMarkup.prototype.stylize(val, 'bmc');
            pl(sel).html(html);
        }
    },
    getUpdater: function(id) {
        return function(json) {
            var bmcsel = '#' + id + 'bmc',
                newval = json && json.listing && (json.listing[id] !== null) ? json.listing[id] : null,
                html = HTMLMarkup.prototype.stylize(newval, 'bmc');
            if (newval !== null) {
                pl(bmcsel).html(html);
            }
        };
    },
    genDisplay: function(field, displayCalc) {
        return function(result, val) {
            var html;
            displayCalc();
            if (result === 0) {
                html = HTMLMarkup.prototype.stylize(val, 'bmc');
                pl(field.fieldBase.sel + 'bmc').html(html);
            }
        }
    }
});
