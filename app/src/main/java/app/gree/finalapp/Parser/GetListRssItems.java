package app.gree.finalapp.Parser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.gree.finalapp.DB.ChannelDAO;
import app.gree.finalapp.DB.RssItemDAO;
import app.gree.finalapp.IconLoader;
import app.gree.finalapp.MainActivity;
import app.gree.finalapp.Model.Channel;
import app.gree.finalapp.Model.RssItem;
import app.gree.finalapp.R;

/**
 * RssItemsList download
 *
 *
 */

public class GetListRssItems extends AsyncTask<Void, Void, Void> {
    private List<Channel> channels;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MainActivity a;
    private static int count = 0;

    public GetListRssItems(List<Channel> channels, Context context, SwipeRefreshLayout swipeRefreshLayout, MainActivity a) {
        this.channels = channels;
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.a = a;
    }


    private void getBitmap(Channel ch) {
        Uri uri = Uri.parse(ch.getLink());
        String host = uri.getHost();
        String scheme = uri.getScheme();
        IconLoader iconLoader = new IconLoader();
        /*
        * downloading bitmap from link
        * Scheme=http/https
        * host=4pda.ru
        * http://4pda.ru/favicon.ico
        * */
        Bitmap b = iconLoader.getBitmapFromUrl(scheme + "://" + host + "/favicon.ico", String.valueOf(ch.getId()));
        if (b == null) {
            b = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_brightness_1_black_24dp);
        }
        iconLoader.saveImage(context, b, String.valueOf(ch.getId()));
    }

    @Override
    protected Void doInBackground(Void... params) {
        Document doc;
        String TITLE = "title";
        String LINK = "link";
        String PUBDATE = "pubDate";
        try {
            RssItemDAO rssItemDao = new RssItemDAO(context);
            rssItemDao.open();
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            for (Channel ch : channels) {
                /*
                * RssFeeds sometimes are comming in XML format
                *
                * */
                if (ch.getLink().contains(".xml")) {
                    doc = Jsoup.parse(new URL(ch.getLink()).openStream(), "UTF-8", "", Parser.xmlParser());
                } else
                    doc = Jsoup.connect(ch.getLink()).get();

                //parsing all elements in tag <item>
                Elements elements = doc.select("item");
                ch.setName(String.valueOf(doc.title()));

                if (ch.getLastUpdate() == null) {
                    getBitmap(ch);
                }

                /*
                * parsing  LINK,TITLE,PUBDATE from <item> tag
                * saving to DB
                */
                for (Element e : elements) {
                    RssItem rssItem = new RssItem();
                    rssItem.setTitle(e.select(TITLE).first().ownText());
                    rssItem.setLink(e.select(LINK).first().ownText());
                    Date pub = formatter.parse(e.select(PUBDATE).first().ownText());
                    String pubDt = pub.toString();
                    rssItem.setPubDate(pub);
                    rssItem.setFavourite(0);
                    rssItem.setChannel(ch);
                    /*
                    * if channel lastUpdate==null - news haven't been downloaded
                    * if channel lastUpdate is date - if items pub date is greater than
                    * lastUpdate, adding to DB
                    */
                    if (ch.getLastUpdate() == null || pub.after(ch.getLastUpdate())) {
                        rssItemDao.addRssItem(rssItem);
                        count++;
                    } else
                        break;
                }
                /*
                *
                * */
                Calendar c = Calendar.getInstance();
                String newDate = outputFormat.format(c.getTime());
                ChannelDAO channelDAO = new ChannelDAO(context);
                channelDAO.open();
                channelDAO.updateChannel(ch, newDate);
                channelDAO.close();
            }
            rssItemDao.close();
        } catch (Exception ex) {
            Log.e("EXCEPTION", ex.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(context, "Downloaded " + count + " newses", Toast.LENGTH_SHORT).show();
        a.updateChannelList();
        a.updateRssItemList();
    }
}
