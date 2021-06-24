package com.example.diplom.feed;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navdrav.R;
import com.example.navdrav.databinding.FragmentFeedLineBinding;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class FeedFragment extends Fragment implements RecyclerViewAdapt.downloadButtonClickListener {

    private FragmentFeedLineBinding binding;

    List<FeedItem> feedItems = new ArrayList<>();
    private static final String LoGTAG = "NewsLog";
    private String rssResult = "";
    RecyclerView rv;

    private boolean threadOn = false;
    private Handler handler;
    private int newsCounter;
    private boolean item = false;
    private int i = -1;
    private int scrollPos = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        handler = new MyHandler(this);
        binding = FragmentFeedLineBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        rv = binding.rv;
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        rv.setLayoutManager(llm);
        initializeAdapter();
        Toast.makeText(getContext(),
                "Загружаю новости, пожалуйста, подождите.",
                     Toast.LENGTH_LONG).show();
        RssDownloadTrhead();
        Log.d(LoGTAG, "onCreateView");

        return root;
    }

    private void initializeAdapter() {
        RecyclerViewAdapt adapter = new RecyclerViewAdapt(feedItems);
        adapter.downloadButtonClickListener(this);
        rv.setAdapter(adapter);
        ((LinearLayoutManager) Objects
                .requireNonNull(rv.getLayoutManager()))
                .scrollToPosition(scrollPos);
    }

    @Override
    public void onCardClick(View view,final int pos) {

        scrollPos = ((LinearLayoutManager) Objects
                .requireNonNull(rv.getLayoutManager()))
                .findFirstCompletelyVisibleItemPosition();
        newsCounter = newsCounter + 12;
        RssDownloadTrhead();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(LoGTAG, "onDestroyView");
        binding = null;
    }


    void prepareNewsList() {
        Log.d(LoGTAG, "FeedListCaptured");

        for (int j = 0; j < this.feedItems.size(); j++) {
            FeedItem fi = this.feedItems.get(j);
            fi.findImageString();
        }
        item = false;
        this.rssResult = "";
        initializeAdapter();
    }

    private class RSSHandler extends DefaultHandler {

        public void startElement(String uri, String localName, String qName,
                                 Attributes attrs) throws SAXException {
            if (localName.equals("item")) {
                i++;
                item = true;
                FeedItem item = new FeedItem();
                feedItems.add(item);
            }
            if (localName.equals("title") && i > 0)
                rssResult = rssResult + "\n";
            if (!localName.equals("item") && item) {
                rssResult = rssResult + " localname: " + localName + "!! ";
                feedItems.get(i).nextAttribute(localName);
            }

        }

        public void characters(char[] ch, int start, int length)
                throws SAXException {
            String cdata = new String(ch, start, length);
            if (item)
                if (!(cdata.trim()).replaceAll("\\s+", " ").equals("")) {
                    String nextText = (cdata.trim()).replaceAll("\\s+", " ");
                    rssResult = rssResult + nextText + "\n";
                    feedItems.get(i).setNextAttribute(nextText);
                }
        }

        public void endElement(String namespaceURI, String localName,
                               String qName) throws SAXException {
        }
    }

    private void RssDownloadTrhead() {
        Thread thread = new Thread(new Runnable() {

            private String pages() {
                //pages() возвращает строку нужного вида,
                //необходимую для вставки в ссылку,
                //чтобы получить старые новости
                String s = "";
                if (newsCounter > 0) {
                    s = "start=" + newsCounter + "&";
                }
                return s;
            }

            @Override
            public void run() {
                Log.d("Debug", "Run");
                threadOn = true;
                try {//устанавливаем соединение, скачиваем файл RSS и читаем его при помощи xmlReader
                    URL rssUrl = new URL("https://vogu35.ru/news?"
                                                + pages()
                                                + "format=feed&type=rss");
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser saxParser = factory.newSAXParser();
                    XMLReader xmlReader = saxParser.getXMLReader();
                    RSSHandler rssHandler = new RSSHandler();
                    xmlReader.setContentHandler(rssHandler);
                    InputSource inputSource = new InputSource(rssUrl.openStream());
                    xmlReader.parse(inputSource);
                    Log.d("Debug", "Done");
                    //Handler служит для звязи между основным потоком и потоком, в котором
                    //выполняется скачивание новостей
                    //после загрузки новостей информируем об этом основной поток
                    Message msg = handler.obtainMessage(0, feedItems);
                    handler.sendMessage(msg);
                } catch (IOException
                        | SAXException
                        | ParserConfigurationException e) {
                    Log.d(LoGTAG, e.getMessage());
                }
                //вызываем метод загрузки картинок у каждой из новостей, если она еще не загружена
                for (int cntr= newsCounter;cntr <feedItems.size();cntr++) {
                    if (feedItems.get(cntr).getImage() == null)
                        feedItems.get(cntr).loadImage();
                }
                threadOn = false;
            }
        });
        thread.start();
        Log.d("Debug", "Thread done");
    }

    static class MyHandler extends Handler {

        //ссылка на основной класс
        WeakReference<FeedFragment> wrActivity;
        //конструктор
        public MyHandler(FeedFragment activity) {
            wrActivity = new WeakReference<FeedFragment>(activity);
        }
        //метод информирования
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FeedFragment activity = wrActivity.get();
            if (activity != null)
                activity.prepareNewsList();
        }
    }
}
