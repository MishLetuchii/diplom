package com.example.diplom.notification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.diplom.notes.NoteEditorActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.example.diplom.notes.Note;


public class NotificService extends Service {
    private final List<Note> noteList = new ArrayList<>();
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    private final String LOG_TAG = "NotificationServiceLog";
    private final int REQ_CODE = 10;

    public NotificService() {}

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "Create Service");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //если намерение запуска пустое, то выполняется загрузка списка заметок
        Log.d(LOG_TAG, "Start Service");
        if (intent == null) {
            this.loadNotes();
            querideNotes();
        } else {
            // в ином случае, если параметр Starter в намерении равен 0
            if (intent.getIntExtra("Starter", 0) == 0) {
                //загружаем список заметок и устанавливаем напоминание на самую ближайшую из них
                this.loadNotes();
            } else if (intent.getIntExtra("Starter", 0) == 1) {
                //если параметр Starter в намерении равен 1, отправляем уведомление с текстом заметки
                Note n = (Note) intent.getSerializableExtra("Note");
                String s;
                if (n == null) {
                    s = "AlarmHandle";
                } else s = n.getText();
                Log.d(LOG_TAG, s);
                sendAlarmMessage(n);
            } else {
                //если параметр Starter в намерении равен другому числу, отменяем напоминание
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent myIntent = new Intent(getApplicationContext(), NotificService.class);
                PendingIntent pi = PendingIntent.getService(this, REQ_CODE,
                                                myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pi);
                //загружаем заметки с целью обновить список требующих напоминания заметок
                this.loadNotes();
            }
        }
        querideNotes();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void loadNotes() {
        //десериализация заметок

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent myIntent = new Intent(getApplicationContext(), NotificService.class);
        PendingIntent pi = PendingIntent.getService(this,
                REQ_CODE,
                myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pi);

        noteList.clear();
        Log.d(LOG_TAG, "LoadNotes");
        String savePath = "notes.txt";
        String pn = this.getFilesDir() + "/" + savePath;
        File file = new File(pn);
        if (file.exists()) {
            try {
                FileInputStream fileInput = this.openFileInput(savePath);
                ObjectInputStream objInput = new ObjectInputStream(fileInput);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInput));
                int i = 0;
                while (bufferedReader.ready()) {
                    Note n = (Note) objInput.readObject();
                    if (Calendar.getInstance().getTimeInMillis() >= n.getNotifyTime().getTimeInMillis()) {
                        n.setNotifyed(false);
                    }
                    if (n.getNotifyed()) {
                        noteList.add(n);
                        noteList.get(noteList.size() - 1).setId(i);
                    }
                    i++;
                }

                Log.d(LOG_TAG, "LoadNotes Completed");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void querideNotes() {
        //если список не пуст
        if (noteList.size() > 0) {
            Collections.sort(noteList);
            //выбираем первый элемент из списка
            Note n = noteList.get(0);
            Log.d(LOG_TAG, "Next alarm at " + n.getText());

            if (n.getNotifyed()) {
                //создаём намерение для запуска этого же сервиса
                Intent myIntent = new Intent(getApplicationContext(), NotificService.class);
                //устанавливаем параметр Starter = 1 для того чтобы при запуске сервис отправил уведомление
                myIntent.putExtra("Starter", 1);
                //устанавливаем параметры уведомлений
                myIntent.putExtra("Note", n);
                myIntent.putExtra("Text", "Alarm at " + n.getText());
                //устанавливаем Alarm в AlarmManager
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                PendingIntent pi = PendingIntent.getService
                        (this, REQ_CODE, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set
                        (AlarmManager.RTC_WAKEUP, noteList.get(0).getNotifyTime().getTimeInMillis(), pi);
                noteList.remove(0);
            }
        } else {
            //если список пуст, уничтожаем сервис
            Log.d(LOG_TAG, "Destroy Service");
            this.onDestroy();
        }
    }

    private void sendAlarmMessage(Note n) {

        Intent notificationIntent = new Intent(getApplicationContext(), NoteEditorActivity.class);
        notificationIntent.putExtra("id_note", n);
        notificationIntent.putExtra("fromNotification", true);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), default_notification_channel_id);
        mBuilder.setContentTitle("NearVogu");
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setContentText("Напоминание");
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(n.getText()));
        mBuilder.setSmallIcon(android.R.drawable.ic_popup_reminder);
        mBuilder.setAutoCancel(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

}