package sg.edu.nus.iss.day22workshop.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import sg.edu.nus.iss.day22workshop.model.RSVP;
import sg.edu.nus.iss.day22workshop.repository.RSVPRepository;

@RestController
@RequestMapping(path="/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class RSVPRestController {

    @Autowired
    private RSVPRepository rsvpRepo;

    @GetMapping(path="/rsvps")
    public ResponseEntity<String> getAllRsvps() {
        List<RSVP> rsvps = rsvpRepo.getAllRSVP();

        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        for (RSVP r : rsvps) {
            arrBuilder.add(r.toJson());
        }

        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(arrBuilder.build().toString());
    }

    @GetMapping(path="/rsvp")
    public ResponseEntity<String> getRsvpByName(@RequestParam String q) {
        List<RSVP> rsvps = rsvpRepo.getRsvpByName(q);

        if (rsvps.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{'error_code' : '" + HttpStatus.NOT_FOUND + "'}");
        }

        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        for (RSVP r : rsvps) {
            arrBuilder.add(r.toJson());
        }

        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(arrBuilder.build().toString());
    }

    @PostMapping(path="/rsvp", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> insertOrUpdateRsvp(HttpServletRequest httpRequest) {
        RSVP rsvp = null;
        
        // from Body json String, create a java RSVP object
        try {
            rsvp = RSVP.createFromReq(httpRequest);
        } catch (Exception e) {
            e.printStackTrace();
            JsonObject errorObject = Json.createObjectBuilder()
                    .add("error", e.getMessage()).build();
            return ResponseEntity.badRequest().body(errorObject.toString());
        }

        // Check if RSVP exists in database. If no, insert into db.
        // If yes, update the record in db. Then convert to JSON object
        RSVP result = rsvpRepo.insertRsvp(rsvp);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(result.toJson().toString());
    }

    @PutMapping(path="/rsvp/{email}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> updateRsvp(@PathVariable String email
            , HttpServletRequest httpRequest) {
        RSVP rsvp = null;
        try {
            rsvp = RSVP.createFromReq(httpRequest);
        } catch (Exception e) {
            e.printStackTrace();
            JsonObject errorObject = Json.createObjectBuilder()
                    .add("error", e.getMessage()).build();
            return ResponseEntity.badRequest().body(errorObject.toString());
        }

        RSVP existingRsvp = rsvpRepo.getRsvpByEmail(email);
        if (Objects.isNull(existingRsvp)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{'error_code' : '" + HttpStatus.NOT_FOUND + "'}");
        }

        RSVP result = rsvpRepo.insertRsvp(rsvp);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(result.toJson().toString());
    }

    @GetMapping(path="/rsvps/count")
    public ResponseEntity<String> getRsvpCount() {
        int count = rsvpRepo.getTotalRSVPCount();

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body("{'rsvp_count' : '" + count + "'}");
    } 
}
