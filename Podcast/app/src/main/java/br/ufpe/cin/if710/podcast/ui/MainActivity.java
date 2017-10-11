package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.db.PodcastProvider;
import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.domain.XmlFeedParser;
import br.ufpe.cin.if710.podcast.ui.adapter.XmlFeedAdapter;

public class MainActivity extends Activity {

    //ao fazer envio da resolucao, use este link no seu codigo!
    private final String RSS_FEED = "http://leopoldomt.com/if710/fronteirasdaciencia.xml";
    //TODO teste com outros links de podcast

    private ListView items;
    private PodcastProvider podcastProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this);
        items = (ListView) findViewById(R.id.items);
        this.podcastProvider =  new PodcastProvider(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();

        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) new DownloadXmlTask().execute(RSS_FEED);
        else new AcessDatebase().execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        XmlFeedAdapter adapter = (XmlFeedAdapter) items.getAdapter();
        adapter.clear();
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, List<ItemFeed>> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "iniciando...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected List<ItemFeed> doInBackground(String... params) {
            List<ItemFeed> itemList = new ArrayList<>();
            try {
                itemList = XmlFeedParser.parse(getRssFeed(params[0]));
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
            return itemList;
        }

        @Override
        protected void onPostExecute(List<ItemFeed> feed) {
            Toast.makeText(getApplicationContext(), "terminando...", Toast.LENGTH_SHORT).show();

            //Adapter Personalizado
            XmlFeedAdapter adapter = new XmlFeedAdapter(getApplicationContext(), R.layout.itemlista, feed);

            //atualizar o list view
            items.setAdapter(adapter);
            items.setTextFilterEnabled(true);
        }
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

    private class AcessDatebase extends AsyncTask<Void, Void, List<ItemFeed>>
    {

        @Override
        protected List<ItemFeed> doInBackground(Void... params) {
            Cursor cursor = podcastProvider.query(PodcastProviderContract.EPISODE_LIST_URI, null, null, null, null);
            List<ItemFeed> feed = new ArrayList<ItemFeed>();
            cursor.moveToFirst();
            while(!cursor.isLast())
            {
                //pegando valores cursor e adicionando na lista de feed
                String title = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_TITLE));
                String link = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_LINK));
                String pubDate = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_DATE));
                String description = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_DESC));
                String downloadLink = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_DOWNLOAD_LINK));
                ItemFeed itemFeed = new ItemFeed(title, link, pubDate, description,downloadLink);
                feed.add(itemFeed);
                cursor.moveToNext();
            }
            Log.i("Mostra ultimo valor", cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_TITLE)));
            return feed;
        }

        @Override
        protected void onPostExecute(List<ItemFeed> feed) {
            XmlFeedAdapter xmlFeedAdapter = new XmlFeedAdapter(getApplicationContext(), R.layout.itemlista, feed);
            items.setAdapter(xmlFeedAdapter);
            items.setTextFilterEnabled(true);
        }
    }
}
