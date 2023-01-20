package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface RecommendationDbStorage {
    Map<Integer, Integer> getUsersCountOfLikedSameFilmsByUser(int userId);

    List<Film> getUserLikedFilms(Integer userId);
}
