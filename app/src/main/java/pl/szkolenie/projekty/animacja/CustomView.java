package pl.szkolenie.projekty.animacja;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.Random;

public class CustomView extends View {
    static Bitmap g_d_bmp, c_pion_bmp, c_poziom_bmp, c_skret_ld_bmp, c_skret_pd_bmp, c_skret_lg_bmp,
            c_skret_pg_bmp, g_g_bmp, g_l_bmp, g_p_bmp, o_d_bmp, o_g_bmp, o_l_bmp, o_p_bmp;
    static String TAG = "Animacja";
    PartOfBodySnake Head;
    ArrayList<PartOfBodySnake> SnakeBody = new ArrayList<PartOfBodySnake>();
    PartOfBodySnake Food = new PartOfBodySnake(true, null);
    boolean isGameOver = false, isPause = false;
    int ptk = 0;

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

    void init() {
        if (c_pion_bmp == null) {
            c_pion_bmp = LoadBitmap(R.drawable.c_pion);
            c_poziom_bmp = LoadBitmap(R.drawable.c_poziom);
            c_skret_ld_bmp = LoadBitmap(R.drawable.c_skret_ld);
            c_skret_pd_bmp = LoadBitmap(R.drawable.c_skret_pd);
            c_skret_lg_bmp = LoadBitmap(R.drawable.c_skret_lg);
            c_skret_pg_bmp = LoadBitmap(R.drawable.c_skret_pg);
            g_d_bmp = LoadBitmap(R.drawable.g_d);
            g_g_bmp = LoadBitmap(R.drawable.g_g);
            g_l_bmp = LoadBitmap(R.drawable.g_l);
            g_p_bmp = LoadBitmap(R.drawable.g_p);
            o_d_bmp = LoadBitmap(R.drawable.o_d);
            o_g_bmp = LoadBitmap(R.drawable.o_g);
            o_l_bmp = LoadBitmap(R.drawable.o_l);
            o_p_bmp = LoadBitmap(R.drawable.o_p);
        }

        if (Head != null) {
            SnakeBody.clear();
            Head.Close();
        }

        Food.x = 500;
        Food.y = 500;
        Food.width = 5;
        Food.height = 5;

        Head = new PartOfBodySnake(false, null);
        this.SnakeBody.add(Head);
        Head.color = Color.GREEN;
        Head.x = 200;
        Head.Add().Add().Add().Add().Add().Add().Add().Add().Add().Add();

        isGameOver = false;
        ptk = 0;
    }

    Bitmap LoadBitmap(int resource) {
        try {
            return BitmapFactory.decodeResource(MainActivity.This.getResources(), resource);
        } catch (Exception ex) {
            return null;
        }
    }

    public void GameOver() {
        isGameOver = true;
    }

