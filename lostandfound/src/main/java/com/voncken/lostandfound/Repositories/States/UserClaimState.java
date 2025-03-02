package com.voncken.lostandfound.Repositories.States;

import java.util.UUID;

public class UserClaimState {

    public UserClaimState()
    {

    }

    public UserClaimState(UUID Guid, String UserId, UUID LostItemGuid, Integer ClaimedQuantity) {
		this.Guid = Guid;
        this.UserId = UserId;
        this.LostItemGuid = LostItemGuid;
        this.ClaimedQuantity = ClaimedQuantity;
	}

    public UUID Guid;
    public String UserId;
    public UUID LostItemGuid;
    public Integer ClaimedQuantity;

    public UUID getLostItemGuid() {
        return LostItemGuid;
    }
    
    public void setLostItemGuid(UUID lostItemGuid) {
        this.LostItemGuid = lostItemGuid;
    }
    
    public int getClaimedQuantity() {
        return ClaimedQuantity;
    }
    
    public void setClaimedQuantity(int claimedQuantity) {
        this.ClaimedQuantity = claimedQuantity;
    }
}
