function QuestionClass(listing_id, listing_owner_id, loggedin_profile_id) {
    this.listing_id = listing_id;
    this.listing_owner_id = listing_owner_id;
    this.loggedin_profile_id = loggedin_profile_id;
}
pl.implement(QuestionClass, {
    load: function() {
        var self = this,
            complete = function(json) {
                console.log(json);
                self.display(json);
            },
            ajax = new AjaxClass('/listings/questions_and_answers/' + this.listing_id, 'qandamsg', complete);
        ajax.setGetData({ max_results: 20 });
        ajax.call();
    },
    store: function(json) {
        this.questionlist = json || [];
    },
    display: function(json) {
        if (json) {
            this.store(json);
        }
        this.displayQuestions();
        if (this.loggedin_profile_id && this.loggedin_profile_id !== this.listing_owner_id) {
            this.displayAddQuestionBox();
        }
        else if (this.loggedin_profile_id && this.loggedin_profile_id === this.listing_owner_id) {
            pl('#addqandabox').before('<div class="messageline"><p style="font-weight: bold;">Questions are private until answered, then they are displayed pubilcly</p></div>');
        }
        else {
            pl('#addqandabox').before('<div class="messageline"><p style="font-weight: bold;">Login to ask a question</p></div>');
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
                var val = SafeStringClass.prototype.clean(pl('#addqandatext').attr('value')),
                    completeFunc = function(json) {
                        console.log(json);
                        var html = self.makeQuestion(json);
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
            //boxsel = '#qanda_replybox_' + questionid,
            textsel = '#qanda_replytext_' + questionid,
            msgsel = '#qanda_replymsg_' + questionid,
            btnsel = '#qanda_replysendbtn_' + questionid;
        console.log(textsel);
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
                            var numitems = 1 * pl('#num_qandas').text(),
                                answer = SafeStringClass.prototype.htmlEntities(json.answer),
                                answerdate = DateClass.prototype.format(json.answer_date);
                            pl('#num_qandas').addClass('successful').text(numitems + 1);
                            pl('#qandatitlemsg').text('question posted');
                            pl('#qanda_answertext_' + json.question_id).text(answer);
                            pl('#qanda_answerdate_' + json.question_id).text(answerdate);
                            pl('#qanda_answerbox_' + json.question_id).show();
                            pl('#qanda_replybox_' + json.question_id).remove();
                        },
                        data = {
                            message: {
                                question_id: questionid,
                                text: SafeStringClass.prototype.clean(pl(textsel).attr('value'))
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
    displayNoComments: function() {
        if (this.loggedin_profile_id) {
            pl('#qandamsg').html('<p>Be the first to ask a question!</p>').show();
        }
        else {
            pl('#qandamsg').html('<p>Login and be the first to ask a question!</p>');
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
        console.log(question, replyable);
        var self = this,
            questionreplybtn = replyable ? '<div class="inputbutton darkblue questionreplybtn span-3 hoverlink" id="qanda_replybtn_' + question.question_id + '">ANSWER</div>' : '',
            questiondate = DateClass.prototype.format(question.create_date),
            answerdate = DateClass.prototype.format(question.answer_date),
            questiontext = SafeStringClass.prototype.htmlEntities(question.question),
            answertext = SafeStringClass.prototype.htmlEntities(question.answer),
            questionreplybox = replyable ? '\
                <div class="questionreplyline droptransition" id="qanda_replybox_' + question.question_id + '">\
                    <p class="messageuser messagereplyuser span-4"></p>\
                    <textarea class="textarea messagetextarea messagereplytextarea" id="qanda_replytext_' + question.question_id+ '"\
                        name="messagetext" cols="20" rows="5">Put your answer here...</textarea>\
                    <span class="span-3 inputbutton messagebutton messagereplybutton" id="qanda_replysendbtn_' + question.question_id + '">ANSWER</span>\
                    <p class="messagereplymsg inputmsg successful" id="qanda_replymsg_' + question.question_id + '"></p>\
                </div>\
            ' : '',
            questiondateblock = '<div>' + questiondate + '</div>',
            questionblock = '<p>' + questiontext + '</p>',
            answerblock = '<div class="questionanswerline' + (question.answer ? '' : ' initialhidden')  + '" id="qanda_answerbox_' + question.question_id + '">'
                + '<p class="messageuser messagereplyuser span-4">Answered by owner</p>'
                + '<span class="messagetext span-15" id="qanda_answertext_' + question.question_id + '">' + answertext + '</span>'
                + '<span class="messagedate" id="qanda_answerdate_' + question.question_id + '">' + answerdate + '</span>'
                + '</div>',
            unansweredblock = question.answer ? '' : '<p class="answerdate" id="qanda_replyanswerdate_' + question.question_id + '">Not yet answered by owner</p>'
                + '<p id="qanda_replyanswer_' + question.question_id + '"></p>',
            questiondateclass = question.answer ? 'messagedate' : 'messagedate questiondate';
        return '\
        <div class="messageline" id="qanda_' + question.question_id + '">\
            <p class="messageuser span-4">Asked by ' + question.user_nickname + '</p>\
            <span class="messagetext span-15">\
                ' + questiontext + '\
                ' + unansweredblock + '\
            </span>\
            <span class="' + questiondateclass + '">\
                ' + questiondateblock + '\
                ' + questionreplybtn + '\
            </span>\
            ' + questionreplybox + '\
            ' + answerblock + '\
        </div>\
        ';
    },
    showReplyBox: function() {
        var questionid = this.id.replace('qanda_replybtn_', '');
        pl('#qanda_replybox_' + questionid).addClass('questionreplylineshow');
        pl('#qanda_replybtn_' + questionid).hide();
        pl('#qanda_replyanswerdate_' + questionid).text('');
        console.log('#qanda_replyanswerdate_'+questionid);
    },
    bindQuestion: function(question) {
        pl('#qanda_replybtn_' + question.question_id).bind('click', this.showReplyBox);
        (this.bindReplyGenerator(question.question_id))();
    }
});
