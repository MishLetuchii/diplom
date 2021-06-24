package com.example.diplom.feed;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navdrav.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RecyclerViewAdapt extends RecyclerView.Adapter<RecyclerViewAdapt.NewsCard> {

    interface downloadButtonClickListener {
        void onCardClick(View view, int position);
    }

    private static downloadButtonClickListener btnListener;

    public void downloadButtonClickListener(downloadButtonClickListener listener) {
        btnListener = listener;
    }

    List<FeedItem> feedItemList;

    RecyclerViewAdapt(List<FeedItem> news) {
        this.feedItemList = news;
    }


    @Override
    public int getItemCount() {
        return feedItemList.size() + 1;
    }

    @NonNull
    @NotNull
    @Override
    public NewsCard onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {


        View v;
        if (viewType == R.layout.newcard) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.newcard, parent, false);
            return new NewsCard(v, 0);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.button, parent, false);
            return new NewsCard(v, 1);
        }

    }


    @Override
    public int getItemViewType(int position) {
        return (position == feedItemList.size()) ? R.layout.button : R.layout.newcard;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NewsCard holder, int position) {

        if (!(position == feedItemList.size())) {
            holder.author.setText(feedItemList.get(position).getAuthor());
            holder.pubDate.setText(formatDate(feedItemList.get(position).getPubDate()));
            holder.title.setText(feedItemList.get(position).getTitle());
            holder.description.setText(feedItemList.get(position).getDescription());
            holder.image.setImageBitmap(feedItemList.get(position).getImage());
            holder.readMoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), NewsActivity.class);
                    intent.putExtra("news_link", feedItemList.get(position).getLink());
                    view.getContext().startActivity(intent);
                }
            });
        } else {
            holder.downloadMoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnListener.onCardClick(view, position);
                }
            });
        }
    }

    private String formatDate(String s) {
        s = s.substring(0, s.indexOf("+") - 1);
        String hour = s.substring(s.indexOf(":") - 2, s.indexOf(":"));
        int h = Integer.parseInt(hour);
        h = h + 3;
        if (h >= 24)
            h = h - 24;
        String res = "";
        String begin = s.substring(0, s.indexOf(":") - 2);
        String end = s.substring(s.indexOf(":"), s.length());
        if (h < 10) res = begin + "0" + h + end;
        else res = begin + h + end;
        return res;
    }
    public static class NewsCard extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView description;
        TextView author;
        TextView pubDate;
        ImageView image;
        Button readMoreBtn;
        Button downloadMoreBtn;


        NewsCard(View itemView, int i) {
            super(itemView);
            if (i == 0) {
                cv = (CardView) itemView.findViewById(R.id.cv);
                title = (TextView) itemView.findViewById(R.id.title_tb);
                description = (TextView) itemView.findViewById(R.id.description_tb);
                author = (TextView) itemView.findViewById(R.id.author_tb);
                pubDate = (TextView) itemView.findViewById(R.id.pub_date_tb);
                image = (ImageView) itemView.findViewById(R.id.desc_image);
                readMoreBtn = (Button) itemView.findViewById(R.id.read_more);
            } else {
                downloadMoreBtn = (Button) itemView.findViewById(R.id.downlaod_more_button);
            }
        }
    }
}