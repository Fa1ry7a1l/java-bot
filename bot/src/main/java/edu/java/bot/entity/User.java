package edu.java.bot.entity;

public class User {
    //потребуется для бд
    // private long id;
    private long telegramId;

    public User(long telegramId) {
        this.telegramId = telegramId;
    }

    public long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(long telegramId) {
        this.telegramId = telegramId;
    }
}
