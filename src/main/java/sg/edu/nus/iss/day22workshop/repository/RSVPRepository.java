package sg.edu.nus.iss.day22workshop.repository;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.day22workshop.model.RSVP;
import static sg.edu.nus.iss.day22workshop.repository.DBQueries.*;

@Repository
public class RSVPRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;
    
    // fetch all RSVP
    public List<RSVP> getAllRSVP() {
        List<RSVP> rsvps = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SELECT_ALL_RSVP);

        while(rs.next()) {
            rsvps.add(RSVP.create(rs));
        }

        return rsvps;
    }

    public List<RSVP> getRsvpByName(String name) {
        List<RSVP> rsvps = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SELECT_RSVP_BY_NAME, new Object[] {"%" + name + "%"});

        while(rs.next()) {
            rsvps.add(RSVP.create(rs));
        }
        return rsvps;
    }

    public RSVP getRsvpByEmail(String email) {
        List<RSVP> rsvps = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SELECT_RSVP_BY_EMAIL, email);

        while(rs.next()) {
            rsvps.add(RSVP.create(rs));
        }

        if (rsvps.isEmpty()) return null;

        return rsvps.get(0);
    }

    public RSVP insertRsvp(RSVP rsvp) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // email is unique - get the RSVP object using its email
        RSVP existingRsvp = getRsvpByEmail(rsvp.getEmail());

        // check if RSVP object is null
        if(Objects.isNull(existingRsvp)) {
            // insert record
            jdbcTemplate.update(conn -> {
                    PreparedStatement statement = conn.prepareStatement
                    (INSERT_NEW_RSVP, Statement.RETURN_GENERATED_KEYS);
            
                statement.setString(1, rsvp.getName());
                statement.setString(2, rsvp.getEmail());
                statement.setString(3, rsvp.getPhone());
                statement.setTimestamp(4, new Timestamp(
                        rsvp.getConfirmationDate().toDateTime().getMillis()));
                statement.setString(5, rsvp.getComments());
                return statement;
            }, keyHolder);

            BigInteger primaryKey = (BigInteger) keyHolder.getKey();

            rsvp.setId(primaryKey.intValue());
        } else {
            // update record
            existingRsvp.setName(rsvp.getName());
            existingRsvp.setPhone(rsvp.getPhone());
            existingRsvp.setConfirmationDate(rsvp.getConfirmationDate());
            existingRsvp.setComments(rsvp.getComments());

            boolean isUpdated = updateRsvp(existingRsvp);

            // if the record is updated (true), set the id and return the RSVP object
            if(isUpdated) {
                rsvp.setId(existingRsvp.getId());
            }
        }
        return rsvp;
    }

    private boolean updateRsvp(RSVP existingRsvp) {
        // update returns the number of rows affected
        // if the number of rows updates is > 0, return true
        return jdbcTemplate.update(UPDATE_RSVP_BY_EMAIL, 
            existingRsvp.getName(),
            existingRsvp.getPhone(),
            new Timestamp(existingRsvp.getConfirmationDate().toDateTime().getMillis()),
            existingRsvp.getComments(),
            existingRsvp.getEmail()) > 0;
    }

    public int getTotalRSVPCount() {
        return jdbcTemplate.queryForObject(TOTAL_RSVP_COUNT, Integer.class);
        // gets a List of one Map element <total_count : int count>
        // List<Map<String, Object>> rows = jdbcTemplate.queryForList(TOTAL_RSVP_COUNT);
        // return (Long) rows.get(0).get("total_count");
    }
}
