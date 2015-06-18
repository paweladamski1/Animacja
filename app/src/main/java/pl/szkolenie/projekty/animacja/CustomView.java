package pl.szkolenie.projekty.animacja;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CustomView extends View{
    float x=50,y=50;
    ThreadFrame thread;
    public CustomView(Context context) {
        super(context);
        init();
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init()
    {
        if(thread==null) {
            thread = new ThreadFrame();
            thread.start();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float tx= event.getX(), ty=event.getY();
        float rx=tx-x;
        float ry=ty-y;
        LOG_Kulka(rx, ry);
        switch(thread.kierunek)
        {
            case Prawa:
                if(rx<0 && ry>-50 && ry<50 )
                    thread.kierunek=EKierunek.Lewa;
                else
                    if(ry>50)
                        thread.kierunek=EKierunek.Dol;
                    else
                        if(ry<-50)
                            thread.kierunek=EKierunek.Gora;

                break;
            case Lewa:
                if(rx>0 && ry>-50 && ry<50 )
                    thread.kierunek=EKierunek.Prawa;

                break;
            case Gora:
                break;
            case Dol:
                break;
        }

        return super.onTouchEvent(event);
    }
    private void LOG_Kulka(float rx, float ry)
    {
        Log.d("Animacja", thread.kierunek+" rx="+rx+"; ry="+ry);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
     /*   paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);/*/
        // Use Color.parseColor to define HTML colors
        paint.setColor(Color.parseColor("#0000FF"));
        paint.setAntiAlias(true);

        canvas.drawCircle(x, y, 50, paint);
        invalidate();
    }

    public class ThreadFrame extends Thread {

        EKierunek kierunek= EKierunek.Prawa;

        public boolean activ = true;
        public boolean Enabled = true;
        int sleepValue = 20;

        //constructor
        public ThreadFrame() {

        }

        @Override
        public void run() {

            while (true)
            {
                switch ( kierunek)
                {
                    case Prawa:
                        if (x > getWidth()-50)
                            kierunek=EKierunek.Dol;
                        else
                            x+=15;
                        break;
                    case Lewa:
                        if (x <50)
                            kierunek=EKierunek.Gora;
                        else
                            x-=15;
                        break;
                    case Gora:
                        if (y <50)
                            kierunek=EKierunek.Prawa;
                        else
                            y-=15;
                        break;
                    case Dol:
                        if(y>getHeight()-50)
                            kierunek=EKierunek.Lewa;
                        else
                            y+=15;
                        break;
                }

                try {
                    Thread.sleep(sleepValue);
                } catch (InterruptedException e) {

                }
            }
        }

        public void SetSleepValue(int newValue) {
            this.sleepValue = newValue;
        }
    }

    enum  EKierunek{ Prawa, Lewa, Gora, Dol}

}
