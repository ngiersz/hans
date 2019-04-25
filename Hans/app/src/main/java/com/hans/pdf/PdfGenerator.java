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

public class PdfGenerator
{
    private static int PAGE_WIDTH = 842;
    private static int PAGE_HEIGHT = 595;

    public String createPdf(Canvas c)
    {
        String date = "14.07.2019 15:33";
        String deliverer = "Marcin Hradowicz";
        String client = "Jan Kowalski";
        String name1 = "Przedmiot1";
        String description1 = "opis1";
        String amount1 =  "ilość1";
        String weight1 = "waga1";
        String measurmentsW1 = "W1";
        String measurmentsH1 = "H1";
        String measurmentsD1 = "D1";

        String item1 = "Numer: 1.      Nazwa: " + name1 + "      Opis: " + description1 + "      Ilość: "
                + amount1 + "      Waga [kg]: " + weight1 + "      Wymiary [*] " + measurmentsW1 + "x" + measurmentsH1 + "x" + measurmentsD1 + "x";

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder
                (PAGE_WIDTH, PAGE_HEIGHT, 1).create();

        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFakeBoldText(true);
        paint.setTextSize(16);
        canvas.drawText("PROTOKÓŁ PRZEKAZANIA TOWARU",PAGE_WIDTH/2, 80, paint);

        paint.setTextSize(14);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Data wystawienia:",300, 110, paint);
        canvas.drawText("Wykonawca usługi transportowej:",300, 130, paint);
        canvas.drawText("Zleceniodawca usługi transportowej:",300, 150, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setFakeBoldText(false);
        canvas.drawText(date,310, 110, paint);
        canvas.drawText(deliverer,310, 130, paint);
        canvas.drawText(client,310, 150, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(11);
        canvas.drawText("Tabela 1. Przyjęte towary", PAGE_WIDTH/2, 190, paint);

        paint.setTextSize(12);
        canvas.drawText(item1, PAGE_WIDTH/2, 220, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("* - szerokość x długość x głębokość", 100, 250, paint);

        paint.setTextSize(14);
        String affirmationLine1 = "Ja, niżej podpisany .............................................................................. oświadczam, ze w dniu " + date.substring(0, 10);
        String affirmationLine2 = " odebrałem od " + deliverer + " wszystkie towary zamieszczone w Tabeli 1. w zadowalającym mnie stanie.";
        canvas.drawText(affirmationLine1, 80, 300, paint);
        canvas.drawText(affirmationLine2, 50, 320, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawLine(PAGE_WIDTH-50-200, 350, PAGE_WIDTH-50, 350, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(11);
        canvas.drawText("podpis odbierającego", PAGE_WIDTH-50-100, 370, paint);

        // border
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        canvas.drawRect(50, 200, PAGE_WIDTH-50, 230, paint);



        document.finishPage(page);

        Log.d("pdf", "PDF was created");

        File pdfFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "//mypdf.pdf");
//        Uri path = Uri.fromFile(pdfFile);
        try
        {
            document.writeTo(new FileOutputStream(pdfFile));
        } catch (IOException e)
        {
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
