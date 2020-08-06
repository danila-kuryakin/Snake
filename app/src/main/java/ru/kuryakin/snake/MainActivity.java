package ru.kuryakin.snake;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ru.kuryakin.snake.engine.Direction;
import ru.kuryakin.snake.engine.GameState;
import ru.kuryakin.snake.engine.GameEngine;
import ru.kuryakin.snake.views.SnakeView;

public class MainActivity extends Activity implements View.OnTouchListener {


    private Button btnStart;

    private GameEngine gameEngine;
    private SnakeView snakeView;
    private final Handler handler = new Handler();
    private final long updateDelay = 150;

    private float prevX, prevY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        btnStart = (Button) findViewById(R.id.btnStart);
        snakeView = (SnakeView)findViewById(R.id.snakeView);
        snakeView.setVisibility(View.INVISIBLE);
        this.menu();
    }

    public void menu() {
        btnStart.setVisibility(View.VISIBLE);
        View.OnClickListener oclStart = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                btnStart.setVisibility(View.INVISIBLE);

                load();
            }
        };
        btnStart.setOnClickListener(oclStart);
    }

    private void load(){
        gameEngine = new GameEngine();
        gameEngine.initGame();

        snakeView.setOnTouchListener(this);
        snakeView.setVisibility(View.VISIBLE);

        startUpdateHandler();
    }

    private void startUpdateHandler(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gameEngine.Update();

                if (gameEngine.getCurrentGameState() == GameState.Running){
                    handler.postDelayed(this, updateDelay);
                }
                if (gameEngine.getCurrentGameState() == GameState.Lost){
                    onGameLost();
                    snakeView.setVisibility(View.INVISIBLE);
                    menu();
                }
                snakeView.setSnakeViewMap(gameEngine.getMap());
                snakeView.invalidate();
            }
        }, updateDelay);
    }

    private void onGameLost(){
        int res = gameEngine.getSnakeSize() - 6;
        Toast.makeText(this, "Вы проиграли \n Очки: " +  res, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                prevX = event.getX();
                prevY = event.getY();

                break;
            case MotionEvent.ACTION_UP:
                float newX = event.getX();
                float newY = event.getY();

                if (Math.abs(newX - prevX) > Math.abs(newY - prevY)){
                    if ( newX > prevX){
                        gameEngine.UpdateDirection(Direction.East);
                    }else {
                        gameEngine.UpdateDirection(Direction.West);
                    }
                } else {
                    if ( newY > prevY){
                        gameEngine.UpdateDirection(Direction.South);
                    }else {
                        gameEngine.UpdateDirection(Direction.Noth);
                    }
                }

                break;
        }

        return true;
    }
}
