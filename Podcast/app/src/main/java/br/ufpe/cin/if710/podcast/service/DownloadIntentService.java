package br.ufpe.cin.if710.podcast.service;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
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
                this.downloadAudio(downloadLink, title);
            }
        }
    }

    private void insert(String rssFeed)
    {
        List<ItemFeed> itemList = new ArrayList<>();
        try {
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
        sendBroadcast(intent);
    }

    private void downloadAudio(String downloadLink, String title)
    {
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
        intent.setAction("br.ufpe.cin.if710.podcast.DOWNLOAD_AUDIO");
        sendBroadcast(intent);
    }
}
