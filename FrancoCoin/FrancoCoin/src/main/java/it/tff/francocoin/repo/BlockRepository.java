package it.tff.francocoin.repo;

import it.tff.francocoin.Block;
import it.tff.francocoin.Transaction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@Repository
public class BlockRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Gson gson;

    public BlockRepository() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    // Salva un blocco nel database
    // public void saveBlock(Block block) {
    //     String insertSQL = "INSERT INTO blocks (height, previous_block, transactions, hash) VALUES (?, ?, ?, ?)";
        
    //     String previousBlockJson = block.getPreviousBlock() != null ? gson.toJson(block.getPreviousBlock()) : null;
    //     String transactionsJson = block.getTransactions() != null ? gson.toJson(block.getTransactions()) : null;
        
    //     jdbcTemplate.update(insertSQL,
    //             block.getHeight(),
    //             previousBlockJson,
    //             transactionsJson,
    //             block.getHash());
    // }

    // Recupera un blocco per altezza (height)
    public Block findBlockByHeight(int height) {
        String selectSQL = "SELECT * FROM blocks WHERE height = ?";
        
        return jdbcTemplate.queryForObject(selectSQL, new Object[]{height}, this::mapRowToBlock);
    }

    private Block mapRowToBlock(ResultSet rs, int rowNum) throws SQLException {
        Block block = new Block();
        block.setHeight(rs.getInt("height"));
        block.setHash(rs.getString("hash"));

        String previousBlockJson = rs.getString("previousBlock");
        if (previousBlockJson != null) {
            TypeToken<HashMap<String, Object>> previousBlockType = new TypeToken<HashMap<String, Object>>() {};
            block.setPreviousBlock(gson.fromJson(previousBlockJson, previousBlockType.getType()));
        } else {
            block.setPreviousBlock(null);
        }

        String transactionsJson = rs.getString("transactions");
        if (transactionsJson != null) {
            block.setTransactions(gson.fromJson(transactionsJson, Transaction[].class));
        } else {
            block.setTransactions(null);
        }

        return block;
    }

    // Inner class for LocalDateTime adapter
    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(FORMATTER));
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            } else {
                return LocalDateTime.parse(in.nextString(), FORMATTER);
            }
        }
    }
}
