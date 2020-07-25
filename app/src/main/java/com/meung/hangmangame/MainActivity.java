package com.meung.hangmangame;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.InputStream;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private final static int REPEAT=10;
    final static private String BLANK= "";
    final static private int BUTTON = 26;
    final static private int [] btnID = {R.id.button_a, R.id.button_b, R.id.button_c, R.id.button_d,
            R.id.button_e, R.id.button_f, R.id.button_g, R.id.button_h, R.id.button_i,
            R.id.button_j, R.id.button_k, R.id.button_l, R.id.button_m, R.id.button_n,
            R.id.button_o, R.id.button_p, R.id.button_q, R.id.button_r, R.id.button_s,
            R.id.button_t, R.id.button_u, R.id.button_v, R.id.button_w, R.id.button_x,
            R.id.button_y, R.id.button_z};
    final static private int [] imgID = {R.drawable.full, R.drawable.full_, R.drawable.full__,
            R.drawable.full___, R.drawable.full____, R.drawable.full_____, R.drawable.empty};
    final static private int WORDS = 500;

    ImageView img;
    Button btn_help;
    Button btn_exit;
    Button [] btnArr = new Button[BUTTON];
    TextView word;
    TextView meaning;
    TextView num;

    private String answer_word_String;
    private String answer_meaning;
    private int wordLength;
    private char [] answer_word_char;
    private char [] hidden_word_char;
    private char userClick;
    private int c_cnt;  //맞은글자수
    private int w_cnt;  //틀린글자수
    private boolean fail = true; //맞는 알파벳이 없으면 true
    private int imgNum=0;
    private boolean complete;
    private Memorize mem = new Memorize();

    private Vector<String> wordVector = new Vector<String>();
    private Vector<String> meanVector = new Vector<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        SoundManager.getInstance();
        SoundManager.initSounds(this);
        SoundManager.loadSounds();

        word = findViewById(R.id.word);
        meaning = findViewById(R.id.word_meaning);
        img = findViewById(R.id.imageView);
        num = findViewById(R.id.TextView_score);
        btn_exit = findViewById(R.id.button_exit);
        btn_help = findViewById(R.id.help_button);

        initAll();
        //도움말 버튼
        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "두 번 틀리면 뜻 힌트가 나옵니다\n알파벳을 눌러 빈칸을 채워보세요!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
        //종료 버튼
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundManager.playSound(1, 1);
                moveTaskToBack(true);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        //알파벳 버튼
        for(int i=0; i<BUTTON; i++){
            btnArr[i] = (Button)findViewById(btnID[i]);
            btnArr[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SoundManager.playSound(1, 1);
                    Button b = (Button) v;
                    userClick = b.getText().charAt(0);
                    b.setEnabled(false);
                    fail = true;

                    check_alphabet();
                    if(!fail){  //있음
                        word.setText(String.valueOf(hidden_word_char));
                    }
                    else if(fail){  //없음
                        w_cnt++;
                        if(imgNum!=6) imgNum++;
                        img.setBackgroundResource(imgID[imgNum]);
                    }

                    if(c_cnt==wordLength) { //성공
                        resize_img();
                        img.setBackgroundResource(R.drawable.success);
                        meaning.setText(answer_meaning);
                        complete = true;
                        setNumAnswer();
                        checkNext(v);
                    }
                    else if(w_cnt == 2) meaning.setText(answer_meaning);   //2번 틀리면 뜻을 보여줌
                    else if(w_cnt == 6) { //실패
                        resize_img();
                        img.setBackgroundResource(R.drawable.fail);
                        word.setText(answer_word_String);
                        complete=false;
                        setNumAnswer();
                        checkNext(v);
                    }
                }
            });
        }

    }

    public void check_alphabet(){
        for(int i=0; i<wordLength; i++){
            if(answer_word_char[i] == userClick){
                hidden_word_char[i] = userClick;
                c_cnt++;
                fail = false;
            }
        }
    }

    private void initAll(){
        imgNum=0; c_cnt=0; w_cnt=0;
        word.setText(BLANK); meaning.setText(BLANK);
        img.setBackgroundResource(imgID[0]);
        wordVector.clear(); meanVector.clear();
        saveWord(); //단어 저장
        pickWord(); //단어 선택
        wordLength = answer_word_String.length();
        String2char_arr();
        setHiddenWordBlank();   //단어 숨김
        num.setText(String.valueOf(getNum()));
        num.append(" /10");
    }

    private void String2char_arr(){
        answer_word_char = new char[wordLength];
        for(int i=0; i<answer_word_String.length(); i++){
            answer_word_char[i] = answer_word_String.charAt(i);
        }
    }

    private void setHiddenWordBlank(){
        hidden_word_char = new char[wordLength];
        for(int i=0; i<wordLength; i++){
            if(answer_word_char[i]==' '){
                hidden_word_char[i] = ' ';
                c_cnt++;
            }
            else
                hidden_word_char[i] = '_';
        }
        word.setText(String.valueOf(hidden_word_char));
    }

    private void saveWord() {
        InputStream input = getResources().openRawResource(R.raw.toeic_word);
        Scanner scanner = new Scanner(input);
        int i=0;
        while (i!=WORDS) {
            String file_line = scanner.nextLine();
            String[] spl = file_line.split(":");
            wordVector.add(spl[1]);
            meanVector.add(spl[2]);
            i++;
        }
    }

    private void pickWord(){
        Random random = new Random();
        int num = random.nextInt(WORDS);
        answer_word_String = wordVector.get(num);
        answer_meaning = meanVector.get(num);
    }

    public void OpenPopup(View V){
        Intent intent = new Intent(this, SubActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(resultCode);
        if (requestCode == 0) {
            if (resultCode == 1) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            } else if (resultCode == 2) {
                moveTaskToBack(true);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    }

    public void resize_img(){
        img.setImageResource(0);
        img.getLayoutParams().height = 100;
        img.getLayoutParams().width = 100;
        img.requestLayout();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) img.getLayoutParams();
        params.gravity = Gravity.CENTER;
        img.setLayoutParams(params);
    }

    public void setNumAnswer(){
        num.setText(String.valueOf(getNum()));
        num.append(" /10");
        if(complete==true) mem.setScore(1);
        else mem.setScore(0);
        mem.setAnswer(answer_word_String, answer_meaning);
    }

    public void checkNext(View v){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if(((mem.num-1)%REPEAT)==0){
            OpenPopup(v);
        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }, 500);
        }
    }

    public int getNum(){
        if(mem.num%10 == 0)
            return 10;
        else
            return mem.num%REPEAT;
    }

}
