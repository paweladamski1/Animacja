package pl.szkolenie.projekty.animacja;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class CustomView extends View{

    PartOfBodySnake Head;
    ArrayList<PartOfBodySnake> SnakeBody=new ArrayList<PartOfBodySnake>();

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
        if(Head ==null) {
            Head = new PartOfBodySnake();

            int sh=50*2,
                sb=45*2;

            Head.x=sh+(sb*3);
            this.SnakeBody.add(Head);

            PartOfBodySnake b=new PartOfBodySnake();
            b.r=45;
            b.x=(sb*3);
            b.parent=Head;

            this.SnakeBody.add(b);

            PartOfBodySnake b2=new PartOfBodySnake();
            b2.r=45;
            b2.x=(sb*2);
            b2.parent=b;

            this.SnakeBody.add(b2);

            PartOfBodySnake b3=new PartOfBodySnake();
            b3.r=45;
            b3.x=sb;
            b3.parent=b2;
            this.SnakeBody.add(b3);

            //
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float tx= event.getX(), ty=event.getY();
        float rx=tx- Head.x;
        float ry=ty- Head.y;
        LOG_Kulka(rx, ry);
        switch(Head.kierunek)
        {
            case Prawa:
                if(rx<0 && ry>-50 && ry<50 )
                    Head.kierunek=EKierunek.Lewa;
                else
                    if(ry>50)
                        Head.kierunek=EKierunek.Dol;
                    else
                        if(ry<-50)
                            Head.kierunek=EKierunek.Gora;

                break;
            case Lewa:
                if(rx>0 && ry>-50 && ry<50 )
                    Head.kierunek=EKierunek.Prawa;
                else
                    if(ry>50)
                        Head.kierunek=EKierunek.Dol;
                    else
                        if(ry<-50)
                            Head.kierunek=EKierunek.Gora;

                break;
            case Gora:
                if(ry>0 && rx>-50 && rx<50 )
                    Head.kierunek=EKierunek.Dol;
                else
                    if(rx>50)
                        Head.kierunek=EKierunek.Prawa;
                    else
                        if(rx<-50)
                            Head.kierunek=EKierunek.Lewa;
                break;
            case Dol:
                if(ry<0 && rx>-50 && rx<50 )
                    Head.kierunek=EKierunek.Gora;
                else
                    if(rx>50)
                        Head.kierunek=EKierunek.Prawa;
                    else
                        if(rx<-50)
                            Head.kierunek=EKierunek.Lewa;
                break;
        }

        return super.onTouchEvent(event);
    }

    private void LOG_Kulka(float rx, float ry)
    {
        Log.d("Animacja", Head.kierunek+" rx="+rx+"; ry="+ry);
    }


    @Override
    protected void onDraw(Canvas c) {

        for(PartOfBodySnake p:SnakeBody) {
            p.Paint(c);
        }
        invalidate();
    }

    public void Start() {
        Head.start();
    }


    public class PartOfBodySnake extends Thread {

        PartOfBodySnake parent=null;

        float x=50,y=75, r=50;
        int color=Color.parseColor("#0000FF");
        EKierunek kierunek= EKierunek.Prawa;
        boolean isParent=false;

        public boolean activ = true;
        public boolean Enabled = true;
        int sleepValue = 20;

        //constructor
        public PartOfBodySnake() {
        }

        @Override
        public void run() {

            while (true)
            {
                if(parent==null) {
                    switch (kierunek) {
                        case Prawa:
                            if (x > getWidth() - r)
                                kierunek = EKierunek.Dol;
                            else
                                x += 15;
                            break;
                        case Lewa:
                            if (x < r)
                                kierunek = EKierunek.Gora;
                            else
                                x -= 15;
                            break;
                        case Gora:
                            if (y < r)
                                kierunek = EKierunek.Prawa;
                            else
                                y -= 15;
                            break;
                        case Dol:
                            if (y > getHeight() - r)
                                kierunek = EKierunek.Lewa;
                            else
                                y += 15;
                            break;
                    }
                }

                for (PartOfBodySnake p:SnakeBody)
                {
                    if(p.parent!=null)
                        p.refreshPosition();
                }

                try {
                    Thread.sleep(sleepValue);
                } catch (InterruptedException e) {

                }
            }
        }

        public void refreshPosition()
        {
            switch (kierunek) {
                case Prawa:
                    x += 15;
                    if(parent.kierunek!=kierunek)
                    {
                        if(x>=parent.x ) {
                            kierunek = parent.kierunek;
                            x=parent.x;
                            if(parent.kierunek==EKierunek.Gora)
                            {
                                y=parent.y+(parent.r+r);
                            }else
                                y=parent.y-(parent.r+r);
                        }
                    }
                    break;
                case Lewa:
                    x -= 15;
                    if(parent.kierunek!=kierunek)
                    {
                        if(x<=parent.x )
                        {
                            kierunek = parent.kierunek;
                            x=parent.x;
                            if(parent.kierunek==EKierunek.Gora)
                            {
                                y=parent.y+(parent.r+r);
                            }else
                                y=parent.y-(parent.r+r);
                        }

                    }
                    break;
                case Gora:
                    y -= 15;
                    if(parent.kierunek!=kierunek)
                    {
                        if(y<=parent.y ) {
                            kierunek = parent.kierunek;
                            y=parent.y;
                            if(parent.kierunek==EKierunek.Lewa)
                            {
                                x=parent.x+(parent.r+r);
                            }else
                                x=parent.x-(parent.r+r);
                        }
                    }
                    break;
                case Dol:
                    y += 15;
                    if(parent.kierunek!=kierunek)
                    {
                        if(y>=parent.y ) {
                            kierunek = parent.kierunek;
                            y=parent.y;
                            if(parent.kierunek==EKierunek.Lewa)
                            {
                                x=parent.x+(parent.r+r);
                            }else
                                x=parent.x-(parent.r+r);
                        }
                    }
                    break;
            }
            //this.kierunek=parent.kierunek;
        }

        public void SetSleepValue(int newValue) {
            this.sleepValue = newValue;
        }

        public void Paint(Canvas c)
        {
            Paint paint = new Paint();
            paint.setColor(color);
            paint.setAntiAlias(true);
            c.drawCircle(x, y, r, paint);
        }
    }

    enum  EKierunek{ Prawa, Lewa, Gora, Dol}

}
