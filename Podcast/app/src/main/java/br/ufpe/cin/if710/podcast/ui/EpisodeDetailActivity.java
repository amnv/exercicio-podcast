package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.db.PodcastProvider;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;

public class EpisodeDetailActivity extends Activity {

    private PodcastProvider podcastProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_detail);

        //TODO preencher com informações do episódio clicado na lista...

        this.podcastProvider = new PodcastProvider(getApplicationContext());
        Intent intent = getIntent();
        //retriving data from mainActivity
        String text = intent.getStringExtra(ItemFeed.CLICKED_ITEM);
        String pubDate = intent.getStringExtra("PUBDATE");
        Log.i("CONTEUDO_CLICADO", text);

        //add information to view
        TextView textView = findViewById(R.id.viewTitle);
        textView.setText(text);

        TextView pubDateView = findViewById(R.id.viewPubDate);
        pubDateView.setText(pubDate);


    }


    /*class RetrieveData extends AsyncTask<String, Void, Cursor>
    {

        @Override
        protected Cursor doInBackground(String... params) {
            String seletion = "title=?";
            String selArgs[] = {params[0]};
            return podcastProvider.query(PodcastProviderContract.EPISODE_LIST_URI, null, seletion, selArgs, null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            String title = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_TITLE));
            String link = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_LINK));
            String pubdate = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_DATE));
            String description = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_DESC));

            TextView titleView = findViewById(R.id.viewTitle);
            titleView.setText(title);

            TextView linkView = findViewById(R.id.viewLink);
            linkView.setText(link);

            TextView pubDateView = findViewById(R.id.viewPubDate);
            pubDateView.setText(pubdate);

            TextView descView = findViewById(R.id.viewDescription);
            descView.setText(description);

         String a = cursor.getString(1);
            Log.i("mostra_valor_cursor", a);
            cursor.close();
        }
    }*/
}
