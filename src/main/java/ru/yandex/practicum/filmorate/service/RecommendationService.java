package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final LikeService likeService;

    public List<Film> getRecommendations(Integer userId) {
        Map<Integer, Integer> mapUserIdWithCountLikes = getUserIdWithCountLikes(userId);
        if (mapUserIdWithCountLikes.size() < 1) {
            return new ArrayList<>();
        }

        Optional<Integer> maxCountLike = mapUserIdWithCountLikes.values().stream().max(Integer::compareTo);
        List<Integer> userForRecommends = mapUserIdWithCountLikes.entrySet().stream()
                .filter(a -> a.getValue().equals(maxCountLike.get()))
                .map(a -> a.getKey()).collect(Collectors.toList());

        return getFilmsRecommendations(userForRecommends, userId);
    }

    private Map<Integer, Integer> getUserIdWithCountLikes(Integer userId) {
        return likeService.getUsersCountOfLikedSameFilmsByUser(userId);
    }

    private List<Film> getFilmsRecommendations(List<Integer> userForRecommends, Integer userId) {
        List<Film> listFilmRecommendation = new ArrayList<>();
        for (Integer anotherUserId : userForRecommends) {
            List<Film> temp = likeService.getUserLikedFilms(anotherUserId);
            temp.removeAll(likeService.getUserLikedFilms(userId));
            listFilmRecommendation.addAll(temp);
        }
        return listFilmRecommendation;
    }
}
