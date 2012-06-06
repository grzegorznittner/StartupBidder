package com.startupbidder.vo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class QuestionAnswerListVO extends BaseResultVO {
	@JsonProperty("questions_answers") private List<QuestionAnswerVO> questionAnswers;
	@JsonProperty("questions_answers_props")	private ListPropertiesVO questionAnswersProperties;
	public List<QuestionAnswerVO> getQuestionAnswers() {
		return questionAnswers;
	}
	public void setQuestionAnswers(List<QuestionAnswerVO> questionAnswers) {
		this.questionAnswers = questionAnswers;
	}
	public ListPropertiesVO getQuestionAnswersProperties() {
		return questionAnswersProperties;
	}
	public void setQuestionAnswersProperties(
			ListPropertiesVO questionAnswersProperties) {
		this.questionAnswersProperties = questionAnswersProperties;
	}
}
