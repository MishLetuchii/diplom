package com.example.diplom.feed;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeedItem {

    private int attributeIndexer;

    private String title;
    private String link;
    private String guid;
    private String description;
    private String author;
    private String category;
    private String pubDate;
    private String imageString;
    private Bitmap image;
    private boolean find=false;



    public FeedItem(){
        this.image=null;
        this.imageString="";
        this.title="";
        this.link="";
        this.guid="";
        this.description="";
        this.author="";
        this.category="";
        this.pubDate="";
        this.attributeIndexer =0;

    }

    public void nextAttribute(String attr)
    {
        switch (attr)
        {
            case  "title":attributeIndexer =0;break;
            case  "link":attributeIndexer =1;break;
            case  "guid":attributeIndexer =2;break;
            case  "description":attributeIndexer =3;break;
            case  "author":attributeIndexer =4;break;
            case  "category":attributeIndexer =5;break;
            case  "pubDate":attributeIndexer =6;break;
            default:attributeIndexer = -1;
        }

    }


    public void setNextAttribute(String attr)
    {
        switch (attributeIndexer)
        {
            case  0:this.setTitle(attr);break;
            case  1:this.setLink(attr);break;
            case  2:this.setGuid(attr);break;
            case  3:this.setDescription(attr);break;
            case  4:this.setAuthor(attr);break;
            case  5:this.setCategory(attr);break;
            case  6:this.setPubDate(attr);break;
            default:break;
        }
    }

   public void findImageString()
    {
        if (!find) {
            String start = "<img src=\"";
            String end = "\".?/>";
            Pattern pattern = Pattern.compile(start + ".*" + end);
            Matcher matcher = pattern.matcher(this.description);
            if (matcher.find()) {
                this.imageString =
                        this.description.substring(matcher.start() + start.length(),
                                matcher.end() - 3);
                if (imageString.endsWith("\""))
                    this.imageString = this.imageString.substring(0,
                            this.imageString.length() - 1);
            }

            clearDesc();
            find=true;
        }
    }


    public void loadImage()
    {
        Bitmap bm = null;
        try {
            URL aURL = new URL(imageString);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("TAG", "Error getting bitmap "+imageString, e);
        }
        this.image = bm;
    }

    private void clearDesc()
    {
        String s="";
        Pattern pattern = Pattern.compile(">"+".*?"+"<");
        Matcher matcher = pattern.matcher(this.description);
        while (matcher.find()) {
            s = s+ this.description.substring(matcher.start()+1, matcher.end()-1);

        }
        s = s.replace("&nbsp;"," ");
        this.description=s;

    }
    public Bitmap getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = this.title+ title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = this.link + link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description =this.description + description;
    }

    public String getGuid() {
        return guid;
    }

    public String getImageString(){return imageString;}

    public void setGuid(String guid) {
        this.guid = this.guid+ guid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = this.author+ author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = this.category+ category;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate =this.pubDate+ pubDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedItem feedItem = (FeedItem) o;
        return getTitle().equals(feedItem.getTitle()) &&
                getLink().equals(feedItem.getLink()) &&
                getGuid().equals(feedItem.getGuid()) &&
                getDescription().equals(feedItem.getDescription()) &&
                getAuthor().equals(feedItem.getAuthor()) &&
                getCategory().equals(feedItem.getCategory()) &&
                getPubDate().equals(feedItem.getPubDate()) &&
                getImageString().equals(feedItem.getImageString());
    }
}
