package com.voncken.lostandfound.Repositories.States;

import java.util.UUID;

public class LostItemState {
    
    public LostItemState() {
    }

    public LostItemState(UUID Guid, String Name, Integer Quantity, String LostAndFoundPlace) {
		this.Guid = Guid;
        this.Name = Name;
        this.Quantity = Quantity;
        this.LostAndFoundPlace = LostAndFoundPlace;
	}

    public UUID Guid;
    public String Name;
    public Integer Quantity;
    public String LostAndFoundPlace;
}
