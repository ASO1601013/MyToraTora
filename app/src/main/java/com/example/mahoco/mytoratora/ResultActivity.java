package com.example.mahoco.mytoratora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
    final int TORATORA_GRANDMOTHER = 0;
    final int TORATORA_KIYOMASA = 1;
    final int TORATORA_TIGER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        int myHand = 0;
        Intent intent = getIntent();
        int id = intent.getIntExtra("MY_HAND",0);

        ImageView myHandImageView =
                (ImageView) findViewById(R.id.my_hand_image);
        switch (id){
            case R.id.grandmother:
                myHandImageView.setImageResource(R.drawable.keirou_katamomi_obaachan2);
                myHand = TORATORA_GRANDMOTHER;
                break;
            case R.id.kiyomasa:
                myHandImageView.setImageResource(R.drawable.d8295f7f78335bde5a10f0f46033bf39f30d6d3da807837aaa6c5312adb147c2_4522336_450_5701755);
                myHand = TORATORA_KIYOMASA;
                break;
            case R.id.tiger:
                myHandImageView.setImageResource(R.drawable.tora);
                myHand = TORATORA_TIGER;
                break;
            default:
                myHand = TORATORA_GRANDMOTHER;
                break;
        }

        //コンピュータの手を決める
        int comHand = getHand();
        ImageView comHandImageView =
                (ImageView) findViewById(R.id.com_hand_image);
        switch (comHand){
            case TORATORA_GRANDMOTHER:
                comHandImageView.setImageResource(R.drawable.keirou_katamomi_obaachan2);
                break;
            case TORATORA_KIYOMASA:
                comHandImageView.setImageResource(R.drawable.d8295f7f78335bde5a10f0f46033bf39f30d6d3da807837aaa6c5312adb147c2_4522336_450_5701755);
                break;
            case TORATORA_TIGER:
                comHandImageView.setImageResource(R.drawable.tora);
                break;
        }

        //勝敗を判定する
        TextView resultLabel = (TextView) findViewById(R.id.result_label);
        int gameResult = (comHand - myHand + 3)%3;
        switch (gameResult) {
            case 0:
                //あいこの場合
                resultLabel.setText(R.string.result_draw);
                break;
            case 1:
                //勝った場合
                resultLabel.setText(R.string.result_win);
                break;
            case 2:
                //負けた場合
                resultLabel.setText(R.string.result_lose);
                break;
        }
        //じゃんけんの結果を保存する
        saveData(myHand,comHand,gameResult);
    }
    public void onBackButtonTapped(View view){
        finish();
    }

    private void saveData (int myHand,int comHand,int gameResult){
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();

        int gameCount = pref.getInt("GAME_COUNT",0);
        int winningStreakCount = pref.getInt("WINNING_STREAK_COUNT",0);
        int lastComHand = pref.getInt("LAST_COM_HAND",0);
        int lastGameResult = pref.getInt("GAME_RESULT",0);
        editor.putInt("GAME_COUNT",gameCount+1);
        if (lastGameResult == 2 && gameResult == 2){
            //コンピュータが連勝した場合
            editor.putInt("WINNING_STREAK_COUNT",winningStreakCount+1);
        }else{
            editor.putInt("WINNING_ATREAK_COUNT",0);
        }
        editor.putInt("LAST_MY_HAND",myHand);
        editor.putInt("LAST_COM_HAND",comHand);
        editor.putInt("BEFORE_LAST_COM_HAND",lastComHand);
        editor.putInt("GAME_RESULT",gameResult);

        editor.commit();
    }
    private int getHand(){
        int hand = (int)(Math.random()*3);
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        int gameCount = pref.getInt("GAME_COUNT",0);
        int winningStreakCount = pref.getInt("WINNIG_STREAK_COUNT",0);
        int lastMyHand = pref.getInt("LAST_MY_HAND",0);
        int lastComHand = pref.getInt("LAST_COM_HAND",0);
        int beforeLastComHand = pref.getInt("BEFORE_LAST_COM_HAND",0);
        int gameResult = pref.getInt("GAME_RESULT",0);

        if (gameCount == 1) {
            if (gameResult == 2) {
                //前回の勝負が一回目で、コンピュータが勝った場合、
                //コンピュータは次に出す手を変える
                while (lastComHand == hand) {
                    hand = (int) (Math.random() * 3);
                }
            } else if (gameResult == 1) {
                //前回の勝負が一回目で、コンピュータが負けた場合、
                //相手の出した手に勝つ手を考える
                hand = (lastMyHand - 1 + 3) % 3;
            }
        }else if (winningStreakCount > 0){
            if (beforeLastComHand == lastComHand) {
                //同じ手で連勝した場合は手を変える
                while (lastComHand == hand){
                    hand = (int)(Math.random()*3);
                }
            }
        }
        return hand;
    }
}
