package com.javaclimb.music.service;

import com.javaclimb.music.domain.ListSong;

import java.util.List;

/**
 * 歌单里面的歌曲Service
 */
public interface ListSongService {
    /**
     * 增加
     */
    public boolean insert(ListSong listSong);

    /**
     * 修改
     */
    public boolean update(ListSong listSong);

    /**
     * 删除
     */
    public boolean delete(Integer id);

    /**
     * 根据歌曲id和歌单id删除
     */
    public boolean deleteBySongIdAndSongListId(List<ListSong> list);

    /**
     * 根据主键查询整个对象
     */
    public ListSong selectByPrimaryKey(Integer id);

    /**
     * 查询所有歌单里面的歌曲
     */
    public List<ListSong> allListSong();

    /**
     * 根据歌单id查询所有的歌曲
     */
    public List<ListSong> listSongOfSongList(Integer songListId);

    /**
     * 根据歌单id查询歌单中不存在的歌曲
     */
    public List<ListSong> songNoInList(Integer songListId,String songName,String username);
}

