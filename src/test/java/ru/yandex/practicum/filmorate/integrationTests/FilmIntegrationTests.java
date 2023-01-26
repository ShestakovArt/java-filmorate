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
import ru.yandex.practicum.filmorate.dao.LikeDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmIntegrationTests {
    final FilmDbStorage filmDbStorage;
    final GenreDbStorage genreDbStorage;
    final UserDbStorage userStorage;
    final LikeDbStorage likeDbStorage;

    @BeforeEach
    void createdFilmForDB() {
        if (filmDbStorage.findAll().size() != 2) {
            List<Genre> genres = new ArrayList<>();
            genres.add(genreDbStorage.findGenreById(2));

            List<Director> directors = new ArrayList<>();
            directors.add(new Director(5, "То́мас Ян"));

            Film film = new Film("Достучатся до небес",
                    "Немецкий кинофильм 1997 года режиссёра Томаса Яна",
                    "1997-02-20",
                    87, 4, new Mpa(1, "G"), genres, directors);
            filmDbStorage.addFilm(film);
            genreDbStorage.setFilmGenre(1, 2);

            Film filmNext = new Film("Тестовая драмма",
                    "Тестовый фильм",
                    "2022-01-01",
                    75, 0, new Mpa(2, "PG"), genres, directors);
            filmDbStorage.addFilm(filmNext);

            genreDbStorage.setFilmGenre(2, 2);
        }
        if (userStorage.findAll().size() != 2) {
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
        genres.add(genreDbStorage.findGenreById(2));

        List<Director> directors = new ArrayList<>();
        directors.add(new Director(5, "То́мас Ян"));

        Film updateFilm = new Film("Достучатся до небес",
                "updateTest",
                "1997-02-20",
                87, 4, new Mpa(1, "G"), genres, directors);
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
    public void testFindFilm() {
        checkFindFilmById(1);
    }

    @Test
    public void testFindAll() {
        Collection<Film> currentList = filmDbStorage.findAll();
        System.out.println(currentList.size());
        assertTrue(currentList.size() == 2, "Не корректное количество фильмов");
    }

    @Test
    public void testSetGenreFilm() {
        assertTrue(genreDbStorage.setFilmGenre(1, 1), "Жанр фильма не изменился");
        List<Genre> genres = new ArrayList<>();
        genres.add(genreDbStorage.findGenreById(1));
        genres.add(genreDbStorage.findGenreById(2));
        Optional<Film> filmOptional = filmDbStorage.findFilm(1);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("genres", genres)
                );

        genreDbStorage.deleteFilmGenre(1, 1);
    }

    @Test
    public void testDeleteGenreFilm() {
        assertTrue(genreDbStorage.deleteFilmGenre(2, 2), "Жанр фильма не изменился");
        List<Genre> genres = new ArrayList<>();
        Optional<Film> filmOptional = filmDbStorage.findFilm(2);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("genres", genres)
                );

        genreDbStorage.setFilmGenre(2, 2);
    }

    @Test
    public void testGetGenresFilm() {
        List<Genre> genreList = genreDbStorage.getFilmGenres(1);
        Optional<Film> filmOptional = filmDbStorage.findFilm(1);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("genres", genreList)
                );
    }

    @Test
    public void testAddLikeFilm() {
        assertTrue(likeDbStorage.addLikeFilm(1, 1), "пользователь не лайкнул фильм");
        likeDbStorage.deleteLike(1, 1);
    }

    @Test
    public void testDeleteLike() {
        likeDbStorage.addLikeFilm(1, 1);
        assertTrue(likeDbStorage.deleteLike(1, 1), "Лайк не удален");
    }

    @Test
    public void testListMostPopularFilms() {
        likeDbStorage.addLikeFilm(1, 1);
        List<Film> filmList = filmDbStorage.listMostPopularFilms(1, null, null);
        assertTrue(filmList.size() == 1, "Размер списка фильмов не соответсвует");
        Optional<Film> filmOptional = filmDbStorage.findFilm(1);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("rate", film.getRate())
                );

        filmList = filmDbStorage.listMostPopularFilms(2, null, null);
        assertTrue(filmList.size() == 2, "Размер списка фильмов не соответсвует");
        filmOptional = filmDbStorage.findFilm(2);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("rate", film.getRate())
                );
    }

    void checkFindFilmById(Integer filmId) {
        Optional<Film> filmOptional = filmDbStorage.findFilm(filmId);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", filmId)
                );
    }
}
