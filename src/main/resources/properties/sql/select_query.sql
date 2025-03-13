-- 아이템 갯수별 사용자 수를 조회하는 쿼리.
SELECT
    user_items.item_count,
    COUNT(*) AS user_count
FROM (
         SELECT
             u.id AS user_id,
             COUNT(i.id) AS item_count
         FROM
             user u
                 LEFT JOIN
             item i ON u.id = i.owner_id
         GROUP BY
             u.id
     ) AS user_items
GROUP BY
    user_items.item_count
ORDER BY
    user_items.item_count;