package pl.szkolenie.projekty.animacja;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CustomView extends View{
    int x=50,y=50;
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
        boolean vLeft = false, vUp = false, vHorizontal=true;
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
                if (vHorizontal)
                {
                    if (vLeft)
                    {
                        x -= 15;
                        if (x < 50) {
                            vLeft = false;
                            vHorizontal = !vHorizontal;

                        }
                    }else
                    {
                        x += 15;
                        if (x > getWidth() - 50) {
                            vLeft = true;
                            vHorizontal = !vHorizontal;
                        }
                    }
                } else
                {
                    if (vUp)
                    {
                        y -= 15;
                        if (y < 50)
                        {
                            vUp = false;
                            vHorizontal = !vHorizontal;
                        }
                    } else
                    {
                        y += 15;
                        if (y > getHeight() - 50) {
                            vUp = true;
                            vHorizontal = !vHorizontal;
                        }
                    }
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
}
