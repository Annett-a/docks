package com.example.documents.repo;

import com.example.documents.model.Document;
import com.example.documents.model.DocumentType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JdbcDocumentRepository implements DocumentRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Document> mapper = (rs, rowNum) -> new Document(
            rs.getObject("id", UUID.class),
            DocumentType.valueOf(rs.getString("doc_type")),
            rs.getTimestamp("created_at").toInstant(),
            rs.getObject("user_id", UUID.class),
            rs.getString("user_fio"),
            rs.getString("card_number")
    );

    @Override
    public Document save(Document d) {
        String sql = """
                INSERT INTO documents(id, doc_type, created_at, user_id, user_fio, card_number)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        if (d.getId() == null) {
            d.setId(UUID.randomUUID());
        }
        if (d.getCreatedDate() == null) {
            d.setCreatedDate(Instant.now());
        }

        jdbcTemplate.update(
                sql,
                d.getId(),
                d.getDocType().name(),
                Timestamp.from(d.getCreatedDate()),
                d.getUserId(),
                d.getUserFio(),
                d.getCardNumber()
        );

        return d;
    }

    @Override
    public Optional<Document> findById(UUID id) {
        String sql = """
                SELECT id, doc_type, created_at, user_id, user_fio, card_number
                FROM documents
                WHERE id = ?
                """;

        List<Document> result = jdbcTemplate.query(sql, mapper, id);
        return result.stream().findFirst();
    }
}