SELECT
    COALESCE(user_defined_category, default_category) AS category,
    id, timestamp, name, amount, comment, system_flag, category_type
FROM transactions
WHERE username = :username
  AND timestamp >= :start
  AND timestamp < :end
  AND is_split = FALSE
ORDER BY category, timestamp DESC;