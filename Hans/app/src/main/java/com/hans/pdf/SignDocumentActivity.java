package com.hans.pdf;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hans.DatabaseFirebase;
import com.hans.MainActivity;
import com.hans.R;
import com.hans.deliverer.DelivererInTransitOrdersFragment;
import com.hans.domain.Order;
import com.hans.domain.OrderStatus;
import com.hans.domain.User;

public class SignDocumentActivity extends AppCompatActivity
{
    SignDocumentActivity.DrawingView dv;
    private Paint mPaint;
    private Path mPath;
    private Canvas canvas;
    private Order order;
    private User client, deliverer;
    private static int PAGE_WIDTH = 842;
    private static int PAGE_HEIGHT = 595;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sign_document);
        context = getBaseContext();

        Intent intent = getIntent();
        String orderJSON = intent.getStringExtra("order");
        String clientJSON = intent.getStringExtra("client");
        String delivererJSON = intent.getStringExtra("deliverer");
        final String receiver_firstname = intent.getStringExtra("receiver_firstname");
        final String receiver_lastname = intent.getStringExtra("receiver_lastname");

        order = Order.createFromJSON(orderJSON);
        client = User.createFromJSON(clientJSON);
        deliverer = User.createFromJSON(delivererJSON);

        Button confirmButton = findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatabaseFirebase db = new DatabaseFirebase();
                order.setOrderStatus(OrderStatus.CLOSED);
                db.setOrder(order);
//                MainActivity.sendNotificationToClient(order);

//                Matrix rotateMatrix = new Matrix();
//                rotateMatrix.setRotate(270f, (float)(canvas.getWidth()/2), (float)(canvas.getHeight()/2));
//                mPath.transform(rotateMatrix);

                Float scaleX = (float)PAGE_WIDTH/(float)canvas.getWidth();
                Float scaleY = (float)PAGE_HEIGHT/(float)canvas.getHeight();


                Matrix scaleMatrix = new Matrix();
                scaleMatrix.setScale(scaleX, scaleY);
                mPath.transform(scaleMatrix);

                Matrix scaleMatrix2 = new Matrix();
                scaleMatrix2.setScale(0.25f, 0.25f, (float)PAGE_HEIGHT/2, (float)PAGE_WIDTH/2);
                mPath.transform(scaleMatrix2);

                Matrix translateMatrix = new Matrix();
                translateMatrix.setTranslate(400f, 100f);
                mPath.transform(translateMatrix);

                PdfGenerator pdfGenerator = new PdfGenerator(getBaseContext(), order);
                pdfGenerator.createPdf(client, deliverer, receiver_firstname, receiver_lastname);
                pdfGenerator.signInDocument(mPath);
                pdfGenerator.saveLocal(order.getId());
                pdfGenerator.sendToFirebaseStorage(order.getId());

                mPath.reset();
                canvas.drawColor(Color.WHITE);
                dv.invalidate();

                Intent newIntent = new Intent(getBaseContext(), MainActivity.class);
                newIntent.putExtra("documentGenerated", "true");
                startActivity(newIntent);

//                Fragment newFragment = new DelivererInTransitOrdersFragment();
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment, newFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();

//                Snackbar.make(getView(), "Zakończono zlecenie", Snackbar.LENGTH_SHORT).show();
//                finishOrder();
//                sendNotificationToClient();


//                try
//                {
//                    Thread.sleep(2000);
//                    pdfGenerator.downloadFileFromFirebaseStorage("mypdf");
//                } catch (Exception e)
//                {
//
//                }


            }
        });

        Button clearButton = findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                mPath.reset();
                canvas.drawColor(Color.WHITE);
                dv.invalidate();

            }
        });

        dv = new SignDocumentActivity.DrawingView(getBaseContext());
        LinearLayout l = findViewById(R.id.sign_document_layout);
        l.addView(dv);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

    }

    public class DrawingView extends View
    {
        public int width;
        public int height;
        private Bitmap mBitmap;
        //        private Canvas mCanvas;
//        private Path mPath;
        private Paint mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;


        public DrawingView(Context c)
        {
            super(c);

            context = c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);

        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh)
        {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Log.d("signnn", Integer.toString(w) + " " + Integer.toString(h));
            canvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
            canvas.drawPath(circlePath, circlePaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y)
        {
//            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y)
        {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)
            {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up()
        {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            canvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
//            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }

    }
}
