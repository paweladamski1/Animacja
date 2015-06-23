package pl.szkolenie.projekty.animacja;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class CustomView extends View{
    static Bitmap  g_d_bmp, c_pion_bmp, c_poziom_bmp, c_skret_dl_bmp, c_skret_dp_bmp, c_skret_lg,
            c_skret_pg_bmp, g_g_bmp, g_l_bmp, g_p_bmp, o_d_bmp, o_g_bmp, o_l_bmp, o_p_bmp;

    static String TAG="Animacja";
    PartOfBodySnake Head;
    ArrayList<PartOfBodySnake> SnakeBody=new ArrayList<PartOfBodySnake>();
    PartOfBodySnake Food=new PartOfBodySnake(true, null);
    boolean isGameOver=false, isPause=false;
    int ptk=0;

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
        if(c_pion_bmp==null)
        {
            c_pion_bmp = LoadBitmap( R.drawable.c_pion);
            c_poziom_bmp = LoadBitmap( R.drawable.c_poziom);
            c_skret_dl_bmp = LoadBitmap( R.drawable.c_skret_dl);
            c_skret_dp_bmp = LoadBitmap( R.drawable.c_skret_dp);
            c_skret_lg = LoadBitmap( R.drawable.c_skret_lg);
            c_skret_pg_bmp = LoadBitmap( R.drawable.c_skret_pg);
            g_d_bmp = LoadBitmap( R.drawable.g_d);
            g_g_bmp = LoadBitmap( R.drawable.g_g);
            g_l_bmp = LoadBitmap( R.drawable.g_l);
            g_p_bmp= LoadBitmap( R.drawable.g_p);
            o_d_bmp= LoadBitmap( R.drawable.o_d);
            o_g_bmp= LoadBitmap( R.drawable.o_g);
            o_l_bmp= LoadBitmap( R.drawable.o_l);
            o_p_bmp= LoadBitmap( R.drawable.o_p);
        }

        if(Head !=null) {
            SnakeBody.clear();
            Head.Close();
        }

        Food.x=500;
        Food.y=500;
        Food.width=5;
        Food.height=5;

        Head = new PartOfBodySnake(false, null);
        this.SnakeBody.add(Head);
        Head.color=Color.GREEN;
        Head.x=200;
        Head.Add().Add().Add().Add().Add().Add().Add().Add().Add().Add();

        isGameOver=false;
        ptk=0;
    }

    Bitmap LoadBitmap(int resource)
    {
        try {
            return BitmapFactory.decodeResource(MainActivity.This.getResources(), resource);
        }
        catch(Exception ex)
        {
            return null;
        }
    }

    public void GameOver()
    {
        isGameOver=true;
    }

    public void SetPause()
    {
        this.isPause=!this.isPause;
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
                if (ry > Head.height)
                    Down();
                else
                    if (ry < -Head.height)
                        Up();

                break;
            }
            case Lewa:
                {
                    if (ry > Head.height)
                        Down();
                    else if (ry < -Head.height)
                        Up();

                    break;
                }
            case Gora:
                {
                    if (rx > Head.width)
                        Right();
                    else if (rx < -Head.width)
                        Left();
                    break;
                }
            case Dol: {

                if (rx > Head.width)
                    Right();
                else if (rx < -Head.width)
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

    //int red, green, blue;

    @Override
    protected void onDraw(Canvas c) {

        //rysowanie pokarmu
        Food.Paint(c);

        //rysowanie weza
        for (PartOfBodySnake p : SnakeBody)
            p.Paint(c);


        if(isGameOver) {
            Random rand=new Random();
            Paint p = new Paint();
            p.setColor(Color.rgb(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255)));
            p.setTextSize(60);
            p.setTextAlign(Paint.Align.CENTER);
            c.drawText("Game Over\n zdobyłeś(aś) "+ptk+" punktów", getWidth()/2, getHeight()/2, p);
        }else
        if(isPause) {
            Paint p = new Paint();
            p.setColor(Color.GRAY);
            p.setTextSize(60);
            p.setTextAlign(Paint.Align.CENTER);
            c.drawText("Pause", getWidth()/2, getHeight()/2, p);
        }else
        {
            Paint p = new Paint();
            p.setColor(Color.BLACK);
            p.setTextSize(30);
            p.setTextAlign(Paint.Align.LEFT);
            c.drawText("Punkty: "+ptk, 25, 50, p);
        }
        invalidate();
    }

    public void Start() {
        if(!Head.isAlive())
            Head.start();
        else
        {
            init();
            Head.start();
        }
    }

    public void Left() {
        PartOfBodySnake b1=SnakeBody.get(1);
        float ry=Math.max(b1.y,Head.y)-Math.min(b1.y,Head.y);

        if (ry>=Head.height)
            Head.Left();
    }

    public void Down() {
        PartOfBodySnake b1=SnakeBody.get(1);
        float rx=Math.max(b1.x,Head.x)-Math.min(b1.x,Head.x);

        if (rx>=Head.width)
            Head.Down();
    }

    public void Right() {
        PartOfBodySnake b1=SnakeBody.get(1);
        float ry=Math.max(b1.y,Head.y)-Math.min(b1.y,Head.y);

        if (ry>= Head.height)
            Head.Right();
    }

    public void Up() {
        PartOfBodySnake b1=SnakeBody.get(1);
        float rx=Math.max(b1.x,Head.x)-Math.min(b1.x,Head.x);

        if (rx>=Head.width)
            Head.Up();
    }


    public class PartOfBodySnake extends Thread {

        PartOfBodySnake parent=null;
        PartOfBodySnake child=null;

        boolean isFood=false, colisionDetect=false, runGame=true;
        float x=50,y=75, width=42, height=42, v=5;

        int color=Color.parseColor("#0000FF");
        private EKierunek kierunek= EKierunek.Prawa;
        boolean isParent=false;

        public boolean activ = true;
        public boolean Enabled = true;
        int sleepValue = 20;
        private float widthByKierunek;

        //constructor
        public PartOfBodySnake(boolean isFood, PartOfBodySnake parent) {
            this.isFood=isFood;
            if(parent!=null)
                parent.child=this;
        }

        @Override
        public void run() {

            while (runGame)
            {
                if (!isGameOver && !isPause) {
                    if (parent == null) {
                        switch (kierunek) {
                            case Prawa:
                                if (x > getWidth() - width)
                                    GameOver();
                                else
                                    x += v;
                                break;
                            case Lewa:
                                if (x < width)
                                    GameOver();
                                else
                                    x -= v;
                                break;
                            case Gora:
                                if (y < height)
                                    GameOver();
                                else
                                    y -= v;
                                break;
                            case Dol:
                                if (y > getHeight() - height)
                                    GameOver();
                                else
                                    y += v;
                                break;
                        }
                    }

                    int i = 0;
                    //ustawianie pozostalych elemenow weza
                    for (PartOfBodySnake p : SnakeBody) {
                        if (p.parent != null)
                            p.refreshPosition();
                        if (i > 1)
                            if (isColision(p, v)) {
                                GameOver();

                            }
                        i++;

                    }

                    if (isColision(Food, 0) && !Food.colisionDetect) {

                        Log.d(TAG, "Kolizja");
                        Food.colisionDetect = true;//bloakada aby nie mozna bylo wykryc kolizji w kolejnej petli

                        PartOfBodySnake last = SnakeBody.get(SnakeBody.size() - 1);//pobieramy ogon
                        last.Add();
                        Random g = new Random();
                        Food.x = g.nextInt(500);
                        Food.y = g.nextInt(500);
                        Random r=new Random();
                        ptk+=r.nextInt(25);

                    } else if (!isColision(Food, 0))
                        Food.colisionDetect = false;
                }
                try {
                    Thread.sleep(sleepValue);
                } catch (InterruptedException e) {

                }
            }
        }

        public PartOfBodySnake Add() {

            PartOfBodySnake n=new PartOfBodySnake(false, this);
            n.x=this.x;
            n.y=this.y;
            n.kierunek=kierunek;
            switch(kierunek)
            {
                case Prawa:
                    n.x-=this.width;
                    break;
                case Lewa:
                    n.x+=this.width;
                    break;
                case Gora:
                    n.y+=this.height;
                    break;
                case Dol:
                    n.y-=this.height;
                    break;
            }
            n.parent=this;

            SnakeBody.add(n);
            return n;
        }

        public void refreshPosition()
        {
            float rx,ry;
            rx=x-parent.x;
            ry=y-parent.y;

            switch (kierunek) {
                case Prawa:
                    x += v;
                    if(parent.kierunek!=kierunek)
                    {
                        if(x>=parent.x ) {
                            kierunek = parent.kierunek;
                            x=parent.x;
                            if(parent.kierunek==EKierunek.Gora)
                            {
                                y=parent.y+(parent.height);
                            }else
                                if(parent.kierunek==EKierunek.Dol) {
                                    y = parent.y - (parent.height);
                                }
                        }
                    }
                    break;
                case Lewa:
                    x -= v;
                    if(parent.kierunek!=kierunek)
                    {
                        if(x<=parent.x )
                        {
                            kierunek = parent.kierunek;
                            x=parent.x;
                            if(parent.kierunek==EKierunek.Gora)
                            {
                                y=parent.y+(parent.height);
                            }else
                                if(parent.kierunek==EKierunek.Dol)
                                    y=parent.y-(parent.height);
                        }
                    }
                    break;
                case Gora:
                    y -= v;
                    if(parent.kierunek!=kierunek)
                    {
                        if(y<=parent.y ) {
                            kierunek = parent.kierunek;
                            y=parent.y;
                            if(parent.kierunek==EKierunek.Lewa)
                            {
                                x=parent.x+(parent.width);
                            }else
                            if(parent.kierunek==EKierunek.Prawa)
                                x=parent.x-(parent.width);
                        }
                    }
                    break;
                case Dol:
                    y += v;
                    if(parent.kierunek!=kierunek)
                    {
                        if(y>=parent.y ) {
                            kierunek = parent.kierunek;
                            y=parent.y;
                            if(parent.kierunek==EKierunek.Lewa)
                            {
                                x=parent.x+(parent.width);
                            }else
                                if(parent.kierunek==EKierunek.Prawa)
                                    x=parent.x-(parent.width);
                        }
                    }
                    break;
            }

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
                c.drawCircle(x, y, v, paint);
            }else
            if(isHead())
            {
                PaintHead(c);
            }else
                if(isTail())
                {

                }else
                    if(isBody())
                    {
                        PaintBody(c);
                    }

        }

        private void PaintBody(Canvas c) {
            Paint paint = new Paint();
            paint.setColor(color);
            paint.setAntiAlias(true);
            if(parent.kierunek != child.kierunek)
            {
                if((parent.kierunek== EKierunek.Dol && child.kierunek==EKierunek.Prawa)||
                        (child.kierunek== EKierunek.Dol && parent.kierunek==EKierunek.Prawa)    )
                  c.drawBitmap(c_skret_dl_bmp, x, y, paint);
                else
                     if((parent.kierunek== EKierunek.Lewa && child.kierunek==EKierunek.Dol) ||
                             (child.kierunek== EKierunek.Lewa && parent.kierunek==EKierunek.Dol))
                         c.drawBitmap(c_skret_lg, x, y, paint);
                     else
                         if((parent.kierunek== EKierunek.Gora && child.kierunek==EKierunek.Lewa) ||
                                 (child.kierunek== EKierunek.Gora && parent.kierunek==EKierunek.Lewa) )
                             c.drawBitmap(c_skret_pg_bmp, x, y, paint);
                            else
                             c.drawBitmap(c_skret_dp_bmp, x, y, paint);




            }else
                switch(kierunek)
                {
                    case Prawa:
                    case Lewa:
                        c.drawBitmap(c_poziom_bmp, x, y, paint);
                        break;

                    case Gora:
                    case Dol:
                        c.drawBitmap(c_pion_bmp, x, y, paint);
                        break;
                }
        }

        private void PaintHead(Canvas c) {
            Paint paint = new Paint();
            paint.setColor(color);
            paint.setAntiAlias(true);
            switch(kierunek)
            {
                case Prawa:
                    c.drawBitmap(g_p_bmp, x, y, null);
                    break;
                case Lewa:
                    c.drawBitmap(g_l_bmp, x, y, null);
                    break;
                case Gora:
                    c.drawBitmap(g_g_bmp, x, y, null);
                    break;
                case Dol:
                    c.drawBitmap(g_d_bmp, x, y, null);
                    break;
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

        public boolean isColision(PartOfBodySnake p, float tolerancja)
        {
            if(p==null)
                return false;

            if (this==p)
                return false;

            float py_g=y-(height-tolerancja),
                  py_d=y+(height-tolerancja);

            float px_l=x-(width-tolerancja),
                  px_p=x+(width-tolerancja);



            boolean colision= p.x>px_l && p.x<px_p && p.y>py_g && p.y<py_d;


            return colision;
        }

        public void Close() {
            runGame=false;
        }

        public boolean isHead() {
            return parent==null;
        }

        public boolean isBody() {
            return parent!=null;
        }

        public boolean isTail()
        {
            return child==null;
        }
    }

    enum  EKierunek{ Prawa, Lewa, Gora, Dol}

}
