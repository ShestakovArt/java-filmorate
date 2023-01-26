package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.LikeDbStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeDbStorage likeDbStorage;

    public boolean addLikeFilm(Integer filmId, Integer userId) {
        return likeDbStorage.addLikeFilm(filmId, userId);
    }

    public boolean deleteLike(Integer filmId, Integer userId) {
        return likeDbStorage.deleteLike(filmId, userId);
    }

    public boolean deleteAllLikes(Integer filmId) {
        return likeDbStorage.deleteAllLikes(filmId);
    }

    public List<Film> getUserLikedFilms(Integer userId) {
        return likeDbStorage.getUserLikedFilms(userId);
    }

    public Map<Integer, Integer> getUsersCountOfLikedSameFilmsByUser(Integer userId) {
        return likeDbStorage.getUsersCountOfLikedSameFilmsByUser(userId);
    }
}
