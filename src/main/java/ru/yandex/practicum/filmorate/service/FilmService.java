package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Stream;

@Service
public class FilmService {
    FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public HashMap<Integer, Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public void deleteFilm(Integer id) {
        filmStorage.deleteFilm(id);
    }

    public void upgradeFilm(Film film) {
        filmStorage.upgradeFilm(film);
    }

    public Collection<Film> getFilmsList(){
        return filmStorage.getFilms().values();
    }

    public void addLikeFilm(Integer filmId, Integer userId){
        if (filmId > 0 && userId > 0 && !filmStorage.getFilms().get(filmId).getLikes().contains(userId)){
            filmStorage.getFilms().get(filmId).addLike(userId);
        }
    }

    public void deleteLikeFilm(Integer filmId, Integer userId){
        if (filmId > 0 && userId > 0){
            filmStorage.getFilms().get(filmId).deleteLike(userId);
        }
    }

    public LinkedList<Film> getMostPopularMoviesOfLikes(Integer count){
        Comparator<Film> filmComparator = (film1, film2) -> {
            if(film2.getRate().compareTo(film1.getRate()) == 0){
                return film1.getName().compareTo(film2.getName());
            }
            return film2.getRate().compareTo(film1.getRate());
        };
        LinkedList<Film> filmLinkedList = new LinkedList<>();
        List<Film> filmList = new ArrayList<>(getFilmsList());
        filmList.sort(filmComparator);
        System.out.println(filmList);
        for (Film film : filmList){
            if(filmLinkedList.size() < count){
                filmLinkedList.add(film);
            }
            else{
                break;
            }
        }
        return filmLinkedList;
    }
}
