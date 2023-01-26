# java-filmorate

Template repository for Filmorate project.
<img src="resources/diagram.png" object-fit:cover width=100% height=400>

- **USERS** - основная информация по пользователю
- **FRIENDSHIP_REQUESTS** - запросы в друзья
- **FILMS** - основная информация по фильмам
- **USER_LIKE_FILM** - понравившиеся фильмы пользователю
- **GENRE** - список жанров
- **FILM_TO_GENRE** - жанры фильмов
- **MPA** - рейтинг Ассоциации кинокомпаний и описание
- **DIRECTOR** - список режиссеров
- **DIRECTOR_TO_FILM** - режиссеры фильмов
- **REVIEWS** - отзывы пользователей
- **REVIEW_LIKES** - рейтинг отзывов
- **EVENT** - лента событий пользователя

Пример:

```sql
SELECT f.FILM_ID,
       f.FILM_NAME,
       f.FILM_DESCRIPTION,
       f.FILM_RELEASE_DATE,
       f.FILM_DURATION,
       f.FILM_RATE,
       f.MPA_ID
FROM FILMS f
         RIGHT JOIN DIRECTOR_TO_FILM dtf ON dtf.FILM_ID = f.FILM_ID
         RIGHT JOIN DIRECTOR dir ON dir.DIRECTOR_ID = dtf.DIRECTOR_ID
WHERE LOWER(dir.DIRECTOR_NAME) LIKE LOWER(?)
```

### Технологический стек

#### Языки программирования

- JAVA

#### Используемый Framework's

- Spring Boot

#### Система сборки

- Maven

#### База данных

- H2 SQL

### Архитектура

- REST API

____

##### Состав команды разработки

- Бегимова Гаухар
- Бузмаков Виктор
- Телейчук Игорь
- Шестаков Артём
- Ябров Николай 