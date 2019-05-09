package com.hans.pdf;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hans.BuildConfig;
import com.hans.DatabaseFirebase;
import com.hans.R;
import com.hans.domain.Order;
import com.hans.domain.OrderStatus;
import com.hans.domain.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class PdfGenerator
{
    private Context context;
    private String CHANNEL_ID_DOWNLOAD = "0";
    private int NOTIFICATION_ID_DOWNLOAD = 0;

    private static int PAGE_WIDTH = 842;
    private static int PAGE_HEIGHT = 595;
    private Canvas canvas;

    private PdfDocument document;
    private PdfDocument.Page page;
    private PdfDocument.PageInfo pageInfo;
    private Order order;

    public PdfGenerator(Context context, Order order)
    {
        this.order = order;
        this.context = context;
        document = new PdfDocument();
        pageInfo = new PdfDocument.PageInfo.Builder
                (PAGE_WIDTH, PAGE_HEIGHT, 1).create();

        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
    }

    public void signInDocument(Path mPath)
    {
        Canvas canvas = page.getCanvas();

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

    public void createPdf(User clientOb, User delivererOb, String receiver_firstname, String receiver_lastname)
    {
        DatabaseFirebase db = new DatabaseFirebase();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm ", new Locale("pl", "PL"));
        String date = sdf.format(new Date());
        String deliverer = delivererOb.getName() + " " + delivererOb.getSurname();
        String client = clientOb.getName() + " " + delivererOb.getSurname();
        String description1 = order.getDescription();
        String weight1 = order.getWeight().toString();
        String measurmentsW1 = order.getDimensions().get("width").toString();
        String measurmentsH1 = order.getDimensions().get("height").toString();
        String measurmentsD1 = order.getDimensions().get("depth").toString();
        String reciverName = receiver_firstname + " " + receiver_lastname;

//
//        String date = "14.07.2019 15:33";
//        String deliverer = "Marcin Hradowicz";
//        String client = "Jan Kowalski";
//        String description1 = "opis1";
//        String weight1 = "waga1";
//        String measurmentsW1 = "W1";
//        String measurmentsH1 = "H1";
//        String measurmentsD1 = "D1";
//        String reciverName = "RECEIVER_NAME";

        String item1 = "Lp. 1." + "      Opis: " + description1 + "      Waga [kg]: " + weight1 + "      Wymiary [*] " + measurmentsW1 + " x " + measurmentsH1 + " x " + measurmentsD1;


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
        canvas.drawText("* - szerokość[cm] x długość[cm] x głębokość[cm]", 100, 250, paint);

        paint.setTextSize(14);
        String affirmationLine1 = "Ja, niżej podpisany " + reciverName + " oświadczam, że w dniu " + date.substring(0, 11);
        String affirmationLine2 = " odebrałem od " + deliverer;
        String affirmationLine3 = " wszystkie towary zamieszczone w Tabeli 1. w zadowalającym mnie stanie.";
        canvas.drawText(affirmationLine1, 80, 300, paint);
        canvas.drawText(affirmationLine2, 50, 320, paint);
        canvas.drawText(affirmationLine3, 50, 340, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawLine(PAGE_WIDTH - 50 - 200, 550, PAGE_WIDTH - 50, 550, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(11);
        canvas.drawText("podpis odbierającego", PAGE_WIDTH - 50 - 100, 570, paint);

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
        // saveLocal in local storage
        document.finishPage(page);
        Log.d("pdf", "PDF was created");

        File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "//" + filename + ".pdf");
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
    }

    public void sendToFirebaseStorage(String filename)
    {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        final File localFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "//" + filename + ".pdf");
        Uri file = Uri.fromFile(localFile);

        StorageReference catalogRef = storageRef.child(user.getUid() + "/" + filename + ".pdf");
        catalogRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        localFile.delete();

//                      Snackbar.make(getView(), "Zakończono zlecenie", Snackbar.LENGTH_SHORT).show();
                        // finish order
                        DatabaseFirebase db = new DatabaseFirebase();
                        order.setOrderStatus(OrderStatus.CLOSED);
                        db.setOrder(order);

//                sendNotificationToClient();

                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception exception)
                    {
                        // Handle unsuccessful uploads
                        // ...

                    }
                });

    }

    public void downloadFileFromFirebaseStorage(String filename)
    {
        createNotificationChannel();

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_DOWNLOAD);
        builder.setContentTitle("Protokół przekazania towaru")
                .setContentText("Pobieranie w trakcie")
                .setSmallIcon(R.drawable.ic_file_download_white)
                .setPriority(NotificationCompat.PRIORITY_LOW);

// Issue the initial notification with zero progress
        int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;
        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, true);
        notificationManager.notify(NOTIFICATION_ID_DOWNLOAD, builder.build());


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference catalogRef = storageRef.child(user.getUid() + "/" + filename + ".pdf");

        final File localFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + filename + ".pdf");
        catalogRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
                    {
                        // Successfully downloaded data to local file
                        // ...
                        Intent target = new Intent(Intent.ACTION_VIEW);
                        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", localFile);

                        target.setDataAndType(uri, "application/pdf");
                        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        Intent intent = Intent.createChooser(target, "Open File");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        // Create the TaskStackBuilder and add the intent, which inflates the back stack
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addNextIntentWithParentStack(intent);
                        // Get the PendingIntent containing the entire back stack
                        PendingIntent resultPendingIntent =
                                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentText("Ukończono pobieranie")
                                .setProgress(0, 0, false)
                                .setContentIntent(resultPendingIntent)
                                .setAutoCancel(true);
                        notificationManager.notify(NOTIFICATION_ID_DOWNLOAD, builder.build());

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

    private void createNotificationChannel()
    {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "Pobieranie plików";
            String description = "Powiadomienie o pobraniu protokołu przyjecia towaru";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_DOWNLOAD, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
