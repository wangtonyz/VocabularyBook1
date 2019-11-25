package com.example.vocabularybook;

public class Words {
    public String name;
    public String trans;
    public String sentence;

    public String getSentence() {
        return sentence;
    }
    public void setSentence(String sentence) {
        this.sentence = sentence;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public Words(){};

    public Words(String name, String trans) {
        this.name = name;
        this.trans = trans;
    }

}
