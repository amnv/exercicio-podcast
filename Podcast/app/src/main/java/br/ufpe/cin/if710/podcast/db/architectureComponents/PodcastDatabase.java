package br.ufpe.cin.if710.podcast.db.architectureComponents;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by allyson on 16/12/17.
 */
@Database(entities = {PodcastRoom.class}, version = 1)
public abstract class PodcastDatabase extends RoomDatabase {
    public abstract PodcastDao podcastDao();
}
