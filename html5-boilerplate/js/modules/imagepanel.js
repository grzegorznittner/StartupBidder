function ImagePanelClass() {
}

pl.implement(ImagePanelClass, {
    setListing: function(listing) {
        this.listing = listing;
        return this;
    },
    display: function() {
        var self = this,
            numpics = 5,
            pic,
            url,
            firstpic,
            i,
            slideshowstart = 1;
        self.runningSlideshow = false;
        self.numPics = 0;
        for (i = 1; i <= numpics; i++) {
            pic = 'pic' + i;
            if (this.listing[pic]) {
                url = '/listing/picture/' + this.listing.listing_id + '/' + i;
                pl('#' + pic + 'nav').removeClass('dotnavempty');
                pl('#' + pic).css({ 'background-image': 'url(' + url + ')' });
                if (!firstpic) {
                    firstpic = pic;
                }
                self.numPics++;
            }
        }
        pl('.dotnav').bind('click', function() {
            var ul = pl(this).hasClass('dotnav') ? this : this.parentNode,
                navid = ul.id,
                picnum = navid.replace(/pic|nav/g, '');
            console.log(picnum);
            self.runningSlideshow = false;
            self.advanceRight(picnum);
        });
        pl('.picslide').bind('click', function() {
            self.runningSlideshow = false;
            self.advanceRight();
        });
        if (firstpic) {
        console.log('foo');
            pl('#' + firstpic + 'nav').addClass('dotnavfilled');
            self.runningSlideshow = true;
            setTimeout(function(){ self.advanceSlideshow(); }, 5000);
        }
    },

    advanceSlideshow: function() {
        var self = this;
        console.log('advanceSlideshow', self, self.runningSlideshow);
        if (self.runningSlideshow) {
            self.advanceRight();
            setTimeout(function() { self.advanceSlideshow() }, 5000);
        }
    },
   
    advanceRight: function(picnum) {
        console.log('advanceRight');
        var left = 1 * pl('#picslideset').css('left').replace(/px/, ''),
            slidewidth = 1 * pl('#pic1').css('width').replace(/px/, ''),
            fullwidth = slidewidth * this.numPics,
            newleft = picnum ? slidewidth * ( 1 - picnum ) : Math.floor((left - slidewidth) % fullwidth),
            newleftpx = newleft + 'px',
            newpicnum = picnum || (Math.floor(Math.abs(newleft) / slidewidth) + 1);
        console.log(left, slidewidth, fullwidth, newleft, newpicnum);
        pl('.dotnav').removeClass('dotnavfilled');
        pl('#pic' + newpicnum + 'nav').addClass('dotnavfilled');
        pl('#picslideset').css({left: newleftpx});
    }
});
