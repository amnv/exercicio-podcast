package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.db.PodcastProvider;
import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.service.DownloadIntentService;
import br.ufpe.cin.if710.podcast.ui.adapter.XmlFeedAdapter;

public class MainActivity extends Activity {

    //ao fazer envio da resolucao, use este link no seu codigo!
    private final String RSS_FEED = "http://leopoldomt.com/if710/fronteirasdaciencia.xml";
    //TODO teste com outros links de podcast

    private ListView items;
    private int positionClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this);
        items = (ListView) findViewById(R.id.items);
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

        //adicionando br.ufpe.cin.if710.podcast.receiver
        IntentFilter intentFilter = new IntentFilter("br.ufpe.cin.if710.podcast.DOWNLOAD_AUDIO");
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadingAudio, intentFilter);

        IntentFilter insertedFilter = new IntentFilter("br.ufpe.cin.if710.podcast.INSERIDO");
        LocalBroadcastManager.getInstance(this).registerReceiver(insertedDatabase, insertedFilter);

        //Called when download is fineshed
        IntentFilter finishedDownloadFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadAudioFinished, finishedDownloadFilter);

        if (isConnected)
        {
            Intent intent = new Intent(this, DownloadIntentService.class);
            intent.setAction(DownloadIntentService.ACTION_INSERT_DATABASE);
            intent.putExtra("RSS_FEED", RSS_FEED);
            startService(intent);
        } else this.acessDatabase();
    }

    @Override
    protected void onStop() {
        super.onStop();
        XmlFeedAdapter adapter = (XmlFeedAdapter) items.getAdapter();
       if (adapter != null) adapter.clear();
        /*
        unregisterReceiver(downloadingAudio);
        unregisterReceiver(insertedDatabase);
        unregisterReceiver(downloadAudioFinished);
        */
    }

    private void acessDatabase()
    {
        PodcastProvider podcastProvider = new PodcastProvider(getApplicationContext());
        Cursor cursor = podcastProvider.query(PodcastProviderContract.EPISODE_LIST_URI, null, null, null, null);
        List<ItemFeed> feed = new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
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
            Log.d("recuperando do banco", title);
        }

        XmlFeedAdapter xmlFeedAdapter = new XmlFeedAdapter(getApplicationContext(), R.layout.itemlista, feed);
        items.setAdapter(xmlFeedAdapter);
        items.setTextFilterEnabled(true);
    }

    BroadcastReceiver downloadingAudio = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.i("downloadAudioReceiver", "download em andamento");
            positionClicked = intent.getIntExtra(DownloadIntentService.POSICAO_ITEM, 0);
            Log.d("valorPosicao", positionClicked + "");
            String estado = intent.getStringExtra(DownloadIntentService.ESTADO_ITEM);
            Button button = items.getChildAt(positionClicked).findViewById(R.id.item_action);
            button.setText(estado);
        }
    };


    BroadcastReceiver insertedDatabase = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("insertDataBse", "Mostrando na tela");
            acessDatabase();

            //enviando notificacao
            //notification();
        }
    };

    BroadcastReceiver downloadAudioFinished = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("downloadAudioFineshed", "download Concluido");
            Log.d("valorPosicaobaixado", positionClicked + "");

            Button button = items.getChildAt(positionClicked).findViewById(R.id.item_action);
            button.setText("PLAY");
        }
    };

    private void notification()
    {

        NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(this)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
       mNotificationManager.notify(123, mBuilder.build());
    }
}

