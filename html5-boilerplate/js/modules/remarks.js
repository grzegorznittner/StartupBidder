/*
new RemarkClass({
    listing_id: listing_id,
    type: 'comment',
    geturl: '/comments/listing/',
    getproperty: 'comments',
    idproperty: 'comment_id',
    fromnameproperty: 'profile_username',
    dateproperty: 'comment_date',
    textproperty: 'text',
    posturl: '/comment/create',
    deleteurl:  '/comment/delete'});

new RemarkClass({
    listing_id: listing_id,
    type: 'message',
    geturl: '/listings/messages',
    getproperty: 'notifications',
    idproperty: 'notification_id',
    fromnameproperty: 'from_user_nickname',
    dateproperty: 'create_date',
    textproperty: 'text_1',
    posturl: '/listing/send_private',
    deleteurl:  null});
*/
function RemarkClass(options) {
    var self = this;
    self.options = options || {};
    self.listing_id = options.listing_id;
    self.type = options.type;
    self.displaytype = options.displaytype || options.type;
    self.geturl = options.geturl + self.listing_id;
    self.getproperty = options.getproperty;
    self.idproperty = options.idproperty;
    self.fromnameproperty = options.fromnameproperty;
    self.fromnameprefix = options.fromnameprefix;
    self.dateproperty = options.dateproperty;
    self.textproperty = options.textproperty;
    self.posturl = options.posturl;
    self.postproperty = options.postproperty || self.type;
    self.deleteurl = options.deleteurl;
    self.statusmsgid = self.type + 'msg';
    self.addmsgid = 'add' + self.type + 'msg';
    self.addtextid = 'add' + self.type + 'text';
    self.addbtnid = 'add' + self.type + 'btn';
    self.addboxid = 'add' + self.type + 'box';
    self.addtitleid = 'add' + self.type + 'title';
    self.addenabledclass = 'add' + self.type + 'enabled';
    self.defaultmsg = '<p>Be the first to ' + self.displaytype + '!</p>';
    self.numitemsid = 'num_' + self.type + 's';
    self.listid = self.type + 'list';
    self.completeFunc = function(json) {
        self.store(json);
        self.display();
    };
    self.ajax = new AjaxClass(self.geturl, self.statusmsgid, self.completeFunc);
}
pl.implement(RemarkClass, {
    load: function() {
        var self = this;
        self.ajax.call();
    },
    store: function(json) {
        var self = this;
        self.remarklist = json[self.getproperty] || [];
        self.loggedin_profile_id = json.loggedin_profile ? json.loggedin_profile.profile_id : null;
    },
    display: function() {
        var self = this;
        if (self.loggedin_profile_id) {
            self.displayAddRemarkBox();
        }
        else {
            self.defaultmsg = '<p>Login and be the first to ' + self.displaytype + '!</p>';
        }
        self.displayRemark();
    },
    bindAddRemarkBox: function() {
        var self = this;
        pl('#'+self.addtextid).bind({
            focus: function() {
                if (!pl('#'+self.addtextid).hasClass('edited')) {
                    pl('#'+self.addtextid).attr({value: ''});
                    pl('#'+self.addmsgid).html('&nbsp;');
                }
            },
            keyup: function() {
                if (!pl('#'+self.addtextid).hasClass('edited')) {
                    pl('#'+self.addtextid).addClass('edited');
                    pl('#'+self.addmsgid).html('&nbsp;');
                }
                else if (pl('#'+self.addtextid).attr('value').length >= 5 && !pl('#'+self.addbtnid).hasClass(self.addenabledclass)) {
                    pl('#'+self.addbtnid).addClass(self.addenabledclass);
                }
                else if (pl('#'+self.addtextid).attr('value').length < 5 && pl('#'+self.addbtnid).hasClass(self.addenabledclass)) {
                    pl('#'+self.addbtnid).removeClass(self.addenabledclass);
                }
                return false;
            },
            blur: function() {
                if (!pl('#'+self.addtextid).hasClass('edited')) {
                    pl('#'+self.addtextid).attr({value: 'Put your ' + self.displaytype + ' here...'});
                    pl('#'+self.addbtnid).removeClass(self.addenabledclass);
                }
            }
        });
        pl('#'+self.addbtnid).bind({
            click: function(event) {
                var completeFunc = function() {
                        pl('#'+self.addtextid).removeClass('edited').attr({value: 'Put your ' + self.displaytype + ' here...'});
                        pl('#'+self.addbtnid).removeClass(self.addenabledclass);
                        pl('#'+self.addmsgid).html(self.displaytype + ' posted');
                        self.ajax.call();
                    },
                    text = SafeStringClass.prototype.clean(pl('#'+self.addtextid).attr('value')),
                    typedata = {
                        listing_id: self.listing_id,
                        profile_id: self.loggedin_profile_id,
                        text: SafeStringClass.prototype.htmlEntities(text)
                    },
                    data = {},
                    ajax = new AjaxClass(self.posturl, self.addmsgid, completeFunc);
                if (!pl('#'+self.addbtnid).hasClass(self.addenabledclass)) {
                    return false;
                }
                data[self.postproperty] = typedata;
                ajax.setPostData(data);
                ajax.call();
                return false;
            }
        });
        pl('#'+this.addboxid).addClass('bound');
    },
    displayAddRemarkBox: function() {
        var self = this;
        if (!pl('#'+this.addboxid).hasClass('bound')) {
            self.bindAddRemarkBox();
        }
        pl('#'+this.addtitleid+', #'+this.addboxid).show();
    },
    displayRemark: function() {
        var self = this,
            html = '',
            deletablelist = [],
            i,
            item,
            deletable,
            deletesel;
        pl('#'+self.numitemsid).text(self.remarklist.length);
        if (self.remarklist.length === 0) {
            pl('#'+self.statusmsgid).html(self.defaultmsg).show();
            return;
        }
        for (i = 0; i < self.remarklist.length; i++) {
            item = self.remarklist[i];
            deletable = false;
            if (item.profile_id === self.loggedin_profile_id && self.type !== 'message') {
                deletablelist.push(item);
                deletable = true;
            }
            html += self.makeRemark(item, deletable);
        }
        pl('#'+this.listid).html(html).show();
        for (i = 0; i < deletablelist.length; i++) {
            item = self.remarklist[i];
            deletesel = '#'+self.type+'_delete_' + item[self.idproperty];
            pl(deletesel).bind({click: self.deleteRemarkGenerator(item[self.idproperty])});
        }
    },
    deleteRemarkGenerator: function(remarkid) {
        var self = this;
        return function() {
            var successFunc = function(json) {
                    pl('#'+self.type+'_'+remarkid).remove();
                    pl('#'+self.type+'_dd_'+remarkid).remove();
                    pl('#'+self.numitemsid).text(pl('#'+self.listid).get(0).childNodes.length / 2);
                    if (!pl('#'+self.listid).get(0).hasChildNodes()) {
                        pl('#'+self.statusmsgid).html(self.defaultmsg);
                    }
                },
                url = self.deleteurl + '?id=' + remarkid,
                deletemsgid = self.type + '_delete_msg_' + remarkid,
                ajax = new AjaxClass(url, deletemsgid, null, successFunc);
            ajax.setPost();
            ajax.call();
        };
    },
    makeRemark: function(remark, deletable) {
        var self = this,
            remarkid = remark[self.idproperty],
            remarkprefix = self.fromnameprefix,
            console.log(self.fromnameproperty, remark[self.fromnameproperty])
            remarkfrom = remark[self.fromnameproperty],
            remarkdate = remark[self.dateproperty],
            remarktext = remark[self.textproperty];
        return '\
<dt id="' + self.type + '_' + remarkid + '">\
<div class="remarkdttitle">\
<div class="remarkdttitleline">' + remarkprefix + ' ' + remarkfrom + ' on ' + DateClass.prototype.format(remarkdate) + '\
    ' + (deletable ? ' <span id="' + self.type + '_delete_msg_' + remarkid + '"></span>' : '') + '\
</div>\
' + (deletable ? '<div id="' + self.type + '_delete_' + remarkid + '" class="remarkdelete checkboxredicon"></div>' : '') + '\
</div>\
</dt>\
<dd id="' + self.type + '_dd_' + remarkid + '">' + remarktext + '</dd>\
';
    }
});