    public void SetPause() {
        this.isPause = !this.isPause;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float tx = event.getX(), ty = event.getY();
        float rx = tx - Head.x;
        float ry = ty - Head.y;
        LOG_Kulka(rx, ry);
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

    private void LOG_Kulka(float rx, float ry) {
        //Log.d("Animacja", Head.direction+" rx="+rx+"; ry="+ry);
    }

    //int red, green, blue;

    @Override
    protected void onDraw(Canvas c) {

        //rysowanie pokarmu
        Food.Paint(c);

        //rysowanie weza
        for (PartOfBodySnake p : SnakeBody)
            if (!p.isHead())
                p.Paint(c);

        Head.Paint(c);

        if (isGameOver) {
            Random rand = new Random();
            Paint p = new Paint();
            p.setColor(Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
            p.setTextSize(60);
            p.setTextAlign(Paint.Align.CENTER);
            c.drawText("Game Over\n zdobyłeś(aś) " + ptk + " punktów", getWidth() / 2, getHeight() / 2, p);
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
            c.drawText("Punkty: " + ptk, 25, 50, p);
        }



      /*  Paint paint=new Paint();
        paint.setColor(Color.YELLOW);


        c.drawCircle(200, 200, 20, paint);*/

        invalidate();
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

        //boolean skret=false;
        PartOfBodySnake parent = null;
        PartOfBodySnake child = null;

        boolean isFood = false, colisionDetect = false, runGame = true;
        float x = 50, y = 75, width = 42, height = 42, v = 5;
        int No = 1;
        int color = Color.parseColor("#0000FF");
        private EKierunek direction = EKierunek.RIGHT;

        int sleepValue = 20;

        float vx = -5, vy = -5;//kiedy ma zmienić direction
        EKierunek vDirection; //na jaki

        //constructor
        public PartOfBodySnake(boolean isFood, PartOfBodySnake parent) {
            this.isFood = isFood;
            if (parent != null)
                parent.child = this;
        }

        @Override
        public void run() {

            while (runGame) {
                if (!isGameOver && !isPause) {
                    if (parent == null) {
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
                    }

                    int i = 0;
                    //ustawianie pozostalych elemenow weza
                    for (PartOfBodySnake p : SnakeBody) {
                        if (p.parent != null)
                            p.refreshPositionBody();
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
                        Random r = new Random();
                        ptk += r.nextInt(25);

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
           boolean changeDirection=false;
            if (isChangeDirection()) {
                vx = parent.vx;
                vy = parent.vy;
                changeDirection=true;
                setChangeDirection(parent.vDirection);
            }
            Position parentPos = parent.getPosition();

            switch (direction) {
                case RIGHT:
                    if(changeDirection)
                        y=parent.y;
                    x += v;
                    break;
                case LEFT:
                    if(changeDirection)
                        y=parent.y;
                    x -= v;
                    break;
                case UP:
                    if(changeDirection)
                        x=parent.x;
                    y -= v;
                    break;
                case DOWN:
                    if(changeDirection)
                        x=parent.x;
                    y += v;

                    break;
            }
        }

        public void SetSleepValue(int newValue) {
            this.sleepValue = newValue;
        }

        public void Paint(Canvas c) {
            if (isFood) {
                Paint paint = new Paint();
                paint.setColor(Color.GREEN);
                paint.setAntiAlias(true);
                c.drawCircle(x, y, v, paint);
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
            if (isHead()) {
                c.drawText("Głowa: " + x + "|" + y, x + 50, y, paint);
            } else if (isTail()) {
                c.drawText("Ogon: " + x + "|" + y, x + 50, y, paint);
            }

           /* c.drawRect(vx, vy, vx + 5, vy + 5, paint);
            paint.setColor(Color.RED);
            c.drawCircle(x, y, 1, paint);
            if (a != null && b != null) {
                paint.setColor(Color.YELLOW);
                c.drawCircle(a.x, a.y, 5, paint);
                c.drawCircle(b.x, b.y, 5, paint);
            }*/
        }

        private void PaintTail(Canvas c) {
            RectF dsc = this.getPosition().Pos;
           switch (direction) {
                case RIGHT: {
                    c.drawBitmap(o_p_bmp, null, dsc, null);
                    break;
                }
                case LEFT: {
                    c.drawBitmap(o_l_bmp, null, dsc, null);
                    break;
                }
                case UP: {
                    c.drawBitmap(o_g_bmp, null, dsc, null);
                    break;
                }
                case DOWN: {
                    c.drawBitmap(o_d_bmp, null, dsc, null);

                    break;
                }
            }
        }

        private void PaintBody(Canvas c) {
            RectF dsc = null;
            if (parent.direction != direction)
                PaintBodyCurve(c);
            else {
                Position posParent = parent.getPosition();
                Position posChild = child.getPosition();
                switch (direction) {
                    case RIGHT: {
                        dsc = GetRectF(posChild.Pos.right-2, y, posParent.Pos.left+2, y + height);
                        c.drawBitmap(c_poziom_bmp, null, dsc, null);
                        break;
                    }
                    case LEFT: {
                        dsc = GetRectF(posChild.Pos.left+2, y, posParent.Pos.right-2, y + height);
                        c.drawBitmap(c_poziom_bmp, null, dsc, null);
                        break;
                    }
                    case UP: {
                        dsc = GetRectF(x, posParent.Pos.bottom-2, x + height, posChild.Pos.top+2);
                        c.drawBitmap(c_pion_bmp, null, dsc, null);
                        break;
                    }
                    case DOWN: {
                        dsc = GetRectF(x, posParent.Pos.top+2, x + height, posChild.Pos.bottom-2);
                        c.drawBitmap(c_pion_bmp, null, dsc, null);

                        break;
                    }
                }
            }
        }

        private void PaintBodyCurve(Canvas c) {
            Position p = this.getPosition();
            switch (p.curve) {
                case None:
                    break;
                case L_D: {
                    RectF r = new RectF(p.Pos.left, p.Pos.bottom, p.Pos.right, parent.getPosition().Pos.top);
                    c.drawBitmap(c_pion_bmp, null, r, null);
                    c.drawBitmap(c_skret_ld_bmp, null, p.Pos, null);

                    break;
                }
                case L_U: {
                    RectF r = new RectF(p.Pos.left, parent.getPosition().Pos.bottom, p.Pos.right, p.Pos.top );
                    c.drawBitmap(c_pion_bmp, null, r, null);

                    c.drawBitmap(c_skret_lg_bmp, null, p.Pos, null);
                    break;
                }
                case R_D:
                    c.drawBitmap(c_skret_pd_bmp, null, p.Pos, null);
                    break;
                case R_U:
                    c.drawBitmap(c_skret_pg_bmp, null, p.Pos, null);
                    break;
            }
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
        }

        void setChangeDirection(EKierunek direction) {
            Log.d(TAG, No + "Zmiana kierunku z " + this.direction + " na " + direction);
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

                /*return
                        (parent.vx >= this.x && parent.vx <= this.x + 2) &&
                                (parent.vy >= this.y && parent.vy <= this.y + 2) &&
                                parent.vDirection != direction;*/
            }

            if (child != null) {
                return this.vx != child.vx && this.vy != child.vy;
            } else
                return false;
        }

        public void Left() {
            if (direction != EKierunek.RIGHT && !isChangeDirection()) {
                setChangeDirection(EKierunek.LEFT);
                direction = EKierunek.LEFT;

                Log.d(TAG, "LEWA");
            }
        }

        public void Down() {
            if (direction != EKierunek.UP && !isChangeDirection()) {
                setChangeDirection(EKierunek.DOWN);
                direction = EKierunek.DOWN;

                Log.d(TAG, "DOL");
            }
        }

        public void Right() {
            if (direction != EKierunek.LEFT && !isChangeDirection()) {
                setChangeDirection(EKierunek.RIGHT);
                direction = EKierunek.RIGHT;

                Log.d(TAG, "PRAWA");
            }
        }

        public void Up() {
            if (direction != EKierunek.DOWN && !isChangeDirection()) {
                setChangeDirection(EKierunek.UP);
                direction = EKierunek.UP;

                Log.d(TAG, "GORA");
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
            return child == null;
        }

        public boolean isCurve() {
            return parent != null && parent.direction != direction;
        }


        public Position getPosition() {
            if (isCurve()) {
                if (parent.direction == EKierunek.DOWN && direction == EKierunek.RIGHT) { //z lewej w dół
                    return new Position(ECURVE.L_D, GetRectF(parent.x, y, parent.x + width, y + height));
                } else if (direction == EKierunek.UP && parent.direction == EKierunek.LEFT) { //z dołu do lewej
                    return new Position(ECURVE.L_D, GetRectF(x, parent.y, x + width, parent.y + height));
                } else if (parent.direction == EKierunek.DOWN && direction == EKierunek.LEFT) { //z prawej w dół
                    return new Position(ECURVE.R_D, GetRectF(parent.x, y, parent.x + width, y + height));
                } else if (direction == EKierunek.UP && parent.direction == EKierunek.RIGHT) { //z dołu do prawej
                    return new Position(ECURVE.R_D, GetRectF(x, parent.y, x + width, parent.y + height));
                } else if (parent.direction == EKierunek.UP && direction == EKierunek.RIGHT) { //z lewej w gore
                    return new Position(ECURVE.L_U, GetRectF(parent.x, y, parent.x + width, y + height));
                } else if (direction == EKierunek.DOWN && parent.direction == EKierunek.LEFT) { //z gory do prawej
                    return new Position(ECURVE.L_U, GetRectF(x, parent.y, x + width, parent.y + height));
                } else if (parent.direction == EKierunek.UP && direction == EKierunek.LEFT) { //z prawej w gore
                    return new Position(ECURVE.R_U, GetRectF(parent.x, y, parent.x + width, y + height));
                } else if (direction == EKierunek.DOWN && parent.direction == EKierunek.RIGHT) { //z gory do prawej
                    return new Position(ECURVE.R_U, GetRectF(x, parent.y, x + width, parent.y + height));
                }
            }
            return new Position(ECURVE.None, GetRectF(x, y, x + width, y + height));
        }
    }

    enum EKierunek {RIGHT, LEFT, UP, DOWN}

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
