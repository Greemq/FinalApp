package app.gree.finalapp.Model;

import java.util.Date;

/**
 * Created by VH on 07.05.2017.
 */

public class RssItem {
    private long id;
    private String title;
    private String link;
    private Date pubDate;
    private int favourite;
    private Channel channel;

    public RssItem() {

    }

    public RssItem(long id, String title, String link, Date pubDate, int favourite, Channel channel) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.pubDate=pubDate;
        this.favourite = favourite;
        this.channel = channel;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public int getFavourite() {
        return favourite;
    }

    public void setFavourite(int favourite) {
        this.favourite = favourite;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
