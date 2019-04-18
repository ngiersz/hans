package com.hans.pdf;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfGenerator {

    public String createPdf(String text) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder
                (300, 600, 1).create();

        // page 1
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
//        paint.setColor(Color.RED);
//        canvas.drawCircle(50, 50, 30, paint);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, 10, 10, paint);
        document.finishPage(page);

        // page 2
        pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 2).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        paint.setColor(Color.BLUE);
        canvas.drawCircle(100, 100, 100, paint);
        document.finishPage(page);

        Log.d("pdf", "PDF was created");

        File pdfFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "//mypdf.pdf");
//        Uri path = Uri.fromFile(pdfFile);
        try {
            document.writeTo(new FileOutputStream(pdfFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("pdf", "PDF was created with path " + pdfFile.getAbsolutePath());
        // close the document
        document.close();

        return pdfFile.getAbsolutePath();


//        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/mypdf/";
//        File file = new File(directory_path);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        String targetPdf = directory_path+"test-2.pdf";
//        File filePath = new File(targetPdf);
//        try {
//            document.writeTo(new FileOutputStream(filePath));
//            Log.d("pdf", "PDF was written to " + targetPdf);
//        } catch (IOException e) {
//            Log.d("pdf", "Cannot write PDF to " + targetPdf);
//
//        }
    }
}
