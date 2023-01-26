package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface LikeDbStorage {

    Map<Integer, Integer> getUsersCountOfLikedSameFilmsByUser(Integer userId);

    List<Film> getUserLikedFilms(Integer userId);

    boolean addLikeFilm(Integer filmId, Integer userId);

    boolean deleteLike(Integer filmId, Integer userId);

    boolean deleteAllLikes(Integer filmId);
}
