function APIPageClass() {}
pl.implement(APIPageClass, {
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass(),
                    listing = json.listings && json.listings[0],
                    listingid = listing && listing.listing_id,
                    profileid = listing && listing.profile_id,
                    profileusername = listing && listing.profile_username,
                    listingfunc = function(json) {
                        var listing = json.listing,
                            fileid = listing && (listing.business_plan_id || listing.presentation_id || listing.financials_id);
                        if (fileid) {
                            pl('.fileid').attr({value: fileid});
                        }
                    },
                    createfunc = function(json) {
                        var listing = json.listing,
                            uploadurl = listing && (listing.business_plan_upload || listing.presentation_upload || listing.financials_upload);
                        if (uploadurl) {
                            pl('.uploadurl').attr({action: uploadurl});
                        }
                    },
                    commentsfunc = function(json) {
                        var comment = json.comments && json.comments[0],
                            commentid = comment && comment.comment_id;
                        if (commentid) {
                            pl('.commentid').attr({value: commentid});
                        }
                    },
                    questionsfunc = function(json) {
                        var question = json.qanda && json.qanda[0],
                            questionid = question && question.question_id;
                        if (questionid) {
                            pl('.questionid').attr({value: questionid});
                            pl('.answerobj').attr({value: '{ question_id: "' + questionid + '", text: "put your answer here" }'});
                        }
                    },
                    listingajax = listingid && new AjaxClass('/listing/get/' + listingid, 'loadmsg', listingfunc),
                    createajax = listingid && new AjaxClass('/listing/create', 'loadmsg', createfunc),
                    commentsajax = listingid && new AjaxClass('/listing/comments/' + listingid, 'loadmsg', commentsfunc),
                    questionsajax = listingid && new AjaxClass('/listing/questions_and_answers/' + listingid, 'loadmsg', questionsfunc);
                header.setLogin(json);
                pl('#loadmsg').html('');
                if (listingid) {
                    pl('.listingid').attr({value: listingid});
                    pl('.commentobj').attr({value: '{ listing_id: "' + listingid + '", text: "put your comment here" }'});
                    pl('.askobj').attr({value: '{ listing_id: "' + listingid + '", text: "put your question here" }'});
                }
                if (profileid) {
                    pl('.profileid').attr({value: profileid});
                    pl('.sendobj').attr({value: '{ profile_id: "' + profileid + '", text: "put your message here" }'});
                }
                if (profileusername) {
                    pl('.profileusername').attr({value: profileusername});
                }
                pl('.apipanel dt').bind({
                    click: function() {
                        var detail = pl(this.nextSibling.nextSibling.nextSibling.nextSibling),
                            cls = 'apidetaildisplay';
                        detail.hasClass(cls) ? detail.removeClass(cls) : detail.addClass(cls);
                        return false;
                    }
                });
                pl('.apidetail form input[type=submit]').bind({
                    click: function() {
                        var iframe = this.parentNode.nextSibling.nextSibling;
                        if (!iframe) {
                            iframe = this.parentNode.parentNode.parentNode.nextSibling.nextSibling;
                        }
                        pl(iframe).addClass('apidetailframe');
                    }
                });
                if (SyntaxHighlighter) {
                    SyntaxHighlighter.all()
                }
                listingajax.call();
                commentsajax.call();
                questionsajax.call();
                createajax.setPostData();
                createajax.call();
            },
            ajax = new AjaxClass('/listing/top/', 'loadmsg', completeFunc);
        ajax.ajaxOpts.data = { max_results: 1 };
        ajax.call();
    }
});

(new APIPageClass()).loadPage();
