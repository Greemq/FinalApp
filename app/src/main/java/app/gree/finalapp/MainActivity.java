package app.gree.finalapp;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.gree.finalapp.Adapter.ListViewAdapter;
import app.gree.finalapp.Adapter.RecyclerViewAdapter;
import app.gree.finalapp.DB.ChannelDAO;
import app.gree.finalapp.DB.DBHelper;
import app.gree.finalapp.DB.RssItemDAO;
import app.gree.finalapp.Model.Channel;
import app.gree.finalapp.Model.RssItem;
import app.gree.finalapp.Parser.GetListRssItems;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;

    private DrawerLayout drawerLayout;

    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;

    private List<RssItem> rssItemList;
    private List<Channel> channelList;
    private Button addChannel, allChannels, bookmarts;
    private ImageButton openDrawer;

    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;

    private ListView listView;
    private ListViewAdapter listViewAdapter;

    private Paint p = new Paint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFields();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //refresh listRssItems button in toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_list:
                Toast.makeText(getApplicationContext(), "refreshing", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(true);
                updateChannelList();
                new GetListRssItems(channelList, getApplicationContext(), swipeRefreshLayout, MainActivity.this).execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initFields() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        dbHelper = new DBHelper(this);
        channelList = new ArrayList<>();
        rssItemList = new ArrayList<>();
        //DropTables();


        //Channel listview init
        listView = (ListView) findViewById(R.id.channel_list);
        listViewAdapter = new ListViewAdapter(this, R.layout.listview_item, channelList);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getChannelNews(channelList.get(position));
            }
        });
        final String[] tempStr = {"1", "2", "3"};
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                deleteChannel(channelList.get(position));
                return false;
            }
        });

        updateChannelList();

        //recycler view init
        recyclerView = (RecyclerView) findViewById(R.id.news_list);
        recyclerViewAdapter = new RecyclerViewAdapter(rssItemList);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        updateRssItemList();
        //swipe refresh init
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiper_refresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getApplicationContext(), "refreshing", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(true);
                updateChannelList();
                new GetListRssItems(channelList, getApplicationContext(), swipeRefreshLayout, MainActivity.this).execute();
            }
        });

        //buttons init
        addChannel = (Button) findViewById(R.id.add_channel_btn);
        addChannel.setOnClickListener(this);
        allChannels = (Button) findViewById(R.id.all_channels_btn);
        allChannels.setOnClickListener(this);
        bookmarts = (Button) findViewById(R.id.bookmarts_btn);
        bookmarts.setOnClickListener(this);
        openDrawer = (ImageButton) findViewById(R.id.open_drawer);
        openDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        initSwipe();
    }

    private void deleteChannel(Channel ch) {
        ChannelDAO channelDAO = new ChannelDAO(this);
        channelDAO.open();
        channelDAO.deleteChannel(ch);
        channelDAO.close();
        updateChannelList();
        updateRssItemList();
    }
    //add channel dialog with textview
    private void addChannelDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.add_channel_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(view);
        final EditText editText = (EditText) view.findViewById(R.id.edit_text_link);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addChannel(editText.getText().toString());
                        updateChannelList();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    //add channel to DB
    public void addChannel(String str) {
        ChannelDAO channelDAO = new ChannelDAO(this);
        channelDAO.open();
        Channel channel = new Channel(str, str);
        channelDAO.addChannel(channel);
        channelDAO.close();
    }
    //refreshChannelList
    public void updateChannelList() {
        getChannelFromDb();
        listViewAdapter.clear();
        listViewAdapter = new ListViewAdapter(this, R.layout.listview_item, channelList);
        listView.setAdapter(listViewAdapter);
    }
    //downloadChannelsFromDb
    public void getChannelFromDb() {
        ChannelDAO channelDAO = new ChannelDAO(this);
        channelDAO.open();
        channelList.clear();
        channelList = channelDAO.getAllChannels();
        for (Channel ch : channelList) {
            ch.setIcon(new IconLoader().getBitmap(this, String.valueOf(ch.getId())));
        }
        channelDAO.close();
    }
    //downloadRssItems from DB
    public void getRssItemsFromDb() {
        RssItemDAO rssItemDAO = new RssItemDAO(this);
        rssItemDAO.open();
        rssItemList.clear();
        rssItemList = rssItemDAO.getAllRssItems();
        Collections.sort(rssItemList, new Comparator<RssItem>() {       //sortByDate desc
            @Override
            public int compare(RssItem o1, RssItem o2) {
                return o2.getPubDate().compareTo(o1.getPubDate());
            }
        });
        rssItemDAO.close();
    }
    //refresh RssItemList
    public void updateRssItemList() {
        getRssItemsFromDb();
        recyclerViewAdapter.setItems(rssItemList);
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recyclerViewAdapter);
    }
    //get channel's news
    public void getChannelNews(Channel channel) {
        drawerLayout.closeDrawers();
        RssItemDAO rssItemDAO = new RssItemDAO(this);
        rssItemDAO.open();
        rssItemList.clear();
        rssItemList = rssItemDAO.getChannelItems(channel.getId());
        rssItemDAO.close();
        recyclerViewAdapter.setItems(rssItemList);
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    //get RssItems which was set as bookmarts
    public void getBookmarts() {
        RssItemDAO rssItemDAO = new RssItemDAO(this);
        rssItemDAO.open();
        rssItemList.clear();
        rssItemList = rssItemDAO.bookmarts();
        Collections.sort(rssItemList, new Comparator<RssItem>() {
            @Override
            public int compare(RssItem o1, RssItem o2) {
                return o2.getPubDate().compareTo(o1.getPubDate());
            }
        });
        rssItemDAO.close();
        recyclerViewAdapter.setItems(rssItemList);
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recyclerViewAdapter);
    }


    private void DropTables() {
        this.deleteDatabase("FinalAppTable.db");
        dbHelper = new DBHelper(this);
        sqLiteDatabase = dbHelper.getWritableDatabase();
        sqLiteDatabase.delete(DBHelper.TABLE_ITEMS, null, null);
        sqLiteDatabase.delete(DBHelper.TABLE_CHANNELS, null, null);

        addChannel("http://4pda.ru/feed/");
        addChannel("https://habrahabr.ru/rss/interesting/");
        addChannel("http://www.football.co.uk/news/rss.xml");
        addChannel("http://feeds.feedburner.com/ChampionshipFootballNews");
        addChannel("http://feeds.skynews.com/feeds/rss/world.xml");
        addChannel("https://news.mail.ru/rss/kazakhstan/");
        addChannel("http://www.economist.com/topics/computer-technology/index.xml");
    }


    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    RssItemDAO rssItemDAO = new RssItemDAO(getApplicationContext());
                    rssItemDAO.open();
                    RssItem tmp = rssItemList.get(position);
                    rssItemDAO.setFavourite(tmp);
                    rssItemDAO.close();
                    recyclerViewAdapter.setItems(rssItemList);
                    recyclerViewAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(recyclerViewAdapter);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    if (dX < 0) {
                        p.setColor(getResources().getColor(R.color.colorPrimary));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = drawableToBitmap(getResources().getDrawable(R.drawable.ic_star_half_black_24dp));
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    //convert from drawable to bitmap
    public Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    //button on click init
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_channel_btn:
                addChannelDialog();
                break;
            case R.id.all_channels_btn:
                drawerLayout.closeDrawers();
                updateRssItemList();
                break;
            case R.id.bookmarts_btn:
                drawerLayout.closeDrawers();
                getBookmarts();
                break;
        }
    }
}
