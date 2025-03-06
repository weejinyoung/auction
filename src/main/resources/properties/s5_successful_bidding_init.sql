INSERT INTO successful_bidding (id, bidding_id, auction_id, created_at, updated_at)
VALUES
    (1, 27, 6, CURRENT_TIMESTAMP() - INTERVAL '5' DAY, CURRENT_TIMESTAMP() - INTERVAL '5' DAY),
    (2, 31, 7, CURRENT_TIMESTAMP() - INTERVAL '7' DAY, CURRENT_TIMESTAMP() - INTERVAL '7' DAY);