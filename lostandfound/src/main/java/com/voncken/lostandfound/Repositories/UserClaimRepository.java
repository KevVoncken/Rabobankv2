package com.voncken.lostandfound.Repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.voncken.lostandfound.Repositories.States.UserClaimState;

@Service
public class UserClaimRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserClaimRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Iterable<UserClaimState> GetAll() {
        List<UserClaimState> userClaimStates = jdbcTemplate.query("SELECT * FROM UserClaim",
				(resultSet, rowNum) -> new UserClaimState(
						UUID.fromString(resultSet.getString("Guid")),
                        resultSet.getString("UserId"),
						UUID.fromString(resultSet.getString("LostItemGuid")),
						resultSet.getInt("ClaimedQuantity")));

        return userClaimStates;
    }

    public void Add(UserClaimState userClaimState)
    {
        String sql = "INSERT INTO UserClaim (Guid, LostItemGuid, UserId, ClaimedQuantity) VALUES (?, ?, ?, ?)";
        Object[] args = new Object[] {
            userClaimState.Guid.toString(),
            userClaimState.LostItemGuid,
            userClaimState.UserId,
            userClaimState.ClaimedQuantity };

        jdbcTemplate.update(sql, args);
    }

    public void init() {
    }
}
