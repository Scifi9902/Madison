package io.github.scifi9902.madison.profile;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class Profile {

    @SerializedName("_id")
    private UUID uniqueId;

    private int kills;

    private int deaths;

    private int balance;

    public Profile(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void addBalance(int amount) {
        this.setBalance(this.getBalance() + amount);
    }

    public void subtractBalance(int amount) {
        this.setBalance(this.getBalance() - amount);
    }


}
