package io.github.joshy56.portfoliobackend.controller;

import io.github.joshy56.portfoliobackend.dto.CardDto;
import io.github.joshy56.portfoliobackend.dto.Message;
import io.github.joshy56.portfoliobackend.entity.Card;
import io.github.joshy56.portfoliobackend.entity.Section;
import io.github.joshy56.portfoliobackend.repository.CardRepository;
import io.github.joshy56.portfoliobackend.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

/**
 * @author joshy56
 * @since 13/5/2023
 */
@RestController
@RequestMapping("/card")
@CrossOrigin(origins = "*")
public class CardController {
    @Autowired
    private CardRepository repository;
    @Autowired
    private SectionRepository sectionRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Card>> findAll() {
        return new ResponseEntity<>(repository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/details")
    public ResponseEntity<Card> findById(@RequestParam("id") UUID id) {
        return repository.findById(id)
                .map(
                        card -> new ResponseEntity<>(card, HttpStatus.OK)
                )
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PostMapping("/create")
    public ResponseEntity<Message> create(@RequestBody CardDto dto) {
        Card.CardBuilder builder = Card.builder();
        if(dto.getSectionId() != null && sectionRepository.existsById(dto.getSectionId()))
            builder.section(sectionRepository.findById(dto.getSectionId()).get());
        builder.avatarHeaderImage(dto.getAvatarHeaderImage())
                .headerTitle(dto.getHeaderTitle())
                .headerSubtitle(dto.getHeaderSubtitle())
                .bodyTitle(dto.getBodyTitle())
                .bodySubtitle(dto.getBodySubtitle())
                .knowledgeOnTech(dto.getKnowledgeOnTech());
        Card card = builder.build();
        repository.save(card);
        Section section = card.getSection();
        if(section != null) {
            card.setSection(section);
            List<Card> cards = section.getCards();
            cards.add(card);
            section.setCards(cards);
            sectionRepository.save(section);
        }
        HttpHeaders headers = new HttpHeaders();
//        headers.setLocation(URI.create());
        return new ResponseEntity<>(new Message("Card successfully created!"), headers, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Message> update(@RequestBody CardDto dto, @RequestParam(name = "id") UUID id) {
        return repository.findById(id)
                .map(
                        oldCard -> Card.builder()
                                .identifier(oldCard.getIdentifier())
                                .avatarHeaderImage(dto.getAvatarHeaderImage())
                                .knowledgeOnTech(dto.getKnowledgeOnTech())
                                .headerTitle(dto.getHeaderTitle())
                                .headerSubtitle(dto.getHeaderSubtitle())
                                .bodyTitle(dto.getBodyTitle())
                                .bodySubtitle(dto.getBodySubtitle())
                                .section((dto.getSectionId() == null) ? null : sectionRepository.findById(dto.getSectionId()).orElse(null))
                                .build()
                )
                .map(
                        card -> {
                            if(card.getSection() == null)
                                return new ResponseEntity<>(new Message("Section with it id not found!"), HttpStatus.BAD_REQUEST);
                            Section section = card.getSection();
                            List<Card> cards = section.getCards();
                            cards.removeIf(any -> any.getIdentifier().equals(card.getIdentifier()));
                            cards.add(card);
                            section.setCards(cards);
                            sectionRepository.save(section);
                            card.setSection(section);
                            repository.save(card);
                            return new ResponseEntity<>(new Message("Card successfully updated!"), HttpStatus.OK);
                        }
                )
                .orElse(new ResponseEntity<>(new Message("Card with it id not found!"), HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Message> delete(@RequestParam("id") UUID id) {
        return repository.findById(id)
                .map(
                        card -> {
                            repository.deleteById(id);
                            Section section = card.getSection();
                            if(section != null)
                                section.getCards().removeIf(any -> card.getIdentifier().equals(card.getIdentifier()));
                            if(sectionRepository.existsById(section.getIdentifier()))
                                sectionRepository.save(section);
                            return new ResponseEntity<>(new Message("Card successfully deleted!"), HttpStatus.OK);
                        }
                )
                .orElse(new ResponseEntity<>(new Message("Card with it id not found!"), HttpStatus.NOT_FOUND));
    }
}
