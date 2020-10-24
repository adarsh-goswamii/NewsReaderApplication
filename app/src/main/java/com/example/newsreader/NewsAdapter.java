package com.example.newsreader;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder>
{
    private Context context;
    private ArrayList<NewsItem> list= new ArrayList<>();

    public NewsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, final int position) {
        holder.title.setText(list.get(position).getTitle());
        holder.description.setText(list.get(position).getDescription());
        holder.date.setText(list.get(position).getDate());

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(context, WebView.class);
                intent.putExtra("url", list.get(position).getLink());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<NewsItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder
    {
        private TextView title, description, date;
        private CardView card;
        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            card= itemView.findViewById(R.id.card);
            description= itemView.findViewById(R.id.description);
            title= itemView.findViewById(R.id.title);
            date= itemView.findViewById(R.id.date);
        }
    }
}
