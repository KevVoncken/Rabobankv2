package com.voncken.lostandfound.Repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.voncken.lostandfound.Repositories.States.LostItemState;

@Service
public class LostItemRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LostItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Iterable<LostItemState> GetAll() {
        List<LostItemState> lostItemStates = jdbcTemplate.query("SELECT * FROM LostItem",
                (resultSet, rowNum) -> new LostItemState(
                        UUID.fromString(resultSet.getString("Guid")),
                        resultSet.getString("Name"),
                        resultSet.getInt("Quantity"),
                        resultSet.getString("LostAndFoundPlace")));

        return lostItemStates;
    }

    public void Add(LostItemState lostItemState) {
        String sql = "INSERT INTO LostItem (Guid, Name, Quantity, LostAndFoundPlace) VALUES (?, ?, ?, ?)";
        Object[] args = new Object[] {
                lostItemState.Guid.toString(),
                lostItemState.Name,
                lostItemState.Quantity,
                lostItemState.LostAndFoundPlace };

        jdbcTemplate.update(sql, args);
    }

    public void init() {
    }
}
