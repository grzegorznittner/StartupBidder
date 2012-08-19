function QuestionClass() {
    var queryString = new QueryStringClass();
    this.listing_id = queryString.vars.id;
}
pl.implement(QuestionClass, {
    load: function() {
        var self = this,
            complete = function(json) {
                var header = new HeaderClass(),
                    companybanner = new CompanyBannerClass('questions');
                CollectionsClass.prototype.merge(self, json);
                if (!json.listing) {
                    json.listing = {};
                    json.listing.listing_id = self.listing_id;
                }
                header.setLogin(json);
                self.listing_owner_id = json.listing && json.listing.profile_id;
                self.loggedin_profile_id = json.loggedin_profile && json.loggedin_profile.profile_id;
                companybanner.display(json);
                self.display(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
            },

            error = function(errornum, json) {
                (new HeaderClass()).setLogin(json);
                pl('.preloader').hide();
                pl('.errorwrapper').show();
            },

            ajax = new AjaxClass('/listings/questions_answers/' + this.listing_id, 'qandamsg', complete, null, null, error);
        //ajax.setGetData({ max_results: 3 });
        ajax.call();
    },

    store: function(json) {
        this.questionlist = json.questions_answers || [];
        this.listing = json.listing || {};
        this.loggedin_profile_id = json.loggedin_profile && json.loggedin_profile.profile_id;
        this.more_results_url = this.questionlist.length > 0 && json.questions_answers_props && json.questions_answers_props.more_results_url;
    },

    display: function(json) {
        if (json) {
            this.store(json);
        }

        this.displayQuestions();

        if (this.more_results_url) {
            this.displayMoreResults();
        }

        if (this.listing.status !== 'active') {
            this.displayInactive();
        }
        else if (this.loggedin_profile) {
            this.displayLoggedIn();
        }
        else {
            this.displaySignIn();
        }
    
        pl('#qandaswrapper').show();

        if (this.more_results_url) {
            this.bindMoreResults();
        }
    },

    displayMoreResults: function() {
        pl('#addqandabox').after('<div class="showmore hoverlink" id="moreresults"><span class="initialhidden" id="moreresultsurl">'
            + this.more_results_url + '</span><span id="moreresultsmsg">More...</span></div>\n');
    },

    displaySignIn: function() {
        pl('#addqandabox').before('\
    <div class="messageline"><p style="font-weight: bold;">Sign in to ask a question</p></div>\
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
    },

    displayInactive: function() {
        pl('#addqandabox').before('<div class="messageline"><p style="font-weight: bold;">'
            + '&nbsp;Questions cannot be asked or answered for this listing because it is not active</p></div>');
    },

    displayLoggedIn: function() {
        var isowner = this.loggedin_profile_id === this.listing_owner_id;
        if (!isowner) {
            this.displayAddQuestionBox();
        }
        else if (isowner && !this.questionlist.length) {
            pl('#addqandabox').before('<div class="messageline"><p style="font-weight: bold;">'
                + '&nbsp;No one has yet asked any questions for you to answer</p></div>');
        }
    },

    displayLoggedOut: function() {
        var nexturl = '/company-questions-page.html?id=' + this.listing_id;
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


    // GREG START
    bindMoreResults: function() {
        var self = this;
        pl('#moreresults').bind({
            click: function() {
                var completeFunc = function(json) {
                        if (!json.listing) {
                            json.listing = {};
                            json.listing.listing_id = self.listing_id;
                        }
                        var html = '',
                        i,
                        question,
                        replyable,
                        bindlist = [];
                        questionlist = json.questions_answers || [];
                        listing_owner_id = json.listing && json.listing.profile_id;
                        loggedin_profile_id = json.loggedin_profile && json.loggedin_profile.profile_id;
                        more_results_url = questionlist.length > 0 && json.questions_answers_props && json.questions_answers_props.more_results_url;
                        
                        if (questionlist.length === 0) {
	                        return;
	                    }
	                    for (i = 0; i < questionlist.length; i++) {
	                        question = questionlist[i];
	                        replyable = loggedin_profile_id && loggedin_profile_id === listing_owner_id && !question.answer;
	                        html += self.makeQuestion(question, replyable);
	                        bindlist.push([question, replyable]);
	                    }
	                    if (html) {
                            pl('#addqandabox').before(html);
                        }
                        if (more_results_url) {
                            pl('#moreresultsurl').text(more_results_url);
                            pl('#moreresultsmsg').text('More...');
                        }
                        else {
                            pl('#moreresultsmsg').text('');
                            pl('#moreresults').removeClass('hoverlink').unbind();
                        }
	                    for (i = 0; i < bindlist.length; i++) {
	                        question = bindlist[i][0];
	                        replyable = bindlist[i][1];
	                        if (replyable) {
	                        	self.bindQuestion(question);
	                        }
	                    }
                    },

                    more_results_url = pl('#moreresultsurl').text(),
                    ajax,
                    data,
                    i;
                if (more_results_url) {
                    ajax = new AjaxClass(more_results_url, 'moreresultsmsg', completeFunc);
                    ajax.setGetData(data);
                    ajax.call();
                }
                else {
                    pl('#moreresultsmsg').text('');
                    pl('#moreresults').removeClass('hoverlink').unbind();
                }
            }
        });
    },

    // GREG END
    bindAddQuestionBox: function() {
        var self = this;
        pl('#addqandatext').bind({
            focus: function() {
                if (!pl('#addqandatext').hasClass('edited')) {
                    pl('#addqandatext').attr({value: ''});
                    pl('#addqandamsg').html('&nbsp;');
                }
            },

            keyup: function() {
                var val = pl('#addqandatext').attr('value');
                if (!pl('#addqandatext').hasClass('edited')) {
                    pl('#addqandatext').addClass('edited');
                    pl('#addqandamsg').html('&nbsp;');
                }
                else if (val && val.length >= 3) {
                    pl('#addqandabtn').addClass('editenabled');
                }
                else if (!val || (val && val.length < 3)) {
                    pl('#addqandabtn').removeClass('editenabled');
                }
                return false;
            },

            blur: function() {
                if (!pl('#addqandatext').hasClass('edited')) {
                    pl('#addqandatext').attr({value: 'Put your question here...'});
                    pl('#addqandabtn').removeClass('editenabled');
                }
            }
        });
        pl('#addqandabtn').bind({
            click: function(event) {
                var val = SafeStringClass.prototype.clean(pl('#addqandatext').attr('value')),
                    complete = function(json) {
                        pl('#addqandaspinner').hide();
                        pl('#addqandatext').removeClass('edited').removeAttr('disabled').attr({value: 'Put your question here...'});
                        pl('#addqandabtn').removeClass('editenabled').show();
                        pl('#qandamsg').text('');
                        pl('#addqandabox').before(self.makeQuestion(json));
                    },

                    text = SafeStringClass.prototype.clean(pl('#addqandatext').attr('value')),

                    data = {
                        message: {
                            listing_id: self.listing_id,
                            text: text
                        }
                    },


                    ajax = new AjaxClass('/listing/ask_owner', 'qandamsg', complete);
                if (pl('#addqandabtn').hasClass('editenabled') && text) {
                    pl('#addqandabtn').hide();
                    pl('#addqandatext').attr('disabled', 'disabled');
                    pl('#addqandaspinner').show();
                    ajax.setPostData(data);
                    ajax.call();
                }
                return false;
            }
        });
        pl('#myusername').text(this.loggedin_profile && this.loggedin_profile.username);
        if (this.loggedin_profile && this.loggedin_profile.avatar) {
            pl('#addqandaavatar').css('background-image', 'url(' + this.loggedin_profile.avatar + ')');
        }
    },


    bindReplyGenerator: function(questionid) {
        var self = this,
            textsel = '#qanda_replytext_' + questionid,
            msgsel = '#qanda_replymsg_' + questionid,
            btnsel = '#qanda_replysendbtn_' + questionid,
            spinnersel = '#qanda_replyspinner_' + questionid;
        return function() {
            pl(textsel).bind({
                focus: function() {
                    if (!pl(textsel).hasClass('edited')) {
                        pl(textsel).attr({value: ''});
                        pl(msgsel).html('&nbsp;');
                    }
                },

                keyup: function() {
                    var val = pl(textsel).attr('value');
                    if (!pl(textsel).hasClass('edited')) {
                        pl(textsel).addClass('edited');
                        pl(msgsel).html('&nbsp;');
                    }
                    else if (val && val.length >= 3) {
                        pl(btnsel).addClass('editenabled');
                    }
                    else if (!val || (val && val.length < 3)) {
                        pl(btnsel).removeClass('editenabled');
                    }
                    return false;
                },

                blur: function() {
                    if (!pl(textsel).hasClass('edited')) {
                        pl(textsel).attr({value: 'Put your question here...'});
                        pl(btnsel).removeClass('editenabled');
                    }
                }
            });
            pl(btnsel).bind({
                click: function(event) {
                    var val = pl(textsel).attr('value'),
                        completeFunc = function(json) {
                            var question_id = json.question_id,
                                answer = HTMLMarkup.prototype.stylize(SafeStringClass.prototype.htmlEntities(json.answer));
                            pl('#qanda_replyspinner_' + question_id).hide();
                            pl('#qandatitlemsg').text('');
                            pl('#qanda_replyanswer_' + question_id).html(answer);
                            pl('#qanda_replybox_' + question_id).remove();
                            pl('#qandamsg').text('');
                        },


                        data = {
                            message: {
                                question_id: questionid,
                                text: SafeStringClass.prototype.clean(pl(textsel).attr('value'))
                            }
                        },

                        ajax = new AjaxClass('/listing/answer_question', 'qanda_replymsg_' + questionid, completeFunc);
                    if (val && pl(btnsel).hasClass('editenabled')) {
                        pl(btnsel).hide();
                        pl(textsel).attr('disabled', 'disabled');
                        pl(spinnersel).show();
                        ajax.setPostData(data);
                        ajax.call();
                    }
                    return false;
                }
            });
        };
    },

    displayAddQuestionBox: function() {
        this.bindAddQuestionBox();
        pl('#addqandabox').show();
    },

    displayNoComments: function() {
        if (this.loggedin_profile_id) {
            pl('#qandamsg').show();
        }
        else {
            pl('#qandamsg').html('<p>Login to ask the owner a question</p>');
        }
    },

    displayQuestions: function() {
        var html = '',
            i,
            question,
            replyable,
            bindlist = [];
        if (this.questionlist.length === 0) {
            this.displayNoComments();
            return;
        }
        for (i = 0; i < this.questionlist.length; i++) {
            question = this.questionlist[i];
            replyable = this.loggedin_profile_id && this.loggedin_profile_id === this.listing_owner_id && !question.answer;
            html += this.makeQuestion(question, replyable);
            bindlist.push([question, replyable]);
        }
        pl('#addqandabox').before(html);
        for (i = 0; i < bindlist.length; i++) {
            question = bindlist[i][0];
            replyable = bindlist[i][1];
            if (replyable) {
                this.bindQuestion(question);
            }
        }
    },

    makeQuestion: function(question, replyable) {
        var avatarstyle = question.from_user_avatar
                ? ' style="background-image: url(' + question.from_user_avatar + ')"'
                : '',
            usertext = '<span class="commentusername">' + question.from_user_nickname + '</span>',
			userclasstext =  question.from_user_class
                ? '<span class="profilelistuserclass">' + question.from_user_class + '</span>'
                : '',
            datetext = '<span class="commentinlinedate">'
                + (question.ago_text || DateClass.prototype.agoText(question.create_date)) + '</span>',
            questionreplybtn = replyable
                ? '<div class="inputbutton darkblue questionreplybtn span-3 hoverlink" id="qanda_replybtn_' + question.question_id + '">ANSWER</div>'
                    + '<div class="commentdeletespinner preloadericon initialhidden"'
                    + ' id="qanda_replyspinner_' + question.question_id + '"></div>'
                : '',
            questiontext = HTMLMarkup.prototype.stylize(SafeStringClass.prototype.htmlEntities(question.question)),
            questionline = '<span class="messagequestion">' + questiontext + '</span>',
            questionreplybox = replyable ? '\
                <div class="questionreplyline droptransition" id="qanda_replybox_' + question.question_id + '">\
                    <p class="messageuser messagereplyuser span-4"></p>\
                    <textarea class="textarea messagetextarea messagereplytextarea" id="qanda_replytext_' + question.question_id+ '"\
                        name="messagetext" cols="20" rows="5">Put your answer here...</textarea>\
                    <span class="span-3 inputbutton messagebutton messagereplybutton" id="qanda_replysendbtn_' + question.question_id + '">ANSWER</span>\
                    <p class="messagereplymsg inputmsg successful" id="qanda_replymsg_' + question.question_id + '"></p>\
                </div>\
            ' : '',
            answertext = HTMLMarkup.prototype.stylize(SafeStringClass.prototype.htmlEntities(question.answer)),
            replyblock = question.answer
                ? '<span class="messageanswer">' + answertext + '</span>'
                : '<span class="messageanswer" id="qanda_replyanswer_' + question.question_id + '">Not yet answered by owner</span>';
        return '<div class="commentline" id="qanda_' + question.question_id + '">'
                + '<div class="commentavatar"' + avatarstyle + '></div>'
                + '<div class="commentheaderline">' + usertext + userclasstext + ' ' + datetext + '</div>'
                + '<p class="commenttext">' + questionline + '</p>'
                + '<p class="commenttext">' + replyblock + '</p>'
                + questionreplybtn
                + questionreplybox
            + '</div>';
    },

    showReplyBox: function() {
        var questionid = this.id.replace('qanda_replybtn_', '');
        pl('#qanda_replybox_' + questionid).addClass('questionreplylineshow');
        pl('#qanda_replybtn_' + questionid).hide();
        pl('#qanda_replyanswer_' + questionid).text('');
    },

    bindQuestion: function(question) {
        pl('#qanda_replybtn_' + question.question_id).bind('click', this.showReplyBox);
        (this.bindReplyGenerator(question.question_id))();
    }
});

