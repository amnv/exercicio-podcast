package br.ufpe.cin.if710.podcast.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class PodcastProvider extends ContentProvider {

    private PodcastDBHelper db;
    public PodcastProvider(Context c) {
        db = PodcastDBHelper.getInstance(c);
    }

    public PodcastProvider() { }

    @Override
    public boolean onCreate()
    {
        return false;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return db.getWritableDatabase().delete(PodcastProviderContract.EPISODE_TABLE, selection, selectionArgs);
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        db.getWritableDatabase().insert(PodcastProviderContract.EPISODE_TABLE, null, values);
        return null;
    }

    /**
     *
     * @param uri
     * @param projection column list
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return db.getWritableDatabase().
                query(PodcastProviderContract.EPISODE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return db.getWritableDatabase().update(PodcastProviderContract.EPISODE_TABLE, values, selection,selectionArgs);
    }
}
