package com.test.csv_assignment.details;

public class SampleCRF {
    private String parentId;
    
	private String parentType;
	
	private String studyId;
	
	private String siteId;
	
	private String crfId;
	
	private String questionId;
	
	private String answerId;
	
	private String type;
	
	private String title;
	
	private String label;
	
	private String questionType;
	
	private String questionMandatory;
	
	private String questiondefault;

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getParentType() {
		return parentType;
	}

	public void setParentType(String parentType) {
		this.parentType = parentType;
	}

	public String getStudyId() {
		return studyId;
	}

	public void setStudyId(String studyId) {
		this.studyId = studyId;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getCrfId() {
		return crfId;
	}

	public void setCrfId(String crfId) {
		this.crfId = crfId;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getAnswerId() {
		return answerId;
	}

	public void setAnswerId(String answerId) {
		this.answerId = answerId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public String getQuestionMandatory() {
		return questionMandatory;
	}

	public void setQuestionMandatory(String questionMandatory) {
		this.questionMandatory = questionMandatory;
	}

	public String getQuestiondefault() {
		return questiondefault;
	}

	public void setQuestiondefault(String questiondefault) {
		this.questiondefault = questiondefault;
	}

	@Override
	public String toString() {
		return "SampleCRF [parentId=" + parentId + ", parentType=" + parentType + ", studyId=" + studyId + ", siteId="
				+ siteId + ", crfId=" + crfId + ", questionId=" + questionId + ", answerId=" + answerId + ", type="
				+ type + ", title=" + title + ", label=" + label + ", questionType=" + questionType
				+ ", questionMandatory=" + questionMandatory + ", questiondefault=" + questiondefault + "]";
	}
	
	

}
