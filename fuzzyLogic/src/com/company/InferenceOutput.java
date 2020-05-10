package com.company;

public class InferenceOutput {
    String memberFunctionName;
    double value;

    public InferenceOutput(String memberFunctionName, double value) {
        this.memberFunctionName = memberFunctionName;
        this.value = value;
    }

    public String getMemberFunctionName() {
        return memberFunctionName;
    }

    public void setMemberFunctionName(String memberFunctionName) {
        this.memberFunctionName = memberFunctionName;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
