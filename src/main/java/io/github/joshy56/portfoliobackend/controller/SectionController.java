package io.github.joshy56.portfoliobackend.controller;

import io.github.joshy56.portfoliobackend.dto.CardDto;
import io.github.joshy56.portfoliobackend.dto.Message;
import io.github.joshy56.portfoliobackend.dto.SectionDto;
import io.github.joshy56.portfoliobackend.entity.Card;
import io.github.joshy56.portfoliobackend.entity.Section;
import io.github.joshy56.portfoliobackend.repository.CardRepository;
import io.github.joshy56.portfoliobackend.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author joshy56
 * @since 14/5/2023
 */
@RestController
@RequestMapping("/section")
@CrossOrigin(origins = "*")
public class SectionController {
    @Autowired
    private SectionRepository repository;
    @Autowired
    private CardRepository cardRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Section>> findAll() {
        return new ResponseEntity<>(repository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/details")
    public ResponseEntity<Section> findById(@RequestParam("id") UUID id) {
        return repository.findById(id)
                .map(
                        section -> new ResponseEntity<>(section, HttpStatus.OK)
                )
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PostMapping("/create")
    public ResponseEntity<Message> create(@RequestBody SectionDto dto) {
        Section.SectionBuilder builder = Section.builder();
        if(dto.getCards() != null && !dto.getCards().isEmpty())
            builder.cards(cardRepository.findAllById(dto.getCards()));
        Section section = Section.builder()
                .title(dto.getTitle())
                .build();
        if(dto.getCards() != null && !dto.getCards().isEmpty()) {
            List<Card> cards = section.getCards();
            cards.parallelStream().forEach(card -> card.setSection(section));
            section.setCards(cards);
        }
        repository.save(section);
        HttpHeaders headers = new HttpHeaders();
//        headers.setLocation(URI.create(""));
        return new ResponseEntity<>(new Message("Section successfully created!"), headers, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Message> update(@RequestParam("id") UUID id, @RequestBody SectionDto dto) {
        return repository.findById(id)
                .map(
                        oldSection -> Section.builder()
                                .identifier(oldSection.getIdentifier())
                                .title(dto.getTitle())
                                .cards((dto.getCards() != null && !dto.getCards().isEmpty()) ? cardRepository.findAllById(dto.getCards()) : Collections.emptyList())
                                .build()
                )
                .map(
                        section -> {
                            section.getCards().parallelStream().forEach(card -> card.setSection(section));
                            return new ResponseEntity<>(new Message("Section successfully updated!"), HttpStatus.OK);
                        }
                )
                .orElse(new ResponseEntity<>(new Message("Section with it id not found!"), HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Message> delete(@RequestParam("id") UUID id) {
        return repository.findById(id)
                .map(
                        section -> {
                            section.getCards().parallelStream().forEach(card -> card.setSection(null));
                            repository.deleteById(id);
                            return new ResponseEntity<>(new Message("Section successfully deleted!"), HttpStatus.OK);
                        }
                )
                .orElse(new ResponseEntity<>(new Message("Section with it id not found!"), HttpStatus.NOT_FOUND));
    }
}
