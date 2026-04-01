SELECT
    COALESCE(user_defined_category, default_category) AS category,
    id, timestamp, name, amount, comment, system_flag
FROM transactions
WHERE username = :username
  AND timestamp >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')
  AND is_expense = TRUE
  AND is_split = FALSE
ORDER BY category, timestamp DESC;