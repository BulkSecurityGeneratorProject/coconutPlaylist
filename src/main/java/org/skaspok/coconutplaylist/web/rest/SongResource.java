package org.skaspok.coconutplaylist.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.time.ZonedDateTime;

import org.skaspok.coconutplaylist.domain.Song;
import org.skaspok.coconutplaylist.repository.SongRepository;
import org.skaspok.coconutplaylist.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.skaspok.coconutplaylist.domain.User;
import org.skaspok.coconutplaylist.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing Song.
 */
@RestController
@RequestMapping("/api")
public class SongResource {

    private final Logger log = LoggerFactory.getLogger(SongResource.class);

    private static final String ENTITY_NAME = "song";

    private final SongRepository songRepository;
    private final UserService userService;

    public SongResource(SongRepository songRepository, UserService userService) {
        this.songRepository = songRepository;
        this.userService = userService;
    }

    /**
     * POST  /songs : Create a new song.
     *
     * @param song the song to create
     * @return the ResponseEntity with status 201 (Created) and with body the new song, or with status 400 (Bad Request) if the song has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/songs")
    @Timed
    public ResponseEntity<Song> createSong(@RequestBody Song song) throws URISyntaxException {
        log.debug("REST request to save Song : {}", song);

        Optional<User> optUser = userService.getCurrentUser();
        song.setDate(ZonedDateTime.now());
        song.setAddingUser(optUser.get());

        if (song.getId() != null) {
            return ResponseEntity.badRequest().headers(
                    HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new song cannot already have an ID"))
                    .body(null);
        }
        Song result = songRepository.save(song);
        return ResponseEntity.created(new URI("/api/songs/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString())).body(result);
    }

    /**
     * PUT  /songs : Updates an existing song.
     *
     * @param song the song to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated song,
     * or with status 400 (Bad Request) if the song is not valid,
     * or with status 500 (Internal Server Error) if the song couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/songs")
    @Timed
    public ResponseEntity<Song> updateSong(@RequestBody Song song) throws URISyntaxException {
        log.debug("REST request to update Song : {}", song);
        if (song.getId() == null) {
            return createSong(song);
        }
        Song result = songRepository.save(song);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, song.getId().toString()))
                .body(result);
    }

    /**
     * GET  /songs : get all the songs.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of songs in body
     */
    @GetMapping("/songs")
    @Timed
    public List<Song> getAllSongs() {
        log.debug("REST request to get all Songs");
        List<Song> listSong = songRepository.findAll();
        return listSong;
    }

    /**
     * GET  /songs/:id : get the "id" song.
     *
     * @param id the id of the song to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the song, or with status 404 (Not Found)
     */
    @GetMapping("/UpdateSong/{id}")
    @Timed
    public ResponseEntity<Song> getSong(@PathVariable Long id) {
        log.debug("REST request to get Song : {}", id);
        Song song = songRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(song));
    }

    /**
     * DELETE  /songs/:id : delete the "id" song.
     *
     * @param id the id of the song to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/songs/{id}")
    @Timed
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        log.debug("REST request to delete Song : {}", id);
        songRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
