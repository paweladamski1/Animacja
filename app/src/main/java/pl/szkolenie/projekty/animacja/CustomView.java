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
        float rx=tx-thread.x;
        float ry=ty-thread.y;
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
                else
                    if(ry>50)
                        thread.kierunek=EKierunek.Dol;
                    else
                        if(ry<-50)
                            thread.kierunek=EKierunek.Gora;

                break;
            case Gora:
                if(ry>0 && rx>-50 && rx<50 )
                    thread.kierunek=EKierunek.Dol;
                else
                    if(rx>50)
                        thread.kierunek=EKierunek.Prawa;
                    else
                        if(rx<-50)
                            thread.kierunek=EKierunek.Lewa;
                break;
            case Dol:
                if(ry<0 && rx>-50 && rx<50 )
                    thread.kierunek=EKierunek.Gora;
                else
                    if(rx>50)
                        thread.kierunek=EKierunek.Prawa;
                    else
                        if(rx<-50)
                            thread.kierunek=EKierunek.Lewa;
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
        paint.setColor(thread.color);
        paint.setAntiAlias(true);

        canvas.drawCircle(thread.x, thread.y, thread.r, paint);
        invalidate();
    }


    public class ThreadFrame extends Thread {
        float x=50,y=50, r=50;
        int color=Color.parseColor("#0000FF");
        EKierunek kierunek= EKierunek.Prawa;
        boolean isParent=false;

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
                        if (x > getWidth()-r)
                            kierunek=EKierunek.Dol;
                        else
                            x+=15;
                        break;
                    case Lewa:
                        if (x <r)
                            kierunek=EKierunek.Gora;
                        else
                            x-=15;
                        break;
                    case Gora:
                        if (y <r)
                            kierunek=EKierunek.Prawa;
                        else
                            y-=15;
                        break;
                    case Dol:
                        if(y>getHeight()-r)
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
