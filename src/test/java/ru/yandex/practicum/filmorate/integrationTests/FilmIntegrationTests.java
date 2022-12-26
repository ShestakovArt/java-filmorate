package ru.yandex.practicum.filmorate.integrationTests;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level= AccessLevel.PRIVATE)
public class FilmIntegrationTests {
    final FilmDbStorage filmDbStorage;
    final GenreDbStorage genreDbStorage;
    final UserDbStorage userStorage;

    @BeforeEach
    void createdFilmForDB(){
        if(filmDbStorage.findAll().size() != 2){
            List<Genre> genres = new ArrayList<>();
            genres.add(new Genre(2, genreDbStorage.findNameGenre(2)));
            Film film = new Film("Достучатся до небес",
                    "Немецкий кинофильм 1997 года режиссёра Томаса Яна",
                    "1997-02-20",
                    87, 4, new Mpa(1, "G"), genres);
            filmDbStorage.addFilm(film);
            filmDbStorage.setGenreFilm(1, 2);

            Film filmNext = new Film("Тестовая драмма",
                    "Тестовый фильм",
                    "2022-01-01",
                    75, 0, new Mpa(2, "PG"), genres);
            filmDbStorage.addFilm(filmNext);

            filmDbStorage.setGenreFilm(2, 2);
        }
        if(userStorage.findAll().size() != 2){
            User firstTestUser = new User("testUserOne@yandex.ru",
                    "UserOne",
                    "Tester",
                    "2000-01-01");
            userStorage.addUser(firstTestUser);
            User SecondTestUser = new User("testUserTwo@yandex.ru",
                    "UserTwo",
                    "Toster",
                    "2000-02-01");
            userStorage.addUser(SecondTestUser);
        }
    }

    @Test
    public void testAddFilm() {
        checkFindFilmById(1);
        checkFindFilmById(2);
    }

    @Test
    public void testUpgradeFilm() {
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre(2, genreDbStorage.findNameGenre(2)));
        Film updateFilm = new Film("Достучатся до небес",
                "updateTest",
                "1997-02-20",
                87, 4, new Mpa(1, "G"), genres);
        updateFilm.setId(1);

        filmDbStorage.upgradeFilm(updateFilm);

        Optional<Film> filmOptional = filmDbStorage.findFilm(1);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description", "updateTest")
                );
    }

    @Test
    public void testFindFilm(){
        checkFindFilmById(1);
    }

    @Test
    public void testFindAll(){
        Collection<Film> currentList = filmDbStorage.findAll();
        System.out.println(currentList.size());
        assertTrue(currentList.size() == 2, "Не корректное количество фильмов");
    }

    @Test
    public void testSetGenreFilm(){
        assertTrue(filmDbStorage.setGenreFilm(1, 1), "Жанр фильма не изменился");
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre(2, genreDbStorage.findNameGenre(2)));
        genres.add(new Genre(1, genreDbStorage.findNameGenre(1)));
        Optional<Film> filmOptional = filmDbStorage.findFilm(1);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("genres", genres)
                );

        filmDbStorage.deleteGenreFilm(1, 1);
    }

    @Test
    public void testDeleteGenreFilm(){
        assertTrue(filmDbStorage.deleteGenreFilm(2, 2), "Жанр фильма не изменился");
        List<Genre> genres = new ArrayList<>();
        Optional<Film> filmOptional = filmDbStorage.findFilm(2);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("genres", genres)
                );

        filmDbStorage.setGenreFilm(2, 2);
    }

    @Test
    public void testGetGenresFilm(){
        List<Genre> genreList = filmDbStorage.getGenresFilm(1);
        Optional<Film> filmOptional = filmDbStorage.findFilm(1);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("genres", genreList)
                );
    }

    @Test
    public void testAddLikeFilm(){
        assertTrue(filmDbStorage.addLikeFilm(1, 1), "пользователь не лайкнул фильм");
        filmDbStorage.deleteLike(1, 1);
    }

    @Test
    public void testDeleteLike(){
        filmDbStorage.addLikeFilm(1, 1);
        assertTrue(filmDbStorage.deleteLike(1, 1), "Лайк не удален");
    }

    @Test
    public void testListMostPopularFilms(){
        filmDbStorage.addLikeFilm(1, 1);
        List<Film> filmList = filmDbStorage.listMostPopularFilms(1);
        assertTrue(filmList.size() == 1, "Размер списка фильмов не соответсвует");
        Optional<Film> filmOptional = filmDbStorage.findFilm(1);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("rateAndLikes", film.getRate() + 1)
                );

        filmList = filmDbStorage.listMostPopularFilms(2);
        assertTrue(filmList.size() == 2, "Размер списка фильмов не соответсвует");
        filmOptional = filmDbStorage.findFilm(2);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("rateAndLikes", film.getRate())
                );
    }

    void checkFindFilmById(Integer idFilm){
        Optional<Film> filmOptional = filmDbStorage.findFilm(idFilm);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", idFilm)
                );
    }
}
