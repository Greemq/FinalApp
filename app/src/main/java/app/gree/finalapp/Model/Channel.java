package app.gree.finalapp.Model;

import android.graphics.Bitmap;

import java.util.Date;

/**
 *
 */

public class Channel {
    private long id;
    private String link;
    private String name;
    private Date lastUpdate;
    private Bitmap icon;


    public Channel(String link, String name) {
        this.link = link;
        this.name = name;
    }

    public Channel(String link) {
        this.link = link;
    }

    public Channel(long id, String link, String name, Date lastUpdate) {
        this.id = id;
        this.link = link;
        this.name = name;
        this.lastUpdate = lastUpdate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }
}
