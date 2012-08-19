function CommentClass() {
    var queryString = new QueryStringClass();
    this.listing_id = queryString.vars.id;
}
pl.implement(CommentClass, {
    load: function() {
        var self = this,
            complete = function(json) {
                var header = new HeaderClass(),
                    companybanner = new CompanyBannerClass('comments');
                CollectionsClass.prototype.merge(self, json);
                header.setLogin(json);
                companybanner.display(json);
                self.display(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
            },

            ajax = new AjaxClass('/listing/comments/' + this.listing_id, 'commentmsg', complete);
        ajax.call();
    },

    store: function(json) {
        this.loggedin_profile_id = json.loggedin_profile && json.loggedin_profile.profile_id;
        this.listing = json.listing || {};
        this.commentlist = json.comments || [];
    },

    display: function(json) {
        if (json) {
            this.store(json);
        }
        this.displayComments();
        if (this.loggedin_profile_id && this.listing.status === 'active') {
            this.displayAddCommentBox();
        }
        else if (this.listing.status !== 'active') {
            pl('#addcommentbox').before('<div class="commentline"><p style="font-weight: bold;">Comments cannot be adding to this listing as it is not active</p></div>');
        }
        else {
            pl('#addcommentbox').before('\
    <div class="commentline"><p style="font-weight: bold;">Sign in to post a comment</p></div>\
    <div class="addlistingloginrow">\
        <a id="google_login" href="">\
            <div class="addlistinglogin headericon headersignin"></div>\
        </a>\
        <a id="twitter_login" href="">\
            <div class="addlistinglogin headericon headertwittersignin"></div>\
        </a>\
        <a id="fb_login" href="">\
            <div class="addlistinglogin headericon headerfbsignin"></div>\
        </a>\
    </div>\
            ');
            this.displayLoggedOut();
        }
        pl('#commentswrapper').show();
    },


    displayLoggedOut: function() {
        var nexturl = '/company-comments-page.html?id=' + this.listing_id;
            login_url = this.login_url,
            twitter_login_url = this.twitter_login_url,
            fb_login_url = this.fb_login_url;
        if (login_url) {
            pl('#google_login').attr({href: login_url + encodeURIComponent(nexturl)});
        } else {
            pl('#google_login').hide();
        }
        if (twitter_login_url) {
            pl('#twitter_login').attr({href: twitter_login_url + '?url=' + encodeURIComponent(nexturl)}).show();
        } else {
            pl('#twitter_login').hide();
        }
        if (fb_login_url) {
            pl('#fb_login').attr({href: fb_login_url + '?url=' + encodeURIComponent(nexturl)}).show();
        } else {
            pl('#fb_login').hide();
        }
    },


    bindAddCommentBox: function() {
        var self = this;
        if (pl('#addcommentbox').hasClass('bound')) {
            return;
        }
        pl('#addcommenttext').bind({
            focus: function() {
                if (!pl('#addcommenttext').hasClass('edited')) {
                    pl('#addcommenttext').attr({value: ''});
                    pl('#commentmsg').html('&nbsp;');
                }
            },

            keyup: function() {
                var val = pl('#addcommenttext').attr('value');
                if (!pl('#addcommenttext').hasClass('edited')) {
                    pl('#addcommenttext').addClass('edited');
                    pl('#commentmsg').html('&nbsp;');
                }
                if (val && val.length >= 1) {
                    pl('#addcommentbtn').addClass('editenabled');
                }
                else if (val && val.length < 1) {
                    pl('#addcommentbtn').removeClass('editenabled');
                }
                return false;
            },

            blur: function() {
                if (!pl('#addcommenttext').hasClass('edited')) {
                    pl('#addcommenttext').attr({value: 'Put your comment here...'});
                    pl('#addcommentbtn').removeClass('editenabled');
                }
            }
        });
        pl('#addcommentbtn').bind({
            click: function(event) {
                var complete = function(json) {
                        pl('#addcommentspinner').hide();
                        pl('#addcommenttext').removeClass('edited').removeAttr('disabled').attr({value: 'Put your comment here...'});
                        pl('#addcommentbtn').removeClass('editenabled').show();
                        pl('#commentmsg').text('');
                        pl('#addcommentbox').before(self.makeComment(json));
                        self.bindComment(json);
                    },

                    text = SafeStringClass.prototype.clean(pl('#addcommenttext').attr('value')),
                    data = {
                        comment: {
                            listing_id: self.listing_id,
                            text: text
                        }
                    },

                    ajax = new AjaxClass('/listing/post_comment', 'commentmsg', complete);
                if (pl('#addcommentbtn').hasClass('editenabled') && text) {
                    pl('#addcommentbtn').hide();
                    pl('#addcommenttext').attr('disabled', 'disabled');
                    pl('#addcommentspinner').show();
                    ajax.setPostData(data);
                    ajax.call();
                }
                return false;
            }
        });
        pl('#myusername').text(this.loggedin_profile && this.loggedin_profile.username);
        if (this.loggedin_profile && this.loggedin_profile.avatar) {
            pl('#addcommentavatar').css('background-image', 'url(' + this.loggedin_profile.avatar + ')');
        }
        pl('#addcommentbox').addClass('bound');
    },

    displayAddCommentBox: function() {
        this.bindAddCommentBox();
        pl('#addcommentbox').show();
    },

    displayNoComments: function() {
        if (this.loggedin_profile_id) {
            pl('#commentmsg').html('<p>Be the first to comment!</p>').show();
        }
        else {
            pl('#commentmsg').html('<p>Login and be the first to comment!</p>');
        }
    },

    displayComments: function() {
        var self = this,
            html = '',
            i,
            comment,
            deletesel,
            firstchild,
            bindlist = [];
        if (self.commentlist.length === 0) {
            self.displayNoComments();
            return;
        }
        for (i = 0; i < self.commentlist.length; i++) {
            comment = self.commentlist[i];
            html += self.makeComment(comment);
            bindlist.push(comment);
        }
        pl('#addcommentbox').before(html);
        for (i = 0; i < bindlist.length; i++) {
            comment = bindlist[i];
            self.bindComment(comment);
        }
    },

    makeComment: function(comment) {
        var avatarstyle = comment.profile_avatar
                ? ' style="background-image: url(' + comment.profile_avatar + ')"'
                : '',
            isowner = comment.profile_id === this.listing.profile_id,
            ownermarker = isowner ? '<span class="commentowner">Owner</span> ' : '',
            usertext = '<span class="commentusername">' + comment.profile_username + '</span>',
			userclasstext =  comment.profile_user_class
                ? '<span class="profilelistuserclass">' + comment.profile_user_class + '</span>'
                : '',
            datetext = '<span class="commentinlinedate">'
                + (comment.ago_text || DateClass.prototype.agoText(comment.comment_date)) + '</span>',
            deletable = comment.profile_id === this.loggedin_profile_id,
            deletemsgspan = deletable
                ? ' <p id="comment_delete_msg_' + comment.comment_id
                    + '" class="span-8 commentdeletemsg inputmsg successful"></span>'
                : '',
            deleteicon = deletable
                ? '<div id="comment_delete_' + comment.comment_id + '" class="commentdeleteicon hoverlink"></div>'
                    + '<div class="commentdeletespinner preloadericon initialhidden"'
                    + ' id="comment_deletespinner_' + comment.comment_id + '"></div>'
                : '',
            text = HTMLMarkup.prototype.stylize(SafeStringClass.prototype.htmlEntities(comment.text));
        return '\
        <div class="commentline" id="comment_' + comment.comment_id + '">\
            <div class="commentavatar"' + avatarstyle + '></div>\
            <div class="commentheaderline">\
                ' + ownermarker + usertext + userclasstext + ' ' + datetext + deleteicon + deletemsgspan + '\
            </div>\
            <p class="commenttext">'
                  + text + '\
            </p>\
        </div>\
        ';
    },

    deleteComment: function() {
        var commentid = this.id.replace('comment_delete_', '');
            completefunc = function(json) {
                pl('#comment_'+commentid).remove();
            },

            url = '/listing/delete_comment?id=' + commentid,
            deletemsgid = 'comment_delete_msg_' + commentid,
            ajax = new AjaxClass(url, deletemsgid, completefunc);
        pl('#comment_delete_' + commentid).hide();
        pl('#comment_deletespinner_' + commentid).show();
        ajax.setPost();
        ajax.call();
    },

    bindComment: function(comment) {
        if (comment.profile_id === this.loggedin_profile_id) {
            pl('#comment_delete_' + comment.comment_id).bind('click', this.deleteComment);
        }
    }
});

(new CommentClass()).load();
