package com.wavesplatform.steps;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;


public class CommonSteps {

    private String url;
    private BackendSteps backendSteps;

    public CommonSteps(String url) {
        this.url = url;
        this.backendSteps = new BackendSteps();
    }




    public void checkThatListIsEmpty(List list) {
        assertThat("Список не пустой!", list.isEmpty());
    }

    public void checkThatListIsNotEmpty(List list) {
        assertThat("Список пуст...", not(list.isEmpty()));
    }

//    ("Check list elements not null")
//    public <T> void checkThatListItemsNotNull(List<T> list) {
//        assertThat(list.stream().allMatch(Objects::nonNull));
//    }



    public void resultCodeShouldBeError(String resultCode) {
        assertThat(resultCode, equalTo("error"));
    }

    public void resultCodeShouldBeOK(String resultCode) {
        assertThat(resultCode, equalTo("ok"));
    }

}
