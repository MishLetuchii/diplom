package com.example.diplom.notes;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.diplom.MainActivity;
import com.example.navdrav.R;

import java.util.Calendar;
import java.util.Date;

import com.example.diplom.notes.Note;
import com.example.diplom.notes.NoteController;

public class NoteEditorActivity extends AppCompatActivity implements
        View.OnClickListener,
        PopupMenu.OnMenuItemClickListener,
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //инициализация визуальной составляющей
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.workspaceLayout);
        TextView noticeBox = (TextView) findViewById(R.id.noticeBox);
        Button fontMenuBtn = (Button) findViewById(R.id.notice_menu);
        Button buttonColor = (Button) findViewById(R.id.color_menu);
        Button buttonClose = (Button) findViewById(R.id.done_menu);
        //установка обработчика клика на кнопки
        fontMenuBtn.setOnClickListener(this);
        buttonColor.setOnClickListener(this);
        buttonClose.setOnClickListener(this);
        //получение заметки из намерения, запускающего данное окно
        Note note = (Note) getIntent().getSerializableExtra("id_note");
        //получение объектов-элементов интерфейса

        //если идентификатор передаваемой заметки был не -1,
        // то заполняем окно редактирования текста текстом заметки,
        // выставляем настройки в соответсвии с параметрами заметки
        if (note.getId() != -1) {
            EditText et = (EditText) findViewById(R.id.edititorArea);
            et.setText(note.getText());
            rl.setBackgroundResource(note.getNoteColor());
        }
        //если на заметке было установлено уведомление, выводим об этом информацию
        if (!note.getNotifyed()) {
            noticeBox.setText("");
        } else {
            writeNoticeTime();
        }
    }

    @SuppressLint("SetTextI18n")
    //вывод информации о времени напоминания заметки
    private void writeNoticeTime() {
        Note note = (Note) getIntent().getSerializableExtra("id_note");
        TextView noticeBox = (TextView) findViewById(R.id.noticeBox);
        noticeBox.setText("Напомнить " + DateUtils.formatDateTime(this,
                note.getNotifyTime().getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_SHOW_TIME));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_note_menu, menu);
        return true;
    }


    @Override
    public void onClick(View v) {
        //Создание всплывающего меню
        Note note = (Note) getIntent().getSerializableExtra("id_note");
        if (note != null) {

            EditText et = (EditText) findViewById(R.id.edititorArea);
            switch (v.getId()) {

                case (R.id.notice_menu): {
                    PopupMenu popup = new PopupMenu(this, findViewById(R.id.notice_menu));
                    //Inflating the Popup using xml file
                    popup.getMenuInflater()
                            .inflate(R.menu.notice_menu, popup.getMenu());

                    MenuItem m;
                    if (note.getNotifyed()) {
                        m = (MenuItem) popup.getMenu().findItem(R.id.set_notice_menuitem);
                    } else {
                        m = (MenuItem) popup.getMenu().findItem(R.id.delete_notice_menuitem);
                        m.setVisible(false);
                        m = (MenuItem) popup.getMenu().findItem(R.id.change_notice_menuitem);
                    }

                    m.setVisible(false);
                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(this);
                    popup.show(); //showing popup menu
                    break;
                }
                case (R.id.color_menu): {

                    PopupMenu popup = new PopupMenu(this, findViewById(R.id.color_menu));
                    //Inflating the Popup using xml file
                    popup.getMenuInflater()
                            .inflate(R.menu.color_menu, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(this);
                    popup.show(); //showing popup menu
                    break;
                }
                case (R.id.done_menu): {
                    NoteController nc = new NoteController(this);
                    //удаление заметки из списка, если такая уже существует
                    if (note.getId() != -1) {
                        nc.getNoteList().remove(note.getId());
                    }
                    //если текст заметки не пуст, то в нее устанавливаются значения полей текста
                    //и напоминания
                    if (!et.getText().toString().equals("")) {
                        note.setMakeDate(new Date());
                        note.setText(et.getText().toString());

                        Calendar calendar = Calendar.getInstance();
                        if ((note.getNotifyTime().getTimeInMillis() < calendar.getTimeInMillis() - 3000)) {
                            note.setNotifyed(false);
                        }
                        //заметка добавляется к остальным
                        nc.addNote(note);
                    }
                    //заметки сохраняются, запускается команда, ключающая окно управления заметками
                    nc.saveNotes();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                }

                default:
                    throw new IllegalStateException("Unexpected value: " + v);
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        Note note = (Note) getIntent().getSerializableExtra("id_note");
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.workspaceLayout);
        TextView noticeBox = (TextView) findViewById(R.id.noticeBox);

        assert note != null;
        switch (item.getItemId()) {
            case (R.id.white_note_menuitem): {
                note.setStyle(R.drawable.notebox_white_bckg);
                rl.setBackgroundResource(note.getNoteColor());
                break;
            }
            case (R.id.green_note_menuitem): {
                note.setStyle(R.drawable.notebox_green_bckg);
                rl.setBackgroundResource(note.getNoteColor());
                break;
            }
            case (R.id.purple_note_menuitem): {
                note.setStyle(R.drawable.notebox_purple_bckg);
                rl.setBackgroundResource(note.getNoteColor());
                break;
            }
            case (R.id.yellow_note_menuitem): {
                note.setStyle(R.drawable.notebox_yellow_bckg);
                rl.setBackgroundResource(note.getNoteColor());
                break;
            }
            case (R.id.blue_note_menuitem): {
                note.setStyle(R.drawable.notebox_blue_bckg);
                rl.setBackgroundResource(note.getNoteColor());
                break;
            }
            case (R.id.delete_notice_menuitem): {
                noticeBox.setText("");
                note.setNotifyed(false);
                break;
            }
            case (R.id.change_notice_menuitem): {
                setTime();
                setDate();
                note.setNotifyed(true);
                writeNoticeTime();
                break;
            }

            case (R.id.set_notice_menuitem): {
                note.setNotifyTime(Calendar.getInstance());
                setTime();
                setDate();
                note.setNotifyed(true);
                writeNoticeTime();
                break;
            }
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Note note = (Note) getIntent().getSerializableExtra("id_note");
        assert note != null;
        // Операции для выбранного пункта меню
        if (item.getItemId() == R.id.delete_note_menuitem) {
            NoteController nc = new NoteController(this);
            if (note.getId() != -1) {
                nc.getNoteList().remove(note.getId());
            }
            nc.saveNotes();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else return false;
    }

    // отображаем диалоговое окно для выбора даты
    public void setDate() {

        Note note = (Note) getIntent().getSerializableExtra("id_note");
        assert note != null;
        DatePickerDialog dpd = new DatePickerDialog(this, this,
                note.getNotifyTime().get(Calendar.YEAR),
                note.getNotifyTime().get(Calendar.MONTH),
                note.getNotifyTime().get(Calendar.DAY_OF_MONTH));
        dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dpd.show();
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime() {

        Note note = (Note) getIntent().getSerializableExtra("id_note");
        assert note != null;
        TimePickerDialog tpd = new TimePickerDialog(this, this,
                note.getNotifyTime().get(Calendar.HOUR_OF_DAY),
                note.getNotifyTime().get(Calendar.MINUTE), true);
        tpd.show();
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Note note = (Note) getIntent().getSerializableExtra("id_note");
        assert note != null;
        Calendar dateAndTime = note.getNotifyTime();
        //Calendar calendar = Calendar.getInstance();
        dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        dateAndTime.set(Calendar.MINUTE, minute);
        writeNoticeTime();
    }

    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        Note note = (Note) getIntent().getSerializableExtra("id_note");
        assert note != null;
        Calendar dateAndTime = note.getNotifyTime();

        dateAndTime.set(Calendar.YEAR, year);
        dateAndTime.set(Calendar.MONTH, monthOfYear);
        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        note.setNotifyTime(dateAndTime);
        writeNoticeTime();
    }
}