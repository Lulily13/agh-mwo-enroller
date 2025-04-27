package com.company.enroller.controllers;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.company.enroller.model.Participant;
import com.company.enroller.persistence.ParticipantService;

@RestController
@RequestMapping("/participants")
public class ParticipantRestController {

    @Autowired
    ParticipantService participantService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getParticipants(
            @RequestParam(name = "sortBy", defaultValue = "login") String sortBy,
            @RequestParam(name = "sortOrder", required = false) String sortOrder,
            @RequestParam(name = "key", required = false) String key) {

        Collection<Participant> participants = participantService.getAll();

        // Filtrowanie po loginie
        if (key != null && !key.isEmpty()) {
            String keywordLower = key.toLowerCase();
            participants = participants.stream()
                    .filter(p -> p.getLogin().toLowerCase().contains(keywordLower))
                    .collect(Collectors.toList());
        }

        // Sortowanie
        if ("login".equalsIgnoreCase(sortBy)) {
            if (sortOrder == null || sortOrder.equalsIgnoreCase("ASC")) {
                participants = participants.stream()
                        .sorted(Comparator.comparing(Participant::getLogin))
                        .collect(Collectors.toList());
            } else if (sortOrder.equalsIgnoreCase("DESC")) {
                participants = participants.stream()
                        .sorted(Comparator.comparing(Participant::getLogin).reversed())
                        .collect(Collectors.toList());
            } else {
                return new ResponseEntity<>("Invalid sortOrder. Allowed values are 'ASC' or 'DESC'.", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Invalid sortBy parameter. Only 'login' is supported.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(participants, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getParticipant(@PathVariable("id") String login) {
        Participant participant = participantService.findByLogin(login);
        if (participant == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Participant>(participant, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> addParticipant(@RequestBody Participant participant) {
        return participantService.createParticipant(participant);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateParticipant(@PathVariable("id") String login, @RequestBody Participant updatedParticipant) {
        return participantService.updateParticipant(login, updatedParticipant);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteParticipant(@PathVariable("id") String login) {
        return participantService.deleteParticipant(login);
    }
}


