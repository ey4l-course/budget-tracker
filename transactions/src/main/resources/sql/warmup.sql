SELECT
    COALESCE(user_defined_category, default_category) AS category,
    id, timestamp, name, amount, comment, system_flag
FROM transactions
WHERE user_id = :userId
  AND timestamp >= DATE_TRUNC('MONTH', CURRENT_DATE)
  AND is_expense = TRUE
  AND is_split = FALSE
ORDER BY category, timestamp DESC;