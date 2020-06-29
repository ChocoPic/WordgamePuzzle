package com.meung.hangmangame;

import java.util.ArrayList;

public class Memorize {
    private final static int REPEAT=10;
    private static ArrayList<String> answer = new ArrayList<String>(); //답 저장
    private static int [] score = new int[10] ;  //점수저장
    public static int num=1;   //몇번째문제인지

    public void setAnswer(String word, String meaning){ //단어저장
        answer.add(word);
        answer.add(meaning);
        num++;
    }
    public ArrayList<String> getAnswer(){   //정답배열
        return answer;
    }
    public void setScore(int n){  //정답1 / 실패0
        this.score[num%REPEAT] = n;
    }
    public int [] getScore(){   //점수배열
        return score;
    }
}
