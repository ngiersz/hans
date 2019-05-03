package com.hans;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hans.pdf.PdfGenerator;

public class SignDocumentFragment extends Fragment
{
    View view;
    DrawingView dv;
    private Paint mPaint;
    private Path mPath;
    private Canvas canvas;

//    PdfDocument document;
//    PdfDocument.Page page;
//    private static int PAGE_WIDTH = 1080;
//    private static int PAGE_HEIGHT = 1920;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        ((MainActivity) getActivity()).setActionBarTitle("Złóż podpis");

        view = inflater.inflate(R.layout.fragment_sign_document, container, false);

        Button confirmButton = view.findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                RectF rectF = new RectF();
                mPath.computeBounds(rectF, true);
                Matrix rotateMatrix = new Matrix();
                rotateMatrix.setRotate(270f, rectF.centerX(),rectF.centerY());
                mPath.transform(rotateMatrix);
                PdfGenerator pdfGenerator = new PdfGenerator();
                pdfGenerator.createPdf();
                pdfGenerator.signInDocument(mPath);
                pdfGenerator.saveLocal("mypdf");

                mPath.reset();
                canvas.drawColor(Color.WHITE);
                dv.invalidate();
            }
        });

        Button clearButton = view.findViewById(R.id.clear_button);
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

        dv = new DrawingView(getContext());
        LinearLayout l = view.findViewById(R.id.sign_document_layout);
        l.addView(dv);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        return view;
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

            Log.d("psint", "consctuctor");

        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh)
        {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(mBitmap);
            Log.d("psint", "onSizeChanged");

        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
            canvas.drawPath(circlePath, circlePaint);
            Log.d("psint", "onDraw");
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y)
        {
//            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
            Log.d("psint", "touch_start");

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
            Log.d("psint", "touch_move");

        }

        private void touch_up()
        {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            canvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
//            mPath.reset();

            Log.d("psint", "touch_up");

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
            Log.d("psint", "onTouchEvent");

            return true;
        }

    }
}
