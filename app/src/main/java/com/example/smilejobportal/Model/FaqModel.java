package com.example.smilejobportal.Model;

public class FaqModel {
    private String question;
    private String answer;

    public FaqModel(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
}

