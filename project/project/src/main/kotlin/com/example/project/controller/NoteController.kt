package com.example.project.controller

import com.example.project.database.model.Note
import com.example.project.database.repository.NoteRepository
import com.example.project.database.resources.NoteRequestDto
import com.example.project.database.resources.NoteResponseDto
import org.bson.types.ObjectId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/notes")
class NoteController (
    private val repository: NoteRepository,
) {

    @PostMapping
    fun save(@RequestBody body: NoteRequestDto): ResponseEntity<NoteResponseDto> {
        val note = repository.save(
            Note(
                id = body.id?.let { ObjectId(it) } ?: ObjectId.get(),
                title = body.title,
                content = body.content,
                color = body.color,
                createdAt = Instant.now(),
                ownerId = ObjectId()
            )
        )

        return ResponseEntity.status(201).body(note.toResponse())
    }

    @GetMapping
    fun findByOwnerId(@RequestParam(required = true) ownerId: String): List<NoteResponseDto> {
        return repository.findByOwnerId(ObjectId(ownerId)).map {
            it.toResponse()
        }
    }

    private fun Note.toResponse(): NoteResponseDto {
        return NoteResponseDto(
            id = id.toHexString(),
            title = title,
            content = content,
            color = color,
            createdAt = createdAt.toString()
        )
    }
}