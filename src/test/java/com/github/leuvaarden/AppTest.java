package com.github.leuvaarden;

import com.github.leuvaarden.fipasample.common.util.AbilityUtils;
import com.github.leuvaarden.fipasample.common.data.Ability;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class AppTest {
    @Test
    public void testAbilitySearcher() {
        Ability desired = new Ability();
        desired.setInputType("String");
        desired.setOutputType("Integer");
        desired.setTime(Duration.ofSeconds(30));

        Ability stringToInteger = new Ability();
        stringToInteger.setInputType("String");
        stringToInteger.setOutputType("Integer");
        stringToInteger.setTime(Duration.ofSeconds(30));

        Ability stringToDouble = new Ability();
        stringToDouble.setInputType("String");
        stringToDouble.setOutputType("Double");
        stringToDouble.setTime(Duration.ofSeconds(10));

        Ability doubleToInteger = new Ability();
        doubleToInteger.setInputType("Double");
        doubleToInteger.setOutputType("Integer");
        doubleToInteger.setTime(Duration.ofSeconds(10));

        Ability doubleToFloat = new Ability();
        doubleToFloat.setInputType("Double");
        doubleToFloat.setOutputType("Float");
        doubleToFloat.setTime(Duration.ofSeconds(5));

        Ability doubleToBoolean = new Ability();
        doubleToBoolean.setInputType("Double");
        doubleToBoolean.setOutputType("Boolean");
        doubleToBoolean.setTime(Duration.ofSeconds(15));

        Ability floatToInteger = new Ability();
        floatToInteger.setInputType("Float");
        floatToInteger.setOutputType("Integer");
        floatToInteger.setTime(Duration.ofSeconds(5));

        Ability booleanToInteger = new Ability();
        booleanToInteger.setInputType("Boolean");
        booleanToInteger.setOutputType("Integer");
        booleanToInteger.setTime(Duration.ofSeconds(10));

        List<Ability> abilityList = List.of(
                booleanToInteger,
                floatToInteger,
                doubleToBoolean,
                doubleToFloat,
                doubleToInteger,
                stringToDouble,
                stringToInteger
        );

        List<List<Ability>> result = AbilityUtils.findChain(abilityList, desired)
                .map(abilityStream -> abilityStream.collect(Collectors.toList()))
                .collect(Collectors.toList());
        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());

        List<List<Ability>> fastest = AbilityUtils.findFastest(result);
        Assert.assertNotNull(fastest);
        Assert.assertEquals(2, fastest.size());
    }
}
