package pl.szkolenie.projekty.animacja;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    public static final String TAG ="SnakeGame";

    public static MainActivity This;
    CustomView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        This=this;
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        Button StartBtn=(Button)findViewById(R.id.StartBtn);
        gameView=(CustomView)findViewById(R.id.GameView);

        StartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.Start();
            }
        });

        HttpServ t=new HttpServ(this, "http://192.168.137.1:8080/", null, new OnResponseFromServer()
            {
                @Override
                public void Response(String html, boolean success,  Exception e) {
                    if(success)
                        Toast.makeText(MainActivity.This, "Ok", Toast.LENGTH_LONG).show();
                    else
                    if(e!=null)
                    {
                        Toast.makeText(MainActivity.This, "Wystąpił następujący błąd: "+e.getMessage(), Toast.LENGTH_LONG).show();
                    }else
                        Toast.makeText(MainActivity.This, "Wystąpił błąd, którego programista nie przewidział!", Toast.LENGTH_LONG).show();
                }
            }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void PauseClick(View view) {
        gameView.SetPause();
    }
}
