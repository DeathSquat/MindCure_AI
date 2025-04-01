package com.eyantra.mind_cure_ai;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GoalManager {
    private static final String PREF_NAME = "GoalPrefs";
    private static final String GOALS_KEY = "goals";
    private SharedPreferences prefs;
    private Gson gson;

    public GoalManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveGoals(List<Goal> goals) {
        String json = gson.toJson(goals);
        prefs.edit().putString(GOALS_KEY, json).apply();
    }

    public List<Goal> getGoals() {
        String json = prefs.getString(GOALS_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Goal>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void addGoal(Goal goal) {
        List<Goal> goals = getGoals();
        goals.add(goal);
        saveGoals(goals);
    }

    public void updateGoal(int index, Goal goal) {
        List<Goal> goals = getGoals();
        if (index >= 0 && index < goals.size()) {
            goals.set(index, goal);
            saveGoals(goals);
        }
    }

    public void deleteGoal(int index) {
        List<Goal> goals = getGoals();
        if (index >= 0 && index < goals.size()) {
            goals.remove(index);
            saveGoals(goals);
        }
    }
} 