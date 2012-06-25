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
        this.commentlist = json.comments || [];
    },
    display: function(json) {
        if (json) {
            this.store(json);
        }
        this.displayComments();
        if (this.loggedin_profile_id) {
            this.displayAddCommentBox();
        }
        else {
            pl('#addcommentbox').before('<div class="messageline"><p style="font-weight: bold;">Login to post a comment</p></div>');
        }
        pl('#commentswrapper').show();
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
                var completeFunc = function(json) {
                        var numcomments = 1 * pl('#num_comments').text(),
                            comment = json;
                        pl('#addcommenttext').removeClass('edited').attr({value: 'Put your comment here...'});
                        pl('#addcommentbtn').removeClass('editenabled');
                        pl('#commentmsg').html('Comment posted');
                        pl('#num_comments').text(numcomments + 1);
                        pl('#addcommentbox').before(self.makeComment(comment));
                        self.bindComment(comment);
                    },
                    text = SafeStringClass.prototype.clean(pl('#addcommenttext').attr('value')),
                    data = {
                        comment: {
                            listing_id: self.listing_id,
                            text: SafeStringClass.prototype.htmlEntities(text)
                        }
                    },
                    ajax = new AjaxClass('/listing/post_comment', 'commentmsg', completeFunc);
                if (pl('#addcommentbtn').hasClass('editenabled') && text) {
                    ajax.setPostData(data);
                    ajax.call();
                }
                return false;
            }
        });
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
        var text = SafeStringClass.prototype.htmlEntities(comment.text),
            datetext = DateClass.prototype.format(comment.comment_date),
            deletable = comment.profile_id === this.loggedin_profile_id,
            deletemsgspan = deletable ? ' <span id="comment_delete_msg_' + comment.comment_id + '" class="push-4 span-14 messagetext inputmsg successful"></span>' : '',
            deleteicon = deletable ? '<div id="comment_delete_' + comment.comment_id + '" class="commentdeleteicon checkboxredicon hoverlink"></div>' : '';
        return '\
        <div class="messageline" id="comment_' + comment.comment_id + '">\
            <p class="messageuser span-4">' + comment.profile_username + '</p>\
            <p class="messagetext span-14">\
                ' + text + '\
            </p>\
            <p class="messagedate">'+  datetext + '</p>\
                ' + deleteicon + '\
                ' + deletemsgspan + '\
        </div>\
        ';
    },
    deleteComment: function() {
        var commentid = this.id.replace('comment_delete_', '');
            completefunc = function(json) {
                var numcomments = pl('#num_comments').text() - 1;
                pl('#comment_'+commentid).remove();
                pl('#num_comments').text(numcomments);
                if (numcomments === 0) {
                    pl('#commentmsg').html('<p>Be the first to comment!</p>');
                }
            },
            url = '/listing/delete_comment?id=' + commentid,
            deletemsgid = 'comment_delete_msg_' + commentid,
            ajax = new AjaxClass(url, deletemsgid, completefunc);
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
