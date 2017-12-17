package br.ufpe.cin.if710.podcast.db.architectureComponents;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by allyson on 16/12/17.
 */
@Dao
public interface PodcastDao {
    @Insert
    void insert(PodcastRoom... podcastRooms);

    @Delete
    void delete(PodcastRoom podcastRoom);

    @Update
    void update(PodcastRoom podcastRoom);

    @Query("select * from episodes")
    List<PodcastRoom> getAll();

    @Query("select downloadLink from episodes where title= :title")
    List<String> getTitle(String title);

    @Query("select downloadUri from episodes where title = :title")
    List<String> getDownloadUri(String title);

    @Query("Select * from episodes where title = :title")
    List<PodcastRoom> getPodcastRoom(String title);
}
