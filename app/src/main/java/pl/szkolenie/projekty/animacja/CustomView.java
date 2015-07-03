package pl.szkolenie.projekty.animacja;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class CustomView extends View {
    static Bitmap g_d_bmp, c_pion_bmp, c_poziom_bmp, c_skret_ld_bmp, c_skret_pd_bmp,
            c_skret_lg1_bmp,
            c_skret_pg_bmp, g_g_bmp, g_l_bmp, g_p_bmp, o_d_bmp, o_g_bmp, o_l_bmp, o_p_bmp, cherry_bmp, bk_bmp;
    static String TAG = "Animacja";
    boolean CPU=true;
    int frame=0;
   PartOfBodySnake Head;
    ArrayList<PartOfBodySnake> SnakeBody = new ArrayList<PartOfBodySnake>();
    PartOfBodySnake Food = new PartOfBodySnake(true, null);
    boolean isGameOver = false, isPause = false;
    int ptk = 0, rekord=0;
    String nicknameRekord="", nick;

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

    static RectF GetRectF(float x1, float y1, float x2, float y2) {
        float _x1 = Math.min(x1, x2),
                _x2 = Math.max(x1, x2),
                _y1 = Math.min(y1, y2),
                _y2 = Math.max(y1, y2);

        return new RectF(_x1, _y1, _x2, _y2);
    }


    void showDialogEnterName()
    {
        MainActivity.This.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.This);

                final EditText edittext = new EditText(MainActivity.This);
                if (nick!=null)
                    edittext.setText(nick);
                alert.setMessage("Wprowadź pseudonim");
               // alert.setTitle("Enter Your Title");

                alert.setView(edittext);

                alert.setPositiveButton("Wyślij nam swój wynik", new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface dialog, int whichButton) {
                        nick = edittext.getText().toString();
                        sendScoreToServer();

                    }
                });

                alert.setNegativeButton("Nie wysyłaj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });

                alert.show();
            }
        });

    }

    static RectF GetRectF_IfOk(float x1, float y1, float x2, float y2) {

        if(x1<x2 && y1<y2)
            return new RectF(x1, y1, x2, y2);
        return null;
    }

    void init() {
        if (c_pion_bmp == null) {
            c_pion_bmp = LoadBitmap(R.drawable.c_pion);
            c_poziom_bmp = LoadBitmap(R.drawable.c_poziom);
            c_skret_ld_bmp = LoadBitmap(R.drawable.c_skret_ld);
            c_skret_pd_bmp = LoadBitmap(R.drawable.c_skret_pd);
            c_skret_lg1_bmp = LoadBitmap(R.drawable.c_skret_lg1);

            c_skret_pg_bmp = LoadBitmap(R.drawable.c_skret_pg);
            g_d_bmp = LoadBitmap(R.drawable.g_d);
            g_g_bmp = LoadBitmap(R.drawable.g_g);
            g_l_bmp = LoadBitmap(R.drawable.g_l);
            g_p_bmp = LoadBitmap(R.drawable.g_p);
            o_d_bmp = LoadBitmap(R.drawable.o_d);
            o_g_bmp = LoadBitmap(R.drawable.o_g);
            o_l_bmp = LoadBitmap(R.drawable.o_l);
            o_p_bmp = LoadBitmap(R.drawable.o_p);
            cherry_bmp = LoadBitmap(R.drawable.cherry);
            bk_bmp=LoadBitmap(R.drawable.bk);
        }

        if (Head != null) {
            SnakeBody.clear();
            Head.Close();
        }

        Random g = new Random();
        Food.x = 15;
        Food.y = 15;

        Food.width = 5;
        Food.height = 5;

        Head = new PartOfBodySnake(false, null);
        this.SnakeBody.add(Head);
        Head.x = 200;
        Head.Add().Add().Add().Add().Add().Add().Add().Add().Add().Add();

        isGameOver = false;
        ptk = 0;
        refreshHiScore();
    }

    void refreshHiScore()
    {
        HttpServ.GET(MainActivity.This, "http://192.168.137.1:8080/hiScore.php", null, onGetHiScoreFromServ);
    }

    void sendScoreToServer()
    {
        ContentValues v=new ContentValues();
        v.put("nickname", nick);
        v.put("mobileId", "sdfsdefkshdbfsdhbfjsdhbfsdbfjsdbfdsjvbf");
        v.put("value", ptk);
        HttpServ.GET(MainActivity.This, "http://192.168.137.1:8080/saveScore.php", v, onPostScoreToServ);
   }

    Bitmap LoadBitmap(int resource) {
        try {
            return BitmapFactory.decodeResource(MainActivity.This.getResources(), resource);
        } catch (Exception ex) {
            return null;
        }
    }

    public void GameOver() {
        if (!CPU)
            showDialogEnterName();
        refreshHiScore();
        if (!CPU)
            isGameOver = true;
        else
            Start();
    }



    OnResponseFromServer onPostScoreToServ =new OnResponseFromServer() {
        @Override
        public void Response(String html, boolean success, Exception e) {


                if (success) {
                    if (html != null && html.equals("1")) {
                        Toast.makeText(MainActivity.This, "Wysłano wyniki na serwer", Toast.LENGTH_LONG).show();
                    }
                } else if (e != null) {
                    Toast.makeText(MainActivity.This, "Wystąpił następujący błąd: " + e.getMessage(), Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(MainActivity.This, "Wystąpił błąd, którego programista nie przewidział!", Toast.LENGTH_LONG).show();
        }
    };


    OnResponseFromServer onGetHiScoreFromServ =new OnResponseFromServer() {
        @Override
        public void Response(String html, boolean success, Exception e) {

            if (success) {
                if (html != null &&  html.indexOf(";")>0) {
                   String [] s=html.split(";");
                    try {
                        nicknameRekord = s[0];
                        rekord = Integer.parseInt(s[1]);

                    }
                    catch(Exception ex)
                    {

                    }

                   //Toast.makeText(MainActivity.This, "Wysłano wyniki na serwer", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    public void SetPause() {
        this.isPause = !this.isPause;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float tx = event.getX(), ty = event.getY();
        float rx = tx - Head.x;
        float ry = ty - Head.y;
        switch (Head.direction) {
            case RIGHT: {
                if (ry > Head.height)
                    Down();
                else if (ry < -Head.height)
                    Up();

                break;
            }
            case LEFT: {
                if (ry > Head.height)
                    Down();
                else if (ry < -Head.height)
                    Up();

                break;
            }
            case UP: {
                if (rx > Head.width)
                    Right();
                else if (rx < -Head.width)
                    Left();
                break;
            }
            case DOWN: {

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

    @Override
    protected void onDraw(Canvas c) {
        try {
            //rysowanie tla
            c.drawBitmap(bk_bmp, null, new RectF(0, 0, getWidth(), getHeight()), null);

            //rysowanie pokarmu
            Food.Paint(c);

            //rysowanie weza
            try {
                for (PartOfBodySnake p : SnakeBody)
                    if (!p.isHead())
                        p.Paint(c);
            } catch (Exception ex) {
            }

            Head.Paint(c);

            if (isGameOver) {
                Random rand = new Random();
                Paint p = new Paint();
                p.setColor(Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
                p.setTextSize(60);
                p.setTextAlign(Paint.Align.CENTER);
                c.drawText("Game Over", getWidth() / 2, getHeight() / 2, p);
                c.drawText("Zdobyłeś(aś)" + ptk + " ptk.", getWidth() / 2, getHeight() / 2 + 50, p);

            } else if (isPause) {
                Paint p = new Paint();
                p.setColor(Color.GRAY);
                p.setTextSize(60);
                p.setTextAlign(Paint.Align.CENTER);
                c.drawText("Pause", getWidth() / 2, getHeight() / 2, p);
            } else {
                Paint p = new Paint();
                p.setColor(Color.BLACK);
                p.setTextSize(30);
                p.setTextAlign(Paint.Align.LEFT);
                if(nick==null)
                    c.drawText("Punkty: " + ptk, 25, 50, p);
                else
                    c.drawText(nick+": " + ptk, 25, 50, p);
                if(nicknameRekord==null)
                    nicknameRekord="";
                c.drawText("Rekord: "+ rekord+" - "+nicknameRekord,25,75, p);
            }

            invalidate();
        } catch (Exception ex) {

        }
    }



    public void Start() {
        if (!Head.isAlive())
            Head.start();
        else {
            init();
            Head.start();
        }
    }

    public void Left() {
        PartOfBodySnake b1 = SnakeBody.get(1);
        float y1, y2;
        y1 = b1.getPosition().Pos.top;
        y2 = Head.getPosition().Pos.top;
        float ry = Math.max(y1, y2) - Math.min(y1, y2) + 3;

        if (ry >= Head.height)
            Head.Left();
    }

    public void Down() {
        PartOfBodySnake b1 = SnakeBody.get(1);
        float rx = Math.max(b1.x, Head.x) - Math.min(b1.x, Head.x) + 3;

        if (rx >= Head.width)
            Head.Down();
    }

    public void Right() {
        PartOfBodySnake b1 = SnakeBody.get(1);
        float ry = Math.max(b1.y, Head.y) - Math.min(b1.y, Head.y) + 3;

        if (ry >= Head.height)
            Head.Right();
    }

    public void Up() {
        PartOfBodySnake b1 = SnakeBody.get(1);
        float rx = Math.max(b1.x, Head.x) - Math.min(b1.x, Head.x) + 3;

        if (rx >= Head.width)
            Head.Up();
    }


    public class PartOfBodySnake extends Thread {

        PartOfBodySnake parent = null;
        PartOfBodySnake child = null;
        RectF PaintedRc=new RectF(0,0,0,0);


        boolean isFood = false, colisionDetect = false, runGame = true;
        float x = 50, y = 75, width = 42, height = 42, v = 5;
        int No = 1;

        private EDIRECTION direction = EDIRECTION.RIGHT;

        int sleepValue = 20;

        float vx = -5, vy = -5;//kiedy ma zmienić direction
        EDIRECTION vDirection; //na jaki

        //constructor
        public PartOfBodySnake(boolean isFood, PartOfBodySnake parent) {
            this.isFood = isFood;
            if (parent != null)
                parent.child = this;
        }
        int margin=15;

        void setDirectionRandom(boolean left,boolean right,boolean up,boolean down)
        {
            if (!left && !right && !up && !down)
                return ;

            while(true)
            {
                Log.d(MainActivity.TAG, "setDirectionRandom");
                Random r=new Random();
                int i=r.nextInt(4);
                switch(i)
                {
                    case 0:
                        if(left)
                           Left();
                        return;
                    case 1:
                        if(right)
                           Right();
                        return;
                    case 2:
                        if(up)
                            Up();
                        return;
                    case 3:
                        if(down)
                            Down();
                        return;
                }
            }
        }
        int c=100;
        private void cpuControl()
        {
            c--;
            if (c<0)
            {
                setDirectionRandom(true, true, true, true);
                Random r=new Random();
                c=r.nextInt(50)+50;
            }
            switch (direction) {
                case RIGHT:
                    if (x > getWidth() - (width+margin))
                       setDirectionRandom(false, false, true, true);


                    break;
                case LEFT:
                    if (x < margin)
                        setDirectionRandom(false, false, true, true);

                    break;
                case UP:
                    if (y < margin)
                        setDirectionRandom(true, true, false, false);

                    break;
                case DOWN:
                    if (y > getHeight()-margin)
                        setDirectionRandom(true, true, false, false);
                    break;
            }
        }


        @Override
        public void run() {

            while (runGame) {
                if (!isGameOver && !isPause) {
                    if (CPU) {
                        this.cpuControl();
                    }
                    switch (direction) {
                        case RIGHT:
                            if (x > getWidth() - width)
                                GameOver();
                            else
                                x += v;
                            break;
                        case LEFT:
                            if (x < 0)
                                GameOver();
                            else
                                x -= v;
                            break;
                        case UP:
                            if (y < 0)
                                GameOver();
                            else
                                y -= v;
                            break;
                        case DOWN:
                            if (y > getHeight())
                                GameOver();
                            else
                                y += v;
                            break;
                    }


                    int i = 0;

                    //ustawianie pozostalych elemenow weza
                    try {
                        for (PartOfBodySnake p : SnakeBody) {
                            if (p.parent != null)
                                p.refreshPositionBody();
                            if (i > 1)
                                if (isColision(p, v)) {
                                    GameOver();

                                }
                            i++;
                        }
                    } catch (Exception ex) {
                    }

                    if (isColision(Food, 0) && !Food.colisionDetect) {

                        Log.d(TAG, "Kolizja");
                        Food.colisionDetect = true;//blokada aby nie mozna bylo wykryc kolizji w kolejnej petli

                        PartOfBodySnake last = SnakeBody.get(SnakeBody.size() - 1);//pobieramy ogon
                        last.Add();
                        Random g = new Random();
                        Food.x = g.nextInt(getWidth() - 50);
                        Food.y = g.nextInt(getHeight() - 50);
                        ptk += g.nextInt(25);

                    } else if (!isColision(Food, 0))
                        Food.colisionDetect = false;
                }
                try {
                    frame++;
                    Thread.sleep(sleepValue);

                } catch (InterruptedException e) {

                }
            }
        }


        public PartOfBodySnake Add() {

            PartOfBodySnake n = new PartOfBodySnake(false, this);
            n.x = this.x;
            n.y = this.y;
            n.direction = direction;
            switch (direction) {
                case RIGHT:
                    n.x -= this.width;
                    break;
                case LEFT:
                    n.x += this.width;
                    break;
                case UP:
                    n.y += this.height;
                    break;
                case DOWN:
                    n.y -= this.height;
                    break;
            }
            n.parent = this;

            SnakeBody.add(n);
            this.No = SnakeBody.size() - 1;
            return n;
        }

        public void refreshPositionBody() {
            boolean changeDirection = false;
            if (isChangeDirection()) {
                vx = parent.vx;
                vy = parent.vy;
                changeDirection = true;
                setChangeDirection(parent.vDirection);
            }
            Position parentPos = parent.getPosition();

            switch (direction) {
                case RIGHT:
                    if (changeDirection)
                        y = parent.y;
                    x += v;
                    break;
                case LEFT:
                    if (changeDirection)
                        y = parent.y;
                    x -= v;
                    break;
                case UP:
                    if (changeDirection)
                        x = parent.x;
                    y -= v;
                    break;
                case DOWN:
                    if (changeDirection)
                        x = parent.x;
                    y += v;

                    break;
            }
        }

        public void SetSleepValue(int newValue) {
            this.sleepValue = newValue;
        }

        public void Paint(Canvas c) {
            if (isFood) {
                PaintCherry(c);
            } else if (isBody()) {
                PaintBody(c);
            } else if (isHead()) {
                PaintHead(c);
            } else if (isTail()) {
                PaintTail(c);
            }
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setAntiAlias(true);
            paint.setTextSize(15);
           /* if (isHead()) {
                c.drawText("Głowa: " + x + "|" + y, x + 50, y, paint);
            } else if (isTail()) {
                RectF parentRc = parent.getPosition().Pos;
                c.drawText("przedogon: " + parentRc, parent.x + 50, parent.y - 60, paint);
                c.drawText("Ogon: " + getPosition().Pos, x + 50, y, paint);
            }*/
        }

        private void PaintCherry(Canvas c) {

            RectF dsc = getPosition().Pos;
            dsc.left -= 20;
            dsc.right += 20;
            dsc.top -= 20;
            dsc.bottom += 20;
            c.drawBitmap(cherry_bmp, null, dsc, null);
            PaintedRc=dsc;

        }

        private void PaintTail(Canvas c) {

            EDIRECTION direction = parent.direction;


            RectF dsc = parent.lastPosition.Pos;
            switch (direction) {
                case RIGHT: {
                    dsc = new RectF(dsc.left - width, dsc.top, dsc.left, dsc.bottom);
                    c.drawBitmap(o_p_bmp, null, dsc, null);
                    break;
                }
                case LEFT: {

                    dsc = new RectF(dsc.right, dsc.top, dsc.right + width, dsc.bottom);
                    c.drawBitmap(o_l_bmp, null, dsc, null);
                    break;
                }
                case UP: {
                    dsc = new RectF(dsc.left, dsc.bottom, dsc.right, dsc.bottom + height);
                    c.drawBitmap(o_g_bmp, null, dsc, null);
                    break;
                }
                case DOWN: {

                    dsc = new RectF(dsc.left, dsc.top - height, dsc.right, dsc.top);
                    c.drawBitmap(o_d_bmp, null, dsc, null);
                    break;
                }
            }
            PaintedRc=dsc;
        }

        private void PaintBody(Canvas c) {
            RectF dsc = null;
            if (parent.direction != direction)
                PaintBodyCurve(c);
            else {
                Position posParent = parent.getPosition();
                Position posChild = child.getPosition();
                lastPosition = getPosition();
                switch (direction) {
                    case RIGHT: {
                        dsc = GetRectF(posChild.Pos.right - 2, y, posParent.Pos.left + 2, y + height);
                        c.drawBitmap(c_poziom_bmp, null, dsc, null);
                        break;
                    }
                    case LEFT: {
                        dsc = GetRectF(posChild.Pos.left + 2, y, posParent.Pos.right - 2, y + height);
                        c.drawBitmap(c_poziom_bmp, null, dsc, null);
                        break;
                    }
                    case UP: {
                        dsc = GetRectF(x, posParent.Pos.bottom - 2, x + height, posChild.Pos.top + 2);
                        c.drawBitmap(c_pion_bmp, null, dsc, null);
                        break;
                    }
                    case DOWN: {
                        dsc = GetRectF(x, posParent.Pos.top + 2, x + height, posChild.Pos.bottom - 2);
                        c.drawBitmap(c_pion_bmp, null, dsc, null);
                        break;
                    }
                }
                PaintedRc=dsc;
                lastPosition.Pos = dsc;
            }

        }



        private void PaintBodyCurve(Canvas c) {

            Position p = this.getPosition();
            RectF rcParent = parent.PaintedRc;

            switch (p.curve) {
                case None:
                    break;
                case L_D: {
                    RectF r = GetRectF_IfOk(p.Pos.left, p.Pos.bottom, p.Pos.right, rcParent.top);
                    if(r!=null)
                        c.drawBitmap(c_pion_bmp, null, r, null);

                    r = GetRectF_IfOk(rcParent.right, p.Pos.top, p.Pos.left, p.Pos.bottom);//dopelnienie po lewej stronie
                    if(r!=null)
                        c.drawBitmap(c_poziom_bmp, null, r, null);

                    c.drawBitmap(c_skret_ld_bmp, null, p.Pos, null);

                    break;
                }
                case L_U: {//lewa gora
                    RectF r =GetRectF_IfOk(p.Pos.left, rcParent.bottom, p.Pos.right, p.Pos.top);
                    if(r!=null)
                        c.drawBitmap(c_pion_bmp, null, r, null);//dopelnienie na gorze pionowe

                    r = GetRectF_IfOk(rcParent.right, p.Pos.top, p.Pos.left, p.Pos.bottom);//dopelnienie po lewej stronie
                    if(r!=null)
                        c.drawBitmap(c_poziom_bmp, null, r, null);
                    c.drawBitmap(c_skret_lg1_bmp, null, p.Pos, null);

                    break;
                }
                case R_D: {
                    RectF r = GetRectF_IfOk(p.Pos.left, p.Pos.bottom, p.Pos.right, rcParent.top);
                    if(r!=null)
                        c.drawBitmap(c_pion_bmp, null, r, null);

                    r = GetRectF_IfOk(p.Pos.right, p.Pos.top, rcParent.left, p.Pos.bottom);//dopelnienie po prawej stronie stronie
                    if(r!=null)
                        c.drawBitmap(c_poziom_bmp, null, r, null);
                    c.drawBitmap(c_skret_pd_bmp, null, p.Pos, null);
                    break;
                }
                case R_U: {
                    RectF r = GetRectF_IfOk(p.Pos.left, rcParent.bottom, p.Pos.right, p.Pos.top);
                    if(r!=null)
                        c.drawBitmap(c_pion_bmp, null, r, null);

                    r = GetRectF_IfOk(p.Pos.right, p.Pos.top, rcParent.left, p.Pos.bottom);//dopelnienie po prawej stronie stronie
                    if(r!=null)
                        c.drawBitmap(c_poziom_bmp, null, r, null);
                    c.drawBitmap(c_skret_pg_bmp, null, p.Pos, null);
                    break;
                }
            }
            lastPosition.Pos=p.Pos;
            PaintedRc=p.Pos;
        }

        private void PaintHead(Canvas c) {
            RectF pos = getPosition().Pos;
            switch (direction) {
                case RIGHT:

                    c.drawBitmap(g_p_bmp, null, pos, null);
                    break;
                case LEFT:

                    c.drawBitmap(g_l_bmp, null, pos, null);
                    break;
                case UP:

                    c.drawBitmap(g_g_bmp, null, pos, null);
                    break;
                case DOWN:

                    c.drawBitmap(g_d_bmp, null, pos, null);
                    break;
            }
            PaintedRc=pos;
        }

        void setChangeDirection(EDIRECTION direction) {
            //Log.d(TAG, No + "Zmiana kierunku z " + this.direction + " na " + direction);
            if (isHead())
                switch (this.direction) {
                    case RIGHT:
                        this.vx = x + width;
                        this.vy = y + Math.round(height / 2);
                        break;
                    case LEFT:
                        this.vx = x;
                        this.vy = y + Math.round(height / 2);
                        break;
                    case UP:
                        this.vx = x + Math.round(width / 2);
                        this.vy = y;
                        break;
                    case DOWN:
                        this.vx = x + Math.round(width / 2);
                        this.vy = y + height;
                        break;
                }
            this.vDirection = direction;
            this.direction = direction;
        }

        PointF a, b;

        boolean isChangeDirection() {

            if (parent != null) {
                switch (direction) {
                    case RIGHT:
                        a = new PointF(x + width, y);
                        b = new PointF(x + width, y + height);
                        return a.y <= parent.vy && b.y >= parent.vy &&
                                a.x >= parent.vx;
                    case LEFT:
                        a = new PointF(x, y);
                        b = new PointF(x, y + height);
                        return a.y <= parent.vy && b.y >= parent.vy &&
                                a.x <= parent.vx;
                    case UP:
                        a = new PointF(x, y);
                        b = new PointF(x + width, y);
                        return a.x <= parent.vx && b.x >= parent.vx &&
                                a.y <= parent.vy;
                    case DOWN:
                        a = new PointF(x, y + height);
                        b = new PointF(x + width, y + height);
                        return a.x <= parent.vx && b.x >= parent.vx &&
                                a.y >= parent.vy;
                }
            }

            if (child != null) {
                return this.vx != child.vx && this.vy != child.vy && !isGameOver && !isPause;
            } else
                return false;
        }


        public void Left() {
            if (direction != EDIRECTION.RIGHT && !isChangeDirection()) {
                setChangeDirection(EDIRECTION.LEFT);
                direction = EDIRECTION.LEFT;
            }
        }

        public void Down() {
            if (direction != EDIRECTION.UP && !isChangeDirection()) {
                setChangeDirection(EDIRECTION.DOWN);
                direction = EDIRECTION.DOWN;
            }
        }

        public void Right() {
            if (direction != EDIRECTION.LEFT && !isChangeDirection()) {
                setChangeDirection(EDIRECTION.RIGHT);
                direction = EDIRECTION.RIGHT;
            }
        }

        public void Up() {
            if (direction != EDIRECTION.DOWN && !isChangeDirection()) {
                setChangeDirection(EDIRECTION.UP);
                direction = EDIRECTION.UP;
            }
        }

        public boolean isColision(PartOfBodySnake p, float tolerancja) {
            if (p == null)
                return false;

            if (this == p)
                return false;

            float py_g = y - (height - tolerancja),
                    py_d = y + (height - tolerancja);

            float px_l = x - (width - tolerancja),
                    px_p = x + (width - tolerancja);


            boolean colision = p.x > px_l && p.x < px_p && p.y > py_g && p.y < py_d;

            return colision;
        }

        public void Close() {
            runGame = false;
        }

        public boolean isHead() {
            return parent == null && !isFood;
        }

        public boolean isBody() {
            return parent != null && child != null;
        }

        public boolean isTail() {
            return child == null && !isFood;
        }

        public boolean isCurve() {
            return parent != null && parent.direction != direction;
        }

        public Position getPosition() {
            return getPosition(this.direction);
        }

        public Position getLastPosition() {
            return getPosition(this.direction);
        }

        Position lastPosition;

        public Position getPosition(EDIRECTION direction) {
            Position ret = null;
            if (isCurve()) {
                if (parent.direction == EDIRECTION.DOWN && direction == EDIRECTION.RIGHT) { //z lewej w dół
                    ret = new Position(ECURVE.L_D, GetRectF(parent.x, y, parent.x + width, y + height));
                } else if (direction == EDIRECTION.UP && parent.direction == EDIRECTION.LEFT) { //z dołu do lewej
                    ret = new Position(ECURVE.L_D, GetRectF(x, parent.y, x + width, parent.y + height));
                } else if (parent.direction == EDIRECTION.DOWN && direction == EDIRECTION.LEFT) { //z prawej w dół
                    ret = new Position(ECURVE.R_D, GetRectF(parent.x, y, parent.x + width, y + height));
                } else if (direction == EDIRECTION.UP && parent.direction == EDIRECTION.RIGHT) { //z dołu do prawej
                    ret = new Position(ECURVE.R_D, GetRectF(x, parent.y, x + width, parent.y + height));
                } else if (parent.direction == EDIRECTION.UP && direction == EDIRECTION.RIGHT) { //z lewej w gore
                    ret = new Position(ECURVE.L_U, GetRectF(parent.x, y, parent.x + width, y + height));
                } else if (direction == EDIRECTION.DOWN && parent.direction == EDIRECTION.LEFT) { //z gory do prawej
                    ret = new Position(ECURVE.L_U, GetRectF(x, parent.y, x + width, parent.y + height));
                } else if (parent.direction == EDIRECTION.UP && direction == EDIRECTION.LEFT) { //z prawej w gore
                    ret = new Position(ECURVE.R_U, GetRectF(parent.x, y, parent.x + width, y + height));
                } else if (direction == EDIRECTION.DOWN && parent.direction == EDIRECTION.RIGHT) { //z gory do prawej
                    ret = new Position(ECURVE.R_U, GetRectF(x, parent.y, x + width, parent.y + height));
                }
                if (ret != null) {
                    lastPosition = ret;
                    return ret;
                }
            }
            ret = new Position(ECURVE.None, GetRectF(x, y, x + width, y + height));
            lastPosition = ret;
            return ret;
        }
    }

    enum EDIRECTION {RIGHT, LEFT, UP, DOWN}

    public enum ECURVE {None, L_D, L_U, R_D, R_U}

    class Position {
        public Position(ECURVE c, RectF pos) {
            this.curve = c;
            this.Pos = pos;
        }

        public ECURVE curve = ECURVE.None;
        public RectF Pos;
    }
}
