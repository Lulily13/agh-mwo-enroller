package com.company.enroller.persistence;

import java.util.Collection;

import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Participant;

@Component("participantService")
public class ParticipantService {

    DatabaseConnector connector;

    public ParticipantService() {
        connector = DatabaseConnector.getInstance();
    }

    public Collection<Participant> getAll() {
        String hql = "FROM Participant";
        Query query = connector.getSession().createQuery(hql);
        return query.list();
    }

    public Participant findByLogin(String login) {
        return getAll().stream()
                .filter(p -> p.getLogin().equals(login))
                .findFirst()
                .orElse(null);
    }

    public ResponseEntity<String> createParticipant(Participant participant) {
        if (findByLogin(participant.getLogin()) != null) {
            return new ResponseEntity<>("Unable to create. A participant with login "
                    + participant.getLogin() + " already exists.", HttpStatus.CONFLICT);
        }

        connector.getSession().beginTransaction();
        connector.getSession().save(participant);
        connector.getSession().getTransaction().commit();

        return new ResponseEntity<>("Participant created successfully", HttpStatus.CREATED);
    }

    public ResponseEntity<String> updateParticipant(String login, Participant updatedParticipant) {
        Participant existingParticipant = findByLogin(login);
        if (existingParticipant == null) {
            return new ResponseEntity<>("Participant not found", HttpStatus.NOT_FOUND);
        }

        connector.getSession().beginTransaction();
        existingParticipant.setLogin(updatedParticipant.getLogin());
        existingParticipant.setPassword(updatedParticipant.getPassword());
        connector.getSession().update(existingParticipant);
        connector.getSession().getTransaction().commit();

        return new ResponseEntity<>("Participant updated successfully", HttpStatus.OK);
    }

    public ResponseEntity<String> deleteParticipant(String login) {
        Participant existingParticipant = findByLogin(login);
        if (existingParticipant == null) {
            return new ResponseEntity<>("Participant not found", HttpStatus.NOT_FOUND);
        }

        connector.getSession().beginTransaction();
        connector.getSession().delete(existingParticipant);
        connector.getSession().getTransaction().commit();

        return new ResponseEntity<>("Participant deleted successfully", HttpStatus.OK);
    }
}
