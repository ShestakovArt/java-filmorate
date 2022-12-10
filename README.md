# java-filmorate
Template repository for Filmorate project.
<img src="resources/ER_scheme.svg" object-fit:cover width=100% height=400>
- **User** - основная информация по пользователю
* **UsersMovieLibrary** - отслеживает какие фильмы пользователь добавляет в свою библиотеку
+ **FriendshipRequests** - отслеживает запросы в друзья пользоватлей, и их статус
+ **Films** - основная информация по фильмам
+ **FilmToGenre** - отслеживает принодлежность фильма к жанрам
+ **Genre** - список жанров
+ **MPA** - рейтинг Ассоциации кинокомпаний и описание