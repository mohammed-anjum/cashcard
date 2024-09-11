package example.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/cashcards")
class CashCardController {
    private final CashCardRepository cashCardRepository;

    private CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    private CashCard findCashCard(Long requestedId, Principal principal) {
        return cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
        CashCard cashCard = findCashCard(requestedId, principal);
        if (cashCard != null) {
            return ResponseEntity.ok(cashCard);
        } else {
            return ResponseEntity.notFound().build();
        }

//        if (requestedId.equals(99L)) {
//            CashCard cashCard = new CashCard(99L, 123.45);
//            return ResponseEntity.ok(cashCard);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
    }

//    @PostMapping
//    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest) {
//        return ResponseEntity.created(URI.create("/what/should/go/here?")).build();
//    }

    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb, Principal principal) {
        CashCard cashCardWithOwner = new CashCard(null, newCashCardRequest.amount(), principal.getName());
        CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);
        URI locationOfNewCashCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }

//    @PostMapping
//    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb) {
//        CashCard savedCashCard = cashCardRepository.save(newCashCardRequest);
//        URI locationOfNewCashCard = ucb
//                .path("cashcards/{id}")
//                .buildAndExpand(savedCashCard.id())
//                .toUri();
//        return ResponseEntity.created(locationOfNewCashCard).build();
//    }

    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))));
        return ResponseEntity.ok(page.getContent());
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putCashCard(@PathVariable Long requestedId, @RequestBody CashCard cashCardUpdate, Principal principal) {
        CashCard cashCard = findCashCard(requestedId, principal);
        if (cashCard != null) {
            CashCard updatedCashCard = new CashCard(cashCard.id(), cashCardUpdate.amount(), principal.getName());
            cashCardRepository.save(updatedCashCard);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long id, Principal principal) {
        if (cashCardRepository.existsByIdAndOwner(id, principal.getName())) {
            cashCardRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

//    private ResponseEntity<Void> deleteCashCard(
//            @PathVariable Long id,
//    @DeleteMapping("/{id}")
//            Principal principal) {
//        // Add the following 3 lines:
//        if (!cashCardRepository.existsByIdAndOwner(id, principal.getName())) {
//            return ResponseEntity.notFound().build();
//        }
//        cashCardRepository.deleteById(id); // Add this line
//        return ResponseEntity.noContent().build();
//    }

//    @PutMapping("/{requestedId}")
//    private ResponseEntity<Void> putCashCard(@PathVariable Long requestedId, @RequestBody CashCard cashCardUpdate, Principal principal) {
//        CashCard cashCard = cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
//        CashCard updatedCashCard = new CashCard(cashCard.id(), cashCardUpdate.amount(), principal.getName());
//        cashCardRepository.save(updatedCashCard);
//        return ResponseEntity.noContent().build();
//    }

//    @GetMapping
//    private ResponseEntity<List<CashCard>> findAll(Pageable pageable) {
//        Page<CashCard> page = cashCardRepository.findAll(
//                PageRequest.of(
//                        pageable.getPageNumber(),
//                        pageable.getPageSize(),
//                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
//                ));
//        return ResponseEntity.ok(page.getContent());
//    }
