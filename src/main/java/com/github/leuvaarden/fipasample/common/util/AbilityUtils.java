package com.github.leuvaarden.fipasample.common.util;

import com.github.leuvaarden.fipasample.common.data.Ability;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AbilityUtils {
    private AbilityUtils() {
    }

    public static Stream<Stream<Ability>> findChain(List<Ability> abilities, Ability desired) {
        if (desired.getInputType().equals(desired.getOutputType())) {
            return Stream.of(Stream.empty());
        }
        return abilities.stream()
                .filter(ability -> desired.getInputType().equals(ability.getInputType()))
                .filter(ability -> desired.getTime().compareTo(ability.getTime()) >= 0)
                .flatMap(ability -> findChain(abilities, nextAbility(ability, desired)).map(s -> Stream.concat(Stream.of(ability), s)));
    }

    private static Ability nextAbility(Ability current, Ability desired) {
        Ability ability = new Ability();
        ability.setInputType(current.getOutputType());
        ability.setOutputType(desired.getOutputType());
        ability.setTime(desired.getTime().minus(current.getTime()));
        return ability;
    }

    public static List<List<Ability>> findFastest(List<List<Ability>> lists) {
        List<Duration> durations = totalDurations(lists);
        Duration min = getMinOrZero(durations);
        return IntStream.range(0, lists.size())
                .mapToObj(value -> durations.get(value).equals(min) ? lists.get(value) : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static List<Duration> totalDurations(List<List<Ability>> lists) {
        return lists.stream()
                .map(AbilityUtils::totalDuration)
                .collect(Collectors.toList());
    }

    private static Duration totalDuration(List<Ability> abilities) {
        return abilities.stream()
                .reduce(Duration.ZERO, (duration, ability) -> duration.plus(ability.getTime()), Duration::plus);
    }

    private static Duration getMinOrZero(List<Duration> list) {
        return list.stream()
                .min(Duration::compareTo)
                .orElse(Duration.ZERO);
    }
}
