package com.iridium.iridiumteams;

import com.iridium.iridiumcore.Item;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class UserRank {
    public String name;
    public Item item;
    
    @Override
    public String toString() {
        return "Rank:" + name;
    }
}
