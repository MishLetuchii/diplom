package com.example.diplom.notes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.navdrav.R;
import com.example.navdrav.databinding.FragmentHomeBinding;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.example.diplom.notification.NotificService;

public class UserNotesFragment extends Fragment implements View.OnClickListener {

    private FragmentHomeBinding binding;
    private NoteController nc;
    private static final String CHANNEL_ID = "NearVoguNotifyer";
    private int width;

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.delete_note_menu, menu);
        menu.findItem(R.id.delete_note_menuitem).setTitle(R.string.clear_title);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //Создаем контроллер заметок
        nc = new NoteController(this.getContext());

        binding.FAB.setOnClickListener(this);

        //после открытия меню запускаем сервис проверки заметок на ближайшие уведомления
        Intent i = new Intent(this.getContext(), NotificService.class);
        i.putExtra("Starter", 0);
        getActivity().startService(i);

        //отрисовываем списки заметок
        drawNotes();
        return root;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_note_menuitem: {
                nc.clearNotes();
                drawNotes();
                Intent i = new Intent(this.getContext(), NotificService.class);
                i.putExtra("Starter", 2);
                requireActivity().startService(i);
                return true;
            }
            default:
                return false;
        }
    }

    @SuppressLint("SetTextI18n")
    private void drawNotes() {
        int colCount1 = 0, colCount2 = 0;//счетчик величин левой и правой колонки меню заметок
       //получение контейнеров, отображающик левую и правую колонку заметок
        LinearLayout verticalLayout1 = binding.firstVertical;
        LinearLayout verticalLayout2 = binding.secondVertical;
        verticalLayout1.removeAllViews();
        verticalLayout2.removeAllViews();
        //Установка значений параметров отступов для карточек заметок
        LinearLayout.LayoutParams VParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        VParams.setMargins(10, 10, 10, 10);

        Note n;
        //обход списка заметок
        int i = nc.getNoteList().size() - 1;
        while (i > -1) {
            n = nc.getNoteList().get(i);

            //создание контейнера для карточки-заметки
            LinearLayout boxLayout = new LinearLayout(this.getContext());
            boxLayout.setOrientation(LinearLayout.VERTICAL);
            boxLayout.setLayoutParams(VParams);

            //установка идентификатора заметки
            //Используется при передаче заметки в меню работы с заметкой
            n.setId(i);
            //установка текста и параметров отображения
            TextView tv = new TextView(this.getContext());
            tv.setText(n.getText());
            //tv.setLayoutParams(VParams);
            tv.setMaxLines(12);//максимум отображения - 12 строк
            tv.setEllipsize(TextUtils.TruncateAt.END);//если текст имеет большую длину,
            // завершить отображение при помощи"..."

            //добавление текстового поля к карточке заметки
            boxLayout.addView(tv);

            //настройка отображения даты напоминания или даты создания у карточки-заметки
            TextView lbl = new TextView(this.getContext());
            if (n.getNotifyed()) {
                lbl.setText("Напомнить: " + DateUtils.formatDateTime(this.getContext(),
                        n.getNotifyTime().getTimeInMillis(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME));
            } else {
                DateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy",
                                                                    Locale.getDefault());
                lbl.setText(dateFormat.format(n.getMakeDate()));
            }
            boxLayout.addView(lbl);

            //установка стиля отображения в зависимости от цвета заметки
            boxLayout.setBackgroundResource(n.getStyle());
            //установка обработчика клика по карточке
            Note finalN = n;
            boxLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), NoteEditorActivity.class);
                    intent.putExtra("id_note", finalN);
                    startActivity(intent);
                }
            });
            //просчитывание величины полученной карточки
            int wms = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int hms = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            tv.measure(wms, hms);

            //добавление карточки в колонку с наименьшей длиной
            if (colCount1 > colCount2) {
                verticalLayout2.addView(boxLayout);
                colCount2 += tv.getMeasuredHeight();
            } else {
                verticalLayout1.addView(boxLayout);
                colCount1 += tv.getMeasuredHeight();
            }
            i--;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.FAB: {
                Note note = new Note();
                note.setId(-1);
                Intent intent = new Intent(this.getContext(), NoteEditorActivity.class);
                intent.putExtra("id_note", note);
                startActivity(intent);
                requireActivity().finish();
                break;
            }
            default:
                break;
        }
    }
}