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
    static String TAG="Animacja";
    PartOfBodySnake Head;
    ArrayList<PartOfBodySnake> SnakeBody=new ArrayList<PartOfBodySnake>();
    PartOfBodySnake Food=new PartOfBodySnake(true);


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
            Food.x=500;
            Food.y=500;

            Head = new PartOfBodySnake(false);

            int sh=50*2,
                sb=45*2;
            Head.color=Color.GREEN;
            Head.x=sh+(sb*3);
            this.SnakeBody.add(Head);

            PartOfBodySnake b=new PartOfBodySnake(false);
            b.r=45;
            b.x=(sb*3);
            b.parent=Head;

            this.SnakeBody.add(b);

            PartOfBodySnake b2=new PartOfBodySnake(false);
            b2.r=45;
            b2.x=(sb*2);
            b2.parent=b;

            this.SnakeBody.add(b2);

            PartOfBodySnake b3=new PartOfBodySnake(false);
            b3.r=45;
            b3.x=sb;
            b3.parent=b2;
            this.SnakeBody.add(b3);
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
            case Prawa: {
                //if(!(rx<0 && ry>-50 && ry<50 ))
                if (ry > 50)
                    Down();
                else
                    if (ry < -50)
                        Up();

                break;
            }
            case Lewa:
                {
                    // if(!(rx>0 && ry>-50 && ry<50 ))
                    if (ry > 50)
                        Down();
                    else if (ry < -50)
                        Up();

                    break;
                }
            case Gora:
                //if(!(ry>0 && rx>-50 && rx<50 ))
                {
                    if (rx > 50)
                        Right();
                    else if (rx < -50)
                        Left();
                    break;
                }
            case Dol: {
                //if(!(ry<0 && rx>-50 && rx<50 ))
                if (rx > 50)
                    Right();
                else if (rx < -50)
                    Left();

                break;
            }
        }

        //return super.onTouchEvent(event);
        return true;
    }

    private void LOG_Kulka(float rx, float ry)
    {
        //Log.d("Animacja", Head.kierunek+" rx="+rx+"; ry="+ry);
    }


    @Override
    protected void onDraw(Canvas c) {

        //rysowanie wê¿a na ekranie
        for(PartOfBodySnake p:SnakeBody)
            p.Paint(c);

        //rysowanie pokarmu
        Food.Paint(c);

        invalidate();
    }

    public void Start() {
        Head.start();
    }

    public void Left() {
        PartOfBodySnake b1=SnakeBody.get(1);
        float ry=Math.max(b1.y,Head.y)-Math.min(b1.y,Head.y);

        if (ry>=Head.r+b1.r)
            Head.Left();
    }

    public void Down() {
        PartOfBodySnake b1=SnakeBody.get(1);
        float rx=Math.max(b1.x,Head.x)-Math.min(b1.x,Head.x);

        if (rx>=Head.r+b1.r)
            Head.Down();
    }

    public void Right() {
        PartOfBodySnake b1=SnakeBody.get(1);
        float ry=Math.max(b1.y,Head.y)-Math.min(b1.y,Head.y);

        if (ry>=Head.r+b1.r)
            Head.Right();
    }

    public void Up() {
        PartOfBodySnake b1=SnakeBody.get(1);
        float rx=Math.max(b1.x,Head.x)-Math.min(b1.x,Head.x);

        if (rx>=Head.r+b1.r)
            Head.Up();
    }


    public class PartOfBodySnake extends Thread {

        PartOfBodySnake parent=null;
        boolean isFood=false;
        float x=50,y=75, r=50;
        int color=Color.parseColor("#0000FF");
        private EKierunek kierunek= EKierunek.Prawa;
        boolean isParent=false;

        public boolean activ = true;
        public boolean Enabled = true;
        int sleepValue = 20;

        //constructor
        public PartOfBodySnake(boolean isFood) {
            this.isFood=isFood;
        }

        @Override
        public void run() {

            while (true)
            {
                if(parent==null) {
                    switch (kierunek) {
                        case Prawa:
                            if (x > getWidth() - r)
                                Down();
                            else
                                x += 15;
                            break;
                        case Lewa:
                            if (x < r)
                                Up();
                            else
                                x -= 15;
                            break;
                        case Gora:
                            if (y < r)
                                Right();
                            else
                                y -= 15;
                            break;
                        case Dol:
                            if (y > getHeight() - r)
                                Left();
                            else
                                y += 15;
                            break;
                    }
                }

                //ustawianie pozostalych elemenow weza
                for (PartOfBodySnake p:SnakeBody)
                {
                    if(p.parent!=null)
                        p.refreshPosition();
                }

                if(isColision(Food))
                {
                    /*Food.x=50;
                    Food.y=600;*/
                    Log.d(TAG, "Kolizja");

                }

                try {
                    Thread.sleep(sleepValue);
                } catch (InterruptedException e) {

                }
            }
        }

        public void refreshPosition()
        {
            float rx,ry;
            rx=x-parent.x;
            ry=y-parent.y;

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
                                if(parent.kierunek==EKierunek.Dol) {
                                    y = parent.y - (parent.r + r);
                                }
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
                                if(parent.kierunek==EKierunek.Dol)
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
                            if(parent.kierunek==EKierunek.Prawa)
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
                                if(parent.kierunek==EKierunek.Prawa)
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
            if(isFood)
            {
                Paint paint = new Paint();
                paint.setColor(Color.GREEN);
                paint.setAntiAlias(true);
                c.drawCircle(x, y, 15, paint);
            }else {
                Paint paint = new Paint();
                paint.setColor(color);
                paint.setAntiAlias(true);
                c.drawCircle(x, y, r, paint);
            }
        }

        public void Left() {
            if(kierunek!=EKierunek.Prawa) {
                kierunek = EKierunek.Lewa;
                Log.d(TAG, "LEWA");
            }
        }

        public void Down() {
            if(kierunek!=EKierunek.Gora) {
                kierunek = EKierunek.Dol;
                Log.d(TAG, "DOL" );
            }
        }

        public void Right() {
            if(kierunek!=EKierunek.Lewa) {
                kierunek = EKierunek.Prawa;
                Log.d(TAG, "PRAWA" );
            }
        }

        public void Up() {
            if(kierunek!=EKierunek.Dol) {
                kierunek = EKierunek.Gora;
                Log.d(TAG, "GORA" );
            }
        }

        public boolean isColision(PartOfBodySnake p)
        {
            if(p==null)
                return false;

            float py_g=y-r,
                  py_d=y+r;

            float px_l=x-r,
                  px_p=x+r;

            return p.x>px_l && p.x<px_p && p.y>py_g && p.y<py_d ;
        }
    }

    enum  EKierunek{ Prawa, Lewa, Gora, Dol}

}
