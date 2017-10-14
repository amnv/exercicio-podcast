package br.ufpe.cin.if710.podcast.service;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.if710.podcast.db.PodcastProvider;
import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;
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

    private PodcastProvider podcastProvider;


    public DownloadIntentService() {
        super("DownloadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.podcastProvider = new PodcastProvider(getApplicationContext());

        Log.i("DownloadIntentService", "entrou");

        if (intent != null)
        {
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
            for (ItemFeed it : itemList)
            {
                ContentValues cv = new ContentValues();
                cv.put(PodcastProviderContract.EPISODE_TITLE, it.getTitle());
                cv.put(PodcastProviderContract.EPISODE_DATE, it.getPubDate());
                cv.put(PodcastProviderContract.EPISODE_LINK, it.getLink());
                cv.put(PodcastProviderContract.EPISODE_DESC, it.getDescription());
                cv.put(PodcastProviderContract.EPISODE_DOWNLOAD_LINK, it.getDownloadLink());

                podcastProvider.insert(PodcastProviderContract.EPISODE_LIST_URI, cv);
            }
        } catch (IOException e) {
            e.printStackTrace();
            } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        Log.i("insert database", "inserido no banco");
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
        downloadManager.enqueue(request);

        //adicionando uri no banco
        String arg[] = {title};
        ContentValues c = new ContentValues();
        c.put(PodcastProviderContract.EPISODE_FILE_URI, downloadManager.COLUMN_LOCAL_URI);
        PodcastProvider podcastProvider = new PodcastProvider(getApplicationContext());
        podcastProvider
                .update(PodcastProviderContract.EPISODE_LIST_URI, c, "title=?", arg);

        Intent intent = new Intent();
        intent.putExtra(POSICAO_ITEM, pos);
        intent.putExtra(ESTADO_ITEM, estado);
        intent.setAction("br.ufpe.cin.if710.podcast.DOWNLOAD_AUDIO");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
