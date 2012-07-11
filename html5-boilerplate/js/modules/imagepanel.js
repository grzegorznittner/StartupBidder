function ImagePanelClass(options) {
    this.options = options || {}; // set options.editmode to true for an editable panel
    this.listing = this.options.listing || {};
}

pl.implement(ImagePanelClass, {
    setListing: function(listing) {
        this.listing = listing;
        return this;
    },
    enableImage: function(i) {
        var pic = 'pic' + i,
            cachebust = pl('#' + pic + 'nav').hasClass('dotnavempty') ? '' : '?id=' + Math.floor(Math.random()*1000000000),
            url = '/listing/picture/' + this.listing.listing_id + '/' + i + cachebust;
        pl('#' + pic + 'nav').removeClass('dotnavempty');
        pl('#' + pic).css({ 'background-image': 'url(' + url + ')' });
    },
    display: function() {
        var self = this,
            firstpic = this.options.editmode ? 'pic1' : undefined,
            numpics = 5,
            slideshowstart = 1,
            pic,
            i;
        self.runningSlideshow = false;
        self.numPics = 0;
        for (i = 1; i <= numpics; i++) {
            pic = 'pic' + i;
            if (this.listing[pic]) {
                self.enableImage(i);
                if (!firstpic) {
                    firstpic = pic;
                }
                self.numPics++;
            }
            else if (this.options.editmode) {
                pl('#' + pic + 'nav').removeClass('dotnavempty');
                self.numPics++;
            }
        }
        if (!(firstpic && !this.options.editmode && self.numPics <= 1)) {
            pl('.dotnav').bind('click', function() {
                var ul = pl(this).hasClass('dotnav') ? this : this.parentNode,
                    navid = ul.id,
                    picnum = navid.replace(/pic|nav/g, '');
                self.runningSlideshow = false;
                self.advanceRight(picnum);
            });
            pl('.picslide').bind('click', function() {
                self.runningSlideshow = false;
                self.advanceRight();
            });
        }
        if (firstpic && !this.options.editmode && self.numPics <= 1) {
            pl('#imagetitle').text('IMAGE');
            pl('.dotnavwrapper').hide();
        }
        else if (firstpic && !this.options.editmode) {
            pl('#' + firstpic + 'nav').addClass('dotnavfilled');
            self.runningSlideshow = true;
            setTimeout(function(){ self.advanceSlideshow(); }, 5000);
        }
        else { // default highlight first
            pl('#pic1nav').addClass('dotnavfilled');
        }
    },

    advanceSlideshow: function() {
        var self = this;
        if (self.runningSlideshow) {
            self.advanceRight();
            setTimeout(function() { self.advanceSlideshow() }, 5000);
        }
    },
  
    advanceRight: function(picnum) {    
        var left = 1 * pl('#picslideset').css('left').replace(/px/, ''),
            slidewidth = 1 * pl('#pic1').css('width').replace(/px/, ''),
            fullwidth = slidewidth * this.numPics,
            newleft = picnum ? slidewidth * ( 1 - picnum ) : Math.floor((left - slidewidth) % fullwidth),
            newleftpx = newleft + 'px',
            newpicnum = picnum || (Math.floor(Math.abs(newleft) / slidewidth) + 1),
            onboundary = Math.floor(left % slidewidth) === 0;
        if (onboundary) { // prevent in-transition movements
            if (this.options.editmode) {
                pl('#picnum').text(newpicnum);
                pl('#picuploadfile').attr({name: 'PIC' + newpicnum});
            }
            pl('.dotnav').removeClass('dotnavfilled');
            pl('#pic' + newpicnum + 'nav').addClass('dotnavfilled');
            pl('#picslideset').css({left: newleftpx});
        }
    }
});
