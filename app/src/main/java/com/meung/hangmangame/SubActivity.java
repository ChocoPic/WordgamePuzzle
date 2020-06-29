package com.meung.hangmangame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SubActivity extends AppCompatActivity {
    private final static int REPEAT=10;
    private TextView answerList;
    private TextView totalScore;
    private Button next;
    private Button exit;

    private ArrayList<String> answer = new ArrayList<String>();
    private int [] scoreArr = new int[10];
    private int total;
    private Memorize memo = new Memorize();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        answerList = findViewById(R.id.textView_answer);
        totalScore = findViewById(R.id.textView_score);
        next = findViewById(R.id.next);
        exit = findViewById(R.id.exit);

        answer = memo.getAnswer();
        scoreArr = memo.getScore();
        total = 0;
        for(int i=0; i<REPEAT; i++){
            if(scoreArr[i]==1){
                total++;
            }
        }
        totalScore.setText(String.valueOf(total));
        totalScore.append("개 정답이네요!");

        if(total<3) totalScore.append(" 분발하세요");
        else if(total<6) totalScore.append(" 잘하고있어요");
        if(total>8) totalScore.append(" 대단해요!!");
        else if(total>=6) totalScore.append(" 나쁘지 않네요~");

        System.out.println(answer.get(0));
        answerList.setText(answer.get(0));
        for(int i=1; i<REPEAT*2; i++){
            answerList.append(answer.get(i));
            if(i%2==0) answerList.append("  ");
            else if(i%2==1) answerList.append("\n");
        }

        next.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                closePopup_next(v);
            }
        });
        exit.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                closePopup_exit(v);
            }
        });

    }
    public void closePopup_next(View v){
        Intent intent = new Intent();
        intent.putExtra("result", "next");
        setResult(1, intent);
        finish();
    }
    public void closePopup_exit(View v){
        Intent intent = new Intent();
        intent.putExtra("result", "exit");
        setResult(2, intent);
        finish();
    }
}