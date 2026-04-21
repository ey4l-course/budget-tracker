SELECT
    COALESCE(t.user_defined_category, t.default_category) as category_name,
    t.category_type,
    SUM(t.amount) as bug_subtotal
FROM transactions t
         LEFT JOIN budget_configs bc ON
    t.username = bc.username AND
    COALESCE(t.user_defined_category, t.default_category) = bc.category_name
WHERE t.username = :username
  AND t.timestamp >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')
  AND t.is_split = 0
  AND bc.category_name IS NULL -- This finds the "dropped" items
GROUP BY category_name, t.category_type;