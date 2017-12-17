package br.ufpe.cin.if710.podcast.service;

import android.app.DownloadManager;
import android.app.IntentService;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.if710.podcast.db.architectureComponents.PodcastDatabase;
import br.ufpe.cin.if710.podcast.db.architectureComponents.PodcastRoom;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.domain.XmlFeedParser;
import br.ufpe.cin.if710.podcast.ui.adapter.XmlFeedAdapter;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DownloadIntentService extends IntentService {

    public static final String ACTION_INSERT_DATABASE = "inserir";
    public static final String ACTION_DOWNLOAD_AUDIO = "download";
    public static final String POSICAO_ITEM = "posicao";
    public static final String ESTADO_ITEM = "estado";

    public PodcastDatabase db;
  //  private PodcastProvider podcastProvider;


    public DownloadIntentService() {
        super("DownloadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
       // this.podcastProvider = new PodcastProvider(getApplicationContext());

        Log.i("DownloadIntentService", "entrou");

        if (intent != null)
        {
            this.db = Room.databaseBuilder(this, PodcastDatabase.class, "episodes").build();
            final String action = intent.getAction();
            if (ACTION_INSERT_DATABASE.equals(action))
            {
                String RSS_FEED = intent.getStringExtra("RSS_FEED");
                this.insert(RSS_FEED);
            }
            else if (ACTION_DOWNLOAD_AUDIO.equals(action))
            {
                String downloadLink = intent.getStringExtra(XmlFeedAdapter.DOWNLOAD_LINK);
                String title = intent.getStringExtra(XmlFeedAdapter.TITLE_NAME);
                int pos = intent.getIntExtra(POSICAO_ITEM, 0);
                String estado = intent.getStringExtra(ESTADO_ITEM);
                this.downloadAudio(downloadLink, title, pos, estado);
            }
        }
    }

    private void insert(String rss)
    {
        List<ItemFeed> itemList = new ArrayList<>();
        try {
            String rssFeed = this.getRssFeed(rss);
            Log.d("valorRSS", rssFeed);
            itemList = XmlFeedParser.parse(rssFeed);
            List<ItemFeed> feed = new ArrayList<>();
            List<PodcastRoom> podcast = db.podcastDao().getAll();
            int itemListSize = itemList.size();
            if (podcast.size() != itemListSize)
            {
                PodcastRoom addPodcast[] = new PodcastRoom[itemListSize];
                int i = 0;
                for(ItemFeed it : itemList)
                {
                    addPodcast[i] = new PodcastRoom(it.getTitle(),
                            it.getPubDate(), it.getLink(), it.getDescription(),
                        it.getDownloadLink());
                    i++;
                }

                db.podcastDao().insert(addPodcast);
            }

//            Cursor cursor = this.podcastProvider.query(PodcastProviderContract.EPISODE_LIST_URI, null, null, null, null);
//            cursor.moveToFirst();
//
//            //Update database only if had web update
//            if (itemList.size() != cursor.getCount())
//            {
//
//                for (ItemFeed it : itemList) {
//                    ContentValues cv = new ContentValues();
//                    cv.put(PodcastProviderContract.EPISODE_TITLE, it.getTitle());
//                    cv.put(PodcastProviderContract.EPISODE_DATE, it.getPubDate());
//                    cv.put(PodcastProviderContract.EPISODE_LINK, it.getLink());
//                    cv.put(PodcastProviderContract.EPISODE_DESC, it.getDescription());
//                    cv.put(PodcastProviderContract.EPISODE_DOWNLOAD_LINK, it.getDownloadLink());
//
//                    podcastProvider.insert(PodcastProviderContract.EPISODE_LIST_URI, cv);
//                }
                Log.i("insert database", "inserido no banco");
//            }
            //cursor.close();
        }
        catch(IOException i)
        {
            i.printStackTrace();
        } catch(XmlPullParserException e)
        {
            e.printStackTrace();
        }


        Intent intent = new Intent();
        intent.setAction("br.ufpe.cin.if710.podcast.INSERIDO");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void downloadAudio(String downloadLink, String title, int pos, String estado)
    {
        Log.i("link para download", downloadLink);
        //baixando audio
        DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadLink));
        Long id = downloadManager.enqueue(request);

        //pagando filePath
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor = downloadManager.query(query);

        String filePath = "";
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                filePath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
            }
        }
        cursor.close();

        //adicionando uri no banco
        Log.i("filePath", filePath);
//        String arg[] = {title};
//        ContentValues c = new ContentValues();
//        c.put(PodcastProviderContract.EPISODE_FILE_URI, filePath);
//        PodcastProvider podcastProvider = new PodcastProvider(getApplicationContext());
//        podcastProvider
//                .update(PodcastProviderContract.EPISODE_LIST_URI, c, "title=?", arg);
        PodcastRoom p = db.podcastDao().getPodcastRoom(title).get(0);
        p.setDownloadUri(filePath);
        db.podcastDao().update(p);
        Intent intent = new Intent();
        intent.putExtra(POSICAO_ITEM, pos);
        intent.putExtra(ESTADO_ITEM, estado);
        intent.setAction("br.ufpe.cin.if710.podcast.DOWNLOAD_AUDIO");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("ErroDirectory", "Directory not created");
        }
        return file;
    }

    //TODO Opcional - pesquise outros meios de obter arquivos da internet
     private String getRssFeed(String feed) throws IOException {
        InputStream in = null;
        String rssFeed = "";
        try {
            URL url = new URL(feed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            rssFeed = new String(response, "UTF-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return rssFeed;
    }
}
