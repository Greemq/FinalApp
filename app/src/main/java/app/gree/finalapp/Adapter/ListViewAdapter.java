package app.gree.finalapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.gree.finalapp.IconLoader;
import app.gree.finalapp.Model.Channel;
import app.gree.finalapp.R;

/**
 * Created by VH on 08.05.2017.
 */

public class ListViewAdapter extends ArrayAdapter<Channel>{
    private int resource;
    private Context context;
    private List<Channel> channelList;

    public void setItems(List<Channel> channelList){
        this.channelList=channelList;
    }

    public ListViewAdapter(Context context, int resource, List<Channel> channelList) {
        super(context, resource, channelList);
        this.resource = resource;
        this.context = context;
        this.channelList = channelList;
    }

    static class ViewHolder{
        TextView channelName;
        TextView channelLastUpdate;
        ImageView imageView;
        int position;
    }

    private String dateFormat(Channel ch){
        SimpleDateFormat format=new SimpleDateFormat("dd.MM HH:mm");
        String newDate="";
        if (ch.getLastUpdate()==null){
            newDate="not updated yet";
        }
        else
            newDate="updated: "+format.format(ch.getLastUpdate());
        return newDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView==null){
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(resource,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.position=position;
            viewHolder.channelName=(TextView) convertView.findViewById(R.id.channel_name);
            viewHolder.channelLastUpdate=(TextView) convertView.findViewById(R.id.last_update);
            viewHolder.imageView=(ImageView) convertView.findViewById(R.id.channel_icon);
            convertView.setTag(viewHolder);
        }
        else
            viewHolder=(ViewHolder) convertView.getTag();

        Channel channel=channelList.get(position);
        viewHolder.channelName.setText(channel.getName());
        viewHolder.channelLastUpdate.setText(dateFormat(channel));
        if (channel.getIcon()==null){
            viewHolder.imageView.setImageResource(R.drawable.ic_brightness_1_black_24dp);
        }
        else
            viewHolder.imageView.setImageBitmap(channel.getIcon());

        return convertView;
    }

}
