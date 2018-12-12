package app.gree.finalapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import app.gree.finalapp.DB.RssItemDAO;
import app.gree.finalapp.FullNews;
import app.gree.finalapp.IconLoader;
import app.gree.finalapp.MainActivity;
import app.gree.finalapp.Model.RssItem;
import app.gree.finalapp.R;

/**
 *
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<RssItem> rssItems;
    private Context context;

    public RecyclerViewAdapter(List<RssItem> rssItems) {
        this.rssItems = rssItems;
    }

    public void setItems(List<RssItem> rssItems) {
        this.rssItems = rssItems;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recylcer_view_item, parent, false);
        RecyclerView.ViewHolder viewHolder = new ViewHolder(item);
        context = parent.getContext();
        return (ViewHolder) viewHolder;
    }

    private String dateFormat(RssItem rssItem){
        SimpleDateFormat format=new SimpleDateFormat("dd.MM HH:mm");
        String newDate="";
        newDate=format.format(rssItem.getPubDate());
        return newDate;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, final int position) {
        holder.title.setText(rssItems.get(position).getTitle());
        holder.pubDate.setText(dateFormat(rssItems.get(position)));
        holder.icon.setImageBitmap(new IconLoader().getBitmap(context,String.valueOf(rssItems.get(position).getChannel().getId())));
        if (rssItems.get(position).getFavourite() == 0) {
            holder.favourite.setImageResource(R.drawable.ic_star_border_black_24dp);
        } else
            holder.favourite.setImageResource(R.drawable.ic_star_black_24dp);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RssItemDAO rssItemDAO = new RssItemDAO(context);
                rssItemDAO.open();
                RssItem tmp = rssItems.get(position);
                rssItemDAO.close();
                Intent intent=new Intent(context,FullNews.class);
                intent.putExtra("url_link",tmp.getLink());
                v.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return rssItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView pubDate, title;
        public ImageView favourite, icon;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            pubDate = (TextView) itemView.findViewById(R.id.pub_date);
            favourite = (ImageView) itemView.findViewById(R.id.favourite);
            icon = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
