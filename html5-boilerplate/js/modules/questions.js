function QuestionClass(listing_id, loggedin_profile_id) {
    this.listing_id = listing_id;
    this.loggedin_profile_id = loggedin_profile_id;
}
pl.implement(QuestionClass, {
    load: function() {
        var self = this,
            complete = function(json) {
                self.display(json);
            };
        (new AjaxClass('/listings/questions_and_answers/' + this.listing_id, 'qandamsg', complete)).call();
    },
    store: function(json) {
        var question,
            i;
        this.questionlist = json.qanda || [];
        for (i = 0; i < this.questionlist.length; i++) {
            question = this.questionlist[i];
        }
    },
    display: function(json) {
        if (json) {
            this.store(json);
        }
        if (this.loggedin_profile_id) {
            this.displayAddQuestionBox();
        }
        if (this.questionlist.length === 0 && this.loggedin_profile_id) {
            pl('#qandamsg').html('<p>Be the first to ask a question!</p>').show();
        }
        else if (this.questionlist.length === 0 && !this.loggedin_profile_id) {
            pl('#qandamsg').html('<p>Login and be the first to ask a question!</p>').show();
        }
        else {
            this.displayQuestions();
        }
    },
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
                var val = SafeStringClass.prototype.htmlEntities(SafeStringClass.prototype.clean(pl('#addqandatext').attr('value'))),
                    completeFunc = function(json) {
                        var numitems = pl('#num_qandas').text(),
                            html = self.makeQuestion(json);
                        pl('#addqandatext').removeClass('edited').attr({value: 'Put your question here...'});
                        pl('#addqandabtn').removeClass('editenabled');
                        pl('#addqandamsg').html('question posted');
                        pl('#addqandabox').before(html);
                    },
                    data = {
                        message: {
                            listing_id: self.listing_id,
                            text: val
                        }
                    },
                    ajax = new AjaxClass('/listing/ask_owner', 'addqandamsg', completeFunc);
                if (!val || (val && !val.length > 5) || !pl('#addqandabtn').hasClass('editenabled')) {
                    return false;
                }
                ajax.setPostData(data);
                ajax.call();
                return false;
            }
        });
    },
    bindReplyGenerator: function(questionid) {
        var self = this,
            boxsel = '#qanda_replybox_' + questionid,
            textsel = '#qanda_replytext_' + questionid,
            msgsel = '#qanda_replymsg_' + questionid,
            btnsel = '#qanda_replysendbtn_' + questionid;
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
                    var completeFunc = function(json) {
                            var numitems = pl('#num_qandas').text();
                            pl(textsel).removeClass('edited').attr({value: 'Put your reply here...'});
                            pl(btnsel).removeClass('editenabled');
                            pl(msgsel).html('question posted');
                            pl('#num_qandas').text(numitems + 1);
                            pl('#qanda_replyanswer_' + json.question_id).text(json.answer);
                            pl('#qanda_replyanswerdate_' + json.question_id).text(DateClass.prototype.format(json.answer_date));
                            pl('#qanda_replyanswerbox_' + json.question_id);
                        },
                        data = {
                            answer: {
                                question_id: questionid,
                                text: SafeStringClass.prototype.htmlEntities(SafeStringClass.prototype.clean(pl(textsel).attr('value')))
                            }
                        },
                        ajax = new AjaxClass('/listing/answer_question', 'qanda_replymsg_' + questionid, completeFunc);
                    if (!val || !pl(btnsel).hasClass('editenabled')) {
                        return false;
                    }
                    ajax.setPostData(data);
                    ajax.call();
                    return false;
                }
            });
        };
    },
    displayAddQuestionBox: function() {
        this.bindAddQuestionBox();
        pl('#addqandabox').show();
    },
    displayQuestions: function() {
        var self = this,
            html = '',
            i,
            question,
            islistingowner,
            replyable,
            bindlist = [];
        for (i = 0; i < self.questionlist.length; i++) {
            question = self.questionlist[i];
            islistingowner = self.loggedin_profile_id && self.loggedin_profile_id === question.listing_owner;
            isaddressedtome = self.loggedin_profile_id && question.user_id && self.loggedin_profile_id === question.user_id;
            replyable = isaddressedtome && islistingowner && !question.answer;
            html += self.makeQuestion(question, replyable);
            bindlist.push([question, replyable]);
        }
        pl('#addqandabox').before(html);
        for (i = 0; i < bindlist.length; i++) {
            question = bindlist[i][0];
            replyable = bindlist[i][1];
            if (replyable) {
                self.bindQuestion(question);
            }
        }
    },
    makeQuestion: function(question, replyable) {
        var self = this,
            questionid = question.question_id,
            questionfrom = question.from_user_nickname,
            questionreplybtn = replyable ? '<div class="smallinputbutton darkblue questionreplybtn hoverlink" id="qanda_replybtn_' + questionid + '">REPLY</div>' : '',
            questiondate = DateClass.prototype.format(question.create_date),
            questiontext = question.text_2,
            questionreplybox = replyable ? '\
                <div class="droptransition questionreply" id="qanda_replybox_' + questionid + '">\
                    <textarea class="textarea messagetextarea" id="qanda_replytext_' + questionid + '" name="relpytext" cols="20" rows="5">Put your reply here...</textarea>\
                    <span class="span-12 inputmsg successful" id="qanda_replymsg_' + questionid + '">&nbsp;</span>\
                    <span class="span-3 inputbutton" id="qanda_replysendbtn_' + questionid + '">SEND</span>\
                </div>\
            ' : '',
            answertext;
            if (question.answer) {
                answertext = '<p style="font-weight:bold;">Answered by owner on ' + DateClass.prototype.format(question.answer_date) + ':</p><p style="font-weight:normal;">' + question.answer + '</p>';
            }
            else {
                answertext
                    = '<p style="font-weight:bold; display: none;" id="#qanda_replyanswerbox_' + questionid + '">'
                    + 'Answered by owner on <span id="#qanda_replyanswerdate_' + questionid
                    + ':</p><p style="font-weight:normal;" id="#qanda_replyanswer_' + questionid + '"></p>';
            }
        return '\
<dt id="qanda_' + questionid + '">\
    <div class="questiondttitle">\
        <div class="questiondttitleline">Question from ' + questionfrom + ' on ' + questiondate + '\
        </div>\
        ' + questionreplybtn + '\
    </div>\
</dt>\
<dd id="qanda_dd_' + questionid + '">\
    ' + questiontext + answertext + questionreplybox + '\
</dd>\
';
    },
    showReplyBox: function() {
        var questionid = this.id.replace('#qanda_replybtn_', ''),
            questionreplyboxsel = '#qanda_replybox_' + questionid;
        pl(questionreplyboxsel).addClass('questionreplyshow');
    },
    bindQuestion: function(question) {
        var self = this,
            questionid = question.notify_id,
            boxbinder = self.bindReplyGenerator(questionid);
            pl('#qanda_replybtn_' + questionid).bind('click', this.showReplyBox);
            boxbinder();
    }
});

