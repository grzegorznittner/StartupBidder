function CommentsClass(listing_id) {
    var self = this;
    this.listing_id = listing_id;
    this.url = '/comments/listing/' + this.listing_id;
    this.statusId = 'commentsmsg';
    this.completeFunc = function(json) {
        self.store(json);
        self.display();
    };
    this.ajax = new AjaxClass(this.url, this.statusId, this.completeFunc);
    this.date = new DateClass();
    this.safeStr = new SafeStringClass();
}
pl.implement(CommentsClass, {
    load: function() {
        this.ajax.call();
    },
    store: function(json) {
        this.comments = json.comments || [];
        this.loggedin_profile_id = json.loggedin_profile ? json.loggedin_profile.profile_id : null;
    },
    display: function() {
        if (this.loggedin_profile_id) {
            this.displayAddCommentBox();
        }
        this.displayComments();
    },
    bindAddCommentBox: function() {
        var self = this;
        pl('#addcommenttext').bind({
            focus: function() {
                if (!pl('#addcommenttext').hasClass('edited')) {
                    pl('#addcommenttext').attr({value: ''});
                    pl('#addcommentmsg').html('&nbsp;');
                }
            },
            keyup: function() {
                if (!pl('#addcommenttext').hasClass('edited')) {
                    pl('#addcommenttext').addClass('edited');
                    pl('#addcommentmsg').html('&nbsp;');
                }
                else if (pl('#addcommenttext').attr('value').length >= 5 && !pl('#addcommentbtn').hasClass('addcommentenabled')) {
                    pl('#addcommentbtn').addClass('addcommentenabled');
                }
                else if (pl('#addcommenttext').attr('value').length < 5 && pl('#addcommentbtn').hasClass('addcommentenabled')) {
                    pl('#addcommentbtn').removeClass('addcommentenabled');
                }
                return false;
            },
            blur: function() {
                if (!pl('#addcommenttext').hasClass('edited')) {
                    pl('#addcommenttext').attr({value: 'Put your comment here...'});
                    pl('#addcommentbtn').removeClass('addcommentenabled');
                }
            }
        });
        pl('#addcommentbtn').bind({
            click: function(event) {
                var completeFunc, commentText, ajax;
                if (!pl('#addcommentbtn').hasClass('addcommentenabled')) {
                    return false;
                }
                completeFunc = function() {
                    pl('#addcommenttext').removeClass('edited').attr({value: 'Put your comment here...'});
                    pl('#addcommentbtn').removeClass('addcommentenabled');
                    pl('#addcommentmsg').html('Comment posted');
                    self.ajax.call();
                };
                commentText = SafeStringClass.prototype.clean(pl('#addcommenttext').attr('value'));
                ajax = new AjaxClass('/comment/create', 'addcommentmsg', completeFunc);
                ajax.setPostData({
                    comment: {
                        listing_id: self.listing_id,
                        profile_id: self.loggedin_profile_id,
                        text: commentText
                    }
                });
                ajax.call();
                return false;
            }
        });
        pl('#addcommentbox').addClass('bound');
    },
    displayAddCommentBox: function() {
        var self = this;
        if (!pl('#addcommentbox').hasClass('bound')) {
            this.bindAddCommentBox();
        }
        pl('#addcommenttitle,#addcommentbox').show();
    },
    displayComments: function() {
        var html, deletableComments, i, comment, deletable, commentDeleteSel;
        pl('#num_comments').text(this.comments.length);
        if (this.comments.length === 0) {
            pl('#commentsmsg').html('<p>Be the first to comment!</p>').show();
            return;
        }
        html = '';
        deletableComments = [];
        for (i = 0; i < this.comments.length; i++) {
            comment = this.comments[i];
            deletable = false;
            if (comment.profile_id === this.loggedin_profile_id) {
                deletableComments.push(comment);
                deletable = true;
            }
            html += this.makeComment(comment, deletable);
        }
        pl('#commentlist').html(html).show();
        for (i = 0; i < deletableComments.length; i++) {
            comment = this.comments[i];
            commentDeleteSel = '#comment_delete_' + comment.comment_id;
            pl(commentDeleteSel).bind({click: this.deleteCommentGenerator(comment.comment_id)});
        }
    },
    deleteCommentGenerator: function(commentId) {
        return function() {
            var successFunc = function(json) {
                    pl('#comment_'+commentId).remove();
                    pl('#comment_dd_'+commentId).remove();
                    pl('#num_comments').text(pl('#commentlist').get(0).childNodes.length / 2);
                    if (!pl('#commentlist').get(0).hasChildNodes()) {
                        pl('#commentsmsg').html('<p>Be the first to comment!</p>');
                    }
                },
                ajax = new AjaxClass('/comment/delete?id='+commentId, 'comment_delete_msg_'+commentId, null, successFunc);
            ajax.setPost();
            ajax.call();
        };
    },
    makeComment: function(comment, deletable) {
        return '\
<dt id="comment_' + comment.comment_id + '">\
<div class="commentdttitle">\
<div class="commentdttitleline">Posted by ' + comment.profile_username + ' on ' + this.date.format(comment.comment_date) + '\
    ' + (deletable ? ' <span id="comment_delete_msg_' + comment.comment_id + '"></span>' : '') + '\
</div>\
' + (deletable ? '<div id="comment_delete_' + comment.comment_id + '" class="commentdelete checkboxredicon"></div>' : '') + '\
</div>\
</dt>\
<dd id="comment_dd_' + comment.comment_id + '">' + this.safeStr.htmlEntities(comment.text) + '</dd>\
';
    }
});


