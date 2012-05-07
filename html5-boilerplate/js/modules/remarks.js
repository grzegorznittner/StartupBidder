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
        self.loggedin_profile = json.loggedin_profile;
        self.loggedin_profile_id = self.loggedin_profile ? self.loggedin_profile.profile_id : null;
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
        var self = this,
            boxbinder = self.bindAddBoxGenerator(0, self.addboxid, self.addtextid, self.addmsgid, self.addbtnid, self.displaytype, self.posturl);
        boxbinder();
    },
    bindAddBoxGenerator: function(remarkid, boxid, textid, msgid, btnid, displaytype, posturl) {
        var self = this,
            boxsel = '#' + boxid,
            textsel = '#' + textid,
            msgsel = '#' + msgid,
            btnsel = '#' + btnid;
        console.log('foo', boxsel, textsel, msgsel, btnsel);
        return function() {
            if (pl(boxsel).hasClass('bound')) {
                return;
            }
            pl(textsel).bind({
                focus: function() {
                    if (!pl(textsel).hasClass('edited')) {
                        pl(textsel).attr({value: ''});
                        pl(msgsel).html('&nbsp;');
                    }
                },
                keyup: function() {
                    if (!pl(textsel).hasClass('edited')) {
                        pl(textsel).addClass('edited');
                        pl(msgsel).html('&nbsp;');
                    }
                    else if (pl(textsel).attr('value').length >= 5 && !pl(btnsel).hasClass(self.addenabledclass)) {
                        pl(btnsel).addClass(self.addenabledclass);
                    }
                    else if (pl(textsel).attr('value').length < 5 && pl(btnsel).hasClass(self.addenabledclass)) {
                        pl(btnsel).removeClass(self.addenabledclass);
                    }
                    return false;
                },
                blur: function() {
                    if (!pl(textsel).hasClass('edited')) {
                        pl(textsel).attr({value: 'Put your ' + displaytype + ' here...'});
                        pl(btnsel).removeClass(self.addenabledclass);
                    }
                }
            });
            pl(btnsel).bind({
                click: function(event) {
                    console.log('clicked');
                    var completeFunc = function() {
                            pl(textsel).removeClass('edited').attr({value: 'Put your ' + displaytype + ' here...'});
                            pl(btnsel).removeClass(self.addenabledclass);
                            pl(msgsel).html(displaytype + ' posted');
                            self.ajax.call();
                        },
                        text = SafeStringClass.prototype.clean(pl(textsel).attr('value')),
                        typedata = {
                            text: SafeStringClass.prototype.htmlEntities(text)
                        },
                        data = {},
                        ajax = new AjaxClass(posturl, msgid, completeFunc);
                    if (self.listing_id) {
                        typedata.listing_id = self.listing_id;
                    }
                    if (self.type === 'comments') {
                        typedata.profile_id = self.loggedin_profile_id;
                    }
                    else if (remarkid) {
                        typedata.message_id = remarkid;
                    }
                    if (!pl(btnsel).hasClass(self.addenabledclass)) {
                        return false;
                    }
                    data[self.postproperty] = typedata;
                    ajax.setPostData(data);
                    ajax.call();
                    return false;
                }
            });
            pl(boxsel).addClass('bound');
        };
    },
    displayAddRemarkBox: function() {
        var self = this;
        self.bindAddRemarkBox();
        pl('#'+self.addtitleid+', #'+self.addboxid).show();
    },
    displayRemark: function() {
        var self = this,
            html = '',
            i,
            remark,
            deletable,
            islistingowner,
            replyable,
            deletesel,
            bindlist = [];
        pl('#'+self.numremarksid).text(self.remarklist.length);
        if (self.remarklist.length === 0) {
            pl('#'+self.statusmsgid).html(self.defaultmsg).show();
            return;
        }
        for (i = 0; i < self.remarklist.length; i++) {
            remark = self.remarklist[i];
            islistingowner = self.loggedin_profile_id && (self.loggedin_profile_id === remark.listing_owner);
            deletable = (remark.profile_id === self.loggedin_profile_id) && self.type !== 'message';
            replyable = self.loggedin_profile_id && remark.user_id && self.loggedin_profile_id === remark.user_id;
            html += self.makeRemark(remark, deletable, replyable);
            bindlist.push([remark, deletable, replyable]);
        }
        pl('#'+this.listid).html(html);
        for (i = 0; i < bindlist.length; i++) {
            remark = bindlist[i][0];
            deletable = bindlist[i][1];
            replyable = bindlist[i][2];
            self.bindRemark(remark, deletable, replyable);
        }
        pl('#'+this.listid).show();
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
    showReplyBoxGenerator: function(remarkid) {
        var self = this;
        return function() {
            var remarkreplyboxsel = '#' + self.type + '_replybox_' + remarkid;
            pl(remarkreplyboxsel).addClass('remarkreplyshow');
        };
    },
    makeRemark: function(remark, deletable, replyable) {
        var self = this,
            remarkid = remark[self.idproperty],
            remarkprefix = self.fromnameprefix,
            remarkfrom = remark[self.fromnameproperty],
            remarkto = remark[self.tonameproperty],
            remarkreplybtn = replyable ? '<div class="smallinputbutton darkblue remarkreplybtn hoverlink" id="' + self.type + '_replybtn_' + remarkid + '">REPLY</div>' : '',
            remarkdate = remark[self.dateproperty],
            remarktext = remark[self.textproperty],
            remarkreplybox = replyable ? '\
                <div class="droptransition remarkreply" id="' + self.type + '_replybox_' + remarkid + '">\
                    <textarea class="textarea messagetextarea" id="' + self.type + '_replytext_' + remarkid + '" name="relpytext" cols="20" rows="5">Put your reply here...</textarea>\
                    <span class="span-12 inputmsg successful" id="' + self.type + '_replymsg_' + remarkid + '">&nbsp;</span>\
                    <span class="span-3 inputbutton" id="' + self.type + '_replysendbtn_' + remarkid + '">SEND</span>\
                </div>\
            ' : '';
        return '\
<dt id="' + self.type + '_' + remarkid + '">\
    <div class="remarkdttitle">\
        <div class="remarkdttitleline">' + remarkprefix + ' ' + remarkfrom + ' on ' + DateClass.prototype.format(remarkdate) + '\
        ' + (deletable ? ' <span id="' + self.type + '_delete_msg_' + remarkid + '"></span>' : '') + '\
        </div>\
        ' + (deletable ? '<div id="' + self.type + '_delete_' + remarkid + '" class="remarkdelete checkboxredicon"></div>' : '') + '\
        ' + remarkreplybtn + '\
    </div>\
</dt>\
<dd id="' + self.type + '_dd_' + remarkid + '">\
    ' + remarktext + remarkreplybox + '\
</dd>\
';
    },
    bindRemark: function(remark, deletable, replyable) {
        var self = this,
            remarkid = remark[self.idproperty],
            deletesel,
            replybtnsel,
            replyboxid,
            replytextid,
            replymsgid,
            replybtnid,
            boxbinder;
        if (deletable) {
            deletesel = '#'+self.type+'_delete_' + remarkid,
            pl(deletesel).bind({
                click: self.deleteRemarkGenerator(remarkid)
            });
        }
        if (replyable) {
            replybtnsel = '#' + self.type + '_replybtn_' + remarkid,
            pl(replybtnsel).bind({
                click: self.showReplyBoxGenerator(remarkid)
            });
            replyboxid = self.type + '_replybox_' + remarkid;
            replytextid = self.type + '_replytext_' + remarkid;
            replymsgid = self.type + '_replymsg_' + remarkid;
            replybtnid = self.type + '_replysendbtn_' + remarkid;
            boxbinder = self.bindAddBoxGenerator(remarkid, replyboxid, replytextid, replymsgid, replybtnid, 'reply', '/listing/reply_message');
            boxbinder();
        }
    }
});
