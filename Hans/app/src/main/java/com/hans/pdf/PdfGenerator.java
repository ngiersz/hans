package com.hans.pdf;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.grpc.internal.IoUtils;

public class PdfGenerator
{
    private static int PAGE_WIDTH = 842;
    private static int PAGE_HEIGHT = 595;
    private Canvas canvas;

    PdfDocument document;
    PdfDocument.Page page;
    PdfDocument.PageInfo pageInfo;

    public PdfGenerator()
    {
        document = new PdfDocument();
        pageInfo = new PdfDocument.PageInfo.Builder
                (PAGE_WIDTH, PAGE_HEIGHT, 1).create();

        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
    }

    public void signInDocument(Path mPath)
    {
        Canvas canvas = page.getCanvas();
        Matrix scaleMatrix = new Matrix();
        RectF rectF = new RectF();
        mPath.computeBounds(rectF, true);
        scaleMatrix.setScale(0.1f, 0.1f, rectF.centerX(), rectF.centerY());
        mPath.transform(scaleMatrix);

        Matrix translateMatrix = new Matrix();
        translateMatrix.setTranslate(130f, -300f);
        mPath.transform(translateMatrix);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(1.5f);
        canvas.drawPath(mPath, paint);
    }

    public void createPdf()
    {
        String date = "14.07.2019 15:33";
        String deliverer = "Marcin Hradowicz";
        String client = "Jan Kowalski";
        String name1 = "Przedmiot1";
        String description1 = "opis1";
        String amount1 = "ilość1";
        String weight1 = "waga1";
        String measurmentsW1 = "W1";
        String measurmentsH1 = "H1";
        String measurmentsD1 = "D1";

        String item1 = "Numer: 1.      Nazwa: " + name1 + "      Opis: " + description1 + "      Ilość: "
                + amount1 + "      Waga [kg]: " + weight1 + "      Wymiary [*] " + measurmentsW1 + "x" + measurmentsH1 + "x" + measurmentsD1 + "x";


        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFakeBoldText(true);
        paint.setTextSize(16);
        canvas.drawText("PROTOKÓŁ PRZEKAZANIA TOWARU", PAGE_WIDTH / 2, 80, paint);

        paint.setTextSize(14);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Data wystawienia:", 300, 110, paint);
        canvas.drawText("Wykonawca usługi transportowej:", 300, 130, paint);
        canvas.drawText("Zleceniodawca usługi transportowej:", 300, 150, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setFakeBoldText(false);
        canvas.drawText(date, 310, 110, paint);
        canvas.drawText(deliverer, 310, 130, paint);
        canvas.drawText(client, 310, 150, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(11);
        canvas.drawText("Tabela 1. Przyjęte towary", PAGE_WIDTH / 2, 190, paint);

        paint.setTextSize(12);
        canvas.drawText(item1, PAGE_WIDTH / 2, 220, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("* - szerokość x długość x głębokość", 100, 250, paint);

        paint.setTextSize(14);
        String affirmationLine1 = "Ja, niżej podpisany .............................................................................. oświadczam, ze w dniu " + date.substring(0, 10);
        String affirmationLine2 = " odebrałem od " + deliverer + " wszystkie towary zamieszczone w Tabeli 1. w zadowalającym mnie stanie.";
        canvas.drawText(affirmationLine1, 80, 300, paint);
        canvas.drawText(affirmationLine2, 50, 320, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawLine(PAGE_WIDTH - 50 - 200, 450, PAGE_WIDTH - 50, 450, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(11);
        canvas.drawText("podpis odbierającego", PAGE_WIDTH - 50 - 100, 470, paint);

        // border
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        canvas.drawRect(50, 200, PAGE_WIDTH - 50, 230, paint);


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

    public void saveLocal(String filename)
    {
        document.finishPage(page);

        Log.d("pdf", "PDF was created");

        File pdfFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "//" + filename + ".pdf");
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
        try
        {
            downloadFileFromFirebaseStorage(filename);
        } catch (Exception e)
        {

        }
    }

    public void saveInFirebaseStorage(final String filename)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        Uri file = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "//" + filename + ".pdf"));
        StorageReference catalogRef = storageRef.child(user.getUid() + "/" + filename + ".pdf");
        catalogRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        // Get a URL to the uploaded content
//                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.d("storage", "yay");
                        try
                        {
                            downloadFileFromFirebaseStorage(filename);
                        } catch (Exception e)
                        {

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception exception)
                    {
                        // Handle unsuccessful uploads
                        // ...
                        Log.d("storage", "nieyay");

                    }
                });

    }

    public void downloadFileFromFirebaseStorage(String filename) throws IOException
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        File TEMP_FOLDER = new File(Environment.getExternalStorageDirectory().toString(), "HansTemp");
        if (!TEMP_FOLDER.exists())
            TEMP_FOLDER.mkdirs();

        StorageReference catalogRef = storageRef.child(user.getUid() + "/" + filename + ".pdf");

        File localFile = File.createTempFile(filename, ".pdf", TEMP_FOLDER);
        catalogRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
                    {
                        // Successfully downloaded data to local file
                        // ...
                        Log.d("storage", "yay2");
                    }
                }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                // Handle failed download
                // ...
                Log.d("storage", "nieyay2");

            }
        });
    }
}
