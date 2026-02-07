package com.growfund.seedtowealth.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Quiz implements Serializable {
    private Long id;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private Integer correctOptionIndex;
    private Long rewardCoins;

    // Getters
    public Long getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public Integer getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public Long getRewardCoins() {
        return rewardCoins;
    }
}
