package com.teamtreehouse.ribbit.models;

import com.teamtreehouse.ribbit.models.callbacks.FindCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by benjakuben on 10/12/16.
 */

/*This class has implemented methods to perform work in the background and
to store data such that it simulates a data base.*/

public class Query<T extends Comparable<T>> {

    private List<T> queryItems;

    public Query(String className) {
        queryItems = new ArrayList<>();
    }

    public void orderByAscending(String key) {
        Collections.sort(queryItems);
    }

    public void addAscendingOrder(String key) {
        Collections.sort(queryItems);
    }

    public void addDescendingOrder(String key) {
        Collections.sort(queryItems, Collections.<T>reverseOrder());
    }

    public void setLimit(int limit) {
    }

    public void whereEqualTo(String key, String id) {
    }
/*This method takes a call back, the call back is use to execute work in the back ground.
So if the query is not null the query is passed other wise an exception is passed.*/
    public void findInBackground(FindCallback<T> callback) {
        if (queryItems != null) {
            callback.done(queryItems, null);
        } else {
            callback.done(null, new Exception());
        }
    }

    public void setDataSet(List<T> data) {
        queryItems = data;
    }
}
